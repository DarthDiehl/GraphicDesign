import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;

public class ConstructiveAreaGeometry extends JPanel{
    // This class is intended to build complex shapes using set theory
    // (union, intersection, difference, symmetric difference) operations on basic geometric primitives.
    // A union B -> Everything is A and B
    // A intersection B -> Everything that is both A and B 
    // A - B -> Everything that is A but not B
    // A symmetric difference B -> Everything that is in either A or B, but not in both

    public ConstructiveAreaGeometry() {
        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.WHITE);
    }   
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        Shape s1 = new Ellipse2D.Double(50, 50, 100, 100);
        Shape s2 = new Rectangle2D.Double(100, 100, 150, 150);

        g2.translate(20, 50);
        g2.draw(s1);
        g2.draw(s2);
        // // Union
        // Area union = new Area(s1);
        // union.add(new Area(s2));

        // // Intersection
        // Area intersection = new Area(s1);
        // intersection.intersect(new Area(s2));

        // // Difference
        // Area difference = new Area(s1);
        // difference.subtract(new Area(s2));

        // // Symmetric Difference
        // Area symmetricDifference = new Area(s1);
        // symmetricDifference.xor(new Area(s2));

        // g2.setColor(Color.RED);
        // g2.fill(union);

        // Union operation
        Area a1 = new Area(s1);
        Area a2 = new Area(s2);
        a1.add(a2);// union 
        g2.fill(a1);
    }
}   