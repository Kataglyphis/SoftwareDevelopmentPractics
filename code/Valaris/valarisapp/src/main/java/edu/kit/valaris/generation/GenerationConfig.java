package edu.kit.valaris.generation;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * Class providing access to the Configuration file.
 * @author Sidney Hansen, Lukas Schölch
 */
public class GenerationConfig {


    private JSONObject m_config;

    /**
     * Creates a new {@link GenerationConfig}.
     */
    public GenerationConfig() {
        JSONParser parser = new JSONParser();

        try {
            String path = "/edu.kit.valaris/config/GenerationConfig.json";
            m_config = (JSONObject)parser.parse(new InputStreamReader(getClass().getResourceAsStream(path)));
            System.out.println("json found at:" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the value of the referenced field in the configuration file.
     * E.g.
     * To access "value" the key must be: foo.bar
     * {
     *     "foo" : {
     *         "bar" : "value"
     *     }
     * }
     *
     * @param key the key, referencing a String value
     * @return the String value
     */
    public String getString(String key) {
        String[] path = key.split("\\.");
        JSONObject currentObj = m_config;

        for (int i = 0; i < path.length - 1; i++) {
            currentObj = (JSONObject)currentObj.get(path[i]);
            if (currentObj == null) {
                throw new IllegalArgumentException("The key: " + key + " does not match any entry.");
            }
        }
        return (String)currentObj.get(path[path.length - 1]);
    }

    /**
     * Returns the value of the referenced field in the configuration file.
     * E.g.
     * To access 0815 the key must be: foo.bar
     * {
     *     "foo" : {
     *         "bar" : 0815
     *     }
     * }
     *
     * @param key the key, referencing a Number value
     * @return the Number value
     */
    public Number getNumber(String key) {
        String[] path = key.split("\\.");
        JSONObject currentObj = m_config;

        for (int i = 0; i < path.length - 1; i++) {
            currentObj = (JSONObject)currentObj.get(path[i]);
            if (currentObj == null) {
                throw new IllegalArgumentException("The key: " + key + " does not match any entry.");
            }
        }

        return (Number) currentObj.get(path[path.length - 1]);
    }

    /**
     * Returns the value of the referenced field in the configuration file.
     * E.g.
     * To access [ 0.8, 1.5] the key must be: foo.bar
     * {
     *     "foo" : {
     *         "bar" : [ 0.8, 1.5]
     *     }
     * }
     *
     * @param key the key, referencing a float[] value
     * @return the float[] value
     */
    public float[] getFloatArray(String key) {
        String[] path = key.split("\\.");
        JSONObject currentObj = m_config;
        ArrayList<Float> floatList = new ArrayList<>();

        for (int i = 0; i < path.length - 1; i++) {
            currentObj = (JSONObject)currentObj.get(path[i]);
            if (currentObj == null) {
                throw new IllegalArgumentException("The key: " + key + " does not match any entry.");
            }
        }

        JSONArray jsonArray = (JSONArray) currentObj.get(path[path.length - 1]);

        for (int i = 0; i < jsonArray.size(); i++) {
            floatList.add((float) ((double) jsonArray.get(i)));
        }

        float[] floatArray = new float[floatList.size()];

        for (int i = 0; i < floatArray.length; i++) {
            floatArray[i] = floatList.get(i);
        }

        return floatArray;
    }

    /**
     * Returns the value of the referenced field in the configuration file.
     * E.g.
     * To access [ 8, 5] the key must be: foo.bar
     * {
     *     "foo" : {
     *         "bar" : [ 8, 5]
     *     }
     * }
     *
     * @param key the key, referencing a int[] value
     * @return the int[] value
     */
    public int[] getIntArray(String key) {
        String[] path = key.split("\\.");
        JSONObject currentObj = m_config;
        ArrayList<Integer> intList = new ArrayList<>();

        for (int i = 0; i < path.length - 1; i++) {
            currentObj = (JSONObject)currentObj.get(path[i]);
            if (currentObj == null) {
                throw new IllegalArgumentException("The key: " + key + " does not match any entry.");
            }
        }

        JSONArray jsonArray = (JSONArray) currentObj.get(path[path.length - 1]);

        for (int i = 0; i < jsonArray.size(); i++) {
            intList.add((int) (long) jsonArray.get(i));
        }

        int[] intArray = new int[intList.size()];

        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = intList.get(i);
        }

        return intArray;
    }

    /**
     * Returns the value of the referenced field in the configuration file.
     * E.g.
     * To access [ "cool", "stuff"] the key must be: foo.bar
     * {
     *     "foo" : {
     *         "bar" : [ "cool", "stuff"]
     *     }
     * }
     *
     * @param key the key, referencing a String[] value
     * @return the String[] value
     */
    public String[] getStringArray(String key) {
        String[] path = key.split("\\.");
        JSONObject currentObj = m_config;
        String[] stringArray;

        for (int i = 0; i < path.length - 1; i++) {
            currentObj = (JSONObject)currentObj.get(path[i]);
            if (currentObj == null) {
                throw new IllegalArgumentException("The key: " + key + " does not match any entry.");
            }
        }

        JSONArray jsonArray = (JSONArray) currentObj.get(path[path.length - 1]);

        stringArray = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            stringArray[i] = ((String) jsonArray.get(i));
        }
        return stringArray;
    }

    /**
     * Returns the value of the referenced field in the configuration file.
     * E.g.
     * To get the array:["books", "trees"] the key must be: foo.bar
     * {
     *     "foo" : {
     *         "bar" : { "books" : someValue, "trees" : someOtherValue }
     *     }
     * }
     *
     * @param key the key, referencing a key int the Json file
     * @return the String[] off all child keys
     */
    public String[] getChildren(String key) {
        String[] path = key.split("\\.");
        JSONObject currentObj = m_config;

        for (int i = 0; i < path.length; i++) {
            currentObj = (JSONObject)currentObj.get(path[i]);
            if (currentObj == null) {
                throw new IllegalArgumentException("The key: " + key + " does not match any entry.");
            }
        }

        Iterator<String> iterator = currentObj.keySet().iterator();


        String [] children = new String[currentObj.keySet().size()];
        int cnt = 0;
        while (iterator.hasNext()) {
            children[cnt] = iterator.next();
            cnt++;
        }
        return children;
    }

    /**
     * Returns if the given key matches a entry.
     *
     * @param key the Key
     * @return true if key is valid, false else wise
     */
    public boolean pathExists(String key) {
        String[] path = key.split("\\.");
        JSONObject currentObj = m_config;

        for (int i = 0; i < path.length; i++) {
            currentObj = (JSONObject)currentObj.get(path[i]);
            if (currentObj == null) {
                return false;
            }
        }
        return true;
    }
}
