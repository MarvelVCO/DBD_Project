import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    static ArrayList<String> departments;
    static String[] assignmentTypes = {"Major", "Minor"};
    static ArrayList<String> courseTypes;
    static ArrayList<String> courses;
    static ArrayList<String> classes;
    static ArrayList<String> teachers;
    static ArrayList<String> students;
    static ArrayList<String> classrooms;
    static ArrayList<Integer> assignments;
    static HashMap<Integer, ArrayList<Integer>> classIdsByPeriod;
    static HashMap<Integer, ArrayList<Integer>> studentToClasses;
    static PrintWriter outputWriter;

    public static void main(String[] args) {
        try {
            outputWriter = new PrintWriter(new FileWriter("db"));

            dropDatabase();
            createDatabase();
            courses = getFileData("src/coursenames.csv");
            teachers = getFileData("src/teachernames.csv");
            departments = generateDepartments();
            courseTypes = generateCourseTypes();
            generateCourses();
            generateClassrooms(720);
            classIdsByPeriod = new HashMap<>();
            for (int i = 1; i <= 10; i++) {
                classIdsByPeriod.put(Integer.valueOf(i), new ArrayList<>());
            }
            generateTeachers(teachers.get(0).split(",").length);
            generateClasses();
            generateAssignments();
            generateStudents(5000);
            generateRosters();
            generateGrades();

            outputWriter.close();
            System.out.println("Database script successfully written to file 'db'");
        } catch (IOException e) {
            System.err.println("Error writing to output file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createDatabase() {
        outputWriter.println("CREATE TABLE Students ( first_name varchar(255), last_name varchar(255), student_id integer Primary Key );");
        outputWriter.println("CREATE TABLE CourseTypes ( type_name varchar(255), type_id integer PRIMARY KEY );");
        outputWriter.println("CREATE TABLE Classrooms ( classroom_name varchar(255), classroom_id integer PRIMARY KEY );");
        outputWriter.println("CREATE TABLE AssignmentTypes ( type_id integer PRIMARY KEY, type_name varchar(255) );");
        outputWriter.println("CREATE TABLE Departments ( department_id integer PRIMARY KEY, department_name varchar(255) );");
        outputWriter.println("CREATE TABLE Assignments ( assignment_name varchar(255), assignment_id integer PRIMARY KEY, type_id integer, FOREIGN KEY (type_id) REFERENCES AssignmentTypes(type_id) );");
        outputWriter.println("CREATE TABLE Courses ( course_name varchar(255), type_id integer, course_id integer PRIMARY KEY, FOREIGN KEY (type_id) REFERENCES CourseTypes(type_id) );");
        outputWriter.println("CREATE TABLE Teachers ( first_name varchar(255), last_name varchar(255), teacher_id integer PRIMARY KEY, department_id integer, FOREIGN KEY (department_id) REFERENCES Departments(department_id) );");
        outputWriter.println("CREATE TABLE Classes ( course_id integer, class_period integer, teacher_id integer, classroom_id integer, class_id integer PRIMARY KEY, FOREIGN KEY (course_id) references Courses(course_id), FOREIGN KEY (teacher_id) references Teachers(teacher_id), FOREIGN KEY (classroom_id) references Classrooms(classroom_id) );");
        outputWriter.println("CREATE TABLE Grades ( assignment_id integer, student_id integer, grade float, FOREIGN KEY ( assignment_id ) REFERENCES Assignments( assignment_id ), FOREIGN KEY ( student_id) REFERENCES Students( student_id) );");
        outputWriter.println("CREATE TABLE Rosters ( student_id integer, class_id integer, FOREIGN KEY (student_id) REFERENCES Students(student_id), FOREIGN KEY (class_id) REFERENCES Classes(class_id) );");
    }

    public static void dropDatabase() {
        outputWriter.println("DROP TABLE IF EXISTS Rosters;");
        outputWriter.println("DROP TABLE IF EXISTS Grades;");
        outputWriter.println("DROP TABLE IF EXISTS Classes;");
        outputWriter.println("DROP TABLE IF EXISTS Teachers;");
        outputWriter.println("DROP TABLE IF EXISTS Courses;");
        outputWriter.println("DROP TABLE IF EXISTS Assignments;");
        outputWriter.println("DROP TABLE IF EXISTS Departments;");
        outputWriter.println("DROP TABLE IF EXISTS AssignmentTypes;");
        outputWriter.println("DROP TABLE IF EXISTS Classrooms;");
        outputWriter.println("DROP TABLE IF EXISTS CourseTypes;");
        outputWriter.println("DROP TABLE IF EXISTS Students;");
    }

    public static void generateStudents(int n) {
        ArrayList<String> studentNames = getFileData("src/names.txt");
        students = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String first_name = studentNames.get((int) (Math.random() * studentNames.size()));
            String last_name = studentNames.get((int) (Math.random() * studentNames.size()));
            first_name = first_name.substring(0, 1).toUpperCase() + first_name.substring(1);
            last_name = last_name.substring(0, 1).toUpperCase() + last_name.substring(1);
            students.add(first_name + " " + last_name);
            outputWriter.println("INSERT INTO Students ( first_name, last_name, student_id ) VALUES ( '" + first_name + "', '" + last_name + "', " + (i + 1) + " );");
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
            outputWriter.println("INSERT INTO Departments ( department_id, department_name ) VALUES ( " + (i + 1) + ", '" + departments[i] + "' );");
        }
        return new ArrayList<>(Arrays.asList(departments));
    }

    public static ArrayList<String> generateCourseTypes() {
        String[] duplicatesIncludedCourseTypes = courses.stream().map(c -> c.split(",")[2]).toArray(String[]::new);
        String[] courseTypes = Arrays.stream(duplicatesIncludedCourseTypes).distinct().toArray(String[]::new);
        for (int i = 0; i < courseTypes.length; i++) {
            outputWriter.println("INSERT INTO CourseTypes ( type_id, type_name ) VALUES ( " + (i + 1) + ", '" + courseTypes[i] + "' );");
        }
        return new ArrayList<>(Arrays.asList(courseTypes));
    }

    public static void generateCourses() {
        for (int i = 0; i < courses.size(); i++) {
            String[] currentCourse = courses.get(i).split(",");
            outputWriter.println("INSERT INTO Courses ( course_id, course_name, type_id ) VALUES ( " + (i + 1) + ", '" + currentCourse[1] + "', " + (courseTypes.indexOf(currentCourse[2]) + 1) + " );");
        }
    }

    public static void printAssignment(int id, int type, int classId) {
        String assignmentName = "Assignment " + (id + 1);
        assignments.add(classId);
        outputWriter.println("INSERT INTO Assignments ( assignment_name, assignment_id, type_id ) VALUES ( '" + assignmentName + "', " + (id + 1) + ", " + type + ");");
    }

    public static void generateAssignments() {
        for (int i = 0; i < assignmentTypes.length; i++) {
            outputWriter.println("INSERT INTO AssignmentTypes ( type_id , type_name ) VALUES (" + (i + 1) + ", '" + assignmentTypes[i] + "');");
        }

        assignments = new ArrayList<>();

        int assignmentId = 1;

        for (int i = 0; i < classes.size(); i++) {
            for (int maj = 0; maj < 3; maj++) {
                printAssignment(assignmentId, 1, i);
                assignmentId++;
            }
            for (int min = 0; min < 12; min++) {
                printAssignment(assignmentId, 2, i);
                assignmentId++;
            }
        }
    }

    public static void generateTeachers(int n) {
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
            outputWriter.println("INSERT INTO Teachers ( first_name, last_name, teacher_id, department_id ) VALUES ( '" + teacherFirstName + "', '" + teacherLastName + "', " + (i-skippedTeachers+1) + " , " + (departmentId+1) + ") ;");
        }
    }

    public static void generateClassrooms(int n) {
        classrooms = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            boolean unique = false;
            while (!unique) {
                int floor = (int) (Math.random() * 9);
                String floorStr;
                if (floor == 0) {
                    floorStr = "B";
                } else {
                    floorStr = String.valueOf(floor);
                }
                String sideStr = "";
                int sideInt = (int) (Math.random() * 4);
                if (sideInt == 0) {
                    sideStr = "N";
                }

                if (sideInt == 1) {
                    sideStr = "E";
                }
                if (sideInt == 2) {
                    sideStr = "S";
                }

                if (sideInt == 3) {
                    sideStr = "W";
                }

                String roomStr = String.valueOf((int) (Math.random() * 20 + 1));
                String className = floorStr + sideStr + roomStr;
                unique = !classrooms.contains(className);
                if (unique) {
                    outputWriter.println("INSERT INTO Classrooms ( classroom_id, classroom_name ) VALUES ( " + (i + 1) + ", '" + className + "') ;");
                    classrooms.add(className);
                }
            }
        }
    }

    public static void generateClasses() {
        classes = new ArrayList<>();
        int class_id = 1;
        ArrayList<String> periods_to_rooms_to_courses = new ArrayList<>();
        for (String classroom : classrooms) {
            for (int period = 1; period <= 10; period++) {
                periods_to_rooms_to_courses.add(period + "," + classroom);
            }
        }
        int count = 0;
        while (count < periods_to_rooms_to_courses.size()) {
            for (int i = 0; i < courses.size() && count < periods_to_rooms_to_courses.size(); i++) {
                periods_to_rooms_to_courses.set(count, periods_to_rooms_to_courses.get(count) + "," + courses.get(i).split(",")[1]);
                count++;
            }
        }

        periods_to_rooms_to_courses = new ArrayList<>(Arrays.asList(periods_to_rooms_to_courses.stream().filter(a -> a.split(",").length > 2).toArray(String[]::new)));
        for (int teacher = 1; teacher <= 336; teacher++) {
            for (int period = 1; period <= 10; period++) {
                if (!periods_to_rooms_to_courses.isEmpty()) {
                    String[] course = periods_to_rooms_to_courses.get(0).split(",");
                    int course_id = -1;

                    for (int i = 0; i < courses.size(); i++) {
                        if (courses.get(i).split(",")[1].equals(periods_to_rooms_to_courses.get(0).split(",")[2])) {
                            course_id = i + 1;
                        }
                    }
                    String printStr = "INSERT INTO Classes ( class_id, course_id, class_period, teacher_id, classroom_id ) VALUES ( " + class_id + ", " + course_id + ", " + period + ", " + teacher + ", " + (classrooms.indexOf(course[1]) + 1) + " );";
                    outputWriter.println(printStr);
                    classes.add(printStr);
                    periods_to_rooms_to_courses.remove(0);
                    classIdsByPeriod.get(period).add(class_id);
                    class_id++;
                }
            }
        }
    }

    public static void generateGrades() {
        for (int assignment_id = 1; assignment_id <= assignments.size(); assignment_id++) {
            int classId = assignments.get(assignment_id - 1);
            Set<Integer> studentIdKeys = studentToClasses.keySet();
            for (int key : studentIdKeys) {
                for (int studentClassId : studentToClasses.get(key)) {
                    if (studentClassId == classId) {
                        double grade = Math.round((Math.random() * 100) * 10.0) / 10.0;
                        outputWriter.println("INSERT INTO Grades ( assignment_id, student_id, grade ) VALUES ( " + assignment_id + ", " + key + ", " + grade + " );");
                    }
                }
            }
        }
    }

    public static void generateRosters() {
        studentToClasses = new HashMap<>();
        int class_id = 1;
        for (int period = 1; period <= 10; period++) {
            for (int student_id = 1; student_id <= students.size(); student_id++) {
                ArrayList<Integer> studentClasses = new ArrayList<>();
                if (studentToClasses.containsKey(student_id)) {
                    studentClasses = studentToClasses.get(student_id);
                } else {
                    studentToClasses.put(student_id, studentClasses);
                }
                ArrayList<Integer> availableClasses = classIdsByPeriod.get(period);

                if (!availableClasses.isEmpty()) {
                    outputWriter.println("INSERT INTO Rosters ( student_id, class_id ) VALUES ( " + student_id + ", " + (class_id % classes.size() + 1) + " );");
                    studentClasses.add(class_id % classes.size() + 1);
                }

                studentToClasses.get(student_id).add(class_id);
                class_id+=1;
            }
        }
    }

    public static ArrayList<String> getFileData(String fileName) {
        ArrayList<String> fileData = new ArrayList<>();
        try {
            File f = new File(fileName);
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (!line.isEmpty())
                    fileData.add(line);
            }
            return fileData;
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
            return fileData;
        }
    }
}
