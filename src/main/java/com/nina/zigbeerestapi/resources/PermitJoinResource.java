package com.nina.zigbeerestapi.resources;

import com.nina.zigbeerestapi.core.PermitJoinStatus;
import com.nina.zigbeerestapi.serialcomm.SerialCommunication;
import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/permitjoin")
@Produces(MediaType.APPLICATION_JSON)
public class PermitJoinResource {
    private final SerialCommunication serialComm;
    private final boolean status;
    private final AtomicLong counter;

    public PermitJoinResource(SerialCommunication serialComm, boolean status) {
        this.serialComm = serialComm;
        this.status = status;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public PermitJoinStatus getPermitJoinStatus() {
        //panggil status here, return
        return new PermitJoinStatus(counter.incrementAndGet(), status);
    }

    @PUT
    @Timed
    public String invokeEZMode(){
        try {
            serialComm.writeCommand("startEzMode");
        }

        catch (Exception exc) {
            return exc.toString();
        }

        return "invokeEZMode success";
    }
}