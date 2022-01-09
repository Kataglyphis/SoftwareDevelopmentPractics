package edu.kit.valaris.rendering.particleEffect;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * collect math func for comp
 */
public class MathUtil {

    /**
     * calc from shere to cartesis
     * @param radius given radius
     * @param theta angle y axis and x-z level
     * @param phi on x-y level
     * @return vector in cartesis
     */
    public static Vector3f convertSpericalToCartesian(float radius, float theta, float phi) {
        if(radius < 0 | theta < 0 | theta > FastMath.PI | phi >= FastMath.PI*2 | phi < 0) {

            throw new IllegalArgumentException("Illegal passing of arguments: No negative radius," +
                    "theta between 0 and PI, phi between 0 and less then 2*PI");

        }
        float x = radius * FastMath.sin(theta) * FastMath.cos(phi);
        float y = radius * FastMath.sin(theta) * FastMath.sin(phi);
        float z = radius * FastMath.cos(theta);
        Vector3f cartesianVector = new Vector3f(x, y, z);
        return cartesianVector;
    }

    /**
     * compute from polar to cartesian
     * @param radius given radius
     * @param phi angle of polar coordinates
     * @return vector in cartesian
     */
    public static Vector3f convertPolarToCartesian(float radius, float phi) {

        if(radius < 0 | phi < 0 | phi >= FastMath.TWO_PI) {
            throw new IllegalArgumentException("Wrong argument(s), radius > 0 and phi between [0,2PI)");
        }

        float x = radius * FastMath.cos(phi);
        float z = radius * FastMath.sin(phi);
        Vector3f result = new Vector3f(x, 0, z);

        return result;
    }

    /**
     * getting a random number in the given intervall
     * @param min the minimum number we have
     * @param max the maximum number to generate
     * @return random number between min and max
     */
    public static float getRandomArbitrary(float min, float max) {
        return FastMath.nextRandomFloat() * (max - min) + min;
    }
}
