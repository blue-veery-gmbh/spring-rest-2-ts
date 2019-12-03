package com.blueveery.springrest2ts.examples.ctrls;


import com.blueveery.springrest2ts.examples.ctrls.core.BaseCtrl;
import com.blueveery.springrest2ts.examples.model.ManufacturerDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("api/manufacturer")
public class ManufacturerCtrl implements BaseCtrl<ManufacturerDTO> {

    @RequestMapping(method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public Page<ManufacturerDTO> findManufacturers(@PageableDefault(page = 0, size = 10, sort = "name",direction = Sort.Direction.ASC) Pageable pageable){
        List<ManufacturerDTO> manufacturerDTOS = new ArrayList<>();
        manufacturerDTOS.add(new ManufacturerDTO());
        return new PageImpl<>(manufacturerDTOS);
    }
}
