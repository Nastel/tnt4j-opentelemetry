package com.jkoolcloud.tnt4j.source.impl.opentelemetry;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.context.Scope;
import io.opentelemetry.metrics.DoubleCounter;
import io.opentelemetry.metrics.Meter;
import io.opentelemetry.sdk.trace.TracerSdkProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Tracer;
import io.opentelemetry.exporters.logging.LoggingSpanExporter;
import org.junit.Test;


class TNTMetricExporterTest {

    private static final LoggingSpanExporter loggingExporter = new LoggingSpanExporter();

    @Test
    public void metricExporterTest() {
        Tracer tracer = OpenTelemetry.getTracer("Test");

        TracerSdkProvider tracerProvider =TracerSdkProvider.builder().build();

        tracerProvider.addSpanProcessor(SimpleSpanProcessor.newBuilder(loggingExporter).build());
        Meter sampleMeter =
                OpenTelemetry.getMeterProvider().get("io.opentelemetry.example.metrics", "0.5");

        Span span = tracer.spanBuilder("calculate space").setSpanKind(Span.Kind.INTERNAL).startSpan();

        DoubleCounter diskSpaceCounter =
                sampleMeter
                        .doubleCounterBuilder("calculated_used_space")
                        .setDescription("Counts disk space used by file extension.")
                        .setUnit("MB")
                        .build();
        try (Scope scope = tracer.withSpan(span)) {
            diskSpaceCounter.add(1);
        } catch (Exception e) {

        } finally {
            span.end();
        }

    }
}

