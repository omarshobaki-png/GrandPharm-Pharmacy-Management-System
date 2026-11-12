package Tables;

public class Inventory {

    private int wareID;
    private String warehouseName;
    private int prodID;
    private String productName;
    private int quantityAvailable;
    private int minStock;
    private String lastUpdated;

    public Inventory(int wareID, String warehouseName, int prodID, String productName,
                     int quantityAvailable, int minStock, String lastUpdated) {
        this.wareID = wareID;
        this.warehouseName = warehouseName;
        this.prodID = prodID;
        this.productName = productName;
        this.quantityAvailable = quantityAvailable;
        this.minStock = minStock;
        this.lastUpdated = lastUpdated;
    }

    public int getWareID()              { return wareID; }
    public void setWareID(int wareID)   { this.wareID = wareID; }

    public String getWarehouseName()                    { return warehouseName; }
    public void setWarehouseName(String warehouseName)  { this.warehouseName = warehouseName; }

    public int getProdID()              { return prodID; }
    public void setProdID(int prodID)   { this.prodID = prodID; }

    public String getProductName()                  { return productName; }
    public void setProductName(String productName)  { this.productName = productName; }

    public int getQuantityAvailable()                       { return quantityAvailable; }
    public void setQuantityAvailable(int quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public int getMinStock()                { return minStock; }
    public void setMinStock(int minStock)   { this.minStock = minStock; }

    public String getLastUpdated()                  { return lastUpdated; }
    public void setLastUpdated(String lastUpdated)  { this.lastUpdated = lastUpdated; }

    // handy check — tells us if this item is running low
    public boolean isLowStock() {
        return quantityAvailable <= minStock;
    }
}
