import javax.swing.JFrame;
import javax.swing.JPanel;


public class GraphicWindow {
    public GraphicWindow(JPanel panel, String title) {
        // Constructor code here
        JFrame frame = new JFrame(title);
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setVisible(true);
        
    }
}
