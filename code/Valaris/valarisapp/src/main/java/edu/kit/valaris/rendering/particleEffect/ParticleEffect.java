package edu.kit.valaris.rendering.particleEffect;

import com.jme3.asset.AssetManager;
import com.jme3.math.Transform;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import edu.kit.valaris.assets.AbstractAssetControl;
import edu.kit.valaris.threading.JobManager;


/**
 * actually in charge for emitting and vanishing on/off again the particles of
 * the screen; is influenced by different strategies of particle effect.
 * {@code myGeometry.addControl(new ParticleEffect("strategyName", assetManager))}
 * on a spatial in the scene graph where you want to play it;
 * implementations {@link FireParticleStrategy}
 *                 {@link JetParticleStrategy}
 *                 {@link ExplosionParticleStrategy}
 *                 {@link GravityTrap}
 * @author Jonas Heinle, Frederik Lingg
 * @verison 1.2
 */
public class ParticleEffect extends AbstractAssetControl {

    private ParticleStrategy strategy;

    private ParticleSimulator m_simulator;
    private Mesh m_mesh;

    private AssetManager assetManager;

    /**
     * basic constructor for our particle effect
     * do not use
     */
    public ParticleEffect() {

    }

    /**
     * particle effect build out of his name and a given strategy
     * @param strategy name of this effect
     * @throws IllegalArgumentException if no valid strategy name was passed
     */

    public ParticleEffect(String strategy, AssetManager assetManager) throws IllegalArgumentException {
        switch(strategy) {
            case("Explosion"): this.strategy = new ExplosionParticleStrategy();
                               break;
            case("Fire"): this.strategy = new FireParticleStrategy();
                               break;
            case("GravityTrap"): this.strategy = new GravityTrap();
                               break;
            case("Jet"): this.strategy = new JetParticleStrategy();
                               break;
            case("Shield"): this.strategy = new ShieldParticleStrategy();
                               break;
            case("Fredsplosion"): this.strategy = new FredsplosionStrategy(100, 4);
                                break;
            default: throw  new IllegalArgumentException("No supported strategy as argument");
        }

        m_simulator = new ParticleSimulator(this.strategy);

        this.assetManager = assetManager;
    }

    public int numberOfParticles() {
        return strategy.getNumParticles();
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        m_simulator.fetchBuffers(m_mesh);
    }

    @Override
    public void setSpatial(Spatial spatial) throws IllegalArgumentException {
        super.setSpatial(spatial);

        if(spatial instanceof Geometry) {
            //this is now spatial, so it is also a Geometry.
            m_mesh = new Mesh();
            m_simulator.prepareMesh(m_mesh);
            ((Geometry) spatial).setMesh(m_mesh);

            //add job
            JobManager.getInstance().addJob(m_simulator);

            this.getSpatial().setMaterial(this.strategy.getMaterial(this.assetManager));

            getSpatial().setBatchHint(Spatial.BatchHint.Never);
            // ignore world transform, unless user sets inLocalSpace
            ((Geometry)spatial).setIgnoreTransform(strategy.isGlobal());

            // particles neither receive nor cast shadows
            spatial.setShadowMode(RenderQueue.ShadowMode.Off);
        } else {
            throw new IllegalArgumentException("Particle Effects can only be added to geometries");
        }
    }

    /**
     * get the used strategy
     * @return the given strategy
     */
    public ParticleStrategy getStrategy(){
        return this.strategy;
    }

    @Override
    public void cleanup() {
        m_simulator.cleanup();
    }
}
