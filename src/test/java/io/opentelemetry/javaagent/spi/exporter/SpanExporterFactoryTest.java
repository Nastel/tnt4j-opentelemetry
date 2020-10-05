package io.opentelemetry.javaagent.spi.exporter;

import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.*;

class SpanExporterFactoryTest {

    @Test
    public void testServiceLoad() {
        ServiceLoader<SpanExporterFactory> serviceLoader = ServiceLoader.load(SpanExporterFactory.class);
        assertTrue(serviceLoader.iterator().next() != null);

    }

}
