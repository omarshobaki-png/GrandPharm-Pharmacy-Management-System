package Tables;

public class Product {

    private int prodID;
    private String prodName;
    private String categoryName;
    private double price;
    private boolean prescriptionRequired;
    private int catID;

    public Product(int prodID, String prodName, String categoryName, double price, boolean prescriptionRequired, int catID) {
        this.prodID = prodID;
        this.prodName = prodName;
        this.categoryName = categoryName;
        this.price = price;
        this.prescriptionRequired = prescriptionRequired;
        this.catID = catID;
    }

    public int getProdID() { return prodID; }
    public void setProdID(int prodID) { this.prodID = prodID; }

    public String getProdName() { return prodName; }
    public void setProdName(String prodName) { this.prodName = prodName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isPrescriptionRequired() { return prescriptionRequired; }
    public void setPrescriptionRequired(boolean prescriptionRequired) { this.prescriptionRequired = prescriptionRequired; }

    public int getCatID() { return catID; }
    public void setCatID(int catID) { this.catID = catID; }
}
