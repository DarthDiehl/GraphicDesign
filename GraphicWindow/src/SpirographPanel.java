import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.Graphics2D;

public class SpirographPanel extends JPanel{

    public SpirographPanel() {
        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.WHITE);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Parameters for the spirograph
        // R = radius of fixed circle
        // r = radius of rolling circle
        // d = distance from center of rolling circle to drawing point
        int nPoints = 1000;
        int r1 = 60; // Radius of the fixed circle
        int r2 = 50;  // Radius of the rolling circle
        int p = 70;  // Distance from the center of the rolling circle to the drawing point

        g2.translate(300, 200); // Move origin to center of panel

        int x1 = (int) (r1 + r2 - p);
        int y1 = 0;
        int x2, y2;

        for (double i = 0; i <= nPoints; i++) {
            double t = i * Math.PI/90;
            x2 = (int) ((r1 + r2) * Math.cos(t) - p * Math.cos(((r1 + r2) * t) / r2));
            y2 = (int) ((r1 + r2) * Math.sin(t) - p * Math.sin(((r1 + r2) * t) / r2));
            g2.drawLine(x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
        }
    }
}
