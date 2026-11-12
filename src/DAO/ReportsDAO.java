package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Tables.ReportRow;

public class ReportsDAO {

    public double getTotalRevenue() {
        return runDoubleQuery("SELECT SUM(TotalAmount) FROM Sale");
    }

    public int getTotalSales() {
        return runIntQuery("SELECT COUNT(*) FROM Sale");
    }

    public int getTotalItemsSold() {
        return runIntQuery("SELECT SUM(Quantity) FROM SaleItem");
    }

    public String getTopBranchName() {
        return runStringQuery("SELECT b.Name FROM Branch b "
                + "JOIN Sale s ON s.BID = b.BID "
                + "GROUP BY b.BID, b.Name "
                + "ORDER BY SUM(s.TotalAmount) DESC LIMIT 1");
    }

    public ArrayList<ReportRow> getRevenueByBranch() {
        return runReportQuery("SELECT b.Name, SUM(s.TotalAmount) AS Revenue "
                + "FROM Branch b "
                + "JOIN Sale s ON s.BID = b.BID "
                + "GROUP BY b.BID, b.Name "
                + "ORDER BY Revenue DESC");
    }

    public ArrayList<ReportRow> getTopSellingProducts() {
        return runReportQuery("SELECT p.ProdName, SUM(si.Quantity) AS TotalSold "
                + "FROM SaleItem si "
                + "JOIN Product p ON si.ProdID = p.ProdID "
                + "GROUP BY p.ProdID, p.ProdName "
                + "HAVING TotalSold > 0 "
                + "ORDER BY TotalSold DESC");
    }

    public ArrayList<ReportRow> getSalesByPaymentMethod() {
        return runReportQuery("SELECT pm.MethodName, COUNT(*) AS NumSales "
                + "FROM Sale s "
                + "JOIN PaymentMethod pm ON s.PayMethodID = pm.PayMethodID "
                + "GROUP BY pm.PayMethodID, pm.MethodName");
    }

    public ArrayList<ReportRow> getSalesTrend() {
        return runReportQuery("SELECT SaleDate, SUM(TotalAmount) AS DailyRevenue "
                + "FROM Sale "
                + "GROUP BY SaleDate "
                + "ORDER BY SaleDate");
    }

    public ArrayList<ReportRow> getInventoryByCategory() {
        return runReportQuery("SELECT c.Name, SUM(i.QuantityAvailable) AS TotalQty "
                + "FROM Category c "
                + "JOIN Product p ON p.CatID = c.CatID "
                + "JOIN Inventory i ON i.ProdID = p.ProdID "
                + "GROUP BY c.CatID, c.Name "
                + "ORDER BY TotalQty DESC");
    }

    public ArrayList<ReportRow> getRevenueByEmployee() {
        return runReportQuery("SELECT e.Name, SUM(s.TotalAmount) AS Revenue "
                + "FROM Employee e "
                + "JOIN Sale s ON s.EID = e.EID "
                + "GROUP BY e.EID, e.Name "
                + "ORDER BY Revenue DESC");
    }

    public ArrayList<ReportRow> getPurchasesBySupplier() {
        return runReportQuery("SELECT sup.Name, SUM(po.TotalCost) AS TotalSpent "
                + "FROM Supplier sup "
                + "JOIN PurchaseOrder po ON po.SuppID = sup.SuppID "
                + "GROUP BY sup.SuppID, sup.Name "
                + "ORDER BY TotalSpent DESC");
    }

    public ArrayList<ReportRow> getPrescriptionsByMonth() {
        return runReportQuery("SELECT DATE_FORMAT(PrescriptionDate, '%Y-%m') AS Month, COUNT(*) AS Total "
                + "FROM Prescription "
                + "GROUP BY Month "
                + "ORDER BY Month");
    }

    public ArrayList<ReportRow> getSalesByDateRange(String from, String to) {
        ArrayList<ReportRow> rows = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT SaleDate, SUM(TotalAmount) AS DailyRevenue "
                    + "FROM Sale "
                    + "WHERE SaleDate BETWEEN ? AND ? "
                    + "GROUP BY SaleDate "
                    + "ORDER BY SaleDate";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, from);
            ps.setString(2, to);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rows.add(new ReportRow(rs.getString(1), rs.getDouble(2)));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    private ArrayList<ReportRow> runReportQuery(String sql) {
        ArrayList<ReportRow> rows = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rows.add(new ReportRow(rs.getString(1), rs.getDouble(2)));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    private int runIntQuery(String sql) {
        int result = 0;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) result = rs.getInt(1);

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private double runDoubleQuery(String sql) {
        double result = 0;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) result = rs.getDouble(1);

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String runStringQuery(String sql) {
        String result = null;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) result = rs.getString(1);

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
