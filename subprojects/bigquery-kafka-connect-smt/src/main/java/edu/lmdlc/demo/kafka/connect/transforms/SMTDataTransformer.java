package edu.lmdlc.demo.kafka.connect.transforms;

import edu.lmdlc.demo.kafka.connect.transforms.mapper.SourceToTargetMapper;
import edu.lmdlc.demo.kafka.connect.transforms.model.client.ClientItem;
import edu.lmdlc.demo.kafka.connect.transforms.model.target.TargetData;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.errors.DataException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SMTDataTransformer<R extends ConnectRecord<R>> implements Transformation<R> {

    private static final Logger log = LoggerFactory.getLogger(SMTDataTransformer.class);
    private ObjectMapper OBJECT_MAPPER;
    private SourceToTargetMapper mapper;
    private JsonConverter jsonConverter;

    @Override
    public void configure(Map<String, ?> configs) {
        mapper = new SourceToTargetMapper();
        jsonConverter = new JsonConverter();
        Map<String, Object> converterConfig = Map.of(
                "schemas.enable", false
        );
        jsonConverter.configure(converterConfig, false);
        OBJECT_MAPPER = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
    }

    @Override
    public R apply(R record) {
        // Skip tombstone records
        if (record.value() == null) {
            return record;
        }

        try {
            Object rawValue = record.value();
            log.debug("Raw value type: {}", rawValue.getClass().getName());
            log.debug("Raw value: {}", rawValue);

            String jsonStr;
            if (rawValue instanceof String) {
                jsonStr = (String) rawValue;

                if (jsonStr.startsWith("'") && jsonStr.endsWith("'")) {
                    // Remove the quotes
                    jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
                }
            } else {
                // Convert Connect data to JSON bytes -> string
                byte[] jsonBytes = jsonConverter.fromConnectData(record.topic(), record.valueSchema(), record.value());
                jsonStr = new String(jsonBytes, StandardCharsets.UTF_8);
            }

            //ClientItem clientItem = OBJECT_MAPPER.treeToValue(documentNode, ClientItem.class);  // Convert JSON -> POJO
            ClientItem clientItem = OBJECT_MAPPER.readValue(jsonStr, ClientItem.class);

            TargetData targetData = mapper.getTargetFromSource(clientItem);

            /*
            JsonNode transformedNode = OBJECT_MAPPER.valueToTree(targetData);
            log.debug("Transformed document: {}", transformedNode);

            // Convert JSON -> Kafka Connect data
            Object transformedValue = jsonConverter.toConnectData(record.topic(),
                    OBJECT_MAPPER.writeValueAsBytes(transformedNode)).value();
            */

            // Generate schema for BQFormat
            Schema schema = SchemaGenerator.getSchema(TargetData.class);

            // Convert BQFormat to Struct with schema
            Struct struct = StructConverter.toStruct(targetData, schema);

            return record.newRecord(
                    record.topic(),
                    record.kafkaPartition(),
                    record.keySchema(),
                    record.key(),
                    schema,
                    struct,
                    record.timestamp()
            );

        } catch (Exception e) {
            log.error("Error transforming record", e);
            throw new DataException("Error transforming record: " + e.getMessage(), e);
        }
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef();
    }

    @Override
    public void close() {
        // Clean up resources if needed
    }
}
