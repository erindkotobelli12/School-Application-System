package adminfunc.adminview;

import Course.Classroom;
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
import userTypes.Professor;

import java.io.IOException;

public class View_Professor_Controller {

    @FXML private TextField txtSearch;
    @FXML private ListView<String> professorListView;
    @FXML private VBox professorDetailsPane;

    // Professor info labels
    @FXML private Label lblProfessorId;
    @FXML private Label lblProfessorName;
    @FXML private Label lblEmail;
    @FXML private Label lblPhone;
    @FXML private Label lblDepartment;
    @FXML private Label lblType;
    @FXML private Label lblTitle;
    @FXML private Label lblExperience;
    @FXML private Label lblOffice;
    @FXML private Label lblSpecialization;
    @FXML private Label lblTenured;
    @FXML private Label lblSalary;

    // Salary section
    @FXML private TextField txtSalaryAmount;
    @FXML private Button btnUpdateSalary;

    // Classes table
    @FXML private TableView<Classroom> classesTable;
    @FXML private TableColumn<Classroom, Integer> colClassroomId;
    @FXML private TableColumn<Classroom, String> colCourseName;

    private Admin admin;
    private Professor selectedProfessor;
    private ObservableList<String> allProfessorNames;

    @FXML
    public void initialize() {
        admin = LoginController.getAdminUser();

        // Hide details pane initially
        professorDetailsPane.setVisible(false);
        professorDetailsPane.setManaged(false);

        // Load all professors
        loadAllProfessors();

        // Setup search filter
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> filterProfessors(newVal));

        // Setup list selection listener
        professorListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadProfessorDetails(newVal);
            }
        });

        // Setup table columns
        colClassroomId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getClassroomID()).asObject());
        colCourseName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getClassroomCourse() != null ?
                                cellData.getValue().getClassroomCourse().getCourseName() : "N/A"));
    }

    private void loadAllProfessors() {
        allProfessorNames = FXCollections.observableArrayList();

        for (Professor p : admin.getAdminProfessors()) {
            String displayName = p.getFirstName() + " " + p.getLastName() + " (ID: " + p.getProfessorId() + ")";
            allProfessorNames.add(displayName);
        }

        professorListView.setItems(allProfessorNames);
    }

    private void filterProfessors(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            professorListView.setItems(allProfessorNames);
            return;
        }

        ObservableList<String> filtered = FXCollections.observableArrayList();
        String lowerSearch = searchText.toLowerCase();

        for (String name : allProfessorNames) {
            if (name.toLowerCase().contains(lowerSearch)) {
                filtered.add(name);
            }
        }

        professorListView.setItems(filtered);
    }

    private void loadProfessorDetails(String selectedName) {
        // Extract professor ID from the display name
        int startIdx = selectedName.indexOf("(ID: ") + 5;
        int endIdx = selectedName.indexOf(")", startIdx);
        int professorId = Integer.parseInt(selectedName.substring(startIdx, endIdx));

        // Find the professor
        selectedProfessor = null;
        for (Professor p : admin.getAdminProfessors()) {
            if (p.getProfessorId() == professorId) {
                selectedProfessor = p;
                break;
            }
        }

        if (selectedProfessor == null) return;

        // Show details pane
        professorDetailsPane.setVisible(true);
        professorDetailsPane.setManaged(true);

        // Populate basic info
        lblProfessorId.setText(String.valueOf(selectedProfessor.getProfessorId()));
        lblProfessorName.setText(selectedProfessor.getFirstName() + " " + selectedProfessor.getLastName());
        lblEmail.setText(selectedProfessor.getEmail());
        lblPhone.setText(selectedProfessor.getPhoneNumber());
        lblDepartment.setText(selectedProfessor.getProfessorDepartment() != null ?
                selectedProfessor.getProfessorDepartment().getDepartmentName() : "N/A");
        lblType.setText(selectedProfessor.getProfessorType());
        lblTitle.setText(selectedProfessor.getProfessorTitle());
        lblExperience.setText(selectedProfessor.getProfessorYearsOfExperience() + " years");
        lblOffice.setText(selectedProfessor.getProfessorOfficeLocation());
        lblSpecialization.setText(selectedProfessor.getProfessorSpecialization());
        lblTenured.setText(selectedProfessor.isProfessorIsTenured() ? "Yes" : "No");
        lblSalary.setText(String.format("€%.2f", selectedProfessor.getProfessorSalary()));

        // Load classes
        ObservableList<Classroom> classes = FXCollections.observableArrayList(
                selectedProfessor.getProfessorGivesLecture());
        classesTable.setItems(classes);
    }

    @FXML
    private void handleUpdateSalary() {
        System.out.println("Button clicked!");

        if (selectedProfessor == null) {
            System.out.println("ERROR: No professor selected");
            showAlert("Error", "No professor selected.");
            return;
        }

        System.out.println("Professor selected: " + selectedProfessor.getProfessorId());

        try {
            String salaryText = txtSalaryAmount.getText();
            System.out.println("Salary text entered: " + salaryText);

            double newSalary = Double.parseDouble(salaryText);
            System.out.println("Parsed salary: " + newSalary);

            if (newSalary < 0) {
                showAlert("Error", "Salary must be positive.");
                return;
            }

            Admin currentAdmin = LoginController.getAdminUser();
            System.out.println("Got admin, professors count: " + currentAdmin.getAdminProfessors().size());

            for (int i = 0; i < currentAdmin.getAdminProfessors().size(); i++) {
                if (currentAdmin.getAdminProfessors().get(i).getProfessorId() == selectedProfessor.getProfessorId()) {
                    System.out.println("Found professor at index: " + i);
                    System.out.println("Old salary: " + currentAdmin.getAdminProfessors().get(i).getProfessorSalary());

                    currentAdmin.getAdminProfessors().get(i).setProfessorSalary(newSalary);

                    System.out.println("New salary set: " + currentAdmin.getAdminProfessors().get(i).getProfessorSalary());
                    selectedProfessor = currentAdmin.getAdminProfessors().get(i);
                    break;
                }
            }

            System.out.println("About to save...");
            LoginController.setProfessorUser(selectedProfessor);
            LoginController.saveAdminProfessors();
            System.out.println("Save completed!");

            loadProfessorDetails(professorListView.getSelectionModel().getSelectedItem());
            txtSalaryAmount.clear();

            showAlert("Success", "Salary updated!");

        } catch (Exception e) {
            System.out.println("EXCEPTION CAUGHT: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed: " + e.getMessage());
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
}