package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.extensions.ModelConversionExtension;
import com.blueveery.springrest2ts.extensions.RestConversionExtension;
import com.blueveery.springrest2ts.tsmodel.*;
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
    public void tsMethodCreated(Method method, TSMethod tsMethod) {
        for (TSParameter tsParameter : tsMethod.getParameterList()) {
            if (tsParameter.getType() instanceof TSParameterizedTypeReference<?>) {
                TSParameterizedTypeReference<?> typeReference = (TSParameterizedTypeReference<?>) tsParameter.getType();
                TSScopedType tsScopedType = (TSScopedType) typeReference.getReferencedType();
                for (Class aClass : tsScopedType.getMappedFromJavaTypeSet()) {
                    if (aClass.isAssignableFrom(Pageable.class)) {
                        for (Annotation annotation : tsParameter.getAnnotationList()) {
                            if (annotation.annotationType() == PageableDefault.class) {
                                tsParameter.setOptional(true);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
