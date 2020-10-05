package com.jkoolcloud.tnt4j.source.impl.opentelemetry;

import io.opentelemetry.javaagent.spi.exporter.MetricExporterFactory;
import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.*;

class TNTMetricExporterFactoryTest {

    @Test
    public void testServiceLoad() {
        ServiceLoader<MetricExporterFactory> serviceLoader = ServiceLoader.load(MetricExporterFactory.class);
        assertTrue(serviceLoader.iterator().next() != null);

    }

}
