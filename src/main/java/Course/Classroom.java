package Course;

import userTypes.Student;
import java.io.Serializable;
import java.util.ArrayList;

public class Classroom implements Serializable {
    private static final long serialVersionUID = 1L;
    private String classroomName;
    private int classroomID;
    private Course classroomCourse;
    private ArrayList<Student> classroomStudents = new ArrayList<>();

    public Classroom(int classroomID, String classroomName, Course classroomCourse) {
        this.classroomID = classroomID;
        this.classroomCourse = classroomCourse;
        this.classroomName = classroomName;
    }

    public void addStudent(Student student) {
        // Fix: Check by ID to prevent duplicate "ghost" objects
        boolean exists = false;
        for (Student s : classroomStudents) {
            if (s.getStudentId() == student.getStudentId()) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            classroomStudents.add(student);
            System.out.println("DEBUG: Added student ID " + student.getStudentId() + " to " + classroomName);
        }
    }

    public void removeStudent(Student student) {
        classroomStudents.removeIf(s -> s.getStudentId() == student.getStudentId());
    }

    public ArrayList<Student> getClassroomStudents() { return classroomStudents; }
    public String getClassroomName() { return classroomName; }
    public int getClassroomID() { return classroomID; }
    public void setClassroomName(String classroomName) { this.classroomName = classroomName; }
    public Course getClassroomCourse() { return classroomCourse; }
    public void setClassroomCourse(Course classroomCourse) { this.classroomCourse = classroomCourse; }
}