package com.giinos.ciixfusion.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;

public class Crypto {
	public static void cryptoTest() {
       
		String data = "TEST";
		
        KeyPair pair = genKeyPair("RSA");
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();

    		byte[] data_en = encrypt(data,pub);
    		String data_de = decrypt(data_en,priv);
    		
    		System.out.println("private key : "+priv);
    		System.out.println("public key : "+pub);
    		
    		System.out.println("data encryption : "+data_en);
    		System.out.println("data decryption : "+data_de);
    	
    
    	}
	public static KeyPair genKeyPair(String algo) {
		KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance(algo);
	        keyGen.initialize(1024);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        KeyPair pair = keyGen.generateKeyPair();
		return pair;
		
	}
	public static byte[] encrypt(String text, PublicKey key) {
	    byte[] cipherText = null;
	    try {
	      Cipher cipher = Cipher.getInstance("RSA");
	      cipher.init(Cipher.ENCRYPT_MODE,key);
	      cipherText = cipher.doFinal(text.getBytes("ISO-8859-1"));
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return cipherText;
	  }
	
	public static String decrypt(byte[] text, PrivateKey key) {
	    byte[] dectyptedText = null;
	    try {
	      Cipher cipher = Cipher.getInstance("RSA");
	      cipher.init(Cipher.DECRYPT_MODE, key);
	      dectyptedText = cipher.doFinal(text);

	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }

	    return new String(dectyptedText);
	  }
}
