package FX;

import DAO.DBConnection;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Dashboard extends Application {

    private Stage mainStage;

    @Override
    public void start(Stage stage) {
        this.mainStage = stage;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.APP_BG + ";");
        root.setLeft(buildSidebar());
        root.setCenter(buildDashboardContent());

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("GrandPharm — Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    // -------------------------------------------------------
    // SIDEBAR
    // -------------------------------------------------------
    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(230);
        sidebar.setPadding(new Insets(30, 15, 25, 15));
        sidebar.setStyle("-fx-background-color: " + UIHelper.SIDEBAR_BG + ";");

        // logo
        ImageView logoImage = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("logo.png"));
            logoImage.setImage(img);
            logoImage.setFitWidth(130);
            logoImage.setPreserveRatio(true);
        } catch (Exception e) {
            // logo not found — text still shows
        }

        VBox imageWrapper = new VBox(logoImage);
        imageWrapper.setStyle("-fx-background-color: " + UIHelper.SIDEBAR_BG + ";");

        Label appName = new Label("GrandPharm");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label appSub = new Label("Management System");
        appSub.setStyle("-fx-text-fill: #9090B0; -fx-font-size: 12px;");

        VBox logoBox = new VBox(6, imageWrapper, appName, appSub);
        logoBox.setStyle("-fx-background-color: " + UIHelper.SIDEBAR_BG + ";");
        logoBox.setPadding(new Insets(0, 0, 30, 5));

        // nav buttons
        Button btnDashboard     = navButton("Dashboard",       true);
        Button btnProducts      = navButton("Products",        false);
        Button btnInventory     = navButton("Inventory",       false);
        Button btnSales         = navButton("Sales",           false);
        Button btnPurchases     = navButton("Purchase Orders", false);
        Button btnPrescriptions = navButton("Prescriptions",   false);
        Button btnCustomers     = navButton("Customers",       false);
        Button btnEmployees     = navButton("Employees",       false);
        Button btnSuppliers     = navButton("Suppliers",       false);
        Button btnBatches       = navButton("Batches",         false);
        Button btnReports       = navButton("Reports",         false);

        btnDashboard.setOnAction(e -> showDashboard());
        btnProducts.setOnAction(e -> openModule("Products"));
        btnInventory.setOnAction(e -> openModule("Inventory"));
        btnSales.setOnAction(e -> openModule("Sales"));
        btnPurchases.setOnAction(e -> openModule("Purchases"));
        btnPrescriptions.setOnAction(e -> openModule("Prescriptions"));
        btnCustomers.setOnAction(e -> openModule("Customers"));
        btnEmployees.setOnAction(e -> openModule("Employees"));
        btnSuppliers.setOnAction(e -> openModule("Suppliers"));
        btnBatches.setOnAction(e -> openModule("Batches"));
        btnReports.setOnAction(e -> openModule("Reports"));

        sidebar.getChildren().addAll(
            logoBox,
            btnDashboard, btnProducts, btnInventory, btnSales,
            btnPurchases, btnPrescriptions, btnCustomers,
            btnEmployees, btnSuppliers, btnBatches, btnReports
        );

        return sidebar;
    }

    private Button navButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        if (active) {
            btn.setStyle(
                "-fx-background-color: " + UIHelper.ACCENT + ";" +
                "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;" +
                "-fx-padding: 11 14; -fx-background-radius: 8;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #B0B0CC; -fx-font-size: 13px;" +
                "-fx-padding: 11 14; -fx-background-radius: 8;"
            );
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #2A2A40;" +
                "-fx-text-fill: white; -fx-font-size: 13px;" +
                "-fx-padding: 11 14; -fx-background-radius: 8;"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #B0B0CC; -fx-font-size: 13px;" +
                "-fx-padding: 11 14; -fx-background-radius: 8;"
            ));
        }

        return btn;
    }

    // -------------------------------------------------------
    // DASHBOARD CONTENT
    // -------------------------------------------------------
    private VBox buildDashboardContent() {
        VBox content = new VBox(25);
        content.setPadding(new Insets(35, 35, 35, 35));

        Label pageTitle = new Label("Dashboard");
        pageTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_DARK + ";");

        content.getChildren().addAll(pageTitle, buildStatsRow(), buildQuickAccess(), buildRecentSales());
        return content;
    }

    private HBox buildStatsRow() {
        HBox row = new HBox(18);

        int totalProducts   = getCount("SELECT COUNT(*) FROM Product");
        int totalEmployees  = getCount("SELECT COUNT(*) FROM Employee");
        int totalCustomers  = getCount("SELECT COUNT(*) FROM Customer");
        int totalSales      = getCount("SELECT COUNT(*) FROM Sale");
        int lowStockItems   = getCount("SELECT COUNT(*) FROM Inventory WHERE QuantityAvailable <= MinStock");
        int expiringBatches = getCount("SELECT COUNT(*) FROM Batch WHERE ExpDate <= DATE_ADD(CURDATE(), INTERVAL 90 DAY)");

        row.getChildren().addAll(
            UIHelper.statCard("Total Products",   new Label(String.valueOf(totalProducts))),
            UIHelper.statCard("Employees",        new Label(String.valueOf(totalEmployees))),
            UIHelper.statCard("Customers",        new Label(String.valueOf(totalCustomers))),
            UIHelper.statCard("Sales",            new Label(String.valueOf(totalSales))),
            UIHelper.statCard("Low Stock Alerts", new Label(String.valueOf(lowStockItems))),
            UIHelper.statCard("Expiring Soon",    new Label(String.valueOf(expiringBatches)))
        );

        for (var node : row.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);
        return row;
    }

    private VBox buildQuickAccess() {
        VBox section = new VBox(14);

        Label sectionTitle = new Label("Quick Access");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_DARK + ";");

        HBox row1 = new HBox(12);
        HBox row2 = new HBox(12);

        row1.getChildren().addAll(
            quickButton("Products",        "Products"),
            quickButton("Sales",           "Sales"),
            quickButton("Purchase Orders", "Purchases"),
            quickButton("Prescriptions",   "Prescriptions")
        );

        row2.getChildren().addAll(
            quickButton("Customers",  "Customers"),
            quickButton("Employees",  "Employees"),
            quickButton("Suppliers",  "Suppliers"),
            quickButton("Inventory",  "Inventory"),
            quickButton("Batches",    "Batches"),
            quickButton("Reports",    "Reports")
        );

        for (var node : row1.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);
        for (var node : row2.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);

        section.getChildren().addAll(sectionTitle, row1, row2);
        return section;
    }

    private Button quickButton(String label, String module) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(UIHelper.primaryBtn());
        btn.setOnAction(e -> openModule(module));
        return btn;
    }

    private VBox buildRecentSales() {
        VBox section = new VBox(12);

        Label sectionTitle = new Label("Recent Sales");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.TEXT_DARK + ";");

        TableView<String[]> recentTable = new TableView<>();
        recentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        recentTable.setPrefHeight(180);
        recentTable.setStyle("-fx-background-color: white; -fx-border-color: " + UIHelper.BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        TableColumn<String[], String> saleIdCol   = new TableColumn<>("Sale ID");
        TableColumn<String[], String> customerCol = new TableColumn<>("Customer");
        TableColumn<String[], String> dateCol     = new TableColumn<>("Date");
        TableColumn<String[], String> branchCol   = new TableColumn<>("Branch");
        TableColumn<String[], String> totalCol    = new TableColumn<>("Total (₪)");

        saleIdCol.setCellValueFactory(r   -> new SimpleStringProperty(r.getValue()[0]));
        customerCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()[1]));
        dateCol.setCellValueFactory(r     -> new SimpleStringProperty(r.getValue()[2]));
        branchCol.setCellValueFactory(r   -> new SimpleStringProperty(r.getValue()[3]));
        totalCol.setCellValueFactory(r    -> new SimpleStringProperty(r.getValue()[4]));

        recentTable.getColumns().addAll(saleIdCol, customerCol, dateCol, branchCol, totalCol);

        String query =
            "SELECT s.SID, c.Name, s.SaleDate, b.Name, s.TotalAmount " +
            "FROM Sale s " +
            "JOIN Customer c ON s.CID = c.CID " +
            "JOIN Branch b ON s.BID = b.BID " +
            "ORDER BY s.SaleDate DESC, s.SaleTime DESC LIMIT 5";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                recentTable.getItems().add(new String[]{
                    String.valueOf(rs.getInt(1)),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    String.format("%.2f", rs.getDouble(5))
                });
            }
            rs.close(); stmt.close(); conn.close();
        } catch (Exception e) {
            System.out.println("Error loading recent sales");
            e.printStackTrace();
        }

        VBox card = new VBox(recentTable);
        card.setPadding(new Insets(20));
        card.setStyle(UIHelper.card());

        section.getChildren().addAll(sectionTitle, card);
        return section;
    }

    // -------------------------------------------------------
    // NAVIGATION
    // -------------------------------------------------------
    private void showDashboard() {
        BorderPane root = (BorderPane) mainStage.getScene().getRoot();
        root.setCenter(buildDashboardContent());
    }

    private void openModule(String module) {
        try {
            Stage moduleStage = new Stage();
            switch (module) {
                case "Products":
                    new ProductFX().start(moduleStage);
                    break;
                case "Inventory":
                    new InventoryFX().start(moduleStage);
                    break;
                case "Sales":         new SaleFX().start(moduleStage);          break;
                case "Employees":     new EmployeeFX().start(moduleStage);      break;
                case "Suppliers":     new SupplierFX().start(moduleStage);      break;
                case "Batches":       new BatchFX().start(moduleStage);         break;
                case "Customers":     new CustomerFX().start(moduleStage);      break;
                case "Purchases":     new PurchaseOrderFX().start(moduleStage); break;
                case "Prescriptions": new PrescriptionFX().start(moduleStage);  break;
                case "Reports":       new ReportsFX().start(moduleStage);       break;
                default:
                    showComingSoon(module);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showComingSoon(String module) {
        Stage popup = new Stage();
        popup.setTitle(module);
        Label msg = new Label(module + " — coming soon.");
        msg.setStyle("-fx-font-size: 15px; -fx-text-fill: " + UIHelper.TEXT_GRAY + ";");
        msg.setPadding(new Insets(40));
        popup.setScene(new javafx.scene.Scene(msg, 300, 130));
        popup.show();
    }

    // -------------------------------------------------------
    // DB HELPER
    // -------------------------------------------------------
    private int getCount(String query) {
        int count = 0;
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) count = rs.getInt(1);
            rs.close(); stmt.close(); conn.close();
        } catch (Exception e) {
            System.out.println("Error running count: " + query);
            e.printStackTrace();
        }
        return count;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
