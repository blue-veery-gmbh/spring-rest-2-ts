package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.*;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSFormalTypeParameter;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TypeMapper {
    public static TSType tsVoid = new TSSimpleType("void");
    public static TSType tsNumber = new TSSimpleType("number");
    public static TSType tsString = new TSSimpleType("string");
    public static TSType tsBoolean  = new TSSimpleType("boolean");
    public static TSType tsDate  = new TSSimpleType("Date");
    public static TSType tsObject  = new TSSimpleType("Object");
    public static TSType tsAny = new TSSimpleType("any");
    public static TSType tsNull = new TSSimpleType("null");
    public static TSType tsUndefined = new TSSimpleType("undefined");
    public static TSModule systemModule = new TSModule("system", Paths.get(""), true);

    private static Map<Class, TSType> complexTypeMap = new HashMap<>();

    public static TSType map(Type javaType){
        return map(javaType, tsAny, Collections.emptyMap());
    }

    public static TSType map(Type javaType, Map<TypeVariable, Type> typeParametersMap){
        return map(javaType, tsAny, typeParametersMap);
    }

    public static TSType map(Type javaType, TSType fallbackType, Map<TypeVariable, Type> typeParametersMap){
        if (javaType instanceof TypeVariable) {
            Type actualType = typeParametersMap.get(javaType);
            if(actualType != null) {
                return map(actualType, fallbackType, typeParametersMap);
            }
            TypeVariable typeVariable = (TypeVariable) javaType;
            return new TSFormalTypeParameter(typeVariable.getName());
        }
        Type javaRawType = javaType;
        List<TSType> actualParameterList = new ArrayList<>();
        if(javaType instanceof ParameterizedType){
            ParameterizedType parameterizedType = (ParameterizedType) javaType;
            javaRawType = parameterizedType.getRawType();
            actualParameterList = mapActualTypeArguments(actualParameterList, parameterizedType, typeParametersMap);
        }

        if(complexTypeMap.containsKey(javaRawType)){
            TSType tsType = complexTypeMap.get(javaRawType);
            if (tsType instanceof TSInterface) {
                TSInterface tsInterface = (TSInterface) tsType;
                return new TSInterfaceReference(tsInterface, actualParameterList);
            }
            if (tsType instanceof TSClass) {
                TSClass tsClass = (TSClass) tsType;
                return new TSClassReference(tsClass, actualParameterList);
            }
            return tsType;
        }

        if(Object.class == javaRawType){
            return tsObject;
        }
        if (void.class == javaRawType || Void.class == javaRawType) {
            return tsVoid;
        }
        if(String.class == javaRawType || char.class == javaRawType || Character.class == javaRawType){
            return tsString;
        }
        if(boolean.class == javaRawType || Boolean.class == javaRawType){
            return tsBoolean;
        }
        if(javaRawType instanceof Class){
            Class javaClass = (Class) javaRawType;
            if(Number.class.isAssignableFrom(javaClass)){
                return tsNumber;
            }
            if(javaClass.isAssignableFrom(Date.class)){
                return tsDate;
            }
            if(javaClass.isArray()){
                return new TSArray(TypeMapper.map(javaClass.getComponentType(), fallbackType, typeParametersMap));
            }

            if(javaClass.isPrimitive()){
                return tsNumber;
            }
        }

        if(javaType instanceof ParameterizedType){
            ParameterizedType javaParameterizedType = (ParameterizedType) javaType;
            if(Collection.class.isAssignableFrom((Class<?>) javaParameterizedType.getRawType())){
                return new TSArray(TypeMapper.map(javaParameterizedType.getActualTypeArguments()[0], fallbackType, typeParametersMap));
            }

            if(Map.class.isAssignableFrom((Class<?>) javaParameterizedType.getRawType())){
                return new TSMap(TypeMapper.map(javaParameterizedType.getActualTypeArguments()[1], fallbackType, typeParametersMap));
            }

            if(Optional.class == javaParameterizedType.getRawType()){
                return TypeMapper.map(javaParameterizedType.getActualTypeArguments()[0], fallbackType, typeParametersMap);
            }
        }

        return fallbackType;
    }

    private static List<TSType> mapActualTypeArguments(
            List<TSType> actualParameterList, ParameterizedType parameterizedType,
            Map<TypeVariable, Type> typeParametersMap
    ) {
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length > 0) {
            actualParameterList = new ArrayList<>();
            for (Type actualTypeArgument : actualTypeArguments) {
                actualParameterList.add(map(actualTypeArgument, typeParametersMap));
            }
        }
        return actualParameterList;
    }

    public static void registerTsType(Class javaType, TSType tsType){
        if(!complexTypeMap.containsKey(javaType)){
            complexTypeMap.put(javaType, tsType);
        }
        if (tsType instanceof TSScopedElement) {
            TSScopedElement tsScopedElement = (TSScopedElement) tsType;
            tsScopedElement.getMappedFromJavaTypeSet().add(javaType);
        }
    }
}

