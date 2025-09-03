package br.com.salazar.model.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class ProductCreateRequestDto {

    @JsonProperty("title")
    @NotBlank(message = "Título do produto é obrigatório")
    @Size(min = 3, max = 100, message = "Título deve ter entre 3 e 100 caracteres")
    private String title;

    @JsonProperty("description")
    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @JsonProperty("price")
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    private Double price;

    @JsonProperty("discountPercentage")
    @NotNull(message = "Desconto é obrigatório")
    @DecimalMin(value = "0.0", message = "Desconto deve ser no mínimo 0%")
    @DecimalMax(value = "100.0", message = "Desconto deve ser no máximo 100%")
    private Double discountPercentage;

    @JsonProperty("rating")
    @NotNull(message = "Rating é obrigatório")
    @DecimalMin(value = "0.0", message = "Rating deve ser no mínimo 0")
    @DecimalMax(value = "5.0", message = "Rating deve ser no máximo 5")
    private Double rating;

    @JsonProperty("stock")
    @NotNull(message = "Stock é obrigatório")
    @Min(value = 0, message = "Stock não pode ser negativo")
    private Integer stock;

    @JsonProperty("brand")
    @NotBlank(message = "Marca é obrigatória")
    private String brand;

    @JsonProperty("category")
    @NotBlank(message = "Categoria é obrigatória")
    private String category;

    @JsonProperty("thumbnail")
    @NotBlank(message = "Thumbnail é obrigatório")
    private String thumbnail;

    public ProductCreateRequestDto() {}

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getDiscountPercentage() {
        return discountPercentage; }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage; }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
