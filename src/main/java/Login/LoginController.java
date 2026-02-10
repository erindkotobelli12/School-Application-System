package Login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.stage.Stage;
import userTypes.Professor;
import userTypes.Student;
import userTypes.Admin;
import java.io.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final Authentication authService = new Authentication();

    public static Admin adminUser;
    public static Student studentUser;
    public static Professor professorUser;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException, ClassNotFoundException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "Please enter a username and password.", Alert.AlertType.WARNING);
            return;
        }

        try {
            int usernameInt = Integer.parseInt(username);

            // 1. STUDENT LOGIN
            if (usernameInt >= 10000) {
                int cnt = authService.StudentLoginCheck(username, password);
                if (cnt == -1) {
                    showAlert("Login Failed", "Invalid Student ID or Password", Alert.AlertType.ERROR);
                    return;
                }
                File file = new File("src/main/database/Student_ser.ser");
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    for (int i = 0; i < cnt; i++) studentUser = (Student) ois.readObject();
                    AddAdminUser();
                    switchScene(Role.Student, event);
                }
            }
            // 2. PROFESSOR LOGIN
            else if (usernameInt >= 1) {
                int cnt = authService.ProfessorLoginCheck(username, password);
                if (cnt == -1) {
                    showAlert("Login Failed", "Invalid Professor ID or Password", Alert.AlertType.ERROR);
                    return;
                }
                File file = new File("src/main/database/Professor_ser.ser");
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    for (int i = 0; i < cnt; i++) professorUser = (Professor) ois.readObject();
                    AddAdminUser();
                    switchScene(Role.Professor, event);
                }
            }
            // 3. ADMIN LOGIN
            else if (usernameInt == 0) {
                if (password.equals("0")) {
                    AddAdminUser();
                    switchScene(Role.Admin, event);
                } else {
                    showAlert("Login Failed", "Invalid Admin Password", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Login Failed", "User not found.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Username must be a numeric ID.", Alert.AlertType.ERROR);
        } catch (FileNotFoundException e) {
            showAlert("Database Error", "System database file missing.", Alert.AlertType.ERROR);
        }
    }

    private void AddAdminUser() throws IOException, ClassNotFoundException {
        File file = new File("src/main/database/Admin_ser.ser");
        if (file.exists() && file.length() != 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                adminUser = (Admin) ois.readObject();
                int maxId = 10000;
                for (Student s : adminUser.getAdminStudents()) {
                    if (s.getStudentId() > maxId) {
                        maxId = s.getStudentId();
                    }
                }
                Student.setStudentNextID(maxId + 1);
                int profid=1;
                for (Professor p : adminUser.getAdminProfessors()) {
                    if (p.getProfessorId() > maxId) {
                        maxId = p.getProfessorId();
                        p.setProfessorNextId(maxId+1);
                    }
                }
            }
        } else {
            adminUser = new Admin(0, "0");
        }
    }

    // --- STATIC SAVE METHODS ---
    public static void saveAdminUser() throws IOException {
        if (adminUser == null) {
            System.err.println("WARNING: adminUser is null, cannot save");
            return;
        }
        File file = new File("src/main/database/Admin_ser.ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(adminUser);
            oos.flush(); // Ensure data is written
            System.out.println("DEBUG: Admin saved with " + adminUser.getAdminClassrooms().size() + " classrooms");
        }
    }

    public static void saveStudentUser() throws IOException {
        if (adminUser == null) {
            System.err.println("WARNING: adminUser is null in saveStudentUser");
            return;
        }

        if (studentUser != null) {
            for (int i = 0; i < adminUser.getAdminStudents().size(); i++) {
                if (adminUser.getAdminStudents().get(i).getStudentId() == studentUser.getStudentId()) {
                    adminUser.getAdminStudents().set(i, studentUser);
                    break;
                }
            }
        }

        // Save CSV
        try (FileWriter fw = new FileWriter("src/main/database/Login_info_Student.csv")) {
            fw.write(adminUser.getAdminStudentsSize() + "\n");
            for (Student s : adminUser.getAdminStudents()) {
                fw.write(s.getStudentId() + "," + s.getPassword() + "\n");
            }
        }

        // Save SER - Write all students sequentially
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/database/Student_ser.ser"))) {
            for (Student s : adminUser.getAdminStudents()) {
                oos.writeObject(s);
            }
            oos.flush();
        }

        // Save Admin again to ensure classroom references are current
        saveAdminUser();
    }


    public static void saveAdminProfessors() throws IOException {
        if (adminUser == null) return;
        if (professorUser != null) {
            for (int i = 0; i < adminUser.getAdminProfessors().size(); i++) {
                if (adminUser.getAdminProfessors().get(i).getProfessorId() == professorUser.getProfessorId()) {
                    adminUser.getAdminProfessors().set(i, professorUser);
                    break;
                }
            }
        }
        // Save CSV
        try (FileWriter fw = new FileWriter("src/main/database/Login_info_Professor.csv")) {
            fw.write(adminUser.getAdminProfessorsSize() + "\n");
            for (Professor p : adminUser.getAdminProfessors()) {
                fw.write(p.getProfessorId() + "," + p.getPassword() + "\n");
            }
        }
        // Save SER
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/database/Professor_ser.ser"))) {
            for (Professor p : adminUser.getAdminProfessors()) oos.writeObject(p);
            oos.flush();
        }

        // ADD THIS LINE - Save Admin to persist the updated professor list
        saveAdminUser();
    }

    public static Admin getAdminUser() { return adminUser; }
    public static Student getStudentUser() { return studentUser; }
    public static Professor getProfessorUser() { return professorUser; }
    public static void setProfessorUser(Professor prof) {
        professorUser = prof;
    }

    public static void setStudentUser(Student stud) {
        studentUser = stud;
    }

    private void switchScene(Role role, ActionEvent event) throws IOException {
        String path = switch (role) {
            case Student -> "/student_view.fxml";
            case Professor -> "/professor_view.fxml";
            case Admin -> "/admin_view.fxml";
            default -> "/login_view.fxml";
        };
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource(path)).load(), 900, 600));
        stage.show();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}