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

/**
 * Deals with the new authentication function called...Yggdrasil.
 */
public class Yggdrasil extends Credentials {

	public static final String AGENT_NAME = "MinecraftMiddleKnife";
	public static final int AGENT_VERSION = 884;
	public static final String ENDPOINT_AUTHENTICATE = "authenticate";
	public static final String ENDPOINT_REFRESH = "refresh";
	public static final String ENDPOINT_VALIDATE = "validate";
	public static final String MOJANG_SERVER = "https://authserver.mojang.com/";
	private String agentName = AGENT_NAME;
	private int agentVersion = AGENT_VERSION;

	public Yggdrasil() {
	}

	public Yggdrasil(Credentials credentials) {
		super(credentials.getUsername(), credentials.getPassword());
	}

	public Yggdrasil(String username, String password) {
		super(username, password);
	}

	public String getAgentName() {
		return agentName;
	}

	public int getAgentVersion() {
		return agentVersion;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public void setAgentVersion(int agentVersion) {
		this.agentVersion = agentVersion;
	}
}
