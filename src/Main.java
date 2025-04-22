import java.io.File;
import java.io.FileNotFoundException;
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
    static ArrayList<String> assignments;
    static HashMap<Integer, ArrayList<Integer>> classIdsByPeriod;

    public static void main(String[] args) {
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
        generateClasses();
//        generateStudents(5000);
//        generateAssignments();
//        generateTeachers(teachers.get(0).split(",").length);
//        generateGrades();
//        generateRosters();
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
        System.out.println("CREATE TABLE Classes ( course_id integer, class_period integer, teacher_id integer, classroom_id integer, class_id integer PRIMARY KEY, FOREIGN KEY (course_id) references Courses(course_id), FOREIGN KEY (teacher_id) references Teachers(teacher_id), FOREIGN KEY (classroom_id) references Classrooms(classroom_id) );");
        System.out.println("CREATE TABLE Grades ( assignment_id integer, student_id integer, grade float, FOREIGN KEY ( assignment_id ) REFERENCES Assignments( assignment_id ), FOREIGN KEY ( student_id) REFERENCES Students( student_id) );");
        System.out.println("CREATE TABLE Rosters ( student_id integer, class_id integer, FOREIGN KEY (student_id) REFERENCES Students(student_id), FOREIGN KEY (class_id) REFERENCES Classes(class_id) );");
    }

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
        return new ArrayList<>(Arrays.asList(departments));
    }

    public static ArrayList<String> generateCourseTypes() {
        String[] duplicatesIncludedCourseTypes = courses.stream().map(c -> c.split(",")[2]).toArray(String[]::new);
        String[] courseTypes = Arrays.stream(duplicatesIncludedCourseTypes).distinct().toArray(String[]::new);
        for (int i = 0; i < courseTypes.length; i++) {
            System.out.println("INSERT INTO CourseTypes ( type_id, type_name ) VALUES ( " + (i + 1) + ", '" + courseTypes[i] + "' );");
        }
        return new ArrayList<>(Arrays.asList(courseTypes));
    }

    public static void generateCourses() {
        for (int i = 0; i < courses.size(); i++) {
            String[] currentCourse = courses.get(i).split(",");
            System.out.println("INSERT INTO Courses ( course_id, course_name, type_id ) VALUES ( " + (i + 1) + ", '" + currentCourse[1] + "', " + (courseTypes.indexOf(currentCourse[2]) + 1) + " );");
        }
    }

    public static void printAssignment(int id, int type) {
        String assignmentName = "Assignment " + (id + 1);
        assignments.add(assignmentName);
        System.out.println("INSERT INTO Assignments ( assignment_name, assignment_id, type_id ) VALUES ( '" + assignmentName + "', " + (id + 1) + ", " + type + ");");
    }

    // TODO: Make it do 12 minor 3 major ty very much
    public static void generateAssignments() {
        for (int i = 0; i < assignmentTypes.length; i++) {
            System.out.println("INSERT INTO AssignmentTypes ( type_id , type_name ) VALUES (" + (i + 1) + ", '" + assignmentTypes[i] + "');");
        }

        assignments = new ArrayList<>();

        int assignmentId = 1;

        for (int i = 0; i < classes.size(); i++) {
            for (int maj = 0; maj < 3; maj++) {
                printAssignment(assignmentId, 0);
                assignmentId++;
            }
            for (int min = 0; min < 12; min++) {
                printAssignment(assignmentId, 1);
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
                    System.out.println("INSERT INTO Classrooms ( classroom_id, classroom_name ) VALUES ( " + (i + 1) + ", '" + className + "') ;");
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
                periods_to_rooms_to_courses.set(count, periods_to_rooms_to_courses.get(i) + "," + courses.get(i).split(",")[1]);
                count++;
            }
        }

        for (String periodsToRoomsToCours : periods_to_rooms_to_courses) {
            System.out.println(periodsToRoomsToCours);
        }

        periods_to_rooms_to_courses = new ArrayList<>(Arrays.asList(periods_to_rooms_to_courses.stream().filter(a -> a.split(",").length == 3).toArray(String[]::new)));

        for (int teacher = 1; teacher <= teachers.get(0).split(",").length; teacher++) {
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
                    String printStr = "INSERT INTO Classes ( class_id, course_id, class_period, teacher_id, classroom_id ) VALUES ( " + class_id + ", " + course_id + ", " + period + ", " + teacher + ", " + (classrooms.indexOf(course[1]) + 1) + " );";
                    System.out.println(printStr);
                    periods_to_rooms_to_courses.remove(random);
                    class_id++;
                }
            }
        }
    }

    public static void generateGrades() {
        for (int assignment_id = 1; assignment_id <= assignments.size(); assignment_id++) {
            int numberOfStudentsToGrade = 200 + (int)(Math.random() * 800);

            Set<Integer> studentIds = new HashSet<>();
            while (studentIds.size() < numberOfStudentsToGrade && studentIds.size() < students.size()) {
                studentIds.add(Integer.valueOf((int)(Math.random() * students.size()) + 1));
            }

            for (int student_id : studentIds) {
                double grade = Math.round((Math.random() * 100) * 10.0) / 10.0;
                System.out.println("INSERT INTO Grades ( assignment_id, student_id, grade ) VALUES ( " +
                        assignment_id + ", " + student_id + ", " + grade + " );");
            }
        }
    }

    public static void generateRosters() {
        for (int student_id = 1; student_id <= students.size(); student_id++) {
            for (int period = 1; period <= 10; period++) {
                ArrayList<Integer> availableClasses = classIdsByPeriod.get(period);

                if (!availableClasses.isEmpty()) {
                    int randomIndex = (int)(Math.random() * availableClasses.size());
                    int class_id = availableClasses.get(randomIndex);

                    System.out.println("INSERT INTO Rosters ( student_id, class_id ) VALUES ( " +
                            student_id + ", " + class_id + " );");
                }
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
            return fileData;
        }
    }
}
