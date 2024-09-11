package fauna.sample.controllers.customers;

import com.fauna.annotation.FaunaId;

import java.util.List;

public class Customer {
    // Add @FaunaId attribute to indicate that it's an ID on a document. This tells
    // the codec library to ignore this during encoding.
    //
    // To use client-generated IDs, use @FaunaId( isClientGenerate = true), which will
    // tell the codec library to encode it if it is not null.
    @FaunaId
    private String id;

    private String name;
    private String email;
    private Address address;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public static class Address {
        private String street;
        private String city;
        private String state;
        private String postalCode;
        private String country;


        public String getStreet() {
            return street;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getCountry() {
            return country;
        }
    }
}
