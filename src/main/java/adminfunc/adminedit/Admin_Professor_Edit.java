package adminfunc.adminedit;

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
import userTypes.Professor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Admin_Professor_Edit {

    @FXML private TextField txtSearchId, txtFirstName, txtLastName, txtAge, txtEmail, txtPhone,
            txtExperience, txtOffice, txtSpecialization, txtAddress, txtSalary;
    @FXML private PasswordField txtPassword;
    @FXML private DatePicker dpBirthDate;
    @FXML private ComboBox<String> comboGender, comboType, comboDepartment, comboTitle;
    @FXML private CheckBox checkTenured;

    private Admin admin;
    private LoginController loginController;

    @FXML
    public void initialize() {
        loginController = new LoginController();
        admin = loginController.getAdminUser();

        comboGender.getItems().addAll("Male", "Female");
        comboType.getItems().addAll("Professor", "Assistant Professor");
        comboTitle.getItems().addAll("Dr.", "Prof.", "Msc.");

        if (admin != null && admin.getAdminDepartments() != null) {
            for (Department dept : admin.getAdminDepartments()) {
                comboDepartment.getItems().add(dept.getDepartmentName());
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
            for (Professor p : admin.getAdminProfessors()) {
                if (p.getProfessorId() == searchId) {
                    txtFirstName.setText(p.getFirstName());
                    txtLastName.setText(p.getLastName());
                    txtEmail.setText(p.getEmail());
                    txtPhone.setText(p.getPhoneNumber());
                    txtAge.setText(String.valueOf(p.getAge()));
                    txtPassword.setText(p.getPassword());
                    txtAddress.setText(p.getAddress());
                    txtSalary.setText(String.valueOf(p.getProfessorSalary()));
                    comboGender.setValue(p.getGender());

                    comboTitle.setValue(p.getProfessorTitle());
                    comboType.setValue(p.getProfessorType());
                    txtExperience.setText(String.valueOf(p.getProfessorYearsOfExperience()));
                    txtOffice.setText(p.getProfessorOfficeLocation());
                    txtSpecialization.setText(p.getProfessorSpecialization());
                    checkTenured.setSelected(p.isProfessorIsTenured());

                    if (p.getProfessorDepartment() != null) {
                        comboDepartment.setValue(p.getProfessorDepartment().getDepartmentName());
                    }

                    if (p.getDateOfBirth() != null && !p.getDateOfBirth().isEmpty()) {
                        dpBirthDate.setValue(LocalDate.parse(p.getDateOfBirth(), DateTimeFormatter.ISO_DATE));
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
    private void handleUpdateProfessor() {
        try {
            // 1. Identify Target
            int searchId = Integer.parseInt(txtSearchId.getText());
            Professor target = null;

            for (Professor p : admin.getAdminProfessors()) {
                if (p.getProfessorId() == searchId) {
                    target = p;
                    break;
                }
            }

            if (target == null) {
                showAlert("Error", "No professor found.");
                return;
            }

            // 2. Collect and Clean Inputs
            String fName = txtFirstName.getText().trim();
            String lName = txtLastName.getText().trim();
            String email = txtEmail.getText().trim();
            String ageStr = txtAge.getText().trim();
            String salaryStr = txtSalary.getText().trim();

            if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || ageStr.isEmpty() || salaryStr.isEmpty()) {
                showAlert("Validation Error", "Please fill in all mandatory fields.");
                return;
            }

            // 3. Name Validation (No numbers - Manual Loop)
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

            // 4. Email Validation (@ check)
            if (!email.contains("@")) {
                showAlert("Validation Error", "Email must contain an '@' symbol.");
                return;
            }

            // 5. Age Validation (> 18 check)
            int age = Integer.parseInt(ageStr);
            if (age <= 18) {
                showAlert("Validation Error", "Professor must be older than 18.");
                return;
            }

            // 6. Update Object Fields
            target.setFirstName(fName);
            target.setLastName(lName);
            target.setAge(age);
            target.setEmail(email);
            target.setAddress(txtAddress.getText());
            target.setProfessorSalary(Double.parseDouble(salaryStr));

            target.setProfessorTitle(comboTitle.getValue());
            target.setProfessorYearsOfExperience(Integer.parseInt(txtExperience.getText()));
            target.setProfessorOfficeLocation(txtOffice.getText());
            target.setProfessorSpecialization(txtSpecialization.getText());
            target.setProfessorType(comboType.getValue());
            target.setPassword(txtPassword.getText());
            target.setProfessorIsTenured(checkTenured.isSelected());
            target.setPhoneNumber(txtPhone.getText());

            if (dpBirthDate.getValue() != null) {
                target.setDateOfBirth(dpBirthDate.getValue().format(DateTimeFormatter.ISO_DATE));
            }

            // 7. Save to Database/File
            loginController.saveStudentUser();
            loginController.saveAdminUser();
            loginController.saveAdminProfessors();

            showAlert("Success", "Professor Profile (ID: " + searchId + ") updated!");

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please verify Age, Salary, and Experience are numeric.");
        } catch (IOException e) {
            showAlert("Storage Error", "Failed to save data to system.");
        }
    }

    private void clearFields() {
        txtFirstName.clear(); txtLastName.clear(); txtAge.clear();
        txtEmail.clear(); txtPhone.clear(); txtExperience.clear();
        txtOffice.clear(); txtSpecialization.clear(); txtPassword.clear();
        txtAddress.clear(); txtSalary.clear();
        dpBirthDate.setValue(null);
        comboGender.getSelectionModel().clearSelection();
        comboType.getSelectionModel().clearSelection();
        comboTitle.getSelectionModel().clearSelection();
        comboDepartment.getSelectionModel().clearSelection();
        checkTenured.setSelected(false);
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
        } catch (IOException e) { e.printStackTrace(); }
    }
}