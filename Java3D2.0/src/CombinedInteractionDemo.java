import javax.swing.*;
import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.*;

public class CombinedInteractionDemo extends JFrame {

    public CombinedInteractionDemo() {
        super("Assignment 8");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv = new Canvas3D(gc);
        add(cv, BorderLayout.CENTER);

        SimpleUniverse su = new SimpleUniverse(cv);
        su.getViewingPlatform().setNominalViewingTransform();

        // view platform behavior - the orbit camera
        // lets the user to rotate, zoom and transalate the object 
        OrbitBehavior orbit= new OrbitBehavior(cv, OrbitBehavior.REVERSE_ALL); //OrbitBehavior.REVERSE_ALL makes mouse dragging feel smooth
        BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 100.0);
        orbit.setSchedulingBounds(bounds); 
       
        su.getViewingPlatform().setViewPlatformBehavior(orbit);       
        BranchGroup scene = createSceneGraph(cv, bounds);
        scene.compile();
        su.addBranchGraph(scene);

        setSize(1000, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private BranchGroup createSceneGraph(Canvas3D cv, BoundingSphere bounds) {
         BranchGroup root = new BranchGroup();

         // light setup
        AmbientLight ambient = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
        ambient.setInfluencingBounds(bounds);
        root.addChild(ambient);
        
        DirectionalLight direct = new DirectionalLight(new Color3f(1f, 1f, 1f), new Vector3f(-1f, -1f, -1f));
        direct.setInfluencingBounds(bounds);
        root.addChild(direct);

        // setup object for picking 
                // sphere and a box 
        TransformGroup tgSphere = createInteractiveTG(new Vector3d(-0.6, 0, 0));
        Sphere sphere = new Sphere(0.4f, Sphere.GENERATE_NORMALS, 50, createMaterial(Color.RED));
        // PickTool.setCapabilities(sphere, PickTool.INTERSECT_FULL)
        enablePicking(sphere);       //helper method   
        tgSphere.addChild(sphere);
        root.addChild(tgSphere);


        TransformGroup tgBox = createInteractiveTG(new Vector3d(0.6, 0, 0));
        Box box = new Box(0.3f, 0.3f, 0.3f, Box.GENERATE_NORMALS, createMaterial(Color.BLUE));
        // PickTool.setCapabilities(sphere, PickTool.INTERSECT_FULL)
        enablePicking(box);        //helper method   
        tgBox.addChild(box);
        root.addChild(tgBox);

        TransformGroup tgCylinder = createInteractiveTG(new Vector3d(0, 0.6, 0));
        Cylinder cylinder = new Cylinder(0.3f, 0.6f, Cylinder.GENERATE_NORMALS, createMaterial(Color.GREEN));
        enablePicking(cylinder);
        tgCylinder.addChild(cylinder);
        root.addChild(tgCylinder);


        // pciking behavior setup
        PickRotateBehavior pickRotate= new PickRotateBehavior(root, cv, bounds,PickTool.GEOMETRY); // PickTool.GEOMETRY enforce strict collison testing 
        root.addChild(pickRotate);
        PickTranslateBehavior pickTranslate= new PickTranslateBehavior(root, cv, bounds,PickTool.GEOMETRY);
        root.addChild(pickTranslate);

        return root;

 
    }

    //method to setup TransformGroups with the exact capabilities needed for picking
    private TransformGroup createInteractiveTG(Vector3d pos) {
       Transform3D t3d = new Transform3D();
       t3d.setTranslation(pos);

       TransformGroup tg = new TransformGroup(t3d);       
       // Without this, the program will crash when you click a shape to move it  
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);        
        return tg;
    }

    //method to create a shiny colored material
    private Appearance createMaterial(Color color) {
        Appearance app = new Appearance();
        Material mat = new Material();
        mat.setDiffuseColor(new Color3f(color));
        mat.setSpecularColor(new Color3f(1f, 1f, 1f));
        mat.setShininess(80.0f);
        app.setMaterial(mat);
        return app;
    }

     private void enablePicking(Primitive primitive) {
        for (int i = 0; i < primitive.numChildren(); i++) {
            Node child = primitive.getChild(i);
            if (child instanceof Shape3D) {
                PickTool.setCapabilities(child, PickTool.INTERSECT_FULL);
            }
        }

     }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CombinedInteractionDemo());
    }
}