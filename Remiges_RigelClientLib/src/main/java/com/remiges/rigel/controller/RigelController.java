package com.remiges.rigel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.remiges.rigel.service.RigelService;

/**
 * Controller class for handling Rigel configurations.
 */
@RestController
public class RigelController {

	private static final Logger logger = LoggerFactory.getLogger(RigelController.class);

	/**
	 * Retrieves a configuration value from the Etcd cache based on the specified
	 * parameters.
	 *
	 * @param version       The version of the configuration.
	 * @param appName       The name of the application.
	 * @param moduleName    The name of the module.
	 * @param configName    The name of the configuration.
	 * @param namedConfig   The named configuration.
	 * @param parameterName The name of the parameter.
	 * @return The configuration value retrieved from the Etcd cache.
	 */
	@GetMapping("/fetchConfigFromCache")
	public String fetchConfigFromCache(@RequestParam String version, @RequestParam String appName,
			@RequestParam String moduleName, @RequestParam String configName, @RequestParam String namedConfig,
			@RequestParam String parameterName) {
		return RigelService.fetchConfigFromEtcdCache(appName, moduleName, version, configName, namedConfig,
				parameterName);
	}

	/**
	 * Fetches a configuration value from etcd based on the provided parameters.
	 *
	 * @param version       The version number.
	 * @param appName       The application name.
	 * @param moduleName    The module name.
	 * @param configName    The configuration name.
	 * @param namedConfig   The named configuration.
	 * @param parameterName The parameter name.
	 * @return The configuration value, or null if not found.
	 */
	@GetMapping("/fetchConfigFromEtcd")
	public String fetchConfigValue(@RequestParam String version, @RequestParam String appName,
			@RequestParam String moduleName, @RequestParam String configName, @RequestParam String namedConfig,
			@RequestParam String parameterName) {
		return RigelService.fetchConfigFromEtcd(appName, moduleName, version, configName, namedConfig, parameterName);
	}

	/**
	 * Stores a configuration value in etcd based on the provided parameters.
	 *
	 * @param version       The version number.
	 * @param appName       The application name.
	 * @param moduleName    The module name.
	 * @param configName    The configuration name.
	 * @param namedConfig   The named configuration.
	 * @param parameterName The parameter name.
	 * @param value         The value to store.
	 */
	@PutMapping("/putConfig")
	public String putConfigValue(@RequestParam String version, @RequestParam String appName,
			@RequestParam String moduleName, @RequestParam String configName, @RequestParam String namedConfig,
			@RequestParam String parameterName, @RequestBody String value) {
		RigelService.putValue(version, appName, moduleName, configName, namedConfig, parameterName, value);
		logger.info("Value '{}' stored successfully for parameter '{}'", value, parameterName);
		return "Value added successfully";
	}
}
