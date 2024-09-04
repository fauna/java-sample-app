package fauna.sample.controllers.products;

import com.fauna.annotation.FaunaId;

public class Product {

    public static class Category {
        @FaunaId
        private String id;
        private String name;
        private String description;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    @FaunaId
    private String id;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private Category category;

    public String getId() {
        return id;
    }

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

    public Category getCategory() {
        return category;
    }
}
