/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.job.engine.app.web;

import com.google.common.base.MoreObjects;
import org.eclipse.kapua.commons.jpa.JdbcConnectionUrlResolvers;
import org.eclipse.kapua.commons.liquibase.KapuaLiquibaseClient;
import org.eclipse.kapua.commons.populators.DataPopulatorRunner;
import org.eclipse.kapua.commons.rest.errors.ExceptionConfigurationProvider;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.job.engine.app.web.jaxb.JobEngineJAXBContextProvider;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.guice.GuiceLocatorImpl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.UriConnegFilter;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

public class JobEngineApplication extends ResourceConfig {
    private static final SystemSetting SYSTEM_SETTING = SystemSetting.getInstance();
    private static final Logger LOG = LoggerFactory.getLogger(JobEngineApplication.class);

    public JobEngineApplication() {
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                this.bind(ExceptionConfigurationProviderImpl.class)
                        .to(ExceptionConfigurationProvider.class)
                        .in(Singleton.class);
            }
        });
        packages("org.eclipse.kapua.commons.rest", "org.eclipse.kapua.job.engine.app", "org.eclipse.kapua.app.api.core");

        // Bind media type to resource extension
        HashMap<String, MediaType> mappedMediaTypes = new HashMap<>();
        mappedMediaTypes.put("json", MediaType.APPLICATION_JSON_TYPE);

        property(ServerProperties.MEDIA_TYPE_MAPPINGS, mappedMediaTypes);
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
        register(UriConnegFilter.class);
        register(JacksonFeature.class);

        register(new ContainerLifecycleListener() {

            @Override
            public void onStartup(Container container) {

                if (SYSTEM_SETTING.getBoolean(SystemSettingKey.DB_SCHEMA_UPDATE, false)) {
                    try {
                        String dbUsername = SYSTEM_SETTING.getString(SystemSettingKey.DB_USERNAME);
                        String dbPassword = SYSTEM_SETTING.getString(SystemSettingKey.DB_PASSWORD);
                        String schema = MoreObjects.firstNonNull(
                                SYSTEM_SETTING.getString(SystemSettingKey.DB_SCHEMA_ENV),
                                SYSTEM_SETTING.getString(SystemSettingKey.DB_SCHEMA)
                        );

                        // Loading JDBC Driver
                        String jdbcDriver = SYSTEM_SETTING.getString(SystemSettingKey.DB_JDBC_DRIVER);
                        try {
                            Class.forName(jdbcDriver);
                        } catch (ClassNotFoundException e) {
                            LOG.warn("Could not find jdbc driver: {}. Subsequent DB operation failures may occur...", SYSTEM_SETTING.getString(SystemSettingKey.DB_JDBC_DRIVER));
                        }

                        // Starting Liquibase Client
                        new KapuaLiquibaseClient(JdbcConnectionUrlResolvers.resolveJdbcUrl(), dbUsername, dbPassword, schema).update();
                    } catch (Exception e) {
                        throw new ExceptionInInitializerError(e);
                    }
                } else {
                    LOG.warn("Not updating database schema");
                }

                ServiceLocator serviceLocator = container.getApplicationHandler().getInjectionManager().getInstance(ServiceLocator.class);
                JobEngineJAXBContextProvider provider = serviceLocator.createAndInitialize(JobEngineJAXBContextProvider.class);
                XmlUtil.setContextProvider(provider);
                final KapuaLocator kapuaLocator = KapuaLocator.getInstance();
                //TODO: Move to databaseUpdate
                kapuaLocator.getService(DataPopulatorRunner.class).runPopulators();
                if (kapuaLocator instanceof GuiceLocatorImpl) {
                    GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
                    GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
                    guiceBridge.bridgeGuiceInjector(((GuiceLocatorImpl) kapuaLocator).getInjector());
                }
            }

            @Override
            /**
             * Nothing to do
             */
            public void onReload(Container container) {
            }

            @Override
            /**
             * Nothing to do
             */
            public void onShutdown(Container container) {
            }
        });

    }

}
