package br.edu.oficina.config;

import br.edu.oficina.model.*;
import br.edu.oficina.repository.*;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CatalogoInicial implements ApplicationRunner {
    private final CategoriaRepository categorias;
    private final EquipamentoRepository equipamentos;
    private final TecnicoRepository tecnicos;
    private final ServicoRepository servicos;

    public CatalogoInicial(CategoriaRepository categorias, EquipamentoRepository equipamentos,
                           TecnicoRepository tecnicos, ServicoRepository servicos) {
        this.categorias = categorias;
        this.equipamentos = equipamentos;
        this.tecnicos = tecnicos;
        this.servicos = servicos;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (categorias.count() > 0) {
            return;
        }

        Categoria informatica = categorias.save(new Categoria("Informática"));
        Categoria telefonia = categorias.save(new Categoria("Telefonia"));
        equipamentos.saveAll(List.of(
            new Equipamento("Notebook", informatica),
            new Equipamento("Computador de mesa", informatica),
            new Equipamento("Celular", telefonia)
        ));
        tecnicos.saveAll(List.of(new Tecnico("Ana Lima"), new Tecnico("Rafael Costa")));
        servicos.saveAll(List.of(new Servico("Diagnóstico"), new Servico("Manutenção"), new Servico("Troca de peça")));
    }
}
