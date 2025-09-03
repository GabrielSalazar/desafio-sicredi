
# Desafio Sicredi/DB - API de Produtos e Autenticação

## Descrição
Projeto implementado em Java com Spring Boot para gerenciar produtos e autenticar usuários para a solução de um desafio do banco Sicredi.

## Tecnologias
- Java 17+
- Spring Boot 3.x
- Lombok
- JUnit 5
- Mockito
- MockMvc
- AssertJ
- Jackson

## Requisitos
- Java 17+
- Maven 3.8+
- IDE compatível (recomendado IntelliJ IDEA)

## Como executar

1. Clone o repositório:

```
git clone https://github.com/GabrielSalazar/desafio-sicredi.git
cd desafio-sicredi
```

2. Execute a aplicação:

Via terminal:

```
mvn spring-boot:run
```

Via IDE:

Execute a classe `br.com.salazar.Application`

3. Execute os testes:

```
mvn test
```

## Endpoints

### Autenticação

- POST `/auth/login`

### Produtos autenticados (permitem somente com token)

- GET `/auth/products`
- POST `/auth/products/add`

### Produtos públicos

- GET `/products`
- GET `/products/{id}`
- POST `/products/add`

## Exemplos curl

Login:

```
curl -X POST http://localhost:8080/auth/login   -H "Content-Type: application/json"   -d '{"username":"emilys","password":"emilypass"}'
```

Listar produtos autenticados:

```
curl -X GET http://localhost:8080/auth/products   -H "Authorization: Bearer <TOKEN>"
```

Listar produtos públicos:

```
curl -X GET http://localhost:8080/products
curl -X GET http://localhost:8080/products/1
```

Criar produto público:

```
curl -X POST http://localhost:8080/products/add   -H "Content-Type: application/json"   -d '{
    "title": "Perfume Oil",
    "description": "Mega desconto",
    "price": 13,
    "discountPercentage": 8.4,
    "rating": 4.26,
    "stock": 65,
    "brand": "Impression",
    "category": "fragrances",
    "thumbnail": "https://dummyimage.com/thumb.jpg"
  }'
```

## Tipos de Testes

- Unitários: Testes em serviços com mocks
- Controller: Testes isolados de endpoints com MockMvc
- Funcionais: Testes integrados com casos diversos

## Cobertura

- AuthController: Login válido, inválido e validação de payload
- ProductController: Autenticação, autorização, erros e sucesso
- ProductPublicController: Listagem, criação e validação
- ProductService: CRUD, conversão de DTOs e erros

## Executar testes

```
mvn test
```

## Boas práticas

- Cobertura ampla com testes parametrizados
- Uso de Builders para criação fluente de dados
- AssertJ para assertions expressivas
- Validações completas com Bean Validation
- Sanitização e prevenção de XSS
- Isolamento com Mockito

## Considerações finais
Projeto com foco em qualidade, segurança e manutenção fácil, alguns pontos podem ser melhorados como  a criação de um report completo, documentação no código/comentarios.
