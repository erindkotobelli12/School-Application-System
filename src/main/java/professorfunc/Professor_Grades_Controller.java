package professorfunc;

import Course.Classroom;
import Course.Course;
import Login.LoginController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import userTypes.Professor;
import userTypes.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Professor_Grades_Controller {

    @FXML private Label professorNameLabel;
    @FXML private Label lblClassroomName;
    @FXML private Label lblCourseName;
    @FXML private ListView<String> classListView;

    @FXML private TableView<Student> gradesTable;
    @FXML private TableColumn<Student, String> colStudentId;
    @FXML private TableColumn<Student, String> colStudentName;
    @FXML private TableColumn<Student, Double> colMidterm;
    @FXML private TableColumn<Student, Double> colFinal;
    @FXML private TableColumn<Student, Double> colFinalGrade;

    private Classroom currentClassroom;
    private Professor p;

    // Store grades temporarily in a map: studentId -> [midterm, final]
    private Map<Integer, double[]> tempGrades = new HashMap<>();

    @FXML
    public void initialize() {
        p = LoginController.getProfessorUser();

        // Get the professor from Admin's list to ensure we have current classroom data
        var admin = LoginController.getAdminUser();
        if (admin != null && p != null) {
            for (Professor prof : admin.getAdminProfessors()) {
                if (prof.getProfessorId() == p.getProfessorId()) {
                    p = prof;
                    break;
                }
            }
        }

        if (p != null) {
            professorNameLabel.setText(p.getFirstName() + " " + p.getLastName());
            System.out.println("DEBUG: Professor " + p.getProfessorId() + " has " +
                    p.getProfessorGivesLecture().size() + " classrooms");
            loadClassrooms();
        } else {
            System.out.println("Error: Professor object is null. Check LoginController.");
        }

        setupTableColumns();

        // Listener for selecting a class from the list
        classListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                var admin2 = LoginController.getAdminUser();
                currentClassroom = null;

                for (Classroom c : admin2.getAdminClassrooms()) {
                    if (c.getClassroomName().equals(newVal)) {
                        currentClassroom = c;
                        System.out.println("DEBUG: Selected classroom '" + c.getClassroomName() +
                                "' with " + c.getClassroomStudents().size() + " students");
                        break;
                    }
                }

                if (currentClassroom != null) {
                    lblClassroomName.setText(currentClassroom.getClassroomName());
                    if (currentClassroom.getClassroomCourse() != null) {
                        lblCourseName.setText(currentClassroom.getClassroomCourse().getCourseName());
                    }
                    refreshGrades(currentClassroom);
                } else {
                    System.err.println("ERROR: Classroom '" + newVal + "' not found in admin's classroom list!");
                }
            }
        });
    }

    private void setupTableColumns() {
        gradesTable.setEditable(true);

        // Student ID
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        // Student Name
        colStudentName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));

        // --- Midterm Column ---
        colMidterm.setCellValueFactory(cellData -> {
            Student s = cellData.getValue();
            int studentId = s.getStudentId();

            // Check temp grades first
            if (tempGrades.containsKey(studentId)) {
                return new SimpleObjectProperty<>(tempGrades.get(studentId)[0]);
            }

            // Otherwise get from student object
            int courseIdx = getCourseIndexForStudent(s);
            if (courseIdx != -1 && s.getCurrentCourseGrades().size() > courseIdx) {
                ArrayList<Double> grades = s.getCurrentCourseGrades().get(courseIdx);
                return new SimpleObjectProperty<>(grades.size() > 0 ? grades.get(0) : 0.0);
            }
            return new SimpleObjectProperty<>(0.0);
        });

        colMidterm.setCellFactory(col -> new EditingCell(true));
        colMidterm.setEditable(true);

        // --- Final Exam Column ---
        colFinal.setCellValueFactory(cellData -> {
            Student s = cellData.getValue();
            int studentId = s.getStudentId();

            // Check temp grades first
            if (tempGrades.containsKey(studentId)) {
                return new SimpleObjectProperty<>(tempGrades.get(studentId)[1]);
            }

            // Otherwise get from student object
            int courseIdx = getCourseIndexForStudent(s);
            if (courseIdx != -1 && s.getCurrentCourseGrades().size() > courseIdx) {
                ArrayList<Double> grades = s.getCurrentCourseGrades().get(courseIdx);
                return new SimpleObjectProperty<>(grades.size() > 1 ? grades.get(1) : 0.0);
            }
            return new SimpleObjectProperty<>(0.0);
        });

        colFinal.setCellFactory(col -> new EditingCell(false));
        colFinal.setEditable(true);

        // --- Final Grade Column (read-only, calculated) ---
        colFinalGrade.setCellValueFactory(cellData -> {
            Student s = cellData.getValue();
            int studentId = s.getStudentId();

            double midterm = 0.0;
            double finalExam = 0.0;

            // Get grades from temp storage or student object
            if (tempGrades.containsKey(studentId)) {
                midterm = tempGrades.get(studentId)[0];
                finalExam = tempGrades.get(studentId)[1];
            } else {
                int courseIdx = getCourseIndexForStudent(s);
                if (courseIdx != -1 && s.getCurrentCourseGrades().size() > courseIdx) {
                    ArrayList<Double> grades = s.getCurrentCourseGrades().get(courseIdx);
                    if (grades.size() >= 2) {
                        midterm = grades.get(0);
                        finalExam = grades.get(1);
                    }
                }
            }

            // Calculate final grade
            double midtermWeight = 0.4;
            double finalWeight = 0.6;

            if (currentClassroom != null && currentClassroom.getClassroomCourse() != null) {
                ArrayList<Double> distribution = currentClassroom.getClassroomCourse().getCourseGradeDistribution();
                if (distribution != null && distribution.size() >= 2) {
                    midtermWeight = distribution.get(0);
                    finalWeight = distribution.get(1);
                }
            }

            double finalGrade = (midterm * midtermWeight) + (finalExam * finalWeight);
            return new SimpleObjectProperty<>(Math.round(finalGrade * 100.0) / 100.0);
        });
        colFinalGrade.setEditable(false);
        colFinalGrade.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
    }

    // Custom editing cell
    private class EditingCell extends TableCell<Student, Double> {
        private TextField textField;
        private boolean isMidterm;

        public EditingCell(boolean isMidterm) {
            this.isMidterm = isMidterm;
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
                textField.requestFocus();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem() == null ? "" : String.format("%.2f", getItem()));
            setGraphic(null);
        }

        @Override
        public void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    commitValue();
                }
            });

            textField.setOnAction(evt -> commitValue());
        }

        private void commitValue() {
            try {
                double value = Double.parseDouble(textField.getText());
                Student student = getTableRow().getItem();

                if (student != null) {
                    int studentId = student.getStudentId();

                    // Get current grades or create new entry
                    double[] grades = tempGrades.getOrDefault(studentId, new double[2]);

                    // If new entry, initialize with current values
                    if (!tempGrades.containsKey(studentId)) {
                        int courseIdx = getCourseIndexForStudent(student);
                        if (courseIdx != -1 && student.getCurrentCourseGrades().size() > courseIdx) {
                            ArrayList<Double> currentGrades = student.getCurrentCourseGrades().get(courseIdx);
                            if (currentGrades.size() >= 2) {
                                grades[0] = currentGrades.get(0);
                                grades[1] = currentGrades.get(1);
                            }
                        }
                    }

                    // Update the appropriate grade
                    if (isMidterm) {
                        grades[0] = value;
                        System.out.println("DEBUG: Temp midterm for student " + studentId + " = " + value);
                    } else {
                        grades[1] = value;
                        System.out.println("DEBUG: Temp final for student " + studentId + " = " + value);
                    }

                    tempGrades.put(studentId, grades);
                    commitEdit(value);
                    gradesTable.refresh(); // Refresh to update final grade
                }
            } catch (NumberFormatException e) {
                cancelEdit();
            }
        }

        private String getString() {
            return getItem() == null ? "0.0" : String.format("%.2f", getItem());
        }
    }

    private int getCourseIndexForStudent(Student s) {
        if (currentClassroom == null || currentClassroom.getClassroomCourse() == null) return -1;
        int targetCourseID = currentClassroom.getClassroomCourse().getCourseID();

        for (int i = 0; i < s.getStudentCurrentCourses().size(); i++) {
            if (s.getStudentCurrentCourses().get(i).getCourseID() == targetCourseID) {
                return i;
            }
        }
        return -1;
    }

    private void loadClassrooms() {
        var admin = LoginController.getAdminUser();
        if (admin == null) return;

        ObservableList<String> names = FXCollections.observableArrayList();

        for (Classroom c : admin.getAdminClassrooms()) {
            if (p.getProfessorGivesLecture().contains(c)) {
                names.add(c.getClassroomName());
                System.out.println("DEBUG: Added classroom '" + c.getClassroomName() +
                        "' with " + c.getClassroomStudents().size() + " students");
            }
        }

        if (names.isEmpty()) {
            System.out.println("WARNING: No classrooms found for professor " + p.getProfessorId());
        }

        classListView.setItems(names);
    }

    private void refreshGrades(Classroom classroom) {
        var admin = LoginController.getAdminUser();
        ArrayList<Student> students = classroom.getClassroomStudents();

        // Clear temp grades when switching classrooms
        tempGrades.clear();

        if (students == null || students.isEmpty()) {
            System.out.println("Debug: No students found in " + classroom.getClassroomName());
            gradesTable.setItems(FXCollections.observableArrayList());
        } else {
            System.out.println("Debug: Loading " + students.size() + " students into table.");

            ArrayList<Student> masterStudents = new ArrayList<>();

            for (Student classroomStudent : students) {
                for (Student adminStudent : admin.getAdminStudents()) {
                    if (adminStudent.getStudentId() == classroomStudent.getStudentId()) {
                        ensureGradeArraysInitialized(adminStudent);
                        masterStudents.add(adminStudent);
                        break;
                    }
                }
            }

            gradesTable.setItems(FXCollections.observableArrayList(masterStudents));
            gradesTable.refresh();
        }
    }

    private void ensureGradeArraysInitialized(Student student) {
        if (currentClassroom == null || currentClassroom.getClassroomCourse() == null) return;

        System.out.println("\n=== ENSURING GRADE ARRAYS FOR STUDENT " + student.getStudentId() + " ===");
        System.out.println("Student's current courses: " + student.getStudentCurrentCourses().size());
        System.out.println("Student's CurrentCourseGrades size: " + student.getCurrentCourseGrades().size());

        int courseIdx = getCourseIndexForStudent(student);
        System.out.println("Course index found: " + courseIdx);

        if (courseIdx == -1) {
            System.err.println("ERROR: Course not found in student's course list!");
            System.out.println("Looking for course ID: " + currentClassroom.getClassroomCourse().getCourseID());
            System.out.println("Student's courses:");
            for (int i = 0; i < student.getStudentCurrentCourses().size(); i++) {
                System.out.println("  [" + i + "] Course ID: " + student.getStudentCurrentCourses().get(i).getCourseID());
            }

            // FORCE ADD: If the course isn't in the student's list but they're in the classroom, add it
            System.out.println("FORCE ADDING COURSE to student's list...");
            student.studentAddCurrentCourse(currentClassroom.getClassroomCourse());
            courseIdx = student.getStudentCurrentCourses().size() - 1;
            System.out.println("Course added at index: " + courseIdx);
        }

        // Make sure CurrentCourseGrades has enough entries
        while (student.getCurrentCourseGrades().size() <= courseIdx) {
            ArrayList<Double> newGradeArray = new ArrayList<>();
            newGradeArray.add(0.0); // Midterm
            newGradeArray.add(0.0); // Final
            student.getCurrentCourseGrades().add(newGradeArray);
            System.out.println("DEBUG: Created missing grade array at index " + (student.getCurrentCourseGrades().size() - 1));
        }

        // Make sure the grade array at this index has 2 slots
        ArrayList<Double> grades = student.getCurrentCourseGrades().get(courseIdx);
        while (grades.size() < 2) {
            grades.add(0.0);
            System.out.println("DEBUG: Added grade slot, now has " + grades.size() + " slots");
        }

        System.out.println("Final CurrentCourseGrades size: " + student.getCurrentCourseGrades().size());
        System.out.println("Grades at index " + courseIdx + ": " + grades);
        System.out.println("=== END INITIALIZATION ===\n");
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            if (currentClassroom == null) {
                showAlert("Error", "No classroom selected", Alert.AlertType.ERROR);
                return;
            }

            if (tempGrades.isEmpty()) {
                showAlert("Info", "No grades have been modified.", Alert.AlertType.INFORMATION);
                return;
            }

            var admin = LoginController.getAdminUser();

            System.out.println("========================================");
            System.out.println("DEBUG: TRANSFERRING TEMP GRADES TO STUDENT OBJECTS");
            System.out.println("Number of students with modified grades: " + tempGrades.size());

            int successCount = 0;

            // Transfer all temp grades to the ACTUAL student objects in admin's master list
            for (Map.Entry<Integer, double[]> entry : tempGrades.entrySet()) {
                int studentId = entry.getKey();
                double[] tempValues = entry.getValue();

                System.out.println("\n--- Processing Student ID: " + studentId + " ---");
                System.out.println("Temp Midterm: " + tempValues[0]);
                System.out.println("Temp Final: " + tempValues[1]);

                // Find the actual student in admin's master list
                Student actualStudent = null;
                for (Student adminStudent : admin.getAdminStudents()) {
                    if (adminStudent.getStudentId() == studentId) {
                        actualStudent = adminStudent;
                        System.out.println("Found student in admin list (hash: " + System.identityHashCode(adminStudent) + ")");
                        break;
                    }
                }

                if (actualStudent == null) {
                    System.err.println("ERROR: Student " + studentId + " not found in admin's master list!");
                    continue;
                }

                // Find which course index this is for this student
                int courseIdx = getCourseIndexForStudent(actualStudent);
                System.out.println("Course index in student's course list: " + courseIdx);

                if (courseIdx == -1) {
                    System.err.println("ERROR: Course not found in student's current courses!");
                    System.err.println("Student has " + actualStudent.getStudentCurrentCourses().size() + " courses");
                    System.err.println("Looking for course ID: " + currentClassroom.getClassroomCourse().getCourseID());

                    // FORCE ADD the course if it's missing
                    System.out.println("FORCE ADDING course to student...");
                    actualStudent.studentAddCurrentCourse(currentClassroom.getClassroomCourse());
                    courseIdx = actualStudent.getStudentCurrentCourses().size() - 1;
                    System.out.println("Course added at index: " + courseIdx);
                }

                // Ensure the student has the grade arrays
                while (actualStudent.getCurrentCourseGrades().size() <= courseIdx) {
                    ArrayList<Double> newGradeArray = new ArrayList<>();
                    newGradeArray.add(0.0);
                    newGradeArray.add(0.0);
                    actualStudent.getCurrentCourseGrades().add(newGradeArray);
                    System.out.println("Added missing grade array at index " + (actualStudent.getCurrentCourseGrades().size() - 1));
                }

                ArrayList<Double> grades = actualStudent.getCurrentCourseGrades().get(courseIdx);

                // Ensure grade array has space for midterm and final
                while (grades.size() < 2) {
                    grades.add(0.0);
                    System.out.println("Added empty grade slot");
                }

                // NOW TRANSFER THE TEMP VALUES TO THE ACTUAL STUDENT OBJECT
                double oldMidterm = grades.get(0);
                double oldFinal = grades.get(1);

                grades.set(0, tempValues[0]);
                grades.set(1, tempValues[1]);

                System.out.println("BEFORE: Midterm=" + oldMidterm + ", Final=" + oldFinal);
                System.out.println("AFTER:  Midterm=" + grades.get(0) + ", Final=" + grades.get(1));
                System.out.println("✓ Successfully transferred to Student object");

                successCount++;
            }

            System.out.println("\n========================================");
            System.out.println("Successfully updated " + successCount + " students");
            System.out.println("Now saving to disk...");

            // Save to disk
            LoginController.saveAdminUser();
            LoginController.saveStudentUser();

            System.out.println("✓ Saved to disk");
            System.out.println("========================================\n");

            // Clear temp grades after successful save
            tempGrades.clear();

            // Refresh the table to show the saved values from Student objects
            gradesTable.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Grades saved successfully for " + successCount + " students!");
            alert.showAndWait();

        } catch (IOException e) {
            showAlert("Save Error", "Failed to save grades: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            System.err.println("Exception during save:");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "Unexpected error: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            System.err.println("Unexpected exception:");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/professorfunction/professor_classes_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (IOException e) {
            System.out.println("Error: Navigation FXML not found.");
        }
    }
}