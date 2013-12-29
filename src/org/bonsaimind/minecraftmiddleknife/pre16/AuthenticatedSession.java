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

	private long currentVersion;
	private String downloadTicket;
	private String username;
	private String sessionId;
	private String userId;

	/**
	 *
	 * @param currentVersion The timestamp of the current version of Minecraft.
	 * @param downloadTicket The download ticket for downloading Minecraft. This is deprecated.
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
	 * Creates an AuthenticatedSession from the string representation.
	 * @param value Needs to be in the format "currentVersion:downloadTicket:username:sessionId:userId".
	 * @return The AuthenticatedSession for this string representation.
	 */
	public static AuthenticatedSession fromString(String value) {
		if (value == null) {
			throw new IllegalArgumentException("value is not allowed to be null.");
		}

		String[] splittedValue = value.split(":");

		if (splittedValue.length != 5) {
			throw new IllegalArgumentException("value is not in the expected format \"currentVersion:downloadTicket:username:sessionId:userId\" but was \"" + value + "\".");
		}

		return new AuthenticatedSession(Long.parseLong(splittedValue[0]),
				splittedValue[1],
				splittedValue[2],
				splittedValue[3],
				splittedValue[4]);
	}

	/**
	 * Returns the UNIX-timestamp of the current version of Minecraft.
	 * @return The current version as UNIX-timestamp.
	 */
	public long getCurrentVersion() {
		return currentVersion;
	}

	/**
	 * The download ticket to download Minecraft from the servers.
	 *
	 * This is deprecated and should only contain the string "deprecated".
	 * @return The string "deprecated".
	 */
	public String getDownloadTicket() {
		return downloadTicket;
	}

	/**
	 * The (if returned from the server) case corrected username.
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * The ID of the current session.
	 * @return The session id.
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * The (inique) user ID.
	 * @return The user ID.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Returns the string representation of this session.
	 *
	 * It's in the format as returned by the server: currentVersion:downloadTicket:username:sessionId:userId
	 * @return The string representation.
	 */
	@Override
	public String toString() {
		return String.format("%s:%s:%s:%s:%s", currentVersion, downloadTicket, username, sessionId, userId);
	}
}
