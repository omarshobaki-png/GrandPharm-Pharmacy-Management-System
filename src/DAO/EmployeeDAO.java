package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Tables.Employee;

public class EmployeeDAO {

    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();

        String sql = "SELECT e.EID, e.Name, e.Role, e.Phone, e.HireDate, e.Salary, e.BID, "
                + "b.Name AS BranchName "
                + "FROM Employee e "
                + "JOIN Branch b ON e.BID = b.BID "
                + "ORDER BY e.EID";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                employees.add(readEmployee(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Could not load employees");
            e.printStackTrace();
        }

        return employees;
    }

    public ArrayList<Employee> searchEmployees(String name) {
        ArrayList<Employee> employees = new ArrayList<>();

        String sql = "SELECT e.EID, e.Name, e.Role, e.Phone, e.HireDate, e.Salary, e.BID, "
                + "b.Name AS BranchName "
                + "FROM Employee e "
                + "JOIN Branch b ON e.BID = b.BID "
                + "WHERE e.Name LIKE ? "
                + "ORDER BY e.EID";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            // Allows searching by part of the name
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                employees.add(readEmployee(rs));
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) { //any database errors
            System.out.println("Could not search employees");
            e.printStackTrace();
        }

        return employees;
    }

    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO Employee "
                + "(Name, Role, Phone, HireDate, Salary, BID) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try { // in case anything fails within the DataBase
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, employee.getName());
            ps.setString(2, employee.getRole());
            ps.setString(3, employee.getPhone());
            ps.setString(4, employee.getHireDate());
            ps.setDouble(5, employee.getSalary());
            ps.setInt(6, employee.getBid());

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Could not add employee");
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE Employee "
                + "SET Name = ?, Role = ?, Phone = ?, HireDate = ?, Salary = ?, BID = ? "
                + "WHERE EID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, employee.getName());
            ps.setString(2, employee.getRole());
            ps.setString(3, employee.getPhone());
            ps.setString(4, employee.getHireDate());
            ps.setDouble(5, employee.getSalary());
            ps.setInt(6, employee.getBid());
            ps.setInt(7, employee.getEid());

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Couldn't update employee");
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteEmployee(int eid) {
        String sql = "DELETE FROM Employee WHERE EID = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, eid);

            int rows = ps.executeUpdate();

            ps.close();
            conn.close();

            return rows > 0;

        } catch (Exception e) {
            System.out.println("Could not delete employee");
            e.printStackTrace();
        }

        return false;
    }

    private Employee readEmployee(ResultSet rs) throws Exception {
        int eid = rs.getInt("EID");
        String name = rs.getString("Name");
        String role = rs.getString("Role");
        String phone = rs.getString("Phone");
        String hireDate = rs.getString("HireDate");
        double salary = rs.getDouble("Salary");
        int bid = rs.getInt("BID");
        String branchName = rs.getString("BranchName");

        return new Employee(eid, name, role, phone, hireDate, salary, bid, branchName);
    }
}