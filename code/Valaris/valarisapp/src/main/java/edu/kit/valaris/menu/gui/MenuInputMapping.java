package edu.kit.valaris.menu.gui;

import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyInputMapping;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.input.keyboard.KeyboardInputEvent;

/**
 * This class is responsible for converting user inputs and actions to NiftyInputEvents. It replaces the
 * default MenuInputMapping created by m_nifty.
 * Because the controller classes only dependencies are NiftyInputEvents it will be easy to add other equipments later
 * on. The only thing that needs to be extended is the Mapping.
 * @author Artur Wesner
 */
public class MenuInputMapping implements NiftyInputMapping {
    /**
     * convert the given KeyboardInputEvent into a neutralized NiftyInputEvent.
     *
     * @param inputEvent input event
     * @return NiftInputEvent
     */
    @Override
    public NiftyInputEvent convert(KeyboardInputEvent inputEvent) {
        if (inputEvent.isKeyDown()) {
            if (inputEvent.getKey() == KeyboardInputEvent.KEY_F1) {
                return NiftyStandardInputEvent.ConsoleToggle;
            } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_RETURN) {
                return NiftyStandardInputEvent.SubmitText;
            } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_SPACE) {
                return NiftyStandardInputEvent.Activate;
            } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_TAB) {
                if (inputEvent.isShiftDown()) {
                    return NiftyStandardInputEvent.PrevInputElement;
                } else {
                    return NiftyStandardInputEvent.NextInputElement;
                }
            } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_UP) {
                return NiftyStandardInputEvent.MoveCursorUp;
            } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_DOWN) {
                return NiftyStandardInputEvent.MoveCursorDown;
            } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_F5) {
                return NiftyStandardInputEvent.Cut;
            } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_ESCAPE) {
                return NiftyStandardInputEvent.Escape;
            }
        }
        return null;
    }
}
