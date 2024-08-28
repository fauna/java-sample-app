package fauna.sample.controllers.products;

public class Product {

    public static class Category {
        public String id;
        public String name;
        public String description;
    }

    public String id;
    public String name;
    public String description;
    public Integer price;
    public Integer stock;
    public Category category;

}
