package Tables;

public class Employee {

    private int eid;
    private String name;
    private String role;
    private String phone;
    private String hireDate;
    private double salary;
    private int bid;
    private String branchName;

    public Employee(int eid, String name, String role, String phone, String hireDate, double salary, int bid, String branchName) {
        this.eid = eid;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.hireDate = hireDate;
        this.salary = salary;
        this.bid = bid;
        this.branchName = branchName;
    }

    public int getEid() { return eid; }
    public void setEid(int eid) { this.eid = eid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public int getBid() { return bid; }
    public void setBid(int bid) { this.bid = bid; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
}
