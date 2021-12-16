package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.ILiteral;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSArrowFunctionLiteral;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSDecorator;
import com.blueveery.springrest2ts.tsmodel.TSElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSFunction;
import com.blueveery.springrest2ts.tsmodel.TSJsonLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteralArray;
import com.blueveery.springrest2ts.tsmodel.TSMap;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.TSTypeLiteral;
import com.blueveery.springrest2ts.tsmodel.TSUnion;
import com.blueveery.springrest2ts.tsmodel.generics.TSParameterizedTypeReference;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.blueveery.springrest2ts.converters.TypeMapper.tsObject;

public class TypeBasedConversionToJacksonJs implements ConversionListener {
    protected TSModule jacksonJSModule;
    protected final TSFunction jsonPropertyFunction;
    protected final TSFunction jsonClassTypeFunction;
    private TSDecorator jsonProperty;

    public TypeBasedConversionToJacksonJs() {
        jacksonJSModule = new TSModule("jackson-js", null, true);
        jsonPropertyFunction = new TSFunction("JsonProperty", jacksonJSModule);
        jsonProperty = new TSDecorator(jsonPropertyFunction);
        jsonClassTypeFunction = new TSFunction("JsonClassType", jacksonJSModule);
    }

    @Override
    public void tsFieldCreated(Property property, TSField tsField) {
        if (tsField.getOwner() instanceof TSClass) {
            tsField.getTsDecoratorList().add(jsonProperty);
            tsField.getOwner().addScopedTypeUsage(jsonProperty.getTsFunction());

            addJsonClassTypeDecorator(tsField);
        }
    }

    private void addJsonClassTypeDecorator(TSField tsField) {
        TSJsonLiteral classTypeLiteral = new TSJsonLiteral();
        ILiteral returnValue = wrapIntoTSLiteralArray(convertToTypeLiteral(tsField.getType()));
        classTypeLiteral.getFieldMap().put("type",
                new TSArrowFunctionLiteral(returnValue)
        );
        TSDecorator jsonClassTypeDecorator = new TSDecorator(jsonClassTypeFunction);
        jsonClassTypeDecorator.getTsLiteralList().add(classTypeLiteral);
        tsField.getTsDecoratorList().add(jsonClassTypeDecorator);
        tsField.getOwner().addScopedTypeUsage(jsonClassTypeDecorator.getTsFunction());
    }

    private ILiteral wrapIntoTSLiteralArray(ILiteral returnValue) {
        return returnValue instanceof TSLiteralArray ? returnValue : new TSLiteralArray(returnValue);
    }

    private ILiteral convertToTypeLiteral(TSType type) {
        TSType sourceType = type;
        if (type instanceof TSUnion) {
            TSUnion tsUnion = (TSUnion) type;
            List<TSElement> typeList = tsUnion.getJoinedTsElementList().stream()
                    .filter(t -> t != TypeMapper.tsNull && t != TypeMapper.tsUndefined)
                    .collect(Collectors.toList());
            if (typeList.size() > 1) {
                String typeListString = typeList.stream().map(TSElement::getName).collect(Collectors.joining(", "));
                throw new IllegalStateException(
                        "TSUnion type should contain one non-null type to be able to define jackson-js mappings! type list: " + typeListString
                );
            }

            sourceType = typeList.size() == 1 ? (TSType) typeList.get(0) : tsObject;
        }

        if (sourceType instanceof TSArray) {
            TSArray tsArray = (TSArray) sourceType;
            return new TSLiteralArray(new TSTypeLiteral(tsArray), wrapIntoTSLiteralArray(convertToTypeLiteral(tsArray.getElementType())));
        }

        if (sourceType instanceof TSMap) {
            return new TSLiteralArray(new TSTypeLiteral(tsObject));
        }

        if (sourceType instanceof TSParameterizedTypeReference) {
            TSParameterizedTypeReference<?> parameterizedTypeReference = (TSParameterizedTypeReference<?>) sourceType;
            TSComplexElement referencedType = (TSComplexElement) parameterizedTypeReference.getReferencedType();
            for (Class<?> mappedFromClass : referencedType.getMappedFromJavaTypeSet()) {
                List<TSType> tsTypeParameterList = parameterizedTypeReference.getTsTypeParameterList();
                if (Collection.class.isAssignableFrom(mappedFromClass)) {
                    Optional<TSType> typeParameter = tsTypeParameterList.stream().findFirst();
                    return new TSLiteralArray(new TSTypeLiteral(referencedType), wrapIntoTSLiteralArray(
                            convertToTypeLiteral(typeParameter.orElse(tsObject)))
                    );
                }
                if (Map.class.isAssignableFrom(mappedFromClass)) {
                    Optional<TSType> keyParameter = tsTypeParameterList.size() > 0 ? Optional.of(tsTypeParameterList.get(0)) : Optional.empty();
                    Optional<TSType> valueParameter = tsTypeParameterList.size() > 1 ? Optional.of(tsTypeParameterList.get(1)) : Optional.empty();
                    return new TSLiteralArray(
                                new TSTypeLiteral(referencedType),
                                new TSLiteralArray(
                                    convertToTypeLiteral(keyParameter.orElse(tsObject)),
                                    convertToTypeLiteral(valueParameter.orElse(tsObject))
                                )
                            );
                }
            }
        }

        return new TSTypeLiteral(TypeMapper.getTypeObjectTypeVersion(sourceType));
    }
}
