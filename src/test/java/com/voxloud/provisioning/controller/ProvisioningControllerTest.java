package com.voxloud.provisioning.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.when;

import com.voxloud.provisioning.exception.DeviceNotFoundException;
import com.voxloud.provisioning.service.ProvisioningService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProvisioningController.class)
class ProvisioningControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProvisioningService provisioningService;

    @DisplayName("Test provisioning device - device not found")
    @Test
    public void testProvisionDevice_DeviceNotFound() throws Exception {
        String macAddress = "aa-bb-cc-11-22-33";

        when(provisioningService.getProvisioningFile(macAddress))
                .thenThrow(new DeviceNotFoundException("Device not found!"));

        mockMvc.perform(get("/api/v1/provisioning/{macAddress}", macAddress))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Device not found!"));
    }

    @DisplayName("Test provisioning device - invalid MAC address")
    @Test
    public void testProvisionDevice_InvalidMacAddress() throws Exception {
        String invalidMacAddress = "invalid-mac-address";

        mockMvc.perform(get("/api/v1/provisioning/{macAddress}", invalidMacAddress))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Device not found!"));
    }

    @DisplayName("Test provisioning device - empty MAC address")
    @Test
    public void testProvisionDevice_EmptyMacAddress() throws Exception {
        String emptyMacAddress = "";

        mockMvc.perform(get("/api/v1/provisioning/{macAddress}", emptyMacAddress))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred: "
                        + "No static resource api/v1/provisioning."));
    }

    @Test
    @DisplayName("Test provisioning device - successful request")
    public void testProvisionDevice_Success() throws Exception {
        String macAddress = "aa-bb-cc-11-22-33";
        String expectedResponse = "Provisioning file for device";

        when(provisioningService.getProvisioningFile(macAddress)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/provisioning/{macAddress}", macAddress))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @DisplayName("Test provisioning device - malformed MAC address")
    public void testProvisionDevice_MalformedMacAddress() throws Exception {
        String malformedMacAddress = "aa:bb:cc:dd:ee:ff:gg";

        mockMvc.perform(get("/api/v1/provisioning/{macAddress}", malformedMacAddress))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Device not found!"));
    }

    @Test
    @DisplayName("Test provisioning device - null MAC address")
    public void testProvisionDevice_NullMacAddress() throws Exception {
        mockMvc.perform(get("/api/v1/provisioning/{macAddress}", (String) null))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred: "
                        + "No static resource api/v1/provisioning."));
    }
}
