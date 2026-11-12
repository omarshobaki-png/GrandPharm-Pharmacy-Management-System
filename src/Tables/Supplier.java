package Tables;

public class Supplier {

    private int suppID;
    private String name;
    private String phone;
    private String email;
    private String address;

    public Supplier(int suppID, String name, String phone, String email, String address) {
        this.suppID = suppID;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getSuppID() { return suppID; }
    public void setSuppID(int suppID) { this.suppID = suppID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
