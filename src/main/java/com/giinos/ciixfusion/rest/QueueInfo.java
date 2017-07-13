package com.giinos.ciixfusion.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.security.Base64Encoder;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giinos.ciixfusion.model.QueueModel;
import com.giinos.ciixfusion.model.R6Model;
import com.giinos.ciixfusion.model.config;
import com.giinos.platform.tools.giinos_logger.GiinosLogger;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/proxy")

public class QueueInfo {

	public static String ADD_EVENT = "PUBLIC.VISIT_CREATE";
	public static String CALL_EVENT = "PUBLIC.VISIT_CALL";
	public static final ObjectMapper MAPPER = new ObjectMapper();
    private static GiinosLogger LOGGER = GiinosLogger.create(QueueInfo.class);
    
	@POST
	@Path("/queueInfo")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public Response getInfo(QueueModel queueModel) {
		LOGGER.info("REST : /queueInfo");
		return Response.status(Status.OK).entity(queueModel).build();
	}
	
	@POST
	@Path("/events")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public Response getInfo(String inputObj) {
		LOGGER.info("REST : /events object : {} ",inputObj);
		JSONObject jsonInput = new JSONObject(inputObj);
		LOGGER.info("jsonInput Object : {} ",jsonInput);
		R6Model.setJSONObject(jsonInput);
		String eventName = R6Model.getVariable("eventName");
		String isMobile = checkIsMobile();
	
		if (eventName.equals(ADD_EVENT)) {
			if (isMobile(isMobile)) {
				addToFirebaseOrg();
			}
		}
		else if (eventName.equals(CALL_EVENT)) {
			UpdateWaitingDetailToFirebase();
		}
		return Response.status(Status.OK).entity(inputObj).build();
	}
	
	private static void UpdateWaitingDetailToFirebase() {
		String branchName = R6Model.getParametersVariable("branchName");
		String queueLogicId = R6Model.getParametersVariable("queueLogicId");
		String branchId = R6Model.getParametersVariable("branchId");
		
		String waitingList = null;

		try {
			String url = String.format("%s/rest/entrypoint/branches/%s/queues/%s/visits/",config.END_POINT_R6,branchId,queueLogicId);
			LOGGER.info(url);
			waitingList = getHTML(url);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	
		DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(branchName).child("WaitingDetails").child(queueLogicId);
		mRef.setValue(R6Model.objectMappingOrgWaitingDetails(waitingList), new DatabaseReference.CompletionListener() {
		    @Override
		    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
		        if (databaseError != null) {
		            System.out.println("Data could not be saved on waitingQueueDetails " + databaseError.getMessage());
		            config.onErrorSaveToFirebaseOrg();
		        } else {
		            System.out.println("Data saved successfully on waitingQueueDetails");
		        }
		    }});
	}
	
	public static String getHTML(String urlToRead) throws Exception {
		String waitingList = "" ;
		try {
            URL url = new URL (urlToRead);
            String encoding = Base64Encoder.encode ("superadmin:ulan");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Basic " + encoding);
            InputStream content = (InputStream)connection.getInputStream();
            BufferedReader in   = new BufferedReader (new InputStreamReader (content));
            String line;
            while ((line = in.readLine()) != null) {
            		waitingList += line;
            }
  
        } catch(Exception e) {
            e.printStackTrace();
        }
		return waitingList;
	   }
	
	private String checkIsMobile() {
		String isMobile;
		try{
			isMobile = R6Model.getParametersVariable("isMobile");
		} catch(Exception handling ) {
			isMobile = "false";
		}
		return isMobile;
	}
	
	private boolean isMobile(String isMobile) {
		return Boolean.valueOf(isMobile);
	}
	
	 private static void addToFirebaseOrg() {
		String branchName = R6Model.getParametersVariable("branchName");
		DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(branchName).push();
		String key = mRef.getKey();
		mRef.setValue(R6Model.objectMappingOrg(), new DatabaseReference.CompletionListener() {
		    @Override
		    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
		        if (databaseError != null) {
		            System.out.println("Data could not be saved on org " + databaseError.getMessage());
		            config.onErrorSaveToFirebaseOrg();
		        } else {
		            System.out.println("Data saved successfully on org.");
		            addToFirebaseRegister(key);
		            UpdateWaitingDetailToFirebase();
		        }
		    }});

	}
	 
	 private static void addToFirebaseRegister(String key) {
		 	String mKey = R6Model.getParametersVariable("mKey");
			DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
			mRootRef.child("Register").child(mKey).setValue(R6Model.objectMappingRegister(key), new DatabaseReference.CompletionListener() {
			    @Override
			    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
			       if (databaseError != null) {
			           System.out.println("Data could not be saved on registers" + databaseError.getMessage());
			           config.onErrorSaveToFirebaseRegister();
			       } else {
			           System.out.println("Data saved successfully on registers.");
			       }
			}});

		}


}


