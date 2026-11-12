package FX;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import Tables.Employee;
import DAO.EmployeeDAO;

public class EmployeeFX extends Application {

    private EmployeeDAO employeeDAO = new EmployeeDAO();

    private TableView<Employee> table = new TableView<>();
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    private TextField idField       = new TextField();
    private TextField nameField     = new TextField();
    private TextField roleField     = new TextField();
    private TextField phoneField    = new TextField();
    private TextField hireDateField = new TextField();
    private TextField salaryField   = new TextField();
    private TextField bidField      = new TextField();
    private TextField searchField   = new TextField();

    private Label totalLabel   = new Label("0");
    private Label managerLabel = new Label("0");
    private Label averageLabel = new Label("0.00");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.APP_BG + ";");
        root.setTop(UIHelper.topBarWithRefresh(stage, "Employee Management", "Add, update, delete and search employees", this::loadEmployees));
        root.setCenter(buildContent());

        loadEmployees();

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("GrandPharm — Employees");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(25, 30, 30, 30));

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
            UIHelper.statCard("Total Employees",    totalLabel),
            UIHelper.statCard("Managers",           managerLabel),
            UIHelper.statCard("Average Salary (₪)", averageLabel)
        );
        for (var node : statsRow.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);

        HBox body = new HBox(18);
        body.getChildren().addAll(buildTableSection(), buildFormSection());
        VBox.setVgrow(body, Priority.ALWAYS);

        content.getChildren().addAll(statsRow, body);
        return content;
    }

    private VBox buildTableSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(18));
        section.setStyle(UIHelper.card());
        HBox.setHgrow(section, Priority.ALWAYS);

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        UIHelper.setupField(searchField, "Search employee by name...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn  = new Button("Search");
        Button showAllBtn = new Button("Show All");
        searchBtn.setStyle(UIHelper.secondaryBtn());
        showAllBtn.setStyle(UIHelper.secondaryBtn());

        searchBtn.setOnAction(e -> searchEmployees());
        showAllBtn.setOnAction(e -> { searchField.clear(); loadEmployees(); });

        searchBox.getChildren().addAll(searchField, searchBtn, showAllBtn);

        buildTableColumns();
        VBox.setVgrow(table, Priority.ALWAYS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) fillFields(selected);
        });

        section.getChildren().addAll(searchBox, table);
        return section;
    }

    @SuppressWarnings("deprecation")
	private void buildTableColumns() {
        table.setItems(employeeList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        TableColumn<Employee, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getEid()));
        idCol.setMaxWidth(70);

        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Employee, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));

        TableColumn<Employee, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));

        TableColumn<Employee, String> hireDateCol = new TableColumn<>("Hire Date");
        hireDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHireDate()));

        TableColumn<Employee, Number> salaryCol = new TableColumn<>("Salary (₪)");
        salaryCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSalary()));

        TableColumn<Employee, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBranchName()));

        table.getColumns().clear();
        table.getColumns().addAll(idCol, nameCol, roleCol, phoneCol, hireDateCol, salaryCol, branchCol);
    }

    private VBox buildFormSection() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        UIHelper.setupField(idField,       "Employee ID");
        UIHelper.setupField(nameField,     "Employee Name");
        UIHelper.setupField(roleField,     "Role");
        UIHelper.setupField(phoneField,    "Phone");
        UIHelper.setupField(hireDateField, "YYYY-MM-DD");
        UIHelper.setupField(salaryField,   "Salary");
        UIHelper.setupField(bidField,      "Branch ID");

        grid.add(UIHelper.fieldLabel("Employee ID"),   0, 0);  grid.add(idField,       0, 1);
        grid.add(UIHelper.fieldLabel("Employee Name"), 0, 2);  grid.add(nameField,     0, 3);
        grid.add(UIHelper.fieldLabel("Role"),          0, 4);  grid.add(roleField,     0, 5);
        grid.add(UIHelper.fieldLabel("Phone"),         0, 6);  grid.add(phoneField,    0, 7);
        grid.add(UIHelper.fieldLabel("Hire Date"),     0, 8);  grid.add(hireDateField, 0, 9);
        grid.add(UIHelper.fieldLabel("Salary"),        0, 10); grid.add(salaryField,   0, 11);
        grid.add(UIHelper.fieldLabel("Branch ID"),     0, 12); grid.add(bidField,      0, 13);

        Button addBtn    = UIHelper.fullWidthBtn("Add Employee",    UIHelper.primaryBtn());
        Button updateBtn = UIHelper.fullWidthBtn("Update Employee", UIHelper.secondaryBtn());
        Button deleteBtn = UIHelper.fullWidthBtn("Delete Employee", UIHelper.dangerBtn());
        Button clearBtn  = UIHelper.fullWidthBtn("Clear Fields",    UIHelper.lightBtn());

        addBtn.setOnAction(e -> addEmployee());
        updateBtn.setOnAction(e -> updateEmployee());
        deleteBtn.setOnAction(e -> deleteEmployee());
        clearBtn.setOnAction(e -> clearFields());

        return UIHelper.formPanel("Employee Details", 340, grid, addBtn, updateBtn, deleteBtn, clearBtn);
    }

    private void loadEmployees() {
        ArrayList<Employee> employees = employeeDAO.getAllEmployees();
        employeeList.setAll(employees);
        updateStats();
    }

    private void searchEmployees() {
        String name = searchField.getText().trim();
        if (name.isEmpty()) { loadEmployees(); return; }
        employeeList.setAll(employeeDAO.searchEmployees(name));
        updateStats();
    }

    private void addEmployee() {
        try {
            Employee e = readFields();
            if (employeeDAO.addEmployee(e)) {
                UIHelper.showInfo("Employee added successfully.");
                clearFields(); loadEmployees();
            } else {
                UIHelper.showError("Could not add employee. Check if the ID already exists or the branch ID is valid.");
            }
        } catch (Exception ex) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void updateEmployee() {
        try {
            Employee e = readFields();
            if (employeeDAO.updateEmployee(e)) {
                UIHelper.showInfo("Employee updated successfully.");
                clearFields(); loadEmployees();
            } else {
                UIHelper.showError("Could not update. Make sure the employee ID exists.");
            }
        } catch (Exception ex) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void deleteEmployee() {
        try {
            int eid = Integer.parseInt(idField.getText().trim());
            if (employeeDAO.deleteEmployee(eid)) {
                UIHelper.showInfo("Employee deleted successfully.");
                clearFields(); loadEmployees();
            } else {
                UIHelper.showError("Could not delete. Employee may be linked to sales, purchases, or prescriptions.");
            }
        } catch (Exception ex) {
            UIHelper.showError("Enter a valid employee ID before deleting.");
        }
    }

    private Employee readFields() throws Exception {
        int eid         = idField.getText().trim().isEmpty() ? 0 : Integer.parseInt(idField.getText().trim());
        String name     = nameField.getText().trim();
        String role     = roleField.getText().trim();
        String phone    = phoneField.getText().trim();
        String hireDate = hireDateField.getText().trim();
        double salary   = Double.parseDouble(salaryField.getText().trim());
        int bid         = Integer.parseInt(bidField.getText().trim());

        if (name.isEmpty() || salary < 0) throw new Exception();
        return new Employee(eid, name, role, phone, hireDate, salary, bid, "");
    }

    private void fillFields(Employee e) {
        idField.setText(String.valueOf(e.getEid()));
        nameField.setText(e.getName());
        roleField.setText(e.getRole());
        phoneField.setText(e.getPhone());
        hireDateField.setText(e.getHireDate());
        salaryField.setText(String.valueOf(e.getSalary()));
        bidField.setText(String.valueOf(e.getBid()));
    }

    private void clearFields() {
        idField.clear(); nameField.clear();
        roleField.clear(); phoneField.clear();
        hireDateField.clear(); salaryField.clear();
        bidField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void updateStats() {
        int total = employeeList.size();
        int managerCount = 0;
        double totalSalary = 0;
        for (Employee e : employeeList) {
            if (e.getRole() != null && e.getRole().equalsIgnoreCase("Manager")) managerCount++;
            totalSalary += e.getSalary();
        }
        totalLabel.setText(String.valueOf(total));
        managerLabel.setText(String.valueOf(managerCount));
        averageLabel.setText(String.format("%.2f", total > 0 ? totalSalary / total : 0));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
