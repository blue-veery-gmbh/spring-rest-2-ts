package com.blueveery.springrest2ts.examples.model.tags;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;

public class TagDTO extends BaseDTO {
    private String name;

    public TagDTO(String name) {
        this.name = name;
    }
}
