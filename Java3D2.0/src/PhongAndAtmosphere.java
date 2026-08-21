import javax.swing.*;
import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.Sphere;


public class PhongAndAtmosphere extends JFrame {

public PhongAndAtmosphere() {
    super("Phong Illumination and Fog Demo");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

         GraphicsConfiguration gc= SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv= new Canvas3D(gc);
        add(cv, BorderLayout.CENTER);
        SimpleUniverse su = new SimpleUniverse(cv);

        Transform3D viewTrans = new Transform3D();
        viewTrans.setTranslation(new Vector3f(0f, 2f, 4f));
        Transform3D tilt = new Transform3D();
        tilt.rotX(Math.toRadians(-20));
        viewTrans.mul(tilt);
        su.getViewingPlatform().getViewPlatformTransform().setTransform(viewTrans);


        BranchGroup scene = createSceneGraph();
        scene.compile();
        su.addBranchGraph(scene);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
}

private BranchGroup createSceneGraph(){
    BranchGroup root = new BranchGroup();

    BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0),Double.MAX_VALUE);
    Material shinyMaterial = new Material();
    // ka coeffient for amibent light source
    shinyMaterial.setAmbientColor(new Color3f(0.1f, 0.1f, 0.2f)); 

     // kd coeffient for diffuse light source
    shinyMaterial.setDiffuseColor(new Color3f(0.2f, 0.5f, 0.8f)); 

     // ks coeffient for amibent light source
    shinyMaterial.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f)); 
    shinyMaterial.setShininess(80.0f); 


    Appearance app = new Appearance();
    app.setMaterial(shinyMaterial);

    // placing sphere objects in a grid format
     for (int x = -2; x <= 2; x++) {
        
            for (int z = 0; z <= 10; z++) {
                //  to push objects farther away into the screen along -z axis
                Vector3f pos = new Vector3f(x * 0.5f, 0.0f, -z * 0.8f); 
                Transform3D tr = new Transform3D();
                tr.setTranslation(pos);
                TransformGroup tg = new TransformGroup(tr);

               // we activate surface normals to work with illumantions 
                Sphere sphere = new Sphere(0.2f, Sphere.GENERATE_NORMALS, 50, app); 
                
                tg.addChild(sphere);
                root.addChild(tg);
            }
        }

        // light 
        DirectionalLight dLight = new DirectionalLight(new Color3f(1f, 1f, 1f), new Vector3f(-1f, -1f, -1f));
        dLight.setInfluencingBounds(bounds);
        root.addChild(dLight);

     

        // atmospheric attenuation
        Color3f fogColor = new Color3f(0.7f, 0.7f, 0.7f);
        Background background = new Background(fogColor);
        background.setApplicationBounds(bounds);
        root.addChild(background);

        // exponential fog effect 
        //  ExponentialFog fog = new ExponentialFog(fogColor, 0.2f); // 0.2f is density factor of the fog

         // linear for effect 
         LinearFog activeFog = new LinearFog(fogColor, 5.0, 12.0);
         activeFog.setInfluencingBounds(bounds);
         root.addChild(activeFog);


            return root;
}

 public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhongAndAtmosphere());
    }

}