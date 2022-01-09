package edu.kit.valaris.rendering.tick;

import edu.kit.valaris.Metadata;
import edu.kit.valaris.menu.gui.LoadingUtil;
import edu.kit.valaris.menu.gui.MainmenuScreen;
import edu.kit.valaris.menu.gui.SinglePlayerModeScreen;
import edu.kit.valaris.menu.menuconfig.SeedConfig;
import edu.kit.valaris.tick.TickEvent;

public class ExceptionEventProcessor implements ITickEventProcessor {

    private TickProcessor m_tickProcessor;

    @Override
    public void setTickProcessor(TickProcessor processor) {
        m_tickProcessor = processor;
    }

    @Override
    public void process(TickEvent event) {
        System.out.println("Current seed name: " + SeedConfig.getCurrentSeed().getName()
                + "\nCurrent seed number: " + SeedConfig.getCurrentSeed().getNumber()
                + "\nCurrent seed road type: " + SeedConfig.getCurrentSeed().getRoadType());
        Metadata meta = m_tickProcessor.getAppStateManager().getState(Metadata.class);

        meta.getApp().enqueue(() -> {
            MainmenuScreen mainmenuScreen = new MainmenuScreen(m_tickProcessor.getAppStateManager(), meta.getApp());
            SinglePlayerModeScreen single = new SinglePlayerModeScreen(m_tickProcessor.getAppStateManager(), meta.getApp());

            LoadingUtil lu = new LoadingUtil(meta.getApp(), m_tickProcessor.getAppStateManager());
            lu.shutDownAndSwitchScreen("MainnSingle", mainmenuScreen, single);

            //get error and display it
            Exception e = (Exception) event.getParams().get(0);
            mainmenuScreen.createPopupMessage(e.getMessage());
        });
    }
}
