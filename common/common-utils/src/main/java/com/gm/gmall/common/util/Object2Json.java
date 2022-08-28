package com.gm.gmall.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author gym
 * @create 2022/8/28 0028 19:04
 */

public class Object2Json {
    private static ObjectMapper mapper=new ObjectMapper();
    public static String toJson(Object o){
        try {
            String s = mapper.writeValueAsString(o);
            return s;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
