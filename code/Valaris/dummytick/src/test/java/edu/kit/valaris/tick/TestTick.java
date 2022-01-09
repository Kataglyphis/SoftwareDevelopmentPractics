package edu.kit.valaris.tick;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests functionality of {@link Tick}.
 */
public class TestTick {

    @Test
    public void testAddRemoveDGO() {
        Tick tick = new Tick();

        Assert.assertEquals("Events were not initialized empty", 0, tick.getEventsAsStream().count());
        Assert.assertEquals("DynamicGameObjects were not initialized empty", 0, tick.getDynamicGameObjects(false).size());

        int id = tick.addDynamicGameObject(new DynamicGameObject(DynamicGameObjectType.DUMMY), "Test");

        Assert.assertEquals("Events did not register correctly", 1, tick.getEventsAsStream().count());
        Assert.assertEquals("DGOs were not added correctly", 1, tick.getDynamicGameObjects(false).size());

        tick.removeDynamicGameObject(id);

        Assert.assertEquals("Events did not register correctly", 1, tick.getEventsAsStream().count());
        //still expect 1 entry since the index shall not be removed from the list.
        Assert.assertEquals("DGOs were not deleted correctly", 1, tick.getDynamicGameObjects(false).size());
    }

    @Test
    public void testAddEvent() {
        Tick tick = new Tick();
        tick.addEvent(new TickEvent("Test", null));

        Assert.assertEquals("Events did not register correctly", 1, tick.getEventsAsStream().count());
    }

    @Test
    public void testSync() {
        Tick tick0 = new Tick();
        int id0 = tick0.addDynamicGameObject(new DynamicGameObject(DynamicGameObjectType.DUMMY), "Test");
        tick0.addDynamicGameObject(new DynamicGameObject(DynamicGameObjectType.DUMMY), "Test");

        Tick tick1 = new Tick();
        tick1.sync(tick0);

        Assert.assertEquals("Events not removed after accessing event queue", 0, tick0.getEventsAsStream().count());
        Assert.assertEquals("Events not synced correctly", 2, tick1.getEventsAsStream().count());
        Assert.assertEquals("DynamicGameObjects not synced correctly", 2, tick1.getDynamicGameObjects(false).size());

        tick0.removeDynamicGameObject(id0);
        tick1.sync(tick0);

        Assert.assertEquals("Events not synced correctly", 1, tick1.getEventsAsStream().count());
        Assert.assertEquals("DynamicGameObjects not synced correctly", null, tick1.getDynamicGameObject(id0));
    }
}
