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

import Tables.Prescription;
import Tables.PrescriptionItem;
import DAO.PrescriptionDAO;

public class PrescriptionFX extends Application {

    private PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    private TableView<Prescription> prescriptionsTable = new TableView<>();
    private ObservableList<Prescription> prescriptionsList = FXCollections.observableArrayList();

    private TableView<PrescriptionItem> itemsTable = new TableView<>();
    private ObservableList<PrescriptionItem> itemsList = FXCollections.observableArrayList();

    private TextField idField       = new TextField();
    private TextField dateField     = new TextField();
    private TextField timeField     = new TextField();
    private TextField doctorField   = new TextField();
    private TextField cidField      = new TextField();
    private TextField bidField      = new TextField();
    private TextField eidField      = new TextField();
    private TextField searchField   = new TextField();

    private TextField prodIdField   = new TextField();
    private TextField quantityField = new TextField();
    private TextField dosageField   = new TextField();

    private Label totalLabel              = new Label("0");
    private Label amountLabel             = new Label("0.00");
    private Label averageLabel            = new Label("0.00");
    private Label prescriptionTotalLabel  = new Label("Total: ₪0.00");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.APP_BG + ";");
        root.setTop(UIHelper.topBarWithRefresh(stage, "Prescription Management",
                "Create, view and manage prescriptions with line items", this::loadPrescriptions));
        root.setCenter(buildContent());

        loadPrescriptions();

        Scene scene = new Scene(root, 1300, 820);
        stage.setTitle("GrandPharm — Prescriptions");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(25, 30, 30, 30));

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
                UIHelper.statCard("Total Prescriptions",             totalLabel),
                UIHelper.statCard("Total Prescription Amount (₪)",   amountLabel),
                UIHelper.statCard("Average Prescription (₪)",        averageLabel)
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

        searchBtn.setOnAction(e -> searchPrescriptions());
        showAllBtn.setOnAction(e -> { searchField.clear(); loadPrescriptions(); });

        searchBox.getChildren().addAll(searchField, searchBtn, showAllBtn);

        buildOrderTableColumns();
        VBox.setVgrow(prescriptionsTable, Priority.ALWAYS);

        prescriptionsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) fillFields(selected);
        });

        section.getChildren().addAll(searchBox, prescriptionsTable);
        return section;
    }

    private void buildOrderTableColumns() {
        prescriptionsTable.setItems(prescriptionsList);
        prescriptionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        prescriptionsTable.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        TableColumn<Prescription, Number> idCol = new TableColumn<>("PrescID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPrescID()));
        idCol.setMaxWidth(65);

        TableColumn<Prescription, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrescriptionDate()));

        TableColumn<Prescription, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomerName()));

        TableColumn<Prescription, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBranchName()));

        TableColumn<Prescription, String> employeeCol = new TableColumn<>("Employee");
        employeeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmployeeName()));

        TableColumn<Prescription, Number> totalCol = new TableColumn<>("Total (₪)");
        totalCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalAmount()));

        prescriptionsTable.getColumns().clear();
        prescriptionsTable.getColumns().addAll(idCol, dateCol, customerCol, branchCol, employeeCol, totalCol);
    }

    private VBox buildFormSection() {
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);

        UIHelper.setupField(idField,     "Prescription ID");
        UIHelper.setupField(dateField,   "YYYY-MM-DD");
        UIHelper.setupField(timeField,   "HH:MM:SS");
        UIHelper.setupField(doctorField, "Doctor Name");
        UIHelper.setupField(cidField,    "Customer ID");
        UIHelper.setupField(bidField,    "Branch ID");
        UIHelper.setupField(eidField,    "Employee ID");

        grid.add(UIHelper.fieldLabel("Prescription ID"), 0, 0);  grid.add(idField,     0, 1);
        grid.add(UIHelper.fieldLabel("Date"),            0, 2);  grid.add(dateField,   0, 3);
        grid.add(UIHelper.fieldLabel("Time"),            0, 4);  grid.add(timeField,   0, 5);
        grid.add(UIHelper.fieldLabel("Doctor Name"),     0, 6);  grid.add(doctorField, 0, 7);
        grid.add(UIHelper.fieldLabel("Customer ID"),     0, 8);  grid.add(cidField,    0, 9);
        grid.add(UIHelper.fieldLabel("Branch ID"),       0, 10); grid.add(bidField,    0, 11);
        grid.add(UIHelper.fieldLabel("Employee ID"),     0, 12); grid.add(eidField,    0, 13);

        Label itemsTitle = new Label("Line Items");
        itemsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_DARK + ";");

        UIHelper.setupField(prodIdField,   "Product ID");
        UIHelper.setupField(quantityField, "Qty");
        UIHelper.setupField(dosageField,   "Dosage");

        Button addLineBtn = new Button("Add Line");
        addLineBtn.setStyle(UIHelper.secondaryBtn());
        addLineBtn.setOnAction(e -> addLineItem());

        HBox lineRow = new HBox(8, prodIdField, quantityField, dosageField, addLineBtn);
        HBox.setHgrow(prodIdField,   Priority.ALWAYS);
        HBox.setHgrow(quantityField, Priority.ALWAYS);
        HBox.setHgrow(dosageField,   Priority.ALWAYS);

        buildItemsTableColumns();
        itemsTable.setPrefHeight(140);

        Button removeLineBtn = new Button("Remove Selected Line");
        removeLineBtn.setStyle(UIHelper.lightBtn());
        removeLineBtn.setMaxWidth(Double.MAX_VALUE);
        removeLineBtn.setOnAction(e -> removeLineItem());

        prescriptionTotalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.ACCENT + ";");

        Button addBtn    = UIHelper.fullWidthBtn("Add Prescription",    UIHelper.primaryBtn());
        Button updateBtn = UIHelper.fullWidthBtn("Update Prescription", UIHelper.secondaryBtn());
        Button deleteBtn = UIHelper.fullWidthBtn("Delete Prescription", UIHelper.dangerBtn());
        Button clearBtn  = UIHelper.fullWidthBtn("Clear Fields",        UIHelper.lightBtn());

        addBtn.setOnAction(e -> addPrescription());
        updateBtn.setOnAction(e -> updatePrescription());
        deleteBtn.setOnAction(e -> deletePrescription());
        clearBtn.setOnAction(e -> clearFields());

        Label note = new Label("Select a prescription to edit. Add lines before saving.");
        note.setWrapText(true);
        note.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIHelper.TEXT_GRAY + ";");

        return UIHelper.formPanel("Prescription Details", 420,
            grid, itemsTitle, lineRow, itemsTable, removeLineBtn, prescriptionTotalLabel,
            addBtn, updateBtn, deleteBtn, clearBtn, note);
    }

    private void buildItemsTableColumns() {
        itemsTable.setItems(itemsList);
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemsTable.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;");

        TableColumn<PrescriptionItem, String> prodCol = new TableColumn<>("Product");
        prodCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProdName()));

        TableColumn<PrescriptionItem, Number> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()));
        qtyCol.setMaxWidth(60);

        TableColumn<PrescriptionItem, Number> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUnitPrice()));

        TableColumn<PrescriptionItem, String> dosageCol = new TableColumn<>("Dosage");
        dosageCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDosage()));

        TableColumn<PrescriptionItem, Number> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getLineTotal()));

        itemsTable.getColumns().clear();
        itemsTable.getColumns().addAll(prodCol, qtyCol, priceCol, dosageCol, totalCol);
    }

    private void addLineItem() {
        try {
            int prodID    = Integer.parseInt(prodIdField.getText().trim());
            int quantity  = Integer.parseInt(quantityField.getText().trim());
            String dosage = dosageField.getText().trim();

            if (quantity <= 0 || dosage.isEmpty()) {
                UIHelper.showError("Quantity and dosage must be valid.");
                return;
            }

            PrescriptionItem item = prescriptionDAO.getProductForLine(prodID, quantity, dosage);
            if (item == null) { UIHelper.showError("Product ID not found."); return; }

            itemsList.add(item);
            prodIdField.clear(); quantityField.clear(); dosageField.clear();
            updatePrescriptionTotal();

        } catch (Exception e) {
            UIHelper.showError("Enter valid product ID, quantity and dosage.");
        }
    }

    private void removeLineItem() {
        PrescriptionItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) { itemsList.remove(selected); updatePrescriptionTotal(); }
    }

    private void updatePrescriptionTotal() {
        double total = 0;
        for (PrescriptionItem item : itemsList) total += item.getLineTotal();
        prescriptionTotalLabel.setText(String.format("Total: ₪%.2f", total));
    }

    private void loadPrescriptions() {
        prescriptionsList.setAll(prescriptionDAO.getAllPrescriptions());
        updateStats();
    }

    private void searchPrescriptions() {
        String name = searchField.getText().trim();
        if (name.isEmpty()) { loadPrescriptions(); return; }
        prescriptionsList.setAll(prescriptionDAO.searchPrescriptions(name));
        updateStats();
    }

    private void addPrescription() {
        try {
            if (itemsList.isEmpty()) { UIHelper.showError("Add at least one line item before saving."); return; }
            Prescription prescription = readFields();
            if (prescriptionDAO.addPrescription(prescription, new ArrayList<>(itemsList))) {
                UIHelper.showInfo("Prescription added successfully.");
                clearFields(); loadPrescriptions();
            } else {
                UIHelper.showError("Could not add prescription. Check IDs.");
            }
        } catch (Exception e) { UIHelper.showError("Please fill in all fields correctly."); }
    }

    private void updatePrescription() {
        try {
            if (itemsList.isEmpty()) { UIHelper.showError("A prescription must have at least one line item."); return; }
            Prescription prescription = readFields();
            if (prescriptionDAO.updatePrescription(prescription, new ArrayList<>(itemsList))) {
                UIHelper.showInfo("Prescription updated successfully.");
                clearFields(); loadPrescriptions();
            } else {
                UIHelper.showError("Could not update. Make sure prescription ID exists.");
            }
        } catch (Exception e) { UIHelper.showError("Please fill in all fields correctly."); }
    }

    private void deletePrescription() {
        try {
            int prescID = Integer.parseInt(idField.getText().trim());
            if (prescriptionDAO.deletePrescription(prescID)) {
                UIHelper.showInfo("Prescription deleted successfully.");
                clearFields(); loadPrescriptions();
            } else {
                UIHelper.showError("Could not delete prescription.");
            }
        } catch (Exception e) { UIHelper.showError("Enter a valid prescription ID before deleting."); }
    }

    private Prescription readFields() throws Exception {
        int prescID    = Integer.parseInt(idField.getText().trim());
        String date    = dateField.getText().trim();
        String time    = timeField.getText().trim();
        String doctor  = doctorField.getText().trim();
        int cid        = Integer.parseInt(cidField.getText().trim());
        int bid        = Integer.parseInt(bidField.getText().trim());
        int eid        = Integer.parseInt(eidField.getText().trim());

        if (date.isEmpty() || doctor.isEmpty()) throw new Exception();

        double total = 0;
        for (PrescriptionItem item : itemsList) total += item.getLineTotal();

        return new Prescription(prescID, date, time, doctor, total, cid, bid, eid, "", "", "");
    }

    private void fillFields(Prescription p) {
        idField.setText(String.valueOf(p.getPrescID()));
        dateField.setText(p.getPrescriptionDate());
        timeField.setText(p.getPrescriptionTime());
        doctorField.setText(p.getDoctorName());
        cidField.setText(String.valueOf(p.getCid()));
        bidField.setText(String.valueOf(p.getBid()));
        eidField.setText(String.valueOf(p.getEid()));

        itemsList.setAll(prescriptionDAO.getPrescriptionItems(p.getPrescID()));
        updatePrescriptionTotal();
    }

    private void clearFields() {
        idField.clear(); dateField.clear(); timeField.clear(); doctorField.clear();
        cidField.clear(); bidField.clear(); eidField.clear();
        prodIdField.clear(); quantityField.clear(); dosageField.clear();
        itemsList.clear();
        updatePrescriptionTotal();
        prescriptionsTable.getSelectionModel().clearSelection();
    }

    private void updateStats() {
        int total    = prescriptionsList.size();
        double amount = prescriptionDAO.getTotalPrescriptionAmount();
        totalLabel.setText(String.valueOf(total));
        amountLabel.setText(String.format("%.2f", amount));
        averageLabel.setText(String.format("%.2f", total > 0 ? amount / total : 0));
    }

    public static void main(String[] args) { launch(args); }
}
