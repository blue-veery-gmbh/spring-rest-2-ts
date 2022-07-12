package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSType;
import java.lang.reflect.Type;

public interface MappingAction {
  TSType map(Type javaClass);
}