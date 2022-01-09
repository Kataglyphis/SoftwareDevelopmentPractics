package edu.kit.valaris.generation;

import com.jme3.math.Vector3f;

/**
 * Class providing mathematical operations.
 * @author Sidney Hansen
 */
public class MathUtility {


    /**
     * Rotate Vector around Axis
     *
     * @param vec Vector to be rotated
     * @param axis Axis to be rotated around
     * @param theta Angle of rotation
     * @return the Rotated Vector
     */
    public static Vector3f rotateVectorCC(Vector3f vec, Vector3f axis, double theta){
        double x, y, z;
        double u, v, w;
        x = vec.getX();
        y = vec.getY();
        z = vec.getZ();
        u = axis.getX();
        v = axis.getY();
        w = axis.getZ();
        double xPrime = u * ( u * x + v * y + w * z ) * (1d - Math.cos(theta))
                + x * Math.cos(theta)
                + (-w * y + v * z) * Math.sin(theta);
        double yPrime = v * (u * x + v * y + w * z) * (1d - Math.cos(theta))
                + y * Math.cos(theta)
                + (w * x - u * z) * Math.sin(theta);
        double zPrime = w * (u * x + v * y + w * z) * (1d - Math.cos(theta))
                + z * Math.cos(theta)
                + (-v * x + u * y) * Math.sin(theta);
        return new Vector3f((float) xPrime,(float) yPrime,(float) zPrime);
    }

    /**
     * Limits the absolute value of value to limit.
     *
     * @param value the Value
     * @param limit the limit
     * @return
     * value if |value| < limit,
     * -limit if value < -limit,
     * limit if value > limit.
     */
    public static float limitAbsValue(float value, float limit) {
        return Math.min(limit,Math.abs(value))*Math.signum(value);

    }
}
