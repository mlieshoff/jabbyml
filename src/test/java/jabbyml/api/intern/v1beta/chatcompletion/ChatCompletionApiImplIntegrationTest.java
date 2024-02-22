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
package jabbyml.api.intern.v1beta.chatcompletion;

import static wiremock.org.apache.commons.lang3.StringUtils.EMPTY;

import jabbyml.IntegrationTestBase;
import jabbyml.essentials.connector.HttpMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class ChatCompletionApiImplIntegrationTest extends IntegrationTestBase {

    private ChatCompletionApi unitUnderTest;

    @BeforeEach
    void setUp() {
        unitUnderTest = getJabbyML().getApi(ChatCompletionApi.class);
    }

    @Test
    void get() throws Exception {
        List<ChatCompletionMessage> messages = null;
        jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionRequest.ChatCompletionRequestBuilder
                builder =
                        jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionRequest.builder(
                                messages);
        jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionRequest request =
                builder.storeRawResponse(true).build();

        prepare(
                "/v1beta/chat/completions",
                EMPTY,
                HttpMethod.POST,
                Map.of("Content-Type", "application/json", "accept", "text/event-stream"),
                "src/test/resources/chatCompletion-get.json");
        jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionResponse expected =
                toJson(
                        jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionResponse.class,
                        readJson("src/test/resources/chatCompletion-get-expected.json"));

        run(expected, () -> unitUnderTest.get(request).get());
    }

    @Test
    void get_whenWithException() {
        List<ChatCompletionMessage> messages = null;
        jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionRequest.ChatCompletionRequestBuilder
                builder =
                        jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionRequest.builder(
                                messages);
        jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionRequest request =
                builder.storeRawResponse(true).build();

        prepareWithErrorAndRun(
                "/v1beta/chat/completions",
                EMPTY,
                HttpMethod.POST,
                Map.of("Content-Type", "application/json", "accept", "text/event-stream"),
                request,
                () -> unitUnderTest.get(request).get());
    }
}
