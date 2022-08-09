/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.assetstore.api;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.assetstore.config.api.DeviceAssetStoreConfiguration;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class DeviceAssetStoreXmlFactory {

    private final DeviceAssetStoreFactory factory = KapuaLocator.getInstance().getFactory(DeviceAssetStoreFactory.class);

    public DeviceAssetStoreConfiguration newDeviceAssetStoreConfiguration() {
        return factory.newDeviceAssetStoreConfiguration();
    }
}
