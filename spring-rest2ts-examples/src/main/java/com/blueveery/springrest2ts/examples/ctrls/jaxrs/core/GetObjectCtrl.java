package com.blueveery.springrest2ts.examples.ctrls.jaxrs.core;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface GetObjectCtrl<T extends BaseDTO> extends BaseCtrl<T> {

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    default T getObject(@PathParam("id") int id) {
        return null;
    }
}
