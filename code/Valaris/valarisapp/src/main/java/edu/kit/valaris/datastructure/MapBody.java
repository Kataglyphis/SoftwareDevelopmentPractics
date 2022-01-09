package edu.kit.valaris.datastructure;

import com.jme3.math.*;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.roadgeneration.Road;
import edu.kit.valaris.generation.roadgeneration.RoadCursor;
import java.util.LinkedList;

public class MapBody implements IMapBody {


    private Road m_road;

    private RoadModel m_roadModel;

    private LinkedList<RoadInitializationObject> roadObjects;

    private GenerationConfig m_generationConfig;
    private float m_vehicleDim;
    private float m_resolution;
    private float m_vehicleGroundDistance;


    public MapBody(RoadModel roadModel, LinkedList<RoadInitializationObject> roadObjects, Road road) {
        this.m_road = road;
        this.m_roadModel = roadModel;
        this.roadObjects = roadObjects;
        this.m_generationConfig = new GenerationConfig();
        m_vehicleGroundDistance = m_generationConfig.getNumber("mapbody.vehicleHeight").floatValue();
        m_vehicleDim = m_generationConfig.getNumber("mapbody.vehicleRotationSurfaceDimension").floatValue();
        m_resolution = m_generationConfig.getNumber("mapbody.vehicleRotationResolution").floatValue();
    }

    @Override
    public RoadModel getRoad() {
        return m_roadModel;
    }

    @Override
    public LinkedList<RoadInitializationObject> getObjects() {
        return roadObjects;
    }


    private Vector3f calc3DCoord(Position position) {

        float xOffsetC = 1;
        Position actualPosition = position.deepCopy();

        // Calculate valid position, updating index and offsets
        while (xOffsetC >= 0 || actualPosition.getOffset().x < 0) {

            if (actualPosition.getSegmentIndex() >= m_roadModel.getLength() - 2
                    || actualPosition.getSegmentIndex() <= 0) {
                break;
            }

            RoadModelSegment segmentB = getRoad().getPath().get(actualPosition.getSegmentIndex());

            Vector2f bDir = new Vector2f(0, 1);
            Vector2f bRight = new Vector2f(1, 0);

            Vector2f pB = new Vector2f(actualPosition.getOffset().y, actualPosition.getOffset().x);

            if (actualPosition.getOffset().x < 0) {
                RoadModelSegment segmentA = getRoad().getPath().get(actualPosition.getSegmentIndex() - 1);
                Vector2f aPos = bDir.mult(-segmentA.getLength());
                aPos.rotateAroundOrigin(segmentB.getAngle(),false);

                Vector2f aDir = new Vector2f(bDir);
                aDir.rotateAroundOrigin(segmentB.getAngle(), false);

                Vector2f aRight = new Vector2f(bRight);
                aRight.rotateAroundOrigin(segmentB.getAngle(), false);

                Vector2f pA = pB.subtract(aPos);
                float xOffsetA = pA.dot(aDir);
                float yOffsetA = pA.dot(aRight);
                actualPosition = new Position(actualPosition.getSegmentIndex() - 1, new Vector2f(xOffsetA, yOffsetA));
            } else {
                RoadModelSegment segmentC = getRoad().getPath().get(actualPosition.getSegmentIndex() + 1);
                Vector2f cPos = new Vector2f(0, segmentB.getLength());
                Vector2f cDir = new Vector2f(bDir);
                cDir.rotateAroundOrigin(segmentC.getAngle(), true);

                Vector2f cRight = new Vector2f(bRight);
                cRight.rotateAroundOrigin(segmentC.getAngle(), true);

                Vector2f pC = pB.subtract(cPos);
                xOffsetC = pC.dot(cDir);
                float yOffsetC = pC.dot(cRight);

                if (xOffsetC >= 0) {
                    actualPosition = new Position(actualPosition.getSegmentIndex() + 1, new Vector2f(xOffsetC, yOffsetC));
                }
            }
        }


        // Calculate 3D Coordinates from valid position
        RoadCursor roadCursorA = m_road.getRoadCursors().get(
                actualPosition.getSegmentIndex());

        RoadCursor roadCursorB = m_road.getRoadCursors().get(
                actualPosition.getSegmentIndex() + 1);

        float alpha = m_roadModel.getWithIndex(actualPosition.getSegmentIndex()+1).getAngle();
        float yOffA = actualPosition.getOffset().y;
        float yOffB = yOffA/(float)Math.cos(alpha);

        // Straight Vector
        Vector3f distance3DA = roadCursorB.getPosition().add(roadCursorB.getRight().mult(yOffB))
                .subtract(roadCursorA.getPosition().add(roadCursorA.getRight().mult(yOffA)));


        Vector3f relativeXCordA = distance3DA.normalize().mult(actualPosition.getOffset().x);
        Vector3f relativeYCordA = roadCursorA.getRight().mult(actualPosition.getOffset().y);

        Vector3f position3D = roadCursorA.getPosition().add(relativeXCordA).add(relativeYCordA);

        return position3D;
    }


    @Override
    public Transform calc3DTransform(Position position, float angle) {

        Vector3f position3D = calc3DCoord(position);


        // Creating continues rotation, using average inclination over several points

        Vector3f avgFront = new Vector3f().zero();
        Vector3f avgRight = new Vector3f().zero();

        Position frontPos = new Position(position.getSegmentIndex(),
                position.getOffset().add(new Vector2f((float) Math.cos(angle), (float) Math.sin(angle)).mult(-m_vehicleDim /2f)));
        Position rightPos = new Position(position.getSegmentIndex(),
                position.getOffset().add(new Vector2f((float) Math.sin(angle), -(float) Math.cos(angle)).mult(-m_vehicleDim /2f)));
        Vector3f lastFront = calc3DCoord(frontPos);
        Vector3f lastRight = calc3DCoord(rightPos);

        for (int i = -(int)(m_resolution /2f) + 1; i <= (int)(m_resolution /2f); i++) {
            frontPos = new Position(position.getSegmentIndex(),
                    position.getOffset().add(new Vector2f((float) Math.cos(angle), (float) Math.sin(angle)).mult((m_vehicleDim)*((float)i/ m_resolution))));

            rightPos = new Position(position.getSegmentIndex(),
                    position.getOffset().add(new Vector2f((float) Math.sin(angle), -(float) Math.cos(angle)).mult((m_vehicleDim)*((float)i/ m_resolution))));

            Vector3f front = calc3DCoord(frontPos);
            Vector3f right = calc3DCoord(rightPos);

            avgFront.addLocal(front.subtract(lastFront).normalize());
            avgRight.addLocal(right.subtract(lastRight).normalize());

            lastFront = front.clone();
            lastRight = right.clone();
        }


        Vector3f frontDir = avgFront.normalize().negateLocal();
        Vector3f rightDir = avgRight.normalize().negateLocal();

        Vector3f normalDir = frontDir.cross(rightDir).normalize();

        Quaternion rotation = new Quaternion().fromAxes(rightDir, normalDir, frontDir);

        // Add ground distance
        position3D.addLocal(normalDir.mult(m_vehicleGroundDistance));

        Transform transform = new Transform(position3D, rotation);

        return transform;
    }

}
