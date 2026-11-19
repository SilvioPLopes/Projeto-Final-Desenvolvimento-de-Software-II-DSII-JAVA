package br.edu.oficina;

import br.edu.oficina.model.StatusOrdem;
import br.edu.oficina.repository.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:oficina_teste;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "app.senha.atendente=atendente-teste",
    "app.senha.consulta=consulta-teste"
})
@AutoConfigureMockMvc
class OficinaIntegrationTest {
    private static final HttpServer viaCep = iniciarViaCep();

    @Autowired MockMvc mvc;
    @Autowired ClienteRepository clientes;
    @Autowired OrdemServicoRepository ordens;
    @Autowired EquipamentoRepository equipamentos;
    @Autowired TecnicoRepository tecnicos;
    @Autowired ServicoRepository servicos;

    @DynamicPropertySource
    static void configurarViaCep(DynamicPropertyRegistry propriedades) {
        propriedades.add("app.viacep.url", () -> "http://127.0.0.1:" + viaCep.getAddress().getPort());
    }

    @AfterAll
    static void pararViaCep() {
        viaCep.stop(0);
    }

    @Test
    void autenticaERestringeAcessoDeConsulta() throws Exception {
        mvc.perform(get("/ordens"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
        mvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Oficina OS")));
        mvc.perform(formLogin("/login").user("atendente").password("atendente-teste"))
            .andExpect(authenticated().withRoles("ATENDENTE"));
        mvc.perform(get("/ordens").with(user("consulta").roles("CONSULTA")))
            .andExpect(status().isOk());
        mvc.perform(get("/ordens/nova").with(user("consulta").roles("CONSULTA")))
            .andExpect(status().isForbidden());
        mvc.perform(post("/ordens/nova").with(user("consulta").roles("CONSULTA")).with(csrf()))
            .andExpect(status().isForbidden());
        mvc.perform(post("/ordens/nova").with(user("atendente").roles("ATENDENTE")))
            .andExpect(status().isForbidden());
    }

    @Test
    void cadastraEditaConsultaEExcluiClientesEOrdens() throws Exception {
        mvc.perform(get("/clientes/novo").with(user("atendente").roles("ATENDENTE")))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Buscar CEP")));
        mvc.perform(post("/clientes/novo").with(user("atendente").roles("ATENDENTE")).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("dados", "nome", "cep"));
        mvc.perform(clienteForm("/clientes/novo", "Joana Teste"))
            .andExpect(redirectedUrl("/clientes"));
        var cliente = clientes.findAll().get(0);
        var clienteId = cliente.getId();
        assertThat(cliente.getEndereco().getCep()).isEqualTo("01001000");

        mvc.perform(clienteForm("/clientes/" + clienteId + "/editar", "Joana Silva"))
            .andExpect(redirectedUrl("/clientes"));
        mvc.perform(get("/clientes").with(user("atendente").roles("ATENDENTE")))
            .andExpect(content().string(containsString("Joana Silva")));

        mvc.perform(get("/ordens/nova").with(user("atendente").roles("ATENDENTE")))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Joana Silva")));
        mvc.perform(ordemForm("/ordens/nova", clienteId, "Não liga", "ABERTA"))
            .andExpect(redirectedUrl("/ordens"));
        Long ordemId = ordens.findAll().get(0).getId();

        mvc.perform(get("/ordens/" + ordemId).with(user("atendente").roles("ATENDENTE")))
            .andExpect(content().string(containsString("Não liga")));
        mvc.perform(ordemForm("/ordens/" + ordemId + "/editar", clienteId, "Fonte trocada", "CONCLUIDA"))
            .andExpect(redirectedUrl("/ordens"));
        assertThat(ordens.findById(ordemId).orElseThrow().getStatus()).isEqualTo(StatusOrdem.CONCLUIDA);
        mvc.perform(get("/api/ordens/" + ordemId).with(user("consulta").roles("CONSULTA")))
            .andExpect(jsonPath("$.descricao").value("Fonte trocada"));
        mvc.perform(get("/v3/api-docs").with(user("atendente").roles("ATENDENTE")))
            .andExpect(jsonPath("$.paths['/api/ordens/{id}']").exists());

        mvc.perform(post("/clientes/{id}/excluir", clienteId).with(user("atendente").roles("ATENDENTE")).with(csrf()))
            .andExpect(flash().attributeExists("erro"));
        assertThat(clientes.existsById(clienteId)).isTrue();
        mvc.perform(post("/ordens/{id}/excluir", ordemId).with(user("atendente").roles("ATENDENTE")).with(csrf()))
            .andExpect(redirectedUrl("/ordens"));
        mvc.perform(post("/clientes/{id}/excluir", clienteId).with(user("atendente").roles("ATENDENTE")).with(csrf()))
            .andExpect(redirectedUrl("/clientes"));
        assertThat(ordens.existsById(ordemId)).isFalse();
        assertThat(clientes.existsById(clienteId)).isFalse();
    }

    @Test
    void consultaViaCepPorHttpETrataCepInvalidoOuAusente() throws Exception {
        mvc.perform(get("/api/cep/01001-000").with(user("atendente").roles("ATENDENTE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.logradouro").value("Praça da Sé"))
            .andExpect(jsonPath("$.cidade").value("São Paulo"))
            .andExpect(jsonPath("$.uf").value("SP"));
        mvc.perform(get("/api/cep/00000000").with(user("atendente").roles("ATENDENTE")))
            .andExpect(status().isNotFound());
        mvc.perform(get("/api/cep/12").with(user("atendente").roles("ATENDENTE")))
            .andExpect(status().isBadRequest());
    }

    private MockHttpServletRequestBuilder clienteForm(String url, String nome) {
        return post(url).with(user("atendente").roles("ATENDENTE")).with(csrf())
            .param("nome", nome).param("telefone", "11999990000").param("cep", "01001-000")
            .param("logradouro", "Praça da Sé").param("numero", "10")
            .param("cidade", "São Paulo").param("uf", "SP");
    }

    private MockHttpServletRequestBuilder ordemForm(String url, Long clienteId, String descricao, String status) {
        return post(url).with(user("atendente").roles("ATENDENTE")).with(csrf())
            .param("clienteId", clienteId.toString())
            .param("equipamentoId", equipamentos.findAll().get(0).getId().toString())
            .param("tecnicoId", tecnicos.findAll().get(0).getId().toString())
            .param("servicoId", servicos.findAll().get(0).getId().toString())
            .param("descricao", descricao).param("status", status);
    }

    private static HttpServer iniciarViaCep() {
        try {
            HttpServer servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            servidor.createContext("/ws/", troca -> {
                String corpo = troca.getRequestURI().getPath().equals("/ws/01001000/json/")
                    ? "{\"logradouro\":\"Praça da Sé\",\"localidade\":\"São Paulo\",\"uf\":\"SP\"}"
                    : "{\"erro\":true}";
                byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
                troca.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                troca.sendResponseHeaders(200, bytes.length);
                try (var saida = troca.getResponseBody()) {
                    saida.write(bytes);
                }
            });
            servidor.start();
            return servidor;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
