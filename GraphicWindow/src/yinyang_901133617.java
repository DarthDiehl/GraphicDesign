import javax.swing.*;
import java.awt.*;

public class yinyang_901133617 {
    public static void main(String[] args) {
        // Create the main window
        JFrame frame = new JFrame("Yin Yang");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.add(new YinYangPanel());
        frame.setVisible(true);
    }

    static class YinYangPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            // Requirement 1
            int size = 200; 
            // Requirement 3
            int centerX = width / 2;
            int centerY = height / 2;
            int x = centerX - size / 2;
            int y = centerY - size / 2;

            // Draw black half
            g2d.setColor(Color.BLACK);
            g2d.fillArc(x, y, size, size , 270, 180);

            // Draw white half
            g2d.setColor(Color.WHITE);
            g2d.fillArc(x, y, size, size, 90, 180);

            // Requirement 4 - conflicts with upper lobe outline here.
            //g2d.setColor(Color.BLACK);
            //g2d.drawOval(x, y, size, size);

            // Lobe size: diameter is half of size
            int lobeSize = size / 2;    // 100
            int lobeR = lobeSize / 2;   // 50

            // Top lobe center is (centerX, centerY - size/4)
            int topLobeX = centerX - lobeR;
            int topLobeY = centerY - (size / 4) - lobeR;

            // Bottom lobe center is (centerX, centerY + size/4)
            int botLobeX = centerX - lobeR;
            int botLobeY = centerY + (size / 4) - lobeR;

            // Draw WHITE lobe circle on TOP
            g2d.setColor(Color.WHITE);
            g2d.fillOval(topLobeX, topLobeY, lobeSize, lobeSize);

            // Draw BLACK lobe circle on BOTTOM
            g2d.setColor(Color.BLACK);
            g2d.fillOval(botLobeX, botLobeY, lobeSize, lobeSize);

            // Requirement 2
            int dotSize = lobeSize / 10;    // 10
            int dotR = dotSize / 2;     // 5

            // Dot centers match lobe centers
            int topDotX = centerX - dotR;
            int topDotY = (centerY - (size / 4)) - dotR;

            int botDotX = centerX - dotR;
            int botDotY = (centerY + (size / 4)) - dotR;

            // Top dot is BLACK
            g2d.setColor(Color.BLACK);
            g2d.fillOval(topDotX, topDotY, dotSize, dotSize);

            // Bottom dot is WHITE
            g2d.setColor(Color.WHITE);
            g2d.fillOval(botDotX, botDotY, dotSize, dotSize);

            // Requirement 4
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, size, size);
        }
    }
}
