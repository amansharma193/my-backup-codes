package com.transporterapi.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.transporterapi.FileUtility;
import com.transporterapi.bean.Rating;
import com.transporterapi.bean.Transporter;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class TransporterService {

	private static final String TAG = "Transporter";
	
	//get all transporter
	public ArrayList<Transporter> getTransporter() throws InterruptedException, ExecutionException{
		Firestore fireStore = FirestoreClient.getFirestore();
		ArrayList<Transporter>transporterList = new ArrayList<Transporter>();
		List<QueryDocumentSnapshot> list = fireStore.collection(TAG).get().get().getDocuments();	
		for(QueryDocumentSnapshot queryDocument : list ) {
			transporterList.add(queryDocument.toObject(Transporter.class));
		}
		return transporterList;		
	}
	
	
	//get Single Transporter
	public Transporter getTransporter(String id) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		Transporter t = fireStore.collection(TAG).document(id).get().get().toObject(Transporter.class);
		return t;
	}
	
	// create new transporter
	public Transporter createTransporter(Transporter transporter,MultipartFile file) throws IOException {		
		Firestore fireStore = FirestoreClient.getFirestore();
		String imageUrl = new FileUtility().uploadFile(file);
		transporter.setImageUrl(imageUrl);
		fireStore.collection(TAG).document(transporter.getTransporterId()).set(transporter);
		return transporter;
	}
	
	//delete Transporter by id
	public Transporter deleteTransporter(String id) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		Transporter t = fireStore.collection(TAG).document(id).get().get().toObject(Transporter.class);
		fireStore.collection(TAG).document(id).delete();
			return t;
	}
	
	//update transporter details without image
	public Transporter updateTransporter(Transporter transporter) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		fireStore.collection(TAG).document(transporter.getTransporterId()).set(transporter);
		return transporter;
	}
	
	//update transporter image only
	public Transporter updateTransporter(String transporterId,MultipartFile file) throws IOException, InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		String imageUrl = new FileUtility().uploadFile(file);
		Transporter t = fireStore.collection(TAG).document(transporterId).get().get().toObject(Transporter.class);
		t.setImageUrl(imageUrl);
		fireStore.collection(TAG).document(transporterId).set(t);
		return t;			
	}
	//create transporter rating
	public Rating createRating(String transporterId,String leadId,Rating rating) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		fireStore.collection("Rating").document(transporterId).collection("Rating").document(leadId).set(rating);
		return rating; 
	}
	
	// get single transporterId and rating
	  public ArrayList<Rating> getTranporterRating(String transporterId) throws InterruptedException, ExecutionException{
	   ArrayList<Rating> ratingList = new ArrayList<>();
	   Firestore fireStore = FirestoreClient.getFirestore();
	   List<QueryDocumentSnapshot> document = fireStore.collection("Rating").document(transporterId).collection("Rating").get().get().getDocuments();
		   for(QueryDocumentSnapshot ds : document) {
			    Rating r = ds.toObject(Rating.class);
			    ratingList.add(r);
		   }
		  return ratingList;
	  }
	  
	 // get Number of Rating
	  public ArrayList<Float> getNumberOfRating(String transporterId) throws InterruptedException, ExecutionException{
		  ArrayList<Float> al = new ArrayList<>(); 
		  Firestore fireStore = FirestoreClient.getFirestore();
		  List<QueryDocumentSnapshot> document = fireStore.collection("Rating").document(transporterId).collection("Rating").get().get().getDocuments();
			al.add((float) document.size()); 
			Float total = (float) 0;
			for(QueryDocumentSnapshot ds : document) {
				Rating rating = ds.toObject(Rating.class);
				float r = Float.parseFloat(rating.getRating());
				total += r;
								
			}
		  al.add(total);
		  return al;
		  }	
}