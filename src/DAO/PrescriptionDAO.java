package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Tables.Prescription;
import Tables.PrescriptionItem;

public class PrescriptionDAO {

    public ArrayList<Prescription> getAllPrescriptions() {
        ArrayList<Prescription> prescriptions = new ArrayList<>();

        String sql = "SELECT pr.PrescID, pr.PrescriptionDate, '' AS PrescriptionTime, "
                + "pr.Doc_Name AS DoctorName, "
                + "COALESCE(SUM(p.Price), 0) AS TotalAmount, "
                + "pr.CID, pr.BID, pr.EID, "
                + "c.Name AS CustomerName, b.Name AS BranchName, e.Name AS EmployeeName "
                + "FROM Prescription pr "
                + "JOIN Customer c ON pr.CID = c.CID "
                + "JOIN Branch b ON pr.BID = b.BID "
                + "JOIN Employee e ON pr.EID = e.EID "
                + "LEFT JOIN PrescriptionItem pi ON pr.PrescID = pi.PrescID "
                + "LEFT JOIN Product p ON pi.ProdID = p.ProdID "
                + "GROUP BY pr.PrescID, pr.PrescriptionDate, pr.Doc_Name, pr.CID, pr.BID, pr.EID, c.Name, b.Name, e.Name "
                + "ORDER BY pr.PrescID ASC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                prescriptions.add(readPrescription(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not load prescriptions");
            e.printStackTrace();
        }

        return prescriptions;
    }

    public ArrayList<Prescription> searchPrescriptions(String customerName) {
        ArrayList<Prescription> prescriptions = new ArrayList<>();

        String sql = "SELECT pr.PrescID, pr.PrescriptionDate, '' AS PrescriptionTime, "
                + "pr.Doc_Name AS DoctorName, "
                + "COALESCE(SUM(p.Price), 0) AS TotalAmount, "
                + "pr.CID, pr.BID, pr.EID, "
                + "c.Name AS CustomerName, b.Name AS BranchName, e.Name AS EmployeeName "
                + "FROM Prescription pr "
                + "JOIN Customer c ON pr.CID = c.CID "
                + "JOIN Branch b ON pr.BID = b.BID "
                + "JOIN Employee e ON pr.EID = e.EID "
                + "LEFT JOIN PrescriptionItem pi ON pr.PrescID = pi.PrescID "
                + "LEFT JOIN Product p ON pi.ProdID = p.ProdID "
                + "WHERE c.Name LIKE ? "
                + "GROUP BY pr.PrescID, pr.PrescriptionDate, pr.Doc_Name, pr.CID, pr.BID, pr.EID, c.Name, b.Name, e.Name "
                + "ORDER BY pr.PrescID ASC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            // Allows searching by part of the name
            ps.setString(1, "%" + customerName + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                prescriptions.add(readPrescription(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) { //any database errors
            System.out.println("Could not search prescriptions");
            e.printStackTrace();
        }

        return prescriptions;
    }

    public boolean addPrescription(Prescription prescription, ArrayList<PrescriptionItem> items) {
        Connection conn = null;

        try { // in case anything fails with the Database
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String prescSql = "INSERT INTO Prescription "
                    + "(PrescID, PrescriptionDate, Doc_Name, CID, BID, EID) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement prescPs = conn.prepareStatement(prescSql);
            prescPs.setInt(1, prescription.getPrescID());
            prescPs.setString(2, prescription.getPrescriptionDate());
            prescPs.setString(3, prescription.getDoctorName());
            prescPs.setInt(4, prescription.getCid());
            prescPs.setInt(5, prescription.getBid());
            prescPs.setInt(6, prescription.getEid());
            prescPs.executeUpdate();
            prescPs.close();

            String itemSql = "INSERT INTO PrescriptionItem "
                    + "(PrescID, ProdID, Dosage, Duration, Instructions) "
                    + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            for (PrescriptionItem item : items) {
                itemPs.setInt(1, prescription.getPrescID());
                itemPs.setInt(2, item.getProdID());
                itemPs.setString(3, item.getDosage());
                itemPs.setString(4, "");
                itemPs.setString(5, "");
                itemPs.executeUpdate();
            }
            itemPs.close();

            conn.commit();
            conn.close();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { }
            try { if (conn != null) conn.close(); } catch (Exception ex) { }
            System.out.println("Could not add prescription");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePrescription(Prescription prescription, ArrayList<PrescriptionItem> items) {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement deletePs = conn.prepareStatement("DELETE FROM PrescriptionItem WHERE PrescID = ?");
            deletePs.setInt(1, prescription.getPrescID());
            deletePs.executeUpdate();
            deletePs.close();

            String prescSql = "UPDATE Prescription SET "
                    + "PrescriptionDate = ?, Doc_Name = ?, CID = ?, BID = ?, EID = ? "
                    + "WHERE PrescID = ?";

            PreparedStatement prescPs = conn.prepareStatement(prescSql);
            prescPs.setString(1, prescription.getPrescriptionDate());
            prescPs.setString(2, prescription.getDoctorName());
            prescPs.setInt(3, prescription.getCid());
            prescPs.setInt(4, prescription.getBid());
            prescPs.setInt(5, prescription.getEid());
            prescPs.setInt(6, prescription.getPrescID());
            prescPs.executeUpdate();
            prescPs.close();

            String itemSql = "INSERT INTO PrescriptionItem "
                    + "(PrescID, ProdID, Dosage, Duration, Instructions) "
                    + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            for (PrescriptionItem item : items) {
                itemPs.setInt(1, prescription.getPrescID());
                itemPs.setInt(2, item.getProdID());
                itemPs.setString(3, item.getDosage());
                itemPs.setString(4, "");
                itemPs.setString(5, "");
                itemPs.executeUpdate();
            }
            itemPs.close();

            conn.commit();
            conn.close();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { }
            try { if (conn != null) conn.close(); } catch (Exception ex) { }
            System.out.println("Couldn't update prescription");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePrescription(int prescID) {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement deleteItems = conn.prepareStatement("DELETE FROM PrescriptionItem WHERE PrescID = ?");
            deleteItems.setInt(1, prescID);
            deleteItems.executeUpdate();
            deleteItems.close();

            PreparedStatement deletePresc = conn.prepareStatement("DELETE FROM Prescription WHERE PrescID = ?");
            deletePresc.setInt(1, prescID);
            int rows = deletePresc.executeUpdate();
            deletePresc.close();

            conn.commit();
            conn.close();
            return rows > 0;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { }
            try { if (conn != null) conn.close(); } catch (Exception ex) { }
            System.out.println("Could not delete prescription");
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<PrescriptionItem> getPrescriptionItems(int prescID) {
        ArrayList<PrescriptionItem> items = new ArrayList<>();

        String sql = "SELECT pi.PrescID, pi.ProdID, 1 AS Quantity, p.Price AS UnitPrice, "
                + "pi.Dosage, p.ProdName "
                + "FROM PrescriptionItem pi "
                + "JOIN Product p ON pi.ProdID = p.ProdID "
                + "WHERE pi.PrescID = ? "
                + "ORDER BY pi.ProdID ASC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, prescID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(readPrescriptionItem(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) { //any database errors
            System.out.println("Could not load prescription items");
            e.printStackTrace();
        }

        return items;
    }

    public PrescriptionItem getProductForLine(int prodID, int quantity, String dosage) {
        PrescriptionItem item = null;

        String sql = "SELECT ProdName, Price FROM Product WHERE ProdID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, prodID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String prodName = rs.getString("ProdName");
                double price    = rs.getDouble("Price");
                item = new PrescriptionItem(0, prodID, quantity, price, dosage, prodName);
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

    public double getTotalPrescriptionAmount() {
        double total = 0;

        String sql = "SELECT COALESCE(SUM(p.Price), 0) "
                + "FROM PrescriptionItem pi "
                + "JOIN Product p ON pi.ProdID = p.ProdID";

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
            System.out.println("Could not get total prescription amount");
            e.printStackTrace();
        }

        return total;
    }

    private Prescription readPrescription(ResultSet rs) throws Exception {
        int prescID            = rs.getInt("PrescID");
        String prescriptionDate = rs.getString("PrescriptionDate");
        String prescriptionTime = rs.getString("PrescriptionTime");
        String doctorName      = rs.getString("DoctorName");
        double totalAmount     = rs.getDouble("TotalAmount");
        int cid                = rs.getInt("CID");
        int bid                = rs.getInt("BID");
        int eid                = rs.getInt("EID");
        String customerName    = rs.getString("CustomerName");
        String branchName      = rs.getString("BranchName");
        String employeeName    = rs.getString("EmployeeName");

        return new Prescription(prescID, prescriptionDate, prescriptionTime, doctorName,
                totalAmount, cid, bid, eid, customerName, branchName, employeeName);
    }

    private PrescriptionItem readPrescriptionItem(ResultSet rs) throws Exception {
        int prescID       = rs.getInt("PrescID");
        int prodID        = rs.getInt("ProdID");
        int quantity      = rs.getInt("Quantity");
        double unitPrice  = rs.getDouble("UnitPrice");
        String dosage     = rs.getString("Dosage");
        String prodName   = rs.getString("ProdName");

        return new PrescriptionItem(prescID, prodID, quantity, unitPrice, dosage, prodName);
    }
}
