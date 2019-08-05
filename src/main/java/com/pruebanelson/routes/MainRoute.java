package com.pruebanelson.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.pruebanelson.Exceptions.GenderContentValidationException;
import com.pruebanelson.Exceptions.HeaderValidationException;
import com.pruebanelson.Exceptions.ParametroGenderValidationException;
import com.pruebanelson.dto.PostRequest;

@Component
public class MainRoute extends RouteBuilder {

    @Autowired
    private Environment env;

    @Bean
    public ServletRegistrationBean camelServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/*");
        registration.setName("CamelServlet");
        return registration;
    }

    @Override
    public void configure() throws Exception {
     // @formatter:off
        
        
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
        
        
        restConfiguration()
            .component("servlet")
            .bindingMode(RestBindingMode.json);
        
        rest("/user")
            .post().type(PostRequest.class)
                .to("direct:generar-usuario")
            .get()
                .to("direct:consultar-usuarios")
                
                ;
   
        from("direct:consultar-usuarios")
            .log("entro a consultar usuarios")
            .to("direct:validation")
            .filter().simple("${headers.gender} != 'male' && ${headers.gender} != 'female'")
                .log(LoggingLevel.INFO,"FALLO VALIDACION CONTENIDO")
                .throwException(new ParametroGenderValidationException())
            .end()
            .to("sql: SELECT * FROM users WHERE genero = :#${headers.gender}")
            .bean("getResponse")
            .log("RESULT >> ${body}")
            .log("HEADERS >> ${headers}")
            ;
        
        
   // @formatter:on    
    }

}
