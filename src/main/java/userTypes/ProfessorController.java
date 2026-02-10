package userTypes;

import Login.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import java.io.IOException;

public class ProfessorController {
    @FXML private BorderPane mainLayout;
    @FXML private VBox sideBar;

    // New labels for dynamic naming
    @FXML private Label userNameLabel;
    @FXML private Label welcomeNameLabel;

    private boolean isCollapsed = false;

    LoginController a = new LoginController();
    Professor p = a.getProfessorUser();

    @FXML
    public void initialize() {
        if (p != null) {
            // Top bar gets full name
            userNameLabel.setText(p.getFirstName() + " " + p.getLastName());
            welcomeNameLabel.setText(p.getFirstName()+" " + p.getLastName());
        }
    }

    @FXML
    private void toggleSidebar() {
        if (isCollapsed) {
            mainLayout.setLeft(sideBar);
        } else {
            mainLayout.setLeft(null);
        }
        isCollapsed = !isCollapsed;
    }

    @FXML
    private void Profile_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professorfunction/professor_profile_view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Professor Profile");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not find professor_profile_view.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void Classes_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professorfunction/professor_classes_view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Classes");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not find professor_classes_view.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void Transportation_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professorfunction/professor_transportation_view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Transportation");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not find professor_transportation_view.fxml");
            e.printStackTrace();
        }
    }
    @FXML
    private void logout(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Login");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Could not find or load login_view.fxml. check the file path!");
        }
    }
}