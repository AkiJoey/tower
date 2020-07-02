package com.akijoey.util;

import com.akijoey.bean.Player;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;

public class ConfigUtil {

    public static Player player;
    public static ArrayList<int[][]> maps;

    public static ArrayList<int[][]> positions;
    public static HashMap<Integer, HashMap<String, Object>> monsters;
    public static HashMap<Integer, ArrayList<String>> messages;

    public static final String path = Thread.currentThread().getContextClassLoader().getResource("config/").getPath();

    static {
        loadDefault();
        Map constant = readConfig("constant");
        positions = parseListArray((List)constant.get("position"));
        monsters = parseIntegerMap((Map)constant.get("monster"));
        messages = parseIntegerList((Map)constant.get("message"));
    }

    public static Map readConfig(String name) {
        File config = new File(path + name + ".json");
        Map data = null;
        try {
            data = new ObjectMapper().readValue(config, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void writeConfig(String name, Object data) {
        File config = new File(path + name + ".json");
        try {
            if (!config.exists()) {
                config.createNewFile();
            }
            new ObjectMapper().writeValue(config, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Integer, HashMap<String, Object>> parseIntegerMap(Map<String, Object> map) {
        return new HashMap<>(){{
            map.forEach((key, value) -> put(Integer.valueOf(key), (HashMap<String, Object>)value));
        }};
    }

    public static HashMap<Integer, ArrayList<String>> parseIntegerList(Map<String, Object> map) {
        return new HashMap<>(){{
            map.forEach((key, value) -> put(Integer.valueOf(key), (ArrayList<String>)value));
        }};
    }

    public static ArrayList<int[][]> parseListArray(List<List<List<Integer>>> data) {
        return new ArrayList<>(){{
            data.forEach(list -> {
                int[][] arr = new int[list.size()][];
                for (int i = 0;i < list.size();i++) {
                    arr[i] = list.get(i).stream().mapToInt(Integer::valueOf).toArray();
                }
                add(arr);
            });
        }};
    }

    public static boolean hasArchive() {
        return hasCloud() || hasLocal();
    }

    public static void saveArchive() {
        saveLocal();
        saveCloud();
    }

    public static void loadArchive() {
        try {
            loadCloud();
        } catch (Exception e) {
            e.printStackTrace();
            loadLocal();
        }
    }

    public static void loadDefault() {
        Map archive = readConfig("default");
        player = new Player((Map)archive.get("player"));
        maps = parseListArray((List)archive.get("map"));
    }

    public static boolean hasLocal() {
        return new File(path + "local.json").exists();
    }

    public static void saveLocal() {
        writeConfig("local", new HashMap<>(){{
            put("player", player);
            put("map", maps);
        }});
    }

    public static void loadLocal() {
        Map archive = readConfig("local");
        player = new Player((Map)archive.get("player"));
        maps = parseListArray((List)archive.get("map"));
    }

    public static boolean hasCloud() {
        if (!new File(path + "cloud.json").exists()) {
            return false;
        }
        String id = (String)readConfig("cloud").get("id");
        String json = MongoUtil.find(JsonUtil.stringify(Map.of("id", id)));
        return json != null;
    }

    public static void saveCloud() {
        if (!hasCloud()) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            writeConfig("cloud", Map.of("id", uuid));
            MongoUtil.insert(JsonUtil.stringify(new HashMap<>(){{
                put("id", readConfig("cloud").get("id"));
                put("player", player);
                put("map", maps);
            }}));
            return;
        }
        String id = (String)readConfig("cloud").get("id");
        MongoUtil.update(JsonUtil.stringify(Map.of("id", id)), JsonUtil.stringify(new HashMap<>(){{
            put("id", id);
            put("player", player);
            put("map", maps);
        }}));
    }

    public static void loadCloud() {
        String id = (String)readConfig("cloud").get("id");
        String json = MongoUtil.find(JsonUtil.stringify(Map.of("id", id)));
        Map archive = JsonUtil.parse(json);
        player = new Player((Map)archive.get("player"));
        maps = parseListArray((List)archive.get("map"));
    }

}
