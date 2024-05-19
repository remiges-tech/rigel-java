package com.remiges.rigel.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.remiges.rigel.constant.RigelConstant;

/**
 * DTO class representing the prefix configuration for etcd keys.
 */
@Component
public class EtcdPrefixDTO {

	private static final Logger logger = LoggerFactory.getLogger(EtcdPrefixDTO.class);

	@Value("${remiges.rigel.etcdKeyPrefix}")
	private String etcdKeyPrefix;

	@Value("${remiges.rigel.AppName}")
	private String appName;

	@Value("${remiges.rigel.ModuleName}")
	private String moduleName;

	@Value("${remiges.rigel.VersionNumber}")
	private String versionNumber;

	@Value("${remiges.rigel.config}")
	private String config;

	@Value("${remiges.rigel.config.NamedConfig}")
	private String namedConfig;

	@Value("${remiges.rigel.config.paninputlimit}")
	private int panInputLimit;

	/**
	 * Gets the etcd key prefix.
	 *
	 * @return The etcd key prefix.
	 */
	public String getEtcdKeyPrefix() {
		return etcdKeyPrefix;
	}

	/**
	 * Gets the application name.
	 *
	 * @return The application name.
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * Gets the module name.
	 *
	 * @return The module name.
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * Gets the version number.
	 *
	 * @return The version number.
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * Gets the configuration.
	 *
	 * @return The configuration.
	 */
	public String getConfig() {
		return config;
	}

	/**
	 * Gets the named configuration.
	 *
	 * @return The named configuration.
	 */
	public String getNamedConfig() {
		return namedConfig;
	}

	/**
	 * Gets the pan input limit.
	 *
	 * @return The pan input limit.
	 */
	public int getPanInputLimit() {
		return panInputLimit;
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
	public String getPrefix(String version, String appName, String moduleName, String configName, String namedConfig) {
		return RigelConstant.ETCD_PREFIX + appName + "/" + moduleName + "/" + version + "/" + configName + "/" + namedConfig
				+ "/";
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
	public String getKey(String version, String appName, String moduleName, String configName, String namedConfig,
			String parameterName) {
		return RigelConstant.ETCD_PREFIX + getPrefix(version, appName, moduleName, configName, namedConfig) + parameterName;
	}
}
