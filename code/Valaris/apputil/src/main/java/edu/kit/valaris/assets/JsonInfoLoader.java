package edu.kit.valaris.assets;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

/**
 * {@link InfoLoader} used to load .json files.
 *
 * @author Frederik Lingg
 */
public class JsonInfoLoader implements InfoLoader {

    /**
     * The {@link ObjectMapper} used by this {@link JsonInfoLoader}.
     */
    private ObjectMapper m_mapper;

    /**
     * Creates a new {@link JsonInfoLoader} with a new {@link ObjectMapper}.
     */
    public JsonInfoLoader() {
        m_mapper = new ObjectMapper();
    }

    @Override
    public AssetPack loadAssetPack(String path) {
        URL location = this.getClass().getResource(path);
        try {
            return m_mapper.readValue(location, AssetPack.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public AssetInfo loadAssetInfo(String path) {
        URL location = this.getClass().getResource(path);
        try {
            return m_mapper.readValue(location, AssetInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
