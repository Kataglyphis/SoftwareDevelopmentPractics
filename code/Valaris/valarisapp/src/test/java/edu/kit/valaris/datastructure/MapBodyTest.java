package edu.kit.valaris.datastructure;

import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.roadgeneration.Road;
import edu.kit.valaris.generation.roadgeneration.RoadCursor;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class MapBodyTest {

    private MapBody mapBody;
    private Road road;
    private GenerationConfig generationConfig;

    @Before
    public void setUp() {

        RoadModel roadModel = new SimpleRoadModel();
        road = new Road();
        int segments = 20;

        generationConfig = new GenerationConfig();

        Vector2f width = new Vector2f(10,5);
        float roadCursorDistance = 2;
        for (int i = 0; i < segments; i++) {
            Vector3f position = new Vector3f(0,0,i*roadCursorDistance);
            RoadCursor roadCursor = new RoadCursor(0,0,0,position,width);
            road.addRoadCursor(roadCursor);
        }

        for (int i = 0; i < road.getRoadCursors().size(); i++) {
            RoadModelSegment roadModelSegment = new RoadModelSegment(i, 0, roadCursorDistance, width.x);
            roadModel.addRoadSegment(roadModelSegment);
        }

        mapBody = new MapBody(roadModel, new LinkedList<>(),road);
    }

    @Test
    public void test0Calc3DTransform() {

        RoadModel roadModel = mapBody.getRoad();
        for (int i = 0; i < roadModel.getLength() - 2; i++) {
            RoadModelSegment segment = roadModel.getWithIndex(i);

            // Located at RoadCursor i, not distorted
            Position position = new Position(i, new Vector2f(0,0));

            Transform transform3D = mapBody.calc3DTransform(position,0);

            Vector3f[] axes = new Vector3f[3];

            transform3D.getRotation().toAxes(axes);

            float vehicleHeight = generationConfig.getNumber("mapbody.vehicleHeight").floatValue();

            Vector3f position3D = transform3D.getTranslation().subtract(axes[1].mult(vehicleHeight));

            RoadCursor roadCursor = road.getRoadCursors().get(i);

            Vector3f relativeCursorPosition = position3D.subtract(roadCursor.getPosition());

            // Must be (very close to) zero
            assertTrue(relativeCursorPosition.length() <= 0.01f);
        }

    }
    @Test
    public void test1Calc3DTransform() {

        RoadModel roadModel = mapBody.getRoad();
        for (int i = 0; i < roadModel.getLength() - 2; i++) {
            RoadModelSegment segment = roadModel.getWithIndex(i);
            // Located at RoadCursor i, distorted to the Front one unit
            Position position = new Position(i, new Vector2f(1,0));

            Transform transform3D = mapBody.calc3DTransform(position,0);

            Vector3f[] axes = new Vector3f[3];

            transform3D.getRotation().toAxes(axes);

            float vehicleHeight = generationConfig.getNumber("mapbody.vehicleHeight").floatValue();

            Vector3f position3D = transform3D.getTranslation().subtract(axes[1].mult(vehicleHeight));

            RoadCursor roadCursor = road.getRoadCursors().get(i);

            Vector3f relativeCursorPosition = position3D.subtract(roadCursor.getPosition());

            // Must be (very close to) zero
            assertTrue(relativeCursorPosition.subtract(roadCursor.getDirection()).length() <= 0.01f);
        }

    }

    @Test
    public void test2Calc3DTransform() {

        RoadModel roadModel = mapBody.getRoad();
        for (int i = 0; i < roadModel.getLength() - 2; i++) {
            RoadModelSegment segment = roadModel.getWithIndex(i);

            // Located at RoadCursor i, distorted to the Right one unit
            Position position = new Position(i, new Vector2f(0, 1));

            Transform transform3D = mapBody.calc3DTransform(position, 0);

            Vector3f[] axes = new Vector3f[3];

            transform3D.getRotation().toAxes(axes);

            float vehicleHeight = generationConfig.getNumber("mapbody.vehicleHeight").floatValue();

            Vector3f position3D = transform3D.getTranslation().subtract(axes[1].mult(vehicleHeight));

            RoadCursor roadCursor = road.getRoadCursors().get(i);

            Vector3f relativeCursorPosition = position3D.subtract(roadCursor.getPosition());

            // Must be (very close to) zero
            assertTrue(relativeCursorPosition.subtract(roadCursor.getRight()).length() <= 0.01f);
        }
    }
}