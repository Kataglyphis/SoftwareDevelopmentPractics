package edu.kit.valaris.profiling;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;

import javax.annotation.Nonnull;

/**
 * Displays a single interval as a box with a label
 */
public class IntervalBox extends AbstractController {

    /**
     * The label displaying the name of the intervall
     */
    private TextRenderer m_label;

    /**
     * The box used to display the color of the intervall
     */
    private Element m_box;

    public void setIntervall(String name, Color color, float width) {
        getElement().setWidth((int) ((float) getElement().getParent().getWidth() * width));
        m_box.getRenderer(PanelRenderer.class).setBackgroundColor(color);
        m_label.setText(name);
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen, @Nonnull Element element, @Nonnull Parameters parameter) {
        super.bind(element);

        m_box = element.findElementById("#box");
        m_label = element.findElementById("#label-backdrop").findElementById("#label").getRenderer(TextRenderer.class);
    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public boolean inputEvent(@Nonnull NiftyInputEvent inputEvent) {
        return false;
    }
}
