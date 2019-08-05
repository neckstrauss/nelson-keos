package com.pruebanelson.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.pruebanelson.Exceptions.GenderContentValidationException;
import com.pruebanelson.Exceptions.HeaderValidationException;
@Component
public class ValidationRoute extends RouteBuilder {

    
    
    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        
        onException(BeanValidationException.class, GenderContentValidationException.class)
        .handled(true)
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
        .bean("errorResponse")
        .marshal().json(JsonLibrary.Jackson);
    
        onException(HeaderValidationException.class)
        .handled(true)
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(401))
        .bean("errorResponse")
        .marshal().json(JsonLibrary.Jackson);
        
        from("direct:validation")
        .log(LoggingLevel.INFO,"ENTRO A VALIDACION")
        .to("bean-validator:validar-campos-requeridos")
        .filter().simple("${headers.Autorizacion} != 'BCZJXoLjZV'")
            .log(LoggingLevel.INFO,"FALLO")
            .throwException(new HeaderValidationException())
        .end()
        ;      
    }

}
