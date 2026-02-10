package adminfunc.adminview;

import Course.Course;
import Login.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import userTypes.Admin;
import userTypes.Student;

import java.io.IOException;

public class View_Student_Controller {

    @FXML private TextField txtSearch;
    @FXML private ListView<String> studentListView;
    @FXML private VBox studentDetailsPane;

    // Student info labels
    @FXML private Label lblStudentId;
    @FXML private Label lblStudentName;
    @FXML private Label lblEmail;
    @FXML private Label lblPhone;
    @FXML private Label lblDepartment;
    @FXML private Label lblDegree;
    @FXML private Label lblEnrollmentYear;
    @FXML private Label lblStatus;
    @FXML private Label lblGPA;
    @FXML private Label lblTotalCost;
    @FXML private Label lblPaidAmount;
    @FXML private Label lblRemainingAmount;

    // Payment section
    @FXML private TextField txtPaymentAmount;
    @FXML private Button btnAddPayment;

    // Current courses table
    @FXML private TableView<Course> currentCoursesTable;
    @FXML private TableColumn<Course, Integer> colCourseID;
    @FXML private TableColumn<Course, String> colCourseName;
    @FXML private TableColumn<Course, Integer> colCredits;

    // Finished courses table
    @FXML private TableView<CourseWithGrade> finishedCoursesTable;
    @FXML private TableColumn<CourseWithGrade, Integer> colFinishedID;
    @FXML private TableColumn<CourseWithGrade, String> colFinishedName;
    @FXML private TableColumn<CourseWithGrade, Integer> colFinishedCredits;
    @FXML private TableColumn<CourseWithGrade, String> colGrade;

    private Admin admin;
    private Student selectedStudent;
    private ObservableList<String> allStudentNames;

    @FXML
    public void initialize() {
        admin = LoginController.getAdminUser();

        // Hide details pane initially
        studentDetailsPane.setVisible(false);
        studentDetailsPane.setManaged(false);

        // Load all students
        loadAllStudents();

        // Setup search filter
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> filterStudents(newVal));

