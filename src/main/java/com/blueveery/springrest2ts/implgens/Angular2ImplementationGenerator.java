package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.tsmodel.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class Angular2ImplementationGenerator implements ImplementationGenerator{
    private TSDecorator injectableDecorator;
    private TSClass observableClass;
    private TSClass httpClass;
    private TSClass responseClass;


    public Angular2ImplementationGenerator() {
        TSModule angularCoreModule = new TSModule("@angular/core");
        angularCoreModule.setExternal(true);
        injectableDecorator = new TSDecorator("", new TSFunction("Injectable", angularCoreModule));

        TSModule rxjsObservableModule = new TSModule("rxjs/Observable");
        rxjsObservableModule.setExternal(true);
        observableClass = new TSClass("Observable", rxjsObservableModule);

        TSModule angularHttpModule = new TSModule("@angular/http");
        angularHttpModule.setExternal(true);

        httpClass = new TSClass("Http", angularHttpModule);
        responseClass = new TSClass("Response", angularHttpModule);
    }

    @Override
    public void write(BufferedWriter writer, TSMethod method) throws IOException {
        if(method.isConstructor()){
            writer.write("this.httpService = http;");
            writer.newLine();
        }else{
            RequestMapping methodRequestMapping = method.findAnnotation(RequestMapping.class);
            RequestMapping classRequestMapping = method.getOwner().findAnnotation(RequestMapping.class);
            writer.write("// path = " + getPathFromRequestMapping(classRequestMapping) + getPathFromRequestMapping(methodRequestMapping));
            writer.newLine();
            writer.write("// HTTP method = " + methodRequestMapping.method()[0]);
            writer.newLine();

            for(TSParameter tsParameter:method.getParameterList()){
                writer.newLine();
                if(tsParameter.findAnnotation(RequestBody.class)!=null){
                    RequestBody requestBody = tsParameter.findAnnotation(RequestBody.class);
                    writer.write(String.format("// parameter %s is send in request body ", tsParameter.getName()));
                    continue;
                }
                if(tsParameter.findAnnotation(PathVariable.class)!=null){
                    PathVariable pathVariable = tsParameter.findAnnotation(PathVariable.class);
                    writer.write(String.format("// parameter %s is send in path variable %s ", tsParameter.getName(), pathVariable.value()));
                    continue;
                }
                if(tsParameter.findAnnotation(RequestParam.class)!=null){
                    RequestParam requestParam = tsParameter.findAnnotation(RequestParam.class);
                    writer.write(String.format("// parameter %s is send as request param %s ", tsParameter.getName(), requestParam.value()));
                }
            }

        }

    }

    private String getPathFromRequestMapping(RequestMapping requestMapping){
        if(requestMapping.path().length>0){
            return requestMapping.path()[0];
        }

        if(requestMapping.value().length>0){
            return requestMapping.value()[0];
        }

        return "";
    }

    @Override
    public TSType mapReturnType(TSMethod tsMethod, TSType tsType) {
        if(isRestClass(tsMethod.getOwner())) {
            return new TSParameterisedType("", observableClass, tsType);
        }

        return tsType;
    }

    @Override
    public SortedSet<TSField> getImplementationSpecificFields(TSComplexType tsComplexType) {
        if(isRestClass(tsComplexType)) {
            SortedSet fieldsSet = new TreeSet();
            fieldsSet.add(new TSField("httpService", tsComplexType, httpClass));
            return fieldsSet;
        }
        return Collections.emptySortedSet();
    }

    @Override
    public List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        if(method.isConstructor() && isRestClass(method.getOwner())){
            TSParameter httpServiceParameter = new TSParameter("http", httpClass);
            List<TSParameter> tsParameters = new ArrayList<>();
            tsParameters.add(httpServiceParameter);
            return tsParameters;
        }
        return Collections.emptyList();
    }

    private boolean isRestClass(TSComplexType tsComplexType) {
        return tsComplexType.findAnnotation(RequestMapping.class) != null;
    }

    @Override
    public List<TSDecorator> getDecorators(TSMethod tsMethod) {
        return Collections.emptyList();
    }

    @Override
    public List<TSDecorator> getDecorators(TSClass tsClass) {
        return Collections.singletonList(injectableDecorator);
    }

    @Override
    public void addComplexTypeUsage(TSClass tsClass) {
        if(isRestClass(tsClass)) {
            tsClass.addScopedTypeUsage(observableClass);
            tsClass.addScopedTypeUsage(httpClass);
            tsClass.addScopedTypeUsage(responseClass);
            tsClass.addScopedTypeUsage(injectableDecorator.getTsFunction());
        }
    }
}
