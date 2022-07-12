package com.blueveery.springrest2ts.webflux;

import com.blueveery.springrest2ts.converters.MappingAction;
import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.tsmodel.TSType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import reactor.core.publisher.Mono;

public class MonoMappingAction implements MappingAction {
  @Override
  public TSType map(Type javaType) {
    ParameterizedType parameterizedType = (ParameterizedType) javaType;
    if (parameterizedType.getRawType() != Mono.class) {
      throw new IllegalArgumentException("Mapping action dedicated only for " + Mono.class.getSimpleName());
    }
    Type[] typeParameters = parameterizedType.getActualTypeArguments();

    if (typeParameters.length > 0) {
      return TypeMapper.map(typeParameters[0]);
    } else {
      return TypeMapper.tsVoid;
    }
  }
}
