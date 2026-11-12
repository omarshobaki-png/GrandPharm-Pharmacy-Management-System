package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Tables.PurchaseOrder;
import Tables.PurchaseItem;

public class PurchaseOrderDAO {

    public ArrayList<PurchaseOrder> getAllPurchaseOrders() {
        ArrayList<PurchaseOrder> orders = new ArrayList<>();

        String sql = "SELECT po.PurchID AS POID, po.PurchaseDate AS OrderDate, '' AS OrderTime, "
                + "po.TotalCost AS TotalAmount, po.SuppID, po.BID, po.EID, "
                + "s.Name AS SupplierName, b.Name AS BranchName, e.Name AS EmployeeName "
                + "FROM PurchaseOrder po "
                + "JOIN Supplier s ON po.SuppID = s.SuppID "
                + "JOIN Branch b ON po.BID = b.BID "
                + "JOIN Employee e ON po.EID = e.EID "
                + "ORDER BY po.PurchID ASC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(readPurchaseOrder(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not load purchase orders");
            e.printStackTrace();
        }

        return orders;
    }

    public ArrayList<PurchaseOrder> searchPurchaseOrders(String supplierName) {
        ArrayList<PurchaseOrder> orders = new ArrayList<>();

        String sql = "SELECT po.PurchID AS POID, po.PurchaseDate AS OrderDate, '' AS OrderTime, "
                + "po.TotalCost AS TotalAmount, po.SuppID, po.BID, po.EID, "
                + "s.Name AS SupplierName, b.Name AS BranchName, e.Name AS EmployeeName "
                + "FROM PurchaseOrder po "
                + "JOIN Supplier s ON po.SuppID = s.SuppID "
                + "JOIN Branch b ON po.BID = b.BID "
                + "JOIN Employee e ON po.EID = e.EID "
                + "WHERE s.Name LIKE ? "
                + "ORDER BY po.PurchID ASC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            // Allows searching by part of the name
            ps.setString(1, "%" + supplierName + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(readPurchaseOrder(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) { //any database errors
            System.out.println("Could not search purchase orders");
            e.printStackTrace();
        }

        return orders;
    }

    public boolean addPurchaseOrder(PurchaseOrder order, ArrayList<PurchaseItem> items) {
        Connection conn = null;

        try { // in case anything fails with the Database
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String orderSql = "INSERT INTO PurchaseOrder "
                    + "(PurchID, PurchaseDate, TotalCost, SuppID, BID, EID) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement orderPs = conn.prepareStatement(orderSql);
            orderPs.setInt(1, order.getPoid());
            orderPs.setString(2, order.getOrderDate());
            orderPs.setDouble(3, order.getTotalAmount());
            orderPs.setInt(4, order.getSuppID());
            orderPs.setInt(5, order.getBid());
            orderPs.setInt(6, order.getEid());
            orderPs.executeUpdate();
            orderPs.close();

            String itemSql = "INSERT INTO PurchaseItem "
                    + "(PurchID, ProdID, Quantity, UnitCost) "
                    + "VALUES (?, ?, ?, ?)";

            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            for (PurchaseItem item : items) {
                itemPs.setInt(1, order.getPoid());
                itemPs.setInt(2, item.getProdID());
                itemPs.setInt(3, item.getQuantity());
                itemPs.setDouble(4, item.getUnitCost());
                itemPs.executeUpdate();
            }
            itemPs.close();

            conn.commit();
            conn.close();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { }
            try { if (conn != null) conn.close(); } catch (Exception ex) { }
            System.out.println("Could not add purchase order");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePurchaseOrder(PurchaseOrder order, ArrayList<PurchaseItem> items) {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement deletePs = conn.prepareStatement("DELETE FROM PurchaseItem WHERE PurchID = ?");
            deletePs.setInt(1, order.getPoid());
            deletePs.executeUpdate();
            deletePs.close();

            String orderSql = "UPDATE PurchaseOrder SET "
                    + "PurchaseDate = ?, TotalCost = ?, SuppID = ?, BID = ?, EID = ? "
                    + "WHERE PurchID = ?";

            PreparedStatement orderPs = conn.prepareStatement(orderSql);
            orderPs.setString(1, order.getOrderDate());
            orderPs.setDouble(2, order.getTotalAmount());
            orderPs.setInt(3, order.getSuppID());
            orderPs.setInt(4, order.getBid());
            orderPs.setInt(5, order.getEid());
            orderPs.setInt(6, order.getPoid());
            orderPs.executeUpdate();
            orderPs.close();

            String itemSql = "INSERT INTO PurchaseItem "
                    + "(PurchID, ProdID, Quantity, UnitCost) "
                    + "VALUES (?, ?, ?, ?)";

            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            for (PurchaseItem item : items) {
                itemPs.setInt(1, order.getPoid());
                itemPs.setInt(2, item.getProdID());
                itemPs.setInt(3, item.getQuantity());
                itemPs.setDouble(4, item.getUnitCost());
                itemPs.executeUpdate();
            }
            itemPs.close();

            conn.commit();
            conn.close();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { }
            try { if (conn != null) conn.close(); } catch (Exception ex) { }
            System.out.println("Couldn't update purchase order");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePurchaseOrder(int poid) {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement deleteItems = conn.prepareStatement("DELETE FROM PurchaseItem WHERE PurchID = ?");
            deleteItems.setInt(1, poid);
            deleteItems.executeUpdate();
            deleteItems.close();

            PreparedStatement deleteOrder = conn.prepareStatement("DELETE FROM PurchaseOrder WHERE PurchID = ?");
            deleteOrder.setInt(1, poid);
            int rows = deleteOrder.executeUpdate();
            deleteOrder.close();

            conn.commit();
            conn.close();
            return rows > 0;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { }
            try { if (conn != null) conn.close(); } catch (Exception ex) { }
            System.out.println("Could not delete purchase order");
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<PurchaseItem> getPurchaseItems(int poid) {
        ArrayList<PurchaseItem> items = new ArrayList<>();

        String sql = "SELECT pi.PurchID AS POID, pi.ProdID, pi.Quantity, pi.UnitCost, p.ProdName "
                + "FROM PurchaseItem pi "
                + "JOIN Product p ON pi.ProdID = p.ProdID "
                + "WHERE pi.PurchID = ? "
                + "ORDER BY pi.ProdID ASC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, poid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(readPurchaseItem(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) { //any database errors
            System.out.println("Could not load purchase items");
            e.printStackTrace();
        }

        return items;
    }

    public PurchaseItem getProductForLine(int prodID, int quantity, double unitCost) {
        PurchaseItem item = null;

        String sql = "SELECT ProdName FROM Product WHERE ProdID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, prodID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String prodName = rs.getString("ProdName");
                item = new PurchaseItem(0, prodID, quantity, unitCost, prodName);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not get product for line item");
            e.printStackTrace();
        }

        return item;
    }

    public double getTotalPurchases() {
        double total = 0;

        String sql = "SELECT COALESCE(SUM(TotalCost), 0) FROM PurchaseOrder";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getDouble(1);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not get total purchases");
            e.printStackTrace();
        }

        return total;
    }

    private PurchaseOrder readPurchaseOrder(ResultSet rs) throws Exception {
        int poid            = rs.getInt("POID");
        String orderDate    = rs.getString("OrderDate");
        String orderTime    = rs.getString("OrderTime");
        double totalAmount  = rs.getDouble("TotalAmount");
        int suppID          = rs.getInt("SuppID");
        int bid             = rs.getInt("BID");
        int eid             = rs.getInt("EID");
        String supplierName = rs.getString("SupplierName");
        String branchName   = rs.getString("BranchName");
        String employeeName = rs.getString("EmployeeName");

        return new PurchaseOrder(poid, orderDate, orderTime, totalAmount,
                suppID, bid, eid, supplierName, branchName, employeeName);
    }

    private PurchaseItem readPurchaseItem(ResultSet rs) throws Exception {
        int poid          = rs.getInt("POID");
        int prodID        = rs.getInt("ProdID");
        int quantity      = rs.getInt("Quantity");
        double unitCost   = rs.getDouble("UnitCost");
        String prodName   = rs.getString("ProdName");

        return new PurchaseItem(poid, prodID, quantity, unitCost, prodName);
    }
}
