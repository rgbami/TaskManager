import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class TaskManagerUtils {
    private static final String PROPERTIES_FILE = "app.properties";
    private static final String CSV_FILE = "tasks.csv";

    // Load frame position from properties file
    public static void loadFramePosition(JFrame frame) {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            props.load(input);
            int x = Integer.parseInt(props.getProperty("frame.x", "100"));
            int y = Integer.parseInt(props.getProperty("frame.y", "100"));
            frame.setLocation(x, y);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // Save frame position to properties file
    public static void saveFramePosition(JFrame frame) {
        Properties props = new Properties();
        props.setProperty("frame.x", String.valueOf(frame.getX()));
        props.setProperty("frame.y", String.valueOf(frame.getY()));
        try (OutputStream output = new FileOutputStream(PROPERTIES_FILE)) {
            props.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load tasks from CSV file
    public static void loadTasksFromCSV(DefaultListModel<Task> listModel) {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    boolean completed = Boolean.parseBoolean(parts[2]);
                    listModel.addElement(new Task(parts[0], parts[1], completed));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save tasks to CSV file
    public static void saveTasksToCSV(DefaultListModel<Task> listModel) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (int i = 0; i < listModel.size(); i++) {
                Task task = listModel.getElementAt(i);
                bw.write(task.getName() + "," + task.getDescription() + "," + task.isCompleted());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}