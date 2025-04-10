package edu.lmdlc.demo.kafka.connect.transforms.mapper;

import edu.lmdlc.demo.kafka.connect.transforms.model.client.ClientItem;
import edu.lmdlc.demo.kafka.connect.transforms.model.target.TargetData;

public class SourceToTargetMapper {

    // Default constructor
    public SourceToTargetMapper(){}

    public TargetData getTargetFromSource(ClientItem clientItem){
        TargetData TargetData = new TargetData();
        TargetData.setFirstName(clientItem.getFields().getFirstName());
        TargetData.setLastName(clientItem.getFields().getLastName());

        return TargetData;
    }

}
