package com.nina.zigbeerestapi.resources;

import com.nina.zigbeerestapi.core.Light;
import com.nina.zigbeerestapi.core.Lights;
import com.nina.zigbeerestapi.serialcomm.SerialCommunication;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/lights")
@Produces(MediaType.APPLICATION_JSON)
public class LightsResource {

    private final SerialCommunication serialComm;
	private final Lights lights;
	private final static Integer DEFAULT_TRANSITION_TIME = new Integer(1);
	private final static Integer MAX_BRIGHTNESS = new Integer(254);
	private final static int READ_ATTR_DELAY_MS = 100;
	private final static int WAIT_ATTR_TIMEOUT_MS = 5000;

	public LightsResource(SerialCommunication serialComm, Lights lights) {
		this.serialComm = serialComm;
		this.lights = lights;
	}

	@GET
	@Timed
	public ArrayList<String> getAllLights() {
		return lights.getAllLights();
	}

	@GET
	@Timed
	@Path("{id}")
	public Light getLight(@PathParam("id") long id) {
		Light light = lights.getLightById(id);
		
		long prevStackVersionLastUpdated = light.getStackVersionLastUpdated();
		
		serialComm.writeCommand("readBasicAttr -s " + light.getShortNwkAddress() 
				+ " " + light.getEndpointId() + " 0x0002");

		long timeout = System.currentTimeMillis() + WAIT_ATTR_TIMEOUT_MS;
		while(light.getStackVersionLastUpdated() <= prevStackVersionLastUpdated) {
			if (System.currentTimeMillis() >= timeout) break;
		}

		long prevModelIdLastUpdated = light.getModelIdLastUpdated();
		
		serialComm.writeCommand("readBasicAttr -s " + light.getShortNwkAddress() 
				+ " " + light.getEndpointId() + " 0x0005");

		timeout = System.currentTimeMillis() + WAIT_ATTR_TIMEOUT_MS;
		while(light.getModelIdLastUpdated() <= prevModelIdLastUpdated) {
			if (System.currentTimeMillis() >= timeout) break;
		}

		return light;
	}

	@PUT
	@Timed
	@Path("{id}")
	public Light setState(@PathParam("id") long id, 
			@QueryParam("name") Optional<String> name, 
			@QueryParam("on") Optional<Boolean> on, 
			@QueryParam("brightness") Optional<Integer> brightness,
			@QueryParam("transitiontime") Optional<Integer> transitionTime) {

		Light light = lights.getLightById(id);
		//kalo light null, return error here

		Integer transitionTimeInt;

		if(!transitionTime.isPresent()) {
			transitionTimeInt = DEFAULT_TRANSITION_TIME;
		}
		else {
			transitionTimeInt = transitionTime.get();
		}

		if(name.isPresent()) {
			light.setName(name.get());
		}

		if(on.isPresent()) {
			long prevOnLastUpdated = light.getOnLastUpdated();

		    String value = "-off";
			if (on.get()) {
				value = "-on";
			}
			serialComm.writeCommand("onOff -s " + light.getShortNwkAddress() 
				+ " " + light.getEndpointId() + " " + value);

			try {
			   	Thread.sleep(READ_ATTR_DELAY_MS);
			}
			catch ( InterruptedException e ) {
				e.printStackTrace();
			}

			serialComm.writeCommand("readOnOffAttr -s " 
				+ light.getShortNwkAddress() + " " + light.getEndpointId() 
				+ " 0x00");
			
			long timeout = System.currentTimeMillis() + WAIT_ATTR_TIMEOUT_MS;
			while(light.getOnLastUpdated() <= prevOnLastUpdated) {
				if (System.currentTimeMillis() >= timeout) break;
			}
		}

		if(brightness.isPresent()) {
			long prevBrightnessLastUpdated = light.getBrightnessLastUpdated();

			Integer value = brightness.get();
			if (value.compareTo(MAX_BRIGHTNESS) > 0) {
				value = MAX_BRIGHTNESS;
			}
			serialComm.writeCommand("moveToLevel -s " 
				+ light.getShortNwkAddress() + " " + light.getEndpointId() 
				+ " " + value + " " + transitionTimeInt + " " + 1);

			try {
			   	Thread.sleep(READ_ATTR_DELAY_MS);
			}
			catch ( InterruptedException e ) {
				e.printStackTrace();
			}

			serialComm.writeCommand("readLevelAttr -s " 
				+ light.getShortNwkAddress() + " " + light.getEndpointId() 
				+ " 0x00");
			
			long timeout = System.currentTimeMillis() + WAIT_ATTR_TIMEOUT_MS;
			while(light.getBrightnessLastUpdated() <= prevBrightnessLastUpdated) {
				if (System.currentTimeMillis() >= timeout) break;
			}

		}

		return light;

	}

}