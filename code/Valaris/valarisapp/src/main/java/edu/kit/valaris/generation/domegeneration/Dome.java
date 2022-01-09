package edu.kit.valaris.generation.domegeneration;


import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import edu.kit.valaris.generation.GridVertex;
import edu.kit.valaris.generation.GridVertexProperty;
import edu.kit.valaris.generation.roadgeneration.Road;
import edu.kit.valaris.generation.triangulation.DelaunayTriangulator;
import edu.kit.valaris.generation.triangulation.NotEnoughPointsException;
import edu.kit.valaris.generation.triangulation.Triangle2D;
import edu.kit.valaris.generation.triangulation.Vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements IDome and is a data structure that represents a dome in the map.
 *
 * @author Lukas Schölch
 */
public class Dome implements IDome {

    private GridVertex[][] m_grid;

    private float m_landscapeResolution;

    private Road m_road;

    private Node m_domeAssetRootNode;

    private IColoring m_coloring;

    private Material m_material;


    /**
     * Constructor.
     * @param grid A grid that represents the underground in the dome.
     * @param road Represents the road that passes through the dome.
     * @param domeAssetRootNode Contains all objects in the dome.
     */
    protected Dome(GridVertex[][] grid, float landscapeResolution, Road road, Node domeAssetRootNode, Material material, IColoring coloring) {
        this.m_grid = grid;
        this.m_landscapeResolution = landscapeResolution;
        this.m_road = road;
        this.m_domeAssetRootNode = domeAssetRootNode;
        this.m_material = material;
        this.m_coloring = coloring;
    }


    @Override
    public Node generateSceneGraph() {
        Node domeRootNode = new Node("domeRootNot");
        Node gridRootNode = new Node("gridRootNode");

        List<Vector2D> triangulationGrid = new ArrayList<>();
        ArrayList<Vector3f> verts = new ArrayList<>();


        for (int i = 1; i < m_grid.length - 2; i++) {
            for (int j = 1; j < m_grid[0].length - 2; j++) {
                if (m_grid[i + 1][j] != null && m_grid[i + 1][j + 1] != null && m_grid[i][j] != null && m_grid[i][j + 1] != null) {

                    //determine the gridVertices needed for the triangulator
                    if (this.isGridVertexNeeded(i, j, m_landscapeResolution)) {
                        triangulationGrid.add(new Vector2D(i, j));
                    }
                }
            }
        }

        DelaunayTriangulator triangulator = new DelaunayTriangulator(triangulationGrid);
        try {
            triangulator.triangulate();
        } catch (NotEnoughPointsException ex) {
            ex.printStackTrace();
        }

        List<Triangle2D> triangles = triangulator.getTriangles();

        for (Triangle2D tri: triangles) {
            verts.add(m_grid[(int) tri.a.x][(int) tri.a.y].getPosition());
            verts.add(m_grid[(int) tri.b.x][(int) tri.b.y].getPosition());
            verts.add(m_grid[(int) tri.c.x][(int) tri.c.y].getPosition());
        }

        Vector3f[] vertices = new Vector3f[verts.size()];
        Vector3f[] normals = new Vector3f[verts.size()];
        GridVertex[] gridVertices = new GridVertex[vertices.length];


        vertices = verts.toArray(vertices);

        for (int i = 0; i < vertices.length; i += 3) {
            //for coloring
            gridVertices[i] = m_grid[(int) vertices[i].x + m_grid.length / 2][(int) vertices[i].z];
            gridVertices[i + 1] = m_grid[(int) vertices[i + 1].x + m_grid.length / 2][(int) vertices[i + 1].z];
            gridVertices[i + 2] = m_grid[(int) vertices[i + 2].x + m_grid.length / 2][(int) vertices[i + 2].z];

            //set normals in right direction
            Vector3f normal = vertices[i].subtract(vertices[i + 1]).cross(vertices[i + 1].subtract(vertices[i + 2])).normalize();
            if (normal.dot(Vector3f.UNIT_Y) < 0) {
                normal.negateLocal();
                Vector3f puffVert = vertices[i];
                vertices[i] = vertices[i + 2];
                vertices[i + 2] = puffVert;
            }
            normals[i] = normal;
            normals[i + 1] = normal;
            normals[i + 2] = normal;
        }

        float[] roughness = m_coloring.makeGloss(gridVertices);
        float[] colorArray = m_coloring.makeColor(gridVertices);
        float[] specular = m_coloring.makeSpecular(gridVertices);


        Mesh mesh = new Mesh();

        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        mesh.setBuffer(VertexBuffer.Type.Color, 4, BufferUtils.createFloatBuffer(colorArray));
        mesh.setBuffer(VertexBuffer.Type.TexCoord3, 1, BufferUtils.createFloatBuffer(roughness));
        mesh.setBuffer(VertexBuffer.Type.TexCoord4, 1, BufferUtils.createFloatBuffer(specular));
        mesh.updateBound();


        Geometry geom = new Geometry("domeGround", mesh);
        geom.setMaterial(m_material);
        geom.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        gridRootNode.attachChild(geom);



        domeRootNode.attachChild(gridRootNode);
        domeRootNode.attachChild(m_domeAssetRootNode);

        domeRootNode.setLocalRotation(this.m_road.getFirstRoadCursor().getRotation());
        domeRootNode.setLocalTranslation(this.m_road.getFirstRoadCursor().getPosition());

        return domeRootNode;
    }

