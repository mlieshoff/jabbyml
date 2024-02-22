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
package jabbyml.api.intern.v1beta.search;

import jabbyml.IntegrationTestBase;
import jabbyml.essentials.connector.HttpMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class SearchApiImplIntegrationTest extends IntegrationTestBase {

    private SearchApi unitUnderTest;

    @BeforeEach
    void setUp() {
        unitUnderTest = getJabbyML().getApi(SearchApi.class);
    }

    @Test
    void search() throws Exception {
        String q = "q";
        int limit = 815;
        int offset = 815;
        jabbyml.api.intern.v1beta.search.SearchRequest.SearchRequestBuilder builder =
                jabbyml.api.intern.v1beta.search.SearchRequest.builder(q, limit, offset);
        jabbyml.api.intern.v1beta.search.SearchRequest request =
                builder.storeRawResponse(true).build();

        prepare(
                "/v1beta/search",
                "q=" + q + "&limit=" + limit + "&offset=" + offset,
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                "src/test/resources/search-search.json");
        jabbyml.api.intern.v1beta.search.SearchResponse expected =
                toJson(jabbyml.api.intern.v1beta.search.SearchResponse.class, getExpected());

        run(expected, () -> unitUnderTest.search(request).get());
    }

    @Test
    void search_whenWithException() {
        String q = "q";
        int limit = 815;
        int offset = 815;
        jabbyml.api.intern.v1beta.search.SearchRequest.SearchRequestBuilder builder =
                jabbyml.api.intern.v1beta.search.SearchRequest.builder(q, limit, offset);
        jabbyml.api.intern.v1beta.search.SearchRequest request =
                builder.storeRawResponse(true).build();

        prepareWithErrorAndRun(
                "/v1beta/search",
                "q=" + q + "&limit=" + limit + "&offset=" + offset,
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                request,
                () -> unitUnderTest.search(request).get());
    }
}
