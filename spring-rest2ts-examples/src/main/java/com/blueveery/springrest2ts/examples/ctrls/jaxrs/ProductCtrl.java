package com.blueveery.springrest2ts.examples.ctrls.jaxrs;

import com.blueveery.springrest2ts.examples.ctrls.jaxrs.core.BaseCtrlImpl;
import com.blueveery.springrest2ts.examples.ctrls.jaxrs.core.GetObjectCtrl;
import com.blueveery.springrest2ts.examples.model.CategoryDTO;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Path("api/product")
public class ProductCtrl extends BaseCtrlImpl<CategoryDTO.ProductDTO> implements GetObjectCtrl<CategoryDTO.ProductDTO> {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CategoryDTO.ProductDTO createProduct(CategoryDTO.ProductDTO entity) {
        return entity;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CategoryDTO.ProductDTO> getProducts(@QueryParam(value = "price") BigInteger price,
                                                     @QueryParam(value = "sortBy") String sortBy,
                                                     @QueryParam(value = "ascending") Boolean ascending,
                                                     @QueryParam(value = "pageNumber") int pageNumber,
                                                     @QueryParam("pageSize") Optional<Integer> pageSize) {
        return new ArrayList<>();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public long countProducts() {
        return 0;
    }

    @DELETE
    @Path("/{id}")
    @Produces()
    public void deleteOrder(@PathParam("id") int id) {
    }
}
