import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.picking.*;

public class PickingDemo extends JFrame implements MouseListener{

    private PickCanvas pickCanvas;
    private TransformGroup cubeTransformGroup;

    public PickingDemo(){
           super(" Head Tracking and Picking");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // universie and canvas
          GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv = new Canvas3D(gc);
        add(cv, BorderLayout.CENTER);
        cv.addMouseListener(this); // listen to users mouse click

        SimpleUniverse su = new SimpleUniverse(cv);
        su.getViewingPlatform().setNominalViewingTransform();
       
     
        BranchGroup scene = createSceneGraph();
        scene.compile();
        su.addBranchGraph(scene);

        pickCanvas = new PickCanvas(cv, scene); 
        pickCanvas.setMode(PickTool.GEOMETRY);  

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        }

        private BranchGroup createSceneGraph(){

            BranchGroup root = new BranchGroup();

        cubeTransformGroup = new TransformGroup();
        cubeTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        cubeTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        root.addChild(cubeTransformGroup);

        ColorCube targetCube = new ColorCube(0.3);
        PickTool.setCapabilities(targetCube, PickTool.INTERSECT_TEST); 
        cubeTransformGroup.addChild(targetCube);

            // background
        Background bg = new Background(new Color3f(0.8f, 0.8f, 0.8f));
        bg.setApplicationBounds(new BoundingSphere(new Point3d(0,0,0), 100));
        root.addChild(bg);

        return root;

        }


         @Override
    public void mouseClicked(MouseEvent e) {
        pickCanvas.setShapeLocation(e); 
    PickResult[] results = pickCanvas.pickAll();

        
     if (results != null) {
            for (int i = 0; i < results.length; i++) {
                Node node = results[i].getObject(); //
                
              
                if (node instanceof ColorCube) {
                    System.out.println("Object Picked!Rotating geometry...");
                    Transform3D currentTrans = new Transform3D();
                    cubeTransformGroup.getTransform(currentTrans); // Read current rotation
                    
                    Transform3D rotation = new Transform3D();
                    rotation.rotY(Math.PI / 4); // Rotate 45 degrees
                    currentTrans.mul(rotation); // tranaformation 
                    
                    cubeTransformGroup.setTransform(currentTrans);// we write it back
                }
            }
        }


    }


    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}


     public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PickingDemo());
    }


}
