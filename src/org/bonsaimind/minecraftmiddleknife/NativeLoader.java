/*
 * Copyright 2014 Robert 'Bobby' Zenz. All rights reserved.
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

import java.io.File;

/**
 * A simple static helper that allows you to load the native libraries which are
 * needed for LWJGL and possibly others.
 */
public final class NativeLoader {
	
	/**
	 * Not supposed to be instantiated.
	 */
	private NativeLoader() {
		throw new AssertionError();
	}
	
	/**
	 * Loads the native libraries from the given directory.
	 * <p/>
	 * To be perfectly honest, this does nothing but making the given path
	 * absolute and setting it into the {@code org.lwjgl.librarypath} and
	 * {@code net.java.games.input.librarypath} system properties.
	 * 
	 * @param dir the directory from which to load the native libraries.
	 */
	public static void loadNativeLibraries(String dir) {
		// This fixes issues with Microsoft Windows.
		String absoluteDir = new File(dir).getAbsolutePath();
		
		System.setProperty("org.lwjgl.librarypath", absoluteDir);
		System.setProperty("net.java.games.input.librarypath", absoluteDir);
	}
}
