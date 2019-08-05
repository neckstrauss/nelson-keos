package com.pruebanelson.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import com.pruebanelson.dto.Response;

@Component("getResponse")
public class GetResponseHandler {

    @Handler
    public Response handler(Exchange ex) {
        
        Response response = new Response();
        
        response.setContent(ex.getIn().getBody());
        response.setStatus("Ok");

        return response;
    }
}
