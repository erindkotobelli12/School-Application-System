package adminfunc.adminadd;

import Course.Classroom;
import Course.Course;
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
import java.io.IOException;

public class Admin_Classroom_Add {

    @FXML private TextField txtClassroomId;
    @FXML private TextField txtClassroomName; // New field
    @FXML private TextField txtClassId;

    @FXML
    private void handleSaveClassroom() {
        String idStr = txtClassroomId.getText().trim();
        String name = txtClassroomName.getText().trim();
        String courseIdStr = txtClassId.getText().trim();

        if (idStr.isEmpty() || name.isEmpty() || courseIdStr.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        try {
            int classroomId = Integer.parseInt(idStr);
            int courseId = Integer.parseInt(courseIdStr);

            LoginController lc = new LoginController();
            Admin admin = lc.getAdminUser();

            // Find Course object
            Course selectedCourse = null;
            for (Course c : admin.getAdminCourses()) {
                if (c.getCourseID() == courseId) {
                    selectedCourse = c;
                    break;
                }
            }

            if (selectedCourse == null) {
                showAlert("Error", "Course ID " + courseId + " not found.");
                return;
            }

            // Use New Constructor: Classroom(id, name, course)
            Classroom newClassroom = new Classroom(classroomId, name, selectedCourse);

            admin.addAdminClassrooms(newClassroom);
            for(Course course : admin.getAdminCourses()) {
                if(course.getCourseID() == courseId) {
                    course.addClassroom(newClassroom);
                }
            }
            lc.saveAdminUser();

            showAlert("Success", "Classroom " + name + " created successfully.");
            clearFields();

        } catch (NumberFormatException | IOException e) {
            showAlert("Input Error", "IDs must be valid numbers.");
        }
    }

    private void clearFields() {
        txtClassroomId.clear();
        txtClassroomName.clear();
        txtClassId.clear();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminfunction/admin_edit_add_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}