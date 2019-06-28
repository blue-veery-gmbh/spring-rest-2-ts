package com.blueveery.springrest2ts.examples.model.enums;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.time.LocalDateTime;

public class CategoryDTO extends BaseDTO {

    private String name;
    private String categoryName;
    private String shortName;
    private CategoryDTO parentCategory;

    @JsonProperty("creationDateProperty")
    private LocalDateTime creationDate;

    public CategoryDTO(String name) {
        this.name = name;
    }

    @JsonGetter("categoryName")
    public String name() {
        return name;
    }

    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("parentCategory")
    public CategoryDTO getParentCategory() {
        return parentCategory;
    }

    @JsonSetter("shortName")
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @JsonProperty("creationDateGetter")
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
