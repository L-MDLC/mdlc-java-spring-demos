package edu.lmdlc.demo.kafka.connect.transforms.model.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ClientItem {

    private String schema;

    private Fields fields;

    private String location;
    private String createdAt;
    private String updatedAt;
    private String refreshedAt;

}
