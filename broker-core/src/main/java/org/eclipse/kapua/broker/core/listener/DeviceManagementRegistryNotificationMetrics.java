/*******************************************************************************
 * Copyright (c) 2020 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.broker.core.listener;

class DeviceManagementRegistryNotificationMetrics {

    private DeviceManagementRegistryNotificationMetrics() { }

    static final String METRIC_MODULE_NAME = "device_management_registry";

    static final String METRIC_COMPONENT_NOTIFICATION = "notification";
    static final String METRIC_COMPONENT_DEVICE_LIFE_CYCLE = "deviceLifeCycle";

    static final String METRIC_MESSAGES = "messages";
    static final String METRIC_PROCESS_QUEUE = "process_queue";
    static final String METRIC_COMMUNICATION = "communication";
    static final String METRIC_CONFIGURATION = "configuration";
    static final String METRIC_GENERIC = "generic";

    static final String METRIC_APPS = "apps";
    static final String METRIC_BIRTH = "birth";
    static final String METRIC_DC = "dc";
    static final String METRIC_MISSING = "missing";
    static final String METRIC_UNMATCHED = "unmatched";

    static final String METRIC_ERROR = "error";
    static final String METRIC_COUNT = "count";

}
