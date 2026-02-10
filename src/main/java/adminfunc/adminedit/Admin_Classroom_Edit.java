package adminfunc.adminedit;

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

public class Admin_Classroom_Edit {

    @FXML private TextField txtClassroomId;
    @FXML private TextField txtClassroomName;
    @FXML private TextField txtClassId;

    private Admin admin;
    private LoginController loginController;

    @FXML
    public void initialize() {
        loginController = new LoginController();
        admin = loginController.getAdminUser();

        txtClassroomId.textProperty().addListener((obs, oldVal, newVal) -> {
            autoPopulate(newVal);
        });
    }

    private void autoPopulate(String idStr) {
        if (idStr == null || idStr.isEmpty()) {
            clearOtherFields();
            return;
        }

        try {
            int searchId = Integer.parseInt(idStr);
            for (Classroom c : admin.getAdminClassrooms()) {
                if (c.getClassroomID() == searchId) {
                    txtClassroomName.setText(c.getClassroomName());
                    if (c.getClassroomCourse() != null) {
                        txtClassId.setText(String.valueOf(c.getClassroomCourse().getCourseID()));
                    }
                    return;
                }
            }
            clearOtherFields();
        } catch (NumberFormatException e) {
            clearOtherFields();
        }
    }

    @FXML
    private void handleUpdateClassroom() {
        try {
            int id = Integer.parseInt(txtClassroomId.getText());
            Classroom targetClassroom = null;

            for (Classroom c : admin.getAdminClassrooms()) {
                if (c.getClassroomID() == id) {
                    targetClassroom = c;
                    break;
                }
            }

            if (targetClassroom == null) {
                showAlert("Error", "Classroom ID not found.");
                return;
            }

            int newCourseId = Integer.parseInt(txtClassId.getText());
            Course oldCourse = targetClassroom.getClassroomCourse();
            Course newCourseObj = null;

            for (Course c : admin.getAdminCourses()) {
                if (c.getCourseID() == newCourseId) {
                    newCourseObj = c;
                    break;
                }
            }

            if (newCourseObj == null) {
                showAlert("Error", "New Course ID not found.");
                return;
            }

            // Reference Logic: Move classroom from old course list to new course list
            if (oldCourse == null || oldCourse.getCourseID() != newCourseId) {
                if (oldCourse != null) {
                    oldCourse.removeClassroom(targetClassroom);
                }
                newCourseObj.addClassroom(targetClassroom);
            }

            // Update attributes
            targetClassroom.setClassroomName(txtClassroomName.getText());
            targetClassroom.setClassroomCourse(newCourseObj);

            loginController.saveAdminUser();
            loginController.saveAdminProfessors();
            loginController.saveStudentUser();
            showAlert("Success", "Classroom updated successfully!");

        } catch (Exception e) {
            showAlert("Error", "Please verify all numeric inputs.");
        }
    }

    private void clearOtherFields() {
        txtClassroomName.clear();
        txtClassId.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminfunction/admin_edit_edit_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}