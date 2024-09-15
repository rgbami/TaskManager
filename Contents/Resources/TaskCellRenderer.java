import javax.swing.*;
import java.awt.*;

public class TaskCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Task) {
            Task task = (Task) value;
            if (task.isCompleted()) {
                label.setText("<html><strike>" + task.toString() + "</strike></html>");
            } else {
                label.setText(task.toString());
            }
        }
        return label;
    }
}