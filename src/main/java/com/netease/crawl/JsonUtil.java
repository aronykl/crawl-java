package com.netease.crawl;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;

public class JsonUtil {

    public static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,true);
    }

    public static <T> T fromJson(final String json, Class<T> t) throws IOException {
    	return mapper.readValue(json,t);
    }

    public static String toJsonStr(Object obj) throws IOException {
        return mapper.writeValueAsString(obj);
    }
    public static ArrayList<?>  toList(final String json,Class<?> t) throws JsonParseException, JsonMappingException, IOException{
    	return mapper.readValue(json,  new TypeReference<ArrayList<?>>(){});
    }
}

