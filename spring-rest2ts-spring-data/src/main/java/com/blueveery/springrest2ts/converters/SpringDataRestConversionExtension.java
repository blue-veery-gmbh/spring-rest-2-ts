package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.extensions.ModelConversionExtension;
import com.blueveery.springrest2ts.extensions.RestConversionExtension;
import com.blueveery.springrest2ts.tsmodel.TSField;
import org.springframework.data.domain.Pageable;

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
    public void tsFieldCreated(Property property, TSField tsField) {
        boolean containsPage = tsField.getOwner().getMappedFromJavaTypeSet().contains(Pageable.class);
        if (containsPage) {
            if (tsField.getName().equals("pageNumber") || tsField.getName().equals("pageSize")) {
                tsField.setReadOnly(false);
            } else {
                tsField.setOptional(true);
            }

            if (tsField.getName().equals("sort")){
                tsField.setReadOnly(false);
                tsField.setOptional(true);
            }
        }
    }
}
