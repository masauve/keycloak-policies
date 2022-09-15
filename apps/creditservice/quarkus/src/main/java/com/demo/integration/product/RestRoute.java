package com.demo.integration.product;

import org.apache.camel.model.rest.RestBindingMode;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;

import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class RestRoute extends RouteBuilder {

    public void configure() throws Exception {

        restConfiguration()
                .bindingMode(RestBindingMode.auto)
                .apiProperty("cors", "true")
                .dataFormatProperty("disableFeatures", "FAIL_ON_EMPTY_BEANS");

            rest("/rest")
                .produces(MediaType.APPLICATION_JSON)
                .get("/credit/health").route()
                .to("direct:health")
                .endRest()
                .get("/credit/{clientId}").route()
                .to("direct:getById")
                .endRest()
                .get("/credit").route()
                .to("direct:getTransactions")
                .endRest()
                .post("/credit").route()
                .to("direct:writecredit")
                .end();

        from("direct:writecredit")
            .log("add credit service")
            .log("BODY: ${body}")
            .setBody(simple("insert into transaction (CLIENT_ID, TYPE, LOCATION, AMOUNT) values ('${body[clientId]}', '${body[type]}','${body[location]}','${body[amount]}' );"))
            .to("jdbc:camel")
            .setBody(simple("done!"));
  
        from("direct:getById")
            .log("get by Id")
            .setBody(simple("select CLIENT_ID, TYPE, LOCATION, AMOUNT from transaction where client_id='${header.clientId}'"))
            .to("jdbc:camel");

        from("direct:getTransactions")
            .log("getAll")
            .setBody(simple("select CLIENT_ID, TYPE, LOCATION, AMOUNT from transaction"))
            .to("jdbc:camel");

        from("direct:health")
            .log("service healthy")
            .setBody(simple("healthy"));

    }
}
