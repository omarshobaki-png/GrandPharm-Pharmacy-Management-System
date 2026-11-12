package Tables;

public class Customer {

    private int cid;
    private String name;
    private String phone;
    private String dob;
    private String gender;

    public Customer(int cid, String name, String phone, String dob, String gender) {
        this.cid = cid;
        this.name = name;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
    }

    public int getCid() { return cid; }
    public void setCid(int cid) { this.cid = cid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
