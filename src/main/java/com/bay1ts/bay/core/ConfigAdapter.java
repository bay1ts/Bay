package com.bay1ts.bay.core;


import com.bay1ts.bay.utils.Utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public abstract class ConfigAdapter {

    private String ownerName;

    private Map<String, String> initParameters;

    public ConfigAdapter(String ownerName) {
        this.ownerName = ownerName;
    }

    public void addInitParameter(String name, String value) {
        if (this.initParameters == null)
            this.initParameters = new HashMap<String, String>();

        this.initParameters.put(name, value);
    }

    public String getInitParameter(String name) {
        if (this.initParameters == null)
            return null;

        return this.initParameters.get(name);
    }

    @SuppressWarnings("unchecked")
    public Enumeration getInitParameterNames() {
        return Utils.enumerationFromKeys(this.initParameters);
    }

    String getOwnerName() {
        return ownerName;
    }

}
