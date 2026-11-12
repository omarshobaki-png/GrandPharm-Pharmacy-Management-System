package Tables;

public class Sale {

    private int sid;
    private String saleDate;
    private String saleTime;
    private double totalAmount;
    private int cid;
    private int bid;
    private int eid;
    private int payMethodID;

    // display-only fields (from joins, not stored in Sale table)
    private String customerName;
    private String branchName;
    private String employeeName;
    private String paymentMethodName;

    public Sale(int sid, String saleDate, String saleTime, double totalAmount,
                int cid, int bid, int eid, int payMethodID,
                String customerName, String branchName,
                String employeeName, String paymentMethodName) {
        this.sid = sid;
        this.saleDate = saleDate;
        this.saleTime = saleTime;
        this.totalAmount = totalAmount;
        this.cid = cid;
        this.bid = bid;
        this.eid = eid;
        this.payMethodID = payMethodID;
        this.customerName = customerName;
        this.branchName = branchName;
        this.employeeName = employeeName;
        this.paymentMethodName = paymentMethodName;
    }

    public int getSid() { return sid; }
    public void setSid(int sid) { this.sid = sid; }

    public String getSaleDate() { return saleDate; }
    public void setSaleDate(String saleDate) { this.saleDate = saleDate; }

    public String getSaleTime() { return saleTime; }
    public void setSaleTime(String saleTime) { this.saleTime = saleTime; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public int getCid() { return cid; }
    public void setCid(int cid) { this.cid = cid; }

    public int getBid() { return bid; }
    public void setBid(int bid) { this.bid = bid; }

    public int getEid() { return eid; }
    public void setEid(int eid) { this.eid = eid; }

    public int getPayMethodID() { return payMethodID; }
    public void setPayMethodID(int payMethodID) { this.payMethodID = payMethodID; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getPaymentMethodName() { return paymentMethodName; }
    public void setPaymentMethodName(String paymentMethodName) { this.paymentMethodName = paymentMethodName; }
}
