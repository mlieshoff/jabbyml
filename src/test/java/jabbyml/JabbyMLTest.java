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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jabbyml.api.intern.v1.completions.CompletionApi;
import jabbyml.essentials.api.Api;
import jabbyml.essentials.api.ApiContext;
import jabbyml.essentials.api.BaseApi;
import jabbyml.essentials.connector.Connector;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JabbyMLTest {

  private static final String URL = "url";
  private static final String API_KEY = "apiKey";

  private JabbyML unitUnderTest;

  @Mock private Connector connector;

  @BeforeEach
  void setUp() {
    unitUnderTest = new JabbyML(URL, API_KEY, connector);
  }

  @Test
  void listApis_whenCalled_shouldReturnListOfApiInterfaceNames() {
    Set<String> expected =
        new HashSet<>(
            Arrays.asList(
                "jabbyml.api.intern.v1beta.search.SearchApi",
                "jabbyml.api.intern.v1.completions.CompletionApi",
                "jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionApi",
                "jabbyml.api.intern.v1.health.HealthApi",
                "jabbyml.api.intern.v1.events.EventApi"));

    List<String> actual = unitUnderTest.listApis();

    assertEquals(expected, new HashSet<>(actual));
  }

  @Test
  void getApi_whenWithValidParameter_shouldReturnApiInstance() {
    CompletionApi actual = unitUnderTest.getApi(CompletionApi.class);

    assertNotNull(actual);
  }

  @ParameterizedTest
  @CsvSource(value = "null,", nullValues = "null")
  void registerApi_whenWithNull_thenThrowException(String actual) {

    assertThrows(
        IllegalArgumentException.class, () -> unitUnderTest.register(FooApi.class, actual));
  }

  @Test
  void registerAndGetApi_whenWithValidParameter_shouldRegister() {
    unitUnderTest.register(FooApi.class, FooApiImpl.class.getName());
    FooApi actual = unitUnderTest.getApi(FooApi.class);

    assertNotNull(actual);
  }

  @Test
  void getApi_whenClassNotFound_thenThrowException() {

    assertThrows(IllegalStateException.class, () -> unitUnderTest.getApi(FooApi.class));
  }

  public interface FooApi extends Api {}

  public static class FooApiImpl extends BaseApi implements FooApi {

    public FooApiImpl(@NonNull ApiContext apiContext) {
      super(apiContext);
    }
  }
}
