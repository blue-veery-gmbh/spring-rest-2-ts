package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
        return map(javaType, tsAny);
    }

    public static TSType map(Type javaType, TSType fallbackType){
        if(complexTypeMap.containsKey(javaType)){
            return complexTypeMap.get(javaType);
        }

        if(Object.class == javaType){
            return tsObject;
        }
        if(void.class == javaType){
            return tsVoid;
        }
        if(String.class == javaType || char.class == javaType || Character.class == javaType){
            return tsString;
        }
        if(boolean.class == javaType || Boolean.class == javaType){
            return tsBoolean;
        }
        if(javaType instanceof Class){
            Class javaClass = (Class) javaType;
            if(Number.class.isAssignableFrom(javaClass)){
                return tsNumber;
            }
            if(javaClass.isAssignableFrom(Date.class)){
                return tsDate;
            }
            if(javaClass.isArray()){
                return new TSArray(TypeMapper.map(javaClass.getComponentType()));
            }

            if(javaClass.isPrimitive()){
                return tsNumber;
            }
        }

        if(javaType instanceof ParameterizedType){
            ParameterizedType javaParameterizedType = (ParameterizedType) javaType;
            if(Collection.class.isAssignableFrom((Class<?>) javaParameterizedType.getRawType())){
                return new TSArray(TypeMapper.map(javaParameterizedType.getActualTypeArguments()[0], fallbackType));
            }

            if(Map.class.isAssignableFrom((Class<?>) javaParameterizedType.getRawType())){
                return new TSMap(TypeMapper.map(javaParameterizedType.getActualTypeArguments()[1], fallbackType));
            }

            if(Optional.class == javaParameterizedType.getRawType()){
                return TypeMapper.map(javaParameterizedType.getActualTypeArguments()[0], fallbackType);
            }
        }

        return fallbackType;
    }

    public static void registerTsType(Class javaType, TSType tsType){
        if(!complexTypeMap.containsKey(javaType)){
            complexTypeMap.put(javaType, tsType);
        }
    }
}

