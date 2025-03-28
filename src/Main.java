import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static ArrayList<String> departments;
    static String[] assignmentTypes = {"Classwork", "Homework", "Quiz", "Exam", "Project"};
    static ArrayList<String> course_types;
    static ArrayList<String> courses;
    static ArrayList<String> teachers;


    public static void main(String[] args) {
        dropDatabase();
        createDatabase();
        courses = getFileData("src/coursenames.csv");
        teachers = getFileData("src/teachernames.csv");
        departments = generateDepartments();
        course_types = generateCourseTypes();
        generateCourses();
    }

    public static void createDatabase() {
        System.out.println("CREATE TABLE Students ( first_name varchar(255), last_name varchar(255), student_id integer Primary Key );");
        System.out.println("CREATE TABLE Courses ( course_name varchar(255), type_id integer, course_id integer PRIMARY KEY, FOREIGN KEY (type_id) REFERENCES Course_types(type_id) );");
        System.out.println("CREATE TABLE Course_types ( type_name varchar(255), type_id integer PRIMARY KEY );");
        System.out.println("CREATE TABLE Teachers ( first_name varchar(255), last_name varchar(255), teacher_id integer PRIMARY KEY, department_id integer, FOREIGN KEY (department_id) REFERENCES departments(department_id) );");
        System.out.println("CREATE TABLE Classes ( course_id integer, class_time date, teacher_id integer, classroom varchar(255), class_id integer PRIMARY KEY, FOREIGN KEY (course_id) references Courses(course_id), FOREIGN KEY (teacher_id) references Teachers(teacher_id) );");
        System.out.println("CREATE TABLE Grades ( assignment_id integer, student_id integer, grade float, FOREIGN KEY ( assignment_id ) REFERENCES Assignments( assignment_id ), FOREIGN KEY ( student_id) REFERENCES Students( student_id) );");
        System.out.println("CREATE TABLE Assignment_types ( type_id integer PRIMARY KEY, type_name varchar(255) );");
        System.out.println("CREATE TABLE Departments ( department_id integer PRIMARY KEY, department_name varchar(255) );");
        System.out.println("CREATE TABLE Rosters ( student_id integer, class_id integer, FOREIGN KEY (student_id) REFERENCES Students(student_id), FOREIGN KEY (class_id) REFERENCES Classes(class_id) );");
        System.out.println("CREATE TABLE Assignments ( assignment_name varchar(255), assignment_id integer PRIMARY KEY, type_id integer, FOREIGN KEY (type_id) REFERENCES assignment_types(type_id) );");
    }

    public static void dropDatabase() {
        System.out.println("DROP TABLE Students;");
        System.out.println("DROP TABLE Courses;");
        System.out.println("DROP TABLE Course_types;");
        System.out.println("DROP TABLE Teachers;");
        System.out.println("DROP TABLE Classes;");
        System.out.println("DROP TABLE Grades;");
        System.out.println("DROP TABLE Assignment_types;");
        System.out.println("DROP TABLE Departments;");
        System.out.println("DROP TABLE Rosters;");
        System.out.println("DROP TABLE Assignments;");
    }

    public static void generateStudents(int n) {
        ArrayList<String> students = getFileData("src/names.txt");
        for (int i = 0; i < n; i++) {
            String first_name = students.get((int) (Math.random() * students.size()));
            String last_name = students.get((int) (Math.random() * students.size()));
            first_name = first_name.substring(0, 1).toUpperCase() + first_name.substring(1);
            last_name = last_name.substring(0, 1).toUpperCase() + last_name.substring(1);
            System.out.println("INSERT INTO Students ( first_name, last_name, student_id ) VALUES ( '" + first_name + "', '" + last_name + "', " + (i + 1) + " );");
        }
    }

    public static ArrayList<String> generateDepartments() {
        String[] departments = teachers.get(1).split(",");
        departments = Arrays.stream(departments).distinct().toArray(String[]::new);
        departments = Arrays.stream(departments).filter(d ->
                        ! (d.equals("Brooklyn Tech Principal") ||
                        d.equals("Administration - Supervisors") ||
                        d.equals("COSA (Coordinator of Student Activities)") ||
                        d.equals("Guidance Counselors") ||
                        d.equals("Health and Safety") ||
                        d.equals("Support Staff") ||
                        d.equals("Teachers"))).toArray(String[]::new);
        for (int i = 0; i < departments.length; i++) {
            System.out.println("INSERT INTO Departments ( department_id, department_name ) VALUES ( " + (i + 1) + ", '" + departments[i] + "' );");
        }
        return new ArrayList<String>(Arrays.asList(departments));
    }
    public static ArrayList<String> generateCourseTypes() {
        String[] duplicatesIncludedCourseTypes = courses.stream().map(c -> c.split(",")[2]).toArray(String[]::new);
        String[] courseTypes = Arrays.stream(duplicatesIncludedCourseTypes).distinct().toArray(String[]::new);
        for (int i = 0; i < courseTypes.length; i++) {
            System.out.println("INSERT INTO Course_types ( type_id, type_name ) VALUES ( " + (i + 1) + ", '" + courseTypes[i] + "' );");
        }
        return new ArrayList<String>(Arrays.asList(courseTypes));
    }

    public static void generateCourses() {
        for (int i = 0; i < courses.size(); i++) {
            String[] currentCourse = courses.get(i).split(",");
            System.out.println("INSERT INTO courses ( course_id, course_name, type_id ) VALUES ( " + (i + 1) + ", '" + currentCourse[1] + "', " + (course_types.indexOf(currentCourse[2]) + 1) + " );");
        }
    }

    public static ArrayList<String> generate_assignments(int n) {
        for (int i = 0; i < assignmentTypes.length; i++) {
            System.out.println("INSERT INTO Assignment_types ( type_id , type_name ) VALUES (" + (i + 1) + ", '" + assignmentTypes[i] + "');");
        }

        ArrayList<String> assignments = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String assignmentName = "Assignment " + (i + 1);
            assignments.add(assignmentName);
            int assignmentTypeInt = (int) (Math.random() * assignmentTypes.length) + 1;
            System.out.println("INSERT INTO Assignments ( assignment_name, assignment_id, type_id ) VALUES ( '" + assignmentName + "', " + (i + 1) + ", " + assignmentTypeInt + ");");
        }
        return assignments;
    }

    public static void generate_teachers(int n) {
        ArrayList<String> teachers = getFileData("src/teachernames.csv");
        String[] teacherNames = teachers.get(0).split(",");
        String[] departmentNames = teachers.get(1).split(",");
        int skippedTeachers = 0;
        for (int i = 0; i < teacherNames.length || i < n; i++) {
            String[] teacherSplit = teacherNames[i].split(" ");
            String teacherFirstName = teacherSplit[0];
            String teacherLastName = teacherSplit[teacherSplit.length-1];
            int departmentId = departments.indexOf(departmentNames[i]);
            if (departmentId == -1) {
                skippedTeachers += 1;
                continue;
            }
            System.out.println("INSERT INTO Teachers ( first_name, last_name, teacher_id, department_id ) VALUES ( '" + teacherFirstName + "', '" + teacherLastName + "', " + (i-skippedTeachers+1) + " , " + departmentId + ",) ;");
        }

    }

    public static ArrayList<String> getFileData(String fileName) {
        ArrayList<String> fileData = new ArrayList<String>();
        try {
            File f = new File(fileName);
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (!line.equals(""))
                    fileData.add(line);
            }
            return fileData;
        }
        catch (FileNotFoundException e) {
            return fileData;
        }
    }
}