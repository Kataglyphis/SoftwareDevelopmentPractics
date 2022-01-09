package edu.kit.valaris.rendering.tick;

import com.jme3.scene.Node;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.rendering.tick.dynamics.AddPropertyEventProcessor;
import edu.kit.valaris.rendering.tick.dynamics.DynamicGameObjectProcessor;
import edu.kit.valaris.rendering.tick.dynamics.RemovePropertyEventProcessor;
import edu.kit.valaris.tick.DynamicGameObjectEvent;
import edu.kit.valaris.tick.TickEvent;
import edu.kit.valaris.tick.DynamicGameObject;

/**
 * Used to process Events triggered by adding a {@link DynamicGameObject}.
 *
 * @author Frederik Lingg
 */
public class AddDynamicGameObjectEventProzessor implements ITickEventProcessor {

    /**
     * The {@link TickProcessor} that uses this {@link ITickEventProcessor}.
     */
    private TickProcessor m_tickProcessor;

    @Override
    public void setTickProcessor(TickProcessor processor) {
        m_tickProcessor = processor;
    }

    @Override
    public void process(TickEvent event) {
        if (m_tickProcessor == null) {
            throw new IllegalStateException("EventProcessor is used without being set to a TickProcessor");
        }

        if (event.getAction().equals(TickEvent.EVENT_DYNAMIC_GAME_OBJECT_ADDED)) {
            AssetProvider assetProvider = m_tickProcessor.getAppStateManager().getState(AssetProvider.class);

            if (assetProvider != null) {
                //load model
                Node model = null;
                if (event.getParams().get(1) != null) {
                    model = assetProvider.provide((String) event.getParams().get(1));
                } else {
                    //just use empty node if no model is given
                    model = new Node();
                }

                //create processor
                DynamicGameObjectProcessor dgo = new DynamicGameObjectProcessor();
                dgo.setEventProcessor(DynamicGameObjectEvent.EVENT_ADD_PROPERTY, new AddPropertyEventProcessor());
                dgo.setEventProcessor(DynamicGameObjectEvent.EVENT_REMOVE_PROPERTY, new RemovePropertyEventProcessor());

                //attach processor to model and add to TickProcessor
                model.addControl(dgo);
                m_tickProcessor.addDynamiGameObjectProcessor((int) event.getParams().get(0), dgo);
            } else {
                throw new IllegalStateException("No AssetProvider found");
            }
        } else {
            throw new IllegalStateException("Event with wrong action: extpected " + TickEvent.EVENT_DYNAMIC_GAME_OBJECT_ADDED
                + "but recieved " + event.getAction());
        }
    }
}
