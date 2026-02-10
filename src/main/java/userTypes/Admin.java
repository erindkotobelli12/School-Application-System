package userTypes;

import Course.Course;

import java.io.Serializable;
import java.util.ArrayList;
import Course.Classroom;
import Course.Department;
import Course.Degree;

public class Admin implements Serializable {
    private int id;
    private String password;
    private ArrayList<Student> adminStudents= new ArrayList<>();
    private ArrayList<Professor> adminProfessors= new ArrayList<>();
    private ArrayList<Course> adminCourses= new ArrayList<>();
    private ArrayList<Classroom> adminClassrooms= new ArrayList<>();
    private ArrayList<Degree> adminDegrees= new ArrayList<>();
    private ArrayList<Department> adminDeparments= new ArrayList<>();
    private  static int currentSemester =1;
    private static int enrollmentYear=2025;
    public Admin(int id, String password) {
        this.id = id;
        this.password = password;
    }

    public static int getEnrollmentYear() {
        return enrollmentYear;
    }
    public static void setEnrollmentYear(int enrollmentYear) {
        Admin.enrollmentYear = enrollmentYear;
    }
    public static int getCurrentSemester() {
        return currentSemester;
    }

    public static void setCurrentSemester(int currentSemester) {
        Admin.currentSemester = currentSemester;
    }

    public ArrayList<Student> getAdminStudents() {
        return adminStudents;
    }
    public int getAdminStudentsSize() {
        return adminStudents.size();
    }
    public int getAdminProfessorsSize() {
        return adminProfessors.size();
    }

    public ArrayList<Professor> getAdminProfessors() {
        return adminProfessors;
    }

    public Student getAdminStudentsStudent(int i) {
        return adminStudents.get(i);
    }
    public void addAdminStudents(Student adminStudents) {
        this.adminStudents.add(adminStudents);
    }
    public Professor getAdminProfessors(int i) {
        return adminProfessors.get(i);
    }
    public void addAdminProfessors(Professor adminProfessors) {
        this.adminProfessors.add(adminProfessors);
    }

    public void addAdminCourses( Course adminCourses) {
        this.adminCourses.add(adminCourses);
    }

    public void addAdminClassrooms(Classroom adminClassrooms) {
        this.adminClassrooms.add(adminClassrooms);
    }

    public void addAdminDepartments(Department adminDeparments) {
        this.adminDeparments.add(adminDeparments);
    }

    public void addAdminDegrees(Degree adminDegrees) {
        this.adminDegrees.add(adminDegrees);
    }

    public int getId() {
        return id;
    }
    public void setPassword() {
        this.password = password;
    }

    public ArrayList<Course> getAdminCourses() {
        return adminCourses;
    }

    public ArrayList<Classroom> getAdminClassrooms() {
        return adminClassrooms;
    }

    public ArrayList<Degree> getAdminDegrees() {
        return adminDegrees;
    }

    public ArrayList<Department> getAdminDepartments() {
        return adminDeparments;
    }
    public void adminDeleteDepartment(Department department){
        adminDeparments.remove(department);
    }
    public int getAdminDegreesSize() {
        return adminDegrees.size();
    }
}
