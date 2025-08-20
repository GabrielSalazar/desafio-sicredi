package br.com.salazar.model.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ProductsResponseDto {

    @JsonProperty("products")
    private List<ProductDto> products;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("skip")
    private Integer skip;

    @JsonProperty("limit")
    private Integer limit;

    public ProductsResponseDto() {}

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }
}

