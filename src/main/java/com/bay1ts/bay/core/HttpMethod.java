package com.bay1ts.bay.core;

/**
 * Created by chenu on 2016/10/12.
 */

import java.util.HashMap;

/**
 * @author Per Wendel
 * 2016年10月12日14:04:12
 * bay1ts从spark framework移植至此
 */
// TODO: 2016/10/12  要加上any
public enum HttpMethod {
    get, post, put, patch, delete, head, trace, connect, options, before, after, unsupported;

    private static HashMap<String, HttpMethod> methods = new HashMap<>();

    static {
        for (HttpMethod method : values()) {
            methods.put(method.toString(), method);
        }
    }


    public static HttpMethod get(String methodStr) {
        HttpMethod method = methods.get(methodStr);
        return method != null ? method : unsupported;
    }
}
