import javax.swing.*;
import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.ColorCube;

// cube is at origin and camera roatates arounf the cube 360 degrees
public class SimpleUniverseWay extends JFrame {

    public SimpleUniverseWay(){
        super("The Smart Way using  SimpleUniverse");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

         GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration(); // asking for best graphics config of your current system
        Canvas3D cv = new Canvas3D(gc);
        add(cv, BorderLayout.CENTER); // add to your window

        // univerise creation
         SimpleUniverse su = new SimpleUniverse(cv, 2); 
         // We use the second one implicitly when we call
         su.getViewingPlatform().setNominalViewingTransform();
         // weuse first to hijack to apply transformation for the camera to give spin effect
        TransformGroup viewTransformGroup = su.getViewingPlatform().getMultiTransformGroup().getTransformGroup(0);

        BranchGroup scene = createContent(viewTransformGroup);
        scene.compile();
        su.addBranchGraph(scene);

        setSize(640, 480);
        setLocationRelativeTo(null);
        setVisible(true);
    }

     private BranchGroup createContent(TransformGroup vtg) {

         BranchGroup root = new BranchGroup();
        

        // static cuvbe sitting at teh origin of teh universe
        Transform3D tr = new Transform3D();
        tr.setScale(0.25);
        TransformGroup tg = new TransformGroup(tr);
        root.addChild(tg);
        tg.addChild(new ColorCube());

        // applying to spin anumation to camera
        Alpha alpha = new Alpha(-1, 4000);
        RotationInterpolator rotator = new RotationInterpolator(alpha, vtg);


        BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 100.0);
        rotator.setSchedulingBounds(bounds);
        root.addChild(rotator);



          // lights and background
        Background background = new Background(1.0f, 1.0f, 1.0f);
        background.setApplicationBounds(bounds);
        root.addChild(background);

        AmbientLight light = new AmbientLight(true, new Color3f(Color.RED));
        light.setInfluencingBounds(bounds);
        root.addChild(light);

        return root;
     }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimpleUniverseWay());
    }
}