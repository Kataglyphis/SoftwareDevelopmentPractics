package edu.kit.valaris.generation.roadgeneration;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import edu.kit.valaris.generation.MathUtility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * Represents a road in 3 dimensional space. Constructed by RoadCursors.
 * @author Sidney Hansen
 */
public class Road {

    private ArrayList<RoadCursor> m_roadCursors;

    private Collection<RoadObject> m_roadObjects;


    public Road() {
        m_roadCursors = new ArrayList<>();
        m_roadObjects = new ArrayList<>();
    }

    /**
     * Updates angles of all road cursors to match position of next road cursor
     */
    public void updateDirections() {
        for (int i = 0; i < m_roadCursors.size() - 1; i++) {
            RoadCursor roadCursorA = m_roadCursors.get(i);
            RoadCursor roadCursorB = m_roadCursors.get(i + 1);

            Vector3f deltaPosition = roadCursorB.getPosition().subtract(roadCursorA.getPosition());

            Vector2f directionHorizontal = new Vector2f(roadCursorA.getDirection().x,roadCursorA.getDirection().z);
            Vector2f deltaHorizontal = new Vector2f(deltaPosition.x,deltaPosition.z);

            Vector2f directionVertical = new Vector2f(directionHorizontal.length(),roadCursorA.getDirection().y).normalize();
            Vector2f deltaVertical  = new Vector2f(deltaHorizontal.length(),deltaPosition.y).normalize();

            float angleHorizontal = deltaHorizontal.normalize().angleBetween(directionHorizontal.normalize());
            float angleVertical = directionVertical.angleBetween(deltaVertical.normalize());


            roadCursorA.setAngles(angleHorizontal + roadCursorA.getXZAngle(),
                    angleVertical + roadCursorA.getYAngle(),
                    roadCursorA.getTiltAngle());

        }
    }

    /**
     * Attaches the given road to this road. Transforming coordinates of all
     * given RoadCursors by adding the rotation and position of the last RoadCursor of this road.
     * @param road the Road to be attached.
     */
    public void attach(Road road) {

        if (this.m_roadCursors.size() == 0) {
            for(int i=0;i<road.m_roadCursors.size();i++) {
                addRoadCursor(road.m_roadCursors.get(i));
            }
            return;
        }

        RoadCursor c = getLastRoadCursor();

        // Delete last road cursor of previous road
        if (road.m_roadCursors.get(0).getPosition().length() < 2) {
            for (RoadCursorProperty property :
                    c.getRoadCursorProperties()) {
                road.m_roadCursors.get(0).addProperty(property);
            }
            m_roadCursors.remove(c);
        }

        // Apply to all roadCursors of the given road the transformation of the previous road
        for (int i = 0; i < road.m_roadCursors.size();i++) {
            road.m_roadCursors.get(i).applyTransformation(c);
            addRoadCursor(road.m_roadCursors.get(i));
        }

        this.m_roadObjects.addAll(road.m_roadObjects);
    }


    /**
     * Get roadCursors.
     * @return ArrayList of RoadCursors contained in this road.
     */
    public ArrayList<RoadCursor> getRoadCursors() {
        return m_roadCursors;

    }

    /**
     * Get Iterator over roadCursors.
     * @return Iterator over roadCursors.
     */
    public Iterator<RoadCursor> getRoadCursorIterator() {
        return m_roadCursors.iterator();

    }

    /**
     * Adds RoadCursor to road.
     * @param roadCursor to be added.
     */
    public void addRoadCursor(RoadCursor roadCursor) {
        m_roadCursors.add(roadCursor);
    }

    /**
     * Adds RoadObject to road. Eg. Representing Item.
     * @param roadObject to be added.
     */
    public void addRoadObject(RoadObject roadObject) {
        this.m_roadObjects.add(roadObject);
    }



    /**
     * Adds RoadObjects to road. Eg. Representing Items.
     * @param roadObjects to be added.
     */
    public void addRoadObjects(Collection<RoadObject> roadObjects) {
        this.m_roadObjects.addAll(roadObjects);
    }

    /**
     * Returns first roadCursor.
     *
     * @return first roadCursor of this road.
     */
    public RoadCursor getFirstRoadCursor() {
        return this.m_roadCursors.get(0);
    }

    /**
     * Returns last roadCursor.
     *
     * @return last roadCursor of this road.
     */
    public RoadCursor getLastRoadCursor() {
        return this.m_roadCursors.get(m_roadCursors.size() - 1);
    }


    /**
     * Returns roadObjects.
     *
     * @return roadObjects of this road.
     */
    public Collection<RoadObject> getRoadObjects() {
        return m_roadObjects;
    }


}
