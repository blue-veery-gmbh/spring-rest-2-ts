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

import static com.blueveery.springrest2ts.converters.TypeMapper.tsAny;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsObject;

public class TypeBasedConversionToJsClassTransformer implements ConversionListener {
    protected final TSModule jsClassTransformerModule;
    protected final TSFunction typeFunction;

    public TypeBasedConversionToJsClassTransformer() {
        jsClassTransformerModule = new TSModule("class-transformer", null, true);
        typeFunction = new TSFunction("Type", jsClassTransformerModule);
    }

    @Override
    public void tsFieldCreated(Property property, TSField tsField) {
        if (tsField.getOwner() instanceof TSClass) {
            TSType sourceType = findSourceType(tsField.getType());
            if (sourceType != null) {
                TSDecorator tsDecorator = new TSDecorator(typeFunction);
                ILiteral tsArrowFunctionLiteral = new TSArrowFunctionLiteral(new TSTypeLiteral(sourceType));
                tsDecorator.getTsLiteralList().add(tsArrowFunctionLiteral);
                tsField.getTsDecoratorList().add(tsDecorator);
                tsField.getOwner().addScopedTypeUsage(typeFunction);
            }
        }
    }

    private TSType findSourceType(TSType type) {
        TSType sourceType = type;
        if (type instanceof TSUnion) {
            TSUnion tsUnion = (TSUnion) type;
            sourceType = unwrapSourceTypeFromUnion(tsUnion);
        }

        if (sourceType instanceof TSArray) {
            TSArray tsArray = (TSArray) sourceType;
            return TypeMapper.getTypeObjectTypeVersion(tsArray.getElementType());
        }

        if (sourceType == tsAny || sourceType instanceof TSMap) {
            return tsObject;
        }

        if (sourceType instanceof TSParameterizedTypeReference) {
            TSParameterizedTypeReference<?> parameterizedTypeReference = (TSParameterizedTypeReference<?>) sourceType;
            TSComplexElement referencedType = (TSComplexElement) parameterizedTypeReference.getReferencedType();
            for (Class<?> mappedFromClass : referencedType.getMappedFromJavaTypeSet()) {
                List<TSType> tsTypeParameterList = parameterizedTypeReference.getTsTypeParameterList();
                if (Collection.class.isAssignableFrom(mappedFromClass)) {
                    Optional<TSType> typeParameter = tsTypeParameterList.stream().findFirst();
                    return TypeMapper.getTypeObjectTypeVersion(typeParameter.orElse(tsObject));
                }
                if (Map.class.isAssignableFrom(mappedFromClass)) {
                    Optional<TSType> valueParameter = tsTypeParameterList.size() > 1 ? Optional.of(tsTypeParameterList.get(1)) : Optional.empty();
                    return TypeMapper.getTypeObjectTypeVersion(valueParameter.orElse(tsObject));
                }
            }
            return sourceType;
        }
        return null;
    }

    private TSType unwrapSourceTypeFromUnion(TSUnion tsUnion) {
        TSType sourceType;
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
        return sourceType;
    }
}
