package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class UserProfileDTO extends BaseDTO {

    private String userLogin;
    @JsonUnwrapped
    private PersonDTO userData;

}
