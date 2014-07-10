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
 * Allows reading and writing of the lastlogin file.
 * <p/>
 * Reading the credentials is easy:
 * 
 * <pre>
 * LastLogin lastLogin = new LastLogin();
 * Credentials credentials = lastLogin.readCredentials(&quot;/path/to/.minecraft/&quot;);
 * System.out.println(credentials.getUsername());
 * System.out.println(credentials.getPassword());
 * </pre>
 * <p/>
 * And writing the credentials is similarly easy:
 * 
 * <pre>
 * Credentials credentials = new Credentials(&quot;username&quot;, &quot;password&quot;);
 * LastLogin lastLogin = new LastLogin();
 * lastLogin.writeCredentials(&quot;/path/to/.minecraft/&quot;, credentials);
 * </pre>
 */
public final class LastLogin {
	
	/**
	 * The default filename of the lastlogin file.
	 */
	public static final String LASTLOGIN_FILENAME = "lastlogin";
	/**
	 * The default password that is used for the cipher.
	 */
	public static final String DEFAULT_CIPHER_PASSWORD = "password";
	/**
	 * The default salt that is used for the cipher.
	 */
	public static final byte[] DEFAULT_CIPHER_SALT = { (byte) 0x0c, (byte) 0x9d, (byte) 0x4a, (byte) 0xe4, (byte) 0x1e, (byte) 0x83, (byte) 0x15, (byte) 0xfc };
	
	private String cipherPassword;
	private byte[] cipherSalt;
	
	/**
	 * Creates a new instance of {@code LastLogin}. The default values for the
	 * password and salt are used.
	 * 
	 * @see {@link LastLogin#DEFAULT_CIPHER_PASSWORD}
	 * @see {@link LastLogin#DEFAULT_CIPHER_SALT}
	 */
	public LastLogin() {
	}
	
	/**
	 * Creates a new instance of {@code LastLogin} with the given
	 * {@code cipherPassword} and {@code cipherSalt}.
	 * 
	 * @param cipherPassword the password used for decrypting the lastlogin
	 *            file. Default is {@link LastLogin#DEFAULT_CIPHER_PASSWORD}.
	 * @param cipherSalt the salt used for decrypting the lastlogin file.
	 *            Default is {@link LastLogin#DEFAULT_CIPHER_SALT}.
	 */
	public LastLogin(String cipherPassword, byte[] cipherSalt) {
		this.cipherPassword = cipherPassword;
		this.cipherSalt = cipherSalt;
	}
	
	/**
	 * Reads the username and password from the given path.
	 * 
	 * @param fileOrPath the path to the lastlogin file or the containing
	 *            directory. If only a directory is specified, the default name
	 *            ({@link LastLogin#LASTLOGIN_FILENAME}) is used.
	 * @return the {@link Credentials} read from the given file.
	 * @throws IOException if reading from the file fails.
	 * @throws LastLoginCipherException if creating the cipher for decrypting
	 *             the file fails.
	 */
	public Credentials readCredentials(String fileOrPath) throws IOException, LastLoginCipherException {
		File file = makeFile(fileOrPath);
		
		Cipher cipher = getCipher(LastLoginCipherMode.DECRYPT, cipherPassword, cipherSalt);
		
		DataInputStream inputStream = null;
		try {
			inputStream = new DataInputStream(new CipherInputStream(new FileInputStream(file), cipher));
			return new Credentials(inputStream.readUTF(), inputStream.readUTF());
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	/**
	 * Writes the current credentials into the given path.
	 * 
	 * @param fileOrPath Either specify a file or a path. A path will be
	 *            extemded with the default filename.
	 * @throws IOException
	 * @throws LastLoginCipherException
	 */
	public void writeCredentials(String fileOrPath, Credentials credentials) throws IOException, LastLoginCipherException {
		File file = makeFile(fileOrPath);
		if (!file.exists()) {
			file.createNewFile();
		}
		
		Cipher cipher = getCipher(LastLoginCipherMode.ENCRYPT, cipherPassword, cipherSalt);
		
		DataOutputStream outputStream = null;
		try {
			outputStream = new DataOutputStream(new CipherOutputStream(new FileOutputStream(file), cipher));
			outputStream.writeUTF(credentials.getUsername());
			outputStream.writeUTF(credentials.getPassword());
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
	
	/**
	 * Creates a {@link File} from the given path. If the given path is a
	 * directory, the default lastlogin filename will be appended.
	 * 
	 * @param fileOrPath
	 * @return
	 */
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
	 * 
	 * @param cipherMode the mode for which to initialize the cipher for.
	 * @return
	 */
	public static Cipher getCipher(LastLoginCipherMode cipherMode) throws LastLoginCipherException {
		return getCipher(cipherMode, DEFAULT_CIPHER_PASSWORD, DEFAULT_CIPHER_SALT);
	}
	
	/**
	 * Initializes a cipher which can be used to decrypt the lastlogin file...or
	 * encrypt, that is.
	 * 
	 * @param cipherMode
	 * @param password the password to use. If {@code null} falls back to the
	 *            default.
	 * @param salt the salt to use. If {@code null} falls back to the default.
	 * @return
	 * @throws LastLoginException
	 * @see {@link LastLogin#DEFAULT_CIPHER_PASSWORD}
	 * @see {@link LastLogin#DEFAULT_CIPHER_SALT}
	 */
	public static Cipher getCipher(LastLoginCipherMode cipherMode, String password, byte[] salt) throws LastLoginCipherException {
		try {
			PBEParameterSpec parameter = new PBEParameterSpec(salt != null ? salt : DEFAULT_CIPHER_SALT, 5);
			PBEKeySpec keySpec = new PBEKeySpec((password != null ? password : DEFAULT_CIPHER_PASSWORD).toCharArray());
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
			Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
			cipher.init(cipherMode.getMode(), key, parameter);
			return cipher;
		} catch (NoSuchAlgorithmException e) {
			throw new LastLoginCipherException("Failed to find Algorithm!", e);
		} catch (InvalidKeySpecException e) {
			throw new LastLoginCipherException("Failed to create cipher!", e);
		} catch (NoSuchPaddingException e) {
			throw new LastLoginCipherException("Failed to create cipher!", e);
		} catch (InvalidKeyException e) {
			throw new LastLoginCipherException("Failed to create cipher!", e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new LastLoginCipherException("Failed to create cipher!", e);
		}
	}
}
