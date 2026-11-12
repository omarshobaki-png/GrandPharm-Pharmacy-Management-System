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

import Tables.Sale;
import Tables.SaleItem;
import DAO.SaleDAO;

public class SaleFX extends Application {

    private SaleDAO saleDAO = new SaleDAO();

    // sales table on the left
    private TableView<Sale> salesTable = new TableView<>();
    private ObservableList<Sale> salesList = FXCollections.observableArrayList();

    // items table inside the form on the right
    private TableView<SaleItem> itemsTable = new TableView<>();
    private ObservableList<SaleItem> itemsList = FXCollections.observableArrayList();

    // form fields (all plain text)
    private TextField idField        = new TextField();
    private TextField dateField      = new TextField();
    private TextField timeField      = new TextField();
    private TextField cidField       = new TextField();
    private TextField bidField       = new TextField();
    private TextField eidField       = new TextField();
    private TextField payMethodField = new TextField();
    private TextField searchField    = new TextField();

    // line item controls
    private TextField prodIdField   = new TextField();
    private TextField quantityField = new TextField();

    // labels
    private Label totalLabel     = new Label("0");
    private Label revenueLabel   = new Label("0.00");
    private Label averageLabel   = new Label("0.00");
    private Label saleTotalLabel = new Label("Total: ₪0.00");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.APP_BG + ";");
        root.setTop(UIHelper.topBarWithRefresh(stage, "Sales Management", "Create, view and manage sales with line items", this::loadSales));
        root.setCenter(buildContent());

        loadSales();

        Scene scene = new Scene(root, 1300, 820);
        stage.setTitle("GrandPharm — Sales");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(25, 30, 30, 30));

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
            UIHelper.statCard("Total Sales",       totalLabel),
            UIHelper.statCard("Total Revenue (₪)", revenueLabel),
            UIHelper.statCard("Average Sale (₪)",  averageLabel)
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

        UIHelper.setupField(searchField, "Search by customer name...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn  = new Button("Search");
        Button showAllBtn = new Button("Show All");
        searchBtn.setStyle(UIHelper.secondaryBtn());
        showAllBtn.setStyle(UIHelper.secondaryBtn());

        searchBtn.setOnAction(e -> searchSales());
        showAllBtn.setOnAction(e -> { searchField.clear(); loadSales(); });

        searchBox.getChildren().addAll(searchField, searchBtn, showAllBtn);

        buildSalesTableColumns();
        VBox.setVgrow(salesTable, Priority.ALWAYS);

        salesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) fillFields(selected);
        });

        section.getChildren().addAll(searchBox, salesTable);
        return section;
    }

    private void buildSalesTableColumns() {
        salesTable.setItems(salesList);
        salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        salesTable.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        TableColumn<Sale, Number> idCol = new TableColumn<>("SID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSid()));
        idCol.setMaxWidth(60);

        TableColumn<Sale, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSaleDate()));

        TableColumn<Sale, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomerName()));

        TableColumn<Sale, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBranchName()));

        TableColumn<Sale, String> employeeCol = new TableColumn<>("Employee");
        employeeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmployeeName()));

        TableColumn<Sale, String> paymentCol = new TableColumn<>("Payment");
        paymentCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaymentMethodName()));

        TableColumn<Sale, Number> totalCol = new TableColumn<>("Total (₪)");
        totalCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalAmount()));

        salesTable.getColumns().clear();
        salesTable.getColumns().addAll(idCol, dateCol, customerCol, branchCol, employeeCol, paymentCol, totalCol);
    }

    private VBox buildFormSection() {
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);

        UIHelper.setupField(idField,        "Sale ID");
        UIHelper.setupField(dateField,      "YYYY-MM-DD");
        UIHelper.setupField(timeField,      "HH:MM:SS");
        UIHelper.setupField(cidField,       "Customer ID");
        UIHelper.setupField(bidField,       "Branch ID");
        UIHelper.setupField(eidField,       "Employee ID");
        UIHelper.setupField(payMethodField, "Payment Method ID");

        grid.add(UIHelper.fieldLabel("Sale ID"),           0, 0);  grid.add(idField,        0, 1);
        grid.add(UIHelper.fieldLabel("Date"),              0, 2);  grid.add(dateField,      0, 3);
        grid.add(UIHelper.fieldLabel("Time"),              0, 4);  grid.add(timeField,      0, 5);
        grid.add(UIHelper.fieldLabel("Customer ID"),       0, 6);  grid.add(cidField,       0, 7);
        grid.add(UIHelper.fieldLabel("Branch ID"),         0, 8);  grid.add(bidField,       0, 9);
        grid.add(UIHelper.fieldLabel("Employee ID"),       0, 10); grid.add(eidField,       0, 11);
        grid.add(UIHelper.fieldLabel("Payment Method ID"), 0, 12); grid.add(payMethodField, 0, 13);

        Label itemsTitle = new Label("Line Items");
        itemsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_DARK + "; -fx-padding: 6 0 0 0;");

        UIHelper.setupField(prodIdField,   "Product ID");
        UIHelper.setupField(quantityField, "Qty");

        Button addLineBtn = new Button("Add Line");
        addLineBtn.setStyle(UIHelper.secondaryBtn());
        addLineBtn.setOnAction(e -> addLineItem());

        HBox lineRow = new HBox(8, prodIdField, quantityField, addLineBtn);
        HBox.setHgrow(prodIdField,   Priority.ALWAYS);
        HBox.setHgrow(quantityField, Priority.ALWAYS);

        buildItemsTableColumns();
        itemsTable.setPrefHeight(140);

        Button removeLineBtn = new Button("Remove Selected Line");
        removeLineBtn.setStyle(UIHelper.lightBtn());
        removeLineBtn.setMaxWidth(Double.MAX_VALUE);
        removeLineBtn.setOnAction(e -> removeLineItem());

        saleTotalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.ACCENT + "; -fx-padding: 4 0 0 0;");

        Button addBtn    = UIHelper.fullWidthBtn("Add Sale",     UIHelper.primaryBtn());
        Button updateBtn = UIHelper.fullWidthBtn("Update Sale",  UIHelper.secondaryBtn());
        Button deleteBtn = UIHelper.fullWidthBtn("Delete Sale",  UIHelper.dangerBtn());
        Button clearBtn  = UIHelper.fullWidthBtn("Clear Fields", UIHelper.lightBtn());

        addBtn.setOnAction(e -> addSale());
        updateBtn.setOnAction(e -> updateSale());
        deleteBtn.setOnAction(e -> deleteSale());
        clearBtn.setOnAction(e -> clearFields());

        return UIHelper.formPanel("Sale Details", 380,
            grid, itemsTitle, lineRow, itemsTable, removeLineBtn, saleTotalLabel,
            addBtn, updateBtn, deleteBtn, clearBtn);
    }

    private void buildItemsTableColumns() {
        itemsTable.setItems(itemsList);
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemsTable.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;");

        TableColumn<SaleItem, String> prodCol = new TableColumn<>("Product");
        prodCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProdName()));

        TableColumn<SaleItem, Number> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()));
        qtyCol.setMaxWidth(60);

        TableColumn<SaleItem, Number> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUnitPrice()));

        TableColumn<SaleItem, Number> lineTotalCol = new TableColumn<>("Total");
        lineTotalCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getLineTotal()));

        itemsTable.getColumns().clear();
        itemsTable.getColumns().addAll(prodCol, qtyCol, priceCol, lineTotalCol);
    }

    private void addLineItem() {
        try {
            int prodID   = Integer.parseInt(prodIdField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (quantity <= 0) {
                UIHelper.showError("Quantity must be greater than 0.");
                return;
            }

            SaleItem item = saleDAO.getProductForLine(prodID, quantity);
            if (item == null) {
                UIHelper.showError("Product ID not found.");
                return;
            }

            itemsList.add(item);
            prodIdField.clear();
            quantityField.clear();
            updateSaleTotal();

        } catch (Exception e) {
            UIHelper.showError("Enter a valid product ID and quantity.");
        }
    }

    private void removeLineItem() {
        SaleItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            itemsList.remove(selected);
            updateSaleTotal();
        }
    }

    private void updateSaleTotal() {
        double total = 0;
        for (SaleItem item : itemsList) total += item.getLineTotal();
        saleTotalLabel.setText(String.format("Total: ₪%.2f", total));
    }

    private void loadSales() {
        ArrayList<Sale> sales = saleDAO.getAllSales();
        salesList.setAll(sales);
        updateStats();
    }

    private void searchSales() {
        String name = searchField.getText().trim();
        if (name.isEmpty()) { loadSales(); return; }
        salesList.setAll(saleDAO.searchSales(name));
        updateStats();
    }

    private void addSale() {
        try {
            if (itemsList.isEmpty()) {
                UIHelper.showError("Add at least one line item before saving.");
                return;
            }
            Sale sale = readFields();
            ArrayList<SaleItem> items = new ArrayList<SaleItem>(itemsList);

            if (saleDAO.addSale(sale, items)) {
                UIHelper.showInfo("Sale added successfully.");
                clearFields(); loadSales();
            } else {
                UIHelper.showError("Could not add sale. Check if the ID already exists and all IDs are valid.");
            }
        } catch (Exception e) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void updateSale() {
        try {
            if (itemsList.isEmpty()) {
                UIHelper.showError("A sale must have at least one line item.");
                return;
            }
            Sale sale = readFields();
            ArrayList<SaleItem> items = new ArrayList<SaleItem>(itemsList);

            if (saleDAO.updateSale(sale, items)) {
                UIHelper.showInfo("Sale updated successfully.");
                clearFields(); loadSales();
            } else {
                UIHelper.showError("Could not update. Make sure the sale ID exists.");
            }
        } catch (Exception e) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void deleteSale() {
        try {
            int sid = Integer.parseInt(idField.getText().trim());
            if (saleDAO.deleteSale(sid)) {
                UIHelper.showInfo("Sale deleted successfully.");
                clearFields(); loadSales();
            } else {
                UIHelper.showError("Could not delete. Sale may be linked to other records.");
            }
        } catch (Exception e) {
            UIHelper.showError("Enter a valid sale ID before deleting.");
        }
    }

    private Sale readFields() throws Exception {
        int sid         = idField.getText().trim().isEmpty() ? 0 : Integer.parseInt(idField.getText().trim());
        String date     = dateField.getText().trim();
        String time     = timeField.getText().trim();
        int cid         = Integer.parseInt(cidField.getText().trim());
        int bid         = Integer.parseInt(bidField.getText().trim());
        int eid         = Integer.parseInt(eidField.getText().trim());
        int payMethodID = Integer.parseInt(payMethodField.getText().trim());

        if (date.isEmpty() || time.isEmpty()) throw new Exception();

        double total = 0;
        for (SaleItem item : itemsList) total += item.getLineTotal();

        return new Sale(sid, date, time, total, cid, bid, eid, payMethodID, "", "", "", "");
    }

    private void fillFields(Sale sale) {
        idField.setText(String.valueOf(sale.getSid()));
        dateField.setText(sale.getSaleDate());
        timeField.setText(sale.getSaleTime());
        cidField.setText(String.valueOf(sale.getCid()));
        bidField.setText(String.valueOf(sale.getBid()));
        eidField.setText(String.valueOf(sale.getEid()));
        payMethodField.setText(String.valueOf(sale.getPayMethodID()));

        itemsList.setAll(saleDAO.getSaleItems(sale.getSid()));
        updateSaleTotal();
    }

    private void clearFields() {
        idField.clear(); dateField.clear(); timeField.clear();
        cidField.clear(); bidField.clear(); eidField.clear();
        payMethodField.clear(); prodIdField.clear(); quantityField.clear();
        itemsList.clear();
        updateSaleTotal();
        salesTable.getSelectionModel().clearSelection();
    }

    private void updateStats() {
        int total = salesList.size();
        double revenue = saleDAO.getTotalRevenue();
        double average = total > 0 ? revenue / total : 0;

        totalLabel.setText(String.valueOf(total));
        revenueLabel.setText(String.format("%.2f", revenue));
        averageLabel.setText(String.format("%.2f", average));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
