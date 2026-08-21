import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;


public class ParametricCirclePanel extends JPanel {
   
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Paremeters for the parametric circle
        // x^2 + y^2 = r^2
        // y = sqrt(r^2 - x^2)
        // y = -sqrt(r^2 - x^2)

        // x = r * cos(t)
        // y = r * sin(t)

        int cx = 300;
        int cy = 200;
        int a = 120;
        int b = 70;
        
    // Draw the parametric circle            t += 0.01
        // 0 to 2π for one full circle/rotation
        double step = 0.1;
        for (double t = 0; t <= 2 * Math.PI; t += step) {
            int x = (int) (cx + a * Math.cos(t));
            int y = (int) (cy + b * Math.sin(t));
            g2.fillRect(x, y, 2, 2); // Draw a small rectangle at (x, y)
        }
    }
}
