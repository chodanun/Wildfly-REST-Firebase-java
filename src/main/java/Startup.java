import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import com.giinos.ciixfusion.crypto.Crypto;
import com.giinos.ciixfusion.rest.QueueInfo;
import com.giinos.platform.tools.giinos_logger.GiinosLogger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;

@javax.ejb.Startup
@Singleton
public class Startup {

	
	 private static GiinosLogger LOGGER = GiinosLogger.create(Startup.class);
 
	@PostConstruct
	public void init() {
		
		LOGGER.info("-- START --");
//		Crypto.cryptoTest();
		connectToFirebase();

	}
	
	public static void connectToFirebase() {
		FileInputStream serviceAccount;
		try {
			serviceAccount = new FileInputStream("/Users/giinos/qr-auth/src/main/java/serviceAccountKey.json");
			FirebaseOptions options; 
			options = new FirebaseOptions.Builder()
			  .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
			  .setDatabaseUrl("https://ciix-fusion-59c3a.firebaseio.com/")
			  .build(); 
			
			FirebaseApp.initializeApp(options);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("error : connect to firebase : ");
			e.printStackTrace();
		} 
	}

	@PreDestroy
	public void shutdown() {

	}
}
