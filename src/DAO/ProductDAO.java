package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Tables.Product;

public class ProductDAO {

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();

        String sql = "SELECT p.ProdID, p.ProdName, c.Name AS CategoryName, p.Price, p.Prescription_Requirement, p.CatID "
                + "FROM Product p "
                + "JOIN Category c ON p.CatID = c.CatID "
                + "ORDER BY p.ProdID";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                products.add(readProduct(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not load products");
            e.printStackTrace();
        }

        return products;
    }

    public ArrayList<Product> searchProducts(String name) {
        ArrayList<Product> products = new ArrayList<>();

        String sql = "SELECT p.ProdID, p.ProdName, c.Name AS CategoryName, p.Price, p.Prescription_Requirement, p.CatID "
                + "FROM Product p "
                + "JOIN Category c ON p.CatID = c.CatID "
                + "WHERE p.ProdName LIKE ? "
                + "ORDER BY p.ProdID";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            // Allows searching by part of the name
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(readProduct(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) { //any database errors
            System.out.println("Could not search products");
            e.printStackTrace();
        }

        return products;
    }

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO Product (ProdName, Price, Prescription_Requirement, CatID) "
                + "VALUES (?, ?, ?, ?)";

        try { // in case anything fails within the DataBase
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, product.getProdName());
            ps.setDouble(2, product.getPrice());
            ps.setBoolean(3, product.isPrescriptionRequired());
            ps.setInt(4, product.getCatID());

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Could not add product");
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE Product "
                + "SET ProdName = ?, Price = ?, Prescription_Requirement = ?, CatID = ? "
                + "WHERE ProdID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, product.getProdName());
            ps.setDouble(2, product.getPrice());
            ps.setBoolean(3, product.isPrescriptionRequired());
            ps.setInt(4, product.getCatID());
            ps.setInt(5, product.getProdID());

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Couldn't update product");
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteProduct(int prodID) {
        String sql = "DELETE FROM Product WHERE ProdID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, prodID);

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Could not delete product");
            e.printStackTrace();
        }

        return false;
    }

    private Product readProduct(ResultSet rs) throws Exception {
        int prodID = rs.getInt("ProdID");
        String prodName = rs.getString("ProdName");
        String categoryName = rs.getString("CategoryName");
        double price = rs.getDouble("Price");
        boolean prescriptionRequired = rs.getBoolean("Prescription_Requirement");
        int catID = rs.getInt("CatID");

        return new Product(prodID, prodName, categoryName, price, prescriptionRequired, catID);
    }
}
