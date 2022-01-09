package edu.kit.valaris.generation.roadgeneration;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import edu.kit.valaris.generation.GenerationConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class RoadGeneratorTest {

    private RoadGenerator roadGenerator;
    private Road road;
    private Vector2f width;
    private String settings;



    @Before
    public void setUp() {
        roadGenerator = new RoadGenerator(new GenerationConfig());
        width = new Vector2f(10, 5);
        settings = "testsettings";

    }


    @Test
    public void generateSphereRoad() {


        road = roadGenerator.generateSphereRoad(666, 60,
                settings,
                width, 0, new HashSet<>());
        assertNotNull(road);
        road.updateDirections();

        for (int i = 0; i < road.getRoadCursors().size()-1; i++) {
            RoadCursor roadCursorA = road.getRoadCursors().get(i);
            RoadCursor roadCursorB = road.getRoadCursors().get(i+1);
            Vector3f direction = roadCursorA.getDirection();
            Vector3f deltaPos = roadCursorB.getPosition().subtract(roadCursorA.getPosition());
            float dirAngleOffset = direction.angleBetween(deltaPos.normalize());

            float dirDotRight = direction.dot(roadCursorA.getRight());

            // Offset must be smaller than 2 degree
            System.out.println("SphereRoad: RoadCursor "+i+" Direction Angle Offset: "+dirAngleOffset);
            assertTrue(dirAngleOffset < Math.PI*(2f/180f));

            System.out.println("SphereRoad: RoadCursor "+i+" dirDotRight: "+dirDotRight);
            assertTrue(dirDotRight < 0.01f);

            // Check Minimum Distance between RoadCursors
            assertTrue(roadCursorB.getPosition().subtract(roadCursorA.getPosition()).length() >= 2f);
        }

    }

    @Test
    public void generateConnectingRoad() {
        RoadCursor target = new RoadCursor(0,0,0,
                new Vector3f(20,0,40),new Vector2f(10,5));
        road = roadGenerator.generateConnectingRoad(666, target, width, settings, new HashSet<>());
        assertNotNull(road);
        road.updateDirections();
        for (int i = 0; i < road.getRoadCursors().size()-1; i++) {
            RoadCursor roadCursorA = road.getRoadCursors().get(i);
            RoadCursor roadCursorB = road.getRoadCursors().get(i+1);
            Vector3f direction = roadCursorA.getDirection();
            Vector3f deltaPos = roadCursorB.getPosition().subtract(roadCursorA.getPosition());
            float dirAngleOffset = direction.angleBetween(deltaPos.normalize());
            float dirDotRight = direction.dot(roadCursorA.getRight());

            // Offset must be smaller than 2 degree

            System.out.println("ConnectingRoad: RoadCursor "+i+" Direction Angle Offset: "+dirAngleOffset);
            assertTrue(dirAngleOffset < Math.PI*(2f/180f));
            System.out.println("SphereRoad: RoadCursor "+i+" dirDotRight: "+dirDotRight);
            assertTrue(dirDotRight < 0.01f);
            // Check Minimum Distance between RoadCursors
            assertTrue(roadCursorB.getPosition().subtract(roadCursorA.getPosition()).length() >= 2f);
        }
    }

    @Test
    public void generateCuboidRoad() {

        road = roadGenerator.generateCuboidRoad(666, new Vector3f(40,10,40),width,
                FastMath.HALF_PI, settings, new HashSet<>());
        assertNotNull(road);
        road.updateDirections();
        for (int i = 0; i < road.getRoadCursors().size()-1; i++) {
            RoadCursor roadCursorA = road.getRoadCursors().get(i);
            RoadCursor roadCursorB = road.getRoadCursors().get(i+1);
            Vector3f direction = roadCursorA.getDirection();
            Vector3f deltaPos = roadCursorB.getPosition().subtract(roadCursorA.getPosition());
            float dirAngleOffset = direction.angleBetween(deltaPos.normalize());
            float dirDotRight = direction.dot(roadCursorA.getRight());

            // Offset must be smaller than 2 degree

            System.out.println("CuboidRoad: RoadCursor "+i+" Direction Angle Offset: "+dirAngleOffset);
            assertTrue(dirAngleOffset < Math.PI*(2f/180f));
            System.out.println("SphereRoad: RoadCursor "+i+" dirDotRight: "+dirDotRight);
            assertTrue(dirDotRight < 0.01f);
            // Check Minimum Distance between RoadCursors
            assertTrue(roadCursorB.getPosition().subtract(roadCursorA.getPosition()).length() >= 2f);
        }
    }
}