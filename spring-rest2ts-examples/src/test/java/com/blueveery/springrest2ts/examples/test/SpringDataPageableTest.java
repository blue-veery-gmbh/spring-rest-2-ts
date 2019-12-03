package com.blueveery.springrest2ts.examples.test;

import com.blueveery.springrest2ts.converters.JacksonObjectMapper;
import com.blueveery.springrest2ts.converters.ModelClassesToTsInterfacesConverter;
import com.blueveery.springrest2ts.examples.model.core.ParametrizedBaseDTO;
import com.blueveery.springrest2ts.filters.ExtendsJavaTypeFilter;
import com.blueveery.springrest2ts.filters.JavaTypeFilter;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.filters.OrFilterOperator;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.Test;
import org.springframework.data.domain.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SpringDataPageableTest extends TsCodeGenerationsTest {

    @Test
    public void controllerWithPageableParam() throws IOException {
        JavaTypeFilter modelClassFilter = new ExtendsJavaTypeFilter(ParametrizedBaseDTO.class);
        Set<Class> springDataTypes = new HashSet<>();
        springDataTypes.add(Slice.class);
        springDataTypes.add(Page.class);
        springDataTypes.add(Pageable.class);
        springDataTypes.add(Sort.class);
        springDataTypes.add(Sort.Order.class);
        JavaTypeFilter springDataTypesFilter = new JavaTypeSetFilter(springDataTypes);
        JavaTypeFilter modelClassesCondition = new OrFilterOperator(Arrays.asList(modelClassFilter, springDataTypesFilter));
        tsGenerator.setModelClassesCondition(modelClassesCondition);
        javaPackageSet = new HashSet<>();
        Collections.addAll(javaPackageSet,"com.blueveery.springrest2ts.examples", "org.springframework.data.domain");

        JacksonObjectMapper jacksonObjectMapperForSpringData = new JacksonObjectMapper();
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        modelClassesConverter = new ModelClassesToTsInterfacesConverter(jacksonObjectMapper);
        modelClassesConverter.getObjectMapperMap().put("org.springframework.data", jacksonObjectMapperForSpringData);
        tsGenerator.setModelClassesConverter(modelClassesConverter);

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }
}
