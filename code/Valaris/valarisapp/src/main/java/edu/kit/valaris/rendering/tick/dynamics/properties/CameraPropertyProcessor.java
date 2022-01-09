package edu.kit.valaris.rendering.tick.dynamics.properties;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import edu.kit.valaris.config.Config;
import edu.kit.valaris.config.ConfigKey;
import edu.kit.valaris.menu.gui.Hud;
import edu.kit.valaris.rendering.SceneManager;
import edu.kit.valaris.rendering.tick.dynamics.DynamicGameObjectProcessor;
import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.PropertyIndex;
import edu.kit.valaris.tick.properties.*;
import edu.kit.valaris.util.VehicleSniffer;

/**
 * {@link IPropertyProcessor} that processes a {@link CameraProperty}.
 *
 * @author Frederik Lingg
 */
public class CameraPropertyProcessor implements IPropertyProcessor {

    /**
     * The {@link Node} this {@link CameraPropertyProcessor} works on.
     */
    private Node m_target;

    /**
     * A node containing a camera.
     */
    private CameraNode m_camNode;

    private Quaternion m_rotation;

    private Vector3f m_front;
    private Vector3f m_left;
    private Vector3f m_up;
    private Vector3f m_pos;

    /**
     * Creates a new {@link CameraPropertyProcessor} working in the given target.
     * @param target the {@link Node} this {@link CameraPropertyProcessor} works on.
     */
    public CameraPropertyProcessor(Node target) {
        m_target = target;
        m_front = new Vector3f();
        m_left = new Vector3f();
        m_pos = new Vector3f();
        m_up = new Vector3f();

        m_rotation = new Quaternion();
    }

    @Override
    public void process(IProperty property, DynamicGameObject dgo, DynamicGameObjectProcessor dgoProcessor) {
        if (property instanceof CameraProperty) {
            //update camera

            if(((CameraProperty) property).getViewPort() >= 0) {
                SceneManager vps = dgoProcessor.getTickProcessor().getAppStateManager().getState(SceneManager.class);
                Camera cam = vps.getViewPort(((CameraProperty) property).getViewPort()).getCamera();

                //other team used x as front/back axis ?!
                m_front.set(((CameraProperty) property).getDirection().z, ((CameraProperty) property).getDirection().y,
                        -((CameraProperty) property).getDirection().x);
                m_left.set(((CameraProperty) property).getLeft().z, ((CameraProperty) property).getLeft().y,
                        -((CameraProperty) property).getLeft().x);

                m_front.normalizeLocal();
                m_left.normalizeLocal();

                m_front.cross(m_left, m_up).normalizeLocal();
                m_rotation.fromAxes(m_left, m_up, m_front);

                //add cam to scenegraph
                if (m_camNode == null) {
                    m_camNode = new CameraNode("Camera-0", cam);
                    m_target.attachChild(m_camNode);
                }

                m_pos.set(((CameraProperty) property).getPosition().z, ((CameraProperty) property).getPosition().y,
                        -((CameraProperty) property).getPosition().x);
                m_camNode.setLocalTranslation(m_pos);
                m_camNode.setLocalRotation(m_rotation);

                Hud hud = vps.getHud(((CameraProperty) property).getViewPort());
                RankingProperty rankingProperty =
                        ((RankingProperty) dgo.getProperty(PropertyIndex.RANKING_PROPERTY.getIndex()));
                if(rankingProperty != null) {
                    hud.updateRank(rankingProperty.getRanking());
                }
                ItemProperty itemProperty =
                        (ItemProperty) dgo.getProperty(PropertyIndex.ITEM_PROPERTY.getIndex());
                if (itemProperty != null) {
                    if (itemProperty.getItemType() != null) {
                        hud.updateItem(itemProperty.getItemType().name());
                    }
                } else {
                    hud.updateItem("No item");
                }

                if(VehicleSniffer.getGameDummy() != null) {
                    GameProperty gameProperty =
                            (GameProperty) VehicleSniffer.getGameDummy().getProperty(PropertyIndex.GAME_PROPERTY.getIndex());
                    if(gameProperty != null) {
                        if (gameProperty.getIsCountdownRunning()){
                            int time = gameProperty.getCountdownTime() - 1;
                            if (time > 0) {
                                hud.updateCountdown(String.valueOf(time));
                            } else {
                                hud.updateCountdown("GO!!");
                            }
                        } else {
                            if (!hud.isCountdownHidden()) {
                                hud.hideCountdown();
                            }
                            if(gameProperty.getCountdownTime() <= 0) {
                                hud.updateTime(gameProperty.getTime());
                            }
                        }
                    }
                }
            }
        } else {
            throw new IllegalStateException("Wrong PropertyType for CameraPropertyProcessor");
        }
    }
}
