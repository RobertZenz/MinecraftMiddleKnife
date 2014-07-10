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
package org.bonsaimind.minecraftmiddleknife.pre16;

/**
 * Represents an authenticated session.
 */
public final class AuthenticatedSession {
	
	private final long currentVersion;
	private final String downloadTicket;
	private final String username;
	private final String sessionId;
	private final String userId;
	
	/**
	 * @param currentVersion the timestamp of the current version of Minecraft.
	 * @param downloadTicket the download ticket for downloading Minecraft. This
	 *            is deprecated and should only contain the string "deprecated".
	 * @param username
	 * @param sessionId
	 * @param userId
	 */
	public AuthenticatedSession(long currentVersion, String downloadTicket, String username, String sessionId, String userId) {
		this.currentVersion = currentVersion;
		this.downloadTicket = downloadTicket;
		this.username = username;
		this.sessionId = sessionId;
		this.userId = userId;
	}
	
	/**
	 * Returns the UNIX-timestamp of the current version of Minecraft.
	 * 
	 * @return the current version as UNIX-timestamp.
	 */
	public long getCurrentVersion() {
		return currentVersion;
	}
	
	/**
	 * The download ticket to download Minecraft from the servers. This is
	 * deprecated and should only contain the string "deprecated".
	 * 
	 * @return the string "deprecated".
	 */
	public String getDownloadTicket() {
		return downloadTicket;
	}
	
	/**
	 * The (if returned from the server) case corrected username.
	 * 
	 * @return the username.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * The ID of the current session.
	 * 
	 * @return the session id.
	 */
	public String getSessionId() {
		return sessionId;
	}
	
	/**
	 * The (unique) user ID.
	 * 
	 * @return the user ID.
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * Returns the string representation of this session. It's in the format as
	 * returned by the server:
	 * {@code currentVersion:downloadTicket:username:sessionId:userId}
	 * 
	 * @return the string representation.
	 */
	@Override
	public String toString() {
		return String.format("%s:%s:%s:%s:%s", Long.valueOf(currentVersion), downloadTicket, username, sessionId, userId);
	}
	
	/**
	 * Creates an AuthenticatedSession from the string representation.
	 * 
	 * @param value needs to be in the format
	 *            {@code currentVersion:downloadTicket:username:sessionId:userId"}
	 *            .
	 * @return the AuthenticatedSession for this string representation.
	 */
	public static AuthenticatedSession fromString(String value) {
		if (value == null) {
			throw new IllegalArgumentException("value is not allowed to be null.");
		}
		
		String[] splittedValue = value.split(":");
		
		if (splittedValue.length != 5) {
			throw new IllegalArgumentException("value is not in the expected format \"currentVersion:downloadTicket:username:sessionId:userId\" but was \""
					+ value + "\".");
		}
		
		return new AuthenticatedSession(Long.parseLong(splittedValue[0]), splittedValue[1], splittedValue[2], splittedValue[3], splittedValue[4]);
	}
}
