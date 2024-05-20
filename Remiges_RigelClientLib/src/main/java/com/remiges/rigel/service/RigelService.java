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

	private static final Client client = Client.builder().endpoints(RigelConstant.ETCD_SERVER_ADDRESS).build();

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
	public static String fetchConfigFromEtcdCache(String appName, String moduleName, String version, String configName,
			String namedConfig, String parameterName) {
		// Generate the key using the provided parameters
		ByteSequence key = ByteSequence.from(
				etcdPrefix.getKey(appName, moduleName, version, configName, namedConfig, parameterName),
				StandardCharsets.UTF_8);

		// Retrieve the configuration value from the Etcd cache based on the generated
		// key
		return etcdCacheStorage.getEtcdValue(key.toString().trim());
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
	public static String fetchConfigFromEtcd(String appName, String moduleName, String version, String configName,
			String namedConfig, String parameterName) {
		ByteSequence keyPrefix = ByteSequence.from(
				etcdPrefix.getPrefix(appName, moduleName, version, configName, namedConfig), StandardCharsets.UTF_8);
		ByteSequence key = ByteSequence.from(
				etcdPrefix.getKey(appName, moduleName, version, configName, namedConfig, parameterName),
				StandardCharsets.UTF_8);
		try {
			KV kvClient = client.getKVClient();
			GetOption getOption = GetOption.newBuilder().withPrefix(keyPrefix).build();
			return kvClient.get(key, getOption).get().getKvs().stream().findFirst()
					.map(kv -> kv.getValue().toString(StandardCharsets.UTF_8)).orElse(null);
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Error fetching configuration value from etcd: {}", e.getMessage());
			return null;
		}
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
	public static void putValue(String version, String appName, String moduleName, String configName,
			String namedConfig, String parameterName, String value) {
		ByteSequence key = getKey(appName, moduleName, version, configName, namedConfig, parameterName);
		ByteSequence val = ByteSequence.from(value, StandardCharsets.UTF_8);
		try {
			KV kvClient = client.getKVClient();
			kvClient.put(key, val, PutOption.newBuilder().build()).get();
			logger.info("Value '{}' stored successfully for key {}", value, key);
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Error storing value '{}' for key {}: {}", value, key, e.getMessage());
		}
	}

	private static ByteSequence getKey(String appName, String moduleName, String version, String configName,
			String namedConfig, String parameterName) {
		String keyString = String.format("/remiges/rigel/%s/%s/%s/%s/%s/%s", appName, moduleName, version, configName,
				namedConfig, parameterName);
		return ByteSequence.from(keyString, StandardCharsets.UTF_8);
	}
}
