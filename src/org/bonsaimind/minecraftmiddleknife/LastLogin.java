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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Allows reading, writing and manipulation of the LastLogin-File.
 */
public class LastLogin {

	public static final String lastloginFilename = "lastlogin";

	/**
	 * Reads and decrypts the contents of the lastlogin file.
	 * @param from The lastlogin-file or the directory to which to read from.
	 * @return A 2-dimensional String array which consists of username [0] and password [1].
	 * @throws LastLoginException
	 */
	public static Credentials getLastLogin(File from) throws LastLoginException {
		if (from.isDirectory()) {
			from = new File(from.getAbsolutePath(), lastloginFilename);
		}
		from = from.getAbsoluteFile();

		try {
			DataInputStream stream = new DataInputStream(new CipherInputStream(new FileInputStream(from), getLastLoginCipher(CipherMode.DECRYPT)));
			return new Credentials(stream.readUTF(), stream.readUTF());
		} catch (FileNotFoundException ex) {
			throw new LastLoginException("File not found!", ex);
		} catch (IOException ex) {
			throw new LastLoginException("Failed to read from the file!", ex);
		}
	}

	/**
	 * Initializes a cipher which can be used to decrypt the lastlogin file...or encrypt, that is.
	 * @param cipherMode
	 * @return
	 * @throws LastLoginException
	 */
	public static Cipher getLastLoginCipher(CipherMode cipherMode) throws LastLoginException {
		byte[] salt = {
			(byte) 0x0c, (byte) 0x9d, (byte) 0x4a, (byte) 0xe4,
			(byte) 0x1e, (byte) 0x83, (byte) 0x15, (byte) 0xfc
		};
		String password = "passwordfile";

		try {
			PBEParameterSpec parameter = new PBEParameterSpec(salt, 5);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
			Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
			cipher.init(cipherMode.getMode(), key, parameter);
			return cipher;
		} catch (NoSuchAlgorithmException ex) {
			throw new LastLoginException("Failed to find Algorithm!", ex);
		} catch (InvalidKeySpecException ex) {
			throw new LastLoginException("Failed to create cipher!", ex);
		} catch (NoSuchPaddingException ex) {
			throw new LastLoginException("Failed to create cipher!", ex);
		} catch (InvalidKeyException ex) {
			throw new LastLoginException("Failed to create cipher!", ex);
		} catch (InvalidAlgorithmParameterException ex) {
			throw new LastLoginException("Failed to create cipher!", ex);
		}
	}

	/**
	 * Writes the given credentials to the lastlogin file.
	 * @param to The target file.
	 * @param credentials The credentials.
	 * @throws LastLoginException
	 */
	public static void setLastlogin(File to, Credentials credentials) throws LastLoginException {
		setLastlogin(to, credentials.getUsername(), credentials.getPassword());
	}

	/**
	 * Writes the given username and password to the lastlogin file.
	 * @param to The target file.
	 * @param username The username.
	 * @param password The password.
	 * @throws LastLoginException
	 */
	public static void setLastlogin(File to, String username, String password) throws LastLoginException {
		if (to.isDirectory()) {
			to = new File(to.getAbsolutePath(), lastloginFilename);
		}
		to = to.getAbsoluteFile();

		if (!to.exists()) {
			try {
				to.createNewFile();
			} catch (IOException ex) {
				throw new LastLoginException("File does not exist and I could not create it!", ex);
			}
		}

		try {
			DataOutputStream stream = new DataOutputStream(new CipherOutputStream(new FileOutputStream(to), getLastLoginCipher(CipherMode.ENCRYPT)));
			stream.writeUTF(username);
			stream.writeUTF(password);
			stream.close();
		} catch (FileNotFoundException ex) {
			throw new LastLoginException("File not found!", ex);
		} catch (IOException ex) {
			throw new LastLoginException("Coulnd not writeto the file!", ex);
		}
	}
}
