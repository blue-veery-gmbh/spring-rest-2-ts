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
import java.util.Optional;
import java.util.SortedSet;

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
        checkJsonClassType("keyNumber", Keyboard.class, new TSTypeLiteral(TypeMapper.tsObjectNumber));
    }

    @Test
    public void stringFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        checkJsonClassType("name", Product.class, new TSTypeLiteral(TypeMapper.tsObjectString));
    }

    @Test
    public void booleanFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        checkJsonClassType("isAdmin", User.class, new TSTypeLiteral(TypeMapper.tsObjectBoolean));
    }

    @Test
    public void objectFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        TSComplexElement tsKeyboard = findTSComplexElement(tsModules, Keyboard.class.getSimpleName());
        checkJsonClassType(
                "keyboard", Product.class, new TSTypeLiteral(tsKeyboard), tsModules
        );
    }

    @Test
    public void nullableFieldShouldHaveJsonClassTypeWithCorrectType() throws IOException {
        checkJsonClassType("nullableField", Product.class, new TSTypeLiteral(TypeMapper.tsObjectNumber));
    }

    private void checkJsonClassType(
            String fieldName, Class javaClass, ILiteral expectedTypeLiteral
    ) throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        checkJsonClassType(fieldName, javaClass, expectedTypeLiteral, tsModules);
    }

    private void checkJsonClassType(
            String fieldName, Class javaClass, ILiteral expectedTypeLiteral, SortedSet<TSModule> tsModules
    ) throws IOException {
        TSClass tsField = (TSClass) findTSComplexElement(tsModules, javaClass.getSimpleName());
        Optional<TSDecorator> jsonClassType = tsField.getFieldByName(fieldName).getTsDecoratorList()
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
        printClass(tsField);
    }

    private void printClass(TSClass tsClass) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        tsClass.write(writer);
        writer.flush();
    }
}