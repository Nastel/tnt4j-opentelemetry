# tnt4j-opentelemetry
OpenTelemetry Exporters for TNT4j

# OpenTelemetry to TNT Transformation

This document defines the transformation between OpenTelemetry and TNT.


## Summary

The following table summarizes the major transformations between OpenTelemetry
and TNT.

| OpenTelemetry            | TNT              | Notes                                                                                         |
| ------------------------ | ---------------- | --------------------------------------------------------------------------------------------- |
| Span.TraceID             | Correlator       | XRay will correlate spans to common actinity.                                                 |
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




| OpenTelemetry            | TNT              | Notes                                                                                         |
| ------------------------ | ---------------- | --------------------------------------------------------------------------------------------- |
| Metric.Name              | EventName        |                                                                                               |
| Metric.Points            | Snapshots        |                                                                                               |
| Metric.Points.EpohNanos  | Snapshot.Timestamp|                                                                                               |
| Metric.Labels            | Snapshot.Properties|                                                                                               |

