package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

public class Light{
	private long id;
	private String shortNwkAddress;
    private String endpointId;
	private String name;
	private boolean on;
    private long onLastUpdated = 0L;
    private long brightnessLastUpdated = 0L;

	@Min(0)
	@Max(255)
	private int brightness;

	public Light(long id) {
		this.id = id;
	}

	@JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getShortNwkAddress() {
    	return shortNwkAddress;
    }

    @JsonProperty
    public String getEndpointId() {
    	return endpointId;
    }

    @JsonProperty
    public String getName() {
    	return name;
    }

    @JsonProperty
    public boolean isOn() {
    	return on;
    }

    @JsonProperty
    public int getBrightness() {
    	return brightness;
    }

    public long getOnLastUpdated() {
        return onLastUpdated;
    }

    public long getBrightnessLastUpdated() {
        return brightnessLastUpdated;
    }

    @JsonProperty
    public void setShortNwkAddress(String shortNwkAddress) {
    	this.shortNwkAddress = shortNwkAddress;
    }

    @JsonProperty
    public void setEndpointId(String endpointId) {
    	this.endpointId = endpointId;
    }

    @JsonProperty
    public void setName(String name) {
    	this.name = name;
    }

    @JsonProperty
    public void setOn(boolean on) {
    	this.on = on;
        onLastUpdated = System.currentTimeMillis();
    }

    @JsonProperty
    public void setBrightness(int brightness) {
    	this.brightness = brightness;
        brightnessLastUpdated = System.currentTimeMillis(); 
    }

}