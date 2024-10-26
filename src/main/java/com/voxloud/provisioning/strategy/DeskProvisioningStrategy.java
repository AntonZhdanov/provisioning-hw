package com.voxloud.provisioning.strategy;

import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.util.OverrideFragmentParser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeskProvisioningStrategy implements ProvisioningStrategy {
    private final OverrideFragmentParser overrideFragmentParser;

    @Override
    public String generateConfig(Device device, String domain, String port, String codecs) {
        StringBuilder config = new StringBuilder();
        config.append("username=")
                .append(device.getUsername())
                .append("\n")
                .append("password=")
                .append(device.getPassword())
                .append("\n")
                .append("domain=")
                .append(overrideFragmentParser.getOverrideValue(device, "domain", domain))
                .append("\n")
                .append("port=")
                .append(overrideFragmentParser.getOverrideValue(device, "port", port))
                .append("\n")
                .append("codecs=")
                .append(codecs)
                .append("\n")
                .append("timeout=")
                .append(overrideFragmentParser.getOverrideValue(device, "timeout", "10"))
                .append("\n");

        return config.toString();
    }
}
