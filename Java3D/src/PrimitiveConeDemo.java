import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class PrimitiveConeDemo extends JFrame {
    public PrimitiveConeDemo() {
        super("Cone Demo using Java 3D Primitives");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv = new Canvas3D(gc);
        add(cv, BorderLayout.CENTER);

        SimpleUniverse su = new SimpleUniverse(cv);
        su.getViewingPlatform().setNominalViewingTransform();

        BranchGroup scene = createContentBranch();
        scene.compile();
        su.addBranchGraph(scene);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private BranchGroup createContentBranch() {
        BranchGroup root = new BranchGroup();

        Appearance ap = new Appearance();
        PolygonAttributes polyAttr = new PolygonAttributes();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(0.0f, 1.0f, 0.0f);
        polyAttr.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        polyAttr.setCullFace(PolygonAttributes.CULL_NONE);
        ap.setPolygonAttributes(polyAttr);
        ap.setColoringAttributes(ca);

        Cone cone = new Cone(0.5f, 1.0f, ap);
        root.addChild(cone);

        BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 100.0);

        Background background = new Background(new Color3f(Color.WHITE));
        background.setApplicationBounds(bounds);
        root.addChild(background);

        AmbientLight light = new AmbientLight(true, new Color3f(1.0f, 1.0f, 1.0f));
        light.setInfluencingBounds(bounds);
        root.addChild(light);

        return root;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PrimitiveConeDemo::new);
    }
}
