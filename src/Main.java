import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    static ArrayList<String> departments;
    static String[] assignmentTypes = {"Classwork", "Homework", "Quiz", "Exam", "Project"};
    static ArrayList<String> courseTypes;
    static ArrayList<String> courses;
    static ArrayList<String> teachers;
    static ArrayList<String> students;
    static ArrayList<String> classrooms;
    static ArrayList<String> assignments;


    public static void main(String[] args) {
        dropDatabase();
        createDatabase();
        courses = getFileData("src/coursenames.csv");
        teachers = getFileData("src/teachernames.csv");
        departments = generateDepartments();
        courseTypes = generateCourseTypes();
        generateCourses();
        generateClassrooms(720);
        generateClasses();
        generateStudents(5000);
        generateAssignments(courses.size() * 15);
        generateTeachers(teachers.getFirst().split(",").length);
    }

    public static void createDatabase() {
        System.out.println("CREATE TABLE Students ( first_name varchar(255), last_name varchar(255), student_id integer Primary Key );");
        System.out.println("CREATE TABLE CourseTypes ( type_name varchar(255), type_id integer PRIMARY KEY );");
        System.out.println("CREATE TABLE Classrooms ( classroom_name varchar(255), classroom_id integer PRIMARY KEY );");
        System.out.println("CREATE TABLE AssignmentTypes ( type_id integer PRIMARY KEY, type_name varchar(255) );");
        System.out.println("CREATE TABLE Departments ( department_id integer PRIMARY KEY, department_name varchar(255) );");
        System.out.println("CREATE TABLE Assignments ( assignment_name varchar(255), assignment_id integer PRIMARY KEY, type_id integer, FOREIGN KEY (type_id) REFERENCES AssignmentTypes(type_id) );");
        System.out.println("CREATE TABLE Courses ( course_name varchar(255), type_id integer, course_id integer PRIMARY KEY, FOREIGN KEY (type_id) REFERENCES CourseTypes(type_id) );");
        System.out.println("CREATE TABLE Teachers ( first_name varchar(255), last_name varchar(255), teacher_id integer PRIMARY KEY, department_id integer, FOREIGN KEY (department_id) REFERENCES Departments(department_id) );");
        System.out.println("CREATE TABLE Classes ( course_id integer, class_time date, teacher_id integer, classroom_id integer, class_id integer PRIMARY KEY, FOREIGN KEY (course_id) references Courses(course_id), FOREIGN KEY (teacher_id) references Teachers(teacher_id), FOREIGN KEY (classroom_id) references Classrooms(classroom_id) );");
        System.out.println("CREATE TABLE Grades ( assignment_id integer, student_id integer, grade float, FOREIGN KEY ( assignment_id ) REFERENCES Assignments( assignment_id ), FOREIGN KEY ( student_id) REFERENCES Students( student_id) );");
        System.out.println("CREATE TABLE Rosters ( student_id integer, class_id integer, FOREIGN KEY (student_id) REFERENCES Students(student_id), FOREIGN KEY (class_id) REFERENCES Classes(class_id) );");}

    public static void dropDatabase() {
        System.out.println("DROP TABLE IF EXISTS Rosters;");
        System.out.println("DROP TABLE IF EXISTS Grades;");
        System.out.println("DROP TABLE IF EXISTS Classes;");
        System.out.println("DROP TABLE IF EXISTS Teachers;");
        System.out.println("DROP TABLE IF EXISTS Courses;");
        System.out.println("DROP TABLE IF EXISTS Assignments;");
        System.out.println("DROP TABLE IF EXISTS Departments;");
        System.out.println("DROP TABLE IF EXISTS AssignmentTypes;");
        System.out.println("DROP TABLE IF EXISTS Classrooms;");
        System.out.println("DROP TABLE IF EXISTS CourseTypes;");
        System.out.println("DROP TABLE IF EXISTS Students;");
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
            System.out.println("INSERT INTO CourseTypes ( type_id, type_name ) VALUES ( " + (i + 1) + ", '" + courseTypes[i] + "' );");
        }
        return new ArrayList<String>(Arrays.asList(courseTypes));
    }

    public static void generateCourses() {
        for (int i = 0; i < courses.size(); i++) {
            String[] currentCourse = courses.get(i).split(",");
            System.out.println("INSERT INTO Courses ( course_id, course_name, type_id ) VALUES ( " + (i + 1) + ", '" + currentCourse[1] + "', " + (courseTypes.indexOf(currentCourse[2]) + 1) + " );");
        }
    }

    // TODO: Make it do 12 minor 3 major ty very much
    public static void generateAssignments(int n) {
        for (int i = 0; i < assignmentTypes.length; i++) {
            System.out.println("INSERT INTO AssignmentTypes ( type_id , type_name ) VALUES (" + (i + 1) + ", '" + assignmentTypes[i] + "');");
        }

        assignments = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String assignmentName = "Assignment " + (i + 1);
            assignments.add(assignmentName);
            int assignmentTypeInt = (int) (Math.random() * assignmentTypes.length) + 1;
            System.out.println("INSERT INTO Assignments ( assignment_name, assignment_id, type_id ) VALUES ( '" + assignmentName + "', " + (i + 1) + ", " + assignmentTypeInt + ");");
        }
    }

    // TODO: Make sure that the index of a teacher is the id of the teacher -1
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
            System.out.println("INSERT INTO Teachers ( first_name, last_name, teacher_id, department_id ) VALUES ( '" + teacherFirstName + "', '" + teacherLastName + "', " + (i-skippedTeachers+1) + " , " + departmentId + ",) ;");
        }
    }

    // Max 720, closer to 720 is worse
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
                int sideInt = (int) (Math.random() * 4);
                String sideStr = "";
                switch (sideInt) {
                    case 0:
                        sideStr = "N";
                        break;
                    case 1:
                        sideStr = "E";
                        break;
                    case 2:
                        sideStr = "S";
                        break;
                    case 3:
                        sideStr = "W";
                        break;
                }
                String roomStr = String.valueOf((int) (Math.random() * 20 + 1));
                String className = floorStr + sideStr + roomStr;
                unique = !classrooms.contains(className);
                if (unique) {
                    System.out.println("INSERT INTO Classrooms ( classroom_id, classroom_name ) VALUES ( " + (i + 1) + ", '" + className + "') ;");
                    classrooms.add(className);
                }
            }
        }
    }

    public static void generateClasses() {
        int class_id = 1;
        ArrayList<String> periods_to_rooms_to_courses = new ArrayList<>();
        for (String classroom : classrooms) {
            for (int period = 1; period <= 10; period++) {
                periods_to_rooms_to_courses.add(period + "," + classroom);
            }
        }

        for (int i = 0; i < courses.size(); i++) {
            periods_to_rooms_to_courses.set(i, periods_to_rooms_to_courses.get(i) + "," + courses.get(i).split(",")[1]);
            System.out.println(periods_to_rooms_to_courses.get(i));
        }
        periods_to_rooms_to_courses = new ArrayList<>(Arrays.asList(periods_to_rooms_to_courses.stream().filter(a -> a.split(",").length == 3).toArray(String[]::new)));

        for (int teacher = 1; teacher <= teachers.getFirst().split(",").length; teacher++) {
            for (int period  = 0; period < 10; period++) {
                if (!periods_to_rooms_to_courses.isEmpty()) {
                    int random = (int) (Math.random() * periods_to_rooms_to_courses.size() - 1);
                    String[] course = periods_to_rooms_to_courses.get(random).split(",");
                    int course_id = -1;

                    for (int i = 0; i < courses.size(); i++) {
                        if (courses.get(i).split(",")[1].equals(periods_to_rooms_to_courses.get(random).split(",")[2])) {
                            course_id = i + 1;
                        }
                    }
                    periods_to_rooms_to_courses.remove(random);
                    System.out.println(courses);
                    System.out.println("INSERT INTO Classes ( class_id, course_id, class_period, teacher_id, classroom_id ) VALUES ( " + class_id + ", " + course_id + ", " + period + ", " + teacher + ", '" + course[1] + "' );");
                    class_id++;
                }
            }
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