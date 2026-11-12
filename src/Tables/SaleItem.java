package Tables;

public class SaleItem {

    private int sid;
    private int prodID;
    private int quantity;
    private double unitPrice;

    // display-only field (from join with Product)
    private String prodName;

    public SaleItem(int sid, int prodID, int quantity, double unitPrice, String prodName) {
        this.sid = sid;
        this.prodID = prodID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.prodName = prodName;
    }

    public int getSid() { return sid; }
    public void setSid(int sid) { this.sid = sid; }

    public int getProdID() { return prodID; }
    public void setProdID(int prodID) { this.prodID = prodID; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public String getProdName() { return prodName; }
    public void setProdName(String prodName) { this.prodName = prodName; }

    // computed field — quantity * unit price
    public double getLineTotal() {
        return quantity * unitPrice;
    }
}
