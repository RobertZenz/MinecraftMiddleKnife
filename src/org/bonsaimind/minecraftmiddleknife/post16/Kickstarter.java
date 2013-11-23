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
package org.bonsaimind.minecraftmiddleknife.post16;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

/**
 * Allows you to start Minecraft.
 */
public final class Kickstarter {

	public static final String ARGUMENT_ACCESS_TOKEN = "accessToken";
	public static final String ARGUMENT_ASSETS_DIR = "assetsDir";
	/**
	 * Does not need a value.
	 */
	public static final String ARGUMENT_DEMO = "demo";
	/**
	 * Does not need a value.
	 */
	public static final String ARGUMENT_FULLSCREEN = "fullscreen";
	public static final String ARGUMENT_GAME_DIR = "gameDir";
	public static final String ARGUMENT_HEIGHT = "height";
	public static final String ARGUMENT_PORT = "port";
	public static final String ARGUMENT_PROXY_HOST = "proxyHost";
	public static final String ARGUMENT_PROXY_PASS = "proxyPass";
	public static final String ARGUMENT_PROXY_PORT = "proxyPort";
	public static final String ARGUMENT_PROXY_USER = "proxyUser";
	public static final String ARGUMENT_RESOURCE_PACK_DIR = "resourcePackDir";
	public static final String ARGUMENT_SERVER = "server";
	public static final String ARGUMENT_USERNAME = "username";
	public static final String ARGUMENT_UUID = "uuid";
	public static final String ARGUMENT_VERSION = "version";
	public static final String ARGUMENT_WIDTH = "width";
	public static final String MAIN_CLASS = "net.minecraft.client.main.Main";
	public static final String MAIN_METHOD = "main";

	private Kickstarter() {
		throw new AssertionError(); // Shouldn't happen.
	}

	/**
	 * Calls the Minecraft main method.
	 * @param arguments
	 * @throws RunException
	 */
	public static void run(Argument... arguments) throws RunException {
		run(Argument.toStrings(arguments));
	}

	/**
	 * Calls the Minecraft main method.
	 * @param arguments
	 * @throws RunException
	 */
	public static void run(String... arguments) throws RunException {
		run(MAIN_CLASS, MAIN_METHOD, arguments);
	}

	/**
	 * Calls the given method in the given class, but converts the arguments first to Strings.
	 * @param mainClass
	 * @param mainMethod
	 * @param arguments
	 * @throws RunException
	 */
	public static void run(String mainClass, String mainMethod, Argument... arguments) throws RunException {
		run(mainClass, mainMethod, Argument.toStrings(arguments));
	}

	/**
	 * Calls the given method in the given class with the given arguments.
	 * Yeah, I suck at JavaDoc.
	 * @param mainClass
	 * @param mainMethod
	 * @param arguments
	 * @throws RunException
	 */
	public static void run(String mainClass, String mainMethod, String... arguments) throws RunException {
		URLClassLoader loader = (URLClassLoader) Thread.currentThread().getContextClassLoader();

		try {
			Class minecraftMainClass = loader.loadClass(mainClass);
			Method minecraftMainMethod = minecraftMainClass.getMethod(mainMethod, String[].class);
			minecraftMainMethod.invoke(null, (Object) (arguments));
		} catch (NoSuchMethodException ex) {
			throw new RunException("Failed to start Minecraft.", ex);
		} catch (SecurityException ex) {
			throw new RunException("Failed to start Minecraft.", ex);
		} catch (IllegalAccessException ex) {
			throw new RunException("Failed to start Minecraft.", ex);
		} catch (IllegalArgumentException ex) {
			throw new RunException("Failed to start Minecraft.", ex);
		} catch (InvocationTargetException ex) {
			throw new RunException("Failed to start Minecraft.", ex);
		} catch (ClassNotFoundException ex) {
			throw new RunException("Failed to start Minecraft.", ex);
		}
	}
}
