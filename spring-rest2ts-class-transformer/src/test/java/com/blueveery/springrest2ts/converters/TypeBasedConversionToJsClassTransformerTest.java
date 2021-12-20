package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.tests.BaseTest;
import com.blueveery.springrest2ts.tests.model.ExtendedKeyboard;
import com.blueveery.springrest2ts.tests.model.Keyboard;
import com.blueveery.springrest2ts.tests.model.KeyboardInterface;
import com.blueveery.springrest2ts.tests.model.Product;
import com.blueveery.springrest2ts.tests.model.User;
import com.blueveery.springrest2ts.tsmodel.ILiteral;
import com.blueveery.springrest2ts.tsmodel.TSArrowFunctionLiteral;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSImport;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSTypeLiteral;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import static com.blueveery.springrest2ts.converters.TypeMapper.tsDate;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsMap;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsObject;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsObjectNumber;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsObjectString;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsSet;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeBasedConversionToJsClassTransformerTest extends BaseTest<JacksonObjectMapper> {
    protected TypeBasedConversionToJsClassTransformer typeBasedConversionToJsClassTransformer;

    @Override
    protected JacksonObjectMapper createObjectMapper() {
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        return jacksonObjectMapper;
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();
        tsGenerator.setModelClassesCondition(
                new JavaTypeSetFilter(Product.class, Keyboard.class, ExtendedKeyboard.class, KeyboardInterface.class, User.class)
        );
        typeBasedConversionToJsClassTransformer = new TypeBasedConversionToJsClassTransformer();
        modelClassesConverter.getConversionListener().getConversionListenerSet().add(typeBasedConversionToJsClassTransformer);
    }

    @Test
    public void numberFieldShouldNotHaveTypeDecorator() throws IOException {
        typeDecoratorShouldBeAbsent("keyNumber", Keyboard.class);
    }

    @Test
    public void stringFieldShouldNotHaveTypeDecorator() throws IOException {
        typeDecoratorShouldBeAbsent("name", Product.class);
    }

    @Test
    public void booleanFieldShouldNotHaveTypeDecorator() throws IOException {
        typeDecoratorShouldBeAbsent("isAdmin", User.class);
    }

    @Test
    public void objectFieldShouldHaveTypeDecoratorWithCorrectType() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement tsKeyboard = findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        checkTypeDecorator(tsModules, "keyboard", Product.class, new TSTypeLiteral(tsKeyboard));
    }

    @Test
    public void nullableFieldShouldNotHaveTypeDecorator() throws IOException {
        typeDecoratorShouldBeAbsent("nullableField", Product.class);
    }

    @Test
    public void stringArrayFieldShouldHaveTypeDecoratorWithCorrectType() throws IOException {
        checkTypeDecorator("roleList", User.class, new TSTypeLiteral(tsObjectString));
    }

    @Test
    public void objectArrayFieldShouldHaveTypeDecoratorWithCorrectType() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement tsExtendedKeyboard = findTSComplexElement(tsModules, ExtendedKeyboard.class.getSimpleName());
        checkTypeDecorator("extendedKeyboards", Product.class, new TSTypeLiteral(tsExtendedKeyboard));
    }

    @Test
    public void dateFieldShouldHaveTypeDecoratorWithCorrectType() throws IOException {
        checkTypeDecorator("joinDate", User.class, new TSTypeLiteral(tsDate));
    }

    @Test
    public void setOfStringsFieldShouldHaveTypeDecoratorWithCorrectType() throws IOException {
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Set.class, tsSet);
        checkTypeDecorator("tagsSet", User.class, new TSTypeLiteral(tsObjectString));
    }

    @Test
    public void hashSetOfStringsFieldShouldHaveTypeDecoratorWithCorrectType() throws IOException {
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Set.class, tsSet);
        checkTypeDecorator("tagsHashSet", User.class, new TSTypeLiteral(tsObject));
    }

    @Test
    public void mapOfStringsFieldShouldHaveTypeDecoratorWithCorrectType() throws IOException {
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Map.class, tsMap);
        checkTypeDecorator("tagsMap", User.class,  new TSTypeLiteral(tsObjectString));
    }

    @Test
    public void mapOfNumbersFieldShouldHaveTypeDecoratorWithCorrectType() throws IOException {
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Map.class, tsMap);
        checkTypeDecorator("numbersMap", User.class, new TSTypeLiteral(tsObjectNumber));
    }

    @Test
    public void mapOfDatesFieldShouldHaveTypeDecoratorWithCorrectType() throws IOException {
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Map.class, tsMap);
        checkTypeDecorator("datesMap", User.class, new TSTypeLiteral(tsDate));
    }

    @Test
    public void mapConvertedToObjectShouldHaveTypeDecoratorWithCorrectType() throws IOException {
        checkTypeDecorator("datesMap", User.class, new TSTypeLiteral(tsObject));
    }

    @Test
    public void anyTypeShouldHaveTypeDecoratorWithCorrectType() throws IOException {
        checkTypeDecorator("tagsHashSet", User.class, new TSTypeLiteral(tsObject));
    }

    @Test
    public void decoratorsShouldBeImportedWhenAdded() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSImport tsImport = tsModules.first().getImportMap().get(typeBasedConversionToJsClassTransformer.jsClassTransformerModule);
        assertThat(tsImport).isNotNull();
        printTSElement(tsModules.first());
    }

    @Test
    public void interfaceFieldShouldNotHaveDecorators() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSInterface tsInterface = (TSInterface) findTSComplexElement(tsModules, KeyboardInterface.class.getSimpleName());
        assertThat(tsInterface.getTsFields().first().getTsDecoratorList()).isEmpty();
    }

    private void typeDecoratorShouldBeAbsent(String fieldName, Class<?> javaClass) throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass tsClass = (TSClass) findTSComplexElement(tsModules, javaClass.getSimpleName());
        Optional<TSDecorator> typeDecorator = findDecorator(typeBasedConversionToJsClassTransformer.typeFunction, tsClass.getFieldByName(fieldName).getTsDecoratorList());
        assertThat(typeDecorator).isEmpty();
    }

    private void checkTypeDecorator(
            String fieldName, Class<?> javaClass, ILiteral expectedTypeLiteral
    ) throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        checkTypeDecorator(tsModules, fieldName, javaClass, expectedTypeLiteral);
    }

    private void checkTypeDecorator(
            SortedSet<TSModule> tsModules, String fieldName, Class<?> javaClass, ILiteral expectedTypeLiteral
    ) throws IOException {
        TSClass tsClass = (TSClass) findTSComplexElement(tsModules, javaClass.getSimpleName());
        Optional<TSDecorator> typeDecorator = findDecorator(typeBasedConversionToJsClassTransformer.typeFunction, tsClass.getFieldByName(fieldName).getTsDecoratorList());
        assertThat(typeDecorator).isPresent();
        ILiteral actual = ((TSArrowFunctionLiteral) typeDecorator.get().getTsLiteralList().get(0)).getReturnValue();
        assertThat(actual).isEqualTo(expectedTypeLiteral);
        printTSElement(tsClass);
    }
}