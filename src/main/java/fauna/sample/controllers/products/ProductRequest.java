package fauna.sample.controllers.products;

import com.fauna.annotation.FaunaField;

public class ProductRequest {
    private String name;
    private String description;
    private Integer price;
    // Fauna object and document fields will be matched to class fields by default, but
    // you can override the name with @FaunaField( name = "my_name" )
    @FaunaField( name = "stock")
    private Integer stockQuantity;
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

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public String getCategory() {
        return category;
    }
}
