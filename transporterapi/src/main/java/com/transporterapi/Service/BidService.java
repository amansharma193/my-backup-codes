package com.transporterapi.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.transporterapi.bean.Bid;
import com.transporterapi.bean.BidWithLead;
import com.transporterapi.bean.Leads;
import com.transporterapi.exception.ResourceNotFoundException;
@Service
public class BidService {
	
	public Bid createBid(Bid bid) throws ResourceNotFoundException, InterruptedException, ExecutionException {
		Firestore fireStore=FirestoreClient.getFirestore();
	    String bidId = fireStore.collection("Bid").document().getId().toString();	
	    bid.setBidId(bidId);
	    fireStore.collection("Bid").document(bidId).set(bid);
	    LeadsService leadsService=new LeadsService();
	    Leads leads=leadsService.getLeadById(bid.getLeadId());
	    int count=Integer.parseInt(leads.getBidCount());
	    count++;
	    leads.setBidCount(count+"");
	    leadsService.updateLeads(leads);
	    return bid;
		
	}
	public Bid updateBid(Bid bid) {
		Firestore fireStore=FirestoreClient.getFirestore();
		fireStore.collection("Bid").document(bid.getBidId()).set(bid);
		return bid;
	}
	
	public Bid deleteBid(String bidId) throws InterruptedException, ExecutionException, ResourceNotFoundException {
		Firestore fireStore=FirestoreClient.getFirestore();
		  Bid bid = fireStore.collection("Bid").document(bidId).get().get().toObject(Bid.class);
	      if(bid!=null) {
		    fireStore.collection("Bid").document(bidId).delete();
		    LeadsService leadsService=new LeadsService();
		    Leads leads=leadsService.getLeadById(bid.getLeadId());
		    int count=Integer.parseInt(leads.getBidCount());
		    count--;
		    leads.setBidCount(count+"");
		    leadsService.updateLeads(leads);
	      }
	      return bid;
	 }
  
	public ArrayList<Bid>getAllBidsByLeadId(String id) throws InterruptedException, ExecutionException{
		Firestore fireStore=FirestoreClient.getFirestore();
		 ArrayList<Bid>al=new ArrayList<Bid>();
		 ApiFuture<QuerySnapshot> future=fireStore.collection("Bid").whereEqualTo("leadId",id).get();
		 List<QueryDocumentSnapshot>documents;
		 documents = future.get().getDocuments();
		   for (QueryDocumentSnapshot document : documents) {
			   al.add(document.toObject(Bid.class));
			}
			
	  return al;
   }
  
	public ArrayList<Bid>getAllBidsByTransporterId(String id) throws InterruptedException, ExecutionException{
		Firestore fireStore=FirestoreClient.getFirestore();
		  ArrayList<Bid>al=new ArrayList<Bid>();
		  ApiFuture<QuerySnapshot> future=fireStore.collection("Bid").whereEqualTo("transporterId",id).get();
		  List<QueryDocumentSnapshot>documents;
		  documents = future.get().getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				   al.add(document.toObject(Bid.class));
				}
		 return al;
	  }
	public ArrayList<BidWithLead> getbidWithLead(String id) throws InterruptedException, ExecutionException {
		Firestore fireStore=FirestoreClient.getFirestore();
		ArrayList<BidWithLead>al =new ArrayList<BidWithLead>();
		ApiFuture<QuerySnapshot> future=fireStore.collection("Bid").whereEqualTo("transporterId",id).get();
		  List<QueryDocumentSnapshot>documents;
		  documents = future.get().getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				   Bid bid=document.toObject(Bid.class);
				   Leads lead=new LeadsService().getLeadById(bid.getLeadId());
				   al.add(new BidWithLead(bid,lead));
				}
		
		return al;
	}
	
    
}