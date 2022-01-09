package edu.kit.valaris.rendering.tick;

import de.lessvoid.nifty.tools.Color;
import edu.kit.valaris.InputHandler;
import edu.kit.valaris.Metadata;
import edu.kit.valaris.RaceAppState;
import edu.kit.valaris.datastructure.ResultObject;
import edu.kit.valaris.menu.gui.EndmenuScreen;
import edu.kit.valaris.menu.gui.NiftyAppState;
import edu.kit.valaris.tick.TickEvent;
import java.util.Iterator;

/**
 * Processes {@link TickEvent}s occuring when the race is finished.
 *
 * @author Frederik Lingg
 */
public class FinishedEventProcessor implements ITickEventProcessor {

    /**
     * The {@link TickProcessor} using this {@link FinishedEventProcessor}.
     */
    private TickProcessor m_tickProcessor;

    @Override
    public void setTickProcessor(TickProcessor processor) {
        m_tickProcessor = processor;
    }

    @Override
    public void process(TickEvent event) {
        //stop app for now
        //m_tickProcessor.getAppStateManager().getState(Metadata.class).getApp().stop();

        if(event.getAction().equals(TickEvent.EVENT_GAME_FINISHED)) {
            m_tickProcessor.getAppStateManager().getState(Metadata.class).getApp().enqueue(() -> {

                if(m_tickProcessor.getAppStateManager().getState(RaceAppState.class) != null) {
                    m_tickProcessor.getAppStateManager().getState(RaceAppState.class).setEnabled(false);
                }

                if(m_tickProcessor.getAppStateManager().getState(InputHandler.class) != null) {
                    m_tickProcessor.getAppStateManager().getState(InputHandler.class).setEnabled(false);
                }
                // Opens the finish screen
                EndmenuScreen endmenuScreen = new EndmenuScreen(m_tickProcessor.getAppStateManager(),
                        m_tickProcessor.getAppStateManager().getApplication(), event.getParams());
                m_tickProcessor.getAppStateManager().attach(new NiftyAppState("Finish", endmenuScreen));

            });
        } else {
            throw new IllegalStateException("Wrong event was sent to FinishedEventProcessor");
        }
    }
}
