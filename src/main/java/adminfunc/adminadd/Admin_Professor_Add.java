package adminfunc.adminadd;

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
import java.time.format.DateTimeFormatter;

public class Admin_Professor_Add {

    @FXML private TextField txtFirstName, txtLastName, txtAge, txtEmail, txtPhone,
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
    }

    @FXML
    private void handleSaveProfessor() {
        try {
            // 1. Basic Empty Check
            if (isInputInvalid()) {
                showAlert("Validation Error", "Please fill in all mandatory fields.");
                return;
            }

            String fName = txtFirstName.getText().trim();
            String lName = txtLastName.getText().trim();
            String email = txtEmail.getText().trim();
            int age = Integer.parseInt(txtAge.getText().trim());

            // 2. Name Validation (No numbers allowed - Manual Loop method)
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

            // 3. Email Validation (Must have @)
            if (!email.contains("@")) {
                showAlert("Validation Error", "Email must contain an '@' symbol.");
                return;
            }

            // 4. Age Validation (Must be > 18)
            if (age <= 18) {
                showAlert("Validation Error", "Professor must be older than 18.");
                return;
            }

            // --- Continue with data extraction ---
            String dob = (dpBirthDate.getValue() != null) ? dpBirthDate.getValue().format(DateTimeFormatter.ISO_DATE) : "";
            String phone = txtPhone.getText();
            String gender = comboGender.getValue();
            String address = txtAddress.getText();
            double salary = Double.parseDouble(txtSalary.getText());

            String type = comboType.getValue();
            String title = comboTitle.getValue();
            int experience = Integer.parseInt(txtExperience.getText());
            String office = txtOffice.getText();
            String specialization = txtSpecialization.getText();
            boolean isTenured = checkTenured.isSelected();
            String password = txtPassword.getText();

            Department department = null;
            for (Department dept : admin.getAdminDepartments()) {
                if (dept.getDepartmentName().equals(comboDepartment.getValue())) {
                    department = dept;
                    break;
                }
            }

            Professor newProf = new Professor(
                    fName, lName, dob, email, phone, age,
                    department, type, title, experience, office,
                    specialization, isTenured, gender, password, address, salary
            );

            admin.addAdminProfessors(newProf);
            loginController.saveAdminProfessors();
            loginController.saveAdminUser();

            showAlert("Success", "Professor Registered with Salary: " + salary);
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Age, Experience, and Salary must be valid numbers.");
        } catch (IOException e) {
            showAlert("Storage Error", "Failed to save data.");
        }
    }

    private boolean isInputInvalid() {
        return txtFirstName.getText().isEmpty() ||
                txtLastName.getText().isEmpty() ||
                txtEmail.getText().isEmpty() ||
                txtAge.getText().isEmpty() ||
                txtSalary.getText().isEmpty() ||
                comboDepartment.getValue() == null ||
                txtPassword.getText().isEmpty();
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
            Parent root = FXMLLoader.load(getClass().getResource("/adminfunction/admin_edit_add_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}