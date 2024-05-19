package com.remiges.rigel.service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(RigelService.class);

	private static final Client client = Client.builder().endpoints(RigelConstant.ETCD_SERVER_ADDRESS).build();

	private static final EtcdPrefixDTO etcdPrefix = new EtcdPrefixDTO();

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
	public static String fetchConfigValue(String version, String appName, String moduleName, String configName,
			String namedConfig, String parameterName) {
		ByteSequence keyPrefix = ByteSequence.from(
				etcdPrefix.getPrefix(version, appName, moduleName, configName, namedConfig), StandardCharsets.UTF_8);
		ByteSequence key = ByteSequence.from(
				etcdPrefix.getKey(version, appName, moduleName, configName, namedConfig, parameterName),
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
		ByteSequence key = getKey(version, appName, moduleName, configName, namedConfig, parameterName);
		ByteSequence val = ByteSequence.from(value, StandardCharsets.UTF_8);

		try {
			KV kvClient = client.getKVClient();
			kvClient.put(key, val, PutOption.newBuilder().build()).get();
			logger.info("Value '{}' stored successfully for key {}", value, key);
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Error storing value '{}' for key {}: {}", value, key, e.getMessage());
		}
	}

	private static ByteSequence getKey(String version, String appName, String moduleName, String configName,
			String namedConfig, String parameterName) {
		String keyString = String.format("/remiges/rigel/%s/%s/%s/%s/%s/%s", version, appName, moduleName, configName,
				namedConfig, parameterName);
		return ByteSequence.from(keyString, StandardCharsets.UTF_8);
	}
}
