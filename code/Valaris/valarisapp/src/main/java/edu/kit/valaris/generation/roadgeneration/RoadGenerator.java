package edu.kit.valaris.generation.roadgeneration;



import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.GeneratorSettings;
import edu.kit.valaris.generation.MathUtility;
import edu.kit.valaris.generation.RandomNumberGenerator;

import java.util.*;

/**
 * Implementation of IRoadGenerator
 *
 * @author Sidney Hansen
 */
public class RoadGenerator implements IRoadGenerator {

    private GenerationConfig m_generationConfig;

    public RoadGenerator(GenerationConfig generationConfig) {
        this.m_generationConfig = generationConfig;
    }




    @Override
    public Road generateSphereRoad(int seed, float radius, String settings, Vector2f entryWidth, float exitAngle, Set<GeneratorSettings> flags) {


        final int roadCursorUntilExitProcedure = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".RoadCursorUntilExitProcedure").intValue();
        final float collisionGridDistance = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".CollisionGridDistance").floatValue();
        final int maxIterations = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".MaxIterations").intValue();

        final float minWidht = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".MinWidht").floatValue();
        final float minHeight = m_generationConfig.getNumber(
                        "roadgeneration.sphereRoad.cofigurations." + settings + ".MinHeight").floatValue();
        final float maxWidht = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".MaxWidht").floatValue();
        final float maxHeight = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".MaxHeight").floatValue();
        final float maxWidhtDerivative = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".MaxWidhtDerivative").floatValue();
        final float widthRecenterPower = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".WidthRecenterPower").floatValue();
        final float widhtSmoothness = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".WidhtSmoothness").floatValue();




        final Vector3f sphereCenter = new Vector3f(0,0,radius);
        int cursorCounter;

        boolean isValid = false;
        int cnt = 0;
        Road road = new Road();
        RoadCursor roadCursor;
        RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(seed);
        boolean roadExit = false;


        // Continue until a valid road is generated.
        while (!isValid) {
            road = new Road();
            cursorCounter = 1;
            roadCursor = new RoadCursor(0,0,0,Vector3f.ZERO, entryWidth);
            road.addRoadCursor(roadCursor);

            Collection<Integer> [][] collisionGrid = getCollisionGrid(radius, collisionGridDistance);

            if (flags.contains(GeneratorSettings.DEBUG)) {
                System.out.println("\nRoadGeneration for sphereRoad started.\n" + cnt + 1 + ". Try.");
            }

            // Continue until road is generated.
            while (cursorCounter <= roadCursorUntilExitProcedure
                    || !roadExit) {

                roadCursor = getNewCursor(road, collisionGrid,
                        collisionGridDistance, randomNumberGenerator.getNewSeed(),
                        radius, exitAngle,
                        (cursorCounter > roadCursorUntilExitProcedure), settings, flags);
                road.addRoadCursor(roadCursor);


                if (flags.contains(GeneratorSettings.DEBUG)) {
                    System.out.println("RoadCursor added: " + cursorCounter + ", position: " + roadCursor.getPosition().toString());

                }

                if (roadCursor.getPosition().y >= 0) {
                    roadExit = roadCursor.getPosition().subtract(sphereCenter).length() > radius;
                } else {
                    roadExit = roadCursor.getPosition().subtract(sphereCenter).length() > Math.sqrt(Math.pow(roadCursor.getPosition().y,2)+Math.pow(radius,2));

                }

                cursorCounter++;



                // Cancel roadgeneration if road is to long.
                if (cursorCounter > roadCursorUntilExitProcedure * 10) {
                    break;
                }
            }

            // Cancel roadgeneration if there is still no valid road after to many iterations.
            if (cnt++ > maxIterations) {
                return null;
            }

            calculateRandomWidth(seed, road, new Vector2f(minWidht,minHeight),
                    new Vector2f(maxWidht,maxHeight), maxWidhtDerivative,
                    widthRecenterPower,widhtSmoothness);

            // Check if road is valid.
            isValid = checkSphereRoad(collisionGrid, collisionGridDistance, road, radius, exitAngle, settings);
        }

        if (flags.contains(GeneratorSettings.DEBUG)) {
            System.out.println("\nRoadGeneration finished after: " + cnt + 1 + " Tries.");
        }

        road.getRoadCursors().remove(road.getRoadCursors().size()-1);
        RoadCursor lastRoadCursor = new RoadCursor(road.getLastRoadCursor());

        Vector3f position;
        if (lastRoadCursor.getPosition().y > 0) {
            position = sphereCenter.add(lastRoadCursor.getPosition().subtract(sphereCenter).normalize().mult(radius));
        } else {
            position = new Vector3f(0, lastRoadCursor.getPosition().y, radius).add(
                    new Vector3f(lastRoadCursor.getPosition().x, 0, lastRoadCursor.getPosition().z)
                            .subtract(sphereCenter).normalize().mult(radius));
        }
        road.getRoadCursors().set(road.getRoadCursors().size() - 1,
                new RoadCursor(
                    road.getFirstRoadCursor().getXZAngle() + exitAngle,
                    lastRoadCursor.getYAngle(),
                    lastRoadCursor.getTiltAngle(),
                    position,
                    lastRoadCursor.getWidhtAndHightAndHight()));
        road.updateDirections();
        return road;
    }


    /**
     * Grid to calculate collisions of road whit itself.
     *
     * @param size size of grid
     * @param gridDistance distance between neighbour points in grid
     * @return collision grid
     */
    private Collection<Integer>[][] getCollisionGrid(float size, float gridDistance) {
        int segments = (int)(1 + size * 2f / gridDistance);

        ArrayList<Integer>[][] collisionGrid =  new ArrayList[segments][segments];

        for (int i = 0; i < collisionGrid.length; i++) {
            for (int j = 0; j < collisionGrid[i].length; j++) {
                collisionGrid[i][j] = new ArrayList<>();
            }
        }
        return collisionGrid;



    }

    /**
     * Map a road cursor to the collisionGrid.
     *
     * @param collisionGrid the collisionGrid
     * @param gridDistance distance between neighbour points in grid
     * @param roadCursor to be mapped
     * @param index of roadcursor
     */
    private void mapToGrid(Collection<Integer>[][] collisionGrid, float gridDistance, RoadCursor roadCursor, int index) {

        ArrayList<int[]> positionIndeces = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                positionIndeces.add(
                        new int[]{(int)(roadCursor.getPosition().x / gridDistance) + collisionGrid.length / 2 + x,
                            (int)(roadCursor.getPosition().z / gridDistance) + z});
            }
        }

        for (int[] positionIndex : positionIndeces) {
            if (0 <= positionIndex[0]
                    && positionIndex[0] < collisionGrid.length
                    && 0 <= positionIndex[1]
                    && positionIndex[1] < collisionGrid.length) {
                collisionGrid[positionIndex[0]][positionIndex[1]].add(index);
            }

        }

    }


    /**
     * Returns a collection containing all possibly intersecting roadcursors
     * whit the given roadcursor.
     *
     * @param collisionGrid the collisionGrid
     * @param gridDistance distance between neighbour points in grid
     * @param road the road
     * @param roadCursor the given roadcursor
     * @param maxIndex highest index to be checked for collision
     * @return collection of possibly intersecting roadcursors
     */
    private Collection<RoadCursor> getIntersectingCursors(
            Collection<Integer>[][] collisionGrid, float gridDistance, Road road, RoadCursor roadCursor, int maxIndex) {

        int [] positionIndex = new int[]{(int)(roadCursor.getPosition().x / gridDistance) + collisionGrid.length / 2,
            (int)(roadCursor.getPosition().z / gridDistance)};
        ArrayList<RoadCursor> intersectingCursors = new ArrayList<>();

        if (0 <= positionIndex[0]
                && positionIndex[0] < collisionGrid.length
                && 0 <= positionIndex[1]
                && positionIndex[1] < collisionGrid.length) {

            for (int cursorIndex : collisionGrid[positionIndex[0]][positionIndex[1]]) {
                if (cursorIndex <= maxIndex) {
                    intersectingCursors.add(road.getRoadCursors().get(cursorIndex));
                }
            }
        }
        return intersectingCursors;
    }


    /**
     * Gets new roadcursor for sphereroad.
     *
     * @param road the current road.
     * @param collisionGrid the current collisionGrid.
     * @param gridDistance distance between neighbour points in collisionGrid.
     * @param seed the current seed.
     * @param radius the sphere radius
     * @param exitAngle angle in which to depart out of the sphere
     * @param exit if the road is in exit procedure
     * @param settings setting of the road
     * @param flags optional settings
     * @return new RoadCursor
     */
    private RoadCursor getNewCursor(
            Road road, Collection<Integer>[][] collisionGrid, float gridDistance,
            int seed, float radius, float exitAngle,
            boolean exit, String settings, Set<GeneratorSettings> flags) {


        final int repulsiveForceIndexDistance = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".RepulsiveForceIndexDistance").intValue();
        final float roadCursorDistance = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".RoadCursorDistance").intValue();

        RoadCursor lastRoadCursor = new RoadCursor(road.getLastRoadCursor());
        int index = road.getRoadCursors().size();
        RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(seed);


        // Get intersecting cursors
        Collection<RoadCursor> intersectingRoadCursors =
                getIntersectingCursors(collisionGrid, gridDistance, road, road.getLastRoadCursor(),
                        index - repulsiveForceIndexDistance);

        // Calculate angles
        float [] angles = calculateNextCursorAngles(randomNumberGenerator.getNewSeed(), intersectingRoadCursors, road, radius, exitAngle, exit, settings);

        // Calculate position
        Vector3f newPosition = lastRoadCursor.getPosition().add(lastRoadCursor.getDirection().mult(roadCursorDistance));

        // Create new RoadCursor
        RoadCursor roadCursor = new RoadCursor(angles[0], angles[1],0,newPosition,lastRoadCursor.getWidhtAndHightAndHight());

        // Add to collisionGrid
        mapToGrid(collisionGrid,gridDistance,roadCursor,index);

        return roadCursor;
    }


    private float[] calculateNextCursorAngles(int seed,Collection<RoadCursor> intersectingRoadCursors,
                                                 Road road, float sphereRadius, float exitAngle, boolean exit, String settings) {

        final RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(seed);
        final RoadCursor lastCursor = road.getLastRoadCursor();
        final Vector3f direction = lastCursor.getDirection().normalize();
        final Vector2f exitDirection = new Vector2f((float)Math.sin(exitAngle),(float)Math.cos(exitAngle)).normalize();


        final float slopeDerivativeRandomnes = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".SlopeDerivativeRandomnes").floatValue();
        final float curveRandomnes = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".CurveRandomnes").floatValue();
        final float maxSlopeDerivative = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".MaxSlopeDerivative").floatValue();
        final float maxSlope = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".MaxSlope").floatValue();
        final float maxCurveAngle = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".MaxCurveAngle").floatValue();
        final float repulsiveForceRadius = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".RepulsiveForceRadius").floatValue();
        final float repulsiveForceGradientPower = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".RepulsiveForceGradientPower").floatValue();
        final float recenterPower = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".RecenterPower").floatValue();
        final float decenterPower = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".DecenterPower").floatValue();


        final Vector3f relativeSpherePosition = lastCursor.getPosition()
                .subtract(new Vector3f(0,0,sphereRadius));

        final Vector2f relativePositionXZ = new Vector2f(relativeSpherePosition.x,relativeSpherePosition.z);

        final float hightRecenterCoefficient = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".HightRecenterCoefficient").floatValue();

        final Vector2f directionXZ = new Vector2f(direction.x,direction.z).normalize();

        final Vector2f directionY = new Vector2f(directionXZ.length(),direction.y).normalize();


        // Calculate recenter angle
        float recenterAngle = calculateAngleFromForceToCenter(
                directionXZ, relativePositionXZ, sphereRadius, recenterPower);

        // Calculate decenter angle, which is used if exit is true
        float decenterAngle = calculateAngleFromForceFromCenter(
                directionXZ, relativePositionXZ, sphereRadius, decenterPower);


        //avoiding collision whit road
        float maxCursorAngle = 0;

        Vector3f relativeCursorPosition;
        Vector2f relativePositionYComponent;
        float currentAngle;

        for (RoadCursor intersectingRoadCursor : intersectingRoadCursors) {
            relativeCursorPosition = lastCursor.getPosition().subtract(intersectingRoadCursor.getPosition());
            relativePositionYComponent = new Vector2f(
                    new Vector2f(relativeCursorPosition.x,relativeCursorPosition.z).length(),relativeCursorPosition.y);

            currentAngle = calculateRepulsiveForce(
                    directionY,relativePositionYComponent,repulsiveForceRadius,repulsiveForceGradientPower);

            if (Math.abs(currentAngle) >= Math.abs(maxCursorAngle)) {
                maxCursorAngle = currentAngle;
            }
        }


        // Deterministic XZAngle

        float deterministicXZAngle;
        if (exit) {
            deterministicXZAngle = MathUtility.limitAbsValue(decenterAngle, maxCurveAngle);

            Vector2f exitDelta = exitDirection.mult(sphereRadius).subtract(relativePositionXZ);

            Vector2f rightVector = new Vector2f(-exitDelta.normalize().y, exitDelta.normalize().x);


            deterministicXZAngle +=
                    (maxCurveAngle - Math.abs(deterministicXZAngle))
                            * (rightVector.dot(directionXZ));
        } else {
            deterministicXZAngle = MathUtility.limitAbsValue(recenterAngle, maxCurveAngle);
        }



        // Deterministic YAngle

        float sphereHeight = (float) Math.sqrt(Math.max(4, Math.pow(sphereRadius,2) - Math.pow(relativePositionXZ.length(),2)));
        float deterministicYAngle = Math.min(Math.abs(maxCursorAngle),maxSlopeDerivative) * Math.signum(maxCursorAngle);

        deterministicYAngle -= maxSlopeDerivative
                * MathUtility.limitAbsValue(hightRecenterCoefficient * (relativeSpherePosition.y / sphereHeight),1);


        // Apply randomness

        float randomXZComponent = 2 * (0.5f - randomNumberGenerator.random());
        float randomYComponent = 2 * (0.5f - randomNumberGenerator.random());

        float randomXZAngle = curveRandomnes * (maxCurveAngle - Math.abs(deterministicXZAngle)) * randomXZComponent;
        float randomYAngle = slopeDerivativeRandomnes * (maxSlopeDerivative - Math.abs(deterministicYAngle)) * randomYComponent;


        float resultingXYAngle = deterministicXZAngle + randomXZAngle;
        float resultingYAngle = deterministicYAngle + randomYAngle;


        // Calculate absolute Angle
        resultingXYAngle = (resultingXYAngle + lastCursor.getXZAngle());
        resultingYAngle = MathUtility.limitAbsValue(resultingYAngle + lastCursor.getYAngle(), maxSlope);



        return new float[]{resultingXYAngle, resultingYAngle};

    }


    private float calculateRepulsiveForce(Vector2f direction, Vector2f relativePosition, float radius, float power) {
        float angle = direction.angleBetween(new Vector2f(0,
                Math.signum(relativePosition.y)).normalize());
        float scale = (float) Math.pow(Math.max(0,(radius - relativePosition.length()) / radius), power);
        return angle * scale;
    }

    private float calculateAngleFromForceToCenter(Vector2f direction, Vector2f relativePosition, float radius, float power) {
        float angle = -(float)Math.acos(direction.normalize()
                .dot(relativePosition.normalize().mult(-1f)))
                * Math.signum(direction.normalize()
                .angleBetween(relativePosition.normalize().mult(-1f)));

        float scale = (float)Math.pow(Math.min(1,relativePosition.length() / radius),power);

        return angle * scale;
    }


    private float calculateAngleFromForceFromCenter(Vector2f direction, Vector2f relativePosition, float radius, float power) {
        float angle = -(float)Math.acos(direction.normalize()
                .dot(relativePosition.normalize()))
                * Math.signum(direction.normalize()
                .angleBetween(relativePosition.normalize()));

        float scale = (float)Math.pow(Math.min(1,relativePosition.length() / radius),power);

        return angle * scale;
    }


    /**
     * Check if given road is valid.
     *
     * @param collisionGrid the collisiongrid
     * @param gridDistance distance between neighbour points in collisionGrid.
     * @param road the given road
     * @param radius of the sphere
     * @param exitAngle angle in which to depart out of the sphere
     * @return if road is valid
     */
    private boolean checkSphereRoad(Collection<Integer>[][] collisionGrid, float gridDistance,
                                    Road road, float radius, float exitAngle, String settings) {

        ArrayList<RoadCursor> roadCursors = road.getRoadCursors();
        boolean validRoad;

        final int repulsiveForceIndexDistance = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".RepulsiveForceIndexDistance").intValue();
        final float minRoadCollisionDistance = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".MinRoadCollisionDistance").floatValue();
        final float maxExitOffset = m_generationConfig.getNumber(
                "roadgeneration.sphereRoad.cofigurations." + settings + ".MaxExitOffset").floatValue();



        validRoad = true;

        final Vector2f exitDirection = new Vector2f((float)Math.sin(exitAngle),(float)Math.cos(exitAngle)).normalize();

        Collection<RoadCursor> intersectingRoadCursors;
        for (int i = 0; i < roadCursors.size(); i++) {

            // Check for collision of road
            intersectingRoadCursors = getIntersectingCursors(
                    collisionGrid,gridDistance,road,roadCursors.get(i),i - repulsiveForceIndexDistance);

            for (RoadCursor intersectingRoadCursor : intersectingRoadCursors) {
                if (intersectingRoadCursor.getPosition().subtract(roadCursors.get(i).getPosition()).length() < minRoadCollisionDistance) {
                    validRoad = false;
                    break;
                }
            }


            // Check if position in NaN
            if (Float.isNaN(roadCursors.get(i).getPosition().length())) {
                validRoad = false;
                break;
            }

            // Check if road exits sphere, before last cursor
            if (i > 0 && i < roadCursors.size() - 1) {
                Vector3f relativeRoadCursorPosition = roadCursors.get(i).getPosition().subtract(new Vector3f(0,0,radius));
                float farestPointFromCenter = Math.max(relativeRoadCursorPosition
                .add(roadCursors.get(i).getRight().mult(roadCursors.get(i).getWidhtAndHightAndHight().x)).length(),
                        relativeRoadCursorPosition
                .add(roadCursors.get(i).getRight().mult(-roadCursors.get(i).getWidhtAndHightAndHight().x)).length());
                if (relativeRoadCursorPosition.y >= 0) {
                    validRoad = validRoad && (farestPointFromCenter < radius);
                } else {
                    validRoad = validRoad && (Math.sqrt(Math.pow(farestPointFromCenter,2) - Math.pow(relativeRoadCursorPosition.y,2)) < radius);
                }
            }
        }

        if (1 -  maxExitOffset >= exitDirection.normalize()
                .dot(new Vector2f(road.getLastRoadCursor().getPosition().x,road.getLastRoadCursor().getPosition().z - radius).normalize())) {
            validRoad = false;
        }

        return validRoad;

    }



    @Override
    public Road generateConnectingRoad(int seed, RoadCursor target, Vector2f entryWidth,
            String settings, Set<GeneratorSettings> flags) {
        Road road = new Road();

        float roadCursorDistance = m_generationConfig.getNumber(
                "roadgeneration.tunnelRoad.configurations." + settings + ".RoadCursorDistance").floatValue();
        float maxAngle = m_generationConfig.getNumber("roadgeneration.tunnelRoad.configurations." + settings + ".MaxAngle").floatValue();

        float minWidht = m_generationConfig.getNumber(
                "roadgeneration.tunnelRoad.configurations." + settings + ".MinWidht").floatValue();
        float maxWidht = m_generationConfig.getNumber(
                "roadgeneration.tunnelRoad.configurations." + settings + ".MaxWidht").floatValue();
        float minHeight = m_generationConfig.getNumber(
                "roadgeneration.tunnelRoad.configurations." + settings + ".MinHeight").floatValue();
        float maxHeight = m_generationConfig.getNumber(
                "roadgeneration.tunnelRoad.configurations." + settings + ".MaxHeight").floatValue();
        float maxDerivative = m_generationConfig.getNumber(
                "roadgeneration.tunnelRoad.configurations." + settings + ".MaxWidhtDerivative").floatValue();
        float widthRecenterPower = m_generationConfig.getNumber(
                "roadgeneration.tunnelRoad.configurations." + settings + ".WidthRecenterPower").floatValue();
        float widthSmoothness = m_generationConfig.getNumber(
                "roadgeneration.tunnelRoad.configurations." + settings + ".WidhtSmoothness").floatValue();

        ArrayList<RoadCursor> roadCursors = getConnectingPath(target, entryWidth, roadCursorDistance, maxAngle);

        if (roadCursors == null) {
            return null;
        }

        road.getRoadCursors().addAll(roadCursors);


        calculateRandomWidth(seed,road,new Vector2f(minWidht,minHeight),
                new Vector2f(maxWidht,maxHeight),maxDerivative,widthRecenterPower,widthSmoothness);

        road.updateDirections();

        return road;

    }

    private ArrayList<RoadCursor> getConnectingPath(RoadCursor target, Vector2f entryWidth, float roadCursorDistance, float maxAngle) {

        RoadCursor start = new RoadCursor(0,0,0, Vector3f.ZERO, entryWidth);


        Vector2f directionHorizontalStart = new Vector2f(start.getDirection().x, start.getDirection().z).normalize();
        Vector2f directionHorizontalTarget = new Vector2f(target.getDirection().x, target.getDirection().z).normalize();


        float angleHorizontal = directionHorizontalStart.normalize().angleBetween(directionHorizontalTarget.normalize());

        // Normalize angle to be between [-PI,PI]
        angleHorizontal = (float) ((Math.abs(angleHorizontal + 2 * Math.PI) < Math.PI)
                ? (angleHorizontal + 2 * Math.PI) : (Math.abs(angleHorizontal - 2 * Math.PI) < Math.PI)
                ? (angleHorizontal - 2 * Math.PI) : (angleHorizontal));

        float angleVertical =  new Vector2f(directionHorizontalStart.length(), start.getDirection().y)
                .angleBetween(new Vector2f(directionHorizontalTarget.length(), target.getDirection().y));

        float angleTilt = target.getTiltAngle() - start.getTiltAngle();


        Vector3f currentPosition = new Vector3f(start.getPosition());


        ArrayList<RoadCursor> roadCursorList = new ArrayList<>();

        RoadCursor roadCursor;


        int cursorCount = (int)(1 + (start.getDirection().angleBetween(target.getDirection()) / maxAngle));

        float coef;
        for (int i = 0;i <= cursorCount; i++) {
            coef = (float)i / (float)cursorCount;
            roadCursor = new RoadCursor(
                    angleHorizontal * coef,
                    angleVertical * coef,
                    angleTilt * coef,
                    currentPosition,
                    entryWidth);


            currentPosition.addLocal(roadCursor.getDirection().mult(roadCursorDistance));
            roadCursorList.add(roadCursor);

        }


        Vector3f deltaPos = new Vector3f(target.getPosition().subtract(roadCursorList.get(roadCursorList.size() - 1).getPosition()));

        float maxDot = 0f;
        int maxDotIndex = 0;


        for (int i = 1;i < roadCursorList.size() - 1;i++) {
            if (maxDot < roadCursorList.get(i).getDirection().dot(deltaPos.normalize())) {
                maxDot = roadCursorList.get(i).getDirection().dot(deltaPos.normalize());
                maxDotIndex = i;
            }
        }

        RoadCursor firstCursor = roadCursorList.get(maxDotIndex);
        RoadCursor end = new RoadCursor(roadCursorList.get(maxDotIndex));
        end.setPosition(end.getPosition().add(deltaPos));

        ArrayList<RoadCursor> connectingPath = getConnectingPathByInterpolation(
                firstCursor,
                end,
                roadCursorDistance,
                maxAngle);

        if (connectingPath == null) {
            return null;
        }


        for (int i = maxDotIndex + 1;i < roadCursorList.size();i++) {
            roadCursorList.get(i).getPosition().addLocal(deltaPos);
        }

        roadCursorList.addAll(maxDotIndex + 1, connectingPath);

        return roadCursorList;

    }





    private ArrayList<RoadCursor> getConnectingPathByInterpolation(RoadCursor src, RoadCursor target, float roadCursorDistance, float maxAngle) {

        Vector3f deltaPosition = target.getPosition().subtract(src.getPosition());

        float angleHorizontal;
        float angleVertical;

        int steps = (int) (2 * (deltaPosition.length() / roadCursorDistance) + 1);


        RoadCursor roadCursorA = new RoadCursor(src);
        RoadCursor roadCursorB = new RoadCursor(target);

        ArrayList<RoadCursor> roadCursorListA = new ArrayList<>();
        ArrayList<RoadCursor> roadCursorListB = new ArrayList<>();


        roadCursorListA.add(roadCursorA);
        roadCursorListB.add(roadCursorB);

        RoadCursor roadCursor;

        Vector2f deltaHorizontal;
        Vector2f deltaVertical;

        Vector2f directionHorizontal;
        Vector2f directionVertical;


        boolean isValid = false;


        for (int i = 0; i <= steps; i++) {

            roadCursorA = roadCursorListA.get(roadCursorListA.size() - 1);
            roadCursorB = roadCursorListB.get(roadCursorListB.size() - 1);


            deltaPosition = roadCursorB.getPosition().subtract(roadCursorA.getPosition());


            directionHorizontal = new Vector2f(roadCursorA.getDirection().x,roadCursorA.getDirection().z);
            deltaHorizontal = new Vector2f(deltaPosition.x,deltaPosition.z);

            directionVertical = new Vector2f(directionHorizontal.length(),roadCursorA.getDirection().y).normalize();
            deltaVertical  = new Vector2f(deltaHorizontal.length(),deltaPosition.y).normalize();

            angleHorizontal = deltaHorizontal.normalize().angleBetween(directionHorizontal.normalize());
            angleVertical = directionVertical.angleBetween(deltaVertical.normalize());



            roadCursor = new RoadCursor(
                    MathUtility.limitAbsValue(angleHorizontal,maxAngle) + roadCursorA.getXZAngle(),
                    MathUtility.limitAbsValue(angleVertical,maxAngle) + roadCursorA.getYAngle(),
                    0,
                    roadCursorA.getPosition().add(roadCursorA.getDirection().mult(roadCursorDistance)),
                    roadCursorA.getWidhtAndHightAndHight());

            roadCursorListA.add(roadCursor);


            roadCursorA = roadCursorListA.get(roadCursorListA.size() - 1);
            roadCursorB = roadCursorListB.get(roadCursorListB.size() - 1);


            deltaPosition = roadCursorB.getPosition().subtract(roadCursorA.getPosition());



            if (deltaPosition.length() < 3 * roadCursorDistance) {

                if (Math.abs(angleHorizontal) < 1.2f * maxAngle) {
                    isValid = true;
                }

                roadCursorA = roadCursorListA.get(roadCursorListA.size() - 2);

                roadCursor = new RoadCursor(
                        angleHorizontal + roadCursorA.getXZAngle(),
                        angleVertical + roadCursorA.getYAngle(),
                        0,
                        roadCursorA.getPosition().add(roadCursorA.getDirection().mult(roadCursorDistance)),
                        roadCursorA.getWidhtAndHightAndHight());

                roadCursorListA.set(roadCursorListA.size() - 1, roadCursor);

                break;
            }

            directionHorizontal = new Vector2f(roadCursorB.getDirection().x,roadCursorB.getDirection().z);
            deltaHorizontal = new Vector2f(deltaPosition.x,deltaPosition.z);

            directionVertical = new Vector2f(directionHorizontal.length(),roadCursorB.getDirection().y).normalize();
            deltaVertical  = new Vector2f(deltaHorizontal.length(),deltaPosition.y).normalize();

            angleHorizontal = deltaHorizontal.normalize().angleBetween(directionHorizontal.normalize());
            angleVertical = directionVertical.angleBetween(deltaVertical.normalize());

            roadCursor = new RoadCursor(
                    MathUtility.limitAbsValue(angleHorizontal,maxAngle) + roadCursorB.getXZAngle(),
                    MathUtility.limitAbsValue(angleVertical,maxAngle) + roadCursorB.getYAngle(),
                    0,
                    roadCursorB.getPosition(),
                    roadCursorB.getWidhtAndHightAndHight());
            roadCursor.setPosition(roadCursor.getPosition().subtract(roadCursor.getDirection().mult(roadCursorDistance)));

            roadCursorListB.add(roadCursor);


        }

        roadCursorListB.remove(0);

        Collections.reverse(roadCursorListB);

        roadCursorListA.remove(0);

        ArrayList<RoadCursor> roadCursorList = new ArrayList<>(roadCursorListA);
        roadCursorList.addAll(roadCursorListB);
        if (!isValid) {
            return null;
        }

        return roadCursorList;
    }




    @Override
    public Road generateCuboidRoad(int seed, Vector3f cuboidDimensions,
                                   Vector2f entryWidth, float exitAngle, String settings, Set<GeneratorSettings> flags) {
        Road road = new Road();

        RoadCursor exit = getExitCursorForCuboid(cuboidDimensions, entryWidth, exitAngle);

        float roadCursorDistance = m_generationConfig.getNumber(
                "roadgeneration.roomRoad.configurations." + settings + ".RoadCursorDistance").floatValue();
        float maxAngle = m_generationConfig.getNumber(
                "roadgeneration.roomRoad.configurations." + settings + ".MaxAngle").floatValue();
        final float minWidht = m_generationConfig.getNumber(
                "roadgeneration.roomRoad.configurations." + settings + ".MinWidht").floatValue();
        final float maxWidht = m_generationConfig.getNumber(
                "roadgeneration.roomRoad.configurations." + settings + ".MaxWidht").floatValue();
        final float minHeight = m_generationConfig.getNumber(
                "roadgeneration.roomRoad.configurations." + settings + ".MinHeight").floatValue();
        final float maxHeight = m_generationConfig.getNumber(
                "roadgeneration.roomRoad.configurations." + settings + ".MaxHeight").floatValue();
        final float maxDerivative = m_generationConfig.getNumber(
                "roadgeneration.roomRoad.configurations." + settings + ".MaxWidhtDerivative").floatValue();
        final float widthRecenterPower = m_generationConfig.getNumber(
                "roadgeneration.roomRoad.configurations." + settings + ".WidthRecenterPower").floatValue();
        final float widthSmoothness = m_generationConfig.getNumber(
                "roadgeneration.roomRoad.configurations." + settings + ".WidhtSmoothness").floatValue();



        ArrayList<RoadCursor> roadCursors = getConnectingPath(exit, entryWidth, roadCursorDistance, maxAngle);
        if (roadCursors == null) {
            return null;
        }
        road.getRoadCursors().addAll(roadCursors);


        calculateRandomWidth(
                seed,road,new Vector2f(minWidht,minHeight),
                new Vector2f(maxWidht,maxHeight),maxDerivative,widthRecenterPower,widthSmoothness);


        road.updateDirections();

        return road;


    }


    private RoadCursor getExitCursorForCuboid(Vector3f dimensions,Vector2f entryWidth, float exitAngle) {

        int exitIndex;
        if (exitAngle > Math.PI / 8f) {
            exitIndex = 1;
        } else if (exitAngle < - Math.PI / 8f) {
            exitIndex = 2;
        } else {
            exitIndex = 0;
        }

        return new RoadCursor[] {
            new RoadCursor(
                0,0,0,
                new Vector3f(0, 0, dimensions.z),
                entryWidth),
            new RoadCursor(
                -(float)Math.PI / 2f,0,0,
                new Vector3f(dimensions.x / 2, 0, dimensions.z / 2),
                entryWidth),
            new RoadCursor(
                (float)Math.PI / 2f,0,0,
                new Vector3f(-dimensions.x / 2, 0, dimensions.z / 2),
                entryWidth)}[exitIndex];

    }


    private void calculateRandomWidth(
            int seed, Road road, Vector2f minWidht, Vector2f maxWidht,
            float maxWidhtDerivative, float recenterPower, float widhtSmoothness) {

        RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(seed);

        Iterator<RoadCursor> it = road.getRoadCursorIterator();

        Vector2f deltaWidht = new Vector2f().zero();
        Vector2f deterministicDeltaWidht = new Vector2f();
        Vector2f currentWidth = road.getFirstRoadCursor().getWidhtAndHightAndHight();
        RoadCursor currentRoadCursor;

        Vector2f averageWidht = new Vector2f((maxWidht.x + minWidht.x) / 2,(maxWidht.y + minWidht.y) / 2);
        Vector2f widhtSpace = new Vector2f((maxWidht.x - minWidht.x) / 2,(maxWidht.y - minWidht.y) / 2);


        Vector2f randomComponent = new Vector2f();
        Vector2f randomVector;

        while (it.hasNext()) {
            randomVector = new Vector2f(
                    1 - 2 * randomNumberGenerator.random(),
                    1 - 2 * randomNumberGenerator.random());
            currentRoadCursor = it.next();


            currentRoadCursor.setWidhtAndHightAndHight(currentWidth.add(deltaWidht));


            if (currentRoadCursor.getWidhtAndHightAndHight().x > maxWidht.x) {
                currentRoadCursor.getWidhtAndHightAndHight().x = maxWidht.x;
            } else if (currentRoadCursor.getWidhtAndHightAndHight().x < minWidht.x) {
                currentRoadCursor.getWidhtAndHightAndHight().x = minWidht.x;
            }
            if (currentRoadCursor.getWidhtAndHightAndHight().y > maxWidht.y) {
                currentRoadCursor.getWidhtAndHightAndHight().y = maxWidht.y;
            } else if (currentRoadCursor.getWidhtAndHightAndHight().y < minWidht.y) {
                currentRoadCursor.getWidhtAndHightAndHight().y = minWidht.y;
            }



            randomComponent.x = MathUtility.limitAbsValue(randomComponent.x + widhtSmoothness * randomVector.x,1);
            randomComponent.y = MathUtility.limitAbsValue(randomComponent.y + widhtSmoothness * randomVector.y,1);
            currentWidth = currentRoadCursor.getWidhtAndHightAndHight();


            if (widhtSpace.x > 0.1 && widhtSpace.x > maxWidhtDerivative) {
                deterministicDeltaWidht.x =
                        (float) - Math.pow((Math.abs(currentWidth.x - averageWidht.x)) / widhtSpace.x,recenterPower)
                                * Math.signum(currentWidth.x - averageWidht.x);
                deltaWidht.x = (deterministicDeltaWidht.x + (1 - deterministicDeltaWidht.x) * randomComponent.x) * maxWidhtDerivative;

            } else {
                deltaWidht.x = 0;
            }

            if (widhtSpace.y > 0.1 && widhtSpace.y > maxWidhtDerivative) {
                deterministicDeltaWidht.y =
                        (float) -Math.pow((Math.abs(currentWidth.y - averageWidht.y)) / widhtSpace.y,recenterPower)
                                * Math.signum(currentWidth.y - averageWidht.y);
                deltaWidht.y = (deterministicDeltaWidht.y + (1 - deterministicDeltaWidht.y) * (randomComponent.x)) * maxWidhtDerivative;
            } else {
                deltaWidht.y = 0;
            }
        }

    }


}
