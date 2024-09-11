package fauna.sample.controllers.products;

import com.fauna.annotation.FaunaField;
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
    // Fauna object and document fields will be matched to class fields by default, but
    // you can override the name with @FaunaField( name = "my_name" )
    @FaunaField( name = "stock")
    private Integer stockQuantity;
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

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public Category getCategory() {
        return category;
    }
}
