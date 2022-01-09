package edu.kit.valaris.menu.gui;

import edu.kit.valaris.datastructure.IMapBody;

/**
 * Used to pass the progress of the loading process.
 *
 * @author Frederik Lingg
 */
public interface LoadingCallback {

    /**
     * Sets the progress made loading the assets.
     * @param progress the progress.
     * @param message the message to display.
     */
    public void setProgress(double progress, String message);

    /**
     * Called when the loading process is finished.
     */
    public void finished(IMapBody body);
}
