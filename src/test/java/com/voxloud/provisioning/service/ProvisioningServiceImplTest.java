package com.voxloud.provisioning.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.exception.DeviceNotFoundException;
import com.voxloud.provisioning.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProvisioningServiceImplTest {
    @Autowired
    private ProvisioningServiceImpl provisioningService;

    @Autowired
    private DeviceRepository deviceRepository;

    @DisplayName("Test retrieving provisioning file for Conference device with override")
    @Test
    public void testGetProvisioningFile_ConferenceDeviceWithOverride() throws JSONException {
        Device device = new Device();
        device.setMacAddress("1a-2b-3c-4d-5e-6f");
        device.setModel(Device.DeviceModel.CONFERENCE);
        device.setUsername("eric");
        device.setPassword("blue");
        device.setOverrideFragment("{\"domain\":\"sip.anotherdomain.com\",\"port\":\"5161\",\"timeout\":10}");
        deviceRepository.save(device);

        String configFile = provisioningService.getProvisioningFile("1a-2b-3c-4d-5e-6f");

        JSONObject jsonObject = new JSONObject(configFile);

        assertThat(jsonObject.getString("username")).isEqualTo("eric");
        assertThat(jsonObject.getString("password")).isEqualTo("blue");
        assertThat(jsonObject.getString("domain")).isEqualTo("sip.anotherdomain.com");
        assertThat(jsonObject.getString("port")).isEqualTo("5161");

        List<String> codecs = new ArrayList<>();
        for (int i = 0; i < jsonObject.getJSONArray("codecs").length(); i++) {
            codecs.add(jsonObject.getJSONArray("codecs").getString(i));
        }

        assertThat(codecs).containsExactly("G711", "G729", "OPUS");

        assertThat(jsonObject.getInt("timeout")).isEqualTo(10);
    }

    @DisplayName("Test retrieving provisioning file for Desk device without override")
    @Test
    public void testGetProvisioningFile_DeskDeviceWithoutOverride() {
        Device device = new Device();
        device.setMacAddress("aa-bb-cc-11-22-33");
        device.setModel(Device.DeviceModel.DESK);
        device.setUsername("john");
        device.setPassword("doe");
        deviceRepository.save(device);

        String configFile = provisioningService.getProvisioningFile("aa-bb-cc-11-22-33");

        assertThat(configFile).contains("username=john");
        assertThat(configFile).contains("password=doe");
        assertThat(configFile).contains("domain=sip.voxloud.com");
        assertThat(configFile).contains("port=5060");
        assertThat(configFile).contains("codecs=G711,G729,OPUS");
    }

    @DisplayName("Test retrieving provisioning file for Conference device without override")
    @Test
    public void testGetProvisioningFile_ConferenceDeviceWithoutOverride() throws JSONException {
        Device device = new Device();
        device.setMacAddress("11-22-33-aa-bb-cc");
        device.setModel(Device.DeviceModel.CONFERENCE);
        device.setUsername("alice");
        device.setPassword("green");
        deviceRepository.save(device);

        String configFile = provisioningService.getProvisioningFile("11-22-33-aa-bb-cc");

        JSONObject jsonObject = new JSONObject(configFile);

        assertThat(jsonObject.getString("username")).isEqualTo("alice");
        assertThat(jsonObject.getString("password")).isEqualTo("green");
        assertThat(jsonObject.getString("domain")).isEqualTo("sip.voxloud.com");
        assertThat(jsonObject.getString("port")).isEqualTo("5060");
        assertThat(jsonObject.getJSONArray("codecs")
                .toString()).isEqualTo("[\"G711\",\"G729\",\"OPUS\"]");
        assertThat(jsonObject.getInt("timeout")).isEqualTo(10);
    }

    @DisplayName("Test retrieving provisioning file when device is not found")
    @Test
    public void testGetProvisioningFile_DeviceNotFound() {
        assertThatThrownBy(() -> provisioningService.getProvisioningFile("00-11-22-33-44-55"))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessage("Device not found for MAC address: 00-11-22-33-44-55");
    }

    @DisplayName("Test retrieving provisioning file for Desk device with override")
    @Test
    public void testGetProvisioningFile_DeskDeviceWithOverride() {
        Device device = new Device();
        device.setMacAddress("cc-bb-aa-11-22-33");
        device.setModel(Device.DeviceModel.DESK);
        device.setUsername("peter");
        device.setPassword("red");
        device.setOverrideFragment("domain=sip.anotherdomain.com\nport=5161");
        deviceRepository.save(device);

        String configFile = provisioningService.getProvisioningFile("cc-bb-aa-11-22-33");

        assertThat(configFile).contains("username=peter");
        assertThat(configFile).contains("password=red");
        assertThat(configFile).contains("domain=sip.anotherdomain.com");
        assertThat(configFile).contains("port=5161");
        assertThat(configFile).contains("codecs=G711,G729,OPUS");
    }

    @DisplayName("Test valid request for Desk device with overrideFragment")
    @Test
    public void testGetProvisioningFile_ValidDeskDevice_WithOverrideFragment() {
        String macAddress = "aa-bb-cc-11-22-33";
        String overrideFragment = "domain=sip.anotherdomain.com\nport=5161\ntimeout=10";

        Device device = new Device();
        device.setMacAddress(macAddress);
        device.setModel(Device.DeviceModel.DESK);
        device.setUsername("john");
        device.setPassword("doe");
        device.setOverrideFragment(overrideFragment);
        deviceRepository.save(device);

        String result = provisioningService.getProvisioningFile(macAddress);

        assertThat(result).isNotNull();

        assertThat(result).contains("username=john");
        assertThat(result).contains("password=doe");
        assertThat(result).contains("domain=sip.anotherdomain.com");
        assertThat(result).contains("port=5161");
        assertThat(result).contains("codecs=G711,G729,OPUS");
        assertThat(result).contains("timeout=10");
    }

    @DisplayName("Test valid request for Conference device with overrideFragment in key=value format")
    @Test
    public void testGetProvisioningFile_ValidConferenceDevice_WithJsonFormat() {
        String macAddress = "dd-ee-ff-11-22-33";
        String overrideFragment = "domain=sip.anotherdomain.com\nport=5161\ntimeout=10";

        Device device = new Device();
        device.setMacAddress(macAddress);
        device.setModel(Device.DeviceModel.CONFERENCE);
        device.setOverrideFragment(overrideFragment);
        deviceRepository.save(device);

        String result = provisioningService.getProvisioningFile(macAddress);

        assertThat(result).isNotNull();
        assertThat(result).contains("\"domain\": \"sip.anotherdomain.com\"");
        assertThat(result).contains("\"port\": \"5161\"");
        assertThat(result).contains("\"timeout\": 10");
        assertThat(result).contains("\"codecs\": [\"G711\", \"G729\", \"OPUS\"]");
    }
}
