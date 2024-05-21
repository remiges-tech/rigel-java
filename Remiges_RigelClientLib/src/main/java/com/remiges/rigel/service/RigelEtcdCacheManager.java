package com.remiges.rigel.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.remiges.rigel.constant.RigelConstant;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.options.WatchOption;

/**
 * Manages the caching of configuration values retrieved from Etcd.
 */
@Component
public class RigelEtcdCacheManager {

	private static final Logger logger = LoggerFactory.getLogger(RigelEtcdCacheManager.class);
	private static final Map<String, String> etcdCacheStorage = Collections.synchronizedMap(new TreeMap<>());

	/**
	 * Constructs a new RigelEtcdCacheManager and starts watching for changes in
	 * Etcd.
	 */
	public RigelEtcdCacheManager() {
		watchChanges();
	}

	/**
	 * Watches for changes in Etcd and updates the cache accordingly.
	 */
	private void watchChanges() {
		try {
			Client.builder().endpoints(RigelConstant.ETCD_SERVER_ADDRESS).build().getWatchClient().watch(
					ByteSequence.from(RigelConstant.ETCD_PREFIX.getBytes(StandardCharsets.UTF_8)),
					WatchOption.newBuilder()
							.withPrefix(ByteSequence.from(RigelConstant.ETCD_PREFIX.getBytes(StandardCharsets.UTF_8)))
							.build(),
					(Consumer<io.etcd.jetcd.watch.WatchResponse>) response -> response.getEvents().forEach(event -> {
						String key = event.getKeyValue().getKey().toString(StandardCharsets.UTF_8);
						String value = event.getKeyValue().getValue().toString(StandardCharsets.UTF_8);
						switch (event.getEventType()) {
						case PUT:
							logger.info("*PUT event received for key: {}, value: {}", key, value);
							etcdCacheStorage.put(key, value);
							break;
						case DELETE:
							logger.info("DELETE event received for key: {}", key);
							etcdCacheStorage.remove(key);
							break;
						default:
							logger.warn("Unsupported event type: {}", event.getEventType());
						}
					}));
		} catch (Exception e) {
			logger.error("Error watching for changes in Etcd: {}", e.getMessage(), e);
		}
	}

	/**
	 * Retrieves the value associated with the specified key from the Etcd cache.
	 *
	 * @param key The key for which to retrieve the value.
	 * @return The value associated with the key, or null if the key is not found.
	 */
	public static String getEtcdValue(String key) {
		return etcdCacheStorage.get(key);
	}
}
