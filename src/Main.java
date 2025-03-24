import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        students();
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