import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class TaskManager {
    private static DefaultListModel<Task> listModel;
    private static JList<Task> taskList;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new ModernFrame("TaskList");
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Load frame position from properties file
            TaskManagerUtils.loadFramePosition(frame);

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
            taskList.setCellRenderer(new TaskCellRenderer());
            JScrollPane scrollPane = new JScrollPane(taskList);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            frame.add(scrollPane, BorderLayout.CENTER);

            // Load tasks from CSV file
            TaskManagerUtils.loadTasksFromCSV(listModel);

            // Panel for buttons
            JPanel panel = new JPanel(); // Use JPanel instead of RoundedPanel
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

            // Add task button
            JButton addButton = createButton("Add Task", new Color(70, 130, 180), Color.BLACK);
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Task task = showAddTaskDialog(frame);
                    if (task != null) {
                        listModel.addElement(task);
                        TaskManagerUtils.saveTasksToCSV(listModel);
                    }
                }
            });
            panel.add(addButton);

            // Remove task button
            JButton removeButton = createButton("Remove Task", new Color(255, 69, 58), Color.BLACK);
            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = taskList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        listModel.remove(selectedIndex);
                        TaskManagerUtils.saveTasksToCSV(listModel);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Select a task to remove.");
                    }
                }
            });
            panel.add(removeButton);

            // Exit button
            JButton exitButton = createButton("Exit", new Color(255, 105, 97), Color.BLACK);
            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    TaskManagerUtils.saveFramePosition(frame);
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
                    TaskManagerUtils.saveFramePosition(frame);
                }
            });

            // Add right-click context menu for editing and completing tasks
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem editMenuItem = new JMenuItem("Edit Task");
            JMenuItem completioneMenuItem = new JMenuItem("Toggle Completion");
            popupMenu.add(editMenuItem);
            popupMenu.add(completioneMenuItem);

            taskList.setComponentPopupMenu(popupMenu);

            editMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = taskList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        Task selectedTask = listModel.getElementAt(selectedIndex);
                        Task updatedTask = showEditTaskDialog(frame, selectedTask);
                        if (updatedTask != null) {
                            listModel.setElementAt(updatedTask, selectedIndex);
                            TaskManagerUtils.saveTasksToCSV(listModel);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Select a task to edit.");
                    }
                }
            });

            completioneMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = taskList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        Task selectedTask = listModel.getElementAt(selectedIndex);
                        selectedTask.setCompleted(!selectedTask.isCompleted());
                        listModel.setElementAt(selectedTask, selectedIndex);
                        TaskManagerUtils.saveTasksToCSV(listModel);
                        taskList.repaint();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Select a task to change its completion status.");
                    }
                }
            });
        });
    }

    // Custom button factory with modern styling
    private static JButton createButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor); // Set text color to black
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

    // Show dialog to add a new task with description
    private static Task showAddTaskDialog(JFrame frame) {
        JTextField nameField = new JTextField(10);
        JTextField descriptionField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Task Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            if (!name.isEmpty() && !description.isEmpty()) {
                return new Task(name, description, false);
            }
        }
        return null;
    }

    // Show dialog to edit an existing task
    private static Task showEditTaskDialog(JFrame frame, Task task) {
        JTextField nameField = new JTextField(task.getName(), 10);
        JTextField descriptionField = new JTextField(task.getDescription(), 20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Task Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            if (!name.isEmpty() && !description.isEmpty()) {
                return new Task(name, description, task.isCompleted());
            }
        }
        return null;
    }
}