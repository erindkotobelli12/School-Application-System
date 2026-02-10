package studentfunc;

import Course.Course;
import Course.Classroom;
import Login.LoginController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import userTypes.Admin;
import userTypes.Student;
import java.io.IOException;
import java.util.ArrayList;

public class Student_Courses_Controller {

    private Admin a = LoginController.getAdminUser();
    private Student s = LoginController.getStudentUser();
    private ArrayList<Course> selectedCourses = (s != null) ? s.getStudentCurrentCourses() : new ArrayList<>();
    private int totalWorkloadCredits = 0;

    @FXML private Label studentNameLabel, creditCounterLabel, validationMessage;
    @FXML private Button confirmButton;
    @FXML private TableView<Course> currentSemesterTable, electiveTable, pastCoursesTable;
    @FXML private TableColumn<Course, Boolean> colCurrentSelect, colElectiveSelect, colPastSelect;
    @FXML private TableColumn<Course, Integer> colCurrentID, colCurrentECTS, colCurrentCredits, colElectiveID, colElectiveECTS, colElectiveCredits;
    @FXML private TableColumn<Course, String> colCurrentName, colCurrentType, colElectiveName, colElectiveType;
    @FXML private TableColumn<Course, Integer> colPastID, colPastCredits, colPastSem;
    @FXML private TableColumn<Course, String> colPastName, colPastGrade;

    @FXML
    public void initialize() {
        setupSelectionColumn(colCurrentSelect);
        setupSelectionColumn(colElectiveSelect);
        setupSelectionColumn(colPastSelect);  // Make past courses selectable too!
        linkColumns(colCurrentID, colCurrentName, colCurrentECTS, colCurrentCredits, colCurrentType);
        linkColumns(colElectiveID, colElectiveName, colElectiveECTS, colElectiveCredits, colElectiveType);

        colPastID.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        colPastName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colPastCredits.setCellValueFactory(new PropertyValueFactory<>("courseCredit"));
        colPastSem.setCellValueFactory(new PropertyValueFactory<>("courseSemester"));

        // Custom cell factory for grades - retrieve from student's final grades
        colPastGrade.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            int courseIndex = s.getStudentCourseFinished().indexOf(course);

            if (courseIndex != -1) {
                // Calculate the actual index in studentFinalGrades
                int gradeIndex = s.getStudentCurrentCourses().size() + courseIndex;

                if (gradeIndex < s.getStudentFinalGrades().size()) {
                    double grade = s.getStudentFinalGrades().get(gradeIndex);
                    return new javafx.beans.property.SimpleStringProperty(
                            grade == 0.0 ? "-" : String.format("%.1f", grade)
                    );
                }
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        if (s != null) {
            studentNameLabel.setText(s.getFirstName().toLowerCase() + " " + s.getLastName().toLowerCase());
            loadStudentDashboard();
        }
    }

    private void setupSelectionColumn(TableColumn<Course, Boolean> column) {
        column.setCellFactory(CheckBoxTableCell.forTableColumn(column));
        column.setEditable(true);
        column.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            SimpleBooleanProperty property = new SimpleBooleanProperty(selectedCourses.contains(course));
            property.addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    if (!selectedCourses.contains(course)) selectedCourses.add(course);
                } else {
                    selectedCourses.remove(course);
                }
                calculateCredits();
                updateValidation();
            });
            return property;
        });
    }

    private void linkColumns(TableColumn<Course, Integer> id, TableColumn<Course, String> name,
                             TableColumn<Course, Integer> ects, TableColumn<Course, Integer> credits,
                             TableColumn<Course, String> type) {
        id.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        name.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        ects.setCellValueFactory(new PropertyValueFactory<>("courseECTS"));
        credits.setCellValueFactory(new PropertyValueFactory<>("courseCredit"));
        type.setCellValueFactory(new PropertyValueFactory<>("courseType"));
    }

    /**
     * Helper method to check if a course has already been completed by the student
     */
    private boolean isCourseFinished(Course course) {
        if (s.getStudentCourseFinished() == null) return false;

        for (Course finished : s.getStudentCourseFinished()) {
            if (finished.getCourseID() == course.getCourseID()) {
                return true;
            }
        }
        return false;
    }

    public void loadStudentDashboard() {
        calculateCredits();
        int targetSem = (Admin.getEnrollmentYear() - s.getStudentEnrollmentYear()) * 2 + Admin.getCurrentSemester();

        System.out.println("========================================");
        System.out.println("DEBUG: Loading dashboard for student " + s.getStudentId());
        System.out.println("DEBUG: Target semester: " + targetSem);
        System.out.println("========================================");

        // Clear tables
        currentSemesterTable.getItems().clear();
        electiveTable.getItems().clear();
        pastCoursesTable.getItems().clear();

        // Populate current semester required courses (show ALL for current semester EXCEPT finished ones)
        int requiredCount = 0;
        if (s.getStudentDegree().getRequiredCourses() != null) {
            for (Course c : s.getStudentDegree().getRequiredCourses()) {
                // Only show if semester matches AND course NOT already finished
                if (c.getCourseSemester() == targetSem && !isCourseFinished(c)) {
                    currentSemesterTable.getItems().add(c);
                    requiredCount++;
                    System.out.println("✓ Added required course: " + c.getCourseName());
                } else if (c.getCourseSemester() == targetSem && isCourseFinished(c)) {
                    System.out.println("✗ Skipped finished required course: " + c.getCourseName() + " (already completed)");
                }
            }
        }

        // Populate elective courses (show ALL that match EXCEPT finished ones)
        int electiveCount = 0;
        if (s.getStudentDegree().getElectiveCourses() != null) {
            for (Course c : s.getStudentDegree().getElectiveCourses()) {
                boolean semesterMatch = (c.getCourseSemester() == targetSem || c.getCourseSemester() == 0);
                // Only show if semester matches AND course NOT already finished
                if (semesterMatch && !isCourseFinished(c)) {
                    electiveTable.getItems().add(c);
                    electiveCount++;
                    System.out.println("✓ Added elective course: " + c.getCourseName());
                } else if (semesterMatch && isCourseFinished(c)) {
                    System.out.println("✗ Skipped finished elective course: " + c.getCourseName() + " (already completed)");
                }
            }
        }

        // Populate past courses - NOW SELECTABLE for retaking!
        int pastCount = 0;
        if (s.getStudentCourseFinished() != null) {
            pastCount = s.getStudentCourseFinished().size();
            pastCoursesTable.getItems().addAll(s.getStudentCourseFinished());
            System.out.println("✓ Added " + pastCount + " finished courses (retake available)");
        }

        System.out.println("\n=== TABLE SUMMARY ===");
        System.out.println("Required courses shown: " + requiredCount);
        System.out.println("Elective courses shown: " + electiveCount);
        System.out.println("Past courses shown: " + pastCount);
        System.out.println("=====================\n");

        // Refresh tables to show checkboxes correctly
        currentSemesterTable.refresh();
        electiveTable.refresh();
        pastCoursesTable.refresh();

        updateValidation();
    }

    private void calculateCredits() {
        totalWorkloadCredits = 0;
        for (Course c : selectedCourses) totalWorkloadCredits += c.getCourseCredit();
    }

    private void updateValidation() {
        creditCounterLabel.setText("Total Credits: " + totalWorkloadCredits);
        confirmButton.setDisable(totalWorkloadCredits > 45 || totalWorkloadCredits < 30);
        validationMessage.setText(confirmButton.isDisabled() ? "Invalid Load (30-45)" : "Ready to Confirm");
    }

    @FXML
    private void handleConfirm() throws IOException {
        if (selectedCourses.isEmpty()) return;

        Admin admin = LoginController.getAdminUser();

        // Find the Official Student object within the Admin's records
        Student officialStudent = null;
        for (Student st : admin.getAdminStudents()) {
            if (st.getStudentId() == s.getStudentId()) {
                officialStudent = st;
                break;
            }
        }
        if (officialStudent == null) {
            System.err.println("WARNING: Student not found in admin records!");
            return;
        }

        // Clear existing classroom entries for this student
        for (Classroom cl : admin.getAdminClassrooms()) {
            cl.removeStudent(officialStudent);
        }

        // IMPORTANT: Clear all current course data properly
        officialStudent.clearCurrentCourses();

        // Re-enroll in classrooms for all selected courses
        for (Course selected : selectedCourses) {
            boolean enrolled = false;

            // Use the proper method to add course (this initializes all arrays correctly)
            officialStudent.studentAddCurrentCourse(selected);

            System.out.println("DEBUG: Added course " + selected.getCourseID() +
                    " - Student now has " + officialStudent.getStudentNrOfCurrentCourses() + " courses");
            System.out.println("DEBUG: CurrentCourseGrades size: " + officialStudent.getCurrentCourseGrades().size());

            // Find and enroll in classroom
            for (Classroom classroom : admin.getAdminClassrooms()) {
                if (classroom.getClassroomCourse().getCourseID() == selected.getCourseID()) {
                    classroom.addStudent(officialStudent);
                    System.out.println("DEBUG: Enrolled student " + officialStudent.getStudentId() +
                            " in classroom " + classroom.getClassroomName() +
                            " (now has " + classroom.getClassroomStudents().size() + " students)");
                    enrolled = true;
                    break;
                }
            }

            if (!enrolled) {
                System.err.println("WARNING: No classroom found for course ID " + selected.getCourseID());
            }
        }

        // Update local reference
        s = officialStudent;
        selectedCourses = s.getStudentCurrentCourses();

        // CRITICAL: Save Admin first (contains classrooms), then Student
        LoginController.saveAdminUser();
        LoginController.saveStudentUser();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Enrollment saved successfully. " + selectedCourses.size() + " courses enrolled.");
        alert.showAndWait();
        loadStudentDashboard();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/student_view.fxml"))));
        } catch (Exception e) { e.printStackTrace(); }
    }
}