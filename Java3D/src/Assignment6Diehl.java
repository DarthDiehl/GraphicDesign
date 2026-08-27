import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.Color;
import java.awt.event.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.picking.*;

public class Assignment6Diehl extends JFrame implements MouseListener {

    private PickCanvas pickCanvas;

    private Box cube;
    private Cylinder cylinder;
    private Sphere sphere;

    private TransformGroup sphereTG;

    private boolean cubeToggle = false;
    private boolean cylinderChanged = false;
    private double sphereScale = 1.0;

    public Assignment6Diehl() {
        super("Assignment 6: Artifact Scanner");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv = new Canvas3D(gc);
        add(cv, BorderLayout.CENTER);
        cv.addMouseListener(this);

        SimpleUniverse su = new SimpleUniverse(cv, 2);
        su.getViewingPlatform().setNominalViewingTransform();

        TransformGroup viewTG =
                su.getViewingPlatform()
                  .getMultiTransformGroup()
                  .getTransformGroup(0);

        Alpha alpha = new Alpha(-1, 4000);
        RotationInterpolator rotator = new RotationInterpolator(alpha, viewTG);

        BoundingSphere bounds =
                new BoundingSphere(new Point3d(0, 0, 0), 100.0);
        rotator.setSchedulingBounds(bounds);

        BranchGroup scene = createScene();
        scene.addChild(rotator);

        scene.compile();
        su.addBranchGraph(scene);

        pickCanvas = new PickCanvas(cv, scene);
        pickCanvas.setMode(PickTool.GEOMETRY);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private BranchGroup createScene() {

        BranchGroup root = new BranchGroup();
        BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), 1000);

        int flags = Primitive.GENERATE_NORMALS
                  | Primitive.ENABLE_APPEARANCE_MODIFY
                  | Primitive.ENABLE_GEOMETRY_PICKING;

        cube = new Box(0.3f, 0.3f, 0.3f, flags, createAppearance(Color.RED));

        Transform3D cubeT = new Transform3D();
        cubeT.setTranslation(new Vector3f(-2f, 0f, 0f));
        TransformGroup cubeTG = new TransformGroup(cubeT);
        cubeTG.addChild(cube);
        root.addChild(cubeTG);

        cylinder = new Cylinder(0.3f, 0.6f, flags, createAppearance(Color.BLUE));

        Transform3D cylT = new Transform3D();
        cylT.setTranslation(new Vector3f(0f, 0f, 0f));
        TransformGroup cylTG = new TransformGroup(cylT);
        cylTG.addChild(cylinder);
        root.addChild(cylTG);

        sphere = new Sphere(0.3f, flags, createAppearance(Color.YELLOW));

        sphereTG = new TransformGroup();
        sphereTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        sphereTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Transform3D sphereT = new Transform3D();
        sphereT.setTranslation(new Vector3f(2f, 0f, 0f));
        sphereTG.setTransform(sphereT);

        sphereTG.addChild(sphere);
        root.addChild(sphereTG);

        DirectionalLight light = new DirectionalLight(
                new Color3f(1f,1f,1f),
                new Vector3f(-1f,-1f,-1f));
        light.setInfluencingBounds(bounds);
        root.addChild(light);

        AmbientLight ambient = new AmbientLight(new Color3f(0.4f,0.4f,0.4f));
        ambient.setInfluencingBounds(bounds);
        root.addChild(ambient);

        Background bg = new Background(0.9f,0.9f,0.9f);
        bg.setApplicationBounds(bounds);
        root.addChild(bg);

        return root;
    }

    private Appearance createAppearance(Color color) {
        Appearance app = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(new Color3f(color));
        app.setColoringAttributes(ca);
        return app;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        pickCanvas.setShapeLocation(e);
        PickResult result = pickCanvas.pickClosest();

        if (result == null) return;

        Node node = result.getObject();

        if (node instanceof Shape3D) {

            Node parent = node.getParent();

            if (parent instanceof Box) {
                cubeToggle = !cubeToggle;

                if (cubeToggle)
                    cube.setAppearance(createAppearance(Color.MAGENTA));
                else
                    cube.setAppearance(createAppearance(Color.RED));
            }

            else if (parent instanceof Cylinder) {
                if (!cylinderChanged) {
                    cylinder.setAppearance(createAppearance(Color.GREEN));
                    cylinderChanged = true;
                }
            }
            
            else if (parent instanceof Sphere) {

                sphereScale *= 1.1;

                Transform3D current = new Transform3D();
                sphereTG.getTransform(current);

                Vector3f pos = new Vector3f();
                current.get(pos);

                Transform3D updated = new Transform3D();
                updated.setScale(sphereScale);
                updated.setTranslation(pos);

                sphereTG.setTransform(updated);
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Assignment6Diehl());
    }
}