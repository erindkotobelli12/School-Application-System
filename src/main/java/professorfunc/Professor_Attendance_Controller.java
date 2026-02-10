package professorfunc;

import Course.Classroom;
import Course.Course;
import Login.LoginController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import userTypes.Professor;
import userTypes.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;

public class Professor_Attendance_Controller {

    @FXML private Label professorNameLabel;
    @FXML private Label lblClassroomName;
    @FXML private Label lblCourseName;
    @FXML private Label lblSessionType;
    @FXML private Label lblTotalSessions;
    @FXML private ListView<String> classListView;
    @FXML private ComboBox<String> sessionTypeCombo;
    @FXML private ComboBox<String> sessionNumberCombo;

    @FXML private TableView<Student> attendanceTable;
    @FXML private TableColumn<Student, String> colStudentId;
    @FXML private TableColumn<Student, String> colStudentName;
    @FXML private TableColumn<Student, Void> colAttendance;
    @FXML private TableColumn<Student, String> colStatus;

    private Classroom currentClassroom;
    private Professor p;

    // Store attendance: studentId -> present(true/false)
    private Map<Integer, Boolean> attendanceMap = new HashMap<>();

    @FXML
    public void initialize() {
        p = LoginController.getProfessorUser();

        // Get the professor from Admin's list
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
            loadClassrooms();
        } else {
            System.out.println("Error: Professor object is null.");
        }

        // Setup session type combo box
        sessionTypeCombo.setItems(FXCollections.observableArrayList("Lecture", "Lab"));
        sessionTypeCombo.setValue("Lecture");

        setupTableColumns();

        // Listener for selecting a class from the list
        classListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                var admin2 = LoginController.getAdminUser();
                currentClassroom = null;

                for (Classroom c : admin2.getAdminClassrooms()) {
                    if (c.getClassroomName().equals(newVal)) {
                        currentClassroom = c;
                        break;
                    }
                }

