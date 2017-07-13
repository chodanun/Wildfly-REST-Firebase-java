package com.giinos.ciixfusion.model;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giinos.ciixfusion.crypto.Crypto;

public class R6Model {
	public static JSONObject json;
	public static final ObjectMapper MAPPER = new ObjectMapper();
	
	public static void setJSONObject(JSONObject jsonInput) {
		R6Model.json = jsonInput;
	}
	
	public static String getVariable(String input) {
		return json.getString(input);
	}
	
	public static String getParametersVariable(String input) {
		String val;
			try {
				val = json.getJSONObject("parameters").getString(input);
			}catch(Exception handling) {
				val = String.valueOf(json.getJSONObject("parameters").getInt(input));
			}
		return val;
	}
	
	public static Map<String, String> objectMappingOrg() {
		String[] data = config.dataOut;
		Map<String, String> obj = new HashMap<String, String>();
		for (int i = 0 ; i < data.length ; i++) {
			try {
				obj.put(data[i],encrypt(getParametersVariable(data[i]),config.isEncrypt));
			}
			catch(Exception handling ) {
				obj.put(data[i],encrypt(getParametersVariable(String.valueOf(data[i])),config.isEncrypt));
			}
			
		}
		return obj;
	}
	
	public static List<WaitingQueue> objectMappingOrgWaitingDetails(String jsonString) {
		List<WaitingQueue> queue = null;
		try {
			queue = MAPPER.readValue(jsonString, new TypeReference<List<WaitingQueue>>() {
			});
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return queue;

	}
	
	public static Map<String, String> objectMappingRegister(String key) {
		String[] data = config.dataRegister;

		Map<String, String> obj = new HashMap<String, String>();
		for (int i = 0 ; i < data.length ; i++) {
			obj.put(data[i],encrypt(getParametersVariable(data[i]),config.isEncrypt));
		}
		obj.put("key",key); // UniqueKey
		return obj;
	}
	
	public static String encrypt(String data,boolean isEncrypt) {
		String ans ;
		if (isEncrypt) {
			KeyPair pair = Crypto.genKeyPair("RSA");
//			PrivateKey priv = pair.getPrivate();
		    PublicKey pub = pair.getPublic();
			ans = Base64.getEncoder().encodeToString(Crypto.encrypt(data,pub));
		}else {
			ans = data;
		}
		return ans;
	}
}
