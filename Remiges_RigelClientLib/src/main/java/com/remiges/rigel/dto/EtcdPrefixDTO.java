package com.remiges.rigel.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.remiges.rigel.constant.RigelConstant;

/**
 * DTO class representing the prefix configuration for etcd keys.
 */
@Component
public class EtcdPrefixDTO {

    private static final Logger logger = LoggerFactory.getLogger(EtcdPrefixDTO.class);

    private static final String ETCD_KEY_PREFIX = "/remiges/rigel/";
    private static final String APP_NAME = "NDMLKRA";
    private static final String MODULE_NAME = "KYCEnquiry";
    private static final int VERSION_NUMBER = 1;
    private static final String CONFIG = "config";
    private static final String NAMED_CONFIG = "uat";
    private static final int PAN_INPUT_LIMIT = 10;

    /**
     * Gets the etcd key prefix.
     *
     * @return The etcd key prefix.
     */
    public String getEtcdKeyPrefix() {
        return ETCD_KEY_PREFIX;
    }

    /**
     * Gets the application name.
     *
     * @return The application name.
     */
    public String getAppName() {
        return APP_NAME;
    }

    /**
     * Gets the module name.
     *
     * @return The module name.
     */
    public String getModuleName() {
        return MODULE_NAME;
    }

    /**
     * Gets the version number.
     *
     * @return The version number.
     */
    public String getVersionNumber() {
        return String.valueOf(VERSION_NUMBER);
    }

    /**
     * Gets the configuration.
     *
     * @return The configuration.
     */
    public String getConfig() {
        return CONFIG;
    }

    /**
     * Gets the named configuration.
     *
     * @return The named configuration.
     */
    public String getNamedConfig() {
        return NAMED_CONFIG;
    }

    /**
     * Gets the pan input limit.
     *
     * @return The pan input limit.
     */
    public int getPanInputLimit() {
        return PAN_INPUT_LIMIT;
    }

    /**
     * Gets the prefix for etcd keys.
     *
     * @param version     The version number.
     * @param appName     The application name.
     * @param moduleName  The module name.
     * @param configName  The configuration name.
     * @param namedConfig The named configuration.
     * @return The prefix for etcd keys.
     */
    public String getPrefix(String appName, String moduleName, String version, String configName, String namedConfig) {
        return ETCD_KEY_PREFIX + appName + "/" + moduleName + "/" + version + "/" + configName + "/" + namedConfig + "/";
    }

    /**
     * Gets the key for etcd based on parameters.
     *
     * @param version       The version number.
     * @param appName       The application name.
     * @param moduleName    The module name.
     * @param configName    The configuration name.
     * @param namedConfig   The named configuration.
     * @param parameterName The parameter name.
     * @return The key for etcd.
     */
    public String getKey(String appName, String moduleName, String version, String configName, String namedConfig,
            String parameterName) {
        return getPrefix(appName, moduleName, version, configName, namedConfig) + parameterName;
    }
}
