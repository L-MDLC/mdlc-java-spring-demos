package edu.lmdlc.demo.kafka.connect.transforms.model.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Fields {

    private String firstName;
    private String lastName;

}
