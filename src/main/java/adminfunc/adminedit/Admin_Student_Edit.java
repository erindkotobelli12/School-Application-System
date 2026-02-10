package adminfunc.adminedit;

import Course.Degree;
import Course.Department;
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
import userTypes.Student;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Admin_Student_Edit {

    @FXML private TextField txtSearchId, txtFirstName, txtLastName, txtAge, txtEmail, txtPhone, txtEnrollYear, txtAddress;
    @FXML private PasswordField txtPassword;
    @FXML private DatePicker dpBirthDate;
    @FXML private ComboBox<String> comboGender, comboStatus, comboDept, comboDegree;
    @FXML private CheckBox checkIsActive;

    private Admin admin;
    private LoginController loginController;

    @FXML
    public void initialize() {
        loginController = new LoginController();
        admin = loginController.getAdminUser();

        // Populate Dropdowns
        comboGender.getItems().addAll("Male", "Female");
        comboStatus.getItems().addAll("Married", "Single");

        if (admin != null) {
            if (admin.getAdminDepartments() != null) {
                for (Department d : admin.getAdminDepartments()) comboDept.getItems().add(d.getDepartmentName());
            }
            if (admin.getAdminDegrees() != null) {
                for (Degree d : admin.getAdminDegrees()) comboDegree.getItems().add(d.getDegreeName());
            }
        }

        txtSearchId.textProperty().addListener((obs, oldVal, newVal) -> autoPopulate(newVal));
    }

    private void autoPopulate(String idStr) {
        if (idStr == null || idStr.isEmpty()) {
            clearFields();
            return;
        }

        try {
            int searchId = Integer.parseInt(idStr);
            for (Student s : admin.getAdminStudents()) {
                if (s.getStudentId() == searchId) {
                    txtFirstName.setText(s.getFirstName());
                    txtLastName.setText(s.getLastName());
                    txtAge.setText(String.valueOf(s.getAge()));
                    txtEmail.setText(s.getEmail());
                    txtPhone.setText(s.getPhoneNumber());
                    txtPassword.setText(s.getPassword());
                    txtAddress.setText(s.getAddress());
                    comboGender.setValue(s.getGender());
                    txtEnrollYear.setText(String.valueOf(s.getStudentEnrollmentYear()));
                    comboStatus.setValue(s.getStudentEnrollmentStatus());
                    checkIsActive.setSelected(s.isStudentIsActive());

                    if (s.getStudentDepartment() != null)
                        comboDept.setValue(s.getStudentDepartment().getDepartmentName());
                    if (s.getStudentDegree() != null)
                        comboDegree.setValue(s.getStudentDegree().getDegreeName());

                    if (s.getDateOfBirth() != null && !s.getDateOfBirth().isEmpty()) {
                        dpBirthDate.setValue(LocalDate.parse(s.getDateOfBirth(), DateTimeFormatter.ISO_DATE));
                    }
                    return;
                }
            }
            clearFields();
        } catch (Exception e) {
            clearFields();
        }
    }

    @FXML
    private void handleUpdateStudent() {
        try {
            // 1. Basic Setup
            int searchId = Integer.parseInt(txtSearchId.getText());
            Student target = null;
            for (Student s : admin.getAdminStudents()) {
                if (s.getStudentId() == searchId) {
                    target = s;
                    break;
                }
            }

            if (target == null) {
                showAlert("Error", "Student not found.");
                return;
            }

            // 2. Validation: Capture and Clean Inputs
            String fName = txtFirstName.getText().trim();
            String lName = txtLastName.getText().trim();
            String email = txtEmail.getText().trim();
            String ageStr = txtAge.getText().trim();

            if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || ageStr.isEmpty()) {
                showAlert("Validation Error", "Please fill in all mandatory fields.");
                return;
            }

            // 3. Validation: Names (No Numbers - Manual Loop)
            for (char c : fName.toCharArray()) {
                if (Character.isDigit(c)) {
                    showAlert("Validation Error", "First Name cannot contain numbers.");
                    return;
                }
            }
            for (char c : lName.toCharArray()) {
                if (Character.isDigit(c)) {
                    showAlert("Validation Error", "Last Name cannot contain numbers.");
                    return;
                }
            }

            // 4. Validation: Email (@ check)
            if (!email.contains("@")) {
                showAlert("Validation Error", "Email must contain an '@' symbol.");
                return;
            }

            // 5. Validation: Age (> 18 check)
            int age = Integer.parseInt(ageStr);
            if (age <= 18) {
                showAlert("Validation Error", "Student must be older than 18.");
                return;
            }

            // 6. Update Object Data
            target.setFirstName(fName);
            target.setLastName(lName);
            target.setAge(age);
            target.setEmail(email);
            target.setPhoneNumber(txtPhone.getText());
            target.setGender(comboGender.getValue());
            target.setAddress(txtAddress.getText());

            if (dpBirthDate.getValue() != null) {
                target.setDateOfBirth(dpBirthDate.getValue().format(DateTimeFormatter.ISO_DATE));
            }

            target.setStudentEnrollmentYear(Integer.parseInt(txtEnrollYear.getText()));
            target.setStudentEnrollmentStatus(comboStatus.getValue());
            target.setStudentIsActive(checkIsActive.isSelected());
            target.setPassword(txtPassword.getText());

            for (Department d : admin.getAdminDepartments()) {
                if (d.getDepartmentName().equals(comboDept.getValue())) {
                    target.setStudentDepartment(d);
                    break;
                }
            }
            for (Degree deg : admin.getAdminDegrees()) {
                if (deg.getDegreeName().equals(comboDegree.getValue())) {
                    target.setStudentDegree(deg);
                    break;
                }
            }

            // 7. Save Everything
            loginController.saveStudentUser();
            loginController.saveAdminUser();
            showAlert("Success", "Student (ID: " + searchId + ") updated successfully!");

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please verify Age and Enrollment Year are numeric.");
        } catch (IOException e) {
            showAlert("Storage Error", "Failed to save the updated student data.");
        }
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
        txtPassword.clear(); txtAddress.clear();
        dpBirthDate.setValue(null);
        comboGender.getSelectionModel().clearSelection();
        comboStatus.getSelectionModel().clearSelection();
        comboDept.getSelectionModel().clearSelection();
        comboDegree.getSelectionModel().clearSelection();
        checkIsActive.setSelected(true);
    }
}