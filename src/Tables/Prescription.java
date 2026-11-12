package Tables;

public class Prescription {
    private int prescID;
    private String prescriptionDate;
    private String prescriptionTime;
    private String doctorName;
    private double totalAmount;
    private int cid;
    private int bid;
    private int eid;

    private String customerName;
    private String branchName;
    private String employeeName;

    public Prescription(int prescID, String prescriptionDate, String prescriptionTime, String doctorName,
                        double totalAmount, int cid, int bid, int eid,
                        String customerName, String branchName, String employeeName) {
        this.prescID = prescID;
        this.prescriptionDate = prescriptionDate;
        this.prescriptionTime = prescriptionTime;
        this.doctorName = doctorName;
        this.totalAmount = totalAmount;
        this.cid = cid;
        this.bid = bid;
        this.eid = eid;
        this.customerName = customerName;
        this.branchName = branchName;
        this.employeeName = employeeName;
    }

    public int getPrescID() { return prescID; }
    public void setPrescID(int prescID) { this.prescID = prescID; }

    public String getPrescriptionDate() { return prescriptionDate; }
    public void setPrescriptionDate(String prescriptionDate) { this.prescriptionDate = prescriptionDate; }

    public String getPrescriptionTime() { return prescriptionTime; }
    public void setPrescriptionTime(String prescriptionTime) { this.prescriptionTime = prescriptionTime; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public int getCid() { return cid; }
    public void setCid(int cid) { this.cid = cid; }

    public int getBid() { return bid; }
    public void setBid(int bid) { this.bid = bid; }

    public int getEid() { return eid; }
    public void setEid(int eid) { this.eid = eid; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
}
