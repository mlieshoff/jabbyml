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

import jabbyml.essentials.common.Request;

import lombok.Builder;

import java.util.Map;

public class SearchRequest extends Request {

    private final String q;
    private final int limit;
    private final int offset;

    @Builder
    private SearchRequest(boolean storeRawResponse, String q, int limit, int offset) {
        super(storeRawResponse);
        this.q = q;
        this.limit = limit;
        this.offset = offset;
    }

    public static SearchRequestBuilder builder(String q, int limit, int offset) {
        return new SearchRequestBuilder().q(q).limit(limit).offset(offset);
    }

    @Override
    public Map<String, Object> getRestParameters() {
        Map<String, Object> map = super.getRestParameters();
        map.put("q", q);
        map.put("limit", limit);
        map.put("offset", offset);
        return map;
    }

    @Override
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> map = super.getQueryParameters();
        map.put("q", q);
        map.put("limit", limit);
        map.put("offset", offset);
        return map;
    }
}
