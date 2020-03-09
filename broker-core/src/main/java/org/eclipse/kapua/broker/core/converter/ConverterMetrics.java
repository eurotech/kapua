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
package org.eclipse.kapua.broker.core.converter;

class ConverterMetrics {

    private ConverterMetrics() { }

    static final String METRIC_MODULE_NAME = "converter";

    static final String METRIC_COMPONENT_NAME = "kapua";

    static final String METRIC_JMS = "jms";
    static final String METRIC_MESSAGE = "message";
    static final String METRIC_MESSAGES = "messages";
    static final String METRIC_ERROR = "error";
    static final String METRIC_COUNT = "count";
    static final String METRIC_KAPUA_MESSAGE = "kapua_message";
    static final String METRIC_DATA = "data";

    static final String METRIC_APP = "app";
    static final String METRIC_BIRTH = "birth";
    static final String METRIC_DC = "dc";
    static final String METRIC_MISSING = "missing";
    static final String METRIC_NOTIFY = "notify";

}
