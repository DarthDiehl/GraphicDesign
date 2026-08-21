//import java.awt.geom.*;
import javax.swing.*;
import java.awt.*;


public class ManualTransformation extends JPanel{

    // hardcoding rectangle 
    double[][] rect = {
        {0, 0, 1},
        {100, 0, 1},
        {100, 60, 1},
        {0, 60, 1}
    };

    // translation matrix
    double[][] translationMatrix(double tx, double ty) {
        return new double[][]{
            {1, 0, tx},
            {0, 1, ty},
            {0, 0, 1}
        };
    }

    // Matrix multiplication method
    double[] multiply(double[][] M, double[] p){
        double[] r = new double[3];
        for (int i = 0; i < 3; i++) {
            r[i] = M[i][0] * p[0] + M[i][1] * p[1] + M[i][2] * p[2];
        }
        return r;
    }   

    // apply matrix multiplication to all 4 coordinates of rectangle

    double[][] translateShape(double[][] shape, double tx, double ty){
        // translation matrix
        double[][] T = translationMatrix(tx, ty);
        double[][] result=new double[shape.length][3];

        for(int i=0;i<shape.length;i++){
            result[i]=multiply(T, shape[i]);
        }
        return result;
    }


    // connect points in order
    // draws what the math produces

        void drawPolygon(Graphics2D g2, double[][] pts) {
        for (int i = 0; i < pts.length; i++) {
            int j = (i + 1) % pts.length;
            g2.drawLine(
                (int) pts[i][0], (int) pts[i][1],
                (int) pts[j][0], (int) pts[j][1]
            );
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g); // cleaning the panel before new drawing

        Graphics2D g2 = (Graphics2D) g;
         g2.setColor(Color.BLACK);
         drawPolygon(g2, rect);

         // transformed rectangle
         double[][] translatedRectangle=translateShape(rect, 200, 120);
        g2.setColor(Color.RED);
        drawPolygon(g2, translatedRectangle);
    }


    
       public static void main(String[] args) {
        new GraphicWindow(
            new ManualTransformation(),
            "Affine Transformation - Translation"
        );
    }

}