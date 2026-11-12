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
import javafx.scene.control.CheckBox;
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

import Tables.Product;
import DAO.ProductDAO;

public class ProductFX extends Application {

    private ProductDAO productDAO = new ProductDAO();

    private TableView<Product> table = new TableView<>();
    private ObservableList<Product> productList = FXCollections.observableArrayList();

    private TextField idField     = new TextField();
    private TextField nameField   = new TextField();
    private TextField priceField  = new TextField();
    private TextField catIdField  = new TextField();
    private TextField searchField = new TextField();
    private CheckBox  prescriptionCheck = new CheckBox("Requires prescription");

    private Label totalLabel        = new Label("0");
    private Label prescriptionLabel = new Label("0");
    private Label averageLabel      = new Label("0.00");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.APP_BG + ";");
        root.setTop(UIHelper.topBarWithRefresh(stage, "Product Management", "Add, update, delete and search products", this::loadProducts));
        root.setCenter(buildContent());

        loadProducts();

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("GrandPharm — Products");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(25, 30, 30, 30));

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
            UIHelper.statCard("Total Products",        totalLabel),
            UIHelper.statCard("Prescription Products", prescriptionLabel),
            UIHelper.statCard("Average Price (₪)",     averageLabel)
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

        UIHelper.setupField(searchField, "Search product by name...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn  = new Button("Search");
        Button showAllBtn = new Button("Show All");
        searchBtn.setStyle(UIHelper.secondaryBtn());
        showAllBtn.setStyle(UIHelper.secondaryBtn());

        searchBtn.setOnAction(e -> searchProducts());
        showAllBtn.setOnAction(e -> { searchField.clear(); loadProducts(); });

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
        table.setItems(productList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        TableColumn<Product, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getProdID()));
        idCol.setMaxWidth(70);

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProdName()));

        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoryName()));

        TableColumn<Product, Number> priceCol = new TableColumn<>("Price (₪)");
        priceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()));

        TableColumn<Product, String> presCol = new TableColumn<>("Prescription");
        presCol.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().isPrescriptionRequired() ? "Required" : "Not Required")
        );

        table.getColumns().clear();
        table.getColumns().addAll(idCol, nameCol, catCol, priceCol, presCol);
    }

    private VBox buildFormSection() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        UIHelper.setupField(idField,    "Product ID");
        UIHelper.setupField(nameField,  "Product Name");
        UIHelper.setupField(priceField, "Price");
        UIHelper.setupField(catIdField, "Category ID");

        grid.add(UIHelper.fieldLabel("Product ID"),   0, 0); grid.add(idField,    0, 1);
        grid.add(UIHelper.fieldLabel("Product Name"), 0, 2); grid.add(nameField,  0, 3);
        grid.add(UIHelper.fieldLabel("Price"),        0, 4); grid.add(priceField, 0, 5);
        grid.add(UIHelper.fieldLabel("Category ID"),  0, 6); grid.add(catIdField, 0, 7);

        prescriptionCheck.setStyle("-fx-text-fill: " + UIHelper.TEXT_DARK + "; -fx-font-size: 13px;");
        grid.add(prescriptionCheck, 0, 8);

        Button addBtn    = UIHelper.fullWidthBtn("Add Product",    UIHelper.primaryBtn());
        Button updateBtn = UIHelper.fullWidthBtn("Update Product", UIHelper.secondaryBtn());
        Button deleteBtn = UIHelper.fullWidthBtn("Delete Product", UIHelper.dangerBtn());
        Button clearBtn  = UIHelper.fullWidthBtn("Clear Fields",   UIHelper.lightBtn());

        addBtn.setOnAction(e -> addProduct());
        updateBtn.setOnAction(e -> updateProduct());
        deleteBtn.setOnAction(e -> deleteProduct());
        clearBtn.setOnAction(e -> clearFields());

        return UIHelper.formPanel("Product Details", 340, grid, addBtn, updateBtn, deleteBtn, clearBtn);
    }

    private void loadProducts() {
        ArrayList<Product> products = productDAO.getAllProducts();
        productList.setAll(products);
        updateStats();
    }

    private void searchProducts() {
        String name = searchField.getText().trim();
        if (name.isEmpty()) { loadProducts(); return; }
        productList.setAll(productDAO.searchProducts(name));
        updateStats();
    }

    private void addProduct() {
        try {
            Product p = readFields();
            if (productDAO.addProduct(p)) {
                UIHelper.showInfo("Product added successfully.");
                clearFields(); loadProducts();
            } else {
                UIHelper.showError("Could not add product. Check if the ID already exists or the category ID is valid.");
            }
        } catch (Exception e) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void updateProduct() {
        try {
            Product p = readFields();
            if (productDAO.updateProduct(p)) {
                UIHelper.showInfo("Product updated successfully.");
                clearFields(); loadProducts();
            } else {
                UIHelper.showError("Could not update. Make sure the product ID exists.");
            }
        } catch (Exception e) {
            UIHelper.showError("Please fill in all fields correctly.");
        }
    }

    private void deleteProduct() {
        try {
            int prodID = Integer.parseInt(idField.getText().trim());
            if (productDAO.deleteProduct(prodID)) {
                UIHelper.showInfo("Product deleted successfully.");
                clearFields(); loadProducts();
            } else {
                UIHelper.showError("Could not delete. Product may be linked to sales or inventory.");
            }
        } catch (Exception e) {
            UIHelper.showError("Enter a valid product ID before deleting.");
        }
    }

    private Product readFields() throws Exception {
        int prodID      = idField.getText().trim().isEmpty() ? 0 : Integer.parseInt(idField.getText().trim());
        String prodName = nameField.getText().trim();
        double price    = Double.parseDouble(priceField.getText().trim());
        int catID       = Integer.parseInt(catIdField.getText().trim());
        boolean presReq = prescriptionCheck.isSelected();

        if (prodName.isEmpty() || price < 0) throw new Exception();
        return new Product(prodID, prodName, "", price, presReq, catID);
    }

    private void fillFields(Product p) {
        idField.setText(String.valueOf(p.getProdID()));
        nameField.setText(p.getProdName());
        priceField.setText(String.valueOf(p.getPrice()));
        catIdField.setText(String.valueOf(p.getCatID()));
        prescriptionCheck.setSelected(p.isPrescriptionRequired());
    }

    private void clearFields() {
        idField.clear(); nameField.clear();
        priceField.clear(); catIdField.clear();
        prescriptionCheck.setSelected(false);
        table.getSelectionModel().clearSelection();
    }

    private void updateStats() {
        int total = productList.size();
        int presCount = 0;
        double totalPrice = 0;
        for (Product p : productList) {
            if (p.isPrescriptionRequired()) presCount++;
            totalPrice += p.getPrice();
        }
        totalLabel.setText(String.valueOf(total));
        prescriptionLabel.setText(String.valueOf(presCount));
        averageLabel.setText(String.format("%.2f", total > 0 ? totalPrice / total : 0));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
