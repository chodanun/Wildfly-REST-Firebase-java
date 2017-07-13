package com.giinos.ciixfusion.model;

public class config {
	// encrypt data
	public static boolean isEncrypt = false;
	public static String END_POINT_R6 = "http://192.168.1.92:8080";
	// data to organize/{uniqueKey} 
	public static String[] dataOut = {"ticket","customerName","mKey","queueName", "type", "queueLogicId","visitId","branchId","branchName"}; // data will be in parameters object
	// data to Register/mKey
	public static String[] dataRegister = {"branchName"}; // {uniqueKey} automatically include in Register/mKey/{uniqueKey}
	
	public static String[] dataDetails = {"ticketId","visitId"};
	
	
	public static void onErrorSaveToFirebaseOrg() {
		//
	}
	
	public static void onErrorSaveToFirebaseRegister() {
		//
	}
}
