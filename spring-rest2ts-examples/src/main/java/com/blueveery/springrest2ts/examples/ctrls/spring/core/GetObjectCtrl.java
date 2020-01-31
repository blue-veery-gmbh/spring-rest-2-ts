package com.blueveery.springrest2ts.examples.ctrls.spring.core;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public interface GetObjectCtrl<T extends BaseDTO> extends BaseCtrl<T> {

    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    default T getObject(@PathVariable("id") int id) {
        return null;
    }

}
