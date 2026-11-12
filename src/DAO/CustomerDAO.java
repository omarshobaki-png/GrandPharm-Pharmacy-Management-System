package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Tables.Customer;

public class CustomerDAO {

    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();

        String sql = "SELECT CID, Name, Phone, DOB, Gender "
                + "FROM Customer "
                + "ORDER BY CID ASC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                customers.add(readCustomer(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not load customers");
            e.printStackTrace();
        }

        return customers;
    }

    public ArrayList<Customer> searchCustomers(String name) {
        ArrayList<Customer> customers = new ArrayList<>();

        String sql = "SELECT CID, Name, Phone, DOB, Gender "
                + "FROM Customer "
                + "WHERE Name LIKE ? "
                + "ORDER BY CID ASC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            // Allows searching by part of the name
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                customers.add(readCustomer(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) { //any database errors
            System.out.println("Could not search customers");
            e.printStackTrace();
        }

        return customers;
    }

    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO Customer "
                + "(CID, Name, Phone, DOB, Gender) "
                + "VALUES (?, ?, ?, ?, ?)";

        try { // in case anything fails within the DataBase
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customer.getCid());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getDob());
            ps.setString(5, customer.getGender());

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Could not add customer");
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE Customer "
                + "SET Name = ?, Phone = ?, DOB = ?, Gender = ? "
                + "WHERE CID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getDob());
            ps.setString(4, customer.getGender());
            ps.setInt(5, customer.getCid());

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Couldn't update customer");
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteCustomer(int cid) {
        String sql = "DELETE FROM Customer WHERE CID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, cid);

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Could not delete customer");
            e.printStackTrace();
        }

        return false;
    }

    public int getTotalSales() { return runCountQuery("SELECT COUNT(*) FROM Sale"); }
    public int getTotalPrescriptions() { return runCountQuery("SELECT COUNT(*) FROM Prescription"); }

    private int runCountQuery(String sql) {
        int count = 0;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not run count query");
            e.printStackTrace();
        }

        return count;
    }

    private Customer readCustomer(ResultSet rs) throws Exception {
        int cid       = rs.getInt("CID");
        String name   = rs.getString("Name");
        String phone  = rs.getString("Phone");
        String dob    = rs.getString("DOB");
        String gender = rs.getString("Gender");

        return new Customer(cid, name, phone, dob, gender);
    }
}
