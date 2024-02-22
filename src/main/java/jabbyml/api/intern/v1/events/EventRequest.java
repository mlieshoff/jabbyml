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

import jabbyml.essentials.common.Request;

import lombok.Builder;

public class EventRequest extends Request {

    private final String type;
    private final String completionId;
    private final int choiceIndex;
    private final String viewIndex;
    private final int elapsed;

    @Builder
    private EventRequest(
            boolean storeRawResponse,
            String type,
            String completionId,
            int choiceIndex,
            String viewIndex,
            int elapsed) {
        super(storeRawResponse);
        this.type = type;
        this.completionId = completionId;
        this.choiceIndex = choiceIndex;
        this.viewIndex = viewIndex;
        this.elapsed = elapsed;
    }

    public static EventRequestBuilder builder(
            String type, String completionId, int choiceIndex, String viewIndex, int elapsed) {
        return new EventRequestBuilder()
                .type(type)
                .completionId(completionId)
                .choiceIndex(choiceIndex)
                .viewIndex(viewIndex)
                .elapsed(elapsed);
    }
}
