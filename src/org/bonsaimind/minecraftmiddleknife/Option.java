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
package org.bonsaimind.minecraftmiddleknife;

/**
 * Represents an option, consisting of a key and a value, in the option file.
 */
public final class Option {
	
	public static final String KEY_VALUE_SEPARATOR = ":";
	
	private final String key;
	private final String value;
	
	/**
	 * Creates a new instance of {@link Option}.
	 * 
	 * @param key the key to use. It is not allowed to be {@code null} or empty.
	 * @param value the value to use. It is not allowed to be {@code null}.
	 * @throws IllegalArgumentException if the key is {@code null} or empty or
	 *             the value is {@code null}.
	 */
	public Option(String key, String value) {
		if (key == null) {
			throw new IllegalArgumentException("key is null.");
		}
		if (key.isEmpty()) {
			throw new IllegalArgumentException("key is empty.");
		}
		if (value == null) {
			throw new IllegalArgumentException("value is null.");
		}
		
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	/**
	 * Returns the string representation of this Option. This is in the format
	 * {@code key + KEY_VALUE_SEPARATOR + value}.
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return key + KEY_VALUE_SEPARATOR + value;
	}
	
	/**
	 * Returns the {@link Option} which is represented by the given
	 * key-value-pair.
	 * 
	 * @param keyValuePair the key-value-pair in the format {@code key:value}.
	 * @return
	 * @throws if keyValuePair is {@code null}, empty, does not contain the
	 *             {@link Option#KEY_VALUE_SEPARATOR} or if the key-part is
	 *             empty.
	 */
	public static Option fromString(String keyValuePair) {
		if (keyValuePair == null) {
			throw new IllegalArgumentException("keyValuePair is null.");
		}
		
		if (keyValuePair.isEmpty()) {
			throw new IllegalArgumentException("keyValuePair is empty.");
		}
		
		if (!keyValuePair.contains(KEY_VALUE_SEPARATOR)) {
			throw new IllegalArgumentException("keyValuePair does not contain the separator.");
		}
		
		String[] pair = keyValuePair.split(KEY_VALUE_SEPARATOR);
		if (pair[0].isEmpty()) {
			throw new IllegalArgumentException("key is empty.");
		}
		
		return new Option(pair[0], pair[1]);
	}
}
