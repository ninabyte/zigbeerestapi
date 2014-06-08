package com.nina.zigbeerestapi.core;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
public class Light{

    @JsonProperty
	private long id;
	@JsonIgnore
    private String shortNwkAddress;
    @JsonProperty
    private String endpointId;
    @JsonProperty
	private String name;
    @JsonProperty
	private boolean on;
    @JsonProperty
    private String stackVersion = "not specified";
    @JsonProperty
    private String modelId = "not specified";
    @JsonIgnore
    private long onLastUpdated = 0L;
    @JsonIgnore
    private long brightnessLastUpdated = 0L;
    @JsonIgnore
    private long stackVersionLastUpdated = 0L;
    @JsonIgnore
    private long modelIdLastUpdated = 0L;
	
    @Min(0)
	@Max(255)
    @JsonProperty
	private int brightness;
	
    public Light(long id) {
		this.id = id;
	}
	public long getId() {
        return id;
    }
    
    public String getShortNwkAddress() {
    	return shortNwkAddress;
    }
    
    public String getEndpointId() {
    	return endpointId;
    }
    
    public String getName() {
    	return name;
    }
    
    public boolean isOn() {
    	return on;
    }
    
    public String getStackVersion() {
        return stackVersion;
    }
    
    public String getModelId() {
        return modelId;
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
    
    public long getModelIdLastUpdated() {
        return modelIdLastUpdated;
    }
    
    public long getStackVersionLastUpdated() {
        return stackVersionLastUpdated;
    }
    
    public void setShortNwkAddress(String shortNwkAddress) {
    	this.shortNwkAddress = shortNwkAddress;
    }
    
    public void setEndpointId(String endpointId) {
    	this.endpointId = endpointId;
    }
    
    public void setName(String name) {
    	this.name = name;
    }

    public void setOn(boolean on) {
    	this.on = on;
        onLastUpdated = System.currentTimeMillis();
    }
    
    public void setStackVersion(String stackVersion) {
        this.stackVersion = stackVersion;
        stackVersionLastUpdated = System.currentTimeMillis();
    }
    
    public void setModelId(String modelId) {
        this.modelId = modelId;
        modelIdLastUpdated = System.currentTimeMillis();
    }
    
    public void setBrightness(int brightness) {
    	this.brightness = brightness;
        brightnessLastUpdated = System.currentTimeMillis(); 
    }
}