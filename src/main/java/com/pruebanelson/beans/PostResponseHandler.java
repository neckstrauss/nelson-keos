package com.pruebanelson.beans;

import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import com.pruebanelson.dto.Response;

@Component("postResponse")
public class PostResponseHandler {

    @Handler
    public Response handler() {
        
        Response response = new Response();
        
        response.setContent("Usuario Creado");
        response.setStatus("ok");

        return response;
    }
}
