package fauna.sample.controllers.products;

public class ProductRequest {
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private String category;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }
}
