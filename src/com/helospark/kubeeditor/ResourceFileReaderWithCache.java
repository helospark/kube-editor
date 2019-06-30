package com.helospark.kubeeditor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceFileReaderWithCache {
    private static Map<String, String> fileNameToValue = new ConcurrentHashMap<>();

    public static String getOrReadFile(String fileName) {
        if (fileNameToValue.containsKey(fileName)) {
            return fileNameToValue.get(fileName);
        } else {
            String result = ResourceFileReader.readFile(fileName);
            fileNameToValue.put(fileName, result);
            return result;
        }
    }
}
