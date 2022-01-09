package edu.kit.valaris.generation.roadgeneration;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RoadTest {

    private RoadCursor roadCursor1;
    private RoadCursor roadCursor2;
    private RoadCursor roadCursor3;
    private RoadCursor roadCursor4;

    private Road road1;
    private Road road2;

    private float maxOffset;

    @Before
    public void setUp() {

        maxOffset = 0.001f;

        Vector3f position = new Vector3f(0,0,0);
        Vector2f width = new Vector2f(10,5);
        roadCursor1 = new RoadCursor(0,0,0,position,width);

        position = new Vector3f(0,0,10);
        width = new Vector2f(10,5);
        roadCursor2 = new RoadCursor(FastMath.HALF_PI,0,0,position,width);


        position = new Vector3f(0,0,0);
        width = new Vector2f(10,5);
        roadCursor3 = new RoadCursor(0,FastMath.HALF_PI,0,position,width);


        position = new Vector3f(0,0,10);
        width = new Vector2f(10,5);
        roadCursor4 = new RoadCursor(0,0,0,position,width);

        road1 = new Road();
        road1.addRoadCursor(roadCursor1);
        road1.addRoadCursor(roadCursor2);

        road2 = new Road();
        road2.addRoadCursor(roadCursor3);
        road2.addRoadCursor(roadCursor4);


    }

    @Test
    public void testUpdateDirections() {
        road2.updateDirections();
        assertTrue(valueSmalerThanOffset(
                roadCursor3.getDirection().subtract(new Vector3f(0,0,1)).length(),maxOffset));
    }

    private boolean valueSmalerThanOffset(float value, float maxOffsetFromZero) {
        return  (Math.abs(value) < maxOffsetFromZero);
    }

    @Test
    public void attach() {
        road1.attach(road2);
        assertEquals(road1.getRoadCursors().size(),3);
        assertTrue(valueSmalerThanOffset(roadCursor4.getPosition().subtract(new Vector3f(10,0,10)).length(),maxOffset));
    }

}