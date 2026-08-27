import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;

public class LightsDemo extends JFrame implements ItemListener {

    private AmbientLight aLight;
    private DirectionalLight dLight;
    private SpotLight sLight;
    private PointLight pLight;

    public LightsDemo(){
        super("Lights Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // canvas setup for rendering 
        GraphicsConfiguration gc= SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv= new Canvas3D(gc);
        add(cv, BorderLayout.CENTER);

        // to create toggle checkboxes
        Panel panel= new Panel();
        panel.setLayout(new GridLayout(1,4));
        add(panel, BorderLayout.SOUTH);

        // checkboxes
        Checkbox cb_ambient= new Checkbox("Ambient", true);
        panel.add(cb_ambient);
        cb_ambient.addItemListener(this);

        Checkbox cb_directional= new Checkbox("Directional", true);
        panel.add(cb_directional);
        cb_directional.addItemListener(this);

        Checkbox cb_point= new Checkbox("Point", true);
        panel.add(cb_point);
        cb_point.addItemListener(this);

        Checkbox cb_spotlight= new Checkbox("SpotLight", true);
        panel.add(cb_spotlight);
        cb_spotlight.addItemListener(this);

        // simple universe setup 
        SimpleUniverse su= new SimpleUniverse(cv, 2);
        su.getViewingPlatform().setNominalViewingTransform();

        TransformGroup vtg= su.getViewingPlatform().getMultiTransformGroup().getTransformGroup(0);

        // the content branch setup
        BranchGroup bg= createContentBranch(vtg);
        bg.compile();
        su.addBranchGraph(bg);

        setSize(640, 480);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // the content branch code
    private BranchGroup createContentBranch(TransformGroup  vtg){
        BranchGroup root = new BranchGroup();

        //the geometry setup
        Appearance ap = new Appearance();
        // to support illumination the object need to an associated mateiral to it
        ap.setMaterial(new Material());
        
        // sphere
        Sphere sphere= new Sphere(0.5f, Sphere.GENERATE_NORMALS, 150, ap);
        root.addChild(sphere);

        // animation for camera 
        Alpha alpha = new Alpha(-1,4000); // loop continous and take 4 seconds for one rotation
        RotationInterpolator rotationInterpolator= new RotationInterpolator(alpha,vtg);

        // bounding sphere of radius 2 for lights and animation
        BoundingSphere boundingSphere = new BoundingSphere(new Point3d(0,0,0), 2);
        rotationInterpolator.setSchedulingBounds(boundingSphere);
        root.addChild(rotationInterpolator);

        // a gray background for the universe
        Background background = new Background(0.5f,0.5f,0.5f);
        background.setApplicationBounds(boundingSphere);
        root.addChild(background);
        
        // light sources
        aLight= new AmbientLight(true, new Color3f(Color.red));
        aLight.setInfluencingBounds(boundingSphere);
        aLight.setCapability(Light.ALLOW_STATE_WRITE);
        root.addChild(aLight);

        // directional light source
        dLight= new DirectionalLight(new Color3f(Color.green),new Vector3f(0f,1f,0f));
        dLight.setInfluencingBounds(boundingSphere);
        dLight.setCapability(Light.ALLOW_STATE_WRITE);
        root.addChild(dLight);

        // point light
        pLight= new PointLight(new Color3f(Color.yellow),new Point3f(-0.7f,0.7f,0.7f),new Point3f(1f,0f,0f));
        pLight.setInfluencingBounds(boundingSphere);
        pLight.setCapability(Light.ALLOW_STATE_WRITE);
        root.addChild(pLight);

        // spot light
        sLight= new SpotLight(new Color3f(Color.blue), new Point3f(0.7f, 0.7f, 0.7f),
                new Point3f(1f, 0f, 0f), new Vector3f(-0.7f, -0.7f, -0.7f), 
                (float) (Math.PI / 6.0), 0f);
        sLight.setInfluencingBounds(boundingSphere);
        sLight.setCapability(Light.ALLOW_STATE_WRITE);
        root.addChild(sLight);

        return root;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        Checkbox cmi = (Checkbox) itemEvent.getSource();
        String label = cmi.getLabel();
        boolean state = cmi.getState();

        if ("Ambient".equals(label)) {
            aLight.setEnable(state);
        } else if ("Directional".equals(label)) {
            dLight.setEnable(state);
        } else if ("Point".equals(label)) {
            pLight.setEnable(state);
        } else if ("SpotLight".equals(label)) {
            sLight.setEnable(state);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LightsDemo());
    }
}