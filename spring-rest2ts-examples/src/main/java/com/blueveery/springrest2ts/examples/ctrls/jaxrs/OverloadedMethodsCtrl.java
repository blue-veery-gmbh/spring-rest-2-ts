package com.blueveery.springrest2ts.examples.ctrls.jaxrs;

import com.blueveery.springrest2ts.examples.ctrls.jaxrs.core.BaseCtrlImpl;
import com.blueveery.springrest2ts.examples.ctrls.jaxrs.core.GetObjectCtrl;
import com.blueveery.springrest2ts.examples.model.CategoryDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("api/product-overloaded")
public class OverloadedMethodsCtrl extends BaseCtrlImpl<CategoryDTO.ProductDTO> implements GetObjectCtrl<CategoryDTO.ProductDTO> {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public CategoryDTO.ProductDTO createProduct(CategoryDTO.ProductDTO entity) {
        return entity;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/product/create/{id}/with-update")
    public CategoryDTO.ProductDTO createProduct(CategoryDTO.ProductDTO entity, @QueryParam("update") boolean update) {
        return entity;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/product/create/{id}")
    public CategoryDTO.ProductDTO createOrder(CategoryDTO.ProductDTO entity) {
        return entity;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/product/create/fake")
    public CategoryDTO.ProductDTO createOrder(CategoryDTO.ProductDTO entity, @QueryParam("update") boolean update) {
        return entity;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/count")
    public long countProducts() {
        return 0;
    }
}