    @Override
    public Vector3f[] getEntryFrame() {
        return m_road.getFirstRoadCursor().getRoadFrame();
    }

    @Override
    public Vector3f[] getExitFrame() {
        return m_road.getLastRoadCursor().getRoadFrame();
    }

    @Override
    public Road getRoad() {
        return this.m_road;
    }

    private boolean isGridVertexNeeded(int x, int z, float maxSecondDerivationDeltaForLandscapeResolution) {

        float maxSecondDerivationDelta = maxSecondDerivationDeltaForLandscapeResolution;

        if (m_grid[x][z].hasProperty(GridVertexProperty.road)
                || m_grid[x - 1][z] == null || m_grid[x][z - 1] == null || m_grid[x + 1][z - 1] == null
                || m_grid[x - 1][z + 1] == null || m_grid[x - 1][z - 1] == null) {
            return true;
        }

        float pitch1 = Math.abs(m_grid[x][z].getPosition().y - m_grid[x + 1][z].getPosition().y);
        float pitch2 = Math.abs(m_grid[x][z].getPosition().y - m_grid[x - 1][z].getPosition().y);

        if (Math.abs(pitch1 - pitch2) >= maxSecondDerivationDelta) {
            return true;
        }

        pitch1 = Math.abs(m_grid[x][z].getPosition().y - m_grid[x][z + 1].getPosition().y);
        pitch2 = Math.abs(m_grid[x][z].getPosition().y - m_grid[x][z - 1].getPosition().y);

        if (Math.abs(pitch1 - pitch2) >= maxSecondDerivationDelta) {
            return true;
        }

        pitch1 = Math.abs(m_grid[x][z].getPosition().y - m_grid[x + 1][z + 1].getPosition().y);
        pitch2 = Math.abs(m_grid[x][z].getPosition().y - m_grid[x - 1][z - 1].getPosition().y);

        if (Math.abs(pitch1 - pitch2) >= maxSecondDerivationDelta) {
            return true;
        }

        pitch1 = Math.abs(m_grid[x][z].getPosition().y - m_grid[x - 1][z + 1].getPosition().y);
        pitch2 = Math.abs(m_grid[x][z].getPosition().y - m_grid[x + 1][z - 1].getPosition().y);

        if (Math.abs(pitch1 - pitch2) >= maxSecondDerivationDelta) {
            return true;
        }

        return false;
    }

}
