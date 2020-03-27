package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.extensions.ModelConversionExtension;
import com.blueveery.springrest2ts.extensions.RestConversionExtension;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.generics.TSParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class SpringDataRestConversionExtension implements RestConversionExtension {

    SpringDataModelConversionExtension modelConversionExtension = new SpringDataModelConversionExtension();

    @Override
    public ModelConversionExtension getModelConversionExtension() {
        return modelConversionExtension;
    }

    @Override
    public boolean isMappedRestParam(Class aClass) {
        return aClass.isAssignableFrom(Pageable.class);
    }

    @Override
    public boolean isMappedRestParam(TSParameter tsParameter) {
        if (tsParameter.getType() instanceof TSParameterizedTypeReference<?>) {
            TSParameterizedTypeReference<?> typeReference = (TSParameterizedTypeReference<?>) tsParameter.getType();
            TSScopedElement tsScopedElement = (TSScopedElement) typeReference.getReferencedType();
            for (Class aClass : tsScopedElement.getMappedFromJavaTypeSet()) {
                if (aClass.isAssignableFrom(Pageable.class)) {
                   return true;
                }
            }
        }
        return false;
    }

    @Override
    public void tsMethodCreated(Method method, TSMethod tsMethod) {

    }

    @Override
    public void tsParameterCreated(Parameter parameter, TSParameter tsParameter) {
        if (isMappedRestParam(tsParameter)){
            tsParameter.setOptional(parameter.getAnnotation(PageableDefault.class) != null);
            TSType tsDirectionEnum = TypeMapper.map(Sort.Direction.class);
            tsParameter.getTsMethod().getOwner().addScopedTypeUsage(tsDirectionEnum);
        }
    }

    @Override
    public String generateImplementation(TSParameter tsParameter, String pathParamsList, String queryParamsList, String headerParamsList) {
        String arrayAssignment = "%s.push({name: '%s', value: %s });\n";

        String forOfTemplate = "for(const %s of %s) {\n%s\n}";
        StringBuilder code = new StringBuilder();
        code.append(String.format(arrayAssignment, queryParamsList, "page", tsParameter.getName() + ".pageNumber+''"));
        code.append(String.format(arrayAssignment, queryParamsList, "size", tsParameter.getName() + ".pageSize+''"));

        String sortField = tsParameter.getName() + ".sort.sortOrders";
        String sortValue = "sortOrder.property + (sortOrder.direction === Direction.DESC ? ',DESC' : '') ";
        String sortMapAssignment =  String.format(arrayAssignment, queryParamsList, "sort", sortValue);
        String sortSerializationFor = String.format(forOfTemplate, "sortOrder", sortField, sortMapAssignment);
        code.append(String.format("if(%s && %s) {\n%s\n}", tsParameter.getName() + ".sort", sortField, sortSerializationFor));

        return String.format("\nif(%s) {\n%s\n}", tsParameter.getName(), code.toString());
    }
}
