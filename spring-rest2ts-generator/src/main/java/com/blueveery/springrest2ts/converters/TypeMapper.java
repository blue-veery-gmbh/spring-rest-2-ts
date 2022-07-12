package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSMap;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;
import com.blueveery.springrest2ts.tsmodel.TSSimpleType;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSFormalTypeParameter;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TypeMapper {
    public static TSType tsVoid = new TSSimpleType("void");
    public static TSType tsNumber = new TSSimpleType("number");
    public static TSType tsString = new TSSimpleType("string");
    public static TSType tsBoolean = new TSSimpleType("boolean");
    public static TSType tsObject = new TSSimpleType("Object");
    public static TSType tsAny = new TSSimpleType("any");
    public static TSType tsNull = new TSSimpleType("null");
    public static TSType tsUndefined = new TSSimpleType("undefined");

    public static TSType tsObjectNumber = new TSSimpleType("Number");
    public static TSType tsObjectString = new TSSimpleType("String");
    public static TSType tsObjectBoolean = new TSSimpleType("Boolean");

    public static TSModule systemModule = new TSModule("system", Paths.get(""), true);

    public static TSClass tsArrayCollection;
    public static TSClass tsSet;
    public static TSClass tsMap;
    public static TSClass tsDate;
    static {
        tsArrayCollection = new TSClass("Array", systemModule, new EmptyImplementationGenerator(), new TSFormalTypeParameter("T"));
        tsArrayCollection.getMappedFromJavaTypeSet().add(Collection.class);

        tsSet = new TSClass("Set", systemModule, new EmptyImplementationGenerator(), new TSFormalTypeParameter("T"));
        tsSet.getMappedFromJavaTypeSet().add(Set.class);

        tsMap = new TSClass(
                "Map", systemModule, new EmptyImplementationGenerator(),
                new TSFormalTypeParameter("K"), new TSFormalTypeParameter("V")
        );
        tsMap.getMappedFromJavaTypeSet().add(Map.class);
        tsDate = new TSClass("Date", systemModule, new EmptyImplementationGenerator());
    }

    private static Map<Class, TSType> complexTypeMap = new HashMap<>();
    private static Map<Class, MappingAction> complexTypeMappingActions = new HashMap<>();

    public static Map<Class, TSComplexElement> complexTypeMapForClassHierarchy = new HashMap<>();

    public static void resetTypeMapping() {
        complexTypeMap.clear();
        complexTypeMapForClassHierarchy.clear();
        complexTypeMappingActions.clear();
    }

    public static TSType map(Type javaType) {
        return map(javaType, tsAny, Collections.emptyMap());
    }

    public static TSType map(Type javaType, Map<TypeVariable, Type> typeParametersMap) {
        return map(javaType, tsAny, typeParametersMap);
    }

    public static TSType map(Type javaType, TSType fallbackType,
        Map<TypeVariable, Type> typeParametersMap) {
        if (javaType instanceof TypeVariable) {
            Type actualType = typeParametersMap.get(javaType);
            if (actualType != null) {
                return map(actualType, fallbackType, typeParametersMap);
            }
            TypeVariable typeVariable = (TypeVariable) javaType;
            return new TSFormalTypeParameter(typeVariable.getName());
        }
        Type javaRawType = javaType;
        List<TSType> actualParameterList = new ArrayList<>();
        if (javaType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) javaType;
            javaRawType = parameterizedType.getRawType();
            actualParameterList = mapActualTypeArguments(actualParameterList, parameterizedType, typeParametersMap);
        }

        if (complexTypeMap.containsKey(javaRawType)) {
            TSType tsType = complexTypeMap.get(javaRawType);
            return wrapTypeInTypeReference(tsType, actualParameterList);
        }

        Optional<Class> hierarchyRoot = findNearestHierarchyRoot(complexTypeMapForClassHierarchy.keySet(), javaRawType);
        if (hierarchyRoot.isPresent()) {
            TSComplexElement tsType = complexTypeMapForClassHierarchy.get(hierarchyRoot.get());
            return wrapTypeInTypeReference(tsType, actualParameterList);
        }

        if (complexTypeMappingActions.containsKey(javaRawType)) {
            MappingAction mappingAction = complexTypeMappingActions.get(javaRawType);
            return mappingAction.map(javaType);
        }

        if (Object.class == javaRawType) {
            return tsObject;
        }
        if (void.class == javaRawType || Void.class == javaRawType) {
            return tsVoid;
        }
        if (String.class == javaRawType || char.class == javaRawType || Character.class == javaRawType) {
            return tsString;
        }
        if (boolean.class == javaRawType || Boolean.class == javaRawType) {
            return tsBoolean;
        }
        if (javaRawType instanceof Class) {
            Class javaClass = (Class) javaRawType;
            if (Number.class.isAssignableFrom(javaClass)) {
                return tsNumber;
            }
            if (javaClass.isAssignableFrom(Date.class)) {
                return tsDate;
            }
            if (javaClass.isArray()) {
                return new TSArray(TypeMapper.map(javaClass.getComponentType(), fallbackType, typeParametersMap));
            }

            if (javaClass.isPrimitive()) {
                return tsNumber;
            }
        }

        if (javaType instanceof ParameterizedType) {
            ParameterizedType javaParameterizedType = (ParameterizedType) javaType;
            if (Collection.class.isAssignableFrom((Class<?>) javaParameterizedType.getRawType())) {
                return new TSArray(TypeMapper.map(javaParameterizedType.getActualTypeArguments()[0], fallbackType, typeParametersMap));
            }

            if (Map.class.isAssignableFrom((Class<?>) javaParameterizedType.getRawType())) {
                return new TSMap(TypeMapper.map(javaParameterizedType.getActualTypeArguments()[1], fallbackType, typeParametersMap));
            }

            if (Optional.class == javaParameterizedType.getRawType()) {
                return TypeMapper.map(javaParameterizedType.getActualTypeArguments()[0], fallbackType, typeParametersMap);
            }
        }

        return fallbackType;
    }

    public static Optional<Class> findNearestHierarchyRoot(Set<Class> roots, Type currentType) {
        class TypeDistance implements Comparable<TypeDistance> {
            public final Class type;
            public final int distance;

            public TypeDistance(Class type, int distance) {
                this.type = type;
                this.distance = distance;
            }

            @Override
            public int compareTo(TypeDistance other) {
                return distance - other.distance;
            }
        }
        if (!(currentType instanceof Class)) {
            return Optional.empty();
        }
        return roots.stream().map(
                    r -> new TypeDistance(r, countTypeDistance(r, (Class) currentType))
                ).filter(td -> td.distance < Integer.MAX_VALUE).sorted().map(d -> d.type).findFirst();
    }

    public static int countTypeDistance(Class root, Class currentType) {
        if (!root.isAssignableFrom(currentType)) {
            return Integer.MAX_VALUE;
        }
        if (root != currentType) {
            if (currentType.getSuperclass() != null && root.isAssignableFrom(currentType.getSuperclass())) {
                return countTypeDistance(root, currentType.getSuperclass()) + 1;
            } else {
                for (Class anInterface : currentType.getInterfaces()) {
                    if (anInterface.isAssignableFrom(currentType)) {
                        return countTypeDistance(root, anInterface) + 1;
                    }
                }
            }
        }
        return 0;
    }

    private static TSType wrapTypeInTypeReference(TSType tsType, List<TSType> actualParameterList) {
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

    public static TSType getTypeObjectTypeVersion(TSType tsType) {
        if (tsType instanceof TSSimpleType) {
            if (tsType == tsNumber) {
                return tsObjectNumber;
            }

            if (tsType == tsBoolean) {
                return tsObjectBoolean;
            }

            if (tsType == tsString) {
                return tsObjectString;
            }
        }
        return tsType;
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

    public static void registerTsType(Class javaType, TSType tsType) {
        if (!complexTypeMap.containsKey(javaType)) {
            complexTypeMap.put(javaType, tsType);
        }
        if (tsType instanceof TSScopedElement) {
            TSScopedElement tsScopedElement = (TSScopedElement) tsType;
            tsScopedElement.getMappedFromJavaTypeSet().add(javaType);
        }
    }

    public static void registerMappingAction(Class javaType, MappingAction mappingAction) {
        if (!complexTypeMappingActions.containsKey(javaType)) {
            complexTypeMappingActions.put(javaType, mappingAction);
        }
    }
}

