package com.voxloud.provisioning.service;

import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.exception.DeviceNotFoundException;
import com.voxloud.provisioning.repository.DeviceRepository;
import com.voxloud.provisioning.strategy.ProvisioningStrategy;
import com.voxloud.provisioning.strategy.ProvisioningStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProvisioningServiceImpl implements ProvisioningService {
    private final DeviceRepository deviceRepository;
    private final ProvisioningStrategyFactory provisioningStrategyFactory;

    @Value("${provisioning.domain}")
    private String domain;
    @Value("${provisioning.port}")
    private String port;
    @Value("${provisioning.codecs}")
    private String codecs;

    @Override
    public String getProvisioningFile(String macAddress) {
        Device device = deviceRepository.findById(macAddress)
                .orElseThrow(() ->
                        new DeviceNotFoundException("Device not found for MAC address: " + macAddress));

        ProvisioningStrategy strategy = provisioningStrategyFactory.getStrategy(device.getModel());
        return strategy.generateConfig(device, domain, port, codecs);
    }
}
