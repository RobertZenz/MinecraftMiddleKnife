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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Deals with the authentication at the Mojang, or any other server.
 */
public class Authentication {

	/**
	 * The default version which will be reported.
	 */
	public static final String launcherVersion = "884";
	/**
	 * The addressof the Mojang server.
	 */
	public static final String mojangServer = "https://login.minecraft.net";

	/**
	 * Authenticate at the given server.
	 * @param address The address of the server.
	 * @param username The username to use.
	 * @param password The password to use.
	 * @param launcherVersion The version to report during authentication.
	 * @return Returns the result of the authentication attempt.
	 * @throws AuthenticationException
	 */
	public static AuthenticationResult authenticate(String address, String username, String password, String launcherVersion) throws AuthenticationException {
		try {
			username = URLEncoder.encode(username, "UTF-8");
			password = URLEncoder.encode(password, "UTF-8");
			launcherVersion = URLEncoder.encode(launcherVersion, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new AuthenticationException("Failed to encode username, password or launcher version!", ex);
		}

		String request = String.format("user=%s&password=%s&version=%s", username, password, launcherVersion);
		String response = httpRequest(address, request);
		String[] splitted = response.split(":");

		if (splitted.length < 5) {
			throw new AuthenticationException(response);
		}

		return new AuthenticationResult(splitted);
	}

	/**
	 * Authenticate at the given server.
	 * @param address The server to authenticate at.
	 * @param credentials The credentials to use.
	 * @return
	 * @throws AuthenticationException
	 */
	public static AuthenticationResult authenticate(String address, Credentials credentials) throws AuthenticationException {
		return authenticate(address, credentials.getUsername(), credentials.getPassword(), launcherVersion);
	}

	/**
	 * Authenticate at the Mojang server.
	 * @param credentials The credentials to use.
	 * @return
	 * @throws AuthenticationException
	 */
	public static AuthenticationResult authenticate(Credentials credentials) throws AuthenticationException {
		return authenticate(mojangServer, credentials.getUsername(), credentials.getPassword(), launcherVersion);
	}

	/**
	 * Authenticate at the Mojang server.
	 * @param username The username to use.
	 * @param password THe password to use.
	 * @return Returns the result of the authentication attempt.
	 * @throws AuthenticationException
	 */
	public static AuthenticationResult authenticate(String username, String password) throws AuthenticationException {
		return authenticate(mojangServer, username, password, launcherVersion);
	}

	private static String httpRequest(String url, String content) throws AuthenticationException {
		byte[] contentBytes = null;
		try {
			contentBytes = content.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new AuthenticationException("Failed to convert content!", ex);
		}

		URLConnection connection = null;
		try {
			connection = new URL(url).openConnection();
		} catch (MalformedURLException ex) {
			throw new AuthenticationException("It wasn't me!", ex);
		} catch (IOException ex) {
			throw new AuthenticationException("Failed to connect to authentication server!", ex);
		}
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", Integer.toString(contentBytes.length));

		try {
			OutputStream requestStream = connection.getOutputStream();
			requestStream.write(contentBytes, 0, contentBytes.length);
			requestStream.close();
		} catch (IOException ex) {
			throw new AuthenticationException("Failed to write request!", ex);
		}

		String response = "";

		try {
			BufferedReader responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			response = responseStream.readLine();
			responseStream.close();
		} catch (IOException ex) {
			throw new AuthenticationException("Failed to read response!", ex);
		}

		return response;
	}

	/**
	 * Sends a keep-alive request to the given server. If it does not throw, it worked.
	 * @param address The address of the server to renew the session at.
	 * @param username The username to use.
	 * @param sessionId The session Id of the session to renew.
	 * @throws AuthenticationException
	 */
	public static void keepAlive(String address, String username, String sessionId) throws AuthenticationException {
		httpRequest(address, String.format("?name={0}&session={1}", username, sessionId));
	}

	/**
	 * Sends a keep-alive request to the given server. If it does not throw, it worked.
	 * @param address The address of the server to renew the session at.
	 * @param authenticationResult The original AuthenticationResult.
	 * @throws AuthenticationException
	 */
	public static void keepAlive(String address, AuthenticationResult authenticationResult) throws AuthenticationException {
		keepAlive(address, authenticationResult.getUsername(), authenticationResult.getSessionId());
	}

	/**
	 * Send a keep-alive request to the Mojang server. If it does not throw, it worked.
	 * @param authenticationResult The original AuthenticationResult.
	 * @throws AuthenticationException
	 */
	public static void keepAlive(AuthenticationResult authenticationResult) throws AuthenticationException {
		keepAlive(mojangServer, authenticationResult.getUsername(), authenticationResult.getSessionId());
	}

	/**
	 * Send a keep-alive request to the Mojang server. If it does not throw, it worked.
	 * @param username The username to use.
	 * @param sessionId The session Id of the session to renew.
	 * @throws AuthenticationException
	 */
	public static void keepAlive(String username, String sessionId) throws AuthenticationException {
		keepAlive(mojangServer, username, sessionId);
	}
}
