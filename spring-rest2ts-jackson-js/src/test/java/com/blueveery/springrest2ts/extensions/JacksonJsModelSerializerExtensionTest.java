package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import org.junit.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JacksonJsModelSerializerExtensionTest {
    private final ModelSerializerExtension serializerExtension = new JacksonJsModelSerializerExtension();
    private final TSModule tsModule = new TSModule("test", Paths.get(""), true);
    private final TSClass vehicle = new TSClass("Vehicle", tsModule);

    @Test
    public void testDeserialization() {
        assertThat(
                serializerExtension.generateDeserializationCode("jsonData", new TSArray(vehicle))
        ).isEqualTo("objectMapper.parse<Vehicle[]>(jsonData, {mainCreator: () => [Array, [Vehicle]]})");
        assertThat(
                serializerExtension.generateDeserializationCode("jsonData", vehicle)
        ).isEqualTo("objectMapper.parse<Vehicle>(jsonData, {mainCreator: () => [Vehicle]})");
    }

    @Test
    public void testSerialization() {
        assertThat(
                serializerExtension.generateSerializationCode("entity", new TSArray(vehicle))
        ).isEqualTo("objectMapper.stringify<Vehicle[]>(entity)");
        assertThat(
                serializerExtension.generateSerializationCode("entity", vehicle)
        ).isEqualTo("objectMapper.stringify<Vehicle>(entity)");
    }
}