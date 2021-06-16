/*******************************************************************************
 * Copyright (c) 2021 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.keystore.message.internal.request;

import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSR;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;

/**
 * {@link DeviceKeystoreCSR} {@link KapuaRequestMessage} implementation.
 *
 * @since 1.5.0
 */
public abstract class KeystoreCsrRequestMessage extends KeystoreRequestMessage<KeystoreCsrRequestMessage> {

    private static final long serialVersionUID = 3593350285989405174L;

    /**
     * Constructor.
     *
     * @since 1.5.0
     */
    public KeystoreCsrRequestMessage() {
        super(KeystoreCsrRequestMessage.class);
    }
}
