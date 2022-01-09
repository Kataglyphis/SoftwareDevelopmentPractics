package edu.kit.valaris.menu.controls.progressbar;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.layout.align.HorizontalAlign;
import de.lessvoid.nifty.layout.align.VerticalAlign;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.spi.render.RenderFont;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import edu.kit.valaris.menu.controls.ProgressBar;

import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Controll Class for the progress bar.
 * @author Artur Wesner
 */
public class ProgressBarControl extends AbstractController implements ProgressBar {
    /**
     * The Logger.
     */
    @Nonnull
    private static final Logger log = Logger.getLogger(ProgressBarControl.class.getName());

    /**
     * The Nitfy object.
     */
    @Nullable
    private Nifty m_nifty;

    /**
     * The button's text element.
     */
    @Nullable
    private Element m_buttonTextElement;

    /**
     * The button's text renderer.
     */
    @Nullable
    private TextRenderer m_buttonTextRenderer;

    /**
     * The progress bar's indicator element.
     */
    @Nullable
    private Element m_progressIndicatorElement;

    /**
     * Binds the class to the progressbar.
     * @param niftyParam The m_nifty object to bind.
     * @param screenParam the screen to bind.
     * @param newElement The new element to bind.
     * @param parameter A parameter which is not needed.
     */
    @Override
    public void bind(
            @Nonnull final Nifty niftyParam,
            @Nonnull final Screen screenParam,
            @Nonnull final Element newElement,
            @Nonnull final Parameters parameter) {
        super.bind(newElement);
        m_nifty = niftyParam;

        final Element text = newElement.findElementById("#text");
        if (text == null) {
            log.severe("ProgressBar element misses the text content element.");
            return;
        }
        m_buttonTextElement = text;

        if (newElement.getId() == null) {
            log.warning("ProgressBar element has no ID and can't publish any events properly.");
        }

        final TextRenderer renderer = m_buttonTextElement.getRenderer(TextRenderer.class);
        if (renderer == null) {
            throw new RuntimeException("ProgressBarControl is corrupted, #text element found, but missing TextRenderer");
        }
        m_buttonTextRenderer = renderer;

        final Element indicator = newElement.findElementById("#indicator");
        if (indicator == null) {
            log.severe("ProgressBar element misses the progress content element.");
            return;
        }
        m_progressIndicatorElement = indicator;
    }

    /**
     * Gets called when the screen is started.
     */
    @Override
    public void onStartScreen() {
    }

    /**
     * Reacts to the input event.
     * @param niftyInputEvent The nifty input event.
     * @return false.
     */
    @Override
    public boolean inputEvent(@Nonnull NiftyInputEvent niftyInputEvent) {
        return false;
    }

    /**
     * Gets the button's text.
     * @return The text.
     */
    @Nonnull
    @Override
    public String getText() {
        if (m_buttonTextRenderer == null) {
            return "";
        } else {
            return m_buttonTextRenderer.getOriginalText();
        }
    }

    /**
     * Sets the buttons text to the given parameter.
     * @param text new text to show
     */
    @Override
    public void setText(@Nonnull final String text) {
        if (m_buttonTextRenderer != null && m_buttonTextElement != null) {
            m_buttonTextRenderer.setText(text);
            if (!m_buttonTextRenderer.isLineWrapping()) {
                //m_buttonTextElement.setConstraintWidth(SizeValue.px(getTextWidth()));
            }
        } else {
            if (!isBound()) {
                throw new IllegalStateException("Setting the text is not possible before the binding is done.");
            }
            log.warning("Failed to apply the text because the required references are not set. Maybe the"
                    + " element is not bound yet?");
        }
    }

    /**
     * Gets the text width.
     * @return The text width.
     */
    @Override
    public int getTextWidth() {
        return m_buttonTextRenderer != null ? m_buttonTextRenderer.getTextWidth() : 0;
    }

    /**
     * Gets the text height.
     * @return The text height.
     */
    @Override
    public int getTextHeight() {
        return m_buttonTextRenderer != null ? m_buttonTextRenderer.getTextHeight() : 0;
    }

    /**
     * Gets the text font.
     * @return The font.
     */
    @Nullable
    @Override
    public RenderFont getFont() {
        return m_buttonTextRenderer != null ? m_buttonTextRenderer.getFont() : null;
    }

    /**
     * Sets the text font.
     * @param fontParam new font or {@code null} to use the default font
     */
    @Override
    public void setFont(@Nullable final RenderFont fontParam) {
        if (m_buttonTextRenderer != null) {
            m_buttonTextRenderer.setFont(fontParam);
        } else {
            if (!isBound()) {
                throw new IllegalStateException("Setting the font is not possible before the binding is done.");
            }
            log.warning("Failed to set the font of the renderer. Maybe the element is not bound yet?");
        }
    }

    /**
     * Gets the text vertical alignment.
     * @return The vertical alignment.
     */
    @Nonnull
    @Override
    public VerticalAlign getTextVAlign() {
        return m_buttonTextRenderer != null ? m_buttonTextRenderer.getTextVAlign() : VerticalAlign.center;
    }

    /**
     * Sets the text vertical alignment.
     * @param newTextVAlign VerticalAlign
     */
    @Override
    public void setTextVAlign(@Nonnull final VerticalAlign newTextVAlign) {
        if (m_buttonTextRenderer != null) {
            m_buttonTextRenderer.setTextVAlign(newTextVAlign);
        } else {
            log.warning("Failed to set the vertical text align. Maybe the element is not bound yet?");
        }
    }

    /**
     * Gets the text horizontal alignment.
     * @return The horizontal alignment.
     */
    @Nonnull
    @Override
    public HorizontalAlign getTextHAlign() {
        return m_buttonTextRenderer != null ? m_buttonTextRenderer.getTextHAlign() : HorizontalAlign.center;
    }

    /**
     * Sets the text horizontal alignment.
     * @param newTextHAlign HorizontalAlign
     */
    @Override
    public void setTextHAlign(@Nonnull final HorizontalAlign newTextHAlign) {
        if (m_buttonTextRenderer != null) {
            m_buttonTextRenderer.setTextHAlign(newTextHAlign);
        } else {
            log.warning("Failed to set the horizontal text align. Maybe the element is not bound yet?");
        }
    }

    /**
     * Gets the text color.
     * @return The color.
     */
    @Nonnull
    @Override
    public Color getTextColor() {
        return m_buttonTextRenderer != null ? m_buttonTextRenderer.getColor() : TextRenderer.DEFAULT_COLOR;
    }

    /**
     * Sets the text color.
     * @param newColor new Color for the button text
     */
    @Override
    public void setTextColor(@Nonnull final Color newColor) {
        if (m_buttonTextRenderer != null) {
            m_buttonTextRenderer.setColor(newColor);
        } else {
            log.warning("Failed to set the text color. Maybe the element is not bound yet?");
        }
    }

    /**
     * Just return false.
     * @return false.
     */
    @Override
    public boolean isIndeterminate() {
        return false;
    }

    /**
     * Just for the override.
     * @param indeterminate parameter.
     */
    @Override
    public void setIndeterminate(boolean indeterminate) {

    }

    /**
     * Gets the progress.
     * @return The progress.
     */
    @Override
    public float getProgress() {
        return m_progressIndicatorElement != null ? (float) m_progressIndicatorElement.getWidth() / getWidth() : 0;
    }

    /**
     * Sets the new progress.
     * @param progress new percent of the indicator.
     */
    @Override
    public void setProgress(float progress) {
        if (m_progressIndicatorElement != null) {
            m_progressIndicatorElement.setWidth(Math.round(Math.max(0, Math.min(progress, 1) * getWidth())));
        } else {
            if (!isBound()) {
                throw new IllegalStateException("Setting the progress is not possible before the binding is done.");
            }
            log.warning("Failed to set the progress indicator. Maybe the element is not bound yet?");
        }

    }
}
