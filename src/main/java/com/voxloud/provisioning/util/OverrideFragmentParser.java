package com.voxloud.provisioning.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.exception.OverrideFragmentException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OverrideFragmentParser {
    private final ObjectMapper objectMapper;

    public String getOverrideValue(Device device, String key, String defaultValue) {
        Map<String, String> overrides = parseOverrideFragment(device);
        return overrides.getOrDefault(key, defaultValue);
    }

    public Map<String, String> parseOverrideFragment(Device device) {
        return Optional.ofNullable(device.getOverrideFragment())
                .filter(overrideFragment -> !overrideFragment.isEmpty())
                .map(this::parseFragment)
                .orElseGet(HashMap::new);
    }

    private Map<String, String> parseFragment(String overrideFragment) {
        if (overrideFragment.trim().startsWith("{")) {
            return parseJson(overrideFragment);
        } else {
            return parseProperties(overrideFragment);
        }
    }

    private Map<String, String> parseJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            throw new OverrideFragmentException("Failed to parse overrideFragment as JSON: " + json, e);
        }
    }

    private Map<String, String> parseProperties(String properties) {
        return Arrays.stream(properties.split("\n"))
                .map(line -> line.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }
}
