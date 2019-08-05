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
public class GenerarUsuarioRoute extends RouteBuilder {

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

        onException(Exception.class)
        .handled(true)
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
        .bean("errorResponse")
        .marshal()
        .json(JsonLibrary.Jackson);
        
        from("direct:generar-usuario")
        .log(">> inicia genracion de usuario ${body.gender}")
        .to("direct:validation")
        .filter().simple("${body.gender} != 'male' && ${body.gender} != 'female'")
            .log(LoggingLevel.INFO,"FALLO VALIDACION CONTENIDO")
            .throwException(new GenderContentValidationException())
        .end()           
        .setHeader(Exchange.HTTP_METHOD).constant("GET")
        .setHeader(Exchange.HTTP_URI).simple("https://randomuser.me/api/?gender=female")
        .setBody().constant(null)
        .log(">> Previo llamdo servicio rest ${headers.CamelHttpUri}")
        .to("http://dummy")
        .setBody().jsonpath("$.results[*]")
        //.convertBodyTo(String.class)
        .log(LoggingLevel.INFO, "REQ>> ${body}")
        .setHeader("user_genero").jsonpath("$.[*].gender", String.class)
        .setHeader("user_nombre").jsonpath("$.[*].name.first", String.class)
        .setHeader("user_apellido").jsonpath("$.[*].name.last", String.class)
        .setHeader("user_email").jsonpath("$.[*].email", String.class)
        .setHeader("user_foto_large").jsonpath("$.[*].picture.large", String.class)
        
        .to("sql:INSERT INTO sys.users " + 
                "(genero, nombre, apellido, email, foto_large) " + 
                "VALUES(:#${headers.user_genero}, :#${headers.user_nombre}, :#${headers.user_apellido}, :#${headers.user_email}, :#${headers.user_foto_large});"+
                "?" +
                "dataSource=dataSource")
        .bean("postResponse")
        ;
    }

}
