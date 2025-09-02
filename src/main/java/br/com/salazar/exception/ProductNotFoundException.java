package br.com.salazar.exception;

/**
 * Exceção para quando um produto não é encontrado no sistema
 *
 * Esta exceção é lançada quando:
 * - Busca por ID de produto inexistente
 * - Produto foi removido do catálogo
 * - ID fornecido é inválido
 *
 * @author Gabriel Salazar
 * @version 1.0
 * @since 2025-09-01
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Construtor com mensagem customizada
     *
     * @param message Mensagem específica sobre o produto não encontrado
     */
    public ProductNotFoundException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa raiz
     *
     * @param message Mensagem do erro
     * @param cause Exceção que causou este erro
     */
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor específico para produto não encontrado por ID
     *
     * @param productId ID do produto que não foi encontrado
     */
    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId);
    }

    /**
     * Construtor específico para produto não encontrado por título
     *
     * @param title Título do produto que não foi encontrado
     * @return Nova instância da exceção
     */
    public static ProductNotFoundException byTitle(String title) {
        return new ProductNotFoundException("Product not found with title: " + title);
    }

    /**
     * Construtor específico para produto fora de estoque
     *
     * @param productId ID do produto sem estoque
     * @return Nova instância da exceção
     */
    public static ProductNotFoundException outOfStock(Long productId) {
        return new ProductNotFoundException("Product with id " + productId + " is out of stock");
    }

    /**
     * Construtor específico para categoria sem produtos
     *
     * @param category Categoria que não possui produtos
     * @return Nova instância da exceção
     */
    public static ProductNotFoundException noneInCategory(String category) {
        return new ProductNotFoundException("No products found in category: " + category);
    }
}
