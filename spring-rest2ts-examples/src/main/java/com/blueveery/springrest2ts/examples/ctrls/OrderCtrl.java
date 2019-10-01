package com.blueveery.springrest2ts.examples.ctrls;

import com.blueveery.springrest2ts.examples.ctrls.core.BaseCtrl;
import com.blueveery.springrest2ts.examples.model.OrderDTO;
import io.swagger.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("api/order")
public class OrderCtrl implements BaseCtrl<OrderDTO> {

    @Operation(summary = "method creates order", description = "method creates order based on given data")
    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody OrderDTO entity) {
        return entity;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public OrderDTO getOrder(@PathVariable UUID id) {
        return new OrderDTO();
    }


    @RequestMapping(path = "/{id}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
    @ResponseBody
    public OrderDTO updateOrder(@RequestBody OrderDTO entity) {
        return entity;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("id") UUID id) {

    }

    @RequestMapping(path = "/for-customer", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public List<OrderDTO> getOrdersForCustomer(@RequestParam(value = "customer-id") UUID customerId,
                                               @RequestParam(value = "sortBy", required = false) String sortBy,
                                               @RequestParam(value = "ascending") Boolean ascending,
                                               @RequestParam(value = "pageNumber") int pageNumber,
                                               @RequestParam(value = "pageSize") int pageSize) {
        return Collections.emptyList();
    }

    @RequestMapping(method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public List<OrderDTO> findOrders(@RequestBody(required = false) List<String>  conditions) {
        return Collections.emptyList();
    }

    @RequestMapping(path = "/count", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public long countOrdersForCustomer(@RequestParam("customer-id") UUID customerId) {
        return 0;
    }
}
