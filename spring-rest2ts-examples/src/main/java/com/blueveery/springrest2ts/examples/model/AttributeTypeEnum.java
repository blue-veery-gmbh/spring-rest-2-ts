package com.blueveery.springrest2ts.examples.model;

import com.google.gson.annotations.Since;

import java.util.ArrayList;
import java.util.List;

@Since(4.0)
public class AttributeTypeEnum extends AttributeType {

    private List<String> options = new ArrayList<>();

}
