package com.kevin.message.protocol.serialize.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.message.protocol.exception.SerializeException;
import com.kevin.message.protocol.serialize.ISerialize;

import java.io.IOException;

/**
 * @author: kevin
 * @description: Jackson
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-14 10:59
 */
public class JacksonSerializer implements ISerialize {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Override
    public  byte[] serialize(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz)  {
        try {
            return OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (JsonParseException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (JsonMappingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
