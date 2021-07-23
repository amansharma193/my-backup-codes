package com.transporterapi.controller;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.transporterapi.Service.LeadsService;
import com.transporterapi.bean.BidWithLead;
import com.transporterapi.bean.Leads;
import com.transporterapi.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/lead")
public class LeadsController {
	
	@Autowired
	LeadsService leadsService;
	
	@PostMapping("/create")
	public ResponseEntity<Leads> createLeads(@RequestBody Leads leads) {
		leads=leadsService.createLeads(leads);
		return new ResponseEntity<Leads>(leads,HttpStatus.OK);
	}
	
	@PostMapping("/update")
	public ResponseEntity<Leads> updateLeads(@RequestBody Leads leads) {
		leads=leadsService.updateLeads(leads);
		return new ResponseEntity<Leads>(leads,HttpStatus.OK);
	}
	
	@GetMapping("/all-lead/{userId}")
	public ResponseEntity<ArrayList<Leads>> getAllLeads(@PathVariable("userId") String id) throws InterruptedException, ExecutionException{		
		ArrayList<Leads> al=leadsService.getAllLeads(id);
		return new ResponseEntity<ArrayList<Leads>>(al,HttpStatus.OK) ;
	}
	
	@GetMapping("/{leadId}")
	public ResponseEntity<Leads> getLeadById(@PathVariable("leadId") String id)throws ResourceNotFoundException, InterruptedException, ExecutionException {
		Leads lead=leadsService.getLeadById(id);
		if(lead!=null)
			return new ResponseEntity<Leads>(lead,HttpStatus.OK);
		else
			throw new ResourceNotFoundException("lead not found with id "+id);
	}
	
	@GetMapping("/confirm-lead/{userId}")
	public ResponseEntity<ArrayList<Leads>> getConfirmLeads(@PathVariable("userId") String id) throws InterruptedException, ExecutionException{
		ArrayList<Leads>al=leadsService.getConfirmLeads(id);
		return new ResponseEntity<ArrayList<Leads>>(al,HttpStatus.OK);
	}
	
	@GetMapping("/completed-lead/{userId}")
	public ResponseEntity<ArrayList<Leads>> getCompletedLeads(@PathVariable("userId") String id) throws InterruptedException, ExecutionException{
		ArrayList<Leads>al=leadsService.getCompletedLeads(id);
		return new ResponseEntity<ArrayList<Leads>>(al,HttpStatus.OK);
	}
	
	//create leads
	@GetMapping("/created-lead/{userId}")
	public ResponseEntity<ArrayList<Leads>> getCreatedLeads(@PathVariable("userId") String id) throws InterruptedException, ExecutionException{
		ArrayList<Leads>al=leadsService.getCreatedLeads(id);
		return new ResponseEntity<ArrayList<Leads>>(al,HttpStatus.OK);
	}
	
	@DeleteMapping("/{leadId}")
	public ResponseEntity<Leads> deleteLeadById(@PathVariable("leadId") String id)throws ResourceNotFoundException, InterruptedException, ExecutionException {
		Leads lead= leadsService.deleteLeadById(id);
		if(lead!=null)
			return new ResponseEntity<Leads>(lead,HttpStatus.OK);
		else
			throw new ResourceNotFoundException("lead not found with id "+id);
	}
	@GetMapping("/transporter/current-lead/{id}")
	public ResponseEntity<ArrayList<BidWithLead>> getCurrentLeadsbyTransporterId(@PathVariable("id") String id) throws InterruptedException, ExecutionException {
		ArrayList<BidWithLead>al=leadsService.getCurrentLeadsbyTransporterId(id);
		return new ResponseEntity<ArrayList<BidWithLead>>(al,HttpStatus.OK);
	}
	@GetMapping("/transporter/completed-lead/{id}")
	public ResponseEntity<ArrayList<BidWithLead>> getCompletedLeadsbyTransporterId(@PathVariable("id") String id) throws InterruptedException, ExecutionException {
		ArrayList<BidWithLead>al=leadsService.getCompletedLeadsbyTransporterId(id);
		return new ResponseEntity<ArrayList<BidWithLead>>(al,HttpStatus.OK);
	}
	
	
	@GetMapping("/all-lead")
	public ResponseEntity<ArrayList<Leads>> getAllLeads() throws InterruptedException, ExecutionException{
		ArrayList<Leads>al=leadsService.getAllLeads();
		return new ResponseEntity<ArrayList<Leads>>(al,HttpStatus.OK);
	}
	@GetMapping("/transporter/lead-id/{id}")
	public ResponseEntity<ArrayList<String>> getCurrentLeadsIdByTransporterId(@PathVariable("id") String id) throws InterruptedException, ExecutionException {
		ArrayList<String>al=leadsService.getCurrentLeadsIdByTransporterId(id);
		return new ResponseEntity<ArrayList<String>>(al,HttpStatus.OK);
	}
	@PostMapping("/transporter/current-lead/filter/")
	public ResponseEntity<ArrayList<Leads>> getCurrentLeadsbyFilter(@RequestBody ArrayList<String>al1) throws InterruptedException, ExecutionException {
		ArrayList<Leads>al=leadsService.getCurrentLeadsbyFilter(al1);
		return new ResponseEntity<ArrayList<Leads>>(al,HttpStatus.OK);
	}
	
	//get Create or Confirmed Load Only
	@GetMapping("/create/confirmed/{userId}")
	public ResponseEntity<ArrayList<Leads>> getCreateAndConfirmed(@PathVariable("userId")String userId) throws InterruptedException, ExecutionException {
		ArrayList<Leads>al=leadsService.getCreateAndConfirmed(userId);
		return new ResponseEntity<ArrayList<Leads>>(al,HttpStatus.OK);
	}
	
	
	
}
