package Tables;

public class PrescriptionItem {
    private int prescID;
    private int prodID;
    private int quantity;
    private double unitPrice;
    private String dosage;
    private String prodName;

    public PrescriptionItem(int prescID, int prodID, int quantity, double unitPrice, String dosage, String prodName) {
        this.prescID = prescID;
        this.prodID = prodID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.dosage = dosage;
        this.prodName = prodName;
    }

    public int getPrescID() { return prescID; }
    public void setPrescID(int prescID) { this.prescID = prescID; }

    public int getProdID() { return prodID; }
    public void setProdID(int prodID) { this.prodID = prodID; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getProdName() { return prodName; }
    public void setProdName(String prodName) { this.prodName = prodName; }

    public double getLineTotal() { return quantity * unitPrice; }
}
