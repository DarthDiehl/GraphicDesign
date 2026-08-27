import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;

public class SentinelDroidAssignment extends JFrame {

    public SentinelDroidAssignment() {
        super("Assignment 4:The Sentinel Droid");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv = new Canvas3D(gc);
        add(cv, BorderLayout.CENTER);
        
        SimpleUniverse su = new SimpleUniverse(cv);
        su.getViewingPlatform().setNominalViewingTransform();
        
        BranchGroup bg = createScene();
        bg.compile();
        su.addBranchGraph(bg);
        
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private BranchGroup createScene() {
        BranchGroup root = new BranchGroup();
        
        //Spin the Droid to see all sides
        TransformGroup spin = new TransformGroup();
        spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        root.addChild(spin);
        
        Alpha alpha = new Alpha(-1, 6000);
        RotationInterpolator rotator = new RotationInterpolator(alpha, spin);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), 100.0);
        rotator.setSchedulingBounds(bounds);
        spin.addChild(rotator);

        // =============================================================
        // YOUR CODE GOES HERE
        // =============================================================
        // Bailey Diehl 901133617
        // TODO 1: Create Main Body (e.g.,Cylinder with Material)
        Appearance bodyApp = new Appearance();
        Material bodyMat = new Material();
        bodyMat.setDiffuseColor(new Color3f(0.4f, 0.4f, 0.9f));
        bodyMat.setSpecularColor(new Color3f(1f,1f,1f));
        bodyMat.setShininess(50f);
        bodyMat.setLightingEnable(true);
        bodyApp.setMaterial(bodyMat);

        Cylinder body = new Cylinder(0.3f, 0.8f, bodyApp);
        Transform3D bodyT = new Transform3D();
        bodyT.setTranslation(new Vector3f(0f,0f,0f));
        TransformGroup bodyTG = new TransformGroup(bodyT);
        bodyTG.addChild(body);
        spin.addChild(bodyTG);
        // TODO 2: Create Head (e.g.,Sphere with Transparency for a "glass dome")
        Appearance headApp = new Appearance();

        TransparencyAttributes ta =
        new TransparencyAttributes(TransparencyAttributes.BLENDED,0.5f);
        headApp.setTransparencyAttributes(ta);

        Material headMat = new Material();
        headMat.setDiffuseColor(new Color3f(0.6f,0.8f,1f));
        headMat.setLightingEnable(true);
        headApp.setMaterial(headMat);

        Sphere head = new Sphere(0.25f, headApp);

        Transform3D headT = new Transform3D();
        headT.setTranslation(new Vector3f(0f,0.6f,0f));
        TransformGroup headTG = new TransformGroup(headT);
        headTG.addChild(head);
        spin.addChild(headTG);
        Appearance limbApp = new Appearance();
        Material limbMat = new Material();
        limbMat.setDiffuseColor(new Color3f(0.7f,0.7f,0.7f));
        limbMat.setLightingEnable(true);
        limbApp.setMaterial(limbMat);

        // left arm
        com.sun.j3d.utils.geometry.Box leftArm = new com.sun.j3d.utils.geometry.Box(0.05f,0.3f,0.05f, limbApp);
        Transform3D leftT = new Transform3D();
        leftT.setTranslation(new Vector3f(-0.4f,0f,0f));
        TransformGroup leftTG = new TransformGroup(leftT);
        leftTG.addChild(leftArm);
        spin.addChild(leftTG);

        // right arm
        com.sun.j3d.utils.geometry.Box rightArm = new com.sun.j3d.utils.geometry.Box(0.05f,0.3f,0.05f, limbApp);
        Transform3D rightT = new Transform3D();
        rightT.setTranslation(new Vector3f(0.4f,0f,0f));
        TransformGroup rightTG = new TransformGroup(rightT);
        rightTG.addChild(rightArm);
        spin.addChild(rightTG);

        // TODO 3: Create Legs (e.g., Cylinders for limbs)
        // left leg
        Cylinder leftLeg = new Cylinder(0.05f, 0.4f, limbApp);
        Transform3D leftLegT = new Transform3D();
        leftLegT.setTranslation(new Vector3f(-0.15f, -0.6f, 0f));
        TransformGroup leftLegTG = new TransformGroup(leftLegT);
        leftLegTG.addChild(leftLeg);
        spin.addChild(leftLegTG);

        // right leg
        Cylinder rightLeg = new Cylinder(0.05f, 0.4f, limbApp);
        Transform3D rightLegT = new Transform3D();
        rightLegT.setTranslation(new Vector3f(0.15f, -0.6f, 0f));
        TransformGroup rightLegTG = new TransformGroup(rightLegT);
        rightLegTG.addChild(rightLeg);
        spin.addChild(rightLegTG);

        // TODO 4: Create Custom Geometry (e.g., A TriangleFan "Shield")
        // Apply PolygonAttributes.POLYGON_LINE to make it a wireframe shield
        int n = 6; // hexagon
        int[] stripCounts = {n+2};

        TriangleFanArray fan = new TriangleFanArray(n+2,
        GeometryArray.COORDINATES,
        stripCounts);

        // center vertex
        fan.setCoordinate(0,new Point3f(0f,0f,0f));

        // outer ring
        for(int i=0;i<=n;i++){
            double angle = 2*Math.PI*i/n;
            float x = (float)(0.3*Math.cos(angle));
            float y = (float)(0.3*Math.sin(angle));
            fan.setCoordinate(i+1,new Point3f(x,y,0f));
        }

        Appearance shieldApp = new Appearance();

        // wireframe mode
        PolygonAttributes pa = new PolygonAttributes();
        pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        shieldApp.setPolygonAttributes(pa);

Shape3D shield = new Shape3D(fan, shieldApp);

        Transform3D shieldT = new Transform3D();
        shieldT.setTranslation(new Vector3f(0f,-0.4f,0.4f));
        TransformGroup shieldTG = new TransformGroup(shieldT);
        shieldTG.addChild(shield);
        spin.addChild(shieldTG);
        // Example of adding a part to the spin group:
        // spin.addChild(myBodyPart);

        // =============================================================
        // END OF CODE
        // =============================================================

        //Lighting
        DirectionalLight light = new DirectionalLight(
            new Color3f(1f, 1f, 1f), new Vector3f(-1f, -1f, -1f)
        );
        light.setInfluencingBounds(bounds);
        root.addChild(light);
        
        // Background
        Background back = new Background(0.05f, 0.05f, 0.2f);
        back.setApplicationBounds(bounds);
        root.addChild(back);

        return root;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SentinelDroidAssignment());
    }
}
