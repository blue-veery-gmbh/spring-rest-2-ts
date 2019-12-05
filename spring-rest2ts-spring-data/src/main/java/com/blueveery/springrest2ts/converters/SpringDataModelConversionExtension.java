package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.extensions.ModelConversionExtension;
import com.blueveery.springrest2ts.filters.JavaTypeFilter;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.*;

public class SpringDataModelConversionExtension implements ModelConversionExtension {
    public JavaTypeFilter getJavaTypeFilter() {
        Set<Class> springDataTypes = new HashSet<>();
        springDataTypes.add(Slice.class);
        springDataTypes.add(Page.class);
        springDataTypes.add(Pageable.class);
        springDataTypes.add(Sort.class);
        springDataTypes.add(Sort.Order.class);
        JavaTypeFilter springDataTypesFilter = new JavaTypeSetFilter(springDataTypes);
        return springDataTypesFilter;
    }

    @Override
    public Set<String> getAdditionalJavaPackages() {
        return Collections.singleton("org.springframework.data.domain");
    }

    @Override
    public Map<String, ObjectMapper> getObjectMapperMap() {
        JacksonObjectMapper jacksonObjectMapperForSpringData = new JacksonObjectMapper();
        jacksonObjectMapperForSpringData.setFieldsVisibility(JsonAutoDetect.Visibility.NONE);
        Map<String, ObjectMapper> objectMapperMap = new HashMap<>();
        objectMapperMap.put("org.springframework.data", jacksonObjectMapperForSpringData);
        return objectMapperMap;
    }

    @Override
    public void tsFieldCreated(Property property, TSField tsField) {
        boolean containsPage = tsField.getOwner().getMappedFromJavaTypeSet().contains(Pageable.class);
        if (containsPage) {
            if (tsField.getName().equals("pageNumber") || tsField.getName().equals("pageSize")) {
                tsField.setReadOnly(false);
            } else {
                tsField.setOptional(true);
            }

            if (tsField.getName().equals("sort")){
                TSType sortOrderType = TypeMapper.map(Sort.Order.class);
                tsField.setType(new TSArray(sortOrderType));
                tsField.getOwner().addScopedTypeUsage(sortOrderType);
                tsField.setReadOnly(false);
                tsField.setOptional(true);
            }
        }
    }
}
