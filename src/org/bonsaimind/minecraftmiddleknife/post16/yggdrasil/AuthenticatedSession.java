/*
 * Copyright 2012 Robert 'Bobby' Zenz. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Robert 'Bobby' Zenz ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Robert 'Bobby' Zenz OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Robert 'Bobby' Zenz.
 */
package org.bonsaimind.minecraftmiddleknife.post16.yggdrasil;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Represents an authenticated session.
 */
public class AuthenticatedSession {

	private final String accessToken;
	private final String clientToken;
	private final List<Profile> availableProfiles = new ArrayList<Profile>();
	private final Profile selectedProfile;

	public AuthenticatedSession(String accessToken, String clientToken, List<Profile> availableProfiles, Profile selectedProfile) {
		this.accessToken = accessToken;
		this.clientToken = clientToken;
		if (availableProfiles != null) {
			this.availableProfiles.addAll(availableProfiles);
		}
		this.selectedProfile = selectedProfile;
	}

	public static AuthenticatedSession fromJSON(String json) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject parent = (JSONObject) parser.parse(json);

		String accessToken = (String) parent.get("accessToken");
		String clientToken = (String) parent.get("clientToken");

		List<Profile> profiles = new ArrayList<Profile>();
		if (parent.containsKey("availableProfiles")) {
			for (Object item : (JSONArray) parent.get("availableProfiles")) {
				JSONObject jsonItem = (JSONObject) item;
				profiles.add(new Profile((String) jsonItem.get("id"), (String) jsonItem.get("name")));
			}
		}
		JSONObject selectedProfile = (JSONObject) parent.get("selectedProfile");
		Profile profile = new Profile((String) selectedProfile.get("id"), (String) selectedProfile.get("name"));

		return new AuthenticatedSession(accessToken, clientToken, profiles, profile);
	}

	public String getAccessToken() {
		return accessToken;
	}

	public List<Profile> getAvailableProfiles() {
		return new ArrayList<Profile>(availableProfiles);
	}

	public String getClientToken() {
		return clientToken;
	}

	public Profile getSelectedProfile() {
		return selectedProfile;
	}
}
