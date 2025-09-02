package br.com.salazar.exception;

import java.util.List;

/**
 * Exceção para erros de validação de dados
 *
 * Esta exceção é lançada quando:
 * - Dados obrigatórios estão ausentes
 * - Formato de dados é inválido
 * - Regras de negócio são violadas
 * - Validação de entrada falha
 *
 * @author Gabriel Salazar
 * @version 1.0
 * @since 2025-09-01
 */
public class ValidationException extends RuntimeException {

    private List<String> validationErrors;

    /**
     * Construtor com mensagem simples
     *
     * @param message Mensagem do erro de validação
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa raiz
     *
     * @param message Mensagem do erro
     * @param cause Exceção que causou este erro
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor com lista de erros de validação
     *
     * @param message Mensagem principal
     * @param validationErrors Lista detalhada dos erros
     */
    public ValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    /**
     * Retorna a lista de erros de validação específicos
     *
     * @return Lista de mensagens de erro ou null se não houver
     */
    public List<String> getValidationErrors() {
        return validationErrors;
    }

    /**
     * Construtor específico para campo obrigatório
     *
     * @param fieldName Nome do campo obrigatório
     * @return Nova instância da exceção
     */
    public static ValidationException requiredField(String fieldName) {
        return new ValidationException(fieldName + " is required and cannot be empty");
    }

    /**
     * Construtor específico para formato inválido
     *
     * @param fieldName Nome do campo com formato inválido
     * @param expectedFormat Formato esperado
     * @return Nova instância da exceção
     */
    public static ValidationException invalidFormat(String fieldName, String expectedFormat) {
        return new ValidationException(fieldName + " has invalid format. Expected: " + expectedFormat);
    }

    /**
     * Construtor específico para valor fora do range
     *
     * @param fieldName Nome do campo
     * @param minValue Valor mínimo aceito
     * @param maxValue Valor máximo aceito
     * @return Nova instância da exceção
     */
    public static ValidationException outOfRange(String fieldName, Object minValue, Object maxValue) {
        return new ValidationException(fieldName + " must be between " + minValue + " and " + maxValue);
    }

    /**
     * Construtor específico para string muito longa
     *
     * @param fieldName Nome do campo
     * @param maxLength Tamanho máximo permitido
     * @return Nova instância da exceção
     */
    public static ValidationException tooLong(String fieldName, int maxLength) {
        return new ValidationException(fieldName + " exceeds maximum length of " + maxLength + " characters");
    }

    /**
     * Construtor específico para email inválido
     *
     * @param email Email que foi rejeitado
     * @return Nova instância da exceção
     */
    public static ValidationException invalidEmail(String email) {
        return new ValidationException("Invalid email format: " + email);
    }
}
