package FX;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UIHelper {

    // -------------------------------------------------------
    // COLORS — change here and it applies everywhere
    // -------------------------------------------------------
    public static final String ACCENT     = "#7B1F3A";
    public static final String SIDEBAR_BG = "#1C1C2E";
    public static final String APP_BG     = "#F5F6FA";
    public static final String CARD_BG    = "#FFFFFF";
    public static final String TEXT_DARK  = "#1C1C2E";
    public static final String TEXT_GRAY  = "#7A7A9D";
    public static final String BORDER     = "#E0E0E0";

    // -------------------------------------------------------
    // STYLE STRINGS
    // -------------------------------------------------------
    public static String card() {
        return "-fx-background-color: " + CARD_BG + ";" +
               "-fx-background-radius: 14; -fx-border-radius: 14;" +
               "-fx-border-color: " + BORDER + ";" +
               "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 0, 3);";
    }

    public static String input() {
        return "-fx-background-color: #F8FAFC; -fx-border-color: " + BORDER + ";" +
               "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 9;";
    }

    public static String primaryBtn() {
        return "-fx-background-color: " + ACCENT + "; -fx-text-fill: white;" +
               "-fx-font-weight: bold; -fx-padding: 10 16; -fx-background-radius: 10;";
    }

    public static String secondaryBtn() {
        return "-fx-background-color: #F3E8EC; -fx-text-fill: " + ACCENT + ";" +
               "-fx-font-weight: bold; -fx-padding: 10 16; -fx-background-radius: 10;";
    }

    public static String dangerBtn() {
        return "-fx-background-color: #FEE2E2; -fx-text-fill: #C0392B;" +
               "-fx-font-weight: bold; -fx-padding: 10 16; -fx-background-radius: 10;";
    }

    public static String lightBtn() {
        return "-fx-background-color: #F1F5F9; -fx-text-fill: #334155;" +
               "-fx-font-weight: bold; -fx-padding: 10 16; -fx-background-radius: 10;";
    }

    public static String topBarBackBtn() {
        return "-fx-background-color: " + ACCENT + ";" +
               "-fx-text-fill: white; -fx-font-weight: bold;" +
               "-fx-padding: 8 16; -fx-background-radius: 8;";
    }

    public static String topBarRefreshBtn() {
        return "-fx-background-color: transparent;" +
               "-fx-text-fill: #B0B0CC; -fx-font-weight: bold;" +
               "-fx-padding: 8 16; -fx-background-radius: 8;" +
               "-fx-border-color: #3A3A5A; -fx-border-radius: 8;";
    }

    // -------------------------------------------------------
    // REUSABLE UI COMPONENTS
    // -------------------------------------------------------

    // the dark top bar used on every module screen
    public static HBox topBar(Stage stage, String title, String subtitle) {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(18, 30, 18, 30));
        bar.setStyle("-fx-background-color: " + SIDEBAR_BG + ";");

        Button backBtn = new Button("← Dashboard");
        backBtn.setStyle(topBarBackBtn());
        backBtn.setOnAction(e -> {
            try {
                stage.close();
                new Dashboard().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label subLabel = new Label(subtitle);
        subLabel.setStyle("-fx-text-fill: #9090B0; -fx-font-size: 12px;");

        VBox titleBox = new VBox(2, titleLabel, subLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bar.getChildren().addAll(backBtn, titleBox, spacer);
        return bar;
    }

    // top bar with a refresh button on the right
    public static HBox topBarWithRefresh(Stage stage, String title, String subtitle, Runnable onRefresh) {
        HBox bar = topBar(stage, title, subtitle);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(topBarRefreshBtn());
        refreshBtn.setOnAction(e -> onRefresh.run());

        bar.getChildren().add(refreshBtn);
        return bar;
    }

    // a stat card with a colored top bar, title, and value label
    public static VBox statCard(String title, Label valueLabel) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18));
        card.setStyle(card());

        Region bar = new Region();
        bar.setPrefHeight(4);
        bar.setStyle("-fx-background-color: " + ACCENT + "; -fx-background-radius: 4;");

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_GRAY + "; -fx-font-weight: bold;");

        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        card.getChildren().addAll(bar, titleLbl, valueLabel);
        return card;
    }

    // a form field label
    public static Label fieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #486581;");
        return lbl;
    }

    // sets up a text field with prompt text and input style
    public static void setupField(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.setStyle(input());
        field.setMaxWidth(Double.MAX_VALUE);
    }

    // a full-width button with a given style
    public static Button fullWidthBtn(String text, String style) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(style);
        return btn;
    }

    // the standard scrollable form panel used on every module screen
    public static VBox formPanel(String title, double prefWidth, Node... content) {
        VBox outer = new VBox();
        outer.setPrefWidth(prefWidth);
        outer.setStyle(card());

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        titleLabel.setPadding(new Insets(22, 22, 0, 22));

        VBox inner = new VBox(14, content);
        inner.setPadding(new Insets(14, 22, 22, 22));

        ScrollPane scroll = new ScrollPane(inner);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        outer.getChildren().addAll(titleLabel, scroll);
        return outer;
    }

    // -------------------------------------------------------
    // ALERTS
    // -------------------------------------------------------
    public static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
