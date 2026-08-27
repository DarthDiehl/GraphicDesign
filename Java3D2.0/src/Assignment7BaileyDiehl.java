import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;

public class Assignment7BaileyDiehl extends JFrame {

    public Assignment7BaileyDiehl() {
        super("Assignment 7 - Bailey Diehl");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv = new Canvas3D(gc);
        add(cv, BorderLayout.CENTER);

        SimpleUniverse su = new SimpleUniverse(cv);

        Transform3D viewTrans = new Transform3D();
        viewTrans.setTranslation(new Vector3f(0f, 1.5f, 4f));

        Transform3D tilt = new Transform3D();
        tilt.rotX(Math.toRadians(-15));

        viewTrans.mul(tilt);
        su.getViewingPlatform().getViewPlatformTransform().setTransform(viewTrans);

        BranchGroup scene = createScene();
        scene.compile();
        su.addBranchGraph(scene);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private BranchGroup createScene() {

        BranchGroup root = new BranchGroup();
        BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), 100);

        Appearance ap = new Appearance();
        Material mat = new Material();

        mat.setDiffuseColor(new Color3f(0.0f, 0.5f, 0.5f));
        mat.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
        mat.setShininess(90.0f);
        mat.setLightingEnable(true);
        ap.setMaterial(mat);

        for (int i = 0; i < 10; i++) {
            Transform3D t = new Transform3D();
            t.setTranslation(new Vector3f(0f, 0f, -i * 1.2f));

            TransformGroup tg = new TransformGroup(t);

            Sphere s = new Sphere(0.4f, Sphere.GENERATE_NORMALS, ap);
            tg.addChild(s);

            root.addChild(tg);
        }

        AmbientLight ambient = new AmbientLight(new Color3f(0.0f, 0.0f, 0.15f));
        ambient.setInfluencingBounds(bounds);
        root.addChild(ambient);

        PointLight point = new PointLight();
        point.setColor(new Color3f(1f, 1f, 1f));
        point.setPosition(new Point3f(0f, 1f, 2f));
        point.setAttenuation(0.1f, 0.0f, 0.03f);
        point.setInfluencingBounds(bounds);
        root.addChild(point);

        Color3f bgColor = new Color3f(0f, 0f, 0.2f);

        Background bg = new Background(bgColor);
        bg.setApplicationBounds(bounds);
        root.addChild(bg);

        ExponentialFog fog = new ExponentialFog(bgColor, 0.08f);
        fog.setInfluencingBounds(bounds);
        root.addChild(fog);

        return root;
    }

    public static void main(String[] args) {
        new Assignment7BaileyDiehl();
    }
}