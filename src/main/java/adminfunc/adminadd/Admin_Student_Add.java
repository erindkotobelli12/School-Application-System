package adminfunc.adminadd;

import Course.Degree;
import Course.Department;
import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userTypes.Admin;
import userTypes.Student;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class Admin_Student_Add {

    @FXML private TextField txtFirstName, txtLastName, txtAge, txtEmail, txtPhone, txtEnrollYear, txtAddress;
    @FXML private PasswordField txtPassword;
    @FXML private DatePicker dpBirthDate;
    @FXML private ComboBox<String> comboGender, comboStatus;
    @FXML private ComboBox<String> comboDept;
    @FXML private ComboBox<String> comboDegree;
    @FXML private CheckBox checkIsActive;

    @FXML
    public void initialize() {
        comboGender.getItems().addAll("Male", "Female");
        comboStatus.getItems().addAll("Married", "Single");

        LoginController a = new LoginController();
        Admin admin = a.getAdminUser();

        if (admin != null) {
            if (admin.getAdminDepartments() != null) {
                for (Department dept : admin.getAdminDepartments()) {
                    comboDept.getItems().add(dept.getDepartmentName());
                }
            }
            if (admin.getAdminDegrees() != null) {
                for (Degree degree : admin.getAdminDegrees()) {
                    comboDegree.getItems().add(degree.getDegreeName());
                }
            }
        }
    }

    @FXML
    private void handleSaveStudent() {
        try {
            LoginController a = new LoginController();
            Admin admin = a.getAdminUser();

            // 1. Collect Data and Trim Whitespace
            String fName = txtFirstName.getText().trim();
            String lName = txtLastName.getText().trim();
            String email = txtEmail.getText().trim();
            String ageStr = txtAge.getText().trim();
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();
            String password = txtPassword.getText();
            String enrollYearStr = txtEnrollYear.getText().trim();

            // 2. Validation: Empty Fields
            if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || ageStr.isEmpty() ||
                    address.isEmpty() || password.isEmpty() || enrollYearStr.isEmpty() ||
                    comboDept.getValue() == null || comboDegree.getValue() == null) {

                showAlert("Error", "Please fill in all required fields.");
                return;
            }

            // 3. Validation: Names (No Numbers Allowed)
            for (char c : fName.toCharArray()) {
                if (!Character.isLetter(c) && !Character.isSpaceChar(c)) {
                    showAlert("Validation Error", "First Name must contain only letters.");
                    return;
                }
            }

            for (char c : lName.toCharArray()) {
                if (!Character.isLetter(c) && !Character.isSpaceChar(c)) {
                    showAlert("Validation Error", "Last Name must contain only letters.");
                    return;
                }
            }

            // 4. Validation: Email (Must contain @)
            if (!email.contains("@")) {
                showAlert("Validation Error", "Please enter a valid email address containing '@'.");
                return;
            }

            // 5. Validation: Age (Must be greater than 18)
            int age = Integer.parseInt(ageStr);
            if (age <= 18) {
                showAlert("Validation Error", "Student must be older than 18 years of age.");
                return;
            }

            // 6. Resolve Department and Degree Objects
            Department dept = null;
            for (Department d : admin.getAdminDepartments()) {
                if (comboDept.getValue().equals(d.getDepartmentName())) {
                    dept = d;
                    break;
                }
            }

            Degree degree = null;
            for (Degree deg : admin.getAdminDegrees()) {
                if (comboDegree.getValue().equals(deg.getDegreeName())) {
                    degree = deg;
                    break;
                }
            }

            // 7. Process Remaining Data
            String dob = (dpBirthDate.getValue() != null) ? dpBirthDate.getValue().format(DateTimeFormatter.ISO_DATE) : "";
            String gender = comboGender.getValue();
            int enrollYear = Integer.parseInt(enrollYearStr);
            String status = comboStatus.getValue();
            boolean isActive = checkIsActive.isSelected();

            // 8. Create and Save Student
            Student newStudent = new Student(
                    fName, lName, dob, email, phone, age,
                    dept, degree, enrollYear, status, isActive, gender, password, address
            );

            admin.addAdminStudents(newStudent);

            // Link student to the degree object
            for (Degree i : admin.getAdminDegrees()) {
                if (comboDegree.getValue().equals(i.getDegreeName())) {
                    i.addStudentDegree(newStudent);
                    break;
                }
            }

            a.saveStudentUser();
            a.saveAdminUser();

            showAlert("Success", "Student registered successfully! ID: " + newStudent.getStudentId());
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Age and Enrollment Year must be valid numbers.");
        } catch (IOException e) {
            showAlert("System Error", "An error occurred while saving the data.");
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
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        txtFirstName.clear(); txtLastName.clear(); txtAge.clear();
        txtEmail.clear(); txtPhone.clear(); txtEnrollYear.clear();
        txtAddress.clear(); txtPassword.clear(); dpBirthDate.setValue(null);
        comboGender.getSelectionModel().clearSelection();
        comboStatus.getSelectionModel().clearSelection();
        comboDept.getSelectionModel().clearSelection();
        comboDegree.getSelectionModel().clearSelection();
        checkIsActive.setSelected(false);
    }
}