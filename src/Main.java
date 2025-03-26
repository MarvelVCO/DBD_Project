import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> teachers = getFileData("src/teachernames.txt");
        create_database();
    }

    public static void create_database() {
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

    public static void drop_database() {
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

    public static void students() {
        ArrayList<String> students = getFileData("src/names.txt");
        for (int i = 0; i < 500; i++) {
            String first_name = students.get((int) (Math.random() * students.size()));
            String last_name = students.get((int) (Math.random() * students.size()));
            first_name = first_name.substring(0, 1).toUpperCase() + first_name.substring(1);
            last_name = last_name.substring(0, 1).toUpperCase() + last_name.substring(1);
            System.out.println("sqlite3 DB \"INSERT INTO Students ( first_name, last_name ) VALUES ( '" + first_name + "', '" + last_name + "' )\";");
        }
    }

    public static void teachers(int n) {
        ArrayList<String> teachers = getFileData("src/teachernames.csv");
        String[] teacherNames = teachers.get(0).split(",");
        String[] departmentNames = teachers.get(1).split(",");
        for (int i = 0; i < teacherNames.length; i++) {
            System.out.println("(" + teacherNames[i] + ") , (" + departmentNames[i] + ")");
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