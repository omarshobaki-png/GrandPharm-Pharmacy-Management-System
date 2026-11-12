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

import Tables.Batch;
import DAO.BatchDAO;

public class BatchFX extends Application {

    private BatchDAO batchDAO = new BatchDAO();

    private TableView<Batch> table = new TableView<>();
    private ObservableList<Batch> batchList = FXCollections.observableArrayList();

    private TextField wareIdField       = new TextField();
    private TextField bNumField         = new TextField();
    private TextField prodIdField       = new TextField();
    private TextField mfgDateField      = new TextField();
    private TextField expDateField      = new TextField();
    private TextField qtyReceivedField  = new TextField();
    private TextField qtyRemainingField = new TextField();
    private TextField searchField       = new TextField();

    private Label totalBatchesLabel   = new Label("0");
    private Label expiringLabel       = new Label("0");
    private Label totalQuantityLabel  = new Label("0");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.APP_BG + ";");
        root.setTop(UIHelper.topBarWithRefresh(stage, "Batch Management", "Track product batches, expiry dates and quantities", this::loadBatches));
        root.setCenter(buildContent());

        loadBatches();

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("GrandPharm — Batches");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(25, 30, 30, 30));

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
            UIHelper.statCard("Total Batches",      totalBatchesLabel),
            UIHelper.statCard("Expiring (90 days)", expiringLabel),
            UIHelper.statCard("Total Qty Remaining", totalQuantityLabel)
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

        UIHelper.setupField(searchField, "Search by product name, batch # or warehouse...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn  = new Button("Search");
        Button showAllBtn = new Button("Show All");
        searchBtn.setStyle(UIHelper.secondaryBtn());
        showAllBtn.setStyle(UIHelper.secondaryBtn());

        searchBtn.setOnAction(e -> searchBatches());
        showAllBtn.setOnAction(e -> { searchField.clear(); loadBatches(); });

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
        table.setItems(batchList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        TableColumn<Batch, Number> wareIdCol = new TableColumn<>("Ware ID");
        wareIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getWareID()));
        wareIdCol.setMaxWidth(80);

        TableColumn<Batch, Number> bNumCol = new TableColumn<>("Batch #");
        bNumCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getBNum()));
        bNumCol.setMaxWidth(80);

        TableColumn<Batch, Number> prodIdCol = new TableColumn<>("Prod ID");
        prodIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getProdID()));
        prodIdCol.setMaxWidth(80);

        TableColumn<Batch, String> prodNameCol = new TableColumn<>("Product");
        prodNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProdName()));

        TableColumn<Batch, String> mfgDateCol = new TableColumn<>("Mfg. Date");
        mfgDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getManufacturingDate()));

        TableColumn<Batch, String> expDateCol = new TableColumn<>("Exp. Date");
        expDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExpDate()));

        TableColumn<Batch, Number> qtyRecCol = new TableColumn<>("Received");
        qtyRecCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantityReceived()));

        TableColumn<Batch, Number> qtyRemCol = new TableColumn<>("Remaining");
        qtyRemCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantityRemaining()));

        table.getColumns().clear();
        table.getColumns().addAll(wareIdCol, bNumCol, prodIdCol, prodNameCol, mfgDateCol, expDateCol, qtyRecCol, qtyRemCol);
    }

    private VBox buildFormSection() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        UIHelper.setupField(wareIdField,       "Warehouse ID");
        UIHelper.setupField(bNumField,         "Batch Number");
        UIHelper.setupField(prodIdField,       "Product ID");
        UIHelper.setupField(mfgDateField,      "Mfg. Date (YYYY-MM-DD)");
        UIHelper.setupField(expDateField,      "Exp. Date (YYYY-MM-DD)");
        UIHelper.setupField(qtyReceivedField,  "Quantity Received");
        UIHelper.setupField(qtyRemainingField, "Quantity Remaining");

        grid.add(UIHelper.fieldLabel("Warehouse ID"),            0, 0);  grid.add(wareIdField,       0, 1);
        grid.add(UIHelper.fieldLabel("Batch Number"),            0, 2);  grid.add(bNumField,         0, 3);
        grid.add(UIHelper.fieldLabel("Product ID"),              0, 4);  grid.add(prodIdField,       0, 5);
        grid.add(UIHelper.fieldLabel("Mfg. Date (YYYY-MM-DD)"), 0, 6);  grid.add(mfgDateField,      0, 7);
        grid.add(UIHelper.fieldLabel("Exp. Date (YYYY-MM-DD)"), 0, 8);  grid.add(expDateField,      0, 9);
        grid.add(UIHelper.fieldLabel("Quantity Received"),       0, 10); grid.add(qtyReceivedField,  0, 11);
        grid.add(UIHelper.fieldLabel("Quantity Remaining"),      0, 12); grid.add(qtyRemainingField, 0, 13);

        Button addBtn    = UIHelper.fullWidthBtn("Add Batch",    UIHelper.primaryBtn());
        Button updateBtn = UIHelper.fullWidthBtn("Update Batch", UIHelper.secondaryBtn());
        Button deleteBtn = UIHelper.fullWidthBtn("Delete Batch", UIHelper.dangerBtn());
        Button clearBtn  = UIHelper.fullWidthBtn("Clear Fields", UIHelper.lightBtn());

        addBtn.setOnAction(e -> addBatch());
        updateBtn.setOnAction(e -> updateBatch());
        deleteBtn.setOnAction(e -> deleteBatch());
        clearBtn.setOnAction(e -> clearForm());

        return UIHelper.formPanel("Batch Details", 340, grid, addBtn, updateBtn, deleteBtn, clearBtn);
    }

    private void loadBatches() {
        batchList.setAll(batchDAO.getAllBatches());
        updateStats();
    }

    private void searchBatches() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) { loadBatches(); return; }
        batchList.setAll(batchDAO.searchBatches(keyword));
        updateStats();
    }

    private void addBatch() {
        try {
            Batch b = readForm();
            if (batchDAO.addBatch(b)) {
                UIHelper.showInfo("Batch added successfully.");
                clearForm(); loadBatches();
            } else {
                UIHelper.showError("Could not add batch. Check that Warehouse ID and Product ID exist.");
            }
        } catch (Exception e) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void updateBatch() {
        try {
            Batch b = readForm();
            if (batchDAO.updateBatch(b)) {
                UIHelper.showInfo("Batch updated successfully.");
                clearForm(); loadBatches();
            } else {
                UIHelper.showError("Could not update. Make sure Warehouse ID and Batch Number exist.");
            }
        } catch (Exception e) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void deleteBatch() {
        Batch selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { UIHelper.showError("Select a batch row first."); return; }

        if (batchDAO.deleteBatch(selected.getWareID(), selected.getBNum())) {
            UIHelper.showInfo("Batch deleted successfully.");
            clearForm(); loadBatches();
        } else {
            UIHelper.showError("Could not delete batch.");
        }
    }

    private Batch readForm() throws Exception {
        int wareID        = Integer.parseInt(wareIdField.getText().trim());
        int bNum          = Integer.parseInt(bNumField.getText().trim());
        int prodID        = Integer.parseInt(prodIdField.getText().trim());
        String mfgDate    = mfgDateField.getText().trim();
        String expDate    = expDateField.getText().trim();
        int qtyReceived   = Integer.parseInt(qtyReceivedField.getText().trim());
        int qtyRemaining  = Integer.parseInt(qtyRemainingField.getText().trim());

        if (mfgDate.isEmpty() || expDate.isEmpty()) throw new Exception();
        return new Batch(wareID, bNum, prodID, mfgDate, expDate, qtyReceived, qtyRemaining, "");
    }

    private void fillForm(Batch b) {
        wareIdField.setText(String.valueOf(b.getWareID()));
        bNumField.setText(String.valueOf(b.getBNum()));
        prodIdField.setText(String.valueOf(b.getProdID()));
        mfgDateField.setText(b.getManufacturingDate());
        expDateField.setText(b.getExpDate());
        qtyReceivedField.setText(String.valueOf(b.getQuantityReceived()));
        qtyRemainingField.setText(String.valueOf(b.getQuantityRemaining()));
    }

    private void clearForm() {
        wareIdField.clear(); bNumField.clear(); prodIdField.clear();
        mfgDateField.clear(); expDateField.clear();
        qtyReceivedField.clear(); qtyRemainingField.clear();
        searchField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void updateStats() {
        int total = batchList.size();
        int totalQty = 0;
        for (Batch b : batchList) totalQty += b.getQuantityRemaining();
        totalBatchesLabel.setText(String.valueOf(total));
        expiringLabel.setText(String.valueOf(batchDAO.getExpiringBatches()));
        totalQuantityLabel.setText(String.valueOf(totalQty));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
