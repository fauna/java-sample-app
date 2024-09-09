package fauna.sample.controllers.products;

import com.fauna.annotation.FaunaId;

public class Product {

    public static class Category {

        // Add @FaunaId attribute to indicate that it's an ID on a document. This tells
        // the codec library to ignore this during encoding.
        //
        // To use client-generated IDs, use @FaunaId( isClientGenerate = true), which will
        // tell the codec library to encode it if it is not null.
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

    // Add @FaunaId attribute to indicate that it's an ID on a document. This tells
    // the codec library to ignore this during encoding.
    //
    // To use client-generated IDs, use @FaunaId( isClientGenerate = true), which will
    // tell the codec library to encode it if it is not null.
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
