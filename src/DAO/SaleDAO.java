package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import Tables.Sale;
import Tables.SaleItem;

public class SaleDAO {

    public ArrayList<Sale> getAllSales() {
        ArrayList<Sale> sales = new ArrayList<>();

        String sql = "SELECT s.SID, s.SaleDate, s.SaleTime, s.TotalAmount, s.CID, s.BID, s.EID, s.PayMethodID, c.Name AS CustomerName, b.Name AS BranchName, e.Name AS EmployeeName, pm.MethodName AS PaymentMethodName "
                + "FROM Sale s "
                + "JOIN Customer c ON s.CID = c.CID "
                + "JOIN Branch b ON s.BID = b.BID "
                + "JOIN Employee e ON s.EID = e.EID "
                + "JOIN PaymentMethod pm ON s.PayMethodID = pm.PayMethodID "
                + "ORDER BY s.SID";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sales.add(readSale(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not load sales");
            e.printStackTrace();
        }

        return sales;
    }

    public ArrayList<Sale> searchSales(String customerName) {
        ArrayList<Sale> sales = new ArrayList<>();

        String sql = "SELECT s.SID, s.SaleDate, s.SaleTime, s.TotalAmount, s.CID, s.BID, s.EID, s.PayMethodID, c.Name AS CustomerName, b.Name AS BranchName, e.Name AS EmployeeName, pm.MethodName AS PaymentMethodName "
                + "FROM Sale s "
                + "JOIN Customer c ON s.CID = c.CID "
                + "JOIN Branch b ON s.BID = b.BID "
                + "JOIN Employee e ON s.EID = e.EID "
                + "JOIN PaymentMethod pm ON s.PayMethodID = pm.PayMethodID "
                + "WHERE c.Name LIKE ? "
                + "ORDER BY s.SID";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            // Allows searching by part of the name
            ps.setString(1, "%" + customerName + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sales.add(readSale(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) { //any database errors
            System.out.println("Could not search sales");
            e.printStackTrace();
        }

        return sales;
    }

    public boolean addSale(Sale sale, ArrayList<SaleItem> items) {
        Connection conn = null;
        try { // in case anything fails with the Database
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String saleSql = "INSERT INTO Sale (SaleDate, SaleTime, TotalAmount, CID, BID, EID, PayMethodID) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement salePs = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS);
            salePs.setString(1, sale.getSaleDate());
            salePs.setString(2, sale.getSaleTime());
            salePs.setDouble(3, sale.getTotalAmount());
            salePs.setInt(4, sale.getCid());
            salePs.setInt(5, sale.getBid());
            salePs.setInt(6, sale.getEid());
            salePs.setInt(7, sale.getPayMethodID());
            salePs.executeUpdate();

            int generatedSid = 0;
            ResultSet generatedKeys = salePs.getGeneratedKeys();
            if (generatedKeys.next()) generatedSid = generatedKeys.getInt(1);
            generatedKeys.close();
            salePs.close();

            String itemSql = "INSERT INTO SaleItem (SID, ProdID, Quantity, UnitPrice) "
                    + "VALUES (?, ?, ?, ?)";

            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            for (SaleItem item : items) {
                itemPs.setInt(1, generatedSid);
                itemPs.setInt(2, item.getProdID());
                itemPs.setInt(3, item.getQuantity());
                itemPs.setDouble(4, item.getUnitPrice());
                itemPs.executeUpdate();
            }
            itemPs.close();

            conn.commit();
            conn.close();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { }
            try { if (conn != null) conn.close(); } catch (Exception ex) { }
            System.out.println("Could not add sale");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSale(Sale sale, ArrayList<SaleItem> items) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement deletePs = conn.prepareStatement("DELETE FROM SaleItem WHERE SID = ?");
            deletePs.setInt(1, sale.getSid());
            deletePs.executeUpdate();
            deletePs.close();

            String saleSql = "UPDATE Sale "
                    + "SET SaleDate = ?, SaleTime = ?, TotalAmount = ?, CID = ?, BID = ?, EID = ?, PayMethodID = ? "
                    + "WHERE SID = ?";

            PreparedStatement salePs = conn.prepareStatement(saleSql);
            salePs.setString(1, sale.getSaleDate());
            salePs.setString(2, sale.getSaleTime());
            salePs.setDouble(3, sale.getTotalAmount());
            salePs.setInt(4, sale.getCid());
            salePs.setInt(5, sale.getBid());
            salePs.setInt(6, sale.getEid());
            salePs.setInt(7, sale.getPayMethodID());
            salePs.setInt(8, sale.getSid());
            salePs.executeUpdate();
            salePs.close();

            String itemSql = "INSERT INTO SaleItem (SID, ProdID, Quantity, UnitPrice) "
                    + "VALUES (?, ?, ?, ?)";

            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            for (SaleItem item : items) {
                itemPs.setInt(1, sale.getSid());
                itemPs.setInt(2, item.getProdID());
                itemPs.setInt(3, item.getQuantity());
                itemPs.setDouble(4, item.getUnitPrice());
                itemPs.executeUpdate();
            }
            itemPs.close();

            conn.commit();
            conn.close();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { }
            try { if (conn != null) conn.close(); } catch (Exception ex) { }
            System.out.println("Couldn't update sale");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSale(int sid) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement deleteItems = conn.prepareStatement("DELETE FROM SaleItem WHERE SID = ?");
            deleteItems.setInt(1, sid);
            deleteItems.executeUpdate();
            deleteItems.close();

            PreparedStatement deleteSale = conn.prepareStatement("DELETE FROM Sale WHERE SID = ?");
            deleteSale.setInt(1, sid);
            int rows = deleteSale.executeUpdate();
            deleteSale.close();

            conn.commit();
            conn.close();
            return rows > 0;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { }
            try { if (conn != null) conn.close(); } catch (Exception ex) { }
            System.out.println("Could not delete sale");
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<SaleItem> getSaleItems(int sid) {
        ArrayList<SaleItem> items = new ArrayList<>();

        String sql = "SELECT si.SID, si.ProdID, si.Quantity, si.UnitPrice, p.ProdName "
                + "FROM SaleItem si "
                + "JOIN Product p ON si.ProdID = p.ProdID "
                + "WHERE si.SID = ? "
                + "ORDER BY si.ProdID";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(readSaleItem(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) { //any database errors
            System.out.println("Could not load sale items");
            e.printStackTrace();
        }

        return items;
    }

    public SaleItem getProductForLine(int prodID, int quantity) {
        SaleItem item = null;

        String sql = "SELECT ProdName, Price FROM Product WHERE ProdID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, prodID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("ProdName");
                double price = rs.getDouble("Price");
                item = new SaleItem(0, prodID, quantity, price, name);
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

    public double getTotalRevenue() {
        double total = 0;

        String sql = "SELECT COALESCE(SUM(TotalAmount), 0) FROM Sale";

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
            System.out.println("Could not get total revenue");
            e.printStackTrace();
        }

        return total;
    }

    private Sale readSale(ResultSet rs) throws Exception {
        int sid = rs.getInt("SID");
        String saleDate = rs.getString("SaleDate");
        String saleTime = rs.getString("SaleTime");
        double totalAmount = rs.getDouble("TotalAmount");
        int cid = rs.getInt("CID");
        int bid = rs.getInt("BID");
        int eid = rs.getInt("EID");
        int payMethodID = rs.getInt("PayMethodID");
        String customerName = rs.getString("CustomerName");
        String branchName = rs.getString("BranchName");
        String employeeName = rs.getString("EmployeeName");
        String paymentMethodName = rs.getString("PaymentMethodName");

        return new Sale(sid, saleDate, saleTime, totalAmount, cid, bid, eid, payMethodID,
                customerName, branchName, employeeName, paymentMethodName);
    }

    private SaleItem readSaleItem(ResultSet rs) throws Exception {
        int sid = rs.getInt("SID");
        int prodID = rs.getInt("ProdID");
        int quantity = rs.getInt("Quantity");
        double unitPrice = rs.getDouble("UnitPrice");
        String prodName = rs.getString("ProdName");

        return new SaleItem(sid, prodID, quantity, unitPrice, prodName);
    }
}
