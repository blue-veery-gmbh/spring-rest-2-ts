package com.blueveery.springrest2ts.examples.ctrls.spring.core;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import io.swagger.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

public interface CreateObjectCtrl<T extends BaseDTO> extends BaseCtrl<T> {

    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    default T createObject(@RequestBody T object) {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    default List<T> createObjects(@RequestBody List<T> object) {
        return null;
    }

}
