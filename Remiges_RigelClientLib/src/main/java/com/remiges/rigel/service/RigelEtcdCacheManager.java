package com.remiges.rigel.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.remiges.rigel.constant.RigelConstant;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;

/**
 * Manages the caching of configuration values retrieved from Etcd.
 */
@Component
public class RigelEtcdCacheManager {

    private static final Logger logger = LoggerFactory.getLogger(RigelEtcdCacheManager.class);
    private static final Map<String, String> etcdCacheStorage = Collections.synchronizedMap(new TreeMap<>());

    /**
     * Constructs a new RigelEtcdCacheManager and starts watching for changes in Etcd.
     */
    public RigelEtcdCacheManager() {
        watchChanges();
    }

    /**
     * Watches for changes in Etcd and updates the cache accordingly.
     */
    private void watchChanges() {
        try {
            Client client = Client.builder().endpoints(RigelConstant.ETCD_SERVER_ADDRESS).build();

            // Convert the prefix String to ByteSequence using UTF-8 charset
            ByteSequence byteSequence = ByteSequence.from(RigelConstant.ETCD_PREFIX.getBytes(StandardCharsets.UTF_8));

            // Create WatchOption to watch for events on keys with the prefix
            WatchOption watchOption = WatchOption.newBuilder().withPrefix(byteSequence).build();

            // Start watching for changes
            client.getWatchClient().watch(byteSequence, watchOption, response -> {
                for (WatchEvent event : response.getEvents()) {
                    String key = event.getKeyValue().getKey().toString(StandardCharsets.UTF_8);

                    // Get the value from the WatchEvent
                    String value = event.getKeyValue().getValue().toString(StandardCharsets.UTF_8);

                    // Handle watch events
                    logger.info("Watch event received: {}", event);
                    logger.info("Watch event received Key: {}", key);
                    logger.info("Watch event received Value: {}", value);
                    etcdCacheStorage.put(key, value);
                }
            });
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
