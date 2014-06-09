package com.nina.zigbeerestapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;

public class Groups {

	private List<Group> groups;

	public Groups() {
		groups = new ArrayList<Group>();
	}

	@JsonProperty
	public List<String> getAllGroups() {
		List<String> arrayId = new ArrayList<String>();
		for (Group group : groups){
			arrayId.add("" + group.getId());
		}
		return arrayId;
	}

	@JsonProperty
	public Group getGroupById(long id) {
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

	public void remove(long groupId) {
		for (int i=0; i<groups.size(); i++) {
			Group group = groups.get(i);
			if(group.getId() == groupId) {
				groups.remove(i);
				return;
			}
		}
	}
}