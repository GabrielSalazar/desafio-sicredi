# Projeto de Testes - Desafio Sicredi

API REST desenvolvida com Spring Boot para testes de endpoints.

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

## Executando - Via Maven
mvn spring-boot:run

## Executando - Via IDE
Execute a classe br.com.salazar.Application

## Testando - Execute os testes com Maven
mvn test

## Endpoints
GET /test
Retorna status da API

Exemplo de resposta:
{
"status": "ok",
"method": "GET"
}