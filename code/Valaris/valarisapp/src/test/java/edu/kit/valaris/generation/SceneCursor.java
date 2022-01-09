package edu.kit.valaris.generation;

import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.Hashtable;

public class SceneCursor implements ActionListener {


    private Vector3f pos;

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public Vector3f getPos() {
        return pos;
    }

    private Camera cam;

    private Vector3f mov;

    private float unit;

    private boolean enter;

    private int blockType;

    private Hashtable<Integer,Spatial> geometry;


    public ArrayList<Geometry> geomType;

    private Node sceneRootNode;

    private Node rootNode;

    private Geometry grid;

    private boolean newMov;


    private float timePerMov;

    private int frameCnt;

    public SceneCursor(Node sceneRootNode, Node rootNode, Camera cam) {
        mov = new Vector3f(Vector3f.ZERO);
        this.cam = cam;
        unit = 1f;
        this.rootNode = rootNode;
        blockType = 0;
        pos = new Vector3f(Vector3f.ZERO);
        this.sceneRootNode = sceneRootNode;
        newMov = false;
        frameCnt = 0;
        timePerMov=0.02f;
    }


    public boolean up,down,left,right,top,bottom;

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Left")) {
            left=isPressed;
        }  if (binding.equals("Right")) {
            right=isPressed;
        }  if (binding.equals("Up")) {
            up=isPressed;
        }  if (binding.equals("Down")) {
            down=isPressed;
        }  if (binding.equals("Top")) {
            top=isPressed;
        }  if (binding.equals("Bottom")) {
            bottom=isPressed;
        }  if (binding.equals("Enter")) {
            enter = isPressed;
        }
    }



    public void update(float tpf) {

        Vector3f movLocal = new Vector3f(Vector3f.ZERO);
        if (left) {
            movLocal.addLocal(unit, 0, 0);
        }  if (right) {
            movLocal.addLocal(-unit, 0, 0);
        }  if (up) {
            movLocal.addLocal(0, 0, unit);
        }  if (down) {
            movLocal.addLocal(0, 0, -unit);
        }  if (top) {
            movLocal.addLocal(0, unit, 0);
        }  if (bottom) {
            movLocal.addLocal(0, -unit, 0);
        }


        mov = new Vector3f(cam.getDirection().x,0,cam.getDirection().z).mult(movLocal.z)
                .add(new Vector3f(cam.getLeft().x,0,cam.getLeft().z).mult(movLocal.x))
                .add(new Vector3f(0, movLocal.y, 0));

        float speed = 1000f;
        mov.multLocal(tpf*(1f/60f)*speed);
        pos.addLocal(mov);

        enter = false;
    }








}
