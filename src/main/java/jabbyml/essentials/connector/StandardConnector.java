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
package jabbyml.essentials.connector;

import static jabbyml.essentials.common.Utils.isNotEmpty;
import static jabbyml.essentials.common.Utils.require;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jabbyml.essentials.common.IResponse;
import jabbyml.essentials.common.RawResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StandardConnector implements Connector {

  private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

  @Override
  public <T extends IResponse> T get(RequestContext requestContext) throws ConnectorException {
    return request(requestContext, HttpMethod.GET);
  }

  private String appendToUrl(
      String url, Map<String, Object> parameters, Map<String, Object> restParameters) {
    StringBuilder appendedUrl = new StringBuilder(url);
    List<String> queries = new ArrayList<>();
    if (isNotEmpty(parameters)) {
      for (Map.Entry<String, Object> entry : parameters.entrySet()) {
        String name = entry.getKey();
        Object value = entry.getValue();
        if (value != null) {
          queries.add(name + '=' + encode(String.valueOf(value)));
        }
      }
      if (!queries.isEmpty()) {
        appendedUrl.append('?');
        for (Iterator<String> iterator = queries.iterator(); iterator.hasNext(); ) {
          appendedUrl.append(iterator.next());
          if (iterator.hasNext()) {
            appendedUrl.append('&');
          }
        }
      }
    }
    String result = appendedUrl.toString();
    for (Map.Entry<String, Object> entry : restParameters.entrySet()) {
      String encodedValue = encode(String.valueOf(entry.getValue()));
      result = result.replace('{' + entry.getKey() + '}', encodedValue);
    }
    log.info("request to: {}", result);
    return result;
  }

  private static String encode(String s) {
    return URLEncoder.encode(s, UTF_8);
  }

  protected String getInitialValue(String url, RequestContext requestContext) throws IOException {
    return null;
  }

  private static void logResponse(HttpResponse<String> httpResponse) {
    if (log.isInfoEnabled()) {
      for (Map.Entry<String, List<String>> entry : httpResponse.headers().map().entrySet()) {
        String name = entry.getKey();
        List<String> values = entry.getValue();
        log.info("    response header: {}={}", name, String.join(",", values));
      }
      int code = httpResponse.statusCode();
      log.info("    status code: {}", code);
    }
  }

  protected void onResponseBodyReceived(String url, String json, RequestContext requestContext) throws IOException {
    // do nothing here
  }

  protected HttpResponse<String> checkResponse(String json, HttpResponse<String> response) {
    return response;
  }

  private <T extends IResponse> T request(RequestContext requestContext, HttpMethod httpMethod)
      throws ConnectorException {
    require("requestContext", requestContext);
    try {
      String url = requestContext.url();
      String replacedUrl =
          appendToUrl(
              url,
              requestContext.request().getQueryParameters(),
              requestContext.request().getRestParameters());
      String responseBody = getInitialValue(replacedUrl, requestContext);
      boolean makeRequest = responseBody == null || responseBody.isEmpty();
      HttpResponse<String> response = null;
      if (makeRequest) {
        HttpRequest.Builder builder =
            HttpRequest.newBuilder()
                .uri(new URI(replacedUrl))
                .header("Authorization", "Bearer " + requestContext.apiKey());
        switch (httpMethod) {
          case POST ->
              builder =
                  builder.POST(
                      HttpRequest.BodyPublishers.ofString(GSON.toJson(requestContext.request())));
          case GET -> builder = builder.GET();
        }
        requestContext.headers().forEach(builder::header);
        HttpRequest request = builder.build();
        response =
            HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        logResponse(response);
        if (response.statusCode() != 200) {
          throw new ConnectorException(String.valueOf(response.statusCode()));
        }
        responseBody = response.body();
        onResponseBodyReceived(replacedUrl, responseBody, requestContext);
      }
      HttpResponse<String> checkedResponse = checkResponse(responseBody, response);
      log.info("    response content: {}", responseBody);
      T result = handleResponse(requestContext, responseBody);
      if (requestContext.request().isStoreRawResponse()) {
        setRawResponse(result, responseBody, checkedResponse);
      }
      return result;
    } catch (InterruptedException | URISyntaxException | IOException e) {
      throw new ConnectorException(e);
    }
  }

  private <T extends IResponse> T handleResponse(
      RequestContext requestContext, String responseBody) {
    if ("text/event-stream".equals(requestContext.headers().get("accept"))) {
      List<String> jsonList = new ArrayList<>();
      String[] lines = responseBody.split("\n");
      for (String line : lines) {
        if (line.startsWith("data: ")) {
          line = line.substring("data: ".length());
          jsonList.add(line);
        }
      }
      StringBuilder jsonAsList = new StringBuilder();
      jsonAsList.append("[");
      for (Iterator<String> iterator = jsonList.iterator(); iterator.hasNext(); ) {
        jsonAsList.append(iterator.next());
        if (iterator.hasNext()) {
          jsonAsList.append(",");
        }
      }
      jsonAsList.append("]");
      String json = jsonAsList.toString();
      return (T) GSON.fromJson(json, requestContext.responseClass());
    } else if ("application/json".equals(requestContext.headers().get("accept"))) {
      return (T) GSON.fromJson(responseBody, requestContext.responseClass());
    } else if ("*/*".equals(requestContext.headers().get("accept"))) {
      return (T) GSON.fromJson(responseBody, requestContext.responseClass());
    } else {
      throw new IllegalStateException("accept header not set!");
    }
  }

  private <T extends IResponse> void setRawResponse(
      T result, String json, HttpResponse<String> response) {
    if (result != null) {

      RawResponse rawResponse = new RawResponse();
      rawResponse.setRaw(json);
      HttpHeaders headers = response.headers();
      if (headers != null) {
        for (Map.Entry<String, List<String>> entry : headers.map().entrySet()) {
          String name = entry.getKey();
          List<String> values = entry.getValue();
          rawResponse
                  .getResponseHeaders()
                  .put(name.toLowerCase(Locale.ROOT), String.join(",", values));
        }
      }
      result.setRawResponse(rawResponse);
    }
  }

  @Override
  public <T extends IResponse> T post(RequestContext requestContext) throws ConnectorException {
    return request(requestContext, HttpMethod.POST);
  }
}
