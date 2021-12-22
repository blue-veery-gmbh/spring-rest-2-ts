package com.blueveery.springrest2ts.jacksonjs;

import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.tsmodel.ILiteral;
import com.blueveery.springrest2ts.tsmodel.TSArray;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSElement;
import com.blueveery.springrest2ts.tsmodel.TSEnum;
import com.blueveery.springrest2ts.tsmodel.TSLiteral;
import com.blueveery.springrest2ts.tsmodel.TSLiteralArray;
import com.blueveery.springrest2ts.tsmodel.TSMap;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.TSTypeLiteral;
import com.blueveery.springrest2ts.tsmodel.TSUnion;
import com.blueveery.springrest2ts.tsmodel.generics.TSFormalTypeParameter;
import com.blueveery.springrest2ts.tsmodel.generics.TSParameterizedTypeReference;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.blueveery.springrest2ts.converters.TypeMapper.tsAny;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsObject;
import static com.blueveery.springrest2ts.converters.TypeMapper.tsObjectNumber;

public class JacksonJsTypeTransformer {
    public static final TSModule jacksonJSModule = new TSModule("jackson-js", null, true);

    public static ILiteral convertToTypeLiteral(TSType type) {
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

        if (sourceType == tsAny || sourceType instanceof TSMap || sourceType instanceof TSFormalTypeParameter) {
            return new TSLiteralArray(new TSTypeLiteral(tsObject));
        }

        if (sourceType instanceof TSEnum) {
            return new TSLiteralArray(new TSTypeLiteral(tsObjectNumber));
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
            if (!parameterizedTypeReference.getTsTypeParameterList().isEmpty()) {
                return new TSLiteral("", tsAny, ((TSParameterizedTypeReference<?>) sourceType).getReferencedType().getName());
            }
        }

        return new TSTypeLiteral(TypeMapper.getTypeObjectTypeVersion(sourceType));
    }

    public static ILiteral wrapIntoTSLiteralArray(ILiteral returnValue) {
        return returnValue instanceof TSLiteralArray ? returnValue : new TSLiteralArray(returnValue);
    }
}