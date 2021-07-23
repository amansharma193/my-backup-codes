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

import com.transporterapi.Service.BidService;
import com.transporterapi.bean.Bid;
import com.transporterapi.bean.BidWithLead;
import com.transporterapi.exception.ResourceNotFoundException;
@RestController
@RequestMapping ("/bid")
public class BidController {
	@Autowired
	BidService bidService;
	@PostMapping("/")
	public ResponseEntity<Bid> createBid(@RequestBody Bid bid) throws ResourceNotFoundException, InterruptedException, ExecutionException{
		if(bid == null) 
			throw new ResourceNotFoundException("Bid Not found");
		return new ResponseEntity<Bid>(bidService.createBid(bid),HttpStatus.OK);}
	
	@PostMapping("/update")
	public ResponseEntity<Bid> updateBid(@RequestBody Bid bid) throws ResourceNotFoundException, InterruptedException, ExecutionException{
	bid=bidService.updateBid(bid);
	return new ResponseEntity<Bid>(bid,HttpStatus.OK);}
		
	@DeleteMapping("/{id}")
	public ResponseEntity<Bid> deleteBid(@PathVariable String id) throws ResourceNotFoundException, InterruptedException, ExecutionException{
		return new ResponseEntity<Bid>(bidService.deleteBid(id),HttpStatus.OK);
	}

	@GetMapping("/{leadId}")
    public ResponseEntity<?> getAllBidsByLeadId(@PathVariable("leadId")String id) throws InterruptedException, ExecutionException{
	ArrayList<Bid>al=bidService.getAllBidsByLeadId(id);
	return new ResponseEntity<ArrayList<Bid>>(al,HttpStatus.OK);
    }
  
	@GetMapping("/transporter/{transporterId}")
    public ResponseEntity<?> getAllBidsByTransporterId(@PathVariable("transporterId")String id) throws InterruptedException, ExecutionException{
	ArrayList<Bid>al=bidService.getAllBidsByTransporterId(id);
	return new ResponseEntity<ArrayList<Bid>>(al,HttpStatus.OK);
   }
	@GetMapping("/bid-lead/{id}")
	public ResponseEntity<ArrayList<BidWithLead>> getBidwithLead(@PathVariable("id")String id) throws InterruptedException, ExecutionException, ResourceNotFoundException{
		ArrayList<BidWithLead> al=bidService.getbidWithLead(id);
		if(al!=null)
			return new ResponseEntity<ArrayList<BidWithLead>>(al,HttpStatus.OK);
		else
			throw new ResourceNotFoundException("Bid not found with id "+id);
	}

}