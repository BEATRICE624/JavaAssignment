import javax.swing.*;
import java.awt.*;

public class TaskDialog extends JDialog {
    private JTextField nameField, dateField;
    private JComboBox<String> priorityBox, statusBox;

    public TaskDialog(JFrame parent, Task task) {
        super(parent, task == null ? "Add Task" : "Edit Task", true);
        setSize(400, 450);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Task Name:");
        nameField = new JTextField(task == null ? "" : task.getName());

        JLabel priorityLabel = new JLabel("Priority:");
        priorityBox = new JComboBox<>(new String[] { "High", "Medium", "Low" });
        if (task != null) priorityBox.setSelectedItem(task.getPriority());

        JLabel dueDateLabel = new JLabel("Due Date:");
        dateField = new JTextField(task == null ? "" : task.getDueDate());

        JLabel statusLabel = new JLabel("Status:");
        statusBox = new JComboBox<>(new String[] { "Pending", "In Progress", "Completed" });
        if (task != null) statusBox.setSelectedItem(task.getStatus());

        JButton saveButton = new JButton(task == null ? "Add Task" : "Save Changes");
        saveButton.addActionListener(e -> saveTask(task));

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

        add(panel);
    }

    private void saveTask(Task task) {
        String name = nameField.getText().trim();
        String priority = (String) priorityBox.getSelectedItem();
        String dueDate = dateField.getText().trim();
        String status = (String) statusBox.getSelectedItem();

        if (task == null) {
            task = new Task(name, priority, dueDate, status);
        } else {
            task.setName(name);
            task.setPriority(priority);
            task.setDueDate(dueDate);
            task.setStatus(status);
        }

        dispose();
    }
}
