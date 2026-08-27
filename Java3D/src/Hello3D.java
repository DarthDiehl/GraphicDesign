import java.awt.*;
import javax.swing.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*; 
//import com.sun.j3d.utils.geometry.*;


// code from textbook  lisitng 5.1
// his code manually builds the entire Java 3D infrastructure that SimpleUniverse usually hides.

public class Hello3D extends JFrame {

    public Hello3D() {
        // window setup
       super("Hello 3d - Manual Setup of Scene Graph");
       setLayout(new BorderLayout());
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

       // configure canvas 
       // asking my machine to give me best grahpics config for rendering
       GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration(); 
       // canvas 3d 
       Canvas3D cv = new Canvas3D(gc);
       // add the 3d canvas to the center of window 
       add(cv, BorderLayout.CENTER);

       // SuperStructure 
        SimpleUniverse su = new SimpleUniverse(cv);
        su.getViewingPlatform().setNominalViewingTransform();
        
        BranchGroup bg = createContentBranch();
        bg.compile();
        su.addBranchGraph(bg);


        // show the window 
        setSize(800, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    
    // content branch    
    private BranchGroup createContentBranch() {
        BranchGroup root = new BranchGroup();

         Appearance ap = new Appearance();
         ap.setMaterial(new Material());
        Font3D font = new Font3D(new Font("SansSerif", Font.PLAIN, 1), new FontExtrusion());
         Text3D text = new Text3D(font, "Hello 3D");
          Shape3D shape = new Shape3D(text, ap);


        //create a tranformation
        Transform3D tr= new Transform3D();
        tr.setScale(0.5);
        tr.setTranslation(new Vector3d(-0.95f, -0.2f, 0f));

        // creating group code to hold the transformations
        TransformGroup tg = new TransformGroup(tr);
        root.addChild(tg);
        tg.addChild(shape);



        // lighting my surrounding 
         PointLight light = new PointLight(
            new Color3f(Color.white),
            new Point3f(1f, 1f, 1f),
            new Point3f(1f, 0.1f, 0f)
        );
         BoundingSphere bounds = new BoundingSphere(); // region where the point light is active
         // telling to shine only in this sppere
           light.setInfluencingBounds(bounds);
           // adding the light to the scene
        root.addChild(light);
        return root;
    }

    
    // main method 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Hello3DFullGraph());
    }
} 