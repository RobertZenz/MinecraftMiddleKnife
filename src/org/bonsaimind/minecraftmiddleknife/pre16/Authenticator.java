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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonsaimind.minecraftmiddleknife.Credentials;

/**
 * Static helper to create an AuthenticatedSession.
 */
public final class Authenticator {
	
	/**
	 * The default version which will be reported.
	 */
	public static final String DEFAULT_LAUNCHER_VERSION = "884";
	private static final Logger LOGGER = Logger.getLogger(Authenticator.class.getName());
	/**
	 * The addressof the Mojang server.
	 */
	public static final String MOJANG_SERVER = "https://login.minecraft.net";
	
	/**
	 * Authenticates at the Mojang server and authenticates with the given
	 * credentials. This will throw an AuthenticationException if something goes
	 * wrong, this includes wrong password or username.
	 * 
	 * @param credentials
	 * @return
	 * @throws AuthenticationException
	 */
	public static AuthenticatedSession authenticate(Credentials credentials) throws AuthenticationException {
		try {
			return authenticate(new URL(MOJANG_SERVER), DEFAULT_LAUNCHER_VERSION, credentials);
		} catch (MalformedURLException ex) {
			throw new AuthenticationException("Faild to build the URL for the Mojang server.", ex);
		}
	}
	
	/**
	 * Authenticates at the given server and authenticates with the given
	 * credentials. This will throw an AuthenticationException if something goes
	 * wrong, this includes wrong password or username.
	 * 
	 * @param server The server to connect to.
	 * @param launcherVersion The launcher version to report.
	 * @param credentials The credentials which will be used.
	 * @return The authenticated session.
	 * @throws AuthenticationException
	 */
	public static AuthenticatedSession authenticate(URL server, String launcherVersion, Credentials credentials) throws AuthenticationException {
		String request;
		try {
			request = String.format("user=%s&password=%s&version=%s", URLEncoder.encode(credentials.getUsername(), "UTF-8"),
					URLEncoder.encode(credentials.getPassword(), "UTF-8"), URLEncoder.encode(launcherVersion, "UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			throw new AuthenticationException("Building of the request string failed.", ex);
		}
		String response;
		try {
			response = httpRequest(server, request);
		} catch (UnsupportedEncodingException e) {
			throw new AuthenticationException("Authentication failed.", e);
		} catch (MalformedURLException e) {
			throw new AuthenticationException("Authentication failed.", e);
		} catch (IOException e) {
			throw new AuthenticationException("Authentication failed.", e);
		}
		
		try {
			return AuthenticatedSession.fromString(response);
		} catch (IllegalArgumentException ex) {
			throw new AuthenticationException(response, ex);
		}
	}
	
	/**
	 * Sends a keep-alive request to the Mojang server.
	 * 
	 * @param authenticatedSession
	 * @throws AuthenticationException
	 */
	public static void keepAlive(AuthenticatedSession authenticatedSession) throws AuthenticationException {
		try {
			keepAlive(new URL(MOJANG_SERVER), authenticatedSession);
		} catch (MalformedURLException e) {
			throw new AuthenticationException("Failed to build URL for the Mojand server.", e);
		}
	}
	
	/**
	 * Sends a keep-alive reuqest to the given server.
	 * 
	 * @param server
	 * @param authenticatedSession
	 * @throws AuthenticationException
	 */
	public static void keepAlive(URL server, AuthenticatedSession authenticatedSession) throws AuthenticationException {
		String request;
		try {
			request = String.format("?name={0}&session={1}", URLEncoder.encode(authenticatedSession.getUsername(), "UTF-8"),
					URLEncoder.encode(authenticatedSession.getSessionId(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new AuthenticationException("Building of the request string failed.", e);
		}
		try {
			httpRequest(server, request);
		} catch (UnsupportedEncodingException e) {
			throw new AuthenticationException("Failed to renew session.", e);
		} catch (IOException e) {
			throw new AuthenticationException("Failed to renew session.", e);
		}
	}
	
	/**
	 * Schedules a keep-alive task.
	 * 
	 * @param authenticatedSession the {@link AuthenticatedSession} that will be
	 *            used for the keep-alive.
	 * @param delay delay in milliseconds before task is to be executed.
	 * @param period time in milliseconds between successive task executions.
	 * @return the {@link Timer} which has the scheduled task.
	 */
	public static Timer scheduleKeepAlive(final AuthenticatedSession authenticatedSession, long delay, long period) {
		Timer timer = new Timer("MinecraftMiddleKnife Authentication Keep Alive Timer", true);
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				try {
					keepAlive(authenticatedSession);
				} catch (AuthenticationException e) {
					LOGGER.log(Level.SEVERE, "Authentication Keep Alive failed.", e);
				}
			}
		}, delay, period);
		
		return timer;
	}
	
	private static String httpRequest(URL url, String content) throws UnsupportedEncodingException, IOException {
		byte[] contentBytes = content.getBytes("UTF-8");
		
		URLConnection connection = url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", Integer.toString(contentBytes.length));
		
		OutputStream requestStream = connection.getOutputStream();
		requestStream.write(contentBytes, 0, contentBytes.length);
		requestStream.close();
		
		String response = "";
		
		BufferedReader responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		response = responseStream.readLine();
		responseStream.close();
		
		return response;
	}
	
	private Authenticator() {
		throw new AssertionError(); // Shouldn't happen.
	}
}
