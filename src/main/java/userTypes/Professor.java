package userTypes;

import Course.Classroom;
import Course.Department;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Professor extends Person implements Serializable {
    private int professorId;
    private String password;
    private static int professorNextId=1;
    private Department professorDepartment;
    private String professorType;
    private String professorTitle;
    private int professorYearsOfExperience;
    private String professorOfficeLocation;
    private String professorSpecialization;
    private boolean professorIsTenured;
    private double professorSalary;
    private ArrayList<Classroom> professorGivesLecture=new ArrayList<>();

    public Professor(String firstName, String lastName, String dateOfBirth, String email, String phoneNumber, int age, Department professorDepartment,String professorType, String professorTitle, int professorYearsOfExperience,
                     String professorOfficeLocation, String professorSpecialization, boolean professorIsTenured,String gender,String password,String address,double salary) {
        super(firstName, lastName, dateOfBirth, email, phoneNumber, age,gender,address);
        this.professorId=professorNextId;
        this.professorDepartment = professorDepartment;
        this.professorTitle = professorTitle;
        this.professorType = professorType;
        this.professorYearsOfExperience = professorYearsOfExperience;
        this.professorOfficeLocation = professorOfficeLocation;
        this.professorSpecialization = professorSpecialization;
        this.professorIsTenured = professorIsTenured;
        this.password=password;
        this.professorSalary=salary;
        professorNextId++;
    }
    public void setProfessorSalary(double professorSalary) {
        this.professorSalary = professorSalary;
    }
    public double getProfessorSalary() {
        return professorSalary;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Department getProfessorDepartment() {
        return professorDepartment;
    }

    public void setProfessorDepartment(Department professorDepartment) {
        this.professorDepartment = professorDepartment;
    }

    public void setProfessorNextId(int professorId) {
        this.professorNextId = professorId;
    }
    public String getProfessorType() {
        return professorType;
    }

    public void setProfessorType(String professorType) {
        this.professorType = professorType;
    }

    public String getProfessorTitle() {
        return professorTitle;
    }

    public void setProfessorTitle(String professorTitle) {
        this.professorTitle = professorTitle;
    }

    public int getProfessorYearsOfExperience() {
        return professorYearsOfExperience;
    }

    public void setProfessorYearsOfExperience(int professorYearsOfExperience) {
        this.professorYearsOfExperience = professorYearsOfExperience;
    }

    public String getProfessorOfficeLocation() {
        return professorOfficeLocation;
    }

    public void setProfessorOfficeLocation(String professorOfficeLocation) {
        this.professorOfficeLocation = professorOfficeLocation;
    }

    public String getProfessorSpecialization() {
        return professorSpecialization;
    }

    public void setProfessorSpecialization(String professorSpecialization) {
        this.professorSpecialization = professorSpecialization;
    }

    public boolean isProfessorIsTenured() {
        return professorIsTenured;
    }

    public void setProfessorIsTenured(boolean professorIsTenured) {
        this.professorIsTenured = professorIsTenured;
    }
    public int getProfessorId() {
        return professorId;
    }
    public void addClassesToProfessor(Classroom classroom) {
        professorGivesLecture.add(classroom);
    }
    public ArrayList<Classroom> getProfessorGivesLecture() {
        return professorGivesLecture;
    }
    public void setProfessorGivesLecture(ArrayList<Classroom> professorGivesLecture) {
        this.professorGivesLecture = professorGivesLecture;
    }

}