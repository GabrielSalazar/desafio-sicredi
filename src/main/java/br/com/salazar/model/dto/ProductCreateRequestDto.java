package br.com.salazar.model.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class ProductCreateRequestDto {

    @JsonProperty("title")
    @NotBlank
    private String title;

    @JsonProperty("description")
    @NotBlank
    private String description;

    @JsonProperty("price")
    @NotNull @Positive
    private Double price;

    @JsonProperty("discountPercentage")
    @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
    private Double discountPercentage;

    @JsonProperty("rating")
    @NotNull @DecimalMin("0.0") @DecimalMax("5.0")
    private Double rating;

    @JsonProperty("stock")
    @NotNull @Min(0)
    private Integer stock;

    @JsonProperty("brand")
    @NotBlank
    private String brand;

    @JsonProperty("category")
    @NotBlank
    private String category;

    @JsonProperty("thumbnail")
    @NotBlank
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
