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
package jabbyml.api.intern;

import jabbyml.api.intern.v1.completions.CompletionApi;
import jabbyml.api.intern.v1.events.EventApi;
import jabbyml.api.intern.v1.health.HealthApi;
import jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionApi;
import jabbyml.api.intern.v1beta.search.SearchApi;
import jabbyml.essentials.api.Api;

import lombok.Getter;

import java.util.Map;

@Getter
public class DefaultApiClasses {

    private final Map<Class<? extends Api>, String> apiClassMap =
            Map.of(
                    CompletionApi.class, "jabbyml.api.intern.v1.completions.CompletionApiImpl",
                    EventApi.class, "jabbyml.api.intern.v1.events.EventApiImpl",
                    HealthApi.class, "jabbyml.api.intern.v1.health.HealthApiImpl",
                    ChatCompletionApi.class,
                            "jabbyml.api.intern.v1beta.chatcompletion.ChatCompletionApiImpl",
                    SearchApi.class, "jabbyml.api.intern.v1beta.search.SearchApiImpl");
}
