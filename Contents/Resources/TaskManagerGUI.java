import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;
import javax.swing.border.EmptyBorder;
import java.nio.file.Paths;

public class TaskManagerGUI {
    private static DefaultListModel<String> listModel;
    private static JList<String> taskList;
        
    private static final String CSV_FILE = Paths.get(System.getProperty("user.dir"), "Contents", "Resources", "tasks.csv").toString();
    private static final String PROPERTIES_FILE = Paths.get(System.getProperty("user.dir"), "Contents", "Resources", "app.properties").toString();
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new ModernFrame("To-Do List");
            
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Load frame position from properties file
            loadFramePosition(frame);

            // Variables to store initial position
            final Point[] initialClick = {new Point()};

            // Add MouseListener to detect mouse press
            frame.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    initialClick[0] = e.getPoint();
                    frame.getComponentAt(initialClick[0]);
                }
            });

            // Add MouseMotionListener to detect mouse drag
            frame.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    // Get the current location of the frame
                    int thisX = frame.getLocation().x;
                    int thisY = frame.getLocation().y;

                    // Determine how much the mouse moved since the initial click
                    int xMoved = e.getX() - initialClick[0].x;
                    int yMoved = e.getY() - initialClick[0].y;

                    // Move frame to this position
                    int X = thisX + xMoved;
                    int Y = thisY + yMoved;
                    frame.setLocation(X, Y);
                }
            });

            // List model and JList
            listModel = new DefaultListModel<>();
            taskList = new JList<>(listModel);
            taskList.setFont(new Font("Arial", Font.PLAIN, 14));        
            taskList.setBorder(new EmptyBorder(25, 25, 25, 25)); // Adjust the padding values as needed
            taskList.setForeground(Color.BLACK); // Set text color to black
            taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(taskList);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            frame.add(scrollPane, BorderLayout.CENTER);

            // Load tasks from CSV file
            loadTasksFromCSV();

            // Panel for buttons
            JPanel panel = new JPanel(); // Use JPanel instead of RoundedPanel
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

            // Add button
            JButton addButton = createModernButton("Add Task", new Color(70, 130, 180), Color.BLACK);
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String task = JOptionPane.showInputDialog(frame, "Enter task:");
                    if (task != null && !task.trim().isEmpty()) {
                        listModel.addElement(task);
                        saveTasksToCSV();
                    }
                }
            });
            panel.add(addButton);

            // Remove button
            JButton removeButton = createModernButton("Remove Task", new Color(255, 69, 58), Color.BLACK);
            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = taskList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        listModel.remove(selectedIndex);
                        saveTasksToCSV();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Select a task to remove.");
                    }
                }
            });
            panel.add(removeButton);

            // Exit button
            JButton exitButton = createModernButton("Exit", new Color(255, 105, 97), Color.BLACK);
            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveFramePosition(frame);
                    System.exit(0);
                }
            });
            panel.add(exitButton);

            frame.add(panel, BorderLayout.SOUTH);
            frame.setVisible(true);

            // Save frame position on close
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    saveFramePosition(frame);
                }
            });
        });
    }

    // Custom button factory with modern styling
    private static JButton createModernButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor); // Set text color to black
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

    // Custom frame class without rounded corners
    static class ModernFrame extends JFrame {
        public ModernFrame(String title) {
            super(title);
            setUndecorated(true); // Remove default window decorations
            setBackground(new Color(240, 240, 240)); // Solid background color
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            // No rounded corners
        }
    }

    // Load tasks from CSV file
    private static void loadTasksFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                listModel.addElement(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save tasks to CSV file
    private static void saveTasksToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (int i = 0; i < listModel.size(); i++) {
                bw.write(listModel.getElementAt(i));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load frame position from properties file
    private static void loadFramePosition(JFrame frame) {
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
    private static void saveFramePosition(JFrame frame) {
        Properties props = new Properties();
        props.setProperty("frame.x", String.valueOf(frame.getX()));
        props.setProperty("frame.y", String.valueOf(frame.getY()));
        try (OutputStream output = new FileOutputStream(PROPERTIES_FILE)) {
            props.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}