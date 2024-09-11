package fauna.sample.controllers.orders;


public class AddToCartRequest {

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    private String productName;
    private int quantity;
}
