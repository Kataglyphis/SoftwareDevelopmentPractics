package edu.kit.valaris.generation;
import edu.kit.valaris.datastructure.MapBodyTest;
import edu.kit.valaris.generation.roadgeneration.RoadCursorTest;
import edu.kit.valaris.generation.roadgeneration.RoadGeneratorTest;
import edu.kit.valaris.generation.roadgeneration.RoadTest;
import edu.kit.valaris.generation.roomgeneration.DynamicRoomGeneratorTest;
import edu.kit.valaris.generation.tunnelgeneration.TunnelGeneratorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        MapBodyTest.class,
        RoadGeneratorTest.class,
        DynamicRoomGeneratorTest.class,
        TunnelGeneratorTest.class,
        RoadTest.class,
        RoadCursorTest.class
})


public class GenerationTestSuite {
    // the class remains empty,
    // used only as a holder for the above annotations
}
