# tnt4j-opentelemetry
OpenTelemetry Exporters for TNT4j

# OpenTelemetry to TNT Transformation

This document defines the transformation between OpenTelemetry and TNT.


## Summary

The following table summarizes the major transformations between OpenTelemetry
and TNT.

| OpenTelemetry            | TNT              | Notes                                                                                         |
| ------------------------ | ---------------- | --------------------------------------------------------------------------------------------- |
| Span.TraceID             | Correlator       | TNT will correlate spans to common activity.                                                 |
| Span.ParentID            |                  |                                                                                               |
| Span.SpanID              |                  |                                                                                               |
| Span.TraceState          |                  |                                                                                               |
| Span.Name                | Name             |                                                                                               |
| Span.Kind                |                  |                                                                                               |
| Span.StartTime           | StartTime        |                                                                                               |
| Span.EndTime             | Endtime          |                                                                                               |
| Span.Attributes          | Properties       |                                                                                               |
| Span.Events              |                  |                                                                                               |
| Span.Links               |                  |                                                                                               |
| Span.Status              |                  |                                                                                               |
| Span.LocalChildSpanCount |                  |                                                                                               |




| OpenTelemetry            | TNT                | Notes                                                                                         |
| ------------------------ | ------------------ | --------------------------------------------------------------------------------------------- |
| Metric.Name              | EventName          |                                                                                               |
| Metric.Points            | Snapshots          |                                                                                               |
| Metric.Points.EpohNanos  | Snapshot.Timestamp |                                                                                               |
| Metric.Labels            | Snapshot.Properties|                                                                                               |



## Using metrics for opentelemetry-java-instrumentation

You need custom build to enable TNT over opentelemetry-java-instrumentation. 

Opentelemetry-java-instrumentation needs custom exporter factories to load TNT metric exporter.

The dependency for exporters:

```
compile group: 'com.jkoolcloud', name: 'tnt4j-opentelemetry', version: '0.4-SNAPSHOT',
```

Metric exporter:

```java
package io.opentelemetry.javaagent.exporters;

import com.google.auto.service.AutoService;
import com.jkoolcloud.tnt4j.source.impl.opentelemetry.TNTMetricExporter;
import io.opentelemetry.javaagent.spi.exporter.MetricExporterFactory;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import java.util.Properties;

@AutoService(MetricExporterFactory.class)
public class TNTMetricExporterFactory implements MetricExporterFactory {
  @Override
  public MetricExporter fromConfig(Properties config) {
    return new TNTMetricExporter("agent");
  }
}

```


Span exporter:

```java
package io.opentelemetry.javaagent.exporters;

import com.google.auto.service.AutoService;
import com.jkoolcloud.tnt4j.source.impl.opentelemetry.TNTSpanExporter;
import io.opentelemetry.javaagent.spi.exporter.SpanExporterFactory;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.Properties;

@AutoService(SpanExporterFactory.class)
public class TNTSpanExporterFactory implements SpanExporterFactory {
  @Override
  public SpanExporter fromConfig(Properties config) {
    return new TNTSpanExporter("agent");
  }
}
```

Once the exporter's is ready you need build uber-jar:
```
./gradlew assemble
./gradlew :javaagent:shadowJar
``` 

The sample project is [here](https://github.com/mjok/opentelemetry-java)

### Running custom built opentelemetry-java-instrumentation agent


To load tnt4j.properties you need to add java property (-D):

```
set "JAVA_OPTS=%JAVA_OPTS% -Dtnt4j.config=c:/ota/tnt4j_dev.properties
```

For TNT4J you need to specify another dault evet factory:

```
"-Dtnt4j.default.event.factory=com.jkoolcloud.tnt4j.sink.impl.slf4j.SLF4JEventSinkFactory"
```

last step is to specify specify Java agent  and laod TNT exporter:

```
-javaagent:c:\ota\opentelemetry-javaagent-all.jar -Dotel.exporter=tnt"
```

