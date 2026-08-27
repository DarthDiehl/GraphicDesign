import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
//import com.sun.j3d.utils.geometry.*;

public class ToggleColor extends JFrame{

    private Background background;

    public ToggleColor(){

        // setting the window
        super("Toggle background color");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // setting canvas 
            Canvas3D cv = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(cv, BorderLayout.CENTER);

        // view branch object 
         BranchGroup bg = createSceneGraph();
        bg.compile();
        
        // the superstructure of my 3d universe
        SimpleUniverse su = new SimpleUniverse(cv);
        su.getViewingPlatform().setNominalViewingTransform();
        su.addBranchGraph(bg);

        cv.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me){
                float r= (float)Math.random();
                float g= (float)Math.random();
                float b= (float)Math.random();

                background.setColor(r,g,b);
                System.out.println("background changed to R:"+r+" G: "+g+" B: "+b);

            }
        });

        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        Appearance ap = new Appearance();
        ap.setMaterial(new Material());
        Font3D font = new Font3D(new Font("SansSerif", Font.BOLD, 1), new FontExtrusion());
         Text3D text = new Text3D(font, "Hello 3D");
        Shape3D shape = new Shape3D(text, ap);

         Transform3D tr= new Transform3D();
        tr.setScale(0.5);
        tr.setTranslation(new Vector3d(-0.95f, -0.2f, 0f));

        // transform group
        TransformGroup tg = new TransformGroup(tr);
        tg.addChild(shape);
        root.addChild(tg);

        // point light souece 
        PointLight light = new PointLight(new Color3f(Color.white), new Point3f(1f, 1f, 1f), new Point3f(1f, 0.1f, 0f));
        // bouunding sphere to set boundary of influence of light 
        BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), 100.0);
        light.setInfluencingBounds(bounds);
        root.addChild(light);

        // setting deafult background to black
        background = new Background(0.2f, 0.2f, 0.2f); 
        background.setApplicationBounds(bounds);

        // we grant permission to write on  background
        background.setCapability(Background.ALLOW_COLOR_WRITE); 

        root.addChild(background);
        return root;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToggleColor());
    }
    
}
