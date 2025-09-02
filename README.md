# Projeto de Testes - Desafio Sicredi

Projeto desenvolvido com Spring Boot para testes de endpoints, para a solução de um desafio do banco Sicredi.

## Requisitos

- Java 17+
- Maven 3.8+
- IDE compatível com Spring Boot (IntelliJ IDEA recomendado)

## Tecnologias

- Spring Boot
- Spring Web
- Lombok
- JUnit 5
- Mockito
  
## Configuração

1. Clone o repositório:
git clone https://github.com/GabrielSalazar/desafio-sicredi.git
cd desafio-sicredi


## Executando - Via Maven
mvn spring-boot:run

## Executando - Via IDE
Execute a classe br.com.salazar.Application

## Testando - Execute os testes com Maven
mvn test
ou
mvn clean package
java -jar target/desafio-sicredi-*.jar

## Testar endpoints (exemplos cURL)
- Login

curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"emilys","password":"emilyspass"}'

## Produtos autenticado

curl -X GET http://localhost:8080/auth/products \
  -H "Authorization: Bearer <TOKEN>"
## Produtos público
curl -X GET http://localhost:8080/products
curl -X GET http://localhost:8080/products/1

## Criar produto (público)
curl -X POST http://localhost:8080/products/add \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Perfume Oil",
    "description":"Mega Discount...",
    "price":13,
    "discountPercentage":8.4,
    "rating":4.26,
    "stock":65,
    "brand":"Impression of Acqua Di Gio",
    "category":"fragrances",
    "thumbnail":"https://i.dummyjson.com/data/products/11/thumnail.jpg"
  }'

## Cobertura de cenários:
1. AuthControllerTest:
- 201 para login com payload válido (service mock retorna sucesso)
- 400 para payload inválido (Bean Validation)
- 401 para credenciais inválidas (service lança exceção de autenticação)

2. ProductControllerTest (autenticado):
- 200 com Bearer válido
- 401 sem header ou token inválido
- 403 propagação de “Authentication Problem”

3. ProductPublicControllerTest (público):
- 200 GET /products
- 200 GET /products/{id}

4. ProductServiceTest:
- Sucesso 200/201 com corpo
- Mapeamento de 401/403/404 para exceções específicas
- Falha em status inesperado
