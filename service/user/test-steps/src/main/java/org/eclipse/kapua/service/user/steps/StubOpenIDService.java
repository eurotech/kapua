/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.user.steps;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.plugin.sso.openid.SSOData;
import org.eclipse.kapua.plugin.sso.openid.exception.OpenIDException;
import org.eclipse.kapua.plugin.sso.openid.provider.AbstractOpenIDService;
import org.eclipse.kapua.plugin.sso.openid.provider.setting.OpenIDSetting;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stub OpenIDService for integration and unit tests.
 * Extends AbstractOpenIDService to reuse default implementations.
 * Allows per-account SSOData configuration via {@link #setSSODataForAccount}.
 */
public class StubOpenIDService extends AbstractOpenIDService {

    public static final String STUB_ID = "test";

    private final Map<KapuaId, SSOData> ssoDataByAccount = new ConcurrentHashMap<>();
    private SSOData defaultSSOData;

    public StubOpenIDService(OpenIDSetting openIDSetting) {
        super(openIDSetting);
    }

    public StubOpenIDService() {
        super(Mockito.mock(OpenIDSetting.class));
    }

    // --- Test control methods ---

    public void setSSODataForAccount(KapuaId accountId, SSOData ssoData) {
        ssoDataByAccount.put(accountId, ssoData);
    }

    public void setDefaultSSOData(SSOData ssoData) {
        this.defaultSSOData = ssoData;
    }

    public void reset() {
        ssoDataByAccount.clear();
        defaultSSOData = null;
    }

    // --- OpenIDService overrides ---

    @Override
    public String getId() {
        return STUB_ID;
    }

    @Override
    public SSOData retrieveSSODataForAccount(KapuaId accountId) throws KapuaException {
        if (ssoDataByAccount.containsKey(accountId)) {
            return ssoDataByAccount.get(accountId);
        }
        return defaultSSOData;
    }

    @Override
    public boolean isBrokeringEnabledForAccount(KapuaId accountId) throws KapuaException {
        SSOData ssoData = retrieveSSODataForAccount(accountId);
        return ssoData != null && ssoData.getAccountSupportsBrokering();
    }

    // --- Abstract methods — not needed in tests ---

    @Override
    protected String getAuthUri() throws OpenIDException {
        throw new UnsupportedOperationException("Not implemented in stub");
    }

    @Override
    protected String getLogoutUri() throws OpenIDException {
        throw new UnsupportedOperationException("Not implemented in stub");
    }

    @Override
    protected String getTokenUri() throws OpenIDException {
        throw new UnsupportedOperationException("Not implemented in stub");
    }

    @Override
    protected String getUserInfoUri() throws OpenIDException {
        throw new UnsupportedOperationException("Not implemented in stub");
    }
}