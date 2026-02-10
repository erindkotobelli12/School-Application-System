package adminfunc.admindelete;

import Course.Course;
import Course.Degree;
import Course.Classroom;
import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import userTypes.Admin;
import userTypes.Professor;
import userTypes.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Admin_Course_Delete {

    @FXML
    private TextField txtCourseId;

    @FXML
    private Button btnDelete;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_delete_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Admin");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not return to the dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCourse() throws IOException {
        String courseIdInput = txtCourseId.getText().trim();

        if (courseIdInput.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a Course ID to proceed.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Action: Delete Course ID " + courseIdInput);
        confirm.setContentText("Are you sure? This will remove the course, associated classrooms, and student enrollments.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Admin admin = LoginController.getAdminUser();

            try {
                int idToFind = Integer.parseInt(courseIdInput);
                Course targetCourse = null;

                // 1. Find the target course
                for (Course course : admin.getAdminCourses()) {
                    if (course.getCourseID() == idToFind) {
                        targetCourse = course;
                        break;
                    }
                }

                if (targetCourse != null) {
                    final Course finalTarget = targetCourse;

                    // 2. Remove from students current AND finished courses
                    for (Student student : admin.getAdminStudents()) {
                        // Find index in current courses to also remove grade arrays
                        int courseIdx = -1;
                        for (int i = 0; i < student.getStudentCurrentCourses().size(); i++) {
                            if (student.getStudentCurrentCourses().get(i).getCourseID() == idToFind) {
                                courseIdx = i;
                                break;
                            }
                        }

                        // Remove from current courses and clean up arrays
                        if (courseIdx != -1) {
                            student.getStudentCurrentCourses().remove(courseIdx);
                            if (courseIdx < student.getCurrentCourseGrades().size())
                                student.getCurrentCourseGrades().remove(courseIdx);
                            if (courseIdx < student.getCurrentTheoryHours().size())
                                student.getCurrentTheoryHours().remove(courseIdx);
                            if (courseIdx < student.getCurrentlabHours().size())
                                student.getCurrentlabHours().remove(courseIdx);
                            student.getStudentNrOfCurrentCourses(); // counter sync
                        }

                        // Remove from finished courses
                        student.getStudentCourseFinished().removeIf(c -> c.getCourseID() == idToFind);
                    }

                    // 3. Remove from Degrees
                    for (Degree degree : admin.getAdminDegrees()) {
                        degree.getRequiredCourses().removeIf(c -> c.getCourseID() == idToFind);
                        degree.getElectiveCourses().removeIf(c -> c.getCourseID() == idToFind);
                    }

                    // 4. Remove classrooms associated with this course
                    List<Classroom> roomsToRemove = new ArrayList<>(targetCourse.getClassrooms());
                    for (Classroom room : roomsToRemove) {
                        // Remove from professors
                        for (Professor p : admin.getAdminProfessors()) {
                            p.getProfessorGivesLecture().removeIf(c -> c.getClassroomID() == room.getClassroomID());
                        }
                        // Remove from admin
                        admin.getAdminClassrooms().removeIf(c -> c.getClassroomID() == room.getClassroomID());
                    }

                    // 5. Remove the course itself
                    admin.getAdminCourses().removeIf(c -> c.getCourseID() == idToFind);

                    // Save
                    LoginController.saveAdminUser();
                    LoginController.saveAdminProfessors();
                    LoginController.saveStudentUser();

                    txtCourseId.clear();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Course " + courseIdInput + " has been completely removed.");

                } else {
                    showAlert(Alert.AlertType.ERROR, "Not Found", "Course ID " + courseIdInput + " does not exist.");
                }

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Course ID must be a numeric value.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}