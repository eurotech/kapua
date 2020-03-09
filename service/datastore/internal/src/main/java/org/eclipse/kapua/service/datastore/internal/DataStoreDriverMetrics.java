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
package org.eclipse.kapua.service.datastore.internal;

class DataStoreDriverMetrics {

    private DataStoreDriverMetrics() { }

    static final String METRIC_MODULE_NAME = "datastore";

    static final String METRIC_COMPONENT_NAME = "driver";

    static final String METRIC_STORE = "store";
    static final String METRIC_QUEUE = "queue";
    static final String METRIC_COMMUNICATION = "communication";
    static final String METRIC_CONFIGURATION = "configuration";
    static final String METRIC_GENERIC = "generic";
    static final String METRIC_MESSAGES = "messages";
    static final String METRIC_VALIDATION = "validation";
    static final String METRIC_TIME = "time";
    static final String METRIC_S = "s";
    static final String METRIC_ALREADY_IN_THE_DATASTORE = "already_in_the_datastore";

    static final String METRIC_ERROR = "error";
    static final String METRIC_COUNT = "count";

}
