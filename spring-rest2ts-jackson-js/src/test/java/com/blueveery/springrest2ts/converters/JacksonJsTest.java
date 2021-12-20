package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tests.BaseTest;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

public class JacksonJsTest extends BaseTest<JacksonObjectMapper> {
    @Override
    protected JacksonObjectMapper createObjectMapper() {
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        return jacksonObjectMapper;
    }
}
