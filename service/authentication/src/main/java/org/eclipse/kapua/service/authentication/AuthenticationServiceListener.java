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
package org.eclipse.kapua.service.authentication;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.spi.UriEndpoint;
import org.eclipse.kapua.client.security.bean.EntityRequest;
import org.eclipse.kapua.client.security.bean.EntityResponse;
import org.eclipse.kapua.client.security.bean.MessageConstants;
import org.eclipse.kapua.client.security.bean.Request;
import org.eclipse.kapua.commons.metric.MetricServiceFactory;
import org.eclipse.kapua.commons.metric.MetricsLabel;
import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.client.security.bean.AuthRequest;
import org.eclipse.kapua.client.security.bean.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@UriEndpoint(title = "authentication service listener", syntax = "bean:authenticationServiceListener", scheme = "bean")
public class AuthenticationServiceListener {

    protected static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceListener.class);

    private static final String REQUEST = "request";

    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectWriter writer = mapper.writer();

    private Counter metricLoginCount;
    private Counter metricLoginRequestCount;
    private Counter metricLogoutCount;
    private Counter metricLogoutRequestCount;
    private Counter metricGetAccountCount;
    private Counter metricGetAccountRequestCount;

    @Inject
    private AuthenticationServiceBackEndCall authenticationServiceBackEndCall;

    public AuthenticationServiceListener() {
        MetricsService metricService = MetricServiceFactory.getInstance();
        metricLoginCount = metricService.getCounter(MetricLabel.SERVICE_AUTHENTICATION, MetricLabel.LOGIN, MetricsLabel.SUCCESS);
        metricLoginRequestCount = metricService.getCounter(MetricLabel.SERVICE_AUTHENTICATION, MetricLabel.LOGIN, REQUEST);
        metricLogoutCount = metricService.getCounter(MetricLabel.SERVICE_AUTHENTICATION, MetricLabel.LOGOUT, MetricsLabel.SUCCESS);
        metricLogoutRequestCount = metricService.getCounter(MetricLabel.SERVICE_AUTHENTICATION, MetricLabel.LOGOUT, REQUEST);
        metricGetAccountCount = metricService.getCounter(MetricLabel.SERVICE_AUTHENTICATION, MetricLabel.GET_ACCOUNT, MetricsLabel.SUCCESS);
        metricGetAccountRequestCount = metricService.getCounter(MetricLabel.SERVICE_AUTHENTICATION, MetricLabel.GET_ACCOUNT, REQUEST);
    }

    public void brokerConnect(Exchange exchange, AuthRequest authRequest) throws JsonProcessingException, JMSException {
        metricLoginRequestCount.inc();
        logRequest(exchange, authRequest);
        AuthResponse authResponse = authenticationServiceBackEndCall.brokerConnect(authRequest);
        updateMessage(exchange, authRequest, authResponse);
        metricLoginCount.inc();
    }

    public void brokerDisconnect(Exchange exchange, AuthRequest authRequest) throws JsonProcessingException, JMSException {
        metricLogoutRequestCount.inc();
        logRequest(exchange, authRequest);
        AuthResponse authResponse = authenticationServiceBackEndCall.brokerDisconnect(authRequest);
        updateMessage(exchange, authRequest, authResponse);
        metricLogoutCount.inc();
    }

    public void getEntity(Exchange exchange, EntityRequest accountRequest) throws JsonProcessingException, JMSException {
        metricGetAccountRequestCount.inc();
        logRequest(exchange, accountRequest);
        EntityResponse accountResponse = authenticationServiceBackEndCall.getEntity(accountRequest);
        updateMessage(exchange, accountRequest, accountResponse);
        metricGetAccountCount.inc();
    }

    public void updateMessage(Exchange exchange, AuthRequest authRequest, AuthResponse authResponse) throws JMSException, JsonProcessingException {
        Message message = exchange.getIn();
        String textPayload = null;
        if (authResponse!=null) {
            textPayload = writer.writeValueAsString(authResponse);
            message.setBody(textPayload, String.class);
        }
        message.setHeader(MessageConstants.HEADER_CLUSTER_NAME, authRequest.getClusterName());
        message.setHeader(MessageConstants.HEADER_REQUESTER, authRequest.getRequester());
        message.setHeader(MessageConstants.HEADER_REQUEST_ID, authRequest.getRequestId());
        message.setHeader(MessageConstants.HEADER_ACTION, authRequest.getAction());
        message.setHeader(MessageConstants.HEADER_USERNAME, authRequest.getUsername());
        message.setHeader(MessageConstants.HEADER_CLIENT_ID, authRequest.getClientId());
        message.setHeader(MessageConstants.HEADER_CLIENT_IP, authRequest.getClientIp());
        message.setHeader(MessageConstants.HEADER_CONNECTION_ID, authRequest.getConnectionId());
        message.setHeader(MessageConstants.HEADER_RESULT_CODE, authResponse.getResultCode());
        message.setHeader(MessageConstants.HEADER_ERROR_CODE, authResponse.getErrorCode());
    }

    public void updateMessage(Exchange exchange, EntityRequest entityRequest, EntityResponse entityResponse) throws JMSException, JsonProcessingException {
        Message message = exchange.getIn();
        String textPayload = null;
        if (entityResponse!=null) {
            textPayload = writer.writeValueAsString(entityResponse);
            message.setBody(textPayload, String.class);
        }
        message.setHeader(MessageConstants.HEADER_CLUSTER_NAME, entityRequest.getClusterName());
        message.setHeader(MessageConstants.HEADER_REQUESTER, entityRequest.getRequester());
        message.setHeader(MessageConstants.HEADER_REQUEST_ID, entityRequest.getRequestId());
        message.setHeader(MessageConstants.HEADER_ACTION, entityRequest.getAction());
        message.setHeader(MessageConstants.HEADER_RESULT_CODE, entityResponse.getResultCode());
        message.setHeader(MessageConstants.HEADER_ERROR_CODE, entityResponse.getErrorCode());
    }

    private void logRequest(Exchange exchange, Request request) {
        logger.info("Message id: {} - request id: {} - action: {} - requester: {}",
            exchange.getIn().getMessageId(),
            request.getRequestId(), request.getAction(), request.getRequester());
    }
}