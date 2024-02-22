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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.common.ContentTypes.AUTHORIZATION;
import static java.util.Collections.emptyMap;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.readLines;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static wiremock.org.apache.commons.lang3.StringUtils.EMPTY;
import static wiremock.org.apache.commons.lang3.StringUtils.isNotBlank;
import static wiremock.org.apache.hc.core5.http.HttpStatus.SC_BAD_REQUEST;
import static wiremock.org.apache.hc.core5.http.HttpStatus.SC_OK;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jabbyml.essentials.common.IResponse;
import jabbyml.essentials.common.Request;
import jabbyml.essentials.connector.HttpMethod;
import jabbyml.essentials.connector.StandardConnector;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class IntegrationTestBase {

  private static final Gson GSON =
      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private static final ThreadLocal<String> expectedRawResponse = new ThreadLocal<>();

  private static WireMockServer wireMockServer;

  JabbyML jabbyML;

  @BeforeAll
  public static void beforeAll() {
    WireMockConfiguration wireMockConfiguration = new WireMockConfiguration().dynamicPort();
    wireMockServer = new WireMockServer(wireMockConfiguration);
    wireMockServer.start();
  }

  private static JabbyML createJabbyML() {
    return new JabbyML(
        "http://localhost:" + wireMockServer.port(), "myApiKey", new StandardConnector());
  }

  protected static <T> T toJson(Class<T> clazz, String s) {
    return GSON.fromJson(s, clazz);
  }

  protected static String body(String filename) throws IOException {
    List<String> lines = readLines(new File(filename), StandardCharsets.UTF_8);
    String s = lines.stream().map(String::trim).collect(Collectors.joining());
    expectedRawResponse.set(s);
    return s;
  }

  protected static String plainBody(String filename) throws IOException {
    expectedRawResponse.set(readJson(filename));
    return expectedRawResponse.get();
  }

  protected static String readJson(String filename) throws IOException {
    return readFileToString(new File(filename), StandardCharsets.UTF_8);
  }

  @BeforeEach
  public void beforeEach() {
    configureFor("localhost", wireMockServer.port());
    jabbyML = createJabbyML();
  }

  @AfterEach
  public void afterEach() {
    wireMockServer.resetAll();
  }

  protected String getExpected() {
    return expectedRawResponse.get();
  }

  protected void prepare(
      String url,
      String queryPart,
      HttpMethod httpMethod,
      Map<String, String> headers,
      String filename)
      throws Exception {
    MappingBuilder mappingBuilder;
    switch (httpMethod) {
      case POST -> mappingBuilder = post(urlEqualTo(createUrl(url, queryPart)));
      default -> mappingBuilder = get(urlEqualTo(createUrl(url, queryPart)));
    }
    mappingBuilder.withHeader(AUTHORIZATION, equalTo("Bearer myApiKey"));
    headers.forEach((key, value) -> mappingBuilder.withHeader(key, equalTo(value)));
    String content;
    if (headers.getOrDefault("accept", "").equals("text/event-stream")) {
      content = plainBody(filename);
    } else {
      content = body(filename);
    }
    stubFor(mappingBuilder.willReturn(aResponse().withBody(content).withStatus(SC_OK)));
  }

  private String createUrl(String url, String queryPart) {
    String createdUrl = url;
    if (isNotBlank(queryPart)) {
      createdUrl += '?' + queryPart;
    }
    return createdUrl;
  }

  protected void run(IResponse expected, ResultTestRunner<? extends IResponse> resultTestRunner)
      throws Exception {
    IResponse actual = resultTestRunner.execute();
    assertEquals(expected, actual);
    if (actual != null) {
      assertNotEquals(emptyMap(), actual.getRawResponse().getResponseHeaders());
      assertEquals(getExpected(), actual.getRawResponse().getRaw());
      assertNotEquals(EMPTY, actual.toString());
    }
  }

  protected void prepareWithErrorAndRun(
      String url,
      String queryPart,
      HttpMethod httpMethod,
      Map<String, String> headers,
      Request request,
      TestRunner testRunner) {
    prepareWithError(url, queryPart, httpMethod, headers);
    try {
      testRunner.execute();
      fail();
    } catch (Exception e) {
      assertEquals("jabbyml.essentials.connector.ConnectorException: 400", e.getMessage());
    }
  }

  private void prepareWithError(
      String url, String queryPart, HttpMethod httpMethod, Map<String, String> headers) {
    MappingBuilder mappingBuilder;
    switch (httpMethod) {
      case POST -> mappingBuilder = post(urlEqualTo(createUrl(url, queryPart)));
      default -> mappingBuilder = get(urlEqualTo(createUrl(url, queryPart)));
    }
    mappingBuilder.withHeader(AUTHORIZATION, equalTo("Bearer myApiKey"));
    headers.forEach((key, value) -> mappingBuilder.withHeader(key, equalTo(value)));
    stubFor(mappingBuilder.willReturn(aResponse().withBody("body").withStatus(SC_BAD_REQUEST)));
  }

  protected JabbyML getJabbyML() {
    return jabbyML;
  }

  @FunctionalInterface
  public interface TestRunner {
    void execute() throws Exception;
  }

  @FunctionalInterface
  public interface ResultTestRunner<T extends IResponse> {
    T execute() throws Exception;
  }
}