        // Setup list selection listener
        studentListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadStudentDetails(newVal);
            }
        });

        // Setup table columns
        colCourseID.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCourseID()).asObject());
        colCourseName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCourseName()));
        colCredits.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCourseCredit()).asObject());

        colFinishedID.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCourse().getCourseID()).asObject());
        colFinishedName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCourse().getCourseName()));
        colFinishedCredits.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCourse().getCourseCredit()).asObject());
        colGrade.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGrade()));
    }

    private void loadAllStudents() {
        allStudentNames = FXCollections.observableArrayList();

        for (Student s : admin.getAdminStudents()) {
            String displayName = s.getFirstName() + " " + s.getLastName() + " (ID: " + s.getStudentId() + ")";
            allStudentNames.add(displayName);
        }

        studentListView.setItems(allStudentNames);
    }

    private void filterStudents(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            studentListView.setItems(allStudentNames);
            return;
        }

        ObservableList<String> filtered = FXCollections.observableArrayList();
        String lowerSearch = searchText.toLowerCase();

        for (String name : allStudentNames) {
            if (name.toLowerCase().contains(lowerSearch)) {
                filtered.add(name);
            }
        }

        studentListView.setItems(filtered);
    }

    private void loadStudentDetails(String selectedName) {
        // Extract student ID from the display name
        int startIdx = selectedName.indexOf("(ID: ") + 5;
        int endIdx = selectedName.indexOf(")", startIdx);
        int studentId = Integer.parseInt(selectedName.substring(startIdx, endIdx));

        // Find the student
        selectedStudent = null;
        for (Student s : admin.getAdminStudents()) {
            if (s.getStudentId() == studentId) {
                selectedStudent = s;
                break;
            }
        }

        if (selectedStudent == null) return;

        // Show details pane
        studentDetailsPane.setVisible(true);
        studentDetailsPane.setManaged(true);

        // Populate basic info
        lblStudentId.setText(String.valueOf(selectedStudent.getStudentId()));
        lblStudentName.setText(selectedStudent.getFirstName() + " " + selectedStudent.getLastName());
        lblEmail.setText(selectedStudent.getEmail());
        lblPhone.setText(selectedStudent.getPhoneNumber());
        lblDepartment.setText(selectedStudent.getStudentDepartment() != null ?
                selectedStudent.getStudentDepartment().getDepartmentName() : "N/A");
        lblDegree.setText(selectedStudent.getStudentDegree() != null ?
                selectedStudent.getStudentDegree().getDegreeName() : "N/A");
        lblEnrollmentYear.setText(String.valueOf(selectedStudent.getStudentEnrollmentYear()));
        lblStatus.setText(selectedStudent.getStudentEnrollmentStatus());
        lblGPA.setText(String.format("%.2f", selectedStudent.getStudentGPA()));

        // Populate financial info
        double degreeCost = selectedStudent.getStudentDegree() != null ?
                selectedStudent.getStudentDegree().getDegreeCost() : 0.0;
        double paidAmount = selectedStudent.getAMTpayed();
        double remaining = degreeCost - paidAmount;

        lblTotalCost.setText(String.format("€%.2f", degreeCost));
        lblPaidAmount.setText(String.format("€%.2f", paidAmount));
        lblRemainingAmount.setText(String.format("€%.2f", remaining));

        // Change color based on payment status
        if (remaining <= 0) {
            lblRemainingAmount.setStyle("-fx-text-fill: #5cb85c; -fx-font-weight: bold;");
        } else {
            lblRemainingAmount.setStyle("-fx-text-fill: #d9534f; -fx-font-weight: bold;");
        }

        // Load current courses
        ObservableList<Course> currentCourses = FXCollections.observableArrayList(
                selectedStudent.getStudentCurrentCourses());
        currentCoursesTable.setItems(currentCourses);

        // Load finished courses with grades
        ObservableList<CourseWithGrade> finishedCourses = FXCollections.observableArrayList();
        int currentCoursesCount = selectedStudent.getStudentCurrentCourses().size();

        for (int i = 0; i < selectedStudent.getStudentCourseFinished().size(); i++) {
            Course course = selectedStudent.getStudentCourseFinished().get(i);
            int gradeIndex = currentCoursesCount + i;

            String gradeStr = "-";
            if (gradeIndex < selectedStudent.getStudentFinalGrades().size()) {
                double grade = selectedStudent.getStudentFinalGrades().get(gradeIndex);
                gradeStr = grade == 0.0 ? "F" : String.format("%.1f", grade);
            }

            finishedCourses.add(new CourseWithGrade(course, gradeStr));
        }
        finishedCoursesTable.setItems(finishedCourses);
    }

    @FXML
    private void handleAddPayment() {
        if (selectedStudent == null) {
            showAlert("Error", "No student selected.");
            return;
        }

        try {
            double amount = Double.parseDouble(txtPaymentAmount.getText());
            if (amount <= 0) {
                showAlert("Error", "Amount must be positive.");
                return;
            }

            System.out.println("=== BEFORE UPDATE ===");
            System.out.println("Selected Student ID: " + selectedStudent.getStudentId());
            System.out.println("Current payment: " + selectedStudent.getAMTpayed());

            // Calculate new total
            double oldAmount = selectedStudent.getAMTpayed();
            double newTotal = oldAmount + amount;

            System.out.println("Adding: " + amount);
            System.out.println("Should become: " + newTotal);

            // Update the student in Admin's list
            Admin currentAdmin = LoginController.getAdminUser();
            boolean found = false;

            if (currentAdmin != null) {
                for (int i = 0; i < currentAdmin.getAdminStudents().size(); i++) {
                    if (currentAdmin.getAdminStudents().get(i).getStudentId() == selectedStudent.getStudentId()) {
                        System.out.println("Found student in admin list at index: " + i);
                        System.out.println("Admin's student current payment: " + currentAdmin.getAdminStudents().get(i).getAMTpayed());

                        currentAdmin.getAdminStudents().get(i).setAMTpayed(newTotal);
                        selectedStudent = currentAdmin.getAdminStudents().get(i);

                        System.out.println("After update in admin list: " + currentAdmin.getAdminStudents().get(i).getAMTpayed());
                        found = true;
                        break;
                    }
                }
            }

            System.out.println("Student found in admin list: " + found);
            System.out.println("selectedStudent payment now: " + selectedStudent.getAMTpayed());

            // Save to file
            LoginController.setStudentUser(selectedStudent);
            LoginController.saveStudentUser();

            System.out.println("=== AFTER SAVE ===");
            System.out.println("Checking admin list again...");
            for (Student s : currentAdmin.getAdminStudents()) {
                if (s.getStudentId() == selectedStudent.getStudentId()) {
                    System.out.println("Student in admin list now has: " + s.getAMTpayed());
                    break;
                }
            }

            // Refresh UI
            loadStudentDetails(studentListView.getSelectionModel().getSelectedItem());

            txtPaymentAmount.clear();
            showAlert("Success", "Payment added: €" + amount + "\nNew Total: €" + newTotal);

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number.");
        } catch (IOException e) {
            showAlert("Error", "Save failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminfunction/admin_view_view.fxml"));
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

    // Helper class for finished courses with grades
    public static class CourseWithGrade {
        private Course course;
        private String grade;

        public CourseWithGrade(Course course, String grade) {
            this.course = course;
            this.grade = grade;
        }

        public Course getCourse() { return course; }
        public String getGrade() { return grade; }
    }
}