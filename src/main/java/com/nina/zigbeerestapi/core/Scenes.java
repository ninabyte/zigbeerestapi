package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Scenes {
	private List<Scene> scenes;
	private long scenesLastDeleted;
	private final AtomicLong counter;

	public Scenes() {
		scenes = new ArrayList<Scene>();
		counter = new AtomicLong();
	}

	public Scene createNewScene() {
		long id = counter.incrementAndGet();
		Scene newScene = new Scene(id, "Scene " + id);
		scenes.add(newScene);
		return newScene;
	}

	@JsonProperty
	public List<String> getAllScenes() {
		List<String> arrayId = new ArrayList<String>();
		for (Scene scene : scenes){
			arrayId.add("" + scene.getId());
		}
		return arrayId;
	}

	@JsonProperty
	public Scene getSceneById(long id) {
		for (Scene scene: scenes){
			if (scene.getId() == id) {
				return scene;
			}
		}
		return null;
	}

	@JsonIgnore
	public long getScenesLastDeleted() {
		return scenesLastDeleted;
	}
	
	public void remove(long sceneId) {
		for (int i=0; i<scenes.size(); i++) {
			Scene scene = scenes.get(i);
			if(scene.getId() == sceneId) {
				scenes.remove(i);
				scenesLastDeleted = System.currentTimeMillis();
				return;
			}
		}
	}
}