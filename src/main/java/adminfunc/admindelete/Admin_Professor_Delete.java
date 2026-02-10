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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import userTypes.Admin;
import userTypes.Professor;

import java.io.IOException;
import java.util.Optional;

public class Admin_Professor_Delete {

    @FXML
    private TextField txtProfessorId;

    @FXML
    private Button btnDelete;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Adjust this path to match your project's Admin Dashboard location
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_delete_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Admin");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not return to the dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteProfessor() {
        String professorIdStr = txtProfessorId.getText().trim();

        if (professorIdStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid Professor ID.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Action: Delete Professor ID " + professorIdStr);
        confirm.setContentText("Are you sure? This will remove the professor from the system and unassign them from their lectures.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int id = Integer.parseInt(professorIdStr);
                Admin admin = LoginController.getAdminUser();

                boolean removed = false;
                Professor toRemove = null;

                // Find professor
                for (Professor p : admin.getAdminProfessors()) {
                    if (p.getProfessorId() == id) {
                        toRemove = p;
                        break;
                    }
                }

                if (toRemove != null) {
                    // Remove professor's classrooms from admin classroom list
                    for (Classroom c : toRemove.getProfessorGivesLecture()) {
                        admin.getAdminClassrooms().remove(c);
                    }
                    // Remove professor
                    admin.getAdminProfessors().remove(toRemove);
                    removed = true;
                }

                if (removed) {
                    LoginController.saveAdminUser();
                    LoginController.saveAdminProfessors();
                    LoginController.saveStudentUser();
                    txtProfessorId.clear();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Professor ID " + id + " has been removed.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Not Found", "No professor found with ID: " + id);
                }

            } catch (NumberFormatException | IOException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid ID", "Professor ID must be a number.");
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