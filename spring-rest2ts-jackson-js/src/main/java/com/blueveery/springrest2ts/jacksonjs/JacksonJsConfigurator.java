package com.blueveery.springrest2ts.jacksonjs;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.JacksonAnnotationsConversionToJacksonJs;
import com.blueveery.springrest2ts.converters.JacksonObjectMapper;
import com.blueveery.springrest2ts.converters.ModelClassesToTsClassesConverter;
import com.blueveery.springrest2ts.extensions.JacksonJsModelSerializerExtension;
import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static com.blueveery.springrest2ts.converters.TypeMapper.tsArrayCollection;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsDate;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsMap;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsSet;

public class JacksonJsConfigurator {
    public static void configure(
            Rest2tsGenerator tsGenerator,
            JacksonObjectMapper jacksonObjectMapper,
            BiFunction<Class, Class, String> typeIdResolver
    ) {
        ModelClassesToTsClassesConverter modelClassesConverter = new ModelClassesToTsClassesConverter(new EmptyImplementationGenerator(), jacksonObjectMapper);
        JacksonAnnotationsConversionToJacksonJs conversionToJacksonJs = new JacksonAnnotationsConversionToJacksonJs(typeIdResolver);
        modelClassesConverter.getConversionListener().getConversionListenerSet().add(conversionToJacksonJs);

        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Map.class, tsMap);
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Set.class, tsSet);
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Collection.class, tsArrayCollection);
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Date.class, tsDate);

        tsGenerator.setModelClassesConverter(modelClassesConverter);
        tsGenerator.getRestClassesConverter().getImplementationGenerator().setSerializationExtension(new JacksonJsModelSerializerExtension());
    }
}
