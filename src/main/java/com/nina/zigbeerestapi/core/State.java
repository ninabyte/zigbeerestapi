package com.nina.zigbeerestapi.core;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

public class State {
	
    @JsonProperty
	private boolean on = false;
	@JsonIgnore
    private long onLastUpdated = 0L;
    @JsonIgnore
    private long brightnessLastUpdated = 0L;

    @Min(0)
	@Max(254)
    @JsonProperty
	private int brightness = 0;

    public boolean isOn() {
    	return on;
    }

    public int getBrightness() {
    	return brightness;
    }
    
    public long getOnLastUpdated() {
        return onLastUpdated;
    }
    
    public long getBrightnessLastUpdated() {
        return brightnessLastUpdated;
    }

    public void setOn(boolean on) {
    	this.on = on;
        onLastUpdated = System.currentTimeMillis();
    }

    public void setBrightness(int brightness) {
    	this.brightness = brightness;
        brightnessLastUpdated = System.currentTimeMillis(); 
    }
}