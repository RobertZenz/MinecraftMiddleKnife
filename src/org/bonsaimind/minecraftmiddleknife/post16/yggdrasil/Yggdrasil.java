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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.parser.ParseException;

/**
 * Static utility class that deals with everything around Yggdrasil.
 */
public final class Yggdrasil {
	
	private static final URL MOJANG_AUTHENTICATION_URL;
	private static final URL MOJANG_INVALIDATE_URL;
	private static final URL MOJANG_REFRESH_URL;
	private static final URL MOJANG_SIGNOUT_URL;
	private static final URL MOJANG_VALIDATE_URL;
	
	static {
		final String mojangServer = "https://authserver.mojang.com/";
		
		try {
			MOJANG_AUTHENTICATION_URL = new URL(mojangServer + "authenticate");
			MOJANG_INVALIDATE_URL = new URL(mojangServer + "invalidate");
			MOJANG_REFRESH_URL = new URL(mojangServer + "refresh");
			MOJANG_SIGNOUT_URL = new URL(mojangServer + "signout");
			MOJANG_VALIDATE_URL = new URL(mojangServer + "validate");
		} catch (MalformedURLException ex) {
			throw new AssertionError("Shouldn't happen...really.", ex);
		}
	}
	
	public static AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws YggdrasilError {
		return authenticate(MOJANG_AUTHENTICATION_URL, authenticationRequest);
	}
	
	public static AuthenticationResponse authenticate(URL server, AuthenticationRequest authenticationRequest) throws YggdrasilError {
		String response = httpRequestExceptionWrapped(server, authenticationRequest.toString());
		
		try {
			return AuthenticationResponse.fromJSON(response);
		} catch (ParseException ex) {
			throw new YggdrasilError("Parsing the response failed.", ex);
		}
	}
	
	public static void invalidate(InvalidationRequest invalidationRequest) throws YggdrasilError {
		invalidate(MOJANG_INVALIDATE_URL, invalidationRequest);
	}
	
	public static void invalidate(URL server, InvalidationRequest invalidationRequest) throws YggdrasilError {
		httpRequestExceptionWrapped(server, invalidationRequest.toString());
	}
	
	public static RefreshResponse refresh(RefreshRequest refreshRequest) throws YggdrasilError {
		return refresh(MOJANG_REFRESH_URL, refreshRequest);
	}
	
	public static RefreshResponse refresh(URL server, RefreshRequest refreshRequest) throws YggdrasilError {
		String response = httpRequestExceptionWrapped(server, refreshRequest.toString());
		
		try {
			return RefreshResponse.fromJSON(response);
		} catch (ParseException ex) {
			throw new YggdrasilError("Parsing the response failed.", ex);
		}
	}
	
	public static void signout(SignoutRequest signoutRequest) throws YggdrasilError {
		signout(MOJANG_SIGNOUT_URL, signoutRequest);
	}
	
	public static void signout(URL server, SignoutRequest signoutRequest) throws YggdrasilError {
		httpRequestExceptionWrapped(server, signoutRequest.toString());
	}
	
	public static void validate(ValidationRequest validationRequest) throws YggdrasilError {
		validate(MOJANG_VALIDATE_URL, validationRequest);
	}
	
	public static void validate(URL server, ValidationRequest validationRequest) throws YggdrasilError {
		httpRequestExceptionWrapped(server, validationRequest.toString());
	}
	
	private static String httpRequest(URL url, String content) throws YggdrasilError, UnsupportedEncodingException, IOException, ParseException {
		byte[] contentBytes = content.getBytes("UTF-8");
		
		URLConnection connection = url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Content-Length", Integer.toString(contentBytes.length));
		
		OutputStream requestStream = connection.getOutputStream();
		requestStream.write(contentBytes, 0, contentBytes.length);
		requestStream.close();
		
		String response = "";
		BufferedReader responseStream;
		if (((HttpURLConnection) connection).getResponseCode() == 200) {
			responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		} else {
			responseStream = new BufferedReader(new InputStreamReader(((HttpURLConnection) connection).getErrorStream(), "UTF-8"));
		}
		
		response = responseStream.readLine();
		responseStream.close();
		
		if (((HttpURLConnection) connection).getResponseCode() != 200) {
			throw YggdrasilError.fromJSON(response);
		}
		
		return response;
	}
	
	private static String httpRequestExceptionWrapped(URL url, String content) throws YggdrasilError {
		try {
			return httpRequest(url, content);
		} catch (UnsupportedEncodingException ex) {
			throw new YggdrasilError("Action failed.", ex);
		} catch (IOException ex) {
			throw new YggdrasilError("Action failed.", ex);
		} catch (ParseException ex) {
			throw new YggdrasilError("Action failed.", ex);
		}
	}
	
	private Yggdrasil() {
		throw new AssertionError(); // Shouldn't do this.
	}
}
