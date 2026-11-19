# Oficina OS

Aplicação web para cadastrar clientes e acompanhar ordens de serviço de uma assistência técnica. O **atendente** cadastra, consulta, edita e exclui clientes e ordens. O perfil **consulta** pode visualizar os registros. Uma ordem associa cliente, equipamento, técnico e serviço e registra a descrição e o status do atendimento. O formulário de cliente usa o ViaCEP para preencher logradouro, cidade e UF.

O pacote `controller` recebe as requisições das telas e da API. O pacote `service` aplica as regras e controla as transações. O pacote `repository` acessa o banco via Spring Data JPA. O pacote `model` contém sete entidades: Cliente, Endereco, OrdemServico, Equipamento, Categoria, Tecnico e Servico. Cada cliente tem um endereço, cada equipamento pertence a uma categoria e cada ordem referencia um cliente, equipamento, técnico e serviço. Equipamentos, categorias, técnicos e tipos de serviço vêm de um catálogo inicial preenchido na primeira execução.

## Integrantes

| Nome | Usuário do GitHub |
| --- | --- |
| Preencher antes da entrega | `@usuario` |

## Tecnologias

Java 17, Maven, Spring Boot, Spring MVC, Thymeleaf, Spring Security, Spring Data JPA, Hibernate, H2, Springdoc OpenAPI, JUnit e Mockito.

## Execução

Com o JDK 17 (ou superior) e Maven 3.9 instalados, execute na pasta que contém o `pom.xml`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Abra [http://localhost:8080](http://localhost:8080). O perfil `local` configura dois usuários de demonstração. Cadastre um cliente antes de abrir a primeira ordem. Use dados fictícios na demonstração.

| Usuário | Senha local | Permissão |
| --- | --- | --- |
| `atendente` | `oficina-local-123` | Consultar e alterar registros |
| `consulta` | `consulta-local-123` | Consultar registros |

O H2 grava os dados em `dados/` e os mantém após reiniciar. Se a consulta de CEP falhar, o formulário aceita o preenchimento manual.

Para executar os testes unitários e de integração:

```bash
mvn verify
```

Os testes usam H2 em memória e uma resposta HTTP simulada para validar o ViaCEP sem depender da rede.

### Docker

O `Dockerfile` compila o projeto e roda os testes antes de montar a imagem. Para iniciar com Docker, defina duas senhas próprias em um arquivo local `.env` com as variáveis `APP_SENHA_ATENDENTE` e `APP_SENHA_CONSULTA` e execute:

```bash
docker build -t oficina-os .
docker run --rm -p 8080:8080 --env-file .env -v oficina-dados:/dados oficina-os
```

Na hospedagem, defina essas duas variáveis no serviço, use uma única instância com disco persistente gravável em `/dados`, publique com HTTPS e configure `/saude` como a rota de verificação. O contêiner aceita a variável `PORT`.

## Links

- Swagger UI local (após o login) — [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI local (após o login) — [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- Aplicação publicada — inserir o endereço após o deploy.
