package com.blueveery.springrest2ts.examples.ctrls;


import com.blueveery.springrest2ts.examples.ctrls.core.BaseCtrl;
import com.blueveery.springrest2ts.examples.model.ManufacturerDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
@RequestMapping("api/manufacturer")
public class ManufacturerCtrl implements BaseCtrl<ManufacturerDTO> {

    @RequestMapping(path = "/manufacturer", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public Page<ManufacturerDTO> findManufacturers(Pageable pageable){
        return new PageImpl<>(new ArrayList<>());
    }
}