                if (currentClassroom != null) {
                    lblClassroomName.setText(currentClassroom.getClassroomName());
                    if (currentClassroom.getClassroomCourse() != null) {
                        Course course = currentClassroom.getClassroomCourse();
                        lblCourseName.setText(course.getCourseName());

                        // Update session options based on course
                        updateSessionOptions(course);
                    }
                    refreshAttendance();
                }
            }
        });

        // Update session numbers when session type changes
        sessionTypeCombo.setOnAction(e -> {
            if (currentClassroom != null && currentClassroom.getClassroomCourse() != null) {
                updateSessionOptions(currentClassroom.getClassroomCourse());
                // Load attendance for the new session type
                if (sessionNumberCombo.getValue() != null && !sessionNumberCombo.getValue().equals("No Lab Sessions")) {
                    loadExistingAttendance();
                }
            }
        });

        // Update attendance display when session number changes
        sessionNumberCombo.setOnAction(e -> {
            if (currentClassroom != null && sessionNumberCombo.getValue() != null && !sessionNumberCombo.getValue().equals("No Lab Sessions")) {
                loadExistingAttendance();
            }
        });
    }

    private void updateSessionOptions(Course course) {
        String sessionType = sessionTypeCombo.getValue();
        ObservableList<String> sessionNumbers = FXCollections.observableArrayList();

        if ("Lecture".equals(sessionType)) {
            lblSessionType.setText("Lecture Session");
            int totalLectures = course.getCourseTheoHours();
            lblTotalSessions.setText("Total Lectures: " + totalLectures);

            // Populate lecture sessions
            for (int i = 1; i <= totalLectures; i++) {
                sessionNumbers.add("Lecture " + i);
            }
        } else {
            lblSessionType.setText("Lab Session");
            if (course.isCourseHasLabHours()) {
                int totalLabs = course.getCourseLabHours();
                lblTotalSessions.setText("Total Labs: " + totalLabs);

                // Populate lab sessions
                for (int i = 1; i <= totalLabs; i++) {
                    sessionNumbers.add("Lab " + i);
                }
            } else {
                lblTotalSessions.setText("No Lab Sessions");
                sessionNumbers.add("No Lab Sessions");
            }
        }

        sessionNumberCombo.setItems(sessionNumbers);
        if (!sessionNumbers.isEmpty()) {
            sessionNumberCombo.setValue(sessionNumbers.get(0));
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

    private void loadExistingAttendance() {
        if (sessionNumberCombo.getValue() == null || sessionNumberCombo.getValue().equals("No Lab Sessions")) {
            return;
        }

        String sessionType = sessionTypeCombo.getValue();
        String sessionSelection = sessionNumberCombo.getValue();

        // Extract session number (e.g., "Lecture 1" -> 1, array index 0)
        String[] parts = sessionSelection.split(" ");
        int sessionNumber = Integer.parseInt(parts[1]);
        int sessionIndex = sessionNumber - 1; // Convert to 0-based index

        System.out.println("\n=== LOADING EXISTING ATTENDANCE ===");
        System.out.println("Session: " + sessionSelection);
        System.out.println("Session Index: " + sessionIndex);

        // Load existing attendance from student records
        attendanceMap.clear();

        for (Student s : attendanceTable.getItems()) {
            int courseIdx = getCourseIndexForStudent(s);

            if (courseIdx != -1) {
                int attendanceValue = 0;

                if ("Lecture".equals(sessionType)) {
                    // Get from CurrentTheoryHours
                    if (s.getCurrentTheoryHours() != null &&
                            courseIdx < s.getCurrentTheoryHours().size()) {
                        ArrayList<Integer> theoryHours = s.getCurrentTheoryHours().get(courseIdx);
                        if (sessionIndex < theoryHours.size()) {
                            attendanceValue = theoryHours.get(sessionIndex);
                            System.out.println("Student " + s.getStudentId() + " - Lecture attendance: " + attendanceValue);
                        } else {
                            System.out.println("Student " + s.getStudentId() + " - Session index out of bounds");
                        }
                    } else {
                        System.out.println("Student " + s.getStudentId() + " - Theory hours not initialized");
                    }
                } else {
                    // Get from CurrentlabHours
                    if (s.getCurrentlabHours() != null &&
                            courseIdx < s.getCurrentlabHours().size()) {
                        ArrayList<Integer> labHours = s.getCurrentlabHours().get(courseIdx);
                        if (sessionIndex < labHours.size()) {
                            attendanceValue = labHours.get(sessionIndex);
                            System.out.println("Student " + s.getStudentId() + " - Lab attendance: " + attendanceValue);
                        } else {
                            System.out.println("Student " + s.getStudentId() + " - Session index out of bounds");
                        }
                    } else {
                        System.out.println("Student " + s.getStudentId() + " - Lab hours not initialized");
                    }
                }

                // 1 = Present, 0 = Absent
                attendanceMap.put(s.getStudentId(), attendanceValue == 1);
                System.out.println("Student " + s.getStudentId() + " set to: " + (attendanceValue == 1 ? "PRESENT" : "ABSENT"));
            } else {
                System.out.println("Student " + s.getStudentId() + " - Course not found in student's courses");
                // Default to absent if course not found
                attendanceMap.put(s.getStudentId(), false);
            }
        }

        System.out.println("=== ATTENDANCE MAP SIZE: " + attendanceMap.size() + " ===\n");

        // CRITICAL: Force refresh the entire table
        attendanceTable.setItems(FXCollections.observableArrayList(attendanceTable.getItems()));
        attendanceTable.refresh();
    }

    private void setupTableColumns() {
        attendanceTable.setEditable(false);

        // Student ID
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        // Student Name
        colStudentName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));

        // Attendance Column with Present/Absent buttons
        colAttendance.setCellFactory(param -> new TableCell<Student, Void>() {
            private final ToggleButton presentBtn = new ToggleButton("Present");
            private final ToggleButton absentBtn = new ToggleButton("Absent");
            private final ToggleGroup toggleGroup = new ToggleGroup();
            private final HBox container = new HBox(10);

            {
                presentBtn.setToggleGroup(toggleGroup);
                absentBtn.setToggleGroup(toggleGroup);

                presentBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-min-width: 80px;");
                absentBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-min-width: 80px;");

                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(presentBtn, absentBtn);

                presentBtn.setOnAction(e -> {
                    Student student = getTableView().getItems().get(getIndex());
                    if (student != null) {
                        attendanceMap.put(student.getStudentId(), true);
                        System.out.println("Student " + student.getStudentId() + " marked PRESENT");
                        // Refresh the table to update status column
                        attendanceTable.refresh();
                    }
                });

                absentBtn.setOnAction(e -> {
                    Student student = getTableView().getItems().get(getIndex());
                    if (student != null) {
                        attendanceMap.put(student.getStudentId(), false);
                        System.out.println("Student " + student.getStudentId() + " marked ABSENT");
                        // Refresh the table to update status column
                        attendanceTable.refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Student student = getTableView().getItems().get(getIndex());
                    if (student != null) {
                        // Get attendance from map (loaded from student records)
                        Boolean isPresent = attendanceMap.get(student.getStudentId());
                        if (isPresent != null && isPresent) {
                            presentBtn.setSelected(true);
                        } else {
                            absentBtn.setSelected(true);
                        }
                    }
                    setGraphic(container);
                }
            }
        });

        // Status Column - shows current attendance status from student data
        colStatus.setCellValueFactory(cellData -> {
            Student s = cellData.getValue();
            Boolean isPresent = attendanceMap.get(s.getStudentId());

            if (isPresent != null && isPresent) {
                return new SimpleStringProperty("✓ Present");
            } else {
                return new SimpleStringProperty("✗ Absent");
            }
        });

        // Custom cell factory to add color styling
        colStatus.setCellFactory(column -> new TableCell<Student, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);

                    if (item.contains("Present")) {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void loadClassrooms() {
        var admin = LoginController.getAdminUser();
        if (admin == null) return;

        ObservableList<String> names = FXCollections.observableArrayList();

        for (Classroom c : admin.getAdminClassrooms()) {
            if (p.getProfessorGivesLecture().contains(c)) {
                names.add(c.getClassroomName());
            }
        }

        classListView.setItems(names);
    }

    private void refreshAttendance() {
        var admin = LoginController.getAdminUser();
        ArrayList<Student> students = currentClassroom.getClassroomStudents();

        if (students == null || students.isEmpty()) {
            System.out.println("No students found in " + currentClassroom.getClassroomName());
            attendanceTable.setItems(FXCollections.observableArrayList());
        } else {
            System.out.println("Loading " + students.size() + " students for attendance.");

            ArrayList<Student> masterStudents = new ArrayList<>();

            for (Student classroomStudent : students) {
                for (Student adminStudent : admin.getAdminStudents()) {
                    if (adminStudent.getStudentId() == classroomStudent.getStudentId()) {
                        // Ensure attendance arrays are initialized
                        ensureAttendanceArraysInitialized(adminStudent);
                        masterStudents.add(adminStudent);
                        break;
                    }
                }
            }

            attendanceTable.setItems(FXCollections.observableArrayList(masterStudents));
            attendanceTable.refresh();

            // Load existing attendance for the current session
            javafx.application.Platform.runLater(() -> {
                loadExistingAttendance();
            });
        }
    }

    private void ensureAttendanceArraysInitialized(Student student) {
        if (currentClassroom == null || currentClassroom.getClassroomCourse() == null) return;

        System.out.println("\n=== ENSURING ATTENDANCE ARRAYS FOR STUDENT " + student.getStudentId() + " ===");

        int courseIdx = getCourseIndexForStudent(student);
        System.out.println("Course index: " + courseIdx);

        if (courseIdx == -1) {
            System.err.println("ERROR: Course not found in student's course list");
            return;
        }

        Course course = currentClassroom.getClassroomCourse();

        // Ensure CurrentTheoryHours array exists and has the right size
        if (student.getCurrentTheoryHours() == null) {
            System.err.println("ERROR: CurrentTheoryHours is NULL - this should never happen!");
            return;
        }

        System.out.println("CurrentTheoryHours size: " + student.getCurrentTheoryHours().size());
        System.out.println("Need to have index: " + courseIdx);

        // Make sure the array has enough entries for this course index
        while (student.getCurrentTheoryHours().size() <= courseIdx) {
            ArrayList<Integer> newTheoryArray = new ArrayList<>();
            // Initialize with 0s for all theory hours
            for (int i = 0; i < course.getCourseTheoHours(); i++) {
                newTheoryArray.add(0);
            }
            student.getCurrentTheoryHours().add(newTheoryArray);
            System.out.println("Created theory hours array at index " + (student.getCurrentTheoryHours().size() - 1));
        }

        // Verify the theory hours array has enough sessions
        ArrayList<Integer> theoryHours = student.getCurrentTheoryHours().get(courseIdx);
        System.out.println("Theory hours array size: " + theoryHours.size());
        System.out.println("Course has " + course.getCourseTheoHours() + " theory hours");

        while (theoryHours.size() < course.getCourseTheoHours()) {
            theoryHours.add(0);
            System.out.println("Added theory hour slot, now size: " + theoryHours.size());
        }

        // Ensure CurrentlabHours array exists and has the right size
        if (student.getCurrentlabHours() == null) {
            System.err.println("ERROR: CurrentlabHours is NULL - this should never happen!");
            return;
        }

        System.out.println("CurrentlabHours size: " + student.getCurrentlabHours().size());

        // Make sure the array has enough entries for this course index
        while (student.getCurrentlabHours().size() <= courseIdx) {
            ArrayList<Integer> newLabArray = new ArrayList<>();
            // Initialize with 0s for all lab hours
            for (int i = 0; i < course.getCourseLabHours(); i++) {
                newLabArray.add(0);
            }
            student.getCurrentlabHours().add(newLabArray);
            System.out.println("Created lab hours array at index " + (student.getCurrentlabHours().size() - 1));
        }

        // Verify the lab hours array has enough sessions
        ArrayList<Integer> labHours = student.getCurrentlabHours().get(courseIdx);
        System.out.println("Lab hours array size: " + labHours.size());
        System.out.println("Course has " + course.getCourseLabHours() + " lab hours");

        while (labHours.size() < course.getCourseLabHours()) {
            labHours.add(0);
            System.out.println("Added lab hour slot, now size: " + labHours.size());
        }

        System.out.println("=== ATTENDANCE ARRAYS INITIALIZED ===\n");
    }

    @FXML
    private void handleMarkAll(ActionEvent event) {
        // Mark all students as present
        for (Student s : attendanceTable.getItems()) {
            attendanceMap.put(s.getStudentId(), true);
        }
        attendanceTable.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("All students marked as present!");
        alert.show();
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            if (currentClassroom == null) {
                showAlert("Error", "No classroom selected", Alert.AlertType.ERROR);
                return;
            }

            if (sessionNumberCombo.getValue() == null || sessionNumberCombo.getValue().equals("No Lab Sessions")) {
                showAlert("Error", "Please select a valid session", Alert.AlertType.ERROR);
                return;
            }

            String sessionType = sessionTypeCombo.getValue();
            String sessionSelection = sessionNumberCombo.getValue();

            // Extract session number from selection (e.g., "Lecture 1" -> 1)
            String[] parts = sessionSelection.split(" ");
            int sessionNumber = Integer.parseInt(parts[1]);
            int sessionIndex = sessionNumber - 1; // Convert to 0-based array index

            var admin = LoginController.getAdminUser();

            System.out.println("========================================");
            System.out.println("DEBUG: SAVING ATTENDANCE");
            System.out.println("Classroom: " + currentClassroom.getClassroomName());
            System.out.println("Session Type: " + sessionType);
            System.out.println("Session: " + sessionSelection);
            System.out.println("Session Number: " + sessionNumber);
            System.out.println("Array Index: " + sessionIndex);

            int presentCount = 0;
            int absentCount = 0;

            // Save attendance for each student in the TABLE (these are from admin's master list)
            for (Student tableStudent : attendanceTable.getItems()) {
                Boolean isPresent = attendanceMap.get(tableStudent.getStudentId());
                if (isPresent == null) {
                    isPresent = false; // Default to absent if not in map
                }

                System.out.println("\n--- Processing Student " + tableStudent.getStudentId() + " ---");

                int courseIdx = getCourseIndexForStudent(tableStudent);
                System.out.println("Course index: " + courseIdx);

                if (courseIdx == -1) {
                    System.err.println("ERROR: Course not found for student " + tableStudent.getStudentId());
                    continue;
                }

                if ("Lecture".equals(sessionType)) {
                    // Ensure CurrentTheoryHours is initialized
                    if (tableStudent.getCurrentTheoryHours() == null) {
                        System.err.println("ERROR: CurrentTheoryHours is NULL!");
                        continue;
                    }

                    System.out.println("CurrentTheoryHours size: " + tableStudent.getCurrentTheoryHours().size());
                    System.out.println("Need index: " + courseIdx);

                    if (courseIdx >= tableStudent.getCurrentTheoryHours().size()) {
                        System.err.println("ERROR: Course index " + courseIdx + " >= array size " + tableStudent.getCurrentTheoryHours().size());
                        continue;
                    }

                    ArrayList<Integer> theoryHours = tableStudent.getCurrentTheoryHours().get(courseIdx);
                    System.out.println("Theory hours array size: " + theoryHours.size());
                    System.out.println("Need session index: " + sessionIndex);

                    if (sessionIndex >= theoryHours.size()) {
                        System.err.println("ERROR: Session index " + sessionIndex + " >= theory hours size " + theoryHours.size());
                        continue;
                    }

                    // Save attendance
                    int oldValue = theoryHours.get(sessionIndex);
                    theoryHours.set(sessionIndex, isPresent ? 1 : 0);
                    System.out.println("Saved: Lecture " + sessionNumber + " - Changed from " + oldValue + " to " + (isPresent ? 1 : 0));

                } else {
                    // Ensure CurrentlabHours is initialized
                    if (tableStudent.getCurrentlabHours() == null) {
                        System.err.println("ERROR: CurrentlabHours is NULL!");
                        continue;
                    }

                    System.out.println("CurrentlabHours size: " + tableStudent.getCurrentlabHours().size());

                    if (courseIdx >= tableStudent.getCurrentlabHours().size()) {
                        System.err.println("ERROR: Course index " + courseIdx + " >= lab array size");
                        continue;
                    }

                    ArrayList<Integer> labHours = tableStudent.getCurrentlabHours().get(courseIdx);
                    System.out.println("Lab hours array size: " + labHours.size());

                    if (sessionIndex >= labHours.size()) {
                        System.err.println("ERROR: Session index " + sessionIndex + " >= lab hours size " + labHours.size());
                        continue;
                    }

                    // Save attendance
                    int oldValue = labHours.get(sessionIndex);
                    labHours.set(sessionIndex, isPresent ? 1 : 0);
                    System.out.println("Saved: Lab " + sessionNumber + " - Changed from " + oldValue + " to " + (isPresent ? 1 : 0));
                }

                if (isPresent) {
                    presentCount++;
                } else {
                    absentCount++;
                }
            }

            System.out.println("\nPresent: " + presentCount);
            System.out.println("Absent: " + absentCount);

            // Save to database
            LoginController.saveAdminUser();
            LoginController.saveStudentUser();

            System.out.println("✓ Attendance saved to Student records");
            System.out.println("========================================\n");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Attendance saved for " + sessionSelection + "!\nPresent: " + presentCount + "\nAbsent: " + absentCount);
            alert.showAndWait();

        } catch (IOException e) {
            showAlert("Save Error", "Failed to save attendance: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "Unexpected error: " + e.getMessage(), Alert.AlertType.ERROR);
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