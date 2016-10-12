package com.bay1ts.bay.route.match;

/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.bay1ts.bay.Action;

/**
 * @author Per Wendel
 */
public class RouteMatch {

    private Action action;
    private String matchUri;
    private String requestURI;
    private String acceptType;

    public RouteMatch(Action action, String matchUri, String requestUri, String acceptType) {
        super();
        this.action = action;
        this.matchUri = matchUri;
        this.requestURI = requestUri;
        this.acceptType = acceptType;
    }

    public Action getAction() {
        return action;
    }

    public String getMatchUri() {
        return matchUri;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getAcceptType() {
        return acceptType;
    }
}
