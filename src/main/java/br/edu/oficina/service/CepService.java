package br.edu.oficina.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CepService {
    private final RestClient client;

    public CepService(RestClient.Builder builder, @Value("${app.viacep.url}") String url) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2500);
        factory.setReadTimeout(8000);
        client = builder.baseUrl(url).requestFactory(factory).build();
    }

    public EnderecoCep consultar(String cep) {
        if (!cep.matches("[0-9]{5}-?[0-9]{3}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido.");
        }

        RespostaViaCep resposta;
        try {
            resposta = client.get().uri("/ws/{cep}/json/", cep.replace("-", ""))
                .retrieve().body(RespostaViaCep.class);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ViaCEP indisponível.", ex);
        }

        if (resposta == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ViaCEP retornou uma resposta vazia.");
        }
        if ("true".equalsIgnoreCase(resposta.erro())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado.");
        }

        return new EnderecoCep(resposta.logradouro(), resposta.localidade(), resposta.uf());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record RespostaViaCep(String logradouro, String localidade, String uf, String erro) {}

    public record EnderecoCep(String logradouro, String cidade, String uf) {}
}
