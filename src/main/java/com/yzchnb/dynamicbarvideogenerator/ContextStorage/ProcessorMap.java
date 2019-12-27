package com.yzchnb.dynamicbarvideogenerator.ContextStorage;

import com.yzchnb.dynamicbarvideogenerator.Generator.CenterProcessor;

import java.util.concurrent.ConcurrentHashMap;

public class ProcessorMap {
    private static ConcurrentHashMap<String, CenterProcessor> processors = new ConcurrentHashMap<>();

    public static CenterProcessor getCenterProcessor(String key){
        return processors.get(key);
    }

    public static void putCenterProcessor(String key, CenterProcessor centerProcessor){
        processors.put(key, centerProcessor);
    }

    public static void removeCenterProcessor(String key){
        processors.remove(key);
    }

    public static boolean containsCenterProcessorKey(String key){
        return processors.containsKey(key);
    }
}
