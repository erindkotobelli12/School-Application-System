package adminfunc.admindelete;

import Course.Classroom;
import Course.Degree;
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
import userTypes.Student;

import java.io.IOException;
import java.util.Optional;

public class Admin_Student_Delete {

    @FXML
    private TextField txtStudentId;

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
    private void handleDeleteStudent() {
        String studentIdStr = txtStudentId.getText().trim();

        // 1. Validate Input
        if (studentIdStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid Student ID.");
            return;
        }

        // 2. Confirmation Dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Action: Delete Student ID " + studentIdStr);
        confirm.setContentText("Warning: This action is permanent. All enrollment history and grades for this student will be deleted.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int id = Integer.parseInt(studentIdStr);
                LoginController a = new LoginController();
                Admin admin = a.getAdminUser();

                // 1. Remove from the Admin's master student list
                boolean removed = admin.getAdminStudents().removeIf(s -> s.getStudentId() == id);

                if (removed) {
                    // 2. Remove from all Classrooms
                    for (Classroom c : admin.getAdminClassrooms()) {
                        c.getClassroomStudents().removeIf(st -> st.getStudentId() == id);
                    }

                    // 3. Remove from all Degrees
                    for (Degree degree : admin.getAdminDegrees()) {
                        degree.getDegreeStudents().removeIf(sd -> sd.getStudentId() == id);
                    }

                    // 4. Save changes
                    a.saveAdminUser();
                    a.saveAdminProfessors(); // If applicable
                    a.saveStudentUser();

                    txtStudentId.clear();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Student ID " + id + " has been successfully removed.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Not Found", "No student found with ID: " + id);
                }

            }catch (NumberFormatException | IOException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid ID", "Student ID must be a numeric value.");
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