package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSComplexType;
import com.blueveery.springrest2ts.tsmodel.TSField;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;

public interface ObjectMapper {
    void addTypeLevelSpecificFields(Class javaType, TSComplexType tsComplexType);

    boolean filterClass(Class clazz);

    boolean filter(Field field);
    boolean filter(Method method, boolean isGetter);

    List<TSField> mapToField(Property field, TSComplexType tsComplexType, ComplexTypeConverter complexTypeConverter);


    String getPropertyName(Field field);

    String getPropertyName(Method method, boolean isGetter);
}
