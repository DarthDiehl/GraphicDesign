import javax.swing.*;
import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.*;
import com.sun.j3d.utils.behaviors.vp.*;

public class GalleryTemplate_Bailey_Diehl extends JFrame {

    public GalleryTemplate_Bailey_Diehl() {
        super("Interactive Gallery");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv = new Canvas3D(gc);
        add(cv, BorderLayout.CENTER);

        SimpleUniverse su = new SimpleUniverse(cv);
        su.getViewingPlatform().setNominalViewingTransform();

        BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 100.0);

       
        // ====TASK 1:ADD ORBIT BEHAVIOR TO THE VIEWING PLATFORM======
     
        // TODO:Create an OrbitBehavior
            OrbitBehavior orbit = new OrbitBehavior(cv, OrbitBehavior.REVERSE_ALL); //OrbitBehavior.REVERSE_ALL makes mouse dragging feel smooth
        // TODO:Set its scheduling bounds
            bounds = new BoundingSphere(new Point3d(0, 0, 0), 100.0);
            orbit.setSchedulingBounds(bounds);
        // TODO:Attach it to the viewing platform of 'su'
            su.getViewingPlatform().setViewPlatformBehavior(orbit);

        BranchGroup scene = createSceneGraph(cv, bounds);
        scene.compile();
        su.addBranchGraph(scene);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private BranchGroup createSceneGraph(Canvas3D cv, BoundingSphere bounds) {
        BranchGroup root = new BranchGroup();

        //Lighting setup
        DirectionalLight direct = new DirectionalLight(new Color3f(1f, 1f, 1f), new Vector3f(-1f, -1f, -1f));
        direct.setInfluencingBounds(bounds);
        root.addChild(direct);
        
        AmbientLight ambient = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
        ambient.setInfluencingBounds(bounds);
        root.addChild(ambient);

        //Red Sphere
        TransformGroup tgSphere = createInteractiveTG(new Vector3d(-0.6, 0, 0));
        Sphere sphere = new Sphere(0.2f, Sphere.GENERATE_NORMALS, 50, createMaterial(Color.RED));
        enablePicking(sphere); // Fixes Java 3D primitive bug
        tgSphere.addChild(sphere);
        root.addChild(tgSphere);

        //greeen Cylinder
        TransformGroup tgCyl = createInteractiveTG(new Vector3d(0.0, 0, 0));
        Cylinder cyl = new Cylinder(0.15f, 0.4f, Cylinder.GENERATE_NORMALS, 50, 50, createMaterial(Color.GREEN));
        enablePicking(cyl);
        tgCyl.addChild(cyl);
        root.addChild(tgCyl);

        //Blue Box
        TransformGroup tgBox = createInteractiveTG(new Vector3d(0.6, 0, 0));
        Box box = new Box(0.15f, 0.2f, 0.15f, Box.GENERATE_NORMALS, createMaterial(Color.BLUE));
        enablePicking(box);
        tgBox.addChild(box);
        root.addChild(tgBox);

        // ===========TASK 3: IMPLEMENT PICKING BEHAVIORS========
        // TODO:Create a PickRotateBehavior (Left Click) and add it to the root.
        PickRotateBehavior pickRotate = new PickRotateBehavior(root, cv, bounds,PickTool.GEOMETRY); // PickTool.GEOMETRY enforce strict collison testing
        root.addChild(pickRotate);
        // TODO:Create a PickTranslateBehavior (Right Click) and add it to the root.
        PickTranslateBehavior pickTranslate = new PickTranslateBehavior(root, cv, bounds,PickTool.GEOMETRY);
        root.addChild(pickTranslate);

        return root;
    }

    // =======TASK 2:CONFIGURE NODE CAPABILITIES===========
    private TransformGroup createInteractiveTG(Vector3d pos) {
        Transform3D t3d = new Transform3D();
        t3d.setTranslation(pos);
        TransformGroup tg = new TransformGroup(t3d);
        
        // TODO:Set capability to ALLOW_TRANSFORM_READ
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        // TODO:Set capability to ALLOW_TRANSFORM_WRITE
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        // TODO:Set capability to ENABLE_PICK_REPORTING
        tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        
        return tg;
    }

    //Helper method to bypass the PickTool Primitive bug
    private void enablePicking(Primitive primitive) {
        for (int i = 0; i < primitive.numChildren(); i++) {
            Node child = primitive.getChild(i);
            if (child instanceof Shape3D) {
                PickTool.setCapabilities(child, PickTool.INTERSECT_FULL);
            }
        }
    }

    //Helper method for materials
    private Appearance createMaterial(Color color) {
        Appearance app = new Appearance();
        Material mat = new Material();
        mat.setDiffuseColor(new Color3f(color));
        mat.setSpecularColor(new Color3f(1f, 1f, 1f));
        mat.setShininess(80.0f);
        app.setMaterial(mat);
        return app;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GalleryTemplate_Bailey_Diehl());
    }
}