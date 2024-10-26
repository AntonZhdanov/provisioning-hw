package com.voxloud.provisioning.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.entity.Device;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OverrideFragmentParserTest {
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OverrideFragmentParser overrideFragmentParser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Should return the correct override value when properties are provided")
    @Test
    void testGetOverrideValueWithProperties() {
        Device device = mock(Device.class);
        String propertiesFragment = "key1=value1\nkey2=value2";
        when(device.getOverrideFragment()).thenReturn(propertiesFragment);

        String result = overrideFragmentParser.getOverrideValue(device, "key1", "defaultValue");

        assertEquals("value1", result);
    }

    @DisplayName("Should return default value when key is not found")
    @Test
    void testGetOverrideValueWhenKeyNotFound() {
        Device device = mock(Device.class);
        String propertiesFragment = "key1=value1\nkey2=value2";
        when(device.getOverrideFragment()).thenReturn(propertiesFragment);

        String result = overrideFragmentParser.getOverrideValue(device, "key3", "defaultValue");

        assertEquals("defaultValue", result);
    }

    @DisplayName("Should return empty map when override fragment is empty")
    @Test
    void testParseOverrideFragmentWithEmptyFragment() {
        Device device = mock(Device.class);
        when(device.getOverrideFragment()).thenReturn("");

        Map<String, String> result = overrideFragmentParser.parseOverrideFragment(device);

        assertTrue(result.isEmpty());
    }

    @DisplayName("Should return empty map when no override fragment is provided")
    @Test
    void testParseOverrideFragmentReturnsEmptyMapWhenNoFragment() {
        Device device = mock(Device.class);
        when(device.getOverrideFragment()).thenReturn(null);

        Map<String, String> result = overrideFragmentParser.parseOverrideFragment(device);

        assertTrue(result.isEmpty());
    }

    @DisplayName("Should return the correct override value with properties and spaces around keys")
    @Test
    void testGetOverrideValueWithPropertiesAndSpaces() {
        Device device = mock(Device.class);
        String propertiesFragment = "key1 = value1\nkey2=value2\n key3 = value3 ";
        when(device.getOverrideFragment()).thenReturn(propertiesFragment);

        String result = overrideFragmentParser.getOverrideValue(device, "key3", "defaultValue");

        assertEquals("value3", result);
    }

    @DisplayName("Should handle malformed input in properties correctly")
    @Test
    void testParsePropertiesHandlesMalformedInput() {
        Device device = mock(Device.class);
        String propertiesFragment = "key1=value1\nkey2\nkey3=value3";
        when(device.getOverrideFragment()).thenReturn(propertiesFragment);

        Map<String, String> result = overrideFragmentParser.parseOverrideFragment(device);

        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value3", result.get("key3"));
        assertNull(result.get("key2"));
    }

    @DisplayName("Should ignore comments in properties and return correct values")
    @Test
    void testParsePropertiesHandlesComments() {
        Device device = mock(Device.class);
        String propertiesFragment = "key1=value1\n# This is a comment\nkey2=value2";
        when(device.getOverrideFragment()).thenReturn(propertiesFragment);

        Map<String, String> result = overrideFragmentParser.parseOverrideFragment(device);

        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @DisplayName("Should return correct values with spaces around equal signs")
    @Test
    void testGetOverrideValueWithPropertiesWithSpacesAroundEqual() {
        Device device = mock(Device.class);
        String propertiesFragment = "key1 = value1\nkey2=value2\nkey3 = value3";
        when(device.getOverrideFragment()).thenReturn(propertiesFragment);

        String result1 = overrideFragmentParser.getOverrideValue(device, "key1", "defaultValue");
        String result2 = overrideFragmentParser.getOverrideValue(device, "key2", "defaultValue");
        String result3 = overrideFragmentParser.getOverrideValue(device, "key3", "defaultValue");

        assertEquals("value1", result1);
        assertEquals("value2", result2);
        assertEquals("value3", result3);
    }
}
