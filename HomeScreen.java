import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeScreen extends JFrame {
    private JPanel taskListPanel;
    private ArrayList<Task> tasks;
    private final String DATA_FILE = "tasks.dat";

    public HomeScreen() {
        tasks = loadTasks();

        setTitle("Task Manager");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        updateTaskList();
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 45, 65));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Task Manager App");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, MMMM d, yyyy").format(new Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setForeground(Color.WHITE);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout());
        footerPanel.setBackground(new Color(240, 240, 240));

        JButton addTaskButton = createStyledButton("Add Task", new Color(46, 204, 113));
        JButton clearTasksButton = createStyledButton("Clear All Tasks", new Color(231, 76, 60));

        addTaskButton.addActionListener(e -> openAddTaskDialog());
        clearTasksButton.addActionListener(e -> clearAllTasks());

        footerPanel.add(addTaskButton);
        footerPanel.add(clearTasksButton);
        return footerPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }

    private void clearAllTasks() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all tasks?",
                "Clear Tasks",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            tasks.clear();
            saveTasks();
            updateTaskList();
        }
    }

    private void openAddTaskDialog() {
        JDialog dialog = new JDialog(this, "Add Task", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Task Name:");
        JTextField nameField = new JTextField();

        JLabel priorityLabel = new JLabel("Priority:");
        JComboBox<String> priorityBox = new JComboBox<>(new String[] { "High", "Medium", "Low" });

        JLabel dueDateLabel = new JLabel("Due Date:");
        JTextField dateField = new JTextField(); 

        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusBox = new JComboBox<>(new String[] { "Pending", "In Progress", "Completed" });

        JButton addButton = createStyledButton("Add Task", new Color(46, 204, 113));
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String priority = (String) priorityBox.getSelectedItem();
            String dueDate = dateField.getText().trim();
            String status = (String) statusBox.getSelectedItem();

            if (name.isEmpty() || dueDate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            tasks.add(new Task(name, priority, dueDate, status));
            tasks.sort((a, b) -> a.getPriorityValue() - b.getPriorityValue());
            saveTasks();
            updateTaskList();
            dialog.dispose();
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(priorityLabel);
        panel.add(priorityBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(dueDateLabel);
        panel.add(dateField); 
        panel.add(Box.createVerticalStrut(10));
        panel.add(statusLabel);
        panel.add(statusBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(addButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void openEditTaskDialog(Task task) {
        JDialog dialog = new JDialog(this, "Edit Task", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Task Name:");
        JTextField nameField = new JTextField(task.getName());

        JLabel priorityLabel = new JLabel("Priority:");
        JComboBox<String> priorityBox = new JComboBox<>(new String[] { "High", "Medium", "Low" });
        priorityBox.setSelectedItem(task.getPriority());

        JLabel dueDateLabel = new JLabel("Due Date:");
        JTextField dateField = new JTextField(task.getDueDate());

        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusBox = new JComboBox<>(new String[] { "Pending", "In Progress", "Completed" });
        statusBox.setSelectedItem(task.getStatus());

        JButton saveButton = createStyledButton("Save Changes", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String priority = (String) priorityBox.getSelectedItem();
            String dueDate = dateField.getText().trim();
            String status = (String) statusBox.getSelectedItem();

            if (name.isEmpty() || dueDate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            task.setName(name);
            task.setPriority(priority);
            task.setDueDate(dueDate);
            task.setStatus(status);

            saveTasks();
            updateTaskList();
            dialog.dispose();
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(priorityLabel);
        panel.add(priorityBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(dueDateLabel);
        panel.add(dateField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(statusLabel);
        panel.add(statusBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(saveButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateTaskList() {
        taskListPanel.removeAll();

        if (tasks.isEmpty()) {
            JLabel noTaskLabel = new JLabel("No tasks available", JLabel.CENTER);
            noTaskLabel.setFont(new Font("Arial", Font.BOLD, 20));
            noTaskLabel.setForeground(Color.RED);
            taskListPanel.setLayout(new GridBagLayout());
            taskListPanel.add(noTaskLabel);
        } else {
            taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
            for (Task task : tasks) {
                JPanel taskPanel = new JPanel(new BorderLayout());
                taskPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                taskPanel.setBackground(Color.WHITE);

                JLabel taskInfo = new JLabel(String.format(
                        "<html><b>Task:</b> %s<br><b>Priority:</b> %s<br><b>Due Date:</b> %s<br><b>Status:</b> <font color='%s'>%s</font></html>",
                        task.getName(), task.getPriority(), task.getDueDate(), task.getStatusColor(),
                        task.getStatus()));
                taskInfo.setBorder(new EmptyBorder(10, 10, 10, 10));

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton editButton = createStyledButton("Edit", new Color(52, 152, 219));
                JButton deleteButton = createStyledButton("Delete", new Color(231, 76, 60));

                editButton.addActionListener(e -> openEditTaskDialog(task));
                deleteButton.addActionListener(e -> {
                    tasks.remove(task);
                    saveTasks();
                    updateTaskList();
                });

                buttonPanel.add(editButton);
                buttonPanel.add(deleteButton);

                taskPanel.add(taskInfo, BorderLayout.CENTER);
                taskPanel.add(buttonPanel, BorderLayout.EAST);
                taskListPanel.add(taskPanel);
            }
        }

        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Task> loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            return (ArrayList<Task>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HomeScreen::new);
    }

    private static class Task implements Serializable {
        private String name, priority, dueDate, status;

        public Task(String name, String priority, String dueDate, String status) {
            this.name = name;
            this.priority = priority;
            this.dueDate = dueDate;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusColor() {
            return switch (status) {
                case "Pending" -> "orange";
                case "In Progress" -> "blue";
                case "Completed" -> "green";
                default -> "black";
            };
        }

        public int getPriorityValue() {
            return switch (priority) {
                case "High" -> 1;
                case "Medium" -> 2;
                default -> 3;
            };
        }
    }
}
