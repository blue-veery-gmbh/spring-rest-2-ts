package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
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
                serializerExtension.generateDeserializationCode("jsonData",
                        new TSMethod("test", vehicle, new TSArray(vehicle), new EmptyImplementationGenerator(), false, false)
                )
        ).isEqualTo("this.objectMapper.parse<Vehicle[]>(jsonData, {mainCreator: () => [Array, [Vehicle]]})");

        assertThat(
                serializerExtension.generateDeserializationCode("jsonData",
                        new TSMethod("test", vehicle, vehicle, new EmptyImplementationGenerator(), false, false)
                )
        ).isEqualTo("this.objectMapper.parse<Vehicle>(jsonData, {mainCreator: () => [Vehicle]})");

        TSMethod methodWithOptionalReturnType = new TSMethod("test", vehicle, vehicle, new EmptyImplementationGenerator(), false, false);
        methodWithOptionalReturnType.setNullable(true);
        assertThat(
                serializerExtension.generateDeserializationCode("jsonData",methodWithOptionalReturnType)
        ).isEqualTo("jsonData && this.objectMapper.parse<Vehicle | null>(jsonData, {mainCreator: () => [Vehicle]})");
    }

    @Test
    public void testSerialization() {
        TSMethod tsMethod = new TSMethod("test", vehicle, vehicle, new EmptyImplementationGenerator(), false, false);

        assertThat(
                serializerExtension.generateSerializationCode("entity",
                        new TSParameter("test", new TSArray(vehicle), tsMethod, new EmptyImplementationGenerator())
                )
        ).isEqualTo("this.objectMapper.stringify<Vehicle[]>(entity)");

        assertThat(
                serializerExtension.generateSerializationCode("entity",
                        new TSParameter("test", vehicle, tsMethod, new EmptyImplementationGenerator())
                )
        ).isEqualTo("this.objectMapper.stringify<Vehicle>(entity)");

        TSParameter optionalParam = new TSParameter("test", vehicle, tsMethod, new EmptyImplementationGenerator());
        optionalParam.setOptional(true);
        assertThat(
                serializerExtension.generateSerializationCode("entity",optionalParam)
        ).isEqualTo("entity && this.objectMapper.stringify<Vehicle>(entity)");

        optionalParam.setOptional(false);
        optionalParam.setNullable(true);
        assertThat(
                serializerExtension.generateSerializationCode("entity",optionalParam)
        ).isEqualTo("entity && this.objectMapper.stringify<Vehicle | null>(entity)");
    }
}