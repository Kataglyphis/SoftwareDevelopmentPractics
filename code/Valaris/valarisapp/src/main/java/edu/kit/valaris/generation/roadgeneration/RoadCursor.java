package edu.kit.valaris.generation.roadgeneration;


import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import edu.kit.valaris.generation.MathUtility;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a point/segment of the Road. Contains Spacial data, and propertys of the road in this point.
 * @author Sidney Hansen
 */
public class RoadCursor {
    private Vector3f m_position;
    private Vector3f m_direction;
    private Vector3f m_right;
    private Vector3f m_normal;
    private Vector2f m_widthAndHeight;
    private Set<RoadCursorProperty> m_roadCursorProperties;

    /**
     * Creates new RoadCursor.
     *
     * @param horizontalAngle Angle in the xz plain.
     * @param slopeAngle Angle of the xz component to the y-axis.
     * @param tiltAngle Rotation around the axis defined by the road direction.
     * @param position Position of the RoadCursor.
     * @param widthAndHeight Vector2f describing width and Height of the road in this point.
     */
    public RoadCursor(float horizontalAngle, float slopeAngle, float tiltAngle, Vector3f position, Vector2f widthAndHeight) {
        m_position = new Vector3f(position);
        this.m_direction = new Vector3f(Vector3f.UNIT_Z);
        this.m_right = Vector3f.UNIT_X.mult(1f);
        this.m_normal = new Vector3f(Vector3f.UNIT_Y);
        this.m_widthAndHeight = new Vector2f(widthAndHeight);
        setAngles(horizontalAngle, slopeAngle, tiltAngle);
        m_roadCursorProperties = new HashSet<>();


    }

    /**
     *
     * Creates new RoadCursor as a copy of roadCursor.
     *
     * @param roadCursor to be copied.
     */
    public RoadCursor(RoadCursor roadCursor) {
        this.m_position = new Vector3f(roadCursor.m_position);
        this.m_widthAndHeight = new Vector2f(roadCursor.m_widthAndHeight);
        this.m_direction = new Vector3f(roadCursor.m_direction);
        this.m_normal = new Vector3f(roadCursor.m_normal);
        this.m_right = new Vector3f(roadCursor.m_right);
        m_roadCursorProperties = new HashSet<>(roadCursor.m_roadCursorProperties);
    }

    /**
     * Returns Position.
     *
     * @return position.
     */
    public Vector3f getPosition() {
        return m_position;
    }

    /**
     * Returns Direction. Ortogonal to m_normal and m_right.
     *
     * @return direction.
     */
    public Vector3f getDirection() {
        return m_direction;
    }

    /**
     * Returns Vector pointing to the right. Ortogonal to m_normal and m_direction.
     *
     * @return right Vector.
     */
    public Vector3f getRight() {
        return m_right;
    }

    /**
     * Returns Vector pointing up. Ortogonal to m_right and m_direction.
     *
     * @return normal Vector
     */
    public Vector3f getNormal() {
        return m_normal;
    }



    // ToDo rename to getWidhtAndHight()
    public Vector2f getWidhtAndHightAndHight() {
        return m_widthAndHeight;
    }


    /**
     * Returns Vector (width, height) of the Road in this point.
     *
     * @param widhtAndHightAndHight (width, height) of the Road.
     */
    public void setWidhtAndHightAndHight(Vector2f widhtAndHightAndHight) {
        this.m_widthAndHeight = widhtAndHightAndHight;
    }

    /**
     * Sets position.
     *
     * @param position to be set to.
     */
    public void setPosition(Vector3f position) {
        this.m_position = position;
    }

    /**
     * Adds property.
     *
     * @param roadCursorProperty to be added.
     */
    public void addProperty(RoadCursorProperty roadCursorProperty) {
        this.m_roadCursorProperties.add(roadCursorProperty);
    }

    /**
     * Returns if this RoadCursor has a given Property.
     *
     * @param roadCursorProperty the given Property.
     * @return true if RoadCursor has given Property, else false.
     */
    public boolean hasProperty(RoadCursorProperty roadCursorProperty) {
        return m_roadCursorProperties.contains(roadCursorProperty);
    }

    /**
     * Returns all RoadCursorProperties of the RoadCursor
     *
     * @return set of RoadCursorProperties
     */
    public Set<RoadCursorProperty> getRoadCursorProperties() {
        return m_roadCursorProperties;
    }

    /**
     * Returns Horizontal Angle.
     *
     * @return Horizontal Angle.
     */
    public float getXZAngle() {
        return new Vector2f(m_direction.x, m_direction.z).angleBetween(new Vector2f(0, 1f));
    }

    /**
     * Returns Vertical Angle.
     *
     * @return Vertical Angle.
     */
    public float getYAngle() {
        return (float) Math.asin(m_direction.y);
    }

    /**
     * returns Tilt Angle.
     *
     * @return Tilt Angle.
     */
    public float getTiltAngle() {
        return new Vector2f(1, 0).angleBetween(new Vector2f(new Vector2f(m_right.x, m_right.z).length(), m_right.y));
    }

    /**
     * Set Angles defining the rotation of this RoadCursor.
     *
     * @param xzAngle Horizontal Angle.
     * @param yAngle Vertical Angle.
     * @param tiltAngle Tilt Angle.
     */
    public void setAngles(float xzAngle, float yAngle, float tiltAngle) {
        Vector2f xzComponent = new Vector2f(0, 1f);
        xzComponent.rotateAroundOrigin(xzAngle, true);
        Vector3f xzVec = new Vector3f(xzComponent.x, 0, xzComponent.y).mult(Math.abs((float) Math.cos(yAngle)));
        xzVec.y = (float) Math.sin(yAngle);

        m_direction = new Vector3f(xzVec).normalize();
        m_right = new Vector3f(-xzVec.z, 0, xzVec.x);
        m_normal = new Vector3f(MathUtility.rotateVectorCC(m_direction, m_right, Math.PI / 2));

        m_normal = MathUtility.rotateVectorCC(m_normal, m_direction, tiltAngle).normalize();
        m_right = MathUtility.rotateVectorCC(m_right, m_direction, tiltAngle).normalize();
    }


    /**
     * Applies rotation to this RoadCursor.
     *
     * @param q Quaternion defining the Rotation.
     */
    public void applyRotation(Quaternion q) {
        q.multLocal(m_direction);
        q.multLocal(m_normal);
        q.multLocal(m_right);
    }


    /**
     * Returns a Quaternion representing this Cursors rotation.
     *
     * @return  Quaternion representing rotation.
     */
    public Quaternion getRotation() {
        Quaternion q = new Quaternion();
        q.fromAxes(m_right.normalize().mult(-1f), m_normal.normalize(), m_direction.normalize());
        return q;
    }


    /**
     * Transforms coordinates of this RoadCursors
     * by adding the rotation and position of the given RoadCursor.
     *
     * @param c the given RoadCursor.
     */
    public void applyTransformation(RoadCursor c) {
        m_position = new Vector3f().add(
                c.m_right.mult(-m_position.x)).add(
                c.m_normal.mult(m_position.y)).add(
                c.m_direction.mult(m_position.z));
        m_position.addLocal(c.m_position);
        applyRotation(c.getRotation());
    }


    /**
     * Returns quad, representing profile of road in this cursor
     * @return quad
     */
    public Vector3f[] getRoadFrame() {

        final float safetyDistance = 2f;

        return new Vector3f[] {
                m_position.add(m_right.mult(safetyDistance + m_widthAndHeight.x / 2f))
                        .add(m_normal.mult(-safetyDistance)),

                m_position.add(m_right.mult(safetyDistance + m_widthAndHeight.x / 2f))
                        .add(m_normal.mult(safetyDistance + m_widthAndHeight.y)),

                m_position.add(m_right.mult(- safetyDistance - m_widthAndHeight.x / 2f))
                        .add(m_normal.mult(safetyDistance + m_widthAndHeight.y)),

                m_position.add(m_right.mult(- safetyDistance - m_widthAndHeight.x / 2f))
                        .add(m_normal.mult(-safetyDistance))
        };
    }

}
