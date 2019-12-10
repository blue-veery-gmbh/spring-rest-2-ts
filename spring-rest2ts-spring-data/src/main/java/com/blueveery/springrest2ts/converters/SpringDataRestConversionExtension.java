package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.extensions.ModelConversionExtension;
import com.blueveery.springrest2ts.extensions.RestConversionExtension;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
import com.blueveery.springrest2ts.tsmodel.TSScopedType;
import com.blueveery.springrest2ts.tsmodel.generics.TSParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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
            TSScopedType tsScopedType = (TSScopedType) typeReference.getReferencedType();
            for (Class aClass : tsScopedType.getMappedFromJavaTypeSet()) {
                if (aClass.isAssignableFrom(Pageable.class)) {
                    for (Annotation annotation : tsParameter.getAnnotationList()) {
                        if (annotation.annotationType() == PageableDefault.class) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void tsMethodCreated(Method method, TSMethod tsMethod) {
        for (TSParameter tsParameter : tsMethod.getParameterList()) {
            if (isMappedRestParam(tsParameter)) {
                tsParameter.setOptional(true);
            }
        }
    }


    @Override
    public String generateImplementation(TSParameter tsParameter, String pathParamsList, String queryParamsList, String headerParamsList) {
        String arrayAssignment = "%s.push({name: '%s', value: %s });";

        String forOfTemplate = "for(const %s of %s) {%s}";
        StringBuilder code = new StringBuilder();
        code.append(String.format(arrayAssignment, queryParamsList, "number", tsParameter.getName() + ".pageNumber+''"));
        code.append(String.format(arrayAssignment, queryParamsList, "size", tsParameter.getName() + ".pageSize+''"));

        String sortField = tsParameter.getName() + ".sort";
        String sortValue = "sortOrder.property + (!sortOrder.ascending ? ',DESC' : '') ";
        String sortMapAssignment =  String.format(arrayAssignment, queryParamsList, "sort", sortValue);
        String sortSerializationFor = String.format(forOfTemplate, "sortOrder", sortField, sortMapAssignment);
        code.append(String.format("if(%s) {%s}", sortField, sortSerializationFor));

        return String.format("if(%s) {%s}", tsParameter.getName(), code.toString());
    }
}
