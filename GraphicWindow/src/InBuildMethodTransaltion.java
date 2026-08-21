import java.awt.geom.*;
import javax.swing.*;
import java.awt.*;


public class InBuildMethodTransaltion extends JPanel {

        @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g); // cleaning the panel before new drawing

        Graphics2D g2 = (Graphics2D) g;
        Rectangle2D rect = new Rectangle2D.Double(0, 0, 100, 60);
        g2.setColor(Color.BLACK);
        g2.draw(rect);

        g2.translate(200,120);
        g2.setColor(Color.BLUE);
        g2.draw(rect);
        }

         public static void main(String[] args) {
        new GraphicWindow(
            new InBuildMethodTransaltion(),
            "Affine Transformation - Translation"
        );
    }
}
