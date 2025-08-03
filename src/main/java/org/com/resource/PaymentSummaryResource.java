package org.com.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;

@Path("/payments-summary")
public class PaymentSummaryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response paymentSummary(@QueryParam(value = "to") Instant to, @QueryParam(value = "from") Instant from) {
        return Response.ok().build();
    }
}