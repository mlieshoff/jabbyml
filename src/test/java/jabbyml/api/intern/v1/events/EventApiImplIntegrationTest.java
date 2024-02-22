/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jabbyml.api.intern.v1.events;

import static wiremock.org.apache.commons.lang3.StringUtils.EMPTY;

import jabbyml.IntegrationTestBase;
import jabbyml.essentials.connector.HttpMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class EventApiImplIntegrationTest extends IntegrationTestBase {

    private EventApi unitUnderTest;

    @BeforeEach
    void setUp() {
        unitUnderTest = getJabbyML().getApi(EventApi.class);
    }

    @Test
    void find() throws Exception {
        String type = "type";
        String completionId = "completionId";
        int choiceIndex = 815;
        String viewIndex = "viewIndex";
        int elapsed = 815;
        jabbyml.api.intern.v1.events.EventRequest.EventRequestBuilder builder =
                jabbyml.api.intern.v1.events.EventRequest.builder(
                        type, completionId, choiceIndex, viewIndex, elapsed);
        jabbyml.api.intern.v1.events.EventRequest request = builder.storeRawResponse(true).build();

        prepare(
                "/v1/events",
                EMPTY,
                HttpMethod.POST,
                Map.of("Content-Type", "application/json", "accept", "*/*"),
                "src/test/resources/event-find.json");
        jabbyml.api.intern.v1.events.EventResponse expected =
                toJson(jabbyml.api.intern.v1.events.EventResponse.class, getExpected());

        run(expected, () -> unitUnderTest.find(request).get());
    }

    @Test
    void find_whenWithException() {
        String type = "type";
        String completionId = "completionId";
        int choiceIndex = 815;
        String viewIndex = "viewIndex";
        int elapsed = 815;
        jabbyml.api.intern.v1.events.EventRequest.EventRequestBuilder builder =
                jabbyml.api.intern.v1.events.EventRequest.builder(
                        type, completionId, choiceIndex, viewIndex, elapsed);
        jabbyml.api.intern.v1.events.EventRequest request = builder.storeRawResponse(true).build();

        prepareWithErrorAndRun(
                "/v1/events",
                EMPTY,
                HttpMethod.POST,
                Map.of("Content-Type", "application/json", "accept", "*/*"),
                request,
                () -> unitUnderTest.find(request).get());
    }
}
