package adminfunc.adminadd;

import Course.Classroom;
import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import userTypes.Admin;
import userTypes.Professor;

import java.io.IOException;

public class Admin_Connect_Controller {

    @FXML
    private TextField txtClassroomId;
    @FXML
    private TextField txtProfessorId;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_edit_add_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConnect() {
        String classroomInput = txtClassroomId.getText().trim();
        String profInput = txtProfessorId.getText().trim();

        if (classroomInput.isEmpty() || profInput.isEmpty()) {
            showAlert("Error", "Both Classroom ID and Professor ID are required.");
            return;
        }

        try {
            int classroomId = Integer.parseInt(classroomInput);
            int professorId = Integer.parseInt(profInput);

            LoginController loginController = new LoginController();
            Admin admin = loginController.getAdminUser();


            Classroom targetClassroom = null;
            for (Classroom c : admin.getAdminClassrooms()) {
                if (c.getClassroomID() == classroomId) {
                    targetClassroom = c;
                    break;
                }
            }
            Professor targetProf = null;
            for (Professor p : admin.getAdminProfessors()) {
                if (p.getProfessorId() == professorId) {
                    targetProf = p;
                    break;
                }
            }

            if (targetClassroom == null) {
                showAlert("Error", "Classroom ID " + classroomId + " not found.");
                return;
            }
            if (targetProf == null) {
                showAlert("Error", "Professor ID " + professorId + " not found.");
                return;
            }


            if (targetProf.getProfessorGivesLecture().contains(targetClassroom)) {
                showAlert("Error", "This professor is already assigned to this classroom.");
                return;
            }


            targetProf.addClassesToProfessor(targetClassroom);
            loginController.saveAdminProfessors();
            loginController.saveAdminUser();

            showAlert("Success", "Classroom " + classroomId + " assigned to Professor " + targetProf.getFirstName());

            txtClassroomId.clear();
            txtProfessorId.clear();

        } catch (NumberFormatException | IOException e) {
            showAlert("Input Error", "Please enter valid numeric IDs.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}