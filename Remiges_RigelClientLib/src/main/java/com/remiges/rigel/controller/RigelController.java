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
	@GetMapping("/fetchConfig")
	public String fetchConfigValue(@RequestParam String version, @RequestParam String appName,
			@RequestParam String moduleName, @RequestParam String configName, @RequestParam String namedConfig,
			@RequestParam String parameterName) {
		return RigelService.fetchConfigValue(appName, moduleName, version, configName, namedConfig, parameterName);
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
