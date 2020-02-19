package com.blueveery.springrest2ts.examples.ctrls.spring;

import com.blueveery.springrest2ts.examples.ctrls.spring.core.BaseCtrlImpl;
import com.blueveery.springrest2ts.examples.model.OrderDTO;
import io.swagger.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@Controller
@RequestMapping("api/order")
public class OrderCtrl extends BaseCtrlImpl<OrderDTO> {

    @Operation(summary = "method creates order", description = "method creates order based on given data")
    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody OrderDTO entity) {
        return entity;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public OrderDTO getOrder(@PathVariable int id) {
        return new OrderDTO();
    }


    @PutMapping(path = "/{id}", consumes = {"application/json"}, produces = {"application/json"})
    @ResponseBody
    public OrderDTO updateOrder(@PathVariable(name = "id") int orderId, @RequestBody OrderDTO entity) {
        return entity;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("id") int id) {

    }

    @RequestMapping(path = "/for-customer", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public List<OrderDTO> getOrdersForCustomer(@RequestParam(value = "customer-id") int customerId,
                                               @RequestParam(value = "sortBy", required = false) String sortBy,
                                               @RequestParam(value = "ascending") Boolean ascending,
                                               @RequestParam(value = "pageNumber") int pageNumber,
                                               @RequestParam(value = "pageSize") int pageSize) {
        return Collections.emptyList();
    }

    @RequestMapping(path = "/filter", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public List<OrderDTO> findOrders(@RequestBody(required = false) List<String> conditions) {
        return Collections.emptyList();
    }

    @RequestMapping(path = "/count", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public long countOrdersForCustomer(@RequestParam("customer-id") int customerId) {
        return 0;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> changeOrderStatus(@PathVariable("id") int id,
                                                @RequestParam(value = "new-status") String status) {
        return ResponseEntity.ok().build();
    }
}
