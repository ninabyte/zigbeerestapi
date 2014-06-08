package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class Groups {

	private ArrayList<Group> groups;

	public Groups() {
		groups = new ArrayList<Group>();
	}

	@JsonProperty
	public ArrayList<String> getAllGroups() {
		ArrayList<String> arrayId = new ArrayList<String>();
		for (Group group : groups){
			arrayId.add("" + group.getId());
		}
		return arrayId;
	}

	@JsonProperty
	public Group getGroup(long id) {
		for (Group group: groups){
			if (group.getId() == id) {
				return group;
			}
		}
		return null;
	}

	public void add(Group group) {
		groups.add(group);
	}
}