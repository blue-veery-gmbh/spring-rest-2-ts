package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tests.BaseTest;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSElement;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Optional;

public class JacksonJsTest extends BaseTest<JacksonObjectMapper> {
    @Override
    protected JacksonObjectMapper createObjectMapper() {
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        return jacksonObjectMapper;
    }

    protected void printTSElement(TSElement tsClass) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        tsClass.write(writer);
        writer.flush();
    }

    protected Optional<TSDecorator> findDecorator(TSFunction tsFunction, List<TSDecorator> decoratorList) {
        return decoratorList
                .stream()
                .filter(d -> d.getTsFunction() == tsFunction)
                .findFirst();
    }
}
