import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;


public class EcllipsePathIterator {
    public static void main(String[] args) {
        Shape ellipse = new Ellipse2D.Double(0, 0, 100, 60);
        PathIterator iterator = ellipse.getPathIterator(null); // path iterator as the DNA of SHAPE interface

        // iterator - list of segments

        // 1 stating point 
        // 4 line segment 
        // 1 close the path

        double[] coords= new double[6];
        System.out.println("Ellipse Path Segments:");

        // prints how the ellipse is broken into path segments
        while(!iterator.isDone()){
            int segmentType= iterator.currentSegment(coords); // picking each segment and comparing through a switch case
            
              switch (segmentType) {

                case PathIterator.SEG_MOVETO:
                    System.out.printf(
                        "MOVETO      (%.2f, %.2f)%n",
                        coords[0], coords[1]
                    );
                    break;

                case PathIterator.SEG_LINETO:
                    System.out.printf(
                        "LINETO      (%.2f, %.2f)%n",
                        coords[0], coords[1]
                    );
                    break;

                case PathIterator.SEG_QUADTO:
                    System.out.printf(
                        "QUADTO      Control:(%.2f, %.2f) End:(%.2f, %.2f)%n",
                        coords[0], coords[1],
                        coords[2], coords[3]
                    );
                    break;

                case PathIterator.SEG_CUBICTO:
                    System.out.printf(
                        "CUBICTO     Control1:(%.2f, %.2f) "
                      + "Control2:(%.2f, %.2f) End:(%.2f, %.2f)%n",
                        coords[0], coords[1],
                        coords[2], coords[3],
                        coords[4], coords[5]
                    );
                    break;

                case PathIterator.SEG_CLOSE:
                    System.out.println("CLOSE");
                    break;
            }
            iterator.next();

        }

    }
    
}
