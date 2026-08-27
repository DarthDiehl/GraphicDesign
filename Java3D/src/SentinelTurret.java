import javax.swing.*;
import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.geometry.Box;

public class SentinelTurret extends JFrame {

    public SentinelTurret() {
        super("Assignment 5:The Sentinel Turret");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv = new Canvas3D(gc);
        add(cv, BorderLayout.CENTER);
        
        SimpleUniverse su = new SimpleUniverse(cv);
        su.getViewingPlatform().setNominalViewingTransform();
        
        BranchGroup bg = createTurret();
        bg.compile();
        su.addBranchGraph(bg);
        
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private BranchGroup createTurret() {
        BranchGroup root = new BranchGroup();
        BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), 1000);

        Appearance baseApp = createColorAppearance(new Color3f(0.55f, 0.55f, 0.55f));
        Appearance yokeApp = createColorAppearance(new Color3f(0.85f, 0.2f, 0.2f));
        Appearance dishApp = createColorAppearance(new Color3f(0.2f, 0.4f, 0.9f));

        // STEP 1: CREATE THE BASE
        Box base = new Box(0.5f, 0.1f, 0.5f, Primitive.GENERATE_NORMALS, baseApp);
        root.addChild(base);

        // STEP 2: CREATE THE ROTATING YOKE
        Cylinder yoke = new Cylinder(0.05f, 0.5f, Primitive.GENERATE_NORMALS, yokeApp);

        Transform3D yokeRot = new Transform3D();
        yokeRot.rotY(0.0);
        TransformGroup yokeRotTG = new TransformGroup(yokeRot);
        yokeRotTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        yokeRotTG.addChild(yoke);

        Transform3D yokeTrans = new Transform3D();
        yokeTrans.setTranslation(new Vector3f(0f, 0.35f, 0f));
        TransformGroup yokeTransTG = new TransformGroup(yokeTrans);
        yokeTransTG.addChild(yokeRotTG);
        root.addChild(yokeTransTG);

        Alpha alpha = new Alpha(-1, 4000);
        RotationInterpolator yokeScanner = new RotationInterpolator(alpha, yokeRotTG);
        yokeScanner.setSchedulingBounds(bounds);
        yokeRotTG.addChild(yokeScanner);


        // STEP 3: CREATE THE RADAR DISH
        Cone radarDish = new Cone(0.22f, 0.12f, Primitive.GENERATE_NORMALS, dishApp);

        Transform3D dishTilt = new Transform3D();
        dishTilt.rotZ(-Math.PI / 4.0);
        Transform3D dishOffset = new Transform3D();
        dishOffset.setTranslation(new Vector3f(0.22f, 0.16f, 0f));
        dishOffset.mul(dishTilt);

        TransformGroup dishTG = new TransformGroup(dishOffset);
        dishTG.addChild(radarDish);
        yokeRotTG.addChild(dishTG);


        //Add a light source so you can see your 3D objects
        DirectionalLight light = new DirectionalLight(new Color3f(1f, 1f, 1f), new Vector3f(-1f, -1f, -1f));
        light.setInfluencingBounds(bounds);
        root.addChild(light);

        AmbientLight ambient = new AmbientLight(new Color3f(0.35f, 0.35f, 0.35f));
        ambient.setInfluencingBounds(bounds);
        root.addChild(ambient);

        Background background = new Background(0.04f, 0.04f, 0.1f);
        background.setApplicationBounds(bounds);
        root.addChild(background);

        return root;
    }

    private Appearance createColorAppearance(Color3f color) {
        Appearance appearance = new Appearance();
        ColoringAttributes coloring = new ColoringAttributes();
        coloring.setColor(color);
        coloring.setShadeModel(ColoringAttributes.NICEST);
        appearance.setColoringAttributes(coloring);
        return appearance;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SentinelTurret());
    }
}
