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

import Tables.PurchaseOrder;
import Tables.PurchaseItem;
import DAO.PurchaseOrderDAO;

public class PurchaseOrderFX extends Application {

    private PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();

    private TableView<PurchaseOrder> ordersTable = new TableView<>();
    private ObservableList<PurchaseOrder> ordersList = FXCollections.observableArrayList();

    private TableView<PurchaseItem> itemsTable = new TableView<>();
    private ObservableList<PurchaseItem> itemsList = FXCollections.observableArrayList();

    private TextField idField       = new TextField();
    private TextField dateField     = new TextField();
    private TextField timeField     = new TextField();
    private TextField suppIdField   = new TextField();
    private TextField bidField      = new TextField();
    private TextField eidField      = new TextField();
    private TextField searchField   = new TextField();

    private TextField prodIdField   = new TextField();
    private TextField quantityField = new TextField();
    private TextField costField     = new TextField();

    private Label totalLabel      = new Label("0");
    private Label amountLabel     = new Label("0.00");
    private Label averageLabel    = new Label("0.00");
    private Label orderTotalLabel = new Label("Total: ₪0.00");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.APP_BG + ";");
        root.setTop(UIHelper.topBarWithRefresh(stage, "Purchase Order Management",
                "Create, view and manage purchase orders with line items", this::loadOrders));
        root.setCenter(buildContent());

        loadOrders();

        Scene scene = new Scene(root, 1300, 820);
        stage.setTitle("GrandPharm — Purchase Orders");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(25, 30, 30, 30));

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
                UIHelper.statCard("Total Orders",         totalLabel),
                UIHelper.statCard("Total Purchases (₪)",  amountLabel),
                UIHelper.statCard("Average Order (₪)",    averageLabel)
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

        UIHelper.setupField(searchField, "Search by supplier name...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn  = new Button("Search");
        Button showAllBtn = new Button("Show All");
        searchBtn.setStyle(UIHelper.secondaryBtn());
        showAllBtn.setStyle(UIHelper.secondaryBtn());

        searchBtn.setOnAction(e -> searchOrders());
        showAllBtn.setOnAction(e -> { searchField.clear(); loadOrders(); });

        searchBox.getChildren().addAll(searchField, searchBtn, showAllBtn);

        buildOrderTableColumns();
        VBox.setVgrow(ordersTable, Priority.ALWAYS);

        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) fillFields(selected);
        });

        section.getChildren().addAll(searchBox, ordersTable);
        return section;
    }

    private void buildOrderTableColumns() {
        ordersTable.setItems(ordersList);
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ordersTable.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        TableColumn<PurchaseOrder, Number> idCol = new TableColumn<>("PurchID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPoid()));
        idCol.setMaxWidth(70);

        TableColumn<PurchaseOrder, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrderDate()));

        TableColumn<PurchaseOrder, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSupplierName()));

        TableColumn<PurchaseOrder, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBranchName()));

        TableColumn<PurchaseOrder, String> employeeCol = new TableColumn<>("Employee");
        employeeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmployeeName()));

        TableColumn<PurchaseOrder, Number> totalCol = new TableColumn<>("Total (₪)");
        totalCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalAmount()));

        ordersTable.getColumns().clear();
        ordersTable.getColumns().addAll(idCol, dateCol, supplierCol, branchCol, employeeCol, totalCol);
    }

    private VBox buildFormSection() {
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);

        UIHelper.setupField(idField,      "Order ID");
        UIHelper.setupField(dateField,    "YYYY-MM-DD");
        UIHelper.setupField(timeField,    "Optional / ignored by DB");
        UIHelper.setupField(suppIdField,  "Supplier ID");
        UIHelper.setupField(bidField,     "Branch ID");
        UIHelper.setupField(eidField,     "Employee ID");

        grid.add(UIHelper.fieldLabel("Order ID"),    0, 0);  grid.add(idField,     0, 1);
        grid.add(UIHelper.fieldLabel("Date"),        0, 2);  grid.add(dateField,   0, 3);
        grid.add(UIHelper.fieldLabel("Time"),        0, 4);  grid.add(timeField,   0, 5);
        grid.add(UIHelper.fieldLabel("Supplier ID"), 0, 6);  grid.add(suppIdField, 0, 7);
        grid.add(UIHelper.fieldLabel("Branch ID"),   0, 8);  grid.add(bidField,    0, 9);
        grid.add(UIHelper.fieldLabel("Employee ID"), 0, 10); grid.add(eidField,    0, 11);

        Label itemsTitle = new Label("Line Items");
        itemsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_DARK + "; -fx-padding: 6 0 0 0;");

        UIHelper.setupField(prodIdField,   "Product ID");
        UIHelper.setupField(quantityField, "Qty");
        UIHelper.setupField(costField,     "Unit Cost");

        Button addLineBtn = new Button("Add Line");
        addLineBtn.setStyle(UIHelper.secondaryBtn());
        addLineBtn.setOnAction(e -> addLineItem());

        HBox lineRow = new HBox(8, prodIdField, quantityField, costField, addLineBtn);
        HBox.setHgrow(prodIdField,   Priority.ALWAYS);
        HBox.setHgrow(quantityField, Priority.ALWAYS);
        HBox.setHgrow(costField,     Priority.ALWAYS);

        buildItemsTableColumns();
        itemsTable.setPrefHeight(140);

        Button removeLineBtn = new Button("Remove Selected Line");
        removeLineBtn.setStyle(UIHelper.lightBtn());
        removeLineBtn.setMaxWidth(Double.MAX_VALUE);
        removeLineBtn.setOnAction(e -> removeLineItem());

        orderTotalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.ACCENT + "; -fx-padding: 4 0 0 0;");

        Button addBtn    = UIHelper.fullWidthBtn("Add Purchase Order",    UIHelper.primaryBtn());
        Button updateBtn = UIHelper.fullWidthBtn("Update Purchase Order", UIHelper.secondaryBtn());
        Button deleteBtn = UIHelper.fullWidthBtn("Delete Purchase Order", UIHelper.dangerBtn());
        Button clearBtn  = UIHelper.fullWidthBtn("Clear Fields",          UIHelper.lightBtn());

        addBtn.setOnAction(e -> addOrder());
        updateBtn.setOnAction(e -> updateOrder());
        deleteBtn.setOnAction(e -> deleteOrder());
        clearBtn.setOnAction(e -> clearFields());

        Label note = new Label("Select an order to edit. Add lines before saving.");
        note.setWrapText(true);
        note.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_GRAY + ";");

        return UIHelper.formPanel("Purchase Order Details", 420,
            grid, itemsTitle, lineRow, itemsTable, removeLineBtn, orderTotalLabel,
            addBtn, updateBtn, deleteBtn, clearBtn, note);
    }

    private void buildItemsTableColumns() {
        itemsTable.setItems(itemsList);
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemsTable.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;");

        TableColumn<PurchaseItem, String> prodCol = new TableColumn<>("Product");
        prodCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProdName()));

        TableColumn<PurchaseItem, Number> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()));
        qtyCol.setMaxWidth(60);

        TableColumn<PurchaseItem, Number> costCol = new TableColumn<>("Unit Cost");
        costCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUnitCost()));

        TableColumn<PurchaseItem, Number> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getLineTotal()));

        itemsTable.getColumns().clear();
        itemsTable.getColumns().addAll(prodCol, qtyCol, costCol, totalCol);
    }

    private void addLineItem() {
        try {
            int prodID   = Integer.parseInt(prodIdField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double cost  = Double.parseDouble(costField.getText().trim());

            if (quantity <= 0 || cost < 0) { UIHelper.showError("Quantity and cost must be valid."); return; }

            PurchaseItem item = purchaseOrderDAO.getProductForLine(prodID, quantity, cost);
            if (item == null) { UIHelper.showError("Product ID not found."); return; }

            itemsList.add(item);
            prodIdField.clear(); quantityField.clear(); costField.clear();
            updateOrderTotal();

        } catch (Exception e) {
            UIHelper.showError("Enter valid product ID, quantity and cost.");
        }
    }

    private void removeLineItem() {
        PurchaseItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) { itemsList.remove(selected); updateOrderTotal(); }
    }

    private void updateOrderTotal() {
        double total = 0;
        for (PurchaseItem item : itemsList) total += item.getLineTotal();
        orderTotalLabel.setText(String.format("Total: ₪%.2f", total));
    }

    private void loadOrders() {
        ordersList.setAll(purchaseOrderDAO.getAllPurchaseOrders());
        updateStats();
    }

    private void searchOrders() {
        String name = searchField.getText().trim();
        if (name.isEmpty()) { loadOrders(); return; }
        ordersList.setAll(purchaseOrderDAO.searchPurchaseOrders(name));
        updateStats();
    }

    private void addOrder() {
        try {
            if (itemsList.isEmpty()) { UIHelper.showError("Add at least one line item before saving."); return; }
            PurchaseOrder order = readFields();
            if (purchaseOrderDAO.addPurchaseOrder(order, new ArrayList<>(itemsList))) {
                UIHelper.showInfo("Purchase order added successfully.");
                clearFields(); loadOrders();
            } else {
                UIHelper.showError("Could not add purchase order. Check IDs.");
            }
        } catch (Exception e) { UIHelper.showError("Please fill in all fields correctly."); }
    }

    private void updateOrder() {
        try {
            if (itemsList.isEmpty()) { UIHelper.showError("An order must have at least one line item."); return; }
            PurchaseOrder order = readFields();
            if (purchaseOrderDAO.updatePurchaseOrder(order, new ArrayList<>(itemsList))) {
                UIHelper.showInfo("Purchase order updated successfully.");
                clearFields(); loadOrders();
            } else {
                UIHelper.showError("Could not update. Make sure order ID exists.");
            }
        } catch (Exception e) { UIHelper.showError("Please fill in all fields correctly."); }
    }

    private void deleteOrder() {
        try {
            int poid = Integer.parseInt(idField.getText().trim());
            if (purchaseOrderDAO.deletePurchaseOrder(poid)) {
                UIHelper.showInfo("Purchase order deleted successfully.");
                clearFields(); loadOrders();
            } else {
                UIHelper.showError("Could not delete purchase order.");
            }
        } catch (Exception e) { UIHelper.showError("Enter a valid order ID before deleting."); }
    }

    private PurchaseOrder readFields() throws Exception {
        int poid     = Integer.parseInt(idField.getText().trim());
        String date  = dateField.getText().trim();
        String time  = timeField.getText().trim();
        int suppID   = Integer.parseInt(suppIdField.getText().trim());
        int bid      = Integer.parseInt(bidField.getText().trim());
        int eid      = Integer.parseInt(eidField.getText().trim());

        if (date.isEmpty()) throw new Exception();

        double total = 0;
        for (PurchaseItem item : itemsList) total += item.getLineTotal();

        return new PurchaseOrder(poid, date, time, total, suppID, bid, eid, "", "", "");
    }

    private void fillFields(PurchaseOrder order) {
        idField.setText(String.valueOf(order.getPoid()));
        dateField.setText(order.getOrderDate());
        timeField.setText(order.getOrderTime());
        suppIdField.setText(String.valueOf(order.getSuppID()));
        bidField.setText(String.valueOf(order.getBid()));
        eidField.setText(String.valueOf(order.getEid()));

        itemsList.setAll(purchaseOrderDAO.getPurchaseItems(order.getPoid()));
        updateOrderTotal();
    }

    private void clearFields() {
        idField.clear(); dateField.clear(); timeField.clear();
        suppIdField.clear(); bidField.clear(); eidField.clear();
        prodIdField.clear(); quantityField.clear(); costField.clear();
        itemsList.clear();
        updateOrderTotal();
        ordersTable.getSelectionModel().clearSelection();
    }

    private void updateStats() {
        int total    = ordersList.size();
        double amount = purchaseOrderDAO.getTotalPurchases();
        totalLabel.setText(String.valueOf(total));
        amountLabel.setText(String.format("%.2f", amount));
        averageLabel.setText(String.format("%.2f", total > 0 ? amount / total : 0));
    }

    public static void main(String[] args) { launch(args); }
}
