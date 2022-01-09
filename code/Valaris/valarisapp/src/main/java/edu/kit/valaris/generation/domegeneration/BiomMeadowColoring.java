package edu.kit.valaris.generation.domegeneration;

import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.GridVertex;
import edu.kit.valaris.generation.GridVertexProperty;
import edu.kit.valaris.generation.RandomNumberGenerator;


/**
 * Colors a landscape of type "BiomMeadow" based on its temperature and humidity.
 *
 * @author Lukas Schölch
 */
public class BiomMeadowColoring implements IColoring {

    /**
     * the humidity of a dome (between 0 and 1).
     */
    private float m_humidity;

    /**
     * the temperature of a dome (between 0 and 1).
     */
    private float m_temperature;

    private String m_paramPath;

    private GenerationConfig m_generationConfig;




    /**
     * Constructor.
     *
     * @param generationConfig Data structure that holds parameters for generation.
     * @param paramPath Indicates where in the JSON document the information about Coloring can be found.
     * @param biom Indicates where in the JSON document the information about the current biom can be found.
     */
    protected BiomMeadowColoring(int seed, GenerationConfig generationConfig, String paramPath, String biom) {
        RandomNumberGenerator numGen = new RandomNumberGenerator(seed);

        m_generationConfig = generationConfig;

        m_paramPath = paramPath + ".Coloring" + biom;

        float minTemperature = m_generationConfig.getNumber(m_paramPath + ".MinTemperature").floatValue();
        float maxTemperature = m_generationConfig.getNumber(m_paramPath + ".MaxTemperature").floatValue();
        if (minTemperature > maxTemperature) {
            throw new IllegalArgumentException("Temperature doesn't make sense!");
        }
        if (minTemperature < 0 || maxTemperature > 1) {
            throw new IllegalArgumentException("Humidity have to be between \"0\" and \"1\"");
        }

        float minHumidity = m_generationConfig.getNumber(m_paramPath + ".MinHumidity").floatValue();
        float maxHumidity = m_generationConfig.getNumber(m_paramPath + ".MaxHumidity").floatValue();
        if (minHumidity > maxHumidity) {
            throw new IllegalArgumentException("Humidity doesn't make sense!");
        }
        if (minHumidity < 0 || maxHumidity > 1) {
            throw new IllegalArgumentException("Humidity have to be between \"0\" and \"1\"");
        }

        m_temperature = minTemperature + numGen.random() * (maxTemperature - minTemperature);
        m_humidity = minHumidity + numGen.random() * (maxHumidity - minHumidity);
    }

    @Override
    public float[] makeColor(GridVertex[] vertices) {

        float[] roadUndergroundExtremColor;

        float[] roadUndergroundExtremColorH;

        float[] roadUndergroundExtremColorT;

        float[] roadUndergroundExtremColorHT;


        float[] rockExtremColor;

        float[] rockExtremColorH;

        float[] rockExtremColorT;

        float[] rockExtremColorHT;


        float[] meadowExtremColor;

        float[] meadowExtremColorH;

        float[] meadowExtremColorT;

        float[] meadowExtremColorHT;



        float[] snowExtremColor;

        float[] snowExtremColorH;

        float[] snowExtremColorT;

        float[] snowExtremColorHT;

        roadUndergroundExtremColor = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".RoadUndergroundExtremColors"));
        roadUndergroundExtremColorH = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".RoadUndergroundExtremColorsH"));
        roadUndergroundExtremColorT = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".RoadUndergroundExtremColorsT"));
        roadUndergroundExtremColorHT = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".RoadUndergroundExtremColorsHT"));

        rockExtremColor = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".RockExtremColor"));
        rockExtremColorH = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".RockExtremColorH"));
        rockExtremColorT = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".RockExtremColorT"));
        rockExtremColorHT = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".RockExtremColorHT"));

        meadowExtremColor = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".MeadowExtremColor"));
        meadowExtremColorH = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".MeadowExtremColorH"));
        meadowExtremColorT = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".MeadowExtremColorT"));
        meadowExtremColorHT = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".MeadowExtremColorHT"));

        snowExtremColor = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".SnowExtremColor"));
        snowExtremColorH = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".SnowExtremColorH"));
        snowExtremColorT = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".SnowExtremColorT"));
        snowExtremColorHT = this.rgbaToFloat(this.m_generationConfig.getIntArray(this.m_paramPath + ".SnowExtremColorHT"));

        float[] colorArray = new float[4 * vertices.length];
        int colorIndex = 0;

        for (int i = 0; i < vertices.length; i += 3) {
            int[] cnts = this.checkTriangleForUndergrounds(vertices[i], vertices[i + 1], vertices[i + 2]);

            int cntRocks = cnts[0];
            int cntRoads = cnts[1];
            int cntMeadows = cnts[2];
            int cntSnow = cnts[3];


            float[] colorsForVertex = new float[4];

            if (cntRoads >= 3) {

                for (int j = 0; j < colorsForVertex.length; j++) {
                    colorsForVertex[j] = (1 - m_temperature) * ((1 - m_humidity) * roadUndergroundExtremColor[j] + m_humidity * roadUndergroundExtremColorH[j])
                            + (m_temperature) * ((1 - m_humidity) * roadUndergroundExtremColorT[j] + m_humidity * roadUndergroundExtremColorHT[j]);
                }
            } else if (cntSnow >= 3) {

                for (int j = 0; j < colorsForVertex.length; j++) {
                    colorsForVertex[j] = (1 - m_temperature) * ((1 - m_humidity) * snowExtremColor[j] + m_humidity * snowExtremColorH[j])
                            + (m_temperature) * ((1 - m_humidity) * snowExtremColorT[j] + m_humidity * snowExtremColorHT[j]);
                }

            } else if (cntMeadows > 0) {

                for (int j = 0; j < colorsForVertex.length; j++) {
                    colorsForVertex[j] = (1 - m_temperature) * ((1 - m_humidity) * meadowExtremColor[j] + m_humidity * meadowExtremColorH[j])
                            + (m_temperature) * ((1 - m_humidity) * meadowExtremColorT[j] + m_humidity * meadowExtremColorHT[j]);
                }
            } else if (cntRocks > 0) {

                for (int j = 0; j < colorsForVertex.length; j++) {
                    colorsForVertex[j] = (1 - m_temperature) * ((1 - m_humidity) * rockExtremColor[j] + m_humidity * rockExtremColorH[j])
                            + (m_temperature) * ((1 - m_humidity) * rockExtremColorT[j] + m_humidity * rockExtremColorHT[j]);

                }
            }

            for (int j = 0; j < 3; j++) {
                colorArray[colorIndex++] = colorsForVertex[0];
                colorArray[colorIndex++] = colorsForVertex[1];
                colorArray[colorIndex++] = colorsForVertex[2];
                colorArray[colorIndex++] = colorsForVertex[3];
            }
        }
        return colorArray;
    }

    @Override
    public float[] makeGloss(GridVertex[] vertices) {

        float[] roughness = new float[vertices.length];
        for (int i = 0; i < roughness.length; i += 3) {
            int[] cnts = this.checkTriangleForUndergrounds(vertices[i], vertices[i + 1], vertices[i + 2]);

            int cntRocks = cnts[0];
            int cntRoads = cnts[1];
            int cntMeadows = cnts[2];
            int cntSnow = cnts[3];

            for (int j = 0; j < 3; j++) {
                if (cntRoads >= 3) {
                    roughness[i + j] = 1 - m_humidity * 0.2f;
                } else if (cntSnow >= 3) {
                    roughness[i + j] = 1 - m_humidity * 0.4f;
                } else if (cntMeadows > 0) {
                    roughness[i + j] = 1.12f - m_humidity;
                } else if (cntRocks > 0) {
                    roughness[i + j] = 1 - m_humidity * 0.2f;
                }
            }
        }
        return roughness;
    }

    @Override
    public float[] makeSpecular(GridVertex[] vertices) {
        float[] specular = new float[vertices.length];
        for (int i = 0; i < specular.length; i++) {
            specular[i] = 0.5f;
        }
        return specular;
    }

    @Override
    public float[] makeTreeColor(int seed) {
        RandomNumberGenerator numberGen = new RandomNumberGenerator(seed);
        float factor = numberGen.random();

        float[] minColors = this.rgbaToFloat(m_generationConfig.getIntArray(m_paramPath + ".MinTreeLeavesColor"));
        float[] maxColors = this.rgbaToFloat(m_generationConfig.getIntArray(m_paramPath + ".MaxTreeLeavesColor"));


        float[] treeColor = new float[4];

        for (int j = 0; j < treeColor.length; j++) {
            treeColor[j] = (1 - factor) * minColors[j] + factor * maxColors[j];
        }
        return treeColor;
    }

    /**
     * Counts number of GridVertexProperties for a Triangle.
     *
     * @param vertex1 First triangleVertex.
     * @param vertex2 Second triangleVertex.
     * @param vertex3 Third triangleVertex.
     * @return {cntRocks, cntRoads, cntMeadows, cntSnow}
     */
    private int[] checkTriangleForUndergrounds (GridVertex vertex1, GridVertex vertex2, GridVertex vertex3) {
        GridVertex[] vertices = {vertex1, vertex2, vertex3};
        int cntRocks = 0;
        int cntRoads = 0;
        int cntMeadows = 0;
        int cntSnow = 0;

        for (int j = 0; j < 3; j++) {
            if (vertices[j].hasProperty(GridVertexProperty.road)) {
                cntRoads++;
            } else if (vertices[j].hasProperty(GridVertexProperty.meadow)) {
                cntMeadows++;

                if (vertices[j].hasProperty(GridVertexProperty.snow)) {
                    cntSnow++;
                }

            } else if (vertices[j].hasProperty(GridVertexProperty.rock)) {
                cntRocks++;
            }

        }
        int[] cnts = {cntRocks, cntRoads, cntMeadows, cntSnow};
        return cnts;
    }

    private float[] rgbaToFloat (int[] rgba) {
        float[] color = new float[4];

        color[0] = (float) rgba[0] / 255f;
        color[1] = (float) rgba[1] / 255f;
        color[2] = (float) rgba[2] / 255f;
        color[3] = (float) rgba[3] / 255f;

        return color;
    }
}
