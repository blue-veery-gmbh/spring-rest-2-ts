package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.*;
import io.swagger.oas.annotations.Operation;

import java.lang.reflect.Method;

public class SwaggerConversionListener implements ConversionListener {
    @Override
    public void tsScopedTypeCreated(Class javaType, TSScopedElement tsScopedElement) {
        Operation operationAnnotation = (Operation) javaType.getAnnotation(Operation.class);
        applyOperationAnnotation(tsScopedElement, operationAnnotation);

    }

    @Override
    public void tsFieldCreated(Property property, TSField tsField) {

    }

    @Override
    public void tsMethodCreated(Method method, TSMethod tsMethod) {
        Operation operationAnnotation = method.getAnnotation(Operation.class);
        applyOperationAnnotation(tsMethod, operationAnnotation);
    }

    private void applyOperationAnnotation(ICommentedElement commentedElement, Operation operationAnnotation) {
        if (operationAnnotation != null) {
            TSCommentSection tsCommentSection = commentedElement.getTsComment().getTsCommentSection("swagger");
            if (!"".equals(operationAnnotation.summary())) {
                tsCommentSection.getCommentText().append(operationAnnotation.summary());
                tsCommentSection.getCommentText().append("\\n");
            }
            if (!"".equals(operationAnnotation.description())) {
                tsCommentSection.getCommentText().append(operationAnnotation.description());
                tsCommentSection.getCommentText().append("\\n");
            }
        }
    }
}
