package com.spring.cloudvendor.controller;

import com.spring.cloudvendor.model.CloudVendor;
import com.spring.cloudvendor.response.ResponseHandler;
import com.spring.cloudvendor.service.CloudVendorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cloudvendor")
public class CloudVendorController {
    CloudVendorService cloudVendorService;

    public CloudVendorController(CloudVendorService cloudVendorService) {
        this.cloudVendorService = cloudVendorService;
    }

    @GetMapping("{vendorId}")
    public ResponseEntity<Object> getCloudVendorDetails(@PathVariable("vendorId") String vendorId) {
        return ResponseHandler.responseBuilder("Requested Vendor Details are Given here",
                HttpStatus.OK, cloudVendorService.getCloudVendor(vendorId));
    }

    @GetMapping()
    public List<CloudVendor> getAllCloudVendorDetails() {
        return cloudVendorService.getAllCloudVendor();
    }

    @PostMapping  
    public String createCloudVendorDetails(@RequestBody CloudVendor cloudVendor) {
        cloudVendorService.createCloudVendor(cloudVendor);
        return "Cloud Vendor Created successfully!";
    }

    @PutMapping
    public String updateCloudVendorDetails(@RequestBody CloudVendor cloudVendor) {
        cloudVendorService.updateCloudVendor(cloudVendor);
        return "Cloud Vendor Updated successfully!";
    }

    @DeleteMapping("{vendorId}")  
    public String deleteCloudVendorDetails(@PathVariable("vendorId") String vendorId) {
        cloudVendorService.deleteCloudVendor(vendorId);
        return "Cloud Vendor Deleted successfully!";
    }
}
