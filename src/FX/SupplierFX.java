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

import Tables.Supplier;
import DAO.SupplierDAO;

public class SupplierFX extends Application {

    private SupplierDAO supplierDAO = new SupplierDAO();

    private TableView<Supplier> table = new TableView<>();
    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    private TextField idField      = new TextField();
    private TextField nameField    = new TextField();
    private TextField phoneField   = new TextField();
    private TextField emailField   = new TextField();
    private TextField addressField = new TextField();
    private TextField searchField  = new TextField();

    private Label totalLabel          = new Label("0");
    private Label productsLabel       = new Label("0");
    private Label purchaseOrdersLabel = new Label("0");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.APP_BG + ";");
        root.setTop(UIHelper.topBarWithRefresh(stage, "Supplier Management", "Add, update, delete and search suppliers", this::loadSuppliers));
        root.setCenter(buildContent());

        loadSuppliers();

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("GrandPharm — Suppliers");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(25, 30, 30, 30));

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
            UIHelper.statCard("Total Suppliers",         totalLabel),
            UIHelper.statCard("Total Products Supplied", productsLabel),
            UIHelper.statCard("Total Purchase Orders",   purchaseOrdersLabel)
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

        UIHelper.setupField(searchField, "Search supplier by name...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn  = new Button("Search");
        Button showAllBtn = new Button("Show All");
        searchBtn.setStyle(UIHelper.secondaryBtn());
        showAllBtn.setStyle(UIHelper.secondaryBtn());

        searchBtn.setOnAction(e -> searchSuppliers());
        showAllBtn.setOnAction(e -> { searchField.clear(); loadSuppliers(); });

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
        table.setItems(supplierList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        TableColumn<Supplier, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSuppID()));
        idCol.setMaxWidth(70);

        TableColumn<Supplier, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));

        TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        TableColumn<Supplier, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress()));

        table.getColumns().clear();
        table.getColumns().addAll(idCol, nameCol, phoneCol, emailCol, addressCol);
    }

    private VBox buildFormSection() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        UIHelper.setupField(idField,      "Supplier ID");
        UIHelper.setupField(nameField,    "Supplier Name");
        UIHelper.setupField(phoneField,   "Phone");
        UIHelper.setupField(emailField,   "Email");
        UIHelper.setupField(addressField, "Address");

        grid.add(UIHelper.fieldLabel("Supplier ID"),   0, 0); grid.add(idField,      0, 1);
        grid.add(UIHelper.fieldLabel("Supplier Name"), 0, 2); grid.add(nameField,    0, 3);
        grid.add(UIHelper.fieldLabel("Phone"),         0, 4); grid.add(phoneField,   0, 5);
        grid.add(UIHelper.fieldLabel("Email"),         0, 6); grid.add(emailField,   0, 7);
        grid.add(UIHelper.fieldLabel("Address"),       0, 8); grid.add(addressField, 0, 9);

        Button addBtn    = UIHelper.fullWidthBtn("Add Supplier",    UIHelper.primaryBtn());
        Button updateBtn = UIHelper.fullWidthBtn("Update Supplier", UIHelper.secondaryBtn());
        Button deleteBtn = UIHelper.fullWidthBtn("Delete Supplier", UIHelper.dangerBtn());
        Button clearBtn  = UIHelper.fullWidthBtn("Clear Fields",    UIHelper.lightBtn());

        addBtn.setOnAction(e -> addSupplier());
        updateBtn.setOnAction(e -> updateSupplier());
        deleteBtn.setOnAction(e -> deleteSupplier());
        clearBtn.setOnAction(e -> clearFields());

        return UIHelper.formPanel("Supplier Details", 340, grid, addBtn, updateBtn, deleteBtn, clearBtn);
    }

    private void loadSuppliers() {
        ArrayList<Supplier> suppliers = supplierDAO.getAllSuppliers();
        supplierList.setAll(suppliers);
        updateStats();
    }

    private void searchSuppliers() {
        String name = searchField.getText().trim();
        if (name.isEmpty()) { loadSuppliers(); return; }
        supplierList.setAll(supplierDAO.searchSuppliers(name));
        updateStats();
    }

    private void addSupplier() {
        try {
            Supplier s = readFields();
            if (supplierDAO.addSupplier(s)) {
                UIHelper.showInfo("Supplier added successfully.");
                clearFields(); loadSuppliers();
            } else {
                UIHelper.showError("Could not add supplier. Check if the ID already exists.");
            }
        } catch (Exception e) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void updateSupplier() {
        try {
            Supplier s = readFields();
            if (supplierDAO.updateSupplier(s)) {
                UIHelper.showInfo("Supplier updated successfully.");
                clearFields(); loadSuppliers();
            } else {
                UIHelper.showError("Could not update. Make sure the supplier ID exists.");
            }
        } catch (Exception e) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void deleteSupplier() {
        try {
            int suppID = Integer.parseInt(idField.getText().trim());
            if (supplierDAO.deleteSupplier(suppID)) {
                UIHelper.showInfo("Supplier deleted successfully.");
                clearFields(); loadSuppliers();
            } else {
                UIHelper.showError("Could not delete. Supplier may be linked to purchase orders or supplier products.");
            }
        } catch (Exception e) {
            UIHelper.showError("Enter a valid supplier ID before deleting.");
        }
    }

    private Supplier readFields() throws Exception {
        int suppID     = idField.getText().trim().isEmpty() ? 0 : Integer.parseInt(idField.getText().trim());
        String name    = nameField.getText().trim();
        String phone   = phoneField.getText().trim();
        String email   = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty()) throw new Exception();
        return new Supplier(suppID, name, phone, email, address);
    }

    private void fillFields(Supplier s) {
        idField.setText(String.valueOf(s.getSuppID()));
        nameField.setText(s.getName());
        phoneField.setText(s.getPhone());
        emailField.setText(s.getEmail());
        addressField.setText(s.getAddress());
    }

    private void clearFields() {
        idField.clear(); nameField.clear();
        phoneField.clear(); emailField.clear();
        addressField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void updateStats() {
        totalLabel.setText(String.valueOf(supplierList.size()));
        productsLabel.setText(String.valueOf(supplierDAO.getTotalProductsSupplied()));
        purchaseOrdersLabel.setText(String.valueOf(supplierDAO.getTotalPurchaseOrders()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
