package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.ClassNameMapper;
import com.blueveery.springrest2ts.GenerationContext;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class ModelClassToTsConverter extends ComplexTypeConverter {
    private ObjectMapper objectMapper;
    private GenerationContext generationContext;

    public ModelClassToTsConverter(ObjectMapper objectMapper, GenerationContext generationContext) {
        this.objectMapper = objectMapper;
        this.generationContext = generationContext;
    }

    public boolean preConverted(ModuleConverter moduleConverter, Class javaClass, ClassNameMapper classNameMapper) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            if (objectMapper.filterClass(javaClass)) {
                TSModule tsModule = moduleConverter.getTsModule(javaClass);
                TSInterface tsInterface = new TSInterface(classNameMapper.mapJavaClassNameToTs(javaClass.getSimpleName()), tsModule);
                tsModule.addScopedType(tsInterface);
                TypeMapper.registerTsType(javaClass, tsInterface);
                return true;
            }
        }
        return false;
    }

    @Override
    public void convert(Class javaClass) {
        TSInterface tsInterface = (TSInterface) TypeMapper.map(javaClass);
        if (!tsInterface.isConverted()) {
            tsInterface.setConverted(true);
            if (javaClass.getSuperclass() != Object.class) {
                TSType superClass = TypeMapper.map(javaClass.getSuperclass());
                if (superClass != TypeMapper.tsAny) {
                    tsInterface.addExtendsInterfaces((TSInterface) superClass);
                }
            }

            for (Field field : javaClass.getDeclaredFields()) {
                if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    if (objectMapper.filter(field, tsInterface)) {
                        List<TSField> tsFieldList = objectMapper.mapToField(field, tsInterface, this);
                        if (tsFieldList.size() == 1) {
                            setAsNullableType(field.getType(), field.getDeclaredAnnotations(), tsFieldList.get(0));
                        }
                        tsFieldList.forEach(tsField -> tsInterface.addTsField(tsField));
                    }
                }
            }

            for (Method method : javaClass.getDeclaredMethods()) {
                if (!Modifier.isStatic(method.getModifiers()) && isGetter(method)) {
                    if (objectMapper.filter(method, tsInterface)) {
                        List<TSField> tsFieldList = objectMapper.mapToField(method, tsInterface, this);
                        if (tsFieldList.size() == 1) {
                            setAsNullableType(method.getReturnType(), method.getDeclaredAnnotations(), tsFieldList.get(0));
                        }
                        tsFieldList.forEach(tsField -> tsInterface.addTsField(tsField));
                    }
                }
            }

            objectMapper.addTypeLevelSpecificFields(javaClass, tsInterface);
        }

    }

    private boolean isGetter(Method method) {
        if (method.getGenericParameterTypes().length != 0) {
            return false;
        }
        if (method.getReturnType() == Void.class) {
            return false;
        }
        if (!method.getName().startsWith("get") && !method.getName().startsWith("is")) {
            return false;
        }
        return true;
    }
}
