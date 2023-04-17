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

import com.jkoolcloud.tnt4j.TrackingLogger;
import com.jkoolcloud.tnt4j.core.Snapshot;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.Data;
import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.ExponentialHistogramPointData;
import io.opentelemetry.sdk.metrics.data.HistogramPointData;
import io.opentelemetry.sdk.metrics.data.LongPointData;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.PointData;
import io.opentelemetry.sdk.metrics.data.SummaryPointData;
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
			Snapshot snapshot = exportToSnapshot(metric);
			if (snapshot.size() > 0) {
				logger.tnt(snapshot);
			}
		}
		return CompletableResultCode.ofSuccess();
	}

	private Snapshot exportToSnapshot(MetricData metric) {
		Snapshot snap = logger.newSnapshot(metric.getName());
		extractData(snap, metric.getData());
		return snap;
	}

	private void extractData(Snapshot snap, Data<?> data) {
		Collection<? extends PointData> points = data.getPoints();
		int count = 0;
		String prefix = "Point-" + count + "/";

		for (PointData point : points) {
			if (point instanceof LongPointData) {
				LongPointData apoint = (LongPointData) point;
				snap.add(prefix + "value", apoint.getValue(), "long");
			}
			else if (point instanceof DoublePointData) {
				DoublePointData apoint = (DoublePointData) point;
				snap.add(prefix + "value", apoint.getValue(), "double");				
			}
			else if (point instanceof SummaryPointData) {
				SummaryPointData apoint = (SummaryPointData) point;
				snap.add(prefix + "sum", apoint.getSum(), "double");								
			}
			else if (point instanceof HistogramPointData) {
				HistogramPointData apoint = (HistogramPointData) point;
				snap.add(prefix + "sum", apoint.getSum(), "double");												
				snap.add(prefix + "count", apoint.getCount() , "long");												
				snap.add(prefix + "max", apoint.getMax(), "double");												
				snap.add(prefix + "min", apoint.getMin(), "double");												
			}
			else if (point instanceof ExponentialHistogramPointData) {
				ExponentialHistogramPointData apoint = (ExponentialHistogramPointData) point;
				snap.add(prefix + "sum", apoint.getSum(), "double");												
				snap.add(prefix + "count", apoint.getCount(), "long");												
				snap.add(prefix + "max", apoint.getMax(), "double");												
				snap.add(prefix + "min", apoint.getMin(), "double");												
			}
			count++;
		}
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

	public TNTMetricExporter open() throws IOException {
		logger.open();
		return this;
	}

	@Override
	public AggregationTemporality getAggregationTemporality(InstrumentType instrumentType) {
		return AggregationTemporality.CUMULATIVE;
	}

	@Override
	public CompletableResultCode shutdown() {
		return CompletableResultCode.ofSuccess();
	}
}
