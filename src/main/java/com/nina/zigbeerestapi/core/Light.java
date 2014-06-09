package com.nina.zigbeerestapi.core;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Light{

    @JsonProperty
	private long id;
    @JsonProperty
    private String name;
	@JsonIgnore
    private String shortNwkAddress;
    @JsonIgnore
    private String endpointId;
    @JsonProperty
    private String stackVersion = "not specified";
    @JsonProperty
    private String modelId = "not specified";
    @JsonIgnore
    private long stackVersionLastUpdated = 0L;
    @JsonIgnore
    private long modelIdLastUpdated = 0L;
    @JsonProperty
    private State state;
	
    public Light(long id) {
		this.id = id;
        state = new State();
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
    
    @JsonIgnore
    public boolean isOn() {
    	return state.isOn();
    }
    
    public String getStackVersion() {
        return stackVersion;
    }
    
    public String getModelId() {
        return modelId;
    }
    
    @JsonIgnore
    public int getBrightness() {
    	return state.getBrightness();
    }
    
    @JsonIgnore
    public long getOnLastUpdated() {
        return state.getOnLastUpdated();
    }
    
    @JsonIgnore
    public long getBrightnessLastUpdated() {
        return state.getBrightnessLastUpdated();
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

    @JsonIgnore
    public void setOn(boolean on) {
    	state.setOn(on);
    }

    @JsonIgnore
    public void setBrightness(int brightness) {
        state.setBrightness(brightness);
    }
    
    public void setStackVersion(String stackVersion) {
        this.stackVersion = stackVersion;
        stackVersionLastUpdated = System.currentTimeMillis();
    }
    
    public void setModelId(String modelId) {
        this.modelId = modelId;
        modelIdLastUpdated = System.currentTimeMillis();
    }
    
}