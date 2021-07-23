package com.transporterapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import com.transporterapi.Service.StateService;
import com.transporterapi.bean.State;
import com.transporterapi.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/state")
public class StateController {
	@Autowired
	StateService stateService;
	@GetMapping("/list")
	public ResponseEntity<List<State>> getstateList() throws ExecutionException, InterruptedException {
		ArrayList<State> stateList = stateService.getStateList();

		return new ResponseEntity<List<State>>(stateList, HttpStatus.OK);
	}
	@PostMapping("/")
	public ResponseEntity<?> saveState(@RequestParam ("stateName") String stateName ) throws Exception {
		
		State s=new State();
		s.setStateName(stateName);
	    State state=stateService.saveState(s);
		return new ResponseEntity<>(state,HttpStatus.OK);
	}
	@GetMapping("/{stateId}")
	  public ResponseEntity<?> getStateById(@PathVariable("stateId") String stateId) throws InterruptedException, ExecutionException, ResourceNotFoundException{
	     State s = stateService.getStateById(stateId);	  
	     if(s == null)
	    	 throw new ResourceNotFoundException("State not found");
	     else
	    	 return new ResponseEntity<State>(s,HttpStatus.OK);
	  }
}
