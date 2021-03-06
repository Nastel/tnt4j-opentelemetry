/*
 * Copyright 2014-2019 JKOOL, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jkoolcloud.tnt4j.source.impl.opentelemetry;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import com.jkoolcloud.tnt4j.TrackingLogger;

import com.jkoolcloud.tnt4j.core.PropertySnapshot;
import com.jkoolcloud.tnt4j.core.Snapshot;
import com.jkoolcloud.tnt4j.core.UsecTimestamp;
import com.jkoolcloud.tnt4j.tracker.TrackingEvent;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;

public class TNTMetricExporter implements MetricExporter {

    /*
     * Tracking logger instance where all messages are logged
     */
    private TrackingLogger logger;


    public TNTMetricExporter(String source) {
        this.logger = TrackingLogger.getInstance(source);
    }

    @Override
    public CompletableResultCode export(Collection<MetricData> metrics) {
        for (MetricData metric : metrics) {
            export(metric);
        }
        return CompletableResultCode.ofSuccess();
    }

    private void export(MetricData metric) {
        TrackingEvent trackingEvent = logger.newEvent(metric.getName(), metric.getDescription());

        Iterator<MetricData.Point> iterator = metric.getPoints().iterator();

        int i =1;

        while (iterator.hasNext()) {
            MetricData.Point point = iterator.next();

            String pointName = "Point";

            if (i != 1) {
                pointName += "_" + point;
            }
            i++;

            Snapshot snapshot = logger.newSnapshot(pointName);
            if (snapshot instanceof PropertySnapshot) {
                ((PropertySnapshot) snapshot).setTimeStamp(new UsecTimestamp(point.getEpochNanos()/1000));
            }

            point.getLabels().forEach((key, value) -> snapshot.add(key, value));

            if (point instanceof MetricData.LongPoint) {
                snapshot.add("value", ((MetricData.LongPoint) point).getValue());
            }
            if (point instanceof MetricData.DoublePoint) {
                snapshot.add("value", ((MetricData.DoublePoint) point).getValue());
            }
            if (point instanceof MetricData.SummaryPoint) {
                snapshot.add("count", ((MetricData.SummaryPoint) point).getCount());
                snapshot.add("sum", ((MetricData.SummaryPoint) point).getSum());
            }

                trackingEvent.getOperation().addSnapshot(snapshot);

        }
        logger.tnt(trackingEvent);

    }

    @Override
    public CompletableResultCode flush() {
        CompletableResultCode resultCode = new CompletableResultCode();
        try {
            logger.getEventSink().flush();
            resultCode.succeed();
        } catch (IOException e) {
            resultCode.fail();
        }
        return resultCode;
    }

    @Override
    public void shutdown() {
        logger.close();
    }

    public TNTMetricExporter open() throws IOException {
        logger.open();
        return this;
    }

    public static class Builder {
        String appName;

        public Builder(String appName) {
            this.appName = appName;
        }

        public TNTMetricExporter build() throws IOException {
            TNTMetricExporter exporter = new TNTMetricExporter(appName);
            return exporter.open();
        }
    }
}

