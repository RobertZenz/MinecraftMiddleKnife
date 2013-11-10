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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
 * Allows reading, writing of the LastLogin-File.
 */
public class LastLogin extends Credentials {

	public static final String LASTLOGIN_FILENAME = "lastlogin";
	public static final String DEFAULT_CIPHER_PASSWORD = "password";
	public static final byte[] DEFAULT_CIPHER_SALT = {
		(byte) 0x0c, (byte) 0x9d, (byte) 0x4a, (byte) 0xe4,
		(byte) 0x1e, (byte) 0x83, (byte) 0x15, (byte) 0xfc
	};
	private String cipherPassword;
	private byte[] cipherSalt;

	public LastLogin() {
		super();
	}

	public LastLogin(Credentials credentials) {
		super(credentials.getUsername(), credentials.getPassword());
	}

	public LastLogin(String username, String password) {
		super(username, password);
	}

	public LastLogin(String cipherPassword, byte[] cipherSalt) {
		this.cipherPassword = cipherPassword;
		this.cipherSalt = cipherSalt;
	}

	public LastLogin(String cipherPassword, byte[] cipherSalt, String username, String password) {
		this(username, password);
		this.cipherPassword = cipherPassword;
		this.cipherSalt = cipherSalt;
	}

	public String getCipherPassword() {
		return cipherPassword;
	}

	public byte[] getCipherSalt() {
		return cipherSalt;
	}

	public void readFrom(String fileOrPath) throws IOException, LastLoginCipherException {
		File file = makeFile(fileOrPath);

		DataInputStream stream = new DataInputStream(new CipherInputStream(new FileInputStream(file), getCipher(LastLoginCipherMode.DECRYPT)));
		setUsername(stream.readUTF());
		setPassword(stream.readUTF());
		stream.close();
	}

	public void setCipherPassword(String cipherPassword) {
		this.cipherPassword = cipherPassword;
	}

	public void setCipherSalt(byte[] cipherSalt) {
		this.cipherSalt = cipherSalt;
	}

	public void writeTo(String fileOrPath) throws IOException, LastLoginCipherException {
		File file = makeFile(fileOrPath);
		if (!file.exists()) {
			file.createNewFile();
		}

		DataOutputStream stream = new DataOutputStream(new CipherOutputStream(new FileOutputStream(file), getCipher(LastLoginCipherMode.ENCRYPT)));
		stream.writeUTF(getUsername());
		stream.writeUTF(getPassword());
		stream.close();
	}

	private File makeFile(String fileOrPath) {
		File file = new File(fileOrPath);
		if (file.isDirectory()) {
			file = new File(file.getAbsolutePath(), LASTLOGIN_FILENAME);
		}
		file = file.getAbsoluteFile();
		return file;
	}

	/**
	 * Initializes a cipher with the default values which can be used to decrypt
	 * the lastlogin file...or encrypt, that is.
	 * @param cipherMode
	 * @return
	 */
	public static Cipher getCipher(LastLoginCipherMode cipherMode) throws LastLoginCipherException {
		return getCipher(cipherMode, DEFAULT_CIPHER_PASSWORD, DEFAULT_CIPHER_SALT);
	}

	/**
	 * Initializes a cipher which can be used to decrypt the lastlogin file...or encrypt, that is.
	 * @param cipherMode
	 * @return
	 * @throws LastLoginException
	 */
	public static Cipher getCipher(LastLoginCipherMode cipherMode, String password, byte[] salt) throws LastLoginCipherException {
		if (password == null) {
			password = DEFAULT_CIPHER_PASSWORD;
		}
		if (salt == null) {
			salt = DEFAULT_CIPHER_SALT;
		}

		try {
			PBEParameterSpec parameter = new PBEParameterSpec(salt, 5);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
			Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
			cipher.init(cipherMode.getMode(), key, parameter);
			return cipher;
		} catch (NoSuchAlgorithmException ex) {
			throw new LastLoginCipherException("Failed to find Algorithm!", ex);
		} catch (InvalidKeySpecException ex) {
			throw new LastLoginCipherException("Failed to create cipher!", ex);
		} catch (NoSuchPaddingException ex) {
			throw new LastLoginCipherException("Failed to create cipher!", ex);
		} catch (InvalidKeyException ex) {
			throw new LastLoginCipherException("Failed to create cipher!", ex);
		} catch (InvalidAlgorithmParameterException ex) {
			throw new LastLoginCipherException("Failed to create cipher!", ex);
		}
	}
}
