import javax.swing.*;
import java.awt.*;

public class ModernFrame extends JFrame {
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