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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Occurs when there is a problem when talking with the server.
 */
public class YggdrasilError extends Exception {
	private static final long serialVersionUID = 8796740508892633454L;
	
	private String error;
	private String message;
	private String cause;
	
	public YggdrasilError(String error, String message, String cause) {
		super(error);
		this.error = error;
		this.message = message;
		this.cause = cause;
	}
	
	public YggdrasilError(String message, Throwable cause) {
		super(message, cause);
	}
	
	public YggdrasilError(String msg) {
		super(msg);
	}
	
	public static YggdrasilError fromJSON(String json) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject parent = (JSONObject) parser.parse(json);
		
		String error = (String) parent.get("error");
		String message = (String) parent.get("errorMessage");
		String cause = null;
		if (parent.containsKey("cause")) {
			cause = (String) parent.get("cause");
		}
		
		return new YggdrasilError(error, message, cause);
	}
	
	@Override
	public String toString() {
		return "Error: " + error + "\nMessage: " + message + "\nCause:" + (cause == null ? "NULL" : cause) + "}";
	}
}
