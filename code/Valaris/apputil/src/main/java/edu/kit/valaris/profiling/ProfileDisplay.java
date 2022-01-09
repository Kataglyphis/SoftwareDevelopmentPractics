package edu.kit.valaris.profiling;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;

import javax.annotation.Nonnull;

/**
 * Used to display the results of a {@link ProfilingSection}.
 */
public class ProfileDisplay extends AbstractController {

    private TextRenderer m_title;
    private Element m_content;

    private Nifty m_nifty;
    private Screen m_screen;

    /**
     * Displays the given {@link ProfilingSection.ProfilingResult}.
     * @param result the {@link ProfilingSection.ProfilingResult} to display.
     */
    public void display(String name, ProfilingSection.ProfilingResult result) {
        //update title
        m_title.setText(name);

        //update children
        for(int i = 0; i < result.getEdges().length - 1; i++) {
            Element element = null;
            if(i < m_content.getChildren().size()) {
                element = m_content.getChildren().get(i);
            } else {
                //create new element and add it to the content panel
                element = new IntervalBoxBuilder("box" + i).build(m_nifty, m_screen, m_content);
            }

            IntervalBox box = element.getNiftyControl(IntervalBox.class);
            box.setIntervall(result.getNames()[i], new Color(result.getColors()[i].asIntRGBA()), result.getEdges()[i + 1] - result.getEdges()[i]);
        }

        //mark other children for removal
        while(m_content.getChildren().size() > result.getNames().length) {
            m_content.getChildren().get(m_content.getChildren().size() - 1).markForRemoval();
        }

        //trigger layout
        m_content.layoutElements();
        getElement().layoutElements();
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen, @Nonnull Element element, @Nonnull Parameters parameter) {
        super.bind(element);

        m_title = element.findElementById("#label-backdrop").findElementById("#label").getRenderer(TextRenderer.class);
        m_content = element.findElementById("#content");
    }

    @Override
    public void onStartScreen() {
        //do nothing here
    }

    @Override
    public boolean inputEvent(@Nonnull NiftyInputEvent inputEvent) {
        return false;
    }
}
