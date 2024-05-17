package com.remiges.rigel.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.remiges.rigel.constant.RigelConstant;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;

import io.etcd.jetcd.options.GetOption;
import java.nio.charset.StandardCharsets;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

/**
 * Service class for interacting with Rigel configurations stored in etcd.
 */


//String value = rigel.get(app,module,version,config,key) // constructor
@Service
public class RigelService {

	// Multimap to store configurations
	private Multimap<String, String> multimap = ArrayListMultimap.create();

	// Etcd client
	private final Client client;

	// Key prefix in etcd
	private final ByteSequence keyPrefix = ByteSequence.from(RigelConstant.ETCD_KEY_PREFIX, StandardCharsets.UTF_8);

	/**
	 * Constructor to initialize the Rigel service.
	 */
	public RigelService() {
		this.client = Client.builder().endpoints(RigelConstant.ETCD_SERVER_ADDRESS).build();
		fetchDataAndStoreInMultimap(); // Fetch data from etcd and store in multimap
	}

	/**
	 * Store a key-value pair in etcd.
	 *
	 * @param key   The key.
	 * @param value The value.
	 */
	public void putValue(String key, String value) {
		ByteSequence keyByteSeq = ByteSequence.from(key, StandardCharsets.UTF_8);
		ByteSequence valueByteSeq = ByteSequence.from(value, StandardCharsets.UTF_8);
		try (Client client = Client.builder().endpoints(RigelConstant.ETCD_SERVER_ADDRESS).build()) {
			KV kvClient = client.getKVClient();
			kvClient.put(keyByteSeq, valueByteSeq).get();
			System.out.println("Value '" + value + "' stored successfully for key " + key);
		} catch (Exception e) {
			System.out.println("Error storing value for key " + key + ": " + e);
		}
	}

	/**
	 * Fetch data from etcd and store in the Multimap.
	 */
	public void fetchDataAndStoreInMultimap() {
		try (client) {
			KV kvClient = client.getKVClient();

			ByteSequence rangeEnd = rangeEndForPrefix(keyPrefix);
			GetOption getOption = GetOption.newBuilder().withRange(rangeEnd).build();

			kvClient.get(keyPrefix, getOption).whenComplete((getResponse, throwable) -> {
				if (throwable != null) {
					throwable.printStackTrace();
					return;
				}
				// Clear existing Multimap before updating
				multimap.clear();

				getResponse.getKvs().forEach(keyValue -> {
					String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
					String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
					String[] keyParts = key.split("/");
					if (keyParts.length >= 2) {
						String nestedKey = keyParts[keyParts.length - 1];
						String parentKey = keyParts[keyParts.length - 2];
						multimap.put(parentKey, nestedKey + "=" + value);
					}
				});

				// Do something with the Multimap
				System.out.println("Updated Multimap: " + multimap);
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fetch a configuration value based on environment and key.
	 *
	 * @param environment The environment.
	 * @param key         The key.
	 * @return The configuration value, or null if not found.
	 */
	public String fetchConfigValue(String environment, String key) { // version, app, module, config, key
		if (multimap.containsKey(environment)) {
			Iterable<String> configValues = multimap.get(environment);
			for (String value : configValues) {
				if (value.contains(key)) {
					String[] parts = value.split("=");
					if (parts.length == 2 && parts[0].equals(key)) {
						return parts[1];
					}
				}
			}
		}
		return null;
	}

	/**
	 * Helper method to compute the range end for a given key prefix.
	 *
	 * @param keyPrefix The key prefix.
	 * @return The range end ByteSequence.
	 */
	private ByteSequence rangeEndForPrefix(ByteSequence keyPrefix) {
		byte[] keyBytes = keyPrefix.getBytes();
		byte[] rangeEnd = new byte[keyBytes.length + 1];
		System.arraycopy(keyBytes, 0, rangeEnd, 0, keyBytes.length);
		rangeEnd[keyBytes.length] = (byte) 0xFF;
		return ByteSequence.from(rangeEnd);
	}
}
