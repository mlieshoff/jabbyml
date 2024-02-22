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

import jabbyml.essentials.api.ApiContext;
import jabbyml.essentials.api.BaseApi;

import java.util.Map;
import java.util.concurrent.Future;

class ChatCompletionApiImpl extends BaseApi implements ChatCompletionApi {

    ChatCompletionApiImpl(ApiContext apiContext) {
        super(apiContext);
    }

    @Override
    public Future<ChatCompletionResponse> get(ChatCompletionRequest chatCompletionRequest) {
        return post(
                "/v1beta/chat/completions",
                chatCompletionRequest,
                ChatCompletionResponse.class,
                Map.of("Content-Type", "application/json", "accept", "text/event-stream"));
    }
}
