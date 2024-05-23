package com.remiges.rigel.service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.remiges.rigel.constant.RigelConstant;
import com.remiges.rigel.dto.EtcdPrefixDTO;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

/**
 * Service class for interacting with Rigel configurations stored in etcd.
 */
@Service
public class RigelService {

	@Autowired
	private static RigelEtcdCacheManager etcdCacheStorage;

	private static final Logger logger = LoggerFactory.getLogger(RigelService.class);

	private static final EtcdPrefixDTO etcdPrefix = new EtcdPrefixDTO();

	/**
	 * Retrieves a configuration value from the Etcd cache based on the specified
	 * parameters.
	 *
	 * @param appName       The name of the application.
	 * @param moduleName    The name of the module.
	 * @param version       The version of the configuration.
	 * @param configName    The name of the configuration.
	 * @param namedConfig   The named configuration.
	 * @param parameterName The name of the parameter.
	 * @return The configuration value retrieved from the Etcd cache.
	 */
	public static String get(String appName, String moduleName, String version, String configName,
			String namedConfig, String parameterName) {
		// Generate the key using the provided parameters
		ByteSequence key = ByteSequence.from(
				etcdPrefix.getKey(appName, moduleName, version, configName, namedConfig, parameterName),
				StandardCharsets.UTF_8);

		// Retrieve the configuration value from the Etcd cache based on the generated
		// key
		return etcdCacheStorage.getEtcdValue(key.toString().trim());
	}
}
