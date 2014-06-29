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

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the main container for the MinecraftApplet. It's usage is simple,
 * create it, set the username, load the natives, load the jars, init, start.
 */
public class ContainerApplet extends Applet implements AppletStub {
	
	public static final String PARAMETER_DEMO = "demo";
	public static final String PARAMETER_FULLSCREEN = "fullscreen";
	public static final String PARAMETER_LOADMAP_ID = "loadmap_id";
	public static final String PARAMETER_LOADMAP_USER = "loadmap_user";
	public static final String PARAMETER_MPPASS = "mppass";
	public static final String PARAMETER_PORT = "port";
	public static final String PARAMETER_SERVER = "server";
	public static final String PARAMETER_SESSION_ID = "sessionid";
	public static final String PARAMETER_STAND_ALONE = "stand-alone";
	public static final String PARAMETER_USERNAME = "username";
	private String appletToLoad;
	private Applet minecraftApplet;
	private Map<String, String> parameters = new HashMap<String, String>();
	private List<String> requestedParameters = new ArrayList<String>();
	
	/**
	 * Create an instance.
	 * 
	 * @throws HeadlessException
	 */
	public ContainerApplet(String appletToLoad) throws HeadlessException {
		super();
		
		this.appletToLoad = appletToLoad;
		
		setLayout(new BorderLayout());
		
		parameters.put(PARAMETER_DEMO, "false");
		parameters.put(PARAMETER_FULLSCREEN, "false");
		parameters.put(PARAMETER_LOADMAP_ID, "0");
		parameters.put(PARAMETER_LOADMAP_USER, "Username");
		parameters.put(PARAMETER_MPPASS, "");
		parameters.put(PARAMETER_PORT, null);
		parameters.put(PARAMETER_SERVER, null);
		parameters.put(PARAMETER_USERNAME, "Username");
		parameters.put(PARAMETER_SESSION_ID, "0");
		parameters.put(PARAMETER_STAND_ALONE, "true");
	}
	
	@Override
	public void appletResize(int width, int height) {
		// And yet nobody cares...
	}
	
	/**
	 * Destroy the applet and the contained MinecraftApplet (if any).
	 */
	@Override
	public void destroy() {
		destroyMinecraftApplet();
		super.destroy();
	}
	
	/**
	 * Stops and destroys the MinecraftApplet.
	 */
	public void destroyMinecraftApplet() {
		if (minecraftApplet != null) {
			remove(minecraftApplet);
			minecraftApplet.stop();
			minecraftApplet.destroy();
			minecraftApplet = null;
		}
	}
	
	/**
	 * Returns a stub-URL which points to localhost.
	 * 
	 * @return
	 */
	@Override
	public URL getDocumentBase() {
		try {
			return new URL("http://localhost:0/");
		} catch (MalformedURLException ex) {
			// If this fails, count me out!
		}
		
		return null;
	}
	
	/**
	 * Returns parameters requested by the MinecraftApplet.
	 * 
	 * @param name
	 * @return
	 */
	@Override
	public String getParameter(String name) {
		if (!requestedParameters.contains(name)) {
			requestedParameters.add(name);
		}
		
		// Check if we now about the parameters.
		// If we don't, you most likely try to launch an update
		// which is now requesting further parameters as I knew about.
		if (parameters.containsKey(name)) {
			return parameters.get(name);
		} else {
			return "";
		}
	}
	
	/**
	 * Returns a list of all parameters that have been requested from this
	 * Applet. This is quite useful if you'd like to know what parameters this
	 * version of Minecraft actually needs and reads.
	 * 
	 * @return
	 */
	public List<String> getRequestedParameters() {
		return requestedParameters;
	}
	
	/**
	 * This returns always true. The MinecraftApplet will check this state and
	 * exit if it does not return true.
	 * 
	 * @return Always true.
	 */
	@Override
	public boolean isActive() {
		// I'm not sure what this is, but it makes it work.
		return true;
	}
	
	/**
	 * Init the MinecraftApplet.
	 */
	@Override
	public void init() {
		minecraftApplet.init();
	}
	
	/**
	 * Load the 4 jars and create an instance of the MinecraftApplet. Better
	 * call loadNatives(String) first.
	 * 
	 * @param minecraftJar The directory of minecraft.jar, or the jar directly.
	 * @param lwjglDir The directory of the lwjgl-jars.
	 * @return
	 */
	public void loadJarsAndApplet(String minecraftJar, String lwjglDir) throws AppletLoadException {
		if (new File(minecraftJar).isDirectory()) {
			minecraftJar = new File(minecraftJar, "minecraft.jar").getAbsolutePath();
		}
		
		try {
			// Our 4 jars which we need.
			URL[] urls = new URL[] { new File(minecraftJar).toURI().toURL(), new File(lwjglDir, "lwjgl.jar").toURI().toURL(),
					new File(lwjglDir, "lwjgl_util.jar").toURI().toURL(), new File(lwjglDir, "jinput.jar").toURI().toURL() };
			
			// Load the jars.
			URLClassLoader loader = new URLClassLoader(urls);
			
			// Create the MinecraftApplet
			setMinecraftApplet((Applet) loader.loadClass(appletToLoad).newInstance());
		} catch (ClassNotFoundException ex) {
			throw new AppletLoadException("Failed to load applet, sorry.", ex);
		} catch (InstantiationException ex) {
			throw new AppletLoadException("Failed to load applet, sorry.", ex);
		} catch (IllegalAccessException ex) {
			throw new AppletLoadException("Failed to load applet, sorry.", ex);
		} catch (MalformedURLException ex) {
			throw new AppletLoadException("Failed to load applet, sorry.", ex);
		}
	}
	
	/**
	 * Load the native libraries.
	 * 
	 * @param nativeDir The directory which contains the native LWJGL libraries.
	 */
	public void loadNatives(String nativeDir) {
		// This fixes issues on a certain OS...
		nativeDir = new File(nativeDir).getAbsolutePath();
		
		System.setProperty("org.lwjgl.librarypath", nativeDir);
		System.setProperty("net.java.games.input.librarypath", nativeDir);
	}
	
	/**
	 * Replace the current MinecraftApplet with the given applet. This will also
	 * call Applet.init().
	 * 
	 * @param applet
	 */
	public void replace(Applet applet) {
		setMinecraftApplet(applet);
		
		// Init the applet we just got.
		minecraftApplet.init();
	}
	
	/**
	 * Sets the given parameter.
	 * 
	 * @param name The name of the parameter.
	 * @param value The value of the paramter.
	 */
	public void setParameter(String name, String value) {
		parameters.put(name, value);
	}
	
	/**
	 * Start the MinecraftApplet.
	 */
	@Override
	public void start() {
		minecraftApplet.start();
	}
	
	/**
	 * Stop the Applet and the contained MinecraftApplet (if any).
	 */
	@Override
	public void stop() {
		if (minecraftApplet != null) {
			minecraftApplet.stop();
		}
		
		super.stop();
	}
	
	/**
	 * Replace the current MinecraftApplet with the given applet.
	 * 
	 * @param applet
	 */
	private void setMinecraftApplet(Applet applet) {
		// Let's make sure that we do not collide with something.
		destroyMinecraftApplet();
		
		minecraftApplet = applet;
		
		// Set the size, otherwise LWJGL will fail to initialize the Display.
		minecraftApplet.setSize(getWidth(), getHeight());
		
		// We're it's...stub...
		minecraftApplet.setStub(this);
		
		// Add it...what else?
		add(minecraftApplet, "Center");
	}
}
