package FX;

import java.util.ArrayList;

import DAO.ReportsDAO;
import Tables.ReportRow;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReportsFX extends Application {

    private ReportsDAO reportsDAO = new ReportsDAO();

    // stat card labels (shared across rebuilds so refresh updates them in place)
    private Label totalRevenueLabel = new Label("0.00");
    private Label totalSalesLabel   = new Label("0");
    private Label itemsSoldLabel    = new Label("0");
    private Label topBranchLabel    = new Label("-");

    // we keep the root so Refresh can swap the center content
    private BorderPane root;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + UIHelper.APP_BG + ";");
        root.setTop(UIHelper.topBarWithRefresh(stage, "Reports & Analytics",
                "Sales, products, and inventory insights", this::refresh));
        root.setCenter(buildContent());

        Scene scene = new Scene(root, 1300, 820);
        stage.setTitle("GrandPharm — Reports");
        stage.setScene(scene);
        stage.show();
    }

    private void refresh() {
        root.setCenter(buildContent());
    }



    private ScrollPane buildContent() {
        loadStats();

        VBox content = new VBox(18);
        content.setPadding(new Insets(25, 30, 30, 30));

        HBox statsRow = buildStatsRow();
        TabPane tabs  = buildTabPane();
        tabs.setPrefHeight(620);
        tabs.setMinHeight(620);

        content.getChildren().addAll(statsRow, tabs);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: " + UIHelper.APP_BG + "; -fx-background: " + UIHelper.APP_BG + ";");
        return scroll;
    }

    private HBox buildStatsRow() {
        HBox row = new HBox(18);

        row.getChildren().addAll(
            UIHelper.statCard("Total Revenue (₪)", totalRevenueLabel),
            UIHelper.statCard("Total Sales",       totalSalesLabel),
            UIHelper.statCard("Items Sold",        itemsSoldLabel),
            UIHelper.statCard("Top Branch",        topBranchLabel)
        );

        for (var node : row.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);
        return row;
    }

    private TabPane buildTabPane() {
        TabPane tabs = new TabPane();
        tabs.setStyle(UIHelper.card() + " -fx-tab-min-width: 130;");
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabs.getTabs().addAll(
            new Tab("Revenue by Branch",     buildRevenueByBranchChart()),
            new Tab("Top Selling Products",  buildTopProductsChart()),
            new Tab("Sales by Payment",      buildSalesByPaymentChart()),
            new Tab("Sales Trend",           buildSalesTrendChart()),
            new Tab("Inventory by Category", buildInventoryByCategoryChart()),
            new Tab("Revenue by Employee",   buildRevenueByEmployeeChart()),
            new Tab("Purchases by Supplier", buildPurchasesBySupplierChart()),
            new Tab("Prescriptions by Month",buildPrescriptionsByMonthChart()),
            new Tab("Sales by Date Range",   buildSalesByDateRangeTab())
        );
        return tabs;
    }



    private void loadStats() {
        totalRevenueLabel.setText(String.format("%.2f", reportsDAO.getTotalRevenue()));
        totalSalesLabel.setText(String.valueOf(reportsDAO.getTotalSales()));
        itemsSoldLabel.setText(String.valueOf(reportsDAO.getTotalItemsSold()));

        String topBranch = reportsDAO.getTopBranchName();
        topBranchLabel.setText(topBranch == null ? "-" : topBranch);
    }

    // ============================================================
    // CHARTS
    // ============================================================

    private BarChart<String, Number> buildRevenueByBranchChart() {
        BarChart<String, Number> chart = createBarChart(
                "Total Sales Revenue per Branch", "Branch", "Revenue (₪)", 0);
        fillBarChart(chart, reportsDAO.getRevenueByBranch());
        return chart;
    }

    private BarChart<String, Number> buildTopProductsChart() {
        BarChart<String, Number> chart = createBarChart(
                "Top 10 Best-Selling Products", "Product", "Units Sold", -25);
        fillBarChart(chart, reportsDAO.getTopSellingProducts());
        return chart;
    }

    private PieChart buildSalesByPaymentChart() {
        PieChart chart = new PieChart();
        chart.setTitle("Number of Sales by Payment Method");
        chart.setPrefHeight(520);
        chart.getStylesheets().add(getClass().getResource("charts.css").toExternalForm());

        for (ReportRow row : reportsDAO.getSalesByPaymentMethod()) {
            chart.getData().add(new PieChart.Data(row.getLabel(), row.getValue()));
        }
        return chart;
    }

    private LineChart<String, Number> buildSalesTrendChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Daily Revenue (₪)");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Daily Sales Revenue Trend");
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(520);
        chart.getStylesheets().add(getClass().getResource("charts.css").toExternalForm());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (ReportRow row : reportsDAO.getSalesTrend()) {
            series.getData().add(new XYChart.Data<>(row.getLabel(), row.getValue()));
        }
        chart.getData().add(series);
        return chart;
    }

    private BarChart<String, Number> buildInventoryByCategoryChart() {
        BarChart<String, Number> chart = createBarChart(
                "Inventory Stock Levels by Category", "Category", "Total Units in Stock", -15);
        fillBarChart(chart, reportsDAO.getInventoryByCategory());
        return chart;
    }

    private BarChart<String, Number> buildRevenueByEmployeeChart() {
        BarChart<String, Number> chart = createBarChart(
                "Total Sales Revenue per Employee", "Employee", "Revenue (₪)", -20);
        fillBarChart(chart, reportsDAO.getRevenueByEmployee());
        return chart;
    }

    private BarChart<String, Number> buildPurchasesBySupplierChart() {
        BarChart<String, Number> chart = createBarChart(
                "Total Purchase Spending per Supplier", "Supplier", "Amount Spent (₪)", -20);
        fillBarChart(chart, reportsDAO.getPurchasesBySupplier());
        return chart;
    }

    private BarChart<String, Number> buildPrescriptionsByMonthChart() {
        BarChart<String, Number> chart = createBarChart(
                "Number of Prescriptions per Month", "Month", "Prescriptions", -15);
        fillBarChart(chart, reportsDAO.getPrescriptionsByMonth());
        return chart;
    }

    // interactive tab — user picks a date range then clicks Run to see the filtered chart
    private VBox buildSalesByDateRangeTab() {
        TextField fromField = new TextField();
        TextField toField   = new TextField();
        UIHelper.setupField(fromField, "From: YYYY-MM-DD");
        UIHelper.setupField(toField,   "To:   YYYY-MM-DD");
        fromField.setPrefWidth(180);
        toField.setPrefWidth(180);

        Button runBtn = new Button("Run Query");
        runBtn.setStyle(UIHelper.primaryBtn());

        Label hint = new Label("Enter a date range and click Run Query to filter sales revenue by day.");
        hint.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIHelper.TEXT_GRAY + ";");

        HBox filterRow = new HBox(12, new Label("From:"), fromField, new Label("To:"), toField, runBtn);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterRow.setPadding(new Insets(16, 16, 8, 16));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Daily Revenue (₪)");
        xAxis.setTickLabelRotation(-20);

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Daily Sales Revenue — Filtered by Date Range");
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(480);
        chart.getStylesheets().add(getClass().getResource("charts.css").toExternalForm());

        runBtn.setOnAction(e -> {
            String from = fromField.getText().trim();
            String to   = toField.getText().trim();
            if (from.isEmpty() || to.isEmpty()) return;

            chart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Tables.ReportRow row : reportsDAO.getSalesByDateRange(from, to)) {
                series.getData().add(new XYChart.Data<>(row.getLabel(), row.getValue()));
            }
            chart.getData().add(series);
        });

        VBox container = new VBox(8, filterRow, hint, chart);
        container.setPadding(new Insets(0, 8, 8, 8));
        return container;
    }

    // ============================================================
    // CHART HELPERS
    // ============================================================

    // builds an empty bar chart with axes, title and optional x-label rotation
    private BarChart<String, Number> createBarChart(String title, String xLabel, String yLabel, int rotation) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        if (rotation != 0) xAxis.setTickLabelRotation(rotation);

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(520);
        chart.getStylesheets().add(getClass().getResource("charts.css").toExternalForm());
        return chart;
    }

    // takes a list of ReportRow and pours it into a bar chart as a series
    private void fillBarChart(BarChart<String, Number> chart, ArrayList<ReportRow> rows) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (ReportRow row : rows) {
            series.getData().add(new XYChart.Data<>(row.getLabel(), row.getValue()));
        }
        chart.getData().add(series);
    }

    public static void main(String[] args) {
        launch(args);
    }
}