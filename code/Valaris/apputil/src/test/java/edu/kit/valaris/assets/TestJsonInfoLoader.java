package edu.kit.valaris.assets;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Test {@link JsonInfoLoader}.
 */
public class TestJsonInfoLoader {

    @Test
    public void testLoadAssetPack() {
        InfoLoader loader = new JsonInfoLoader();
        AssetPack pack = loader.loadAssetPack("/edu.kit.valaris/assets/TestAssetPack.json");

        assertEquals("Wrong group count", 1, pack.getGroups().count());
        pack.getGroups().forEachOrdered(group -> {
            assertEquals("wrong group key", "TestGroup", group.getKey());

            assertEquals("Wrong asset count", 1, group.getAssets().count());
            group.getAssets().forEachOrdered(asset -> {
                assertEquals("Wrong asset name", "/edu.kit.valaris/assets/TestAsset.json", asset);
            });
        });
    }

    @Test
    public void testLoadAssetInfo() {
        InfoLoader loader = new JsonInfoLoader();
        AssetInfo info = loader.loadAssetInfo("/edu.kit.valaris/assets/TestAssetInfo.json");

        assertEquals("Wrong key", "testkey", info.getKey());
        assertEquals("Wrong path", "testpath", info.getPath());

        assertEquals("Wrong node count", 1, info.getNodes().count());
        info.getNodes().forEachOrdered(node -> {
            assertEquals("Wrong node key", "testnode", node.getKey());
            assertEquals("Wrong node path", "testpath", node.getPath());
        });

        assertEquals("Wrong control count", 1, info.getControls().count());
        info.getControls().forEachOrdered(control -> {
            assertEquals("Wrong control key", "testcontrol", control.getKey());
            assertEquals("Wrong control path", "testpath", control.getPath());
            assertEquals("Wrong control type", "edu.kit.edu.kit.edu.kit.valaris.TestControl", control.getType());
            assertEquals("Wrong control isPreload state", false, control.isPreload());
        });
    }
}
