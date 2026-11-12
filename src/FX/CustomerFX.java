package FX;

import java.util.ArrayList;

import javafx.application.Application;
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

import Tables.Customer;
import DAO.CustomerDAO;

public class CustomerFX extends Application {

    private CustomerDAO customerDAO = new CustomerDAO();

    private TableView<Customer> table = new TableView<>();
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    private TextField idField     = new TextField();
    private TextField nameField   = new TextField();
    private TextField phoneField  = new TextField();
    private TextField dobField    = new TextField();
    private TextField genderField = new TextField();
    private TextField searchField = new TextField();

    private Label totalLabel        = new Label("0");
    private Label salesLabel        = new Label("0");
    private Label prescriptionLabel = new Label("0");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.APP_BG + ";");
        root.setTop(UIHelper.topBarWithRefresh(stage, "Customer Management", "Add, update, delete and search customers", this::loadCustomers));
        root.setCenter(buildContent());

        loadCustomers();

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("GrandPharm — Customers");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(25, 30, 30, 30));

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
            UIHelper.statCard("Total Customers",      totalLabel),
            UIHelper.statCard("Total Sales",          salesLabel),
            UIHelper.statCard("Total Prescriptions",  prescriptionLabel)
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

        UIHelper.setupField(searchField, "Search customer by name...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn  = new Button("Search");
        Button showAllBtn = new Button("Show All");
        searchBtn.setStyle(UIHelper.secondaryBtn());
        showAllBtn.setStyle(UIHelper.secondaryBtn());

        searchBtn.setOnAction(e -> searchCustomers());
        showAllBtn.setOnAction(e -> { searchField.clear(); loadCustomers(); });

        searchBox.getChildren().addAll(searchField, searchBtn, showAllBtn);

        buildTableColumns();
        VBox.setVgrow(table, Priority.ALWAYS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) fillFields(selected);
        });

        section.getChildren().addAll(searchBox, table);
        return section;
    }

    private void buildTableColumns() {
        table.setItems(customerList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        TableColumn<Customer, Number> idCol = new TableColumn<>("CID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCid()));
        idCol.setMaxWidth(70);

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));

        TableColumn<Customer, String> dobCol = new TableColumn<>("DOB");
        dobCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDob()));

        TableColumn<Customer, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGender()));

        table.getColumns().clear();
        table.getColumns().addAll(idCol, nameCol, phoneCol, dobCol, genderCol);
    }

    private VBox buildFormSection() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        UIHelper.setupField(idField,     "Customer ID");
        UIHelper.setupField(nameField,   "Customer Name");
        UIHelper.setupField(phoneField,  "Phone");
        UIHelper.setupField(dobField,    "YYYY-MM-DD");
        UIHelper.setupField(genderField, "Gender");

        grid.add(UIHelper.fieldLabel("Customer ID"),   0, 0); grid.add(idField,     0, 1);
        grid.add(UIHelper.fieldLabel("Customer Name"), 0, 2); grid.add(nameField,   0, 3);
        grid.add(UIHelper.fieldLabel("Phone"),         0, 4); grid.add(phoneField,  0, 5);
        grid.add(UIHelper.fieldLabel("DOB"),           0, 6); grid.add(dobField,    0, 7);
        grid.add(UIHelper.fieldLabel("Gender"),        0, 8); grid.add(genderField, 0, 9);

        Button addBtn    = UIHelper.fullWidthBtn("Add Customer",    UIHelper.primaryBtn());
        Button updateBtn = UIHelper.fullWidthBtn("Update Customer", UIHelper.secondaryBtn());
        Button deleteBtn = UIHelper.fullWidthBtn("Delete Customer", UIHelper.dangerBtn());
        Button clearBtn  = UIHelper.fullWidthBtn("Clear Fields",    UIHelper.lightBtn());

        addBtn.setOnAction(e -> addCustomer());
        updateBtn.setOnAction(e -> updateCustomer());
        deleteBtn.setOnAction(e -> deleteCustomer());
        clearBtn.setOnAction(e -> clearFields());

        Label note = new Label("Select a row from the table before updating or deleting.");
        note.setWrapText(true);
        note.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_GRAY + ";");

        return UIHelper.formPanel("Customer Details", 340, grid, addBtn, updateBtn, deleteBtn, clearBtn, note);
    }

    private void loadCustomers() {
        ArrayList<Customer> customers = customerDAO.getAllCustomers();
        customerList.setAll(customers);
        updateStats();
    }

    private void searchCustomers() {
        String name = searchField.getText().trim();
        if (name.isEmpty()) { loadCustomers(); return; }
        customerList.setAll(customerDAO.searchCustomers(name));
        updateStats();
    }

    private void addCustomer() {
        try {
            Customer c = readFields();
            if (customerDAO.addCustomer(c)) {
                UIHelper.showInfo("Customer added successfully.");
                clearFields(); loadCustomers();
            } else UIHelper.showError("Could not add customer. Check if the ID already exists.");
        } catch (Exception e) { UIHelper.showError("Please fill in all fields correctly."); }
    }

    private void updateCustomer() {
        try {
            Customer c = readFields();
            if (customerDAO.updateCustomer(c)) {
                UIHelper.showInfo("Customer updated successfully.");
                clearFields(); loadCustomers();
            } else UIHelper.showError("Could not update. Make sure the customer ID exists.");
        } catch (Exception e) { UIHelper.showError("Please fill in all fields correctly."); }
    }

    private void deleteCustomer() {
        try {
            int cid = Integer.parseInt(idField.getText().trim());
            if (customerDAO.deleteCustomer(cid)) {
                UIHelper.showInfo("Customer deleted successfully.");
                clearFields(); loadCustomers();
            } else UIHelper.showError("Could not delete. Customer may be linked to sales or prescriptions.");
        } catch (Exception e) { UIHelper.showError("Enter a valid customer ID before deleting."); }
    }

    private Customer readFields() throws Exception {
        int cid       = Integer.parseInt(idField.getText().trim());
        String name   = nameField.getText().trim();
        String phone  = phoneField.getText().trim();
        String dob    = dobField.getText().trim();
        String gender = genderField.getText().trim();
        if (name.isEmpty()) throw new Exception();
        return new Customer(cid, name, phone, dob, gender);
    }

    private void fillFields(Customer c) {
        idField.setText(String.valueOf(c.getCid()));
        nameField.setText(c.getName());
        phoneField.setText(c.getPhone());
        dobField.setText(c.getDob());
        genderField.setText(c.getGender());
    }

    private void clearFields() {
        idField.clear(); nameField.clear(); phoneField.clear();
        dobField.clear(); genderField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void updateStats() {
        totalLabel.setText(String.valueOf(customerList.size()));
        salesLabel.setText(String.valueOf(customerDAO.getTotalSales()));
        prescriptionLabel.setText(String.valueOf(customerDAO.getTotalPrescriptions()));
    }

    public static void main(String[] args) { launch(args); }
}
