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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads, parses, manipulates and saves the .minecraft/options.txt-file.
 */
public class OptionsFile {

	private static final String SEPARATOR = ":";
	private File file;
	List<String> keys = new ArrayList<String>();
	List<String> values = new ArrayList<String>();

	/**
	 *
	 * @param from Either the file or the containing directory.
	 */
	public OptionsFile(String from) {
		setPath(from);
	}

	/**
	 * Check if the file exists.
	 * @return
	 */
	public boolean exists() {
		return file.exists();
	}

	/**
	 * Check if the options.txt was (successfully) read.
	 * @return
	 */
	public boolean isRead() {
		return !keys.isEmpty() && !values.isEmpty();
	}

	/**
	 * Get the path to the options.txt file.
	 * @return
	 */
	public String getPath() {
		return file.getAbsolutePath();
	}

	/**
	 * Read the file.
	 */
	public void read() throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));

			String line;
			while ((line = reader.readLine()) != null) {
				String[] keyValue = line.split(SEPARATOR);
				keys.add(keyValue[0]);
				if (keyValue.length > 1) {
					values.add(keyValue[1]);
				} else {
					values.add("");
				}
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
				throw ex;
			}
		}
	}

	/**
	 * Set the path to the options.txt file.
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setOption(String key, String value) {
		if (keys.indexOf(key) >= 0) {
			values.set(keys.indexOf(key), value);
			return true;
		}

		return false;
	}

	private void setPath(String pathOrFile) {
		this.file = new File(pathOrFile);
		if (!this.file.isAbsolute()) {
			this.file = this.file.getAbsoluteFile();
		}
		if (this.file.isDirectory()) {
			this.file = new File(pathOrFile, "options.txt");
		}
	}

	/**
	 * Write the file.
	 */
	public void write() throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));

			for (int idx = 0; idx < keys.size(); idx++) {
				writer.write(keys.get(idx) + SEPARATOR + values.get(idx));
				writer.newLine();
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			try {
				writer.close();
			} catch (IOException ex) {
				throw ex;
			}
		}
	}
}
