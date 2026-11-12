package Tables;

public class Batch {

    private int wareID;
    private int bNum;
    private int prodID;
    private String manufacturingDate;
    private String expDate;
    private int quantityReceived;
    private int quantityRemaining;
    private String prodName;

    public Batch(int wareID, int bNum, int prodID, String manufacturingDate,
                 String expDate, int quantityReceived, int quantityRemaining,
                 String prodName) {
        this.wareID = wareID;
        this.bNum = bNum;
        this.prodID = prodID;
        this.manufacturingDate = manufacturingDate;
        this.expDate = expDate;
        this.quantityReceived = quantityReceived;
        this.quantityRemaining = quantityRemaining;
        this.prodName = prodName;
    }

    public int getWareID() { return wareID; }
    public void setWareID(int wareID) { this.wareID = wareID; }

    public int getBNum() { return bNum; }
    public void setBNum(int bNum) { this.bNum = bNum; }

    public int getProdID() { return prodID; }
    public void setProdID(int prodID) { this.prodID = prodID; }

    public String getManufacturingDate() { return manufacturingDate; }
    public void setManufacturingDate(String manufacturingDate) { this.manufacturingDate = manufacturingDate; }

    public String getExpDate() { return expDate; }
    public void setExpDate(String expDate) { this.expDate = expDate; }

    public int getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(int quantityReceived) { this.quantityReceived = quantityReceived; }

    public int getQuantityRemaining() { return quantityRemaining; }
    public void setQuantityRemaining(int quantityRemaining) { this.quantityRemaining = quantityRemaining; }

    public String getProdName() { return prodName; }
    public void setProdName(String prodName) { this.prodName = prodName; }
}