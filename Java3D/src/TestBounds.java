import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.Sphere; // Needed for the Sphere primitive

public class TestBounds extends JFrame {
    
  
    private PointLight light;
    private BoundingSphere[] bounds = new BoundingSphere[3];
    private int bIndex = 0;

    public TestBounds() {
        super("Spheres");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Canvas3D cv = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(cv, BorderLayout.CENTER);

        // mouse click listener to change light bound
        cv.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ev) {
               
                bIndex = (bIndex + 1) % 3; 
                System.out.println("Switching to Bounds Index: " + bIndex);
                 // setting new boundary to light
                light.setInfluencingBounds(bounds[bIndex]); 
            }
        });

        BranchGroup bg = createSceneGraph();
        bg.compile();

        SimpleUniverse su = new SimpleUniverse(cv);
        su.getViewingPlatform().setNominalViewingTransform();
        su.addBranchGraph(bg);

        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

         // creating 3d tiny spheres
        for (int i = 0; i < 3; i++) {
            Sphere sphere = new Sphere(); 
            Transform3D tr = new Transform3D();
            tr.setScale(0.1); 
           
            tr.setTranslation(new Vector3f(-0.4f * i, 0f, 0f)); 
            
            TransformGroup tg = new TransformGroup(tr);
            tg.addChild(sphere);
            root.addChild(tg);
        }
		

      
        light = new PointLight(new Color3f(Color.white), new Point3f(1f, 1f, 1f), new Point3f(1f, 0f, 0f));
        
       
        light.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE); 

        // creating 3 different bounding sphere each defining the spread of light
        bounds[0] = new BoundingSphere(new Point3d(0, 0, 0), 1.0); 
        bounds[1] = new BoundingSphere(new Point3d(0, 0, 0), 0.6); 
        bounds[2] = new BoundingSphere(new Point3d(0, 0, 0), 0.2); 

   
        light.setInfluencingBounds(bounds[0]); 
        root.addChild(light);

     
        Background bgNode = new Background(1.0f, 1.0f, 1.0f);
        bgNode.setApplicationBounds(bounds[0]);
        root.addChild(bgNode);

        return root;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TestBounds());
    }
}