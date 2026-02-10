package adminfunc.adminadd;

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

public class Admin_Course_Add {

    @FXML private TextField txtCourseName, txtCourseId, txtTypeCode, txtSemester,
            txtCredit, txtECTS, txtTheoHours, txtLabHours;

    @FXML private CheckBox checkHasLab;
    @FXML private ComboBox<String> comboStatus;

    @FXML
    public void initialize() {
        // Logic to disable lab hours if checkbox is not selected
        txtLabHours.disableProperty().bind(checkHasLab.selectedProperty().not());

        // Fill the combo box with types
        comboStatus.getItems().addAll("Elective", "Compulsory");
    }

    @FXML
    private void handleSaveCourse() {
        try {
            // 1. Get values from UI
            String name = txtCourseName.getText();
            String selectedType = comboStatus.getValue();

            // 2. Validation
            if (name.isEmpty() || selectedType == null || txtCourseId.getText().isEmpty() || txtTypeCode.getText().isEmpty()) {
                showAlert("Input Error", "Please fill in Course Name, ID, Degree ID, and Type.");
                return;
            }

            // 3. Parsing numeric inputs
            int id = Integer.parseInt(txtCourseId.getText());
            String degreeTypeCode = txtTypeCode.getText(); // Degree ID from UI
            int semester = Integer.parseInt(txtSemester.getText());
            int credits = Integer.parseInt(txtCredit.getText());
            int ects = Integer.parseInt(txtECTS.getText());
            int theoHours = Integer.parseInt(txtTheoHours.getText());
            boolean hasLab = checkHasLab.isSelected();
            int labHours = hasLab ? Integer.parseInt(txtLabHours.getText()) : 0;
            // 4. Handle Data Logic
            LoginController a = new LoginController();
            Admin admin = a.getAdminUser();

            if (admin.getAdminDegrees().isEmpty()) {
                showAlert("System Error", "No Degrees exist. Create a Degree first.");
                return;
            }

            Degree deg = null;
            for (Degree d : admin.getAdminDegrees()) {
                if (d.getDegreeId().equals(degreeTypeCode)) {
                    deg = d;
                    break;
                }
            }

            if (deg == null) {
                showAlert("Input Error", "Degree ID '" + degreeTypeCode + "' does not exist.");
                return;
            }

            // 5. Create Course Object (Matching your Model Constructor exactly)
            // Constructor: (String name, int ID, Degree deg, int sem, int credit, int ects, String type, boolean hasLab, int labHrs, int theoHrs)
            Course newCourse = new Course(name, id, deg, semester, credits,
                    ects, selectedType, hasLab, labHours, theoHours);

            // 6. Add to Degree lists based on type
            if ("Elective".equals(selectedType)) {
                deg.addCourseElective(newCourse);
            } else if ("Compulsory".equals(selectedType)) {
                deg.addCourseRequired(newCourse);
            }

            // 7. Add to Admin's global list and save
            admin.addAdminCourses(newCourse);
            a.saveAdminUser();


            showAlert("Success", "Course '" + name + "' created and assigned to " + deg.getDegreeName());
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for ID, Semester, Credits, ECTS, and Hours.");
        } catch (IOException e) {
            showAlert("File Error", "Could not save data to the database.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_edit_add_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Could not return to the previous screen.");
        }
    }

    private void clearFields() {
        txtCourseName.clear(); txtCourseId.clear(); txtTypeCode.clear();
        txtSemester.clear(); txtCredit.clear(); txtECTS.clear();
        txtTheoHours.clear(); txtLabHours.clear();
        checkHasLab.setSelected(false);
        comboStatus.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}