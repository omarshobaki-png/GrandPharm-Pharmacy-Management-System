package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConsoleTest {

    public static void main(String[] args) {

        System.out.println("=== testing queries ===\n");

        System.out.println("Q1 - total revenue from all sales:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT COALESCE(SUM(TotalAmount), 0) FROM Sale";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                System.out.println("  total: " + rs.getDouble(1) + " ILS");
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println();

        System.out.println("Q2 - revenue per branch:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT b.Name, COALESCE(SUM(s.TotalAmount), 0) AS Revenue "
                       + "FROM Branch b JOIN Sale s ON s.BID = b.BID "
                       + "GROUP BY b.BID, b.Name ORDER BY Revenue DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                System.out.println("  " + rs.getString(1) + " -> " + rs.getDouble(2) + " ILS");
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println();

        System.out.println("Q3 - top 5 products by units sold:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT p.ProdName, SUM(si.Quantity) AS TotalSold "
                       + "FROM SaleItem si JOIN Product p ON si.ProdID = p.ProdID "
                       + "GROUP BY p.ProdID, p.ProdName ORDER BY TotalSold DESC LIMIT 5";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next())
                System.out.println("  " + i++ + ". " + rs.getString(1) + " - " + rs.getInt(2) + " units");
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println();

        System.out.println("Q4 - sales count by payment method:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT pm.MethodName, COUNT(*) AS NumSales "
                       + "FROM Sale s JOIN PaymentMethod pm ON s.PayMethodID = pm.PayMethodID "
                       + "GROUP BY pm.PayMethodID, pm.MethodName ORDER BY NumSales DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                System.out.println("  " + rs.getString(1) + ": " + rs.getInt(2) + " sales");
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println();

        System.out.println("Q5 - employee with highest revenue:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT e.Name, COALESCE(SUM(s.TotalAmount), 0) AS Revenue "
                       + "FROM Employee e JOIN Sale s ON s.EID = e.EID "
                       + "GROUP BY e.EID, e.Name ORDER BY Revenue DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                System.out.println("  " + rs.getString(1) + " -> " + rs.getDouble(2) + " ILS");
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println();

        System.out.println("Q6 - daily revenue between 2024-01-01 and 2024-12-31:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT SaleDate, SUM(TotalAmount) AS DailyRevenue "
                       + "FROM Sale WHERE SaleDate BETWEEN ? AND ? "
                       + "GROUP BY SaleDate ORDER BY SaleDate";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "2024-01-01");
            ps.setString(2, "2024-12-31");
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                System.out.println("  " + rs.getString(1) + ": " + rs.getDouble(2) + " ILS");
                found = true;
            }
            if (!found) System.out.println("  no sales in this range");
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println();

        System.out.println("Q7 - products with less than 10 units in stock:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT p.ProdName, i.QuantityAvailable "
                       + "FROM Inventory i JOIN Product p ON i.ProdID = p.ProdID "
                       + "WHERE i.QuantityAvailable < 10 ORDER BY i.QuantityAvailable ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                System.out.println("  " + rs.getString(1) + " - only " + rs.getInt(2) + " left");
                found = true;
            }
            if (!found) System.out.println("  all good, nothing low");
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println();

        System.out.println("Q8 - batches expiring in the next 30 days:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT p.ProdName, b.ExpDate, b.QuantityRemaining "
                       + "FROM Batch b JOIN Product p ON b.ProdID = p.ProdID "
                       + "WHERE b.ExpDate BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) "
                       + "ORDER BY b.ExpDate ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                System.out.println("  " + rs.getString(1) + " expires " + rs.getString(2)
                        + " (" + rs.getInt(3) + " units left)");
                found = true;
            }
            if (!found) System.out.println("  nothing expiring soon");
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println();

        System.out.println("Q9 - top 5 customers by prescription count:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT c.Name, COUNT(*) AS PrescCount "
                       + "FROM Customer c JOIN Prescription pr ON pr.CID = c.CID "
                       + "GROUP BY c.CID, c.Name ORDER BY PrescCount DESC LIMIT 5";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next())
                System.out.println("  " + i++ + ". " + rs.getString(1) + " - " + rs.getInt(2) + " prescriptions");
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println();

        System.out.println("Q10 - total purchases per supplier:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT s.Name, COALESCE(SUM(po.TotalCost), 0) AS TotalSpent "
                       + "FROM Supplier s JOIN PurchaseOrder po ON po.SuppID = s.SuppID "
                       + "GROUP BY s.SuppID, s.Name ORDER BY TotalSpent DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                System.out.println("  " + rs.getString(1) + " -> " + rs.getDouble(2) + " ILS");
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println();

        System.out.println("Q11 - average sale amount per branch:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT b.Name, AVG(s.TotalAmount) AS AvgSale "
                       + "FROM Branch b JOIN Sale s ON s.BID = b.BID "
                       + "GROUP BY b.BID, b.Name ORDER BY AvgSale DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                System.out.printf("  %s - avg %.2f ILS%n", rs.getString(1), rs.getDouble(2));
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println();

        System.out.println("Q12 - prescriptions per month:");
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT DATE_FORMAT(PrescriptionDate, '%Y-%m') AS Month, COUNT(*) AS Total "
                       + "FROM Prescription GROUP BY Month ORDER BY Month";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                System.out.println("  " + rs.getString(1) + " -> " + rs.getInt(2) + " prescriptions");
                found = true;
            }
            if (!found) System.out.println("  no data yet");
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println("\n=== done ===");
    }
}
