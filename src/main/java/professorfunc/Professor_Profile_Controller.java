package professorfunc;

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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import userTypes.Professor;

import java.io.IOException;

public class Professor_Profile_Controller {
    LoginController a= new LoginController();
    Professor p = a.getProfessorUser();
    @FXML private StackPane contentArea;
    @FXML private VBox identificationTableContainer;

    @FXML private Button btnIdent;
    @FXML private Button btnAddress;


    @FXML private Label labelName;
    @FXML private Label labelSurname;
    @FXML private Label labelId;
    @FXML private Label labelBirthday;
    @FXML private Label title;
    @FXML private Label email;
    @FXML private Label Fullname;
    @FXML private Label Fullname1;

    @FXML
    public void initialize() {
        // Here is where you "Fill" the data
        labelName.setText(p.getFirstName());
        labelSurname.setText(p.getLastName());
        labelId.setText(String.valueOf(p.getProfessorId()));
        labelBirthday.setText(p.getDateOfBirth());
        title.setText(p.getProfessorTitle());
        email.setText(p.getEmail());
        Fullname.setText(p.getFirstName() + " " + p.getLastName());
        Fullname1.setText(p.getFirstName() + " " + p.getLastName());
        // Ensure the identification view is the default view on startup
        setActiveTab(btnIdent);
    }

    @FXML
    private void showIdentification(ActionEvent event) {
        setActiveTab(btnIdent);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(identificationTableContainer);
    }

    @FXML
    private void showAddress(ActionEvent event) {
        setActiveTab(btnAddress);
        contentArea.getChildren().clear();

        // You can also change this text dynamically here
        String street = p.getAddress();
        contentArea.getChildren().add(createThemedSection("📍 Adress Details", "Home Address", street));
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/professor_view.fxml"));
            // Changed to 1200x800 to match your main dashboard size
            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Navigation error: Ensure professor_view.fxml is in the correct resources folder.");
        }
    }

    private void setActiveTab(Button activeButton) {
        Button[] buttons = {btnIdent, btnAddress};
        for (Button b : buttons) {
            b.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-alignment: CENTER_LEFT; -fx-font-size: 16px; -fx-padding: 0 0 0 15;");
        }
        activeButton.setStyle("-fx-background-color: #0099cc; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 16px; -fx-padding: 0 0 0 15;");
    }

    private VBox createThemedSection(String headerTitle, String subTitle, String infoText) {
        VBox container = new VBox();
        container.setStyle("-fx-border-color: #0099cc; -fx-border-width: 1; -fx-background-color: white;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(45);
        header.setStyle("-fx-background-color: #0099cc; -fx-padding: 0 20 0 20;");
        Label lblHeader = new Label(headerTitle);
        lblHeader.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        header.getChildren().add(lblHeader);

        VBox body = new VBox(15);
        body.setStyle("-fx-padding: 30;");

        if (subTitle != null) {
            Label lblSub = new Label(subTitle);
            lblSub.setStyle("-fx-font-weight: bold; -fx-text-fill: #444444; -fx-font-size: 20px;");
            body.getChildren().add(lblSub);
        }

        Label lblInfo = new Label(infoText);
        lblInfo.setStyle("-fx-text-fill: #666666; -fx-font-size: 18px;");
        body.getChildren().add(lblInfo);

        container.getChildren().addAll(header, body);
        return container;
    }
}