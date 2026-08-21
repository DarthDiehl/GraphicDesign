import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.picking.*;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;

public class DeepSeaSubmarineExplorerRemote_Bailey_Diehl extends JFrame {

    private JLabel messageLabel;

    private TransformGroup submarineTG;
    private Transform3D submarineTransform;
    private Vector3f submarineLocation;
    private float submarineAngle = 0.0f;
    private int lastMouseX = 0;

    private PickCanvas pickCanvas;
    private SpotLight headLight;

    private int artifactsFound = 0;

    // Artifact location data.
    private String[] artifactNames = {
            "Ancient Vase", "Lost Pearl", "Old Treasure"
    };

    private float[] artifactX = {
            -2.0f, 1.8f, 0.3f
    };

    private float[] artifactY = {
            -1.05f, -1.05f, -1.05f
    };

    private float[] artifactZ = {
            -2.0f, -3.0f, -4.3f
    };

    private boolean[] artifactFound = {
            false, false, false
    };

    private Shape3D[] artifactShapes = new Shape3D[3];

    // Collision circles around major objects.
    private String[] obstacleNames = {
            "rock", "rock", "rock", "coral", "Ancient Vase", "Lost Pearl", "Old Treasure"
    };

    private float[] obstacleX = {
            -3.0f, 2.9f, -1.1f, 2.4f, -2.0f, 1.8f, 0.3f
    };

    private float[] obstacleZ = {
            -1.0f, -2.5f, -5.0f, -5.2f, -2.0f, -3.0f, -4.3f
    };

    private float[] obstacleRadius = {
            0.95f, 0.85f, 0.80f, 0.90f, 0.85f, 0.85f, 0.85f
    };

    public DeepSeaSubmarineExplorerRemote_Bailey_Diehl() {
        super("Deep Sea Submarine Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Scene pieces are added in createSceneGraph, but the title and message label are added here so they are on top of the 3D canvas.
        JLabel titleLabel = new JLabel("Deep Sea Search Zone");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBackground(new Color(5, 20, 35));
        titleLabel.setOpaque(true);
        add(titleLabel, BorderLayout.NORTH);

        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv = new Canvas3D(gc);
        add(cv, BorderLayout.CENTER);

        messageLabel = new JLabel("Use W/S to move, A/D to slide, Q/E to move up/down. Drag mouse left/right to rotate the submarine.");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel, BorderLayout.SOUTH);

        SimpleUniverse su = new SimpleUniverse(cv);

        BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 100.0);

// Enabled to show off detailed coral and billboard label. 
        OrbitBehavior orbit = new OrbitBehavior(cv, OrbitBehavior.REVERSE_ALL);
        orbit.setSchedulingBounds(bounds);
        su.getViewingPlatform().setViewPlatformBehavior(orbit);

        Transform3D viewTrans = new Transform3D();
        viewTrans.setTranslation(new Vector3f(0.0f, 3.0f, 9.0f));

        Transform3D tiltDown = new Transform3D();
        tiltDown.rotX(Math.toRadians(-15));
        viewTrans.mul(tiltDown);

        su.getViewingPlatform().getViewPlatformTransform().setTransform(viewTrans);

        // the camera is looking down the negative Z axis, so the submarine starts facing that way at positive Z.
        BranchGroup scene = createSceneGraph(cv, bounds);
        scene.compile();
        su.addBranchGraph(scene);

        setupKeyboard(cv);
// For mouse moving the submarine instead of the camera.
        //setupMouseRotate(cv);
        setupMousePicking(cv);
        setupMousePicking(cv);

        setSize(1000, 800);
        setLocationRelativeTo(null);
        setVisible(true);

        cv.setFocusable(true);
        cv.requestFocus();
    }

    private BranchGroup createSceneGraph(Canvas3D cv, BoundingSphere bounds) {
        BranchGroup root = new BranchGroup();

        addWaterBackground(root, bounds);
        addLights(root, bounds);

        root.addChild(createOceanFloor());
        root.addChild(createSubmarine(bounds));

        addArtifacts(root);
        addRocks(root);
        addDistanceLODCoral(root, bounds);
        root.addChild(createBillboardLabel(bounds));

        pickCanvas = new PickCanvas(cv, root);
        pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
        pickCanvas.setTolerance(3.0f);

        return root;
    }

    private void addWaterBackground(BranchGroup root, BoundingSphere bounds) {
        Color3f waterColor = new Color3f(0.02f, 0.10f, 0.20f);

        Background background = new Background(waterColor);
        background.setApplicationBounds(bounds);
        root.addChild(background);

        LinearFog fog = new LinearFog(waterColor, 3.0, 18.0);
        fog.setInfluencingBounds(bounds);
        root.addChild(fog);
    }

    private void addLights(BranchGroup root, BoundingSphere bounds) {
        AmbientLight ambient = new AmbientLight(new Color3f(0.16f, 0.20f, 0.28f));
        ambient.setInfluencingBounds(bounds);
        root.addChild(ambient);

        DirectionalLight direct = new DirectionalLight(
                new Color3f(0.45f, 0.55f, 0.65f),
                new Vector3f(-1f, -1f, -1f));
        direct.setInfluencingBounds(bounds);
        root.addChild(direct);

        // The submarine model faces positive X.
        headLight = new SpotLight(
                new Color3f(1.0f, 1.0f, 0.85f),
                new Point3f(0.75f, 0.0f, 1.7f),
                new Point3f(1f, 0.01f, 0.0f),
                new Vector3f(1f, -0.1f, 0f),
                (float) Math.toRadians(35),
                12.0f);

        headLight.setInfluencingBounds(bounds);
        headLight.setCapability(PointLight.ALLOW_POSITION_WRITE);
        headLight.setCapability(SpotLight.ALLOW_DIRECTION_WRITE);
        root.addChild(headLight);
    }

    private TransformGroup createOceanFloor() {
        Transform3D tr = new Transform3D();
        tr.setTranslation(new Vector3f(0f, -1.4f, -2f));

        TransformGroup tg = new TransformGroup(tr);
        int flags = Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS;

        Box floor = new Box(7.0f, 0.06f, 7.0f, flags, createFloorTexture());
        tg.addChild(floor);

        return tg;
    }

    private TransformGroup createSubmarine(BoundingSphere bounds) {
        submarineLocation = new Vector3f(0f, 0f, 1.7f);

        submarineTransform = new Transform3D();
        submarineTransform.setTranslation(submarineLocation);

        submarineTG = new TransformGroup(submarineTransform);
        submarineTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        // Submarine body.
        Transform3D bodyRot = new Transform3D();
        bodyRot.rotZ(Math.PI / 2.0);
        TransformGroup bodyTG = new TransformGroup(bodyRot);

        Cylinder body = new Cylinder(0.35f, 1.4f, Cylinder.GENERATE_NORMALS,
                createMaterial(new Color(210, 190, 70)));
        bodyTG.addChild(body);
        submarineTG.addChild(bodyTG);

        // Rounded front.
        Transform3D frontTr = new Transform3D();
        frontTr.setTranslation(new Vector3f(0.72f, 0f, 0f));
        TransformGroup frontTG = new TransformGroup(frontTr);

        Sphere nose = new Sphere(0.35f, Sphere.GENERATE_NORMALS, 40,
                createMaterial(new Color(220, 205, 90)));
        frontTG.addChild(nose);
        submarineTG.addChild(frontTG);

        // Visible spotlight cone. This matches the scanner math below.
        Transform3D beamRot = new Transform3D();
        beamRot.rotZ(Math.PI / 2.0);

        Transform3D beamMove = new Transform3D();
        beamMove.setTranslation(new Vector3f(1.35f, 0.0f, 0.0f));
        beamMove.mul(beamRot);

        TransformGroup beamTG = new TransformGroup(beamMove);
        Cone beam = new Cone(0.42f, 1.2f, Cone.GENERATE_NORMALS, 30, 30,
                createLightBeamMaterial());
        beamTG.addChild(beam);
        submarineTG.addChild(beamTG);

        // Window.
        Transform3D windowTr = new Transform3D();
        windowTr.setTranslation(new Vector3f(0.30f, 0.28f, 0.02f));
        TransformGroup windowTG = new TransformGroup(windowTr);

        Sphere window = new Sphere(0.13f, Sphere.GENERATE_NORMALS, 30,
                createMaterial(new Color(80, 170, 220)));
        windowTG.addChild(window);
        submarineTG.addChild(windowTG);

        // Top fin.
        Transform3D finTr = new Transform3D();
        finTr.setTranslation(new Vector3f(-0.25f, 0.42f, 0f));
        TransformGroup finTG = new TransformGroup(finTr);

        Box fin = new Box(0.16f, 0.25f, 0.05f, Box.GENERATE_NORMALS,
                createMaterial(new Color(180, 160, 60)));
        finTG.addChild(fin);
        submarineTG.addChild(finTG);

// Animated propeller. Dynamic Animation Ch11.
        Transform3D propTr = new Transform3D();
        propTr.setTranslation(new Vector3f(-0.85f, 0f, 0f));
        TransformGroup propMoveTG = new TransformGroup(propTr);

        TransformGroup propSpinTG = new TransformGroup();
        // Allow update while scene is live for animation.
        propSpinTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Box bladeOne = new Box(0.03f, 0.30f, 0.03f, Box.GENERATE_NORMALS,
                createMaterial(new Color(100, 100, 100)));
        Box bladeTwo = new Box(0.30f, 0.03f, 0.03f, Box.GENERATE_NORMALS,
                createMaterial(new Color(100, 100, 100)));

        propSpinTG.addChild(bladeOne);
        propSpinTG.addChild(bladeTwo);

        Alpha alpha = new Alpha(-1, 1000);
        RotationInterpolator spin = new RotationInterpolator(alpha, propSpinTG);
        spin.setSchedulingBounds(bounds);
        propSpinTG.addChild(spin);

        propMoveTG.addChild(propSpinTG);
        submarineTG.addChild(propMoveTG);

        return submarineTG;
    }

    private void addArtifacts(BranchGroup root) {
        for (int i = 0; i < artifactNames.length; i++) {
            root.addChild(createArtifact(i));
        }
    }

    private TransformGroup createArtifact(int index) {
        Color color;

        if (index == 0) {
            color = new Color(180, 120, 70);
        } else if (index == 1) {
            color = new Color(220, 220, 240);
        } else {
            color = new Color(210, 170, 45);
        }

        Transform3D tr = new Transform3D();
        tr.setTranslation(new Vector3f(artifactX[index], artifactY[index], artifactZ[index]));

        TransformGroup tg = new TransformGroup(tr);

        Sphere artifact = new Sphere(0.22f, Sphere.GENERATE_NORMALS, 40, createMaterial(color));
        artifact.setUserData(artifactNames[index]);
        enablePicking(artifact, index);

        tg.addChild(artifact);

        return tg;
    }

    private void addRocks(BranchGroup root) {
        root.addChild(createRock(-3.0f, -1.15f, -1.0f, 0.5f));
        root.addChild(createRock(2.9f, -1.15f, -2.5f, 0.4f));
        root.addChild(createRock(-1.1f, -1.15f, -5.0f, 0.35f));
    }

    private TransformGroup createRock(float x, float y, float z, float size) {
        Transform3D tr = new Transform3D();
        tr.setTranslation(new Vector3f(x, y, z));

        TransformGroup tg = new TransformGroup(tr);
        Sphere rock = new Sphere(size, Sphere.GENERATE_NORMALS, 25,
                createMaterial(new Color(80, 85, 90)));
        tg.addChild(rock);

        return tg;
    }

    private void addDistanceLODCoral(BranchGroup root, BoundingSphere bounds) {
        Transform3D tr = new Transform3D();
        tr.setTranslation(new Vector3f(2.4f, -1.1f, -5.2f));

        TransformGroup coralPositionTG = new TransformGroup(tr);

        // swap high-polygon models for low-polygon blocks
        Switch coralSwitch = new Switch();
        coralSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        coralSwitch.addChild(createDetailedCoral());
        coralSwitch.addChild(createSimpleCoral());

        DistanceLOD lod = new DistanceLOD(new float[] { 5.0f }, new Point3f(0f, 0f, 0f));
        lod.addSwitch(coralSwitch);
        lod.setSchedulingBounds(bounds);

        coralPositionTG.addChild(coralSwitch);
        coralPositionTG.addChild(lod);

        root.addChild(coralPositionTG);
    }

    private TransformGroup createDetailedCoral() {
        TransformGroup coralTG = new TransformGroup();
        Appearance app = createMaterial(new Color(210, 80, 100));

        for (int i = -1; i <= 1; i++) {
            Transform3D tr = new Transform3D();
            tr.setTranslation(new Vector3f(i * 0.18f, 0.25f, 0f));

            TransformGroup branchTG = new TransformGroup(tr);
            Cylinder branch = new Cylinder(0.04f, 0.65f, Cylinder.GENERATE_NORMALS, app);
            branchTG.addChild(branch);

            coralTG.addChild(branchTG);
        }

        return coralTG;
    }

    private TransformGroup createSimpleCoral() {
        TransformGroup coralTG = new TransformGroup();

        Box simple = new Box(0.28f, 0.35f, 0.12f, Box.GENERATE_NORMALS,
                createMaterial(new Color(170, 70, 80)));
        coralTG.addChild(simple);

        return coralTG;
    }

    // Billboard label that always faces the camera.
    private TransformGroup createBillboardLabel(BoundingSphere bounds) {
        Transform3D tr = new Transform3D();
        tr.setTranslation(new Vector3f(0f, 3.8f, -6.5f));

        TransformGroup labelTG = new TransformGroup(tr);
        labelTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Text2D text = new Text2D("Scanner Active", new Color3f(Color.WHITE), "Arial", 50, 1);
        labelTG.addChild(text);

        Billboard billboard = new Billboard(labelTG, Billboard.ROTATE_ABOUT_POINT, new Point3f(0f, 0f, 0f));
        billboard.setSchedulingBounds(bounds);
        labelTG.addChild(billboard);

        return labelTG;
    }

    private void setupKeyboard(Canvas3D cv) {
        cv.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                float step = 0.18f;

                Vector3f nextLocation = new Vector3f(submarineLocation);

                // Direction the submarine is facing.
                float forwardX = (float) Math.cos(submarineAngle);
                float forwardZ = (float) -Math.sin(submarineAngle);

                // Side direction for A/D sliding.
                float sideX = (float) Math.sin(submarineAngle);
                float sideZ = (float) Math.cos(submarineAngle);

                if (e.getKeyCode() == KeyEvent.VK_W) {
                    nextLocation.x += forwardX * step;
                    nextLocation.z += forwardZ * step;
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    nextLocation.x -= forwardX * step;
                    nextLocation.z -= forwardZ * step;
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    nextLocation.x -= sideX * step;
                    nextLocation.z -= sideZ * step;
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    nextLocation.x += sideX * step;
                    nextLocation.z += sideZ * step;
                } else if (e.getKeyCode() == KeyEvent.VK_Q) {
                    nextLocation.y += step;
                } else if (e.getKeyCode() == KeyEvent.VK_E) {
                    nextLocation.y -= step;
                } else {
                    return;
                }

                String scannerMessage = checkHeadlightScanner(nextLocation);
                String collisionMessage = checkCollision(nextLocation);

                if (collisionMessage == null) {
                    submarineLocation.set(nextLocation);
                    applySubmarineTransform();
                    updateHeadLight();

                    if (scannerMessage != null) {
                        messageLabel.setText(scannerMessage);
                    } else {
                        messageLabel.setText("Submarine position: x=" + round(submarineLocation.x)
                                + " y=" + round(submarineLocation.y)
                                + " z=" + round(submarineLocation.z)
                                + " angle=" + round((float) Math.toDegrees(submarineAngle)));
                    }
                } else {
                    if (scannerMessage != null) {
                        messageLabel.setText(scannerMessage + "   " + collisionMessage);
                    } else {
                        messageLabel.setText(collisionMessage);
                    }
                }
            }
        });
    }

    private void setupMouseRotate(Canvas3D cv) {
        cv.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                cv.requestFocus();
            }
        });

        cv.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouseX;
                lastMouseX = e.getX();

                submarineAngle -= dx * 0.01f;

                applySubmarineTransform();
                updateHeadLight();

                String scannerMessage = checkHeadlightScanner(submarineLocation);

                if (scannerMessage != null) {
                    messageLabel.setText(scannerMessage);
                } else {
                    messageLabel.setText("Submarine angle: " + round((float) Math.toDegrees(submarineAngle)) + " degrees");
                }
            }
        });
    }

    private void applySubmarineTransform() {
        Transform3D rotation = new Transform3D();
        rotation.rotY(submarineAngle);

        Transform3D translation = new Transform3D();
        translation.setTranslation(submarineLocation);

        translation.mul(rotation);
        submarineTransform.set(translation);
        submarineTG.setTransform(submarineTransform);
    }

    private void updateHeadLight() {
        float forwardX = (float) Math.cos(submarineAngle);
        float forwardZ = (float) -Math.sin(submarineAngle);

        headLight.setPosition(new Point3f(
                submarineLocation.x + forwardX * 0.75f,
                submarineLocation.y,
                submarineLocation.z + forwardZ * 0.75f
        ));

        headLight.setDirection(new Vector3f(forwardX, -0.1f, forwardZ));
    }

    private String checkHeadlightScanner(Vector3f testLocation) {
        // This checks the base/front of the visible light cone based on the submarine's angle.
        float forwardX = (float) Math.cos(submarineAngle);
        float forwardZ = (float) -Math.sin(submarineAngle);

        float baseCenterX = testLocation.x + forwardX * 1.95f;
        float baseCenterY = testLocation.y;
        float baseCenterZ = testLocation.z + forwardZ * 1.95f;

        float baseRadius = 0.60f;
        float baseThickness = 0.45f;

        for (int i = 0; i < artifactNames.length; i++) {
            if (!artifactFound[i]) {
                float artifactDX = artifactX[i] - baseCenterX;
                float artifactDZ = artifactZ[i] - baseCenterZ;

                // Distance across the round base of the cone.
                float sideDistance = (float) Math.sqrt(artifactDX * artifactDX + artifactDZ * artifactDZ);

                // Make sure it is near the base instead of far behind/in front of it.
                float forwardDistanceFromBase = artifactDX * forwardX + artifactDZ * forwardZ;
                float heightDistance = Math.abs(artifactY[i] - baseCenterY);

                if (sideDistance <= baseRadius
                        && Math.abs(forwardDistanceFromBase) <= baseThickness
                        && heightDistance <= 1.35f) {
                    return markArtifactFound(i, "Headlight found");
                }
            }
        }

        return null;
    }

    private String checkCollision(Vector3f nextLocation) {
        if (nextLocation.x < -6.2f || nextLocation.x > 6.2f
                || nextLocation.z < -8.2f || nextLocation.z > 4.2f) {
            return "Collision warning: edge of search area.";
        }

        if (nextLocation.y < -1.0f) {
            return "Collision warning: too close to the ocean floor.";
        }

        if (nextLocation.y > 2.0f) {
            return "Collision warning: too high above the search area.";
        }

        float forwardX = (float) Math.cos(submarineAngle);
        float forwardZ = (float) -Math.sin(submarineAngle);

        for (int i = 0; i < obstacleNames.length; i++) {
            float centerDX = nextLocation.x - obstacleX[i];
            float centerDZ = nextLocation.z - obstacleZ[i];
            float centerDistance = (float) Math.sqrt(centerDX * centerDX + centerDZ * centerDZ);

            float noseX = nextLocation.x + forwardX * 0.95f;
            float noseZ = nextLocation.z + forwardZ * 0.95f;
            float noseDX = noseX - obstacleX[i];
            float noseDZ = noseZ - obstacleZ[i];
            float noseDistance = (float) Math.sqrt(noseDX * noseDX + noseDZ * noseDZ);

            if ((centerDistance < obstacleRadius[i] || noseDistance < obstacleRadius[i])
                    && nextLocation.y < 0.35f) {
                return "Collision warning: submarine is too close to " + obstacleNames[i] + ".";
            }
        }

        return null;
    }

    private void setupMousePicking(Canvas3D cv) {
        cv.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                pickCanvas.setShapeLocation(e);
                PickResult result = pickCanvas.pickClosest();

                if (result == null) {
                    messageLabel.setText("Scanner result: no artifact detected.");
                    return;
                }

                Shape3D pickedShape = (Shape3D) result.getNode(PickResult.SHAPE3D);

                if (pickedShape != null && pickedShape.getUserData() != null) {
                    String name = pickedShape.getUserData().toString();
                    int index = getArtifactIndex(name);

                    if (index >= 0) {
                        messageLabel.setText(markArtifactFound(index, "Artifact discovered"));
                    }
                } else {
                    messageLabel.setText("Scanner result: object detected, but it is not an artifact.");
                }
            }
        });
    }
// kinda sucks needs more work.
    private String markArtifactFound(int index, String source) {
        if (!artifactFound[index]) {
            artifactFound[index] = true;
            artifactsFound++;

            if (artifactShapes[index] != null) {
                artifactShapes[index].setAppearance(createFoundArtifactMaterial());
            }
        }

        return source + ": " + artifactNames[index]
                + "   Found: " + artifactsFound + "/3";
    }

    private int getArtifactIndex(String artifactName) {
        for (int i = 0; i < artifactNames.length; i++) {
            if (artifactNames[i].equals(artifactName)) {
                return i;
            }
        }

        return -1;
    }

    private Appearance createLightBeamMaterial() {
        Appearance app = new Appearance();

        Material mat = new Material();
        mat.setAmbientColor(new Color3f(0.18f, 0.25f, 0.35f));
        mat.setDiffuseColor(new Color3f(0.45f, 0.75f, 1.0f));
        mat.setSpecularColor(new Color3f(0.8f, 0.9f, 1.0f));
        mat.setShininess(30.0f);
        app.setMaterial(mat);

        TransparencyAttributes ta = new TransparencyAttributes();
        ta.setTransparencyMode(TransparencyAttributes.BLENDED);
        ta.setTransparency(0.65f);
        app.setTransparencyAttributes(ta);

        return app;
    }

    private Appearance createFoundArtifactMaterial() {
        Appearance app = new Appearance();

        Material mat = new Material();
        mat.setAmbientColor(new Color3f(0.1f, 0.35f, 0.15f));
        mat.setDiffuseColor(new Color3f(0.1f, 0.9f, 0.35f));
        mat.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
        mat.setShininess(90.0f);
        app.setMaterial(mat);

        return app;
    }

    private Appearance createMaterial(Color color) {
        Appearance app = new Appearance();

        Material mat = new Material();
        mat.setAmbientColor(new Color3f(color.darker()));
        mat.setDiffuseColor(new Color3f(color));
        mat.setSpecularColor(new Color3f(1f, 1f, 1f));
        mat.setShininess(80.0f);
        app.setMaterial(mat);

        return app;
    }

    private Appearance createFloorTexture() {
        Appearance app = createMaterial(new Color(50, 80, 75));

        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                int value = ((x / 8 + y / 8) % 2 == 0) ? 80 : 55;
                Color c = new Color(25, value, 75);
                img.setRGB(x, y, c.getRGB());
            }
        }

        ImageComponent2D image = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, img);
        Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGB, 64, 64);
        texture.setImage(0, image);
        texture.setEnable(true);

        TextureAttributes ta = new TextureAttributes();
        ta.setTextureMode(TextureAttributes.MODULATE);

        app.setTexture(texture);
        app.setTextureAttributes(ta);

        return app;
    }

    // Chapter 10 picking logic.
    private void enablePicking(Primitive primitive, int artifactIndex) {
        for (int i = 0; i < primitive.numChildren(); i++) {
            Node child = primitive.getChild(i);

            if (child instanceof Shape3D) {
                Shape3D shape = (Shape3D) child;
                shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                shape.setUserData(primitive.getUserData());
                PickTool.setCapabilities(shape, PickTool.INTERSECT_FULL);

                artifactShapes[artifactIndex] = shape;
            }
        }
    }

    private float round(float value) {
        return Math.round(value * 10f) / 10f;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DeepSeaSubmarineExplorerRemote_Bailey_Diehl());
    }
}
