package com.voxloud.provisioning.strategy;

import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.util.OverrideFragmentParser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProvisioningStrategyFactory {
    private final OverrideFragmentParser overrideFragmentParser;

    public ProvisioningStrategy getStrategy(Device.DeviceModel model) {
        switch (model) {
            case DESK:
                return new DeskProvisioningStrategy(overrideFragmentParser);
            case CONFERENCE:
                return new ConferenceProvisioningStrategy(overrideFragmentParser);
            default:
                throw new IllegalArgumentException("Unsupported device model: " + model);
        }
    }
}
