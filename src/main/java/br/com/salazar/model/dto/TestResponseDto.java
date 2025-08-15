package br.com.salazar.model.dto;

public class TestResponseDto {
    private String status;
    private String method;

    public TestResponseDto(String status, String method) {
        this.status = status;
        this.method = method;
    }

    // Getters e Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}

