/*
 * Copyright 2013 Robert 'Bobby' Zenz. All rights reserved.
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
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
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
package org.bonsaimind.minecraftmiddleknife;

import java.util.UUID;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Deals with the new authentication system called...Yggdrasil.
 */
public class Yggdrasil extends Credentials {

	public static final String AGENT_NAME = "MinecraftMiddleKnife";
	public static final int AGENT_VERSION = 884;
	public static final String ENDPOINT_AUTHENTICATE = "authenticate";
	public static final String ENDPOINT_REFRESH = "refresh";
	public static final String ENDPOINT_VALIDATE = "validate";
	public static final String MOJANG_SERVER = "https://authserver.mojang.com/";
	private String accessToken;
	private String agentName = AGENT_NAME;
	private int agentVersion = AGENT_VERSION;
	private String clientToken = UUID.randomUUID().toString();
	private String realUsername;
	private String userId;

	public Yggdrasil() {
	}

	public Yggdrasil(Credentials credentials) {
		super(credentials.getUsername(), credentials.getPassword());
	}

	public Yggdrasil(String username, String password) {
		super(username, password);
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getAgentName() {
		return agentName;
	}

	public int getAgentVersion() {
		return agentVersion;
	}

	public String getClientToken() {
		return clientToken;
	}

	public String getRealUsername() {
		return realUsername;
	}

	public String getUserId() {
		return userId;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public void setAgentVersion(int agentVersion) {
		this.agentVersion = agentVersion;
	}

	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}

	public void setRealUsername(String realUsername) {
		this.realUsername = realUsername;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	private String createAuthenticationJSON() {
		JSONObject parent = new JSONObject();

		JSONObject agent = new JSONObject();
		agent.put("name", getAgentName());
		agent.put("version", getAgentVersion());
		parent.put("agent", agent);

		parent.put("username", getUsername());
		parent.put("password", getPassword());
		parent.put("clientToken", getClientToken());

		return parent.toJSONString();
	}

	private String createRefreshJSON() {
		JSONObject parent = new JSONObject();

		parent.put("accessToken", getAccessToken());
		parent.put("clientToken", getClientToken());

		JSONObject selectedProfile = new JSONObject();
		selectedProfile.put("id", getUserId());
		selectedProfile.put("name", getUsername());
		parent.put("selectedProfile", selectedProfile);

		return parent.toJSONString();
	}

	private String createValidationJSON() {
		JSONObject parent = new JSONObject();

		parent.put("accessToken", getAccessToken());

		return parent.toJSONString();
	}

	private void parseAuthenticationJSON(String response) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject parent = (JSONObject) parser.parse(response);
		setAccessToken((String) parent.get("accessToken"));

		JSONObject selectedProfile = (JSONObject) parent.get("selectedProfile");
		setUserId((String) selectedProfile.get("id"));
		setRealUsername((String) selectedProfile.get("name"));
	}

	private void parseErrorJSON(String response) throws ParseException {
		// Stand tall for the man next door!
	}
	
	private void parseRefreshJSON(String response) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject parent = (JSONObject) parser.parse(response);
		setAccessToken((String) parent.get("accessToken"));

		JSONObject selectedProfile = (JSONObject) parent.get("selectedProfile");
		setUserId((String) selectedProfile.get("id"));
		setRealUsername((String) selectedProfile.get("name"));
	}
}
