package edu.lmdlc.demo.kafka.connect.transforms;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for converting Java objects to Kafka Connect Structs
 */
public class StructConverter {
    private static final Logger log = LoggerFactory.getLogger(StructConverter.class);

    /**
     * Convert a POJO to a Kafka Connect Struct
     */
    public static Struct toStruct(Object obj, Schema schema) {
        if (obj == null) {
            return null;
        }

        Struct struct = new Struct(schema);
        Class<?> objClass = obj.getClass();

        for (org.apache.kafka.connect.data.Field field : schema.fields()) {
            String fieldName = field.name();
            try {
                Field objField = findField(objClass, fieldName);
                if (objField != null) {
                    objField.setAccessible(true);
                    Object value = objField.get(obj);

                    if (value != null) {
                        struct.put(fieldName, convertFieldValue(value, field.schema()));
                    }
                }
            } catch (Exception e) {
                log.warn("Error setting field {}: {}", fieldName, e.getMessage());
            }
        }

        return struct;
    }

    /**
     * Find a field in a class or its superclasses by name
     */
    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Convert a field value to the appropriate type for the schema
     */
    private static Object convertFieldValue(Object value, Schema schema) throws Exception {
        if (value == null) {
            return null;
        }

        switch (schema.type()) {
            case STRING:
                if (value instanceof Enum) {
                    return ((Enum<?>) value).name();
                }
                return value.toString();

            case INT32:
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
                return Integer.parseInt(value.toString());

            case INT64:
                if (value instanceof Date) {
                    return ((Date) value).getTime();
                } else if (value instanceof Number) {
                    return ((Number) value).longValue();
                }
                return Long.parseLong(value.toString());

            case FLOAT32:
                if (value instanceof Number) {
                    return ((Number) value).floatValue();
                }
                return Float.parseFloat(value.toString());

            case FLOAT64:
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                return Double.parseDouble(value.toString());

            case BOOLEAN:
                if (value instanceof Boolean) {
                    return value;
                }
                return Boolean.parseBoolean(value.toString());

            case ARRAY:
                if (value instanceof Collection) {
                    Collection<?> collection = (Collection<?>) value;
                    Schema elementSchema = schema.valueSchema();
                    List<Object> convertedList = new ArrayList<>(collection.size());

                    for (Object element : collection) {
                        convertedList.add(convertFieldValue(element, elementSchema));
                    }

                    return convertedList;
                }
                return null;

            case MAP:
                if (value instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) value;
                    Schema valueSchema = schema.valueSchema();
                    Map<String, Object> convertedMap = new HashMap<>(map.size());

                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        String key = entry.getKey().toString();
                        Object convertedValue = convertFieldValue(entry.getValue(), valueSchema);
                        convertedMap.put(key, convertedValue);
                    }

                    return convertedMap;
                }
                return null;

            case STRUCT:
                return toStruct(value, schema);

            default:
                log.warn("Unsupported schema type: {}", schema.type());
                return null;
        }
    }
}
