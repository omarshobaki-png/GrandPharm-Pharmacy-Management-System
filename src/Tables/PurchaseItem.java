package Tables;

public class PurchaseItem {
    private int poid;
    private int prodID;
    private int quantity;
    private double unitCost;
    private String prodName;

    public PurchaseItem(int poid, int prodID, int quantity, double unitCost, String prodName) {
        this.poid = poid;
        this.prodID = prodID;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.prodName = prodName;
    }

    public int getPoid() { return poid; }
    public void setPoid(int poid) { this.poid = poid; }

    public int getProdID() { return prodID; }
    public void setProdID(int prodID) { this.prodID = prodID; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitCost() { return unitCost; }
    public void setUnitCost(double unitCost) { this.unitCost = unitCost; }

    public String getProdName() { return prodName; }
    public void setProdName(String prodName) { this.prodName = prodName; }

    public double getLineTotal() { return quantity * unitCost; }
}
