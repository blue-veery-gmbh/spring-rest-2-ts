package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.tests.BaseTest;
import com.blueveery.springrest2ts.tests.model.ExtendedKeyboard;
import com.blueveery.springrest2ts.tests.model.Keyboard;
import com.blueveery.springrest2ts.tests.model.KeyboardInterface;
import com.blueveery.springrest2ts.tests.model.Product;
import com.blueveery.springrest2ts.tests.model.User;
import com.blueveery.springrest2ts.tsmodel.ILiteral;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSArrowFunctionLiteral;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteralArray;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSTypeLiteral;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import static com.blueveery.springrest2ts.converters.TypeMapper.tsDate;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsMap;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsObject;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsObjectBoolean;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsObjectNumber;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsObjectString;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsSet;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsString;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeBasedJacksonJsConversionTest extends BaseTest<JacksonObjectMapper> {

    private TypeBasedJacksonJsConversion typeBasedJacksonJsConversion;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        tsGenerator.setModelClassesCondition(
                new JavaTypeSetFilter(Product.class, Keyboard.class, ExtendedKeyboard.class, KeyboardInterface.class, User.class)
        );
    }

    @Override
    protected JacksonObjectMapper createObjectMapper() {
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        return jacksonObjectMapper;
    }

    @Override
    protected ModelClassesAbstractConverter getModelClassesConverter() {
        ModelClassesToTsClassesConverter modelClassesToTsClassesConverter = new ModelClassesToTsClassesConverter(new EmptyImplementationGenerator(), objectMapper);
        typeBasedJacksonJsConversion = new TypeBasedJacksonJsConversion();
        modelClassesToTsClassesConverter.getConversionListener().getConversionListenerSet().add(typeBasedJacksonJsConversion);
        return modelClassesToTsClassesConverter;
    }

    @Test
    public void fieldShouldHaveJsonPropertyDecorator() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSClass keyboard = (TSClass) findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        assertThat(keyboard.getTsFields().first().getTsDecoratorList()).contains(typeBasedJacksonJsConversion.jsonProperty);
        printClass(keyboard);
    }

    @Test
    public void numberFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        checkJsonClassType("keyNumber", Keyboard.class, new TSTypeLiteral(tsObjectNumber));
    }

    @Test
    public void stringFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        checkJsonClassType("name", Product.class, new TSTypeLiteral(tsObjectString));
    }

    @Test
    public void booleanFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        checkJsonClassType("isAdmin", User.class, new TSTypeLiteral(tsObjectBoolean));
    }

    @Test
    public void objectFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement tsKeyboard = findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        checkJsonClassType(
                tsModules, "keyboard", Product.class, new TSTypeLiteral(tsKeyboard)
        );
    }

    @Test
    public void nullableFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        checkJsonClassType("nullableField", Product.class, new TSTypeLiteral(tsObjectNumber));
    }

    @Test
    public void stringArrayFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        ILiteral[] expectedTypeLiteral = {new TSTypeLiteral(new TSArray(tsString)), new TSLiteralArray(new TSTypeLiteral(tsObjectString))};
        checkJsonClassType("roleList", User.class, expectedTypeLiteral);
    }

    @Test
    public void objectArrayFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement tsExtendedKeyboard = findTSComplexElement(tsModules, ExtendedKeyboard.class.getSimpleName());
        ILiteral[] expectedTypeLiteral = {new TSTypeLiteral(new TSArray(tsExtendedKeyboard)), new TSLiteralArray(new TSTypeLiteral(tsExtendedKeyboard))};
        checkJsonClassType("extendedKeyboards", Product.class, expectedTypeLiteral);
    }

    @Test
    public void dateFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        checkJsonClassType("joinDate", User.class, new TSTypeLiteral(tsDate));
    }

    @Test
    public void setOfStringsFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Set.class, tsSet);
        ILiteral[] expectedTypeLiteral = {new TSTypeLiteral(tsSet), new TSLiteralArray(new TSTypeLiteral(tsObjectString))};
        checkJsonClassType("tagsSet", User.class, expectedTypeLiteral);
    }

    @Test
    public void hashSetOfStringsFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Set.class, tsSet);
        ILiteral[] expectedTypeLiteral = {new TSTypeLiteral(tsSet), new TSLiteralArray(new TSTypeLiteral(tsObject))};
        checkJsonClassType("tagsHashSet", User.class, expectedTypeLiteral);
    }

    @Test
    public void mapOfStringsFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Map.class, tsMap);
        TSTypeLiteral tsStringTypeLiteral = new TSTypeLiteral(tsObjectString);
        ILiteral[] expectedTypeLiteral = {new TSTypeLiteral(tsMap), new TSLiteralArray(tsStringTypeLiteral, tsStringTypeLiteral)};
        checkJsonClassType("tagsMap", User.class, expectedTypeLiteral);
    }

    @Test
    public void mapOfNumbersFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Map.class, tsMap);
        ILiteral[] expectedTypeLiteral = {new TSTypeLiteral(tsMap), new TSLiteralArray(new TSTypeLiteral(tsObjectString), new TSTypeLiteral(tsObjectNumber))};
        checkJsonClassType("numbersMap", User.class, expectedTypeLiteral);
    }

    @Test
    public void mapOfDatesFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        tsGenerator.getCustomTypeMappingForClassHierarchy().put(Map.class, tsMap);
        ILiteral[] expectedTypeLiteral = {new TSTypeLiteral(tsMap), new TSLiteralArray(new TSTypeLiteral(tsObjectString), new TSTypeLiteral(tsDate))};
        checkJsonClassType("datesMap", User.class, expectedTypeLiteral);
    }

    private void checkJsonClassType(
            String fieldName, Class<?> javaClass, ILiteral... expectedTypeLiteral
    ) throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        checkJsonClassType(tsModules, fieldName, javaClass, expectedTypeLiteral);
    }

    private void checkJsonClassType(
            SortedSet<TSModule> tsModules, String fieldName, Class<?> javaClass, ILiteral... expectedTypeLiteral
    ) throws IOException {
        TSClass tsClass = (TSClass) findTSComplexElement(tsModules, javaClass.getSimpleName());
        Optional<TSDecorator> jsonClassType = tsClass.getFieldByName(fieldName).getTsDecoratorList()
                .stream()
                .filter(d -> d.getTsFunction() == typeBasedJacksonJsConversion.jsonClassTypeFunction)
                .findFirst();
        assertThat(jsonClassType).isPresent();
        TSJsonLiteral actual = (TSJsonLiteral) jsonClassType.get().getTsLiteralList().get(0);
        assertThat(actual.getFieldMap().get("type")).isNotNull();
        assertThat(actual.getFieldMap().get("type")).isInstanceOf(TSArrowFunctionLiteral.class);
        ILiteral typesArray = ((TSArrowFunctionLiteral) actual.getFieldMap().get("type")).getReturnValue();
        assertThat(typesArray).isInstanceOf(TSLiteralArray.class);
        assertThat(((TSLiteralArray) typesArray).getLiteralList()).containsExactly(expectedTypeLiteral);
        printClass(tsClass);
    }

    private void printClass(TSClass tsClass) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        tsClass.write(writer);
        writer.flush();
    }
}