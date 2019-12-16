package com.blueveery.springrest2ts.tsmodel.generics;

import com.blueveery.springrest2ts.tsmodel.TSType;

import java.util.List;

public interface IParameterizedWithTypes<T extends TSType> {
    List<T> getTsTypeParameterList();

    default String typeParametersToString() {
        StringBuilder parameterList = new StringBuilder();
        if (!getTsTypeParameterList().isEmpty()) {
            parameterList.append("<");
            for (int i = 0; i < getTsTypeParameterList().size(); i++) {
                T tsTypeParameter = getTsTypeParameterList().get(i);
                parameterList.append(tsTypeParameter.getName());
                if(i< getTsTypeParameterList().size()-1){
                    parameterList.append(", ");
                }

            }
            parameterList.append("> ");
        }
        return parameterList.toString();
    }
}
