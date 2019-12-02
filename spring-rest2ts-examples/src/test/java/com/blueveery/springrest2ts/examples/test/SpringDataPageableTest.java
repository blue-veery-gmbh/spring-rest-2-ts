package com.blueveery.springrest2ts.examples.test;

import com.blueveery.springrest2ts.examples.model.core.ParametrizedBaseDTO;
import com.blueveery.springrest2ts.filters.ExtendsJavaTypeFilter;
import com.blueveery.springrest2ts.filters.JavaTypeFilter;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.filters.OrFilterOperator;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SpringDataPageableTest extends TsCodeGenerationsTest {

    @Test
    public void controllerWithPageableParam() throws IOException {
        JavaTypeFilter modelClassFilter = new ExtendsJavaTypeFilter(ParametrizedBaseDTO.class);
        Set<Class> springDataTypes = new HashSet<>();
        springDataTypes.add(Page.class);
        springDataTypes.add(Pageable.class);
        springDataTypes.add(PageRequest.class);
        springDataTypes.add(Sort.class);
        springDataTypes.add(Sort.Order.class);
        JavaTypeFilter springDataTypesFilter = new JavaTypeSetFilter(springDataTypes);
        JavaTypeFilter modelClassesCondition = new OrFilterOperator(Arrays.asList(modelClassFilter, springDataTypesFilter));
        tsGenerator.setModelClassesCondition(modelClassesCondition);
        javaPackageSet = new HashSet<>();
        Collections.addAll(javaPackageSet,"com.blueveery.springrest2ts.examples", "org.springframework.data.domain");

        tsGenerator.generate(javaPackageSet, OUTPUT_DIR_PATH);
    }
}
