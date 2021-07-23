package com.transporterapi.controller;


import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.transporterapi.bean.Transporter;
import com.transporterapi.bean.Vehicle;
import com.transporterapi.exception.ResourceNotFoundException;
import com.transporterapi.Service.VehicleService;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {
	@Autowired
	VehicleService vehicleService;
	
	@PostMapping("/")
	public ResponseEntity<Transporter> createVehicle(@RequestParam("name")String name,
			@RequestParam("count")String count,
			@RequestParam("transporterId")String transporterId,
			@RequestParam("file")MultipartFile file) throws ResourceNotFoundException, IOException, InterruptedException, ExecutionException{
		if(file.isEmpty())
			throw new ResourceNotFoundException("image not found");
		
		Vehicle vehicle = new  Vehicle();
		vehicle.setName(name);
		vehicle.setCount(count);
		return new ResponseEntity<Transporter>(vehicleService.createVehicle(vehicle, transporterId,file),HttpStatus.OK);
	}
	
	@PostMapping("/update")
	public ResponseEntity<Transporter> updateVehicle(@RequestBody Transporter transporter) throws InterruptedException, ExecutionException{
		transporter=vehicleService.updateVehicle(transporter);
		return new ResponseEntity<Transporter>(transporter,HttpStatus.OK);
	}
	
	@GetMapping("/category")
	public ResponseEntity<ArrayList<Object>> getCategory() throws InterruptedException, ExecutionException, ResourceNotFoundException{
		ArrayList<Object> al=vehicleService.getCategory();
		if(al.size()==0) {
			throw new ResourceNotFoundException("No Category Found");
		}
		return new ResponseEntity<ArrayList<Object>>(al,HttpStatus.OK);
	}
	
	
	@PostMapping("/update/image")
	public ResponseEntity<Transporter> updateImage(@RequestParam("file")MultipartFile file,
			@RequestParam("transporterId")String transporterId,
			@RequestParam("id")String id
			) throws IOException, InterruptedException, ExecutionException, ResourceNotFoundException{
		Transporter transporter=vehicleService.updateImage(file,transporterId,id);
		if(transporter==null) {
			throw new ResourceNotFoundException("Transporter not found with id "+transporterId);
		}
		return new ResponseEntity<Transporter>(transporter,HttpStatus.OK);
	}
	
	
	@DeleteMapping("/{id}/{transporterId}")
	public ResponseEntity<Transporter> deleteVehicle(@PathVariable String id,
			@PathVariable String transporterId) throws InterruptedException, ExecutionException, ResourceNotFoundException {
		Transporter t = vehicleService.deleteVehicle(id, transporterId);
		return new ResponseEntity<Transporter>(t,HttpStatus.OK);
	}
	
}