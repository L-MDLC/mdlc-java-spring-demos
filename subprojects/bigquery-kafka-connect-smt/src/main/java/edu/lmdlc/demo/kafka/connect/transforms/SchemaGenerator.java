package edu.lmdlc.demo.kafka.connect.transforms;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for generating Kafka Connect schemas from Java classes using reflection
 */
public class SchemaGenerator {
    private static final Logger log = LoggerFactory.getLogger(SchemaGenerator.class);
    private static final Map<Class<?>, Schema> schemaCache = new ConcurrentHashMap<>();

    /**
     * Get or create a schema for the specified class
     */
    public static Schema getSchema(Class<?> clazz) {
        return schemaCache.computeIfAbsent(clazz, SchemaGenerator::createSchemaForClass);
    }

    /**
     * Create a schema for a class using reflection
     */
    private static Schema createSchemaForClass(Class<?> clazz) {
        SchemaBuilder builder = SchemaBuilder.struct();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();

            Schema fieldSchema = getFieldSchema(field);
            if (fieldSchema != null) {
                builder.field(fieldName, fieldSchema);
            }
        }

        return builder.build();
    }

    /**
     * Determine the appropriate schema for a field based on its type
     */
    private static Schema getFieldSchema(Field field) {
        Class<?> fieldType = field.getType();

        if (fieldType == String.class) {
            return Schema.OPTIONAL_STRING_SCHEMA;
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return Schema.OPTIONAL_INT32_SCHEMA;
        } else if (fieldType == Long.class || fieldType == long.class) {
            return Schema.OPTIONAL_INT64_SCHEMA;
        } else if (fieldType == Double.class || fieldType == double.class) {
            return Schema.OPTIONAL_FLOAT64_SCHEMA;
        } else if (fieldType == Float.class || fieldType == float.class) {
            return Schema.OPTIONAL_FLOAT32_SCHEMA;
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return Schema.OPTIONAL_BOOLEAN_SCHEMA;
        } else if (fieldType == byte[].class || fieldType == Byte[].class) {
            return Schema.OPTIONAL_BYTES_SCHEMA;
        } else if (fieldType == Date.class) {
            return Schema.OPTIONAL_INT64_SCHEMA; // Store as timestamp (milliseconds since epoch)
        } else if (fieldType.isEnum()) {
            return Schema.OPTIONAL_STRING_SCHEMA; // Store enum as string
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            // Handle collections (lists, sets, etc.)
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
                if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                    Class<?> elementType = (Class<?>) typeArgs[0];
                    Schema elementSchema = getElementSchema(elementType);
                    return SchemaBuilder.array(elementSchema).optional().build();
                }
            }
            // Default to array of strings if type cannot be determined
            return SchemaBuilder.array(Schema.OPTIONAL_STRING_SCHEMA).optional().build();
        } else if (Map.class.isAssignableFrom(fieldType)) {
            // Handle maps - use string keys for simplicity
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
                if (typeArgs.length > 1 && typeArgs[1] instanceof Class) {
                    Class<?> valueType = (Class<?>) typeArgs[1];
                    Schema valueSchema = getElementSchema(valueType);
                    return SchemaBuilder.map(Schema.STRING_SCHEMA, valueSchema).optional().build();
                }
            }
            // Default to map of string -> string if types cannot be determined
            return SchemaBuilder.map(Schema.STRING_SCHEMA, Schema.OPTIONAL_STRING_SCHEMA).optional().build();
        } else if (!fieldType.isPrimitive() && !fieldType.isArray()) {
            // Handle complex objects (nested structures)
            Schema nestedSchema = getSchema(fieldType);

            SchemaBuilder nestedBuilder = SchemaBuilder.struct().optional();
            for (org.apache.kafka.connect.data.Field nestedField : nestedSchema.fields()) {
                nestedBuilder.field(nestedField.name(), nestedField.schema());
            }
            return nestedBuilder.build();
            //return SchemaBuilder.struct().optional().fields(nestedSchema.fields()).build();
        }

        // Unknown type - log warning and skip
        log.warn("Unsupported field type: {} for field: {}", fieldType.getName(), field.getName());
        return null;
    }

    private static Schema getElementSchema(Class<?> elementType) {
        if (elementType == String.class) {
            return Schema.OPTIONAL_STRING_SCHEMA;
        } else if (elementType == Integer.class || elementType == int.class) {
            return Schema.OPTIONAL_INT32_SCHEMA;
        } else if (elementType == Long.class || elementType == long.class) {
            return Schema.OPTIONAL_INT64_SCHEMA;
        } else if (elementType == Double.class || elementType == double.class) {
            return Schema.OPTIONAL_FLOAT64_SCHEMA;
        } else if (elementType == Float.class || elementType == float.class) {
            return Schema.OPTIONAL_FLOAT32_SCHEMA;
        } else if (elementType == Boolean.class || elementType == boolean.class) {
            return Schema.OPTIONAL_BOOLEAN_SCHEMA;
        } else if (elementType == Date.class) {
            return Schema.OPTIONAL_INT64_SCHEMA;
        } else if (elementType.isEnum()) {
            return Schema.OPTIONAL_STRING_SCHEMA;
        } else if (!elementType.isPrimitive()) {
            return getSchema(elementType);
        }

        // Default to string for unknown types
        return Schema.OPTIONAL_STRING_SCHEMA;
    }
}