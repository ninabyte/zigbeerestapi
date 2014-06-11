package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.ArrayList;

public class Group {
	@JsonProperty
	private long id;
	
	@JsonProperty
	private String name;

    @JsonProperty
    private List<Long> lights;

    @JsonIgnore
    private long lightsLastUpdated;

    @JsonProperty
	private State latestState;

	@JsonIgnore
	private Scenes scenes;

	public Group(long id, String name) {
		this.id = id;
		this.name = name;
		lights = new ArrayList<Long>();
		latestState = new State();
		scenes = new Scenes();
	}

	public long getId() {
		return id;
	}

	public String getGroupAddress() {
		return ""+id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Long> getLights() {
		return lights;
	}

	public void addLight(Long lightId) {
		if (!lights.contains(lightId)) {
			lights.add(lightId);
		}
		lightsLastUpdated = System.currentTimeMillis();
	}

	public boolean removeLight(long lightId) {
		boolean status = lights.remove(lightId);
		lightsLastUpdated = System.currentTimeMillis();
		return status;
	}

	public long getLightsLastUpdated() {
		return lightsLastUpdated;
	}

	@JsonIgnore
    public boolean isOn() {
    	return latestState.isOn();
    }
    
    @JsonIgnore
    public int getBrightness() {
    	return latestState.getBrightness();
    }
    
    @JsonIgnore
    public long getLatestStateOnLastUpdated() {
        return latestState.getOnLastUpdated();
    }
    
    @JsonIgnore
    public long getLatestStateBrightnessLastUpdated() {
        return latestState.getBrightnessLastUpdated();
    }

    @JsonIgnore
    public void setLatestStateOn(boolean on) {
    	latestState.setOn(on);
    }

    @JsonIgnore
    public void setLatestStateBrightness(int brightness) {
        latestState.setBrightness(brightness);
    }

    @JsonIgnore
    public Scenes getScenes() {
    	return scenes;
    }

    @JsonProperty
    public List<String> getSceneIds() {
    	return scenes.getAllScenes();
    }
    	
}