import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

public class GeneralPathShape extends JPanel{

    @Override
    protected void paintComponent(Graphics g) {
         super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // used to build custom shape using 5 primitives 
        GeneralPath shape = new GeneralPath();
        // g2 int -> float
        // 1.8 double
        g2.translate(300, 200);
        g2.scale(100, 100);

        shape.moveTo(-1.8f, 0f);
        shape.quadTo(0f, 1.6f, 1.8f, 0f);
         shape.quadTo(0f, -1.6f, -1.8f, 0f);
        shape.closePath();
        // (x1,y1,x2,y2)
        // x1,y1 - curve controlling point
        // x2,y2 - end point of the curve
        


    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Custom Shape");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new GeneralPathShape());
        frame.setSize(1000, 1000);
        frame.setVisible(true);
    }
    
}
