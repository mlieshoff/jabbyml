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
package jabbyml.api.intern.v1.health;

import static wiremock.org.apache.commons.lang3.StringUtils.EMPTY;

import jabbyml.IntegrationTestBase;
import jabbyml.essentials.connector.HttpMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class HealthApiImplIntegrationTest extends IntegrationTestBase {

    private HealthApi unitUnderTest;

    @BeforeEach
    void setUp() {
        unitUnderTest = getJabbyML().getApi(HealthApi.class);
    }

    @Test
    void get() throws Exception {
        jabbyml.api.intern.v1.health.HealthRequest.HealthRequestBuilder builder =
                jabbyml.api.intern.v1.health.HealthRequest.builder();
        jabbyml.api.intern.v1.health.HealthRequest request = builder.storeRawResponse(true).build();

        prepare(
                "/v1/health",
                EMPTY,
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                "src/test/resources/health-get.json");
        jabbyml.api.intern.v1.health.HealthResponse expected =
                toJson(jabbyml.api.intern.v1.health.HealthResponse.class, getExpected());

        run(expected, () -> unitUnderTest.get(request).get());
    }

    @Test
    void get_whenWithException() {
        jabbyml.api.intern.v1.health.HealthRequest.HealthRequestBuilder builder =
                jabbyml.api.intern.v1.health.HealthRequest.builder();
        jabbyml.api.intern.v1.health.HealthRequest request = builder.storeRawResponse(true).build();

        prepareWithErrorAndRun(
                "/v1/health",
                EMPTY,
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                request,
                () -> unitUnderTest.get(request).get());
    }
}
