package com.xxl.job.plus.executor.utils;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson util
 * <p>
 * 1、obj need private and set/get；
 * 2、do not support inner class；
 *
 * @author xuxueli 2015-9-25 18:02:56
 */
public class JacksonUtil {
    private static Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

    private final static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .registerModule(new JavaTimeModule()
                    .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)))
                    .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)))
                    .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                    .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
            );

    public static ObjectMapper getInstance() {
        return objectMapper;
    }

    /**
     * bean、array、List、Map --> json
     *
     * @param obj
     * @return json string
     * @throws Exception
     */
    public static String writeValueAsString(Object obj) {
        try {
            return getInstance().writeValueAsString(obj);
        } catch (JsonGenerationException e) {
            logger.error(e.getMessage(), e);
        } catch (JsonMappingException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * string --> bean、Map、List(array)
     *
     * @param jsonStr
     * @param clazz
     * @return obj
     * @throws Exception
     */
    public static <T> T readValue(String jsonStr, Class<T> clazz) {
        try {
            return getInstance().readerFor(clazz).readValue(jsonStr);
        } catch (JsonParseException e) {
            logger.error(e.getMessage(), e);
        } catch (JsonMappingException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * string --> List<Bean>...
     *
     * @param jsonStr
     * @param parametrized     List.class
     * @param parameterClasses Bean.class
     * @param <T>
     * @return
     */
    public static <T> T readValue(String jsonStr, Class<?> parametrized, Class<?>... parameterClasses) {
        try {
            JavaType javaType = getInstance().getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return getInstance().readValue(jsonStr, javaType);
        } catch (JsonParseException e) {
            logger.error(e.getMessage(), e);
        } catch (JsonMappingException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
