package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.spring.RequestMappingUtility;
import com.blueveery.springrest2ts.tsmodel.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.*;
import java.util.Arrays;

public class SpringRestToTsConverter extends SpringAnnotationsBasedRestClassConverter{

    public SpringRestToTsConverter(ImplementationGenerator implementationGenerator) {
        super(implementationGenerator);
    }

    public SpringRestToTsConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper) {
        super(implementationGenerator, classNameMapper);
    }

    @Override
    protected void addClassAnnotations(Class javaClass, TSClass tsClass) {
        tsClass.addAllAnnotations(javaClass.getAnnotations());
    }

    @Override
    protected void addMethodAnnotations(Method method, TSMethod tsMethod) {
        tsMethod.addAllAnnotations(method.getAnnotations());
    }

    @Override
    protected void addParameterAnnotations(Parameter parameter, TSParameter tsParameter) {
        tsParameter.addAllAnnotations(parameter.getAnnotations());
    }

    @Override
    protected Type handleImplementationSpecificReturnTypes(Method method) {
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType) {// handling ResponseEntity
            ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
            if (parameterizedType.getRawType() == ResponseEntity.class) {
                genericReturnType = parameterizedType.getActualTypeArguments()[findPathCommonPrefixIndex(null)];
            }
        }
        return genericReturnType;
    }

    @Override
    protected RequestMapping getRequestMappingForMethod(Method method) {
        return RequestMappingUtility.getRequestMapping(Arrays.asList (method.getDeclaredAnnotations()));
    }
}
