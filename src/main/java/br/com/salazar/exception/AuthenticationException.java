package br.com.salazar.exception;

/**
 * Exceção customizada para problemas de autenticação
 *
 * Esta exceção é lançada quando:
 * - Credenciais inválidas são fornecidas
 * - Usuário não existe no sistema
 * - Token JWT é inválido ou expirado
 * - Falhas gerais de autenticação
 *
 * @author Gabriel Salazar
 * @version 1.0
 * @since 2025-09-01
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Construtor padrão com mensagem genérica
     */
    public AuthenticationException() {
        super("Authentication failed");
    }

    /**
     * Construtor com mensagem customizada
     *
     * @param message Mensagem específica do erro de autenticação
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa raiz
     *
     * @param message Mensagem do erro
     * @param cause Exceção que causou este erro
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor apenas com causa raiz
     *
     * @param cause Exceção que causou este erro
     */
    public AuthenticationException(Throwable cause) {
        super("Authentication failed", cause);
    }

    /**
     * Construtor específico para usuário não encontrado
     *
     * @param username Nome do usuário que não foi encontrado
     * @return Nova instância da exceção
     */
    public static AuthenticationException userNotFound(String username) {
        return new AuthenticationException("User not found: " + username);
    }

    /**
     * Construtor específico para credenciais inválidas
     *
     * @return Nova instância da exceção
     */
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Invalid username or password");
    }

    /**
     * Construtor específico para token expirado
     *
     * @return Nova instância da exceção
     */
    public static AuthenticationException tokenExpired() {
        return new AuthenticationException("Authentication token has expired");
    }
}
