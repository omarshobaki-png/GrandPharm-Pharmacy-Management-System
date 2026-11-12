package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Tables.Inventory;

public class InventoryDAO {

    public ArrayList<Inventory> getAllInventory() {
        ArrayList<Inventory> list = new ArrayList<>();

        String sql = "SELECT i.WareID, w.WareName, i.ProdID, p.ProdName, i.QuantityAvailable, i.MinStock, i.LastUpdated "
                + "FROM Inventory i "
                + "JOIN Warehouse w ON i.WareID = w.WareID "
                + "JOIN Product p ON i.ProdID = p.ProdID "
                + "ORDER BY i.WareID, i.ProdID";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(readInventory(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not load inventory");
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<Inventory> searchInventory(String keyword) {
        ArrayList<Inventory> list = new ArrayList<>();

        String sql = "SELECT i.WareID, w.WareName, i.ProdID, p.ProdName, i.QuantityAvailable, i.MinStock, i.LastUpdated "
                + "FROM Inventory i "
                + "JOIN Warehouse w ON i.WareID = w.WareID "
                + "JOIN Product p ON i.ProdID = p.ProdID "
                + "WHERE p.ProdName LIKE ? OR w.WareName LIKE ? "
                + "ORDER BY i.WareID, i.ProdID";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            // Allows searching by part of the name
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(readInventory(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) { //any database errors
            System.out.println("Could not search inventory");
            e.printStackTrace();
        }

        return list;
    }

    public boolean addInventory(Inventory inv) {
        String sql = "INSERT INTO Inventory (WareID, ProdID, QuantityAvailable, MinStock, LastUpdated) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE QuantityAvailable = QuantityAvailable + VALUES(QuantityAvailable), "
                + "MinStock = VALUES(MinStock), LastUpdated = VALUES(LastUpdated)";

        try { // in case anything fails within the DataBase
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, inv.getWareID());
            ps.setInt(2, inv.getProdID());
            ps.setInt(3, inv.getQuantityAvailable());
            ps.setInt(4, inv.getMinStock());
            ps.setString(5, inv.getLastUpdated());

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Could not add inventory");
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateInventory(Inventory inv) {
        String sql = "UPDATE Inventory "
                + "SET QuantityAvailable = ?, MinStock = ?, LastUpdated = ? "
                + "WHERE WareID = ? AND ProdID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, inv.getQuantityAvailable());
            ps.setInt(2, inv.getMinStock());
            ps.setString(3, inv.getLastUpdated());
            ps.setInt(4, inv.getWareID());
            ps.setInt(5, inv.getProdID());

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Couldn't update inventory");
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteInventory(int wareID, int prodID) {
        String sql = "DELETE FROM Inventory WHERE WareID = ? AND ProdID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, wareID);
            ps.setInt(2, prodID);

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Could not delete inventory");
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<Inventory> getLowStockItems() {
        ArrayList<Inventory> list = new ArrayList<>();

        String sql = "SELECT i.WareID, w.WareName, i.ProdID, p.ProdName, i.QuantityAvailable, i.MinStock, i.LastUpdated "
                + "FROM Inventory i "
                + "JOIN Warehouse w ON i.WareID = w.WareID "
                + "JOIN Product p ON i.ProdID = p.ProdID "
                + "WHERE i.QuantityAvailable <= i.MinStock "
                + "ORDER BY i.QuantityAvailable";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(readInventory(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not load low stock items");
            e.printStackTrace();
        }

        return list;
    }

    private Inventory readInventory(ResultSet rs) throws Exception {
        int wareID = rs.getInt("WareID");
        String wareName = rs.getString("WareName");
        int prodID = rs.getInt("ProdID");
        String prodName = rs.getString("ProdName");
        int quantityAvailable = rs.getInt("QuantityAvailable");
        int minStock = rs.getInt("MinStock");
        String lastUpdated = rs.getString("LastUpdated");

        return new Inventory(wareID, wareName, prodID, prodName, quantityAvailable, minStock, lastUpdated);
    }
}
