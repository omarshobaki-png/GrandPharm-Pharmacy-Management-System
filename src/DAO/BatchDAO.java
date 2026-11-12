package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Tables.Batch;

public class BatchDAO {

    public ArrayList<Batch> getAllBatches() {
        ArrayList<Batch> batches = new ArrayList<>();

        String query =
            "SELECT b.WareID, b.BNum, b.ProdID, b.ManufacturingDate, b.ExpDate, " +
            "b.QuantityReceived, b.QuantityRemaining, p.ProdName " +
            "FROM Batch b " +
            "JOIN Product p ON b.ProdID = p.ProdID " +
            "ORDER BY b.WareID, b.BNum";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                batches.add(readBatch(result));
            }

            result.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Error loading batches");
            e.printStackTrace();
        }

        return batches;
    }

    public ArrayList<Batch> searchBatches(String keyword) {
        ArrayList<Batch> batches = new ArrayList<>();

        String query =
            "SELECT b.WareID, b.BNum, b.ProdID, b.ManufacturingDate, b.ExpDate, " +
            "b.QuantityReceived, b.QuantityRemaining, p.ProdName " +
            "FROM Batch b " +
            "JOIN Product p ON b.ProdID = p.ProdID " +
            "WHERE p.ProdName LIKE ? OR b.BNum LIKE ? OR b.WareID LIKE ? " +
            "ORDER BY b.WareID, b.BNum";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                batches.add(readBatch(result));
            }

            result.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Error searching batches");
            e.printStackTrace();
        }

        return batches;
    }

    public boolean addBatch(Batch batch) {
        String query =
            "INSERT INTO Batch " +
            "(WareID, BNum, ProdID, ManufacturingDate, ExpDate, QuantityReceived, QuantityRemaining) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, batch.getWareID());
            stmt.setInt(2, batch.getBNum());
            stmt.setInt(3, batch.getProdID());
            stmt.setString(4, batch.getManufacturingDate());
            stmt.setString(5, batch.getExpDate());
            stmt.setInt(6, batch.getQuantityReceived());
            stmt.setInt(7, batch.getQuantityRemaining());

            int rows = stmt.executeUpdate();

            stmt.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Error adding batch");
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateBatch(Batch batch) {
        String query =
            "UPDATE Batch SET " +
            "ProdID = ?, ManufacturingDate = ?, ExpDate = ?, QuantityReceived = ?, QuantityRemaining = ? " +
            "WHERE WareID = ? AND BNum = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, batch.getProdID());
            stmt.setString(2, batch.getManufacturingDate());
            stmt.setString(3, batch.getExpDate());
            stmt.setInt(4, batch.getQuantityReceived());
            stmt.setInt(5, batch.getQuantityRemaining());
            stmt.setInt(6, batch.getWareID());
            stmt.setInt(7, batch.getBNum());

            int rows = stmt.executeUpdate();

            stmt.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Error updating batch");
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteBatch(int wareID, int bNum) {
        String query = "DELETE FROM Batch WHERE WareID = ? AND BNum = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, wareID);
            stmt.setInt(2, bNum);

            int rows = stmt.executeUpdate();

            stmt.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Error deleting batch");
            e.printStackTrace();
        }

        return false;
    }

    public int getTotalQuantity() {
        return runCountQuery("SELECT COALESCE(SUM(QuantityRemaining), 0) FROM Batch");
    }

    public int getExpiringBatches() {
        return runCountQuery(
            "SELECT COUNT(*) FROM Batch " +
            "WHERE ExpDate <= DATE_ADD(CURDATE(), INTERVAL 90 DAY)"
        );
    }

    private int runCountQuery(String query) {
        int count = 0;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                count = result.getInt(1);
            }

            result.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Error running count query");
            e.printStackTrace();
        }

        return count;
    }

    private Batch readBatch(ResultSet result) throws Exception {
        return new Batch(
            result.getInt("WareID"),
            result.getInt("BNum"),
            result.getInt("ProdID"),
            result.getString("ManufacturingDate"),
            result.getString("ExpDate"),
            result.getInt("QuantityReceived"),
            result.getInt("QuantityRemaining"),
            result.getString("ProdName")
        );
    }
}