package edu.kit.valaris.rendering.tick;

import edu.kit.valaris.InputHandler;
import edu.kit.valaris.Metadata;
import edu.kit.valaris.RaceAppState;
import edu.kit.valaris.dummysim.SimulationDummy;
import edu.kit.valaris.menu.gui.NiftyAppState;
import edu.kit.valaris.menu.gui.PausemenuScreen;
import edu.kit.valaris.tick.TickEvent;

/**
 * {@link ITickEventProcessor} that is used to process events that are triggered once the game is paused.
 *
 * @author Frederik Lingg
 */
public class PausedEventProcessor implements ITickEventProcessor {

    /**
     * The {@link TickProcessor} using this {@link PausedEventProcessor}.
     */
    private TickProcessor m_tickProcessor;

    @Override
    public void setTickProcessor(TickProcessor processor) {
        m_tickProcessor = processor;
    }

    @Override
    public void process(TickEvent event) {
        if(event.getAction().equals(TickEvent.EVENT_GAME_PAUSED)) {
            m_tickProcessor.getAppStateManager().getState(Metadata.class).getApp().enqueue(() -> {
                //pause simulation
                if(m_tickProcessor.getAppStateManager().getState(SimulationDummy.class) != null) {
                    m_tickProcessor.getAppStateManager().getState(SimulationDummy.class).setEnabled(false);
                }

                if(m_tickProcessor.getAppStateManager().getState(RaceAppState.class) != null) {
                    m_tickProcessor.getAppStateManager().getState(RaceAppState.class).setEnabled(false);
                }

                if(m_tickProcessor.getAppStateManager().getState(InputHandler.class) != null) {
                    m_tickProcessor.getAppStateManager().getState(InputHandler.class).setEnabled(false);
                }

                //open pause menu
                PausemenuScreen pausemenuScreen = new PausemenuScreen(m_tickProcessor.getAppStateManager(),
                        m_tickProcessor.getAppStateManager().getApplication());
                m_tickProcessor.getAppStateManager().attach(new NiftyAppState("Pause", pausemenuScreen));
            });
        } else {
            throw new IllegalStateException("Wrong event was sent to PausedEventProcessor");
        }
    }
}
