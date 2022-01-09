package edu.kit.valaris.tick;

import edu.kit.valaris.tick.properties.IProperty;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Stream;

/**
 * Tests {@link DynamicGameObject} functionality
 */
public class TestDynamicGameObject {

    @Test
    public void testAddRemoveProperty() {
        DynamicGameObject dgo = new DynamicGameObject(DynamicGameObjectType.DUMMY);

        IProperty p0 = new ATestProperty();
        IProperty p1 = new ATestProperty();
        int i0 = dgo.addProperty(p0);
        int i1 = dgo.addProperty(p1);

        Assert.assertEquals("Property a not properly added", p0, dgo.getProperties().get(i0));
        Assert.assertEquals("Property a not properly added", p1, dgo.getProperties().get(i1));

        dgo.removeProperty(i0);
        Assert.assertEquals("Property was not removed", null, dgo.getProperties().get(i0));

        int i2 = dgo.addProperty(p0);
        Assert.assertEquals("Property a not properly added", p0, dgo.getProperties().get(i2));
        //this is no longer accurate
        //Assert.assertEquals("Expected Property to take empty spot", i0, i2);
    }

    @Test
    public void testAddEvent() {
        DynamicGameObject dgo = new DynamicGameObject(DynamicGameObjectType.DUMMY);
        dgo.addEvent(new DynamicGameObjectEvent("TestAction", null));
        Stream<DynamicGameObjectEvent> events = dgo.getEventsAsStream();

        Assert.assertEquals("Event Stream does not match expected length", 1, events.count());
    }

    @Test
    public void testGetEventsAsStream() {
        DynamicGameObject dgo = new DynamicGameObject(DynamicGameObjectType.DUMMY);
        dgo.addEvent(new DynamicGameObjectEvent("TestAction", null));

        //stream should just contain that one element
        Stream<DynamicGameObjectEvent> events = dgo.getEventsAsStream();
        Assert.assertEquals("Event Stream does not match expected length", 1, events.count());

        //stream should contain no elements
        events = dgo.getEventsAsStream();
        Assert.assertEquals("Event Stream does not match expected length", 0, events.count());
    }

    @Test
    public void testSync() {
        ATestProperty p0 = new ATestProperty();
        BTestProperty p1 = new BTestProperty();
        ATestProperty p2 = new ATestProperty();
        p0.setValue(1);
        p1.setValue(2);
        p2.setValue(3);

        DynamicGameObject dgo0 = new DynamicGameObject(DynamicGameObjectType.DUMMY);
        int i0 = dgo0.addProperty(p0);
        int i1 = dgo0.addProperty(p1);

        DynamicGameObject dgo1 = new DynamicGameObject(DynamicGameObjectType.DUMMY);
        dgo1.sync(dgo0);

        Assert.assertEquals("Number of properties does not match", 2, dgo1.getProperties().size());
        Assert.assertEquals("Properties not copied correctly", 1, ((ATestProperty) dgo1.getProperties().get(i0)).getValue());
        Assert.assertEquals("Properties not copied correctly", 2, ((BTestProperty) dgo1.getProperties().get(i1)).getValue());
        Assert.assertEquals("Events not synced correctly", 2, dgo1.getEventsAsStream().count());

        dgo0.removeProperty(i0);
        dgo1.sync(dgo0);

        Assert.assertEquals("Deletion not synced correctly", null, dgo1.getProperties().get(i0));

        i0 = dgo0.addProperty(p0);
        dgo1.sync(dgo0);

        dgo0.removeProperty(i0);
        int i2 = dgo0.addProperty(p2);

        dgo1.sync(dgo0);

        Assert.assertEquals("Properties not synced correctly", 3, ((ATestProperty) dgo1.getProperties().get(i2)).getValue());
        Assert.assertEquals("Events not synced correctly", 4, dgo1.getEventsAsStream().count());
    }

    private class ATestProperty implements IProperty {

        private int m_value;

        @Override
        public IProperty deepCopy() {
            ATestProperty p = new ATestProperty();
            p.setValue(m_value);
            return p;
        }

        @Override
        public boolean canSync(IProperty target) {
            return target instanceof ATestProperty;
        }

        @Override
        public void sync(IProperty target) throws IllegalStateException {
            if(!canSync(target)) throw new IllegalStateException("Properties do not map correctly");

            setValue(((ATestProperty) target).getValue());
        }

        void setValue(int value) {
            m_value = value;
        }

        int getValue() {
            return m_value;
        }
    }

    private class BTestProperty implements IProperty {

        private int m_value;

        @Override
        public IProperty deepCopy() {
            BTestProperty p = new BTestProperty();
            p.setValue(m_value);
            return p;
        }

        @Override
        public boolean canSync(IProperty target) {
            return target instanceof BTestProperty;
        }

        @Override
        public void sync(IProperty target) throws IllegalStateException {
            if(!canSync(target)) throw new IllegalStateException("Properties do not map correctly");

            setValue(((BTestProperty) target).getValue());
        }

        void setValue(int value) {
            m_value = value;
        }

        int getValue() {
            return m_value;
        }
    }
}
