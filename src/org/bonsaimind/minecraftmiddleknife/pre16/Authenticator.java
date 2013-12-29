/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import org.bonsaimind.minecraftmiddleknife.Credentials;

/**
 * Static helper to create an AuthenticatedSession.
 */
public final class Authenticator {

	/**
	 * The default version which will be reported.
	 */
	public static final String DEFAULT_LAUNCHER_VERSION = "884";
	/**
	 * The addressof the Mojang server.
	 */
	public static final String MOJANG_SERVER = "https://login.minecraft.net";

	/**
	 * Authenticates at the Mojang server and authenticates with the given credentials.
	 *
	 * This will throw an AuthenticationException if something goes wrong,
	 * this includes wrong password or username.
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
	 * Authenticates at the given server and authenticates with the given credentials.
	 *
	 * This will throw an AuthenticationException if something goes wrong,
	 * this includes wrong password or username.
	 * @param server The server to connect to.
	 * @param launcherVersion The launcher version to report.
	 * @param credentials The credentials which will be used.
	 * @return The authenticated session.
	 * @throws AuthenticationException
	 */
	public static AuthenticatedSession authenticate(URL server, String launcherVersion, Credentials credentials) throws AuthenticationException {
		String request;
		try {
			request = String.format(
					"user=%s&password=%s&version=%s",
					URLEncoder.encode(credentials.getUsername(), "UTF-8"),
					URLEncoder.encode(credentials.getPassword(), "UTF-8"),
					URLEncoder.encode(launcherVersion, "UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			throw new AuthenticationException("Building of the request string failed.", ex);
		}
		String response;
		try {
			response = httpRequest(server, request);
		} catch (UnsupportedEncodingException ex) {
			throw new AuthenticationException("Authentication failed.", ex);
		} catch (MalformedURLException ex) {
			throw new AuthenticationException("Authentication failed.", ex);
		} catch (IOException ex) {
			throw new AuthenticationException("Authentication failed.", ex);
		}

		try {
			return AuthenticatedSession.fromString(response);
		} catch (IllegalArgumentException ex) {
			throw new AuthenticationException(response, ex);
		}
	}

	/**
	 * Sends a keep-alive request to the Mojang server.
	 * @param authenticatedSession
	 * @throws AuthenticationException
	 */
	public static void keepAlive(AuthenticatedSession authenticatedSession) throws AuthenticationException {
		try {
			keepAlive(new URL(MOJANG_SERVER), authenticatedSession);
		} catch (MalformedURLException ex) {
			throw new AuthenticationException("Failed to build URL for the Mojand server.", ex);
		}
	}

	/**
	 * Sends a keep-alive reuqest to the given server.
	 * @param server
	 * @param authenticatedSession
	 * @throws AuthenticationException
	 */
	public static void keepAlive(URL server, AuthenticatedSession authenticatedSession) throws AuthenticationException {
		String request;
		try {
			request = String.format(
					"?name={0}&session={1}",
					URLEncoder.encode(authenticatedSession.getUsername(), "UTF-8"),
					URLEncoder.encode(authenticatedSession.getSessionId(), "UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			throw new AuthenticationException("Building of the request string failed.", ex);
		}
		try {
			httpRequest(server, request);
		} catch (UnsupportedEncodingException ex) {
			throw new AuthenticationException("Failed to renew session.", ex);
		} catch (IOException ex) {
			throw new AuthenticationException("Failed to renew session.", ex);
		}
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
		throw new AssertionError();
	}
}
