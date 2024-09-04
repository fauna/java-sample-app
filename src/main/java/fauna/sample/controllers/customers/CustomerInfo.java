package fauna.sample.controllers.customers;

public class CustomerInfo {

    private String name;
    private String email;
    private Address address;

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
