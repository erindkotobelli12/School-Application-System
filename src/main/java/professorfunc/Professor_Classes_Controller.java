package professorfunc;

import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import userTypes.Professor;
import java.io.IOException;

public class Professor_Classes_Controller {

    @FXML private Label professorNameLabel;

    LoginController loginController = new LoginController();
    Professor p = loginController.getProfessorUser();

    @FXML
    public void initialize() {
        if (p != null) {
            professorNameLabel.setText(p.getFirstName() + " " + p.getLastName());
        }
    }

    @FXML
    private void handleAttendance(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/professorfunction/professor_attendance_view.fxml"));
            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Could not find professor_attendance.fxml");
        }
    }

    @FXML
    private void handleGrades(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/professorfunction/professor_grades_view.fxml"));
            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Could not find professor_grades.fxml");
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/professor_view.fxml"));
            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Could not find professor_view.fxml");
        }
    }
}