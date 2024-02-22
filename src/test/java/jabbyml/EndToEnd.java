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
package jabbyml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static wiremock.org.apache.commons.lang3.StringUtils.EMPTY;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jabbyml.api.intern.v1.completions.CompletionApi;
import jabbyml.api.intern.v1.completions.CompletionRequest;
import jabbyml.api.intern.v1.completions.CompletionResponse;
import jabbyml.api.intern.v1.completions.Segments;
import jabbyml.api.intern.v1.events.EventApi;
import jabbyml.api.intern.v1.events.EventRequest;
import jabbyml.api.intern.v1.events.EventResponse;
import jabbyml.api.intern.v1.health.HealthApi;
import jabbyml.api.intern.v1.health.HealthRequest;
import jabbyml.api.intern.v1.health.HealthResponse;
import jabbyml.api.intern.v1beta.chatcompletion.ChatCompletion;
import jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionApi;
import jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionChoice;
import jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionMessage;
import jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionRequest;
import jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionResponse;
import jabbyml.api.intern.v1beta.search.SearchApi;
import jabbyml.api.intern.v1beta.search.SearchRequest;
import jabbyml.api.intern.v1beta.search.SearchResponse;
import jabbyml.essentials.connector.FilesystemCachedConnector;
import java.io.StringReader;
import java.util.List;
import javax.json.Json;
import javax.json.JsonPatch;
import javax.json.JsonValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.commons.lang3.StringUtils;

class EndToEnd {

  private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

  private CompletionApi completionApi;

  private EventApi eventApi;

  private HealthApi healthApi;

  private ChatCompletionApi chatCompletionApi;

  private SearchApi searchApi;

  @BeforeEach
  void setUp() {
    JabbyML jabbyML =
        new JabbyML("http://localhost:8088", null, new FilesystemCachedConnector("jabbyml"));
    completionApi = jabbyML.getApi(CompletionApi.class);
    eventApi = jabbyML.getApi(EventApi.class);
    healthApi = jabbyML.getApi(HealthApi.class);
    chatCompletionApi = jabbyML.getApi(ChatCompletionApi.class);
    searchApi = jabbyML.getApi(SearchApi.class);
  }

  @Test
  void completions_find() throws Exception {
    Segments segments = new Segments();
    segments.setPrefix("public static void");
    segments.setSuffix("main");
    CompletionResponse response =
        completionApi
            .find(CompletionRequest.builder("java", segments).storeRawResponse(true).build())
            .get();

    String actual = GSON.toJson(response);

    String expected = response.getRawResponse().getRaw();

    assertDiff(expected, actual);
  }

  private static void assertDiff(String expected, String actual) {
    assertNotEquals("", actual);
    JsonValue source = Json.createReader(new StringReader(actual)).readValue();
    JsonValue target = Json.createReader(new StringReader(expected)).readValue();
    JsonPatch diff;
    try {
      diff = Json.createDiff(source.asJsonObject(), target.asJsonObject());
    } catch (ClassCastException e) {
      diff = Json.createDiff(source.asJsonArray(), target.asJsonArray());
    }
    StringBuilder diffOutput = new StringBuilder();
    diff.toJsonArray().forEach(entry -> diffOutput.append(entry).append('\n'));
    assertEquals(EMPTY, diffOutput.toString());
  }

  @Test
  void events_find() throws Exception {
    EventResponse response =
        eventApi
            .find(
                EventRequest.builder("view", "string", 0, "string", 0)
                    .storeRawResponse(true)
                    .build())
            .get();

    String actual = GSON.toJson(response);

    String expected = response.getRawResponse().getRaw();

    assertDiff(expected, actual);
  }

  @Test
  void search_search() throws Exception {
    SearchResponse response =
        searchApi
            .search(SearchRequest.builder("string", 10, 0).storeRawResponse(true).build())
            .get();

    String actual = GSON.toJson(response);

    String expected = response.getRawResponse().getRaw();

    assertDiff(expected, actual);
  }

  @Test
  void chatCompletion_sightSeeing() throws Exception {
    List<ChatCompletionMessage> chatCompletionMessages =
        List.of(
            createChatCompletionMessage("user", "Suggest me three sightseeing features in Berlin"),
            createChatCompletionMessage(
                "assistant", "Brandenburger Tor, Spandau and the Bundestag"),
            createChatCompletionMessage("user", "What is the most popular?"),
            createChatCompletionMessage("assistant", "The Bundestag"));
    ChatCompletionResponse response =
        chatCompletionApi
            .get(
                ChatCompletionRequest.builder(chatCompletionMessages)
                    .storeRawResponse(true)
                    .build())
            .get();

    String actual = GSON.toJson(response);

    String expected = response.getRawResponse().getRaw();

    assertDiff(expected, actual);
  }

  private ChatCompletionMessage createChatCompletionMessage(String role, String content) {
    ChatCompletionMessage chatCompletionMessage = new ChatCompletionMessage();
    chatCompletionMessage.setContent(content);
    chatCompletionMessage.setRole(role);
    return chatCompletionMessage;
  }

  @Test
  void health_get() throws Exception {
    HealthResponse response =
        healthApi.get(HealthRequest.builder().storeRawResponse(true).build()).get();

    String actual = GSON.toJson(response);

    String expected = response.getRawResponse().getRaw();

    assertDiff(expected, actual);
  }
}
