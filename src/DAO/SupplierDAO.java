package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Tables.Supplier;

public class SupplierDAO {

    public ArrayList<Supplier> getAllSuppliers() {
        ArrayList<Supplier> suppliers = new ArrayList<>();

        String sql = "SELECT SuppID, Name, Phone, Email, Address "
                + "FROM Supplier "
                + "ORDER BY SuppID";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                suppliers.add(readSupplier(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not load suppliers");
            e.printStackTrace();
        }

        return suppliers;
    }

    public ArrayList<Supplier> searchSuppliers(String name) {
        ArrayList<Supplier> suppliers = new ArrayList<>();

        String sql = "SELECT SuppID, Name, Phone, Email, Address "
                + "FROM Supplier "
                + "WHERE Name LIKE ? "
                + "ORDER BY SuppID";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            // Allows searching by part of the name
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                suppliers.add(readSupplier(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) { //any database errors
            System.out.println("Could not search suppliers");
            e.printStackTrace();
        }

        return suppliers;
    }

    public boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO Supplier (Name, Phone, Email, Address) "
                + "VALUES (?, ?, ?, ?)";

        try { // in case anything fails within the DataBase
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getPhone());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getAddress());

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Could not add supplier");
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE Supplier "
                + "SET Name = ?, Phone = ?, Email = ?, Address = ? "
                + "WHERE SuppID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getPhone());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getAddress());
            ps.setInt(5, supplier.getSuppID());

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Couldn't update supplier");
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteSupplier(int suppID) {
        String sql = "DELETE FROM Supplier WHERE SuppID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, suppID);

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Could not delete supplier");
            e.printStackTrace();
        }

        return false;
    }

    public int getTotalProductsSupplied() {
        return runCountQuery("SELECT COUNT(*) FROM SupplierProduct");
    }

    public int getTotalPurchaseOrders() {
        return runCountQuery("SELECT COUNT(*) FROM PurchaseOrder");
    }

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

    private Supplier readSupplier(ResultSet rs) throws Exception {
        int suppID = rs.getInt("SuppID");
        String name = rs.getString("Name");
        String phone = rs.getString("Phone");
        String email = rs.getString("Email");
        String address = rs.getString("Address");

        return new Supplier(suppID, name, phone, email, address);
    }
}
