package com.blueveery.springrest2ts.examples.ctrls.jaxrs;

import com.blueveery.springrest2ts.examples.ctrls.jaxrs.core.BaseCtrl;
import com.blueveery.springrest2ts.examples.ctrls.jaxrs.core.BaseCtrlImpl;
import com.blueveery.springrest2ts.examples.model.OrderDTO;
import io.swagger.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;


@Path("api/order")
public class OrderCtrl extends BaseCtrlImpl<OrderDTO> implements BaseCtrl<OrderDTO> {

    @Operation(summary = "method creates order", description = "method creates order based on given data")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public OrderDTO createOrder(OrderDTO entity) {
        return entity;
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public OrderDTO getOrder(@PathParam("id") int id) {
        return new OrderDTO();
    }


    @Path("/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public OrderDTO updateOrder(OrderDTO entity) {
        return entity;
    }

    @Path("/{id}")
    @DELETE
    public void deleteOrder(@PathParam("id") int id) {
    }

    @Path("/for-customer")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrderDTO> getOrdersForCustomer(@QueryParam(value = "customer-id") int customerId,
                                               @QueryParam(value = "sortBy") String sortBy,
                                               @QueryParam(value = "ascending") Boolean ascending,
                                               @QueryParam(value = "pageNumber") int pageNumber,
                                               @QueryParam(value = "pageSize") int pageSize) {
        return Collections.emptyList();
    }

    @Path("/filter")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<OrderDTO> findOrders(List<String> conditions) {
        return Collections.emptyList();
    }

    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public long countOrdersForCustomer(@QueryParam("customer-id") int customerId) {
        return 0;
    }

    @Path("/{id}")
    @PUT
    public void changeOrderStatus(@PathParam("id") int id, @QueryParam(value = "new-status") String status) {
    }
}
