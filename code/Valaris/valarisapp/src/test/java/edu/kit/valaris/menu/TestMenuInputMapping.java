package edu.kit.valaris.menu;

import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.input.keyboard.KeyboardInputEvent;
import edu.kit.valaris.menu.gui.MenuInputMapping;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artur Wesner
 */
public class TestMenuInputMapping {
    @Test
    public void testConvert() {
        MenuInputMapping menuInputMapping = new MenuInputMapping();

        Assert.assertEquals(menuInputMapping.convert(new KeyboardInputEvent(KeyboardInputEvent.KEY_F1,
                ' ', true, false, false))
                , NiftyStandardInputEvent.ConsoleToggle);
        Assert.assertEquals(menuInputMapping.convert(new KeyboardInputEvent(KeyboardInputEvent.KEY_RETURN,
                ' ', true, false, false))
                , NiftyStandardInputEvent.SubmitText);
        Assert.assertEquals(menuInputMapping.convert(new KeyboardInputEvent(KeyboardInputEvent.KEY_SPACE,
                ' ', true, false, false))
                , NiftyStandardInputEvent.Activate);
        Assert.assertEquals(menuInputMapping.convert(new KeyboardInputEvent(KeyboardInputEvent.KEY_TAB,
                ' ', true, true, false))
                , NiftyStandardInputEvent.PrevInputElement);
        Assert.assertEquals(menuInputMapping.convert(new KeyboardInputEvent(KeyboardInputEvent.KEY_TAB,
                ' ', true, false, false))
                , NiftyStandardInputEvent.NextInputElement);
        Assert.assertEquals(menuInputMapping.convert(new KeyboardInputEvent(KeyboardInputEvent.KEY_UP,
                ' ', true, false, false))
                , NiftyStandardInputEvent.MoveCursorUp);
        Assert.assertEquals(menuInputMapping.convert(new KeyboardInputEvent(KeyboardInputEvent.KEY_DOWN,
                ' ', true, false, false))
                , NiftyStandardInputEvent.MoveCursorDown);
        Assert.assertEquals(menuInputMapping.convert(new KeyboardInputEvent(KeyboardInputEvent.KEY_F5,
                ' ', true, false, false))
                , NiftyStandardInputEvent.Cut);
        Assert.assertEquals(menuInputMapping.convert(new KeyboardInputEvent(KeyboardInputEvent.KEY_ESCAPE,
                ' ', true, false, false))
               , NiftyStandardInputEvent.Escape);
    }
}
