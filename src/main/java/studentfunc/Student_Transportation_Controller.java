package studentfunc;

import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import userTypes.Student;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Student_Transportation_Controller {

    @FXML private StackPane transportContentArea;
    @FXML private Button btnToCampus, btnToCity;
    @FXML private Label titleLabel;
    @FXML private Label userNameLabel;

    // Day Selection Buttons
    @FXML private Button btnMon, btnTue, btnWed, btnThu, btnFri;
    private List<Button> dayButtons;
    Student s = LoginController.getStudentUser();

    @FXML
    public void initialize() {
        dayButtons = Arrays.asList(btnMon, btnTue, btnWed, btnThu, btnFri);
        userNameLabel.setText(s.getFirstName()+" "+s.getLastName());

        // Default View: City - Campus on Monday
        updateSidebarStyle(btnToCampus);
        handleDaySelection(btnMon);
    }

    @FXML
    private void showToCampus(ActionEvent event) {
        updateSidebarStyle(btnToCampus);
        titleLabel.setText("Route: City - Campus");
        refreshView();
    }

    @FXML
    private void showToCity(ActionEvent event) {
        updateSidebarStyle(btnToCity);
        titleLabel.setText("Route: Campus - City");
        refreshView();
    }

    @FXML
    private void handleDayClick(ActionEvent event) {
        handleDaySelection((Button) event.getSource());
    }

    /**
     * Handles the visual toggle of day buttons and refreshes data
     */
    private void handleDaySelection(Button selectedBtn) {
        String unselectedStyle = "-fx-background-color: white; -fx-text-fill: #0099cc; -fx-border-color: #0099cc; -fx-border-radius: 5; -fx-cursor: hand; -fx-font-weight: bold;";
        String selectedStyle = "-fx-background-color: #0099cc; -fx-text-fill: white; -fx-border-color: #0099cc; -fx-border-radius: 5; -fx-cursor: hand; -fx-font-weight: bold;";

        for (Button b : dayButtons) {
            b.setStyle(unselectedStyle);
        }
        selectedBtn.setStyle(selectedStyle);

        refreshView();
    }

    /**
     * Detects current state and calls the view loader
     */
    private void refreshView() {
        String activeDay = "Monday";
        for (Button b : dayButtons) {
            if (b.getStyle().contains("-fx-background-color: #0099cc")) {
                activeDay = b.getText();
            }
        }

        // Determine route based on sidebar button state
        boolean isToCity = btnToCity.getStyle().contains("#0099cc");
        String routeName = isToCity ? "Campus - City" : "City - Campus";
        int routeIndex = isToCity ? 1 : 0;

        loadScheduleView(routeName, activeDay, routeIndex);
    }

    private void loadScheduleView(String route, String day, int routeType) {
        transportContentArea.getChildren().clear();

        // Define different schedules based on route index
        String[] times = (routeType == 1)
                ? new String[]{"09:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00"}
                : new String[]{"08:00", "11:00", "12:00", "14:00", "16:00", "18:00", "20:00"};

        transportContentArea.getChildren().add(createThemedSection("🚌 Transport Schedule", route + " — " + day, times));
    }

    private VBox createThemedSection(String headerTitle, String subTitle, String[] times) {
        VBox container = new VBox();
        container.setStyle("-fx-border-color: #0099cc; -fx-border-width: 1; -fx-background-color: white;");

        HBox header = new HBox(new Label(headerTitle));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(55);
        header.setStyle("-fx-background-color: #0099cc; -fx-padding: 0 25 0 25;");
        ((Label)header.getChildren().get(0)).setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");

        VBox body = new VBox(30);
        body.setStyle("-fx-padding: 40;");

        Label lblSub = new Label(subTitle);
        lblSub.setStyle("-fx-font-weight: bold; -fx-font-size: 26px; -fx-text-fill: #333333;");

        // Schedule Grid Table
        GridPane timeGrid = new GridPane();
        timeGrid.setHgap(20);
        timeGrid.setVgap(15);

        for (int i = 0; i < times.length; i++) {
            Label timeLabel = new Label(times[i]);
            timeLabel.setStyle("-fx-font-size: 19px; -fx-text-fill: #0099cc; -fx-font-weight: bold; " +
                    "-fx-background-color: #f0faff; -fx-padding: 12 25 12 25; -fx-background-radius: 8;");
            timeGrid.add(timeLabel, i % 4, i / 4); // 4 columns per row
        }

        body.getChildren().addAll(lblSub, timeGrid);
        container.getChildren().addAll(header, body);
        return container;
    }

    private void updateSidebarStyle(Button activeBtn) {
        String idle = "-fx-background-color: transparent; -fx-text-fill: #555555; -fx-alignment: CENTER_LEFT; -fx-font-size: 16px; -fx-padding: 0 0 0 15; -fx-cursor: hand;";
        String active = "-fx-background-color: #0099cc; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 16px; -fx-padding: 0 0 0 15; -fx-cursor: hand;";

        btnToCampus.setStyle(idle);
        btnToCity.setStyle(idle);
        activeBtn.setStyle(active);
    }


    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/student_view.fxml"));
            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}