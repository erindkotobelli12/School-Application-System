package adminfunc.admindelete;

import Course.Classroom;
import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import userTypes.Admin;
import userTypes.Professor;

import java.io.IOException;
import java.util.Optional;

public class Admin_Disconnect {

    @FXML
    private TextField txtClassroomId;

    @FXML
    private TextField txtProfessorId;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Updated path to be consistent with your project structure
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_delete_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("Admin - Delete Management");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Navigation failed. Check if admin_delete_view.fxml exists in /resources/adminfunction/");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDisconnect() {
        String classroomIdStr = txtClassroomId.getText().trim();
        String professorIdStr = txtProfessorId.getText().trim();

        if (classroomIdStr.isEmpty() || professorIdStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter both IDs.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Disconnection");
        confirm.setHeaderText("Remove Classroom " + classroomIdStr + " from Professor " + professorIdStr);
        confirm.setContentText("Are you sure you want to remove this classroom from the professor's schedule?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Admin admin = LoginController.getAdminUser();
                int targetClassId = Integer.parseInt(classroomIdStr);
                int targetProfId = Integer.parseInt(professorIdStr);

                boolean success = false;
                for (Professor p : admin.getAdminProfessors()) {
                    if (p.getProfessorId() == targetProfId) {
                        // Use removeIf to avoid ConcurrentModificationException
                        success = p.getProfessorGivesLecture().removeIf(c -> c.getClassroomID() == targetClassId);
                        break;
                    }
                }

                if (success) {
                    LoginController.saveAdminUser();
                    LoginController.saveStudentUser();
                    LoginController.saveAdminProfessors();
                    txtClassroomId.clear();
                    txtProfessorId.clear();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Classroom disconnected successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Connection not found. Verify IDs.");
                }

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "IDs must be numeric.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Data Error", "Could not save changes.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}