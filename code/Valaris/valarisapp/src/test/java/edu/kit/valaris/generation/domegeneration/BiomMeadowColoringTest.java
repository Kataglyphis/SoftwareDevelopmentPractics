package edu.kit.valaris.generation.domegeneration;

import com.jme3.math.Vector3f;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.GridVertex;
import edu.kit.valaris.generation.GridVertexProperty;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Lukas Schölch
 */
public class BiomMeadowColoringTest {

    private GridVertex[] gridVertices = new GridVertex[6];
    private BiomMeadowColoring col;

    @Before
    public void before() {
        gridVertices[0] = new GridVertex(new Vector3f(0,0,0), 0,0,0);
        gridVertices[1] = new GridVertex(new Vector3f(1,0,0), 1,0,0);
        gridVertices[2] = new GridVertex(new Vector3f(0,0,1), 0,0,1);

        gridVertices[3] = new GridVertex(new Vector3f(0,0,0), 0,0,0);
        gridVertices[4] = new GridVertex(new Vector3f(-1,0,0), -1,0,0);
        gridVertices[5] = new GridVertex(new Vector3f(0,0,-1), 0,0,-1);

        col = new BiomMeadowColoring(0, new GenerationConfig(), "domegeneration", ".TestBiom");
    }

    @Test
    public void makeColorRoad() {

        float[] rightColors = {0.1f, 0.1f, 0.1f, 1f, 0.1f, 0.1f, 0.1f, 1f, 0.1f, 0.1f, 0.1f, 1f, 0.1f, 0.1f, 0.1f, 1f,
                0.1f, 0.1f, 0.1f, 1f, 0.1f, 0.1f, 0.1f, 1f,};

        for (int i = 0; i < gridVertices.length; i++) {
            gridVertices[i].setProperty(GridVertexProperty.road);
        }

        float[] colorArray = col.makeColor(gridVertices);

        assertArrayEquals(rightColors, colorArray, 0.01f);
    }

    @Test
    public void makeColorRock() {

        float[] rightColors = {0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f,};

        for (int i = 0; i < gridVertices.length; i++) {
            gridVertices[i].setProperty(GridVertexProperty.rock);
        }

        float[] colorArray = col.makeColor(gridVertices);

        assertArrayEquals(rightColors, colorArray, 0);
    }

    @Test
    public void makeColorSnow() {

        float[] rightColors = {1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,};

        for (int i = 0; i < gridVertices.length; i++) {
            gridVertices[i].setProperty(GridVertexProperty.meadow);
            gridVertices[i].setProperty(GridVertexProperty.snow);
        }

        float[] colorArray = col.makeColor(gridVertices);

        assertArrayEquals(rightColors, colorArray, 0);
    }

    @Test
    public void makeColorSnowOverMeadow() {

        float[] rightColors = {1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
                0.425f, 0.425f, 0.4f, 1f, 0.425f, 0.425f, 0.4f, 1f, 0.425f, 0.425f, 0.4f, 1f};

        for (int i = 0; i < gridVertices.length; i++) {
            gridVertices[i].setProperty(GridVertexProperty.meadow);
            if (i == 0 || i == 1 || i == 2 || i == 5) {
                gridVertices[i].setProperty(GridVertexProperty.snow);
            }
        }

        float[] colorArray = col.makeColor(gridVertices);

        assertArrayEquals(rightColors, colorArray, 0.001f);
    }

    @Test
    public void makeColorMeadow() {

        float[] rightColors = {0.425f, 0.425f, 0.4f, 1f, 0.425f, 0.425f, 0.4f, 1f, 0.425f, 0.425f, 0.4f, 1f, 0.425f, 0.425f, 0.4f, 1f,
                0.425f, 0.425f, 0.4f, 1f, 0.425f, 0.425f, 0.4f, 1f,};

        for (int i = 0; i < gridVertices.length; i++) {
            gridVertices[i].setProperty(GridVertexProperty.meadow);
        }

        float[] colorArray = col.makeColor(gridVertices);

        assertArrayEquals(rightColors, colorArray, 0.001f);
    }

    @Test
    public void makeColorMeadowOverRock() {

        float[] rightColors = {0.425f, 0.425f, 0.4f, 1f, 0.425f, 0.425f, 0.4f, 1f, 0.425f, 0.425f, 0.4f, 1f, 0.425f, 0.425f, 0.4f, 1f,
                0.425f, 0.425f, 0.4f, 1f, 0.425f, 0.425f, 0.4f, 1f,};

        gridVertices[0].setProperty(GridVertexProperty.rock);
        gridVertices[1].setProperty(GridVertexProperty.meadow);
        gridVertices[2].setProperty(GridVertexProperty.rock);
        gridVertices[3].setProperty(GridVertexProperty.road);
        gridVertices[4].setProperty(GridVertexProperty.rock);
        gridVertices[5].setProperty(GridVertexProperty.meadow);

        float[] colorArray = col.makeColor(gridVertices);

        assertArrayEquals(rightColors, colorArray, 0.001f);
    }
}