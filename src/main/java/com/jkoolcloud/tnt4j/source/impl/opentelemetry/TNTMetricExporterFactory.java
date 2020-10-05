package com.jkoolcloud.tnt4j.source.impl.opentelemetry;


import io.opentelemetry.javaagent.spi.exporter.MetricExporterFactory;
import io.opentelemetry.sdk.metrics.export.MetricExporter;

import java.util.Properties;

public class TNTMetricExporterFactory implements MetricExporterFactory {
    @Override
    public MetricExporter fromConfig(Properties config) {
        return new TNTMetricExporter("agent");
    }
}
