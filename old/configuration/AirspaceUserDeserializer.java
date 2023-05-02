package com.jku.dke.bac.ubsm.configuration;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jku.dke.bac.ubsm.model.au.AirspaceUser;

import java.io.IOException;

public class AirspaceUserDeserializer extends JsonDeserializer<AirspaceUser> {
    @Override
    public AirspaceUser deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);
        System.out.println("Deserializing: " + node.toString());
        String type = node.get("type").asText();
        System.out.println("Type: " + type);
        ObjectNode nodeWithoutType = ((ObjectNode) node).deepCopy();
        nodeWithoutType.remove("type");
        System.out.println("Node without type: " + nodeWithoutType.toString());
        try {
            Class<? extends AirspaceUser> c = Class.forName("com.jku.dke.bac.ubsm.model.au." + type).asSubclass(AirspaceUser.class);
            return codec.treeToValue(nodeWithoutType, c);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }
}