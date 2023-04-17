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
import java.util.Map.Entry;

import com.jkoolcloud.tnt4j.TrackingLogger;
import com.jkoolcloud.tnt4j.core.Property;
import com.jkoolcloud.tnt4j.tracker.TrackingEvent;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;

public class TNTSpanExporter implements SpanExporter {
	/*
	 * Tracking logger instance where all messages are logged
	 */
	private TrackingLogger logger;

	public TNTSpanExporter(String source) {
		this.logger = TrackingLogger.getInstance(source);
	}

	@Override
	public CompletableResultCode export(Collection<SpanData> spans) {
		for (SpanData span: spans) {
            TrackingEvent trackingEvent = logger.newEvent(span.getName(), span.toString());
            trackingEvent.getOperation().start(span.getStartEpochNanos()/1000);
            trackingEvent.getOperation().stop(span.getEndEpochNanos()/1000);
            trackingEvent.setCorrelator(span.getSpanId(), span.getTraceId());
            
            // extract span attributes
            Attributes attributes = span.getAttributes();
            for (Entry<AttributeKey<?>, Object> attr: attributes.asMap().entrySet()) {
                trackingEvent.getOperation().addProperty(new Property(attr.getKey().getKey(), attr.getValue()));         	
            }
            logger.tnt(trackingEvent);
		}
		return CompletableResultCode.ofSuccess();
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
	public CompletableResultCode shutdown() {
		logger.close();
		return CompletableResultCode.ofSuccess();
	}
	
	public TNTSpanExporter open() throws IOException {
		logger.open();
		return this;
	}
}
