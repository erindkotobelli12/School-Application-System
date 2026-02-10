package adminfunc.adminedit;

import Course.Course;
import Course.Degree;
import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import userTypes.Admin;

import java.io.IOException;

public class Admin_Course_Edit {

    @FXML private TextField txtCourseName, txtCourseId, txtTypeCode, txtSemester,
            txtCredit, txtECTS, txtTheoHours, txtLabHours;
    @FXML private CheckBox checkHasLab;
    @FXML private ComboBox<String> comboStatus;

    private Admin admin;
    private LoginController loginController;

    @FXML
    public void initialize() {
        loginController = new LoginController();
        admin = loginController.getAdminUser();

        comboStatus.getItems().addAll("Elective", "Compulsory");
        txtLabHours.disableProperty().bind(checkHasLab.selectedProperty().not());

        // Listener for the Course ID field to auto-populate data
        txtCourseId.textProperty().addListener((obs, oldVal, newVal) -> {
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
            for (Course c : admin.getAdminCourses()) {
                if (c.getCourseID() == searchId) {
                    txtCourseName.setText(c.getCourseName());
                    txtTypeCode.setText(c.getCourseDegree().getDegreeId());
                    txtSemester.setText(String.valueOf(c.getCourseSemester()));
                    txtCredit.setText(String.valueOf(c.getCourseCredit()));
                    txtECTS.setText(String.valueOf(c.getCourseECTS()));
                    txtTheoHours.setText(String.valueOf(c.getCourseTheoHours()));
                    comboStatus.setValue(c.getCourseType());
                    checkHasLab.setSelected(c.isCourseHasLabHours());
                    txtLabHours.setText(String.valueOf(c.getCourseLabHours()));
                    return;
                }
            }
            clearOtherFields();
        } catch (NumberFormatException e) {
            clearOtherFields();
        }
    }

    @FXML
    private void handleUpdateCourse() {
        try {
            int id = Integer.parseInt(txtCourseId.getText());
            Course targetCourse = null;

            for (Course c : admin.getAdminCourses()) {
                if (c.getCourseID() == id) {
                    targetCourse = c;
                    break;
                }
            }

            if (targetCourse == null) {
                showAlert("Error", "Course ID not found.");
                return;
            }

            Degree oldDeg = targetCourse.getCourseDegree();
            String oldType = targetCourse.getCourseType();

            // Update attributes using your Model's setter names
            targetCourse.setCourseName(txtCourseName.getText());
            targetCourse.setCourseSemester(Integer.parseInt(txtSemester.getText()));
            targetCourse.setCourseCredit(Integer.parseInt(txtCredit.getText()));
            targetCourse.setCourseECTS(Integer.parseInt(txtECTS.getText()));
            targetCourse.setCourseType(comboStatus.getValue());
            targetCourse.setCourseTheoHours(Integer.parseInt(txtTheoHours.getText()));
            targetCourse.setCourseHasLabHours(checkHasLab.isSelected());
            targetCourse.setCourseLabHours(checkHasLab.isSelected() ? Integer.parseInt(txtLabHours.getText()) : 0);

            // Logic to move course between Degrees or Required/Elective lists if changed
            String newTypeCode = txtTypeCode.getText();
            if (!oldDeg.getDegreeId().equals(newTypeCode) || !oldType.equals(targetCourse.getCourseType())) {

                // Remove from old degree lists
                if ("Elective".equals(oldType)) oldDeg.removeCourseElective(targetCourse);
                else oldDeg.removeCourseRequired(targetCourse);

                // Find new Degree and add to appropriate list
                Degree newDeg = null;
                for (Degree d : admin.getAdminDegrees()) {
                    if (d.getDegreeId().equals(newTypeCode)) {
                        newDeg = d;
                        break;
                    }
                }

                if (newDeg != null) {
                    targetCourse.setCourseDegree(newDeg);
                    if ("Elective".equals(targetCourse.getCourseType())) newDeg.addCourseElective(targetCourse);
                    else newDeg.addCourseRequired(targetCourse);
                }
            }
            loginController.saveAdminProfessors();
            loginController.saveStudentUser();
            loginController.saveAdminUser();
            showAlert("Success", "Course '" + targetCourse.getCourseName() + "' updated successfully!");

        } catch (Exception e) {
            showAlert("Error", "Please verify all numeric inputs are correct.");
        }
    }

    private void clearOtherFields() {
        txtCourseName.clear(); txtTypeCode.clear(); txtSemester.clear();
        txtCredit.clear(); txtECTS.clear(); txtTheoHours.clear();
        txtLabHours.clear(); checkHasLab.setSelected(false);
        comboStatus.getSelectionModel().clearSelection();
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