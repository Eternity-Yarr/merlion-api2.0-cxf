package org.yarr.merlionapi2.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.model.Config;

import java.io.File;

@Service
public class ConfigService
{
    private final Config config;
    public ConfigService() throws Exception {
        String configFile = System.getProperty("config");
        if (configFile == null)
            configFile = "./config.json";
        ObjectMapper om = new ObjectMapper();
        this.config = om.readValue(new File(configFile), Config.class);
    }

    public String merlionLogin() {
        return config.apiLogin;
    }

    public String merlionPassword() {
        return config.apiPassword;
    }

    public String mysqlUri() {
        return config.mysqlUri;
    }

    public String mysqlUser() {
        return config.mysqlUser;
    }

    public String mysqlPassword() {
        return config.mysqlPassword;
    }

    public int merlionSupplierId() {
        return config.merlionSupplierId;
    }

    public int merlionStoreId() {
        return config.merlionStoreId;
    }

    public int valudeAddedPercent() {
        return config.valudeAddedPercent;
    }
}
