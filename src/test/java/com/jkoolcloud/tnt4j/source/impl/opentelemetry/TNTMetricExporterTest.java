package com.jkoolcloud.tnt4j.source.impl.opentelemetry;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.common.Labels;
import io.opentelemetry.context.Scope;
import io.opentelemetry.metrics.AsynchronousInstrument;
import io.opentelemetry.metrics.DoubleCounter;
import io.opentelemetry.metrics.LongValueObserver;
import io.opentelemetry.metrics.Meter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.TracerSdkProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Status;
import io.opentelemetry.trace.Tracer;
import org.junit.jupiter.api.Test;
import io.opentelemetry.exporters.logging.LoggingSpanExporter;

import static org.junit.jupiter.api.Assertions.*;

class TNTMetricExporterTest {

    private static final LoggingSpanExporter loggingExporter = new LoggingSpanExporter();

    @Test
    public void metricExporterTest() {
        Tracer tracer = OpenTelemetry.getTracer("Test");
        TracerSdkProvider tracerProvider = OpenTelemetrySdk.getTracerProvider();

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
            Status status = Status.UNKNOWN.withDescription("Error while finding file");
            span.setStatus(status);
        } finally {
            span.end();
        }

    }
}
