package com.spring.cloudvendor.service.impl;

import com.spring.cloudvendor.exception.CloudVendorNotFoundException;
import com.spring.cloudvendor.model.CloudVendor;
import com.spring.cloudvendor.repository.CloudVendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudVendorServiceImplTest {

    @Mock
    private CloudVendorRepository cloudVendorRepository;

    @InjectMocks
    private CloudVendorServiceImpl cloudVendorService;

    private CloudVendor cloudVendor;
    private List<CloudVendor> cloudVendorList;

    @BeforeEach
    void setUp() {
        cloudVendor = new CloudVendor("1", "Amazon", "USA", "xxx-xxx-xxxx");
        cloudVendorList = new ArrayList<>();
        cloudVendorList.add(cloudVendor);
    }

    @Test
    void testCreateCloudVendor() {
        when(cloudVendorRepository.save(any(CloudVendor.class))).thenReturn(cloudVendor);

        String result = cloudVendorService.createCloudVendor(cloudVendor);

        assertEquals("Success", result);
        verify(cloudVendorRepository, times(1)).save(cloudVendor);
    }

    @Test
    void testUpdateCloudVendor() {
        when(cloudVendorRepository.save(any(CloudVendor.class))).thenReturn(cloudVendor);

        String result = cloudVendorService.updateCloudVendor(cloudVendor);

        assertEquals("Success", result);
        verify(cloudVendorRepository, times(1)).save(cloudVendor);
    }

    @Test
    void testDeleteCloudVendor() {
        doNothing().when(cloudVendorRepository).deleteById(anyString());

        String result = cloudVendorService.deleteCloudVendor("1");

        assertEquals("Success", result);
        verify(cloudVendorRepository, times(1)).deleteById("1");
    }

    @Test
    void testGetCloudVendor() {
        when(cloudVendorRepository.findById(anyString())).thenReturn(Optional.of(cloudVendor));

        CloudVendor result = cloudVendorService.getCloudVendor("1");

        assertNotNull(result);
        assertEquals(cloudVendor.getVendorName(), result.getVendorName());
        verify(cloudVendorRepository, times(2)).findById("1");
    }

    @Test
    void testGetCloudVendor_NotFound() {
        when(cloudVendorRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(CloudVendorNotFoundException.class,
                () -> cloudVendorService.getCloudVendor("1"));

        verify(cloudVendorRepository, times(1)).findById("1");
    }

    @Test
    void testGetAllCloudVendor() {
        when(cloudVendorRepository.findAll()).thenReturn(cloudVendorList);

        List<CloudVendor> result = cloudVendorService.getAllCloudVendor();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cloudVendor.getVendorName(), result.get(0).getVendorName());
        verify(cloudVendorRepository, times(1)).findAll();
    }

    @Test
    void testGetByVendorName() {
        when(cloudVendorRepository.findByVendorName(anyString())).thenReturn(cloudVendorList);

        List<CloudVendor> result = cloudVendorService.getByVendorName("Amazon");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Amazon", result.get(0).getVendorName());
        verify(cloudVendorRepository, times(1)).findByVendorName("Amazon");
    }

    @Test
    void testGetByVendorName_EmptyResult() {
        when(cloudVendorRepository.findByVendorName(anyString())).thenReturn(new ArrayList<>());

        List<CloudVendor> result = cloudVendorService.getByVendorName("NonExistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cloudVendorRepository, times(1)).findByVendorName("NonExistent");
    }
}