package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSComplexType;
import com.blueveery.springrest2ts.tsmodel.TSField;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;

public interface ObjectMapper {
    void addTypeLevelSpecificFields(Class javaType, TSComplexType tsComplexType);

    boolean filter(Member member, TSComplexType tsComplexType);

    boolean filterClass(Class clazz);

    List<TSField> mapToField(Field field, TSComplexType tsComplexType, ComplexTypeConverter complexTypeConverter);

    List<TSField> mapToField(Method method, TSComplexType tsComplexType, ComplexTypeConverter complexTypeConverter);
}
