package org.infinispan.loaders.rest.configuration;

import org.infinispan.configuration.cache.AbstractStoreConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.loaders.rest.logging.Log;
import org.infinispan.loaders.rest.metadata.EmbeddedMetadataHelper;
import org.infinispan.loaders.rest.metadata.MetadataHelper;
import org.infinispan.persistence.keymappers.MarshalledValueOrPrimitiveMapper;
import org.infinispan.persistence.keymappers.MarshallingTwoWayKey2StringMapper;
import org.infinispan.util.logging.LogFactory;

/**
 * RestStoreConfigurationBuilder. Configures a {@link org.infinispan.loaders.rest.RestStore}
 *
 * @author Tristan Tarrant
 * @since 6.0
 */
public class RestStoreConfigurationBuilder extends AbstractStoreConfigurationBuilder<RestStoreConfiguration, RestStoreConfigurationBuilder> implements
                                                                                                                                            RestStoreConfigurationChildBuilder<RestStoreConfigurationBuilder> {
   private static final Log log = LogFactory.getLog(RestStoreConfigurationBuilder.class, Log.class);
   private final ConnectionPoolConfigurationBuilder connectionPool;
   private String key2StringMapper = MarshalledValueOrPrimitiveMapper.class.getName();
   private String metadataHelper = EmbeddedMetadataHelper.class.getName();
   private String path = "/";
   private String host;
   private int port = 80;
   private boolean appendCacheNameToPath = false;

   public RestStoreConfigurationBuilder(PersistenceConfigurationBuilder builder) {
      super(builder);
      connectionPool = new ConnectionPoolConfigurationBuilder(this);
   }

   @Override
   public RestStoreConfigurationBuilder self() {
      return this;
   }

   @Override
   public ConnectionPoolConfigurationBuilder connectionPool() {
      return connectionPool;
   }

   @Override
   public RestStoreConfigurationBuilder host(String host) {
      this.host = host;
      return this;
   }

   @Override
   public RestStoreConfigurationBuilder key2StringMapper(String key2StringMapper) {
      this.key2StringMapper = key2StringMapper;
      return this;
   }


   @Override
   public RestStoreConfigurationBuilder key2StringMapper(Class<? extends MarshallingTwoWayKey2StringMapper> klass) {
      this.key2StringMapper = klass.getName();
      return this;
   }

   @Override
   public RestStoreConfigurationBuilder metadataHelper(String metadataHelper) {
      this.metadataHelper = metadataHelper;
      return this;
   }

   @Override
   public RestStoreConfigurationBuilder metadataHelper(Class<? extends MetadataHelper> metadataHelper) {
      this.metadataHelper = metadataHelper.getName();
      return this;
   }

   @Override
   public RestStoreConfigurationBuilder path(String path) {
      this.path = path;
      return this;
   }

   @Override
   public RestStoreConfigurationBuilder port(int port) {
      this.port = port;
      return this;
   }

   @Override
   public RestStoreConfigurationBuilder appendCacheNameToPath(boolean appendCacheNameToPath) {
      this.appendCacheNameToPath = appendCacheNameToPath;
      return this;
   }

   @Override
   public RestStoreConfiguration create() {
      return new RestStoreConfiguration(purgeOnStartup, fetchPersistentState, ignoreModifications, async.create(),
                                             singletonStore.create(), preload, shared, properties, connectionPool.create(),
                                             key2StringMapper, metadataHelper, host, port, path, appendCacheNameToPath);
   }

   @Override
   public RestStoreConfigurationBuilder read(RestStoreConfiguration template) {
      this.connectionPool.read(template.connectionPool());
      this.host = template.host();
      this.port = template.port();
      this.path = template.path();
      this.appendCacheNameToPath = template.appendCacheNameToPath();
      this.key2StringMapper = template.key2StringMapper();
      this.metadataHelper = template.metadataHelper();

      // AbstractStore-specific configuration
      fetchPersistentState = template.fetchPersistentState();
      ignoreModifications = template.ignoreModifications();
      properties = template.properties();
      purgeOnStartup = template.purgeOnStartup();
      shared = template.shared();
      preload = template.preload();
      async.read(template.async());
      singletonStore.read(template.singletonStore());
      return this;
   }

   @Override
   public void validate() {
      this.connectionPool.validate();
      if (host == null) {
         throw log.hostNotSpecified();
      }
      if (!path.endsWith("/")) {
         path = path + "/";
      }
   }
}
