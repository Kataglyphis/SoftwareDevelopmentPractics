package edu.kit.valaris.generation.tunnelgeneration;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.generation.AssetsUtility;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.GeneratorSettings;
import edu.kit.valaris.generation.roadgeneration.*;

import java.util.Set;


/**
 * Implementation of the {@link ITunnelGenerator}
 * @author Sidney Hansen
 */
public class TunnelGenerator implements ITunnelGenerator {

    private GenerationConfig m_generationConfig;
    private AssetProvider m_assetProvider;

    public TunnelGenerator(GenerationConfig generationConfig, AssetProvider assetProvider) {
        this.m_generationConfig = generationConfig;
        this.m_assetProvider = assetProvider;
    }


    @Override
    public ITunnel generate(int seed, Vector3f targetPosition, float horizontalAngle, float verticalAngle,
                            Vector2f entryWidth, Set<GeneratorSettings> flags) {

        RoadCursor target = new RoadCursor(-horizontalAngle,verticalAngle,0, targetPosition,entryWidth);

        IRoadGenerator roadGenerator = new RoadGenerator(m_generationConfig);

        Road road = roadGenerator.generateConnectingRoad(seed, target, entryWidth, "testsettings", flags);

        if (road == null) {
            return null;
        }

        Node tunnelAssetRootNode = new Node("tunnelAssetRootNode");

        generateTunnelSurrounding(seed, tunnelAssetRootNode, road, flags);
        generateTunnelFloor(seed, tunnelAssetRootNode, road, flags);


        if (flags.contains(GeneratorSettings.CHECKPOINT_ENTRY)) {
            road.getFirstRoadCursor().addProperty(RoadCursorProperty.CHECKPOINT);
        }

        if (flags.contains(GeneratorSettings.CHECKPOINT_EXIT)) {
            road.getLastRoadCursor().addProperty(RoadCursorProperty.CHECKPOINT);
        }


        if (flags.contains(GeneratorSettings.DOOR_ENTRY)) {
            road.getFirstRoadCursor().addProperty(RoadCursorProperty.DOOR);
        }

        if (flags.contains(GeneratorSettings.DOOR_EXIT)) {
            road.getLastRoadCursor().addProperty(RoadCursorProperty.DOOR);
        }


        for (RoadCursor roadCursor :
                road.getRoadCursors()) {
            if (roadCursor.hasProperty(RoadCursorProperty.DOOR)) {
                generateDoor(seed, tunnelAssetRootNode, roadCursor, flags);
            }
        }


        Tunnel tunnel = new Tunnel(road, tunnelAssetRootNode);

        return tunnel;
    }

    private void generateTunnelFloor(int seed, Node tunnelAssetRootNode, Road road, Set<GeneratorSettings> flags) {
        String assetBundle = selectAssetBundle(seed, "roadAsset");
        String assetName = selectAssetFromBundle(seed, "roadAsset", assetBundle);
        String assetPath = "tunnelgeneration.assetGroups.roadAsset." + assetBundle + "." + assetName;
        String assetId = m_generationConfig.getString(assetPath + ".Id");
        Spatial asset = m_assetProvider.provide(assetId);

        Vector3f assetDimensions = new Vector3f(
                m_generationConfig.getNumber(assetPath + ".SizeX").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeY").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeZ").floatValue());

        int segmentsPerAsset = m_generationConfig.getNumber(assetPath + ".Segments").intValue();

        tunnelAssetRootNode.attachChild(AssetsUtility.generateRoadAsset(road, asset, assetDimensions, segmentsPerAsset, false));
    }

    private void generateDoor(int seed, Node tunnelAssetRootNode, RoadCursor roadCursor, Set<GeneratorSettings> flags) {
        String assetBundle = selectAssetBundle(seed, "doorAsset");
        String assetName = selectAssetFromBundle(seed, "doorAsset", assetBundle);
        String assetPath = "tunnelgeneration.assetGroups.doorAsset." + assetBundle + "." + assetName;
        String assetId = m_generationConfig.getString(assetPath + ".Id");
        Spatial asset = m_assetProvider.provide(assetId);

        Vector3f assetDimensions = new Vector3f(
                m_generationConfig.getNumber(assetPath + ".SizeX").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeY").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeZ").floatValue());
        tunnelAssetRootNode.attachChild(AssetsUtility.tranformAssetAlongRoadCursor(
                roadCursor, asset, assetDimensions, true));
    }

    private void generateTunnelSurrounding(int seed, Node tunnelAssetRootNode, Road road, Set<GeneratorSettings> flags) {

        String assetBundle = selectAssetBundle(seed, "surroundingAssets");
        String assetName = selectAssetFromBundle(seed, "surroundingAssets", assetBundle);
        String assetPath = "tunnelgeneration.assetGroups.surroundingAssets." + assetBundle + "." + assetName;
        String assetId = m_generationConfig.getString(assetPath + ".Id");
        Spatial asset = m_assetProvider.provide(assetId);

        Vector3f assetDimensions = new Vector3f(
                m_generationConfig.getNumber(assetPath + ".SizeX").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeY").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeZ").floatValue());

        int segmentsPerAsset = m_generationConfig.getNumber(assetPath + ".Segments").intValue();

        tunnelAssetRootNode.attachChild(AssetsUtility.generateRoadAsset(road, asset, assetDimensions, segmentsPerAsset, true));
    }

    private String selectAssetBundle(int seed, String assetGroup) {

        final String parentPath = "tunnelgeneration.assetGroups." + assetGroup;

        String[] assetBundles =  m_generationConfig.getChildren(parentPath);

        return assetBundles[Math.abs(seed) % assetBundles.length];
    }

    private String selectAssetFromBundle(int seed, String assetGroup, String assetBundle) {

        final String parentPath = "tunnelgeneration.assetGroups." + assetGroup + "." + assetBundle;

        String[] assetNames =  m_generationConfig.getChildren(parentPath);

        return assetNames[seed % assetNames.length];

    }
}
