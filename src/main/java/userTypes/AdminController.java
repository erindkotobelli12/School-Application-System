package userTypes;
import Course.Course;
import Login.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import java.io.IOException;
import java.util.ArrayList;

public class AdminController {
    @FXML private BorderPane mainLayout;
    @FXML private VBox sideBar;
    private boolean isCollapsed = false;


    @FXML
    private void toggleSidebar() {
        if (isCollapsed) {
            mainLayout.setLeft(sideBar); // Put sidebar back
        } else {
            mainLayout.setLeft(null); // Remove sidebar
        }
        isCollapsed = !isCollapsed;
    }

    @FXML
    private void View_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_view_view.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("View");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not find admin_view_view.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void Edit_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_edit_view.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Edit");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not find admin_edit_view.fxml");
            e.printStackTrace();
        }
    }
    @FXML
    private void Delete_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_delete_view.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Delete");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not find admin_delete_view.fxml");
            e.printStackTrace();
        }
    }

    Admin admin = LoginController.getAdminUser();

    @FXML
    private void increaseSemester() {
        try {
            // Confirm action
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Increase Semester");
            confirmAlert.setHeaderText("Are you sure you want to advance to the next semester?");
            confirmAlert.setContentText("This will:\n" +
                    "- Finalize all student grades\n" +
                    "- Check attendance requirements\n" +
                    "- Move current courses to completed courses\n" +
                    "- Clear all classrooms\n" +
                    "- Clear professor assignments\n\n" +
                    "This action cannot be undone!");

            if (confirmAlert.showAndWait().get() != ButtonType.OK) {
                return;
            }

            System.out.println("========================================");
            System.out.println("DEBUG: INCREASING SEMESTER");
            System.out.println("Current Semester: " + Admin.getCurrentSemester());
            System.out.println("Current Year: " + Admin.getEnrollmentYear());

            // Step 1: Process all students - finalize grades and move courses
            for (Student student : admin.getAdminStudents()) {
                System.out.println("\n--- Processing Student " + student.getStudentId() + " ---");

                ArrayList<Course> currentCourses = student.getStudentCurrentCourses();
                ArrayList<ArrayList<Double>> currentGrades = student.getCurrentCourseGrades();
                ArrayList<ArrayList<Integer>> theoryHours = student.getCurrentTheoryHours();
                ArrayList<ArrayList<Integer>> labHours = student.getCurrentlabHours();

                System.out.println("Current courses: " + currentCourses.size());

                // Process each current course
                for (int i = currentCourses.size() - 1; i >= 0; i--) {
                    Course course = currentCourses.get(i);

                    boolean failedAttendance = false;
                    String failureReason = "";

                    // Check theory attendance (must be >= 60%)
                    if (i < theoryHours.size()) {
                        ArrayList<Integer> theory = theoryHours.get(i);
                        int totalTheory = course.getCourseTheoHours();
                        int attendedTheory = 0;

                        for (int hour : theory) {
                            if (hour == 1) attendedTheory++;
                        }

                        double theoryPercentage = (totalTheory > 0) ? (attendedTheory * 100.0 / totalTheory) : 100.0;

                        System.out.println("Theory attendance: " + attendedTheory + "/" + totalTheory +
                                " (" + String.format("%.1f%%", theoryPercentage) + ")");

                        if (theoryPercentage < 60) {
                            failedAttendance = true;
                            failureReason = "Theory attendance below 60% (" + String.format("%.1f%%", theoryPercentage) + ")";
                        }
                    }

                    // Check lab attendance (must be >= 80%) if course has lab
                    if (course.isCourseHasLabHours() && i < labHours.size()) {
                        ArrayList<Integer> lab = labHours.get(i);
                        int totalLab = course.getCourseLabHours();
                        int attendedLab = 0;

                        for (int hour : lab) {
                            if (hour == 1) attendedLab++;
                        }

                        double labPercentage = (totalLab > 0) ? (attendedLab * 100.0 / totalLab) : 100.0;

                        System.out.println("Lab attendance: " + attendedLab + "/" + totalLab +
                                " (" + String.format("%.1f%%", labPercentage) + ")");

                        if (labPercentage < 80) {
                            failedAttendance = true;
                            if (!failureReason.isEmpty()) failureReason += ", ";
                            failureReason += "Lab attendance below 80% (" + String.format("%.1f%%", labPercentage) + ")";
                        }
                    }

                    // Calculate final grade
                    double finalGradeValue = 0.0;

                    if (failedAttendance) {
                        // FAILED due to attendance - grade is 0.0
                        finalGradeValue = 0.0;
                        System.out.println("⚠ FAILED: " + course.getCourseName() + " - " + failureReason);
                    } else {
                        // Calculate grade from midterm and final exam
                        if (i < currentGrades.size()) {
                            ArrayList<Double> grades = currentGrades.get(i);
                            if (grades.size() >= 2) {
                                double midterm = grades.get(0);
                                double finalExam = grades.get(1);

                                // Calculate weighted average (40% midterm, 60% final)
                                double weightedAvg = (midterm * 0.4) + (finalExam * 0.6);

                                // Convert to 4.0 scale
                                finalGradeValue = convertToGradePoints(weightedAvg);

                                System.out.println("✓ PASSED: Course " + course.getCourseID() +
                                        " - Midterm=" + midterm +
                                        ", Final=" + finalExam +
                                        ", Weighted=" + weightedAvg +
                                        ", GPA Points=" + finalGradeValue);
                            }
                        }
                    }

                    // Add to finished courses (regardless of pass/fail)
                    student.getStudentCourseFinished().add(course);

                    // Add final grade to studentFinalGrades (0.0 if failed attendance)
                    student.getStudentFinalGrades().add(finalGradeValue);

                    if (failedAttendance) {
                        System.out.println("✗ Course " + course.getCourseName() + " recorded as FAILED (0.0) due to attendance");
                    } else {
                        System.out.println("✓ Course " + course.getCourseName() + " completed with grade " + finalGradeValue);
                    }
                }

                // Clear all current course data
                student.clearCurrentCourses();

                // Recalculate GPA based on all finished courses
                student.studentCalculateGPA();

                System.out.println("Student " + student.getStudentId() + " - New GPA: " + student.getStudentGPA());
                System.out.println("Total finished courses: " + student.getStudentCourseFinished().size());
            }

            // Step 2: Clear all professor lecture assignments
            System.out.println("\n--- Clearing Professor Assignments ---");
            for (Professor p : admin.getAdminProfessors()) {
                int classroomCount = p.getProfessorGivesLecture().size();
                p.getProfessorGivesLecture().clear();
                System.out.println("Cleared " + classroomCount + " classrooms from Professor " + p.getProfessorId());
            }

            // Step 3: Delete all classrooms
            System.out.println("\n--- Deleting All Classrooms ---");
            int classroomCount = admin.getAdminClassrooms().size();
            admin.getAdminClassrooms().clear();
            System.out.println("Deleted " + classroomCount + " classrooms");

            // Step 4: Remove classroom references from courses
            System.out.println("\n--- Clearing Classroom References from Courses ---");
            for (Course course : admin.getAdminCourses()) {
                course.getClassrooms().clear();
            }

            // Step 5: Update semester and year
            if (Admin.getCurrentSemester() == 1) {
                Admin.setCurrentSemester(2);
                System.out.println("Advanced to Semester 2");
            } else {
                Admin.setCurrentSemester(1);
                Admin.setEnrollmentYear(Admin.getEnrollmentYear() + 1);
                System.out.println("Advanced to Semester 1 of Year " + Admin.getEnrollmentYear());
            }

            // Step 6: Save everything
            System.out.println("\n--- Saving Data ---");
            LoginController.saveAdminUser();
            LoginController.saveStudentUser();
            LoginController.saveAdminProfessors();

            System.out.println("✓ All data saved");
            System.out.println("========================================\n");

            // Show success message
            showAlert("Semester Advanced",
                    "Successfully advanced to Semester " + Admin.getCurrentSemester() +
                            " of Academic Year " + Admin.getEnrollmentYear() +
                            "\n\nAll student grades have been finalized." +
                            "\nAttendance requirements checked." +
                            "\nClassrooms have been cleared." +
                            "\nProfessor assignments have been reset.");

        } catch (Exception e) {
            showAlert("Error", "Failed to increase semester: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double convertToGradePoints(double percentage) {
        if (percentage >= 90) return 4.0;
        else if (percentage >= 85) return 3.5;
        else if (percentage >= 80) return 3.0;
        else if (percentage >= 75) return 2.5;
        else if (percentage >= 70) return 2.0;
        else if (percentage >= 65) return 1.5;
        else if (percentage >= 60) return 1.0;
        else if (percentage >= 55) return 0.5;
        else return 0.0;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void logout(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Login");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Could not find or load login_view.fxml. check the file path!");
        }
    }
}