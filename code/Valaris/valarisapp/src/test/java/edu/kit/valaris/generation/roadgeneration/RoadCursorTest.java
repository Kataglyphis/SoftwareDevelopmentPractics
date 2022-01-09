package edu.kit.valaris.generation.roadgeneration;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RoadCursorTest {

    private RoadCursor roadCursor1;
    private RoadCursor roadCursor2;
    private RoadCursor roadCursor3;
    private RoadCursor roadCursor4;
    private RoadCursor roadCursor4Copy;

    private RoadCursor roadCursor4CopyChanged;
    private RoadCursor roadCursorTranformed;
    private RoadCursor roadCursorRotated;

    private float maxOffset;

    @Before
    public void setUp() {
        maxOffset = 0.001f;

        Vector3f position = new Vector3f(0,0,0);
        Vector2f width = new Vector2f(10,5);
        roadCursor1 = new RoadCursor(0,0,0,position,width);

        position = new Vector3f(10,20,30);
        width = new Vector2f(5,10);
        roadCursor2 = new RoadCursor(FastMath.HALF_PI,0,0,position,width);


        position = new Vector3f(-10,-20,-30);
        width = new Vector2f(10,10);
        roadCursor3 = new RoadCursor(0,FastMath.HALF_PI,0,position,width);


        position = new Vector3f(1000,2000,3000);
        width = new Vector2f(5,5);
        roadCursor4 = new RoadCursor(-FastMath.HALF_PI,-FastMath.HALF_PI,0,position,width);


        position = new Vector3f(0,0,0);
        width = new Vector2f(5,5);
        roadCursorRotated = new RoadCursor(-FastMath.HALF_PI,0,0,position,width);
        roadCursorRotated.applyRotation(roadCursor2.getRotation());

        position = new Vector3f(0,0,10);
        width = new Vector2f(10,10);
        roadCursorTranformed = new RoadCursor(-FastMath.HALF_PI,0,0,position,width);
        roadCursorTranformed.applyTransformation(roadCursor2);

        roadCursor4Copy = new RoadCursor(roadCursor4);

        roadCursor4CopyChanged = new RoadCursor(roadCursor4Copy);
        roadCursor4CopyChanged.setAngles(0,0,0);
        roadCursor4CopyChanged.setPosition(new Vector3f(0,0,0));
        roadCursor4CopyChanged.setWidhtAndHightAndHight(new Vector2f(10,5));


        roadCursor1.addProperty(RoadCursorProperty.CHECKPOINT);

    }

    private boolean valueSmalerThanOffset(float value, float maxOffsetFromZero) {
        return  (Math.abs(value) < maxOffsetFromZero);
    }

    @Test
    public void testPosition() {
        assertTrue(valueSmalerThanOffset(roadCursor1.getPosition()
                .subtract(new Vector3f(0,0,0)).length(),maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor4CopyChanged.getPosition()
                .subtract(new Vector3f(0,0,0)).length(),maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor2.getPosition()
                .subtract(new Vector3f(10,20,30)).length(),maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor3.getPosition()
                .subtract(new Vector3f(-10,-20,-30)).length(),maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor4.getPosition()
                .subtract(new Vector3f(1000,2000,3000)).length(),maxOffset));

        assertTrue(valueSmalerThanOffset(roadCursor4.getPosition()
                .subtract(roadCursor4Copy.getPosition()).length(),maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursorRotated.getPosition()
                .subtract(new Vector3f(0,0,0)).length(),maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursorTranformed.getPosition()
                .subtract(new Vector3f(20,20,30)).length(),maxOffset));
    }

    @Test
    public void testDirection() {


        Vector3f direction1 = new Vector3f(0,0,1);
        Vector3f direction2 = new Vector3f(1,0,0);
        Vector3f direction3 = new Vector3f(0,1,0);
        Vector3f direction4 = new Vector3f(0,-1,0);

        assertTrue(valueSmalerThanOffset(roadCursor1.getDirection()
                .subtract(direction1).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor4CopyChanged.getDirection()
                .subtract(direction1).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursorRotated.getDirection()
                .subtract(direction1).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursorTranformed.getDirection()
                .subtract(direction1).length(), maxOffset));

        assertTrue(valueSmalerThanOffset(roadCursor2.getDirection()
                .subtract(direction2).length(), maxOffset));

        assertTrue(valueSmalerThanOffset(roadCursor3.getDirection()
                .subtract(direction3).length(), maxOffset));

        assertTrue(valueSmalerThanOffset(roadCursor4.getDirection()
                .subtract(direction4).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor4.getDirection()
                .subtract(roadCursor4Copy.getDirection()).length(), maxOffset));
    }

    @Test
    public void testRight() {
        Vector3f right1 = new Vector3f(-1,0,0);
        Vector3f right2 = new Vector3f(0,0,1);
        Vector3f right3 = new Vector3f(-1,0,0);
        Vector3f right4 = new Vector3f(0,0,-1);

        assertTrue(valueSmalerThanOffset(roadCursor1.getRight()
                .subtract(right1).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor4CopyChanged.getRight()
                .subtract(right1).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursorRotated.getRight()
                .subtract(right1).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursorTranformed.getRight()
                .subtract(right1).length(), maxOffset));

        assertTrue(valueSmalerThanOffset(roadCursor2.getRight()
                .subtract(right2).length(), maxOffset));

        assertTrue(valueSmalerThanOffset(roadCursor3.getRight()
                .subtract(right3).length(), maxOffset));

        assertTrue(valueSmalerThanOffset(roadCursor4.getRight()
                .subtract(right4).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor4.getRight()
                .subtract(roadCursor4Copy.getRight()).length(), maxOffset));
    }

    @Test
    public void testNormal() {
        Vector3f normal1 = new Vector3f(0,1,0);
        Vector3f normal2 = new Vector3f(0,1,0);
        Vector3f normal3 = new Vector3f(0,0,-1);
        Vector3f normal4 = new Vector3f(-1,0,0);

        assertTrue(valueSmalerThanOffset(roadCursor1.getNormal()
                .subtract(normal1).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor4CopyChanged.getNormal()
                .subtract(normal1).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursorRotated.getNormal()
                .subtract(normal1).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursorTranformed.getNormal()
                .subtract(normal1).length(), maxOffset));

        assertTrue(valueSmalerThanOffset(roadCursor2.getNormal()
                .subtract(normal2).length(), maxOffset));

        assertTrue(valueSmalerThanOffset(roadCursor3.getNormal()
                .subtract(normal3).length(), maxOffset));

        assertTrue(valueSmalerThanOffset(roadCursor4.getNormal()
                .subtract(normal4).length(), maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor4.getNormal()
                .subtract(roadCursor4Copy.getNormal()).length(), maxOffset));
    }

    @Test
    public void testXZAngle() {
        assertTrue(
                valueSmalerThanOffset(
                normalizeAngle(roadCursor1.getXZAngle()), maxOffset));
        assertTrue(valueSmalerThanOffset(
                normalizeAngle(roadCursor4.getXZAngle() + FastMath.HALF_PI), maxOffset));
    }

    @Test
    public void testYAngle() {
        assertTrue(valueSmalerThanOffset(
                normalizeAngle(roadCursor1.getYAngle()), maxOffset));
        assertTrue(valueSmalerThanOffset(
                normalizeAngle(roadCursor4.getYAngle()+FastMath.HALF_PI), maxOffset));
    }

    @Test
    public void testTiltAngle() {
        assertTrue(valueSmalerThanOffset(
                normalizeAngle(roadCursor1.getTiltAngle()), maxOffset));
        assertTrue(valueSmalerThanOffset(
                normalizeAngle(roadCursor4.getTiltAngle()), maxOffset));
    }

    @Test
    public void testRoadCursorProperty() {
        assertTrue(roadCursor1.hasProperty(RoadCursorProperty.CHECKPOINT));
    }

    @Test
    public void testRoadFrame() {
        assertTrue(valueSmalerThanOffset(roadCursor1.getRoadFrame()[0]
                .subtract(new Vector3f(-7, -2,0)).length(),maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor1.getRoadFrame()[1]
                .subtract(new Vector3f(-7, 7,0)).length(),maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor1.getRoadFrame()[2]
                .subtract(new Vector3f(7,7,0)).length(),maxOffset));
        assertTrue(valueSmalerThanOffset(roadCursor1.getRoadFrame()[3]
                .subtract(new Vector3f(7,-2,0)).length(),maxOffset));
    }

    private float normalizeAngle(float angle) {
        return (float) Math.min(Math.abs(angle),
                Math.abs(Math.abs(angle)-2*Math.PI));
    }
}