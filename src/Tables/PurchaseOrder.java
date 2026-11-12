package Tables;

public class PurchaseOrder {
    private int poid;
    private String orderDate;
    private String orderTime;
    private double totalAmount;
    private int suppID;
    private int bid;
    private int eid;

    private String supplierName;
    private String branchName;
    private String employeeName;

    public PurchaseOrder(int poid, String orderDate, String orderTime, double totalAmount,
                         int suppID, int bid, int eid,
                         String supplierName, String branchName, String employeeName) {
        this.poid = poid;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.totalAmount = totalAmount;
        this.suppID = suppID;
        this.bid = bid;
        this.eid = eid;
        this.supplierName = supplierName;
        this.branchName = branchName;
        this.employeeName = employeeName;
    }

    public int getPoid() { return poid; }
    public void setPoid(int poid) { this.poid = poid; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getOrderTime() { return orderTime; }
    public void setOrderTime(String orderTime) { this.orderTime = orderTime; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public int getSuppID() { return suppID; }
    public void setSuppID(int suppID) { this.suppID = suppID; }

    public int getBid() { return bid; }
    public void setBid(int bid) { this.bid = bid; }

    public int getEid() { return eid; }
    public void setEid(int eid) { this.eid = eid; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
}
