package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.spring.PathVariableEntity;
import com.blueveery.springrest2ts.spring.RequestBodyEntity;
import com.blueveery.springrest2ts.spring.RequestMappingEntity;
import com.blueveery.springrest2ts.spring.RequestParamEntity;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JaxRsRestToTsConverter extends SpringAnnotationsBasedRestClassConverter {
    protected JaxRsRestToTsConverter(ImplementationGenerator implementationGenerator) {
        super(implementationGenerator);
    }

    public JaxRsRestToTsConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper) {
        super(implementationGenerator, classNameMapper);
    }

    @Override
    protected void addClassAnnotations(Class javaClass, TSClass tsClass) {
        RequestMappingEntity requestMapping = null;
        Annotation[] annotationsByType = javaClass.getAnnotationsByType(Path.class);
        if (annotationsByType != null) {
            Path path = (Path) annotationsByType[0];
            requestMapping = new RequestMappingEntity();
            requestMapping.setPath(new String[]{path.value()});
            tsClass.addAllAnnotations(new Annotation[]{requestMapping});
        }

    }

    @Override
    protected void addMethodAnnotations(Method method, TSMethod tsMethod) {
        RequestMappingEntity requestMapping = new RequestMappingEntity();
        Set<RequestMethod> requestMethodSet = new HashSet<>();
        for (Annotation annotation : method.getAnnotations()) {
            if(annotation instanceof Path){
                Path path = (Path) annotation;
                requestMapping.setPath(new String[]{path.value()});
            }
            if(annotation instanceof GET){
                requestMethodSet.add(RequestMethod.GET);
            }
            if(annotation instanceof POST){
                requestMethodSet.add(RequestMethod.POST);
            }
            if(annotation instanceof PUT){
                requestMethodSet.add(RequestMethod.PUT);
            }
            if(annotation instanceof DELETE){
                requestMethodSet.add(RequestMethod.DELETE);
            }
            if(annotation instanceof Produces){
                Produces produces = (Produces) annotation;
                requestMapping.setProduces(produces.value());
            }
            if(annotation instanceof Consumes){
                Consumes consumes = (Consumes) annotation;
                requestMapping.setConsumes(consumes.value());
            }
        }
        if (!requestMethodSet.isEmpty()) {
            requestMapping.setMethod(requestMethodSet.toArray(new RequestMethod[0]));
            tsMethod.addAllAnnotations(new Annotation[]{requestMapping});
        }

    }

    @Override
    protected void addParameterAnnotations(Parameter parameter, TSParameter tsParameter) {
        RequestParamEntity requestParam = null;
        PathVariableEntity pathVariable = null;
        DefaultValue defaultValue = null;
        for (Annotation annotation : parameter.getAnnotations()) {
            if(annotation instanceof PathParam){
                PathParam pathParam = (PathParam) annotation;
                pathVariable = new PathVariableEntity();
                pathVariable.setName(pathParam.value());
            }
            if(annotation instanceof QueryParam){
                QueryParam queryParam = (QueryParam) annotation;
                requestParam = new RequestParamEntity();
                requestParam.setName(queryParam.value());
            }
            if(annotation instanceof DefaultValue){
                defaultValue = (DefaultValue) annotation;
            }
        }

        if (requestParam != null) {
            requestParam.setRequired(defaultValue == null);
            tsParameter.addAllAnnotations(new Annotation[]{requestParam});
        }
        if (pathVariable != null) {
            pathVariable.setRequired(defaultValue == null);
            tsParameter.addAllAnnotations(new Annotation[]{pathVariable});
        }
        if (tsParameter.getAnnotationList().isEmpty()) {
            boolean isPutOrPost = false;
            List<Annotation> annotationList = tsParameter.getTsMethod().getAnnotationList();
            RequestMapping requestMapping = (RequestMapping) annotationList.stream().filter(a -> a instanceof RequestMapping).findFirst().orElse(null);
            if (requestMapping == null) {
                List<Annotation> classAnnotationList = tsParameter.getTsMethod().getOwner().getAnnotationList();
                requestMapping = (RequestMapping) classAnnotationList.stream().filter(a -> a instanceof RequestMapping).findFirst().orElse(null);
            }
            for (RequestMethod requestMethod : requestMapping.method()) {
                switch (requestMethod){
                    case POST:
                    case PUT: isPutOrPost = true; break;
                }
            }
            if (isPutOrPost) {
                RequestBodyEntity requestBodyEntity = new RequestBodyEntity();
                requestBodyEntity.setRequired(true);
                tsParameter.getTsMethod().getAnnotationList().add(requestBodyEntity);
            }
        }
    }

    @Override
    protected Type handleImplementationSpecificReturnTypes(Method method) {
        return method.getGenericReturnType();
    }
}
