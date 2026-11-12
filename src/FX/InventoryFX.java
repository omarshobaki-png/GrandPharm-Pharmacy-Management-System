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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import Tables.Inventory;
import DAO.InventoryDAO;

public class InventoryFX extends Application {

    private InventoryDAO inventoryDAO = new InventoryDAO();

    private TableView<Inventory> table = new TableView<>();
    private ObservableList<Inventory> inventoryList = FXCollections.observableArrayList();

    private TextField wareIdField   = new TextField();
    private TextField prodIdField   = new TextField();
    private TextField quantityField = new TextField();
    private TextField minStockField = new TextField();
    private TextField dateField     = new TextField();
    private TextField searchField   = new TextField();

    private Label totalItemsLabel    = new Label("0");
    private Label lowStockLabel      = new Label("0");
    private Label totalQuantityLabel = new Label("0");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.APP_BG + ";");
        root.setTop(UIHelper.topBarWithRefresh(stage, "Inventory", "Track stock levels across all warehouses", this::loadInventory));
        root.setCenter(buildContent());

        loadInventory();

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("GrandPharm — Inventory");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(25, 30, 30, 30));

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
            UIHelper.statCard("Total Entries",   totalItemsLabel),
            UIHelper.statCard("Low Stock Items", lowStockLabel),
            UIHelper.statCard("Total Quantity",  totalQuantityLabel)
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

        UIHelper.setupField(searchField, "Search by product or warehouse name...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn  = new Button("Search");
        Button showAllBtn = new Button("Show All");
        searchBtn.setStyle(UIHelper.secondaryBtn());
        showAllBtn.setStyle(UIHelper.secondaryBtn());

        searchBtn.setOnAction(e -> searchInventory());
        showAllBtn.setOnAction(e -> { searchField.clear(); loadInventory(); });

        searchBox.getChildren().addAll(searchField, searchBtn, showAllBtn);

        buildTableColumns();
        VBox.setVgrow(table, Priority.ALWAYS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) fillForm(selected);
        });

        section.getChildren().addAll(searchBox, table);
        return section;
    }

    private void buildTableColumns() {
        table.setItems(inventoryList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        TableColumn<Inventory, Number> wareIdCol = new TableColumn<>("Ware ID");
        wareIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getWareID()));
        wareIdCol.setMaxWidth(80);

        TableColumn<Inventory, String> wareNameCol = new TableColumn<>("Warehouse");
        wareNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getWarehouseName()));

        TableColumn<Inventory, Number> prodIdCol = new TableColumn<>("Prod ID");
        prodIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getProdID()));
        prodIdCol.setMaxWidth(80);

        TableColumn<Inventory, String> prodNameCol = new TableColumn<>("Product");
        prodNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProductName()));

        TableColumn<Inventory, Number> qtyCol = new TableColumn<>("Available");
        qtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantityAvailable()));

        TableColumn<Inventory, Number> minCol = new TableColumn<>("Min Stock");
        minCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMinStock()));

        TableColumn<Inventory, String> dateCol = new TableColumn<>("Last Updated");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastUpdated()));

        // red text if quantity is at or below min stock
        qtyCol.setCellFactory(col -> new TableCell<Inventory, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null); setStyle("");
                } else {
                    setText(value.toString());
                    Inventory item = getTableView().getItems().get(getIndex());
                    setStyle(item.isLowStock() ? "-fx-text-fill: #C0392B; -fx-font-weight: bold;" : "");
                }
            }
        });

        table.getColumns().clear();
        table.getColumns().addAll(wareIdCol, wareNameCol, prodIdCol, prodNameCol, qtyCol, minCol, dateCol);
    }

    private VBox buildFormSection() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        UIHelper.setupField(wareIdField,   "Warehouse ID");
        UIHelper.setupField(prodIdField,   "Product ID");
        UIHelper.setupField(quantityField, "Quantity");
        UIHelper.setupField(minStockField, "Min Stock");
        UIHelper.setupField(dateField,     "Date (YYYY-MM-DD)");

        grid.add(UIHelper.fieldLabel("Warehouse ID"),      0, 0); grid.add(wareIdField,   0, 1);
        grid.add(UIHelper.fieldLabel("Product ID"),        0, 2); grid.add(prodIdField,   0, 3);
        grid.add(UIHelper.fieldLabel("Quantity"),          0, 4); grid.add(quantityField, 0, 5);
        grid.add(UIHelper.fieldLabel("Min Stock"),         0, 6); grid.add(minStockField, 0, 7);
        grid.add(UIHelper.fieldLabel("Date (YYYY-MM-DD)"), 0, 8); grid.add(dateField,     0, 9);

        Button addBtn    = UIHelper.fullWidthBtn("Add / Update Stock",    UIHelper.primaryBtn());
        Button updateBtn = UIHelper.fullWidthBtn("Set Quantity Directly", UIHelper.secondaryBtn());
        Button deleteBtn = UIHelper.fullWidthBtn("Delete Entry",          UIHelper.dangerBtn());
        Button clearBtn  = UIHelper.fullWidthBtn("Clear Fields",          UIHelper.lightBtn());

        addBtn.setOnAction(e -> addInventory());
        updateBtn.setOnAction(e -> updateInventory());
        deleteBtn.setOnAction(e -> deleteInventory());
        clearBtn.setOnAction(e -> clearForm());

        return UIHelper.formPanel("Inventory Details", 320, grid, addBtn, updateBtn, deleteBtn, clearBtn);
    }

    private void loadInventory() {
        inventoryList.setAll(inventoryDAO.getAllInventory());
        updateStats();
    }

    private void searchInventory() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) { loadInventory(); return; }
        inventoryList.setAll(inventoryDAO.searchInventory(keyword));
        updateStats();
    }

    private void addInventory() {
        try {
            Inventory inv = readForm();
            if (inventoryDAO.addInventory(inv)) {
                UIHelper.showInfo("Stock added successfully.");
                clearForm(); loadInventory();
            } else {
                UIHelper.showError("Could not add stock. Check that Warehouse ID and Product ID exist.");
            }
        } catch (Exception e) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void updateInventory() {
        try {
            Inventory inv = readForm();
            if (inventoryDAO.updateInventory(inv)) {
                UIHelper.showInfo("Inventory updated successfully.");
                clearForm(); loadInventory();
            } else {
                UIHelper.showError("Could not update. Make sure Warehouse ID and Product ID exist.");
            }
        } catch (Exception e) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void deleteInventory() {
        Inventory selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { UIHelper.showError("Select a row first."); return; }

        if (inventoryDAO.deleteInventory(selected.getWareID(), selected.getProdID())) {
            UIHelper.showInfo("Entry deleted successfully.");
            clearForm(); loadInventory();
        } else {
            UIHelper.showError("Could not delete entry.");
        }
    }

    private Inventory readForm() throws Exception {
        int wareID   = Integer.parseInt(wareIdField.getText().trim());
        int prodID   = Integer.parseInt(prodIdField.getText().trim());
        int quantity = Integer.parseInt(quantityField.getText().trim());
        int minStock = Integer.parseInt(minStockField.getText().trim());
        String date  = dateField.getText().trim();
        if (date.isEmpty()) throw new Exception();
        return new Inventory(wareID, "", prodID, "", quantity, minStock, date);
    }

    private void fillForm(Inventory item) {
        wareIdField.setText(String.valueOf(item.getWareID()));
        prodIdField.setText(String.valueOf(item.getProdID()));
        quantityField.setText(String.valueOf(item.getQuantityAvailable()));
        minStockField.setText(String.valueOf(item.getMinStock()));
        dateField.setText(item.getLastUpdated());
    }

    private void clearForm() {
        wareIdField.clear(); prodIdField.clear();
        quantityField.clear(); minStockField.clear();
        dateField.clear(); searchField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void updateStats() {
        int total = inventoryList.size(), lowStock = 0, totalQty = 0;
        for (Inventory item : inventoryList) {
            if (item.isLowStock()) lowStock++;
            totalQty += item.getQuantityAvailable();
        }
        totalItemsLabel.setText(String.valueOf(total));
        lowStockLabel.setText(String.valueOf(lowStock));
        totalQuantityLabel.setText(String.valueOf(totalQty));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
