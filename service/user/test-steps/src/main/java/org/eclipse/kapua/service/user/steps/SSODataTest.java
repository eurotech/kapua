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

import org.eclipse.kapua.plugin.sso.openid.SSOData;
import org.eclipse.kapua.service.account.Account;

import java.util.List;

/**
 * Simple {@link SSOData} implementation for tests.
 */
public class SSODataTest implements SSOData {

    private Account account;
    private boolean accountSupportsBrokering;
    private boolean supportsDirectLogin;
    private List<String> companyDomainNames;
    private boolean brokeringApiIssues;

    /**
     * No-arg constructor.
     *
     * @since 2.1.0
     */
    public SSODataTest() {
    }

    // Static factory methods for readability in tests

    /**
     * Creates a {@link SSODataTest} with brokering enabled and the given domains.
     *
     * @param domains the list of allowed company domains
     * @return a {@link SSODataTest} instance
     * @since 2.1.0
     */
    public static SSODataTest brokeringEnabled(List<String> domains) {
        SSODataTest ssoData = new SSODataTest();
        ssoData.setAccountSupportsBrokering(true);
        ssoData.setBrokeringApiConnectionIssues(false);
        ssoData.setCompanyDomainNames(domains);
        return ssoData;
    }

    /**
     * Creates a {@link SSODataTest} with brokering disabled.
     *
     * @return a {@link SSODataTest} instance
     * @since 2.1.0
     */
    public static SSODataTest brokeringDisabled() {
        SSODataTest ssoData = new SSODataTest();
        ssoData.setAccountSupportsBrokering(false);
        ssoData.setBrokeringApiConnectionIssues(false);
        return ssoData;
    }

    /**
     * Creates a {@link SSODataTest} simulating connection issues with the brokering API.
     *
     * @return a {@link SSODataTest} instance
     * @since 2.1.0
     */
    public static SSODataTest withConnectionIssues() {
        SSODataTest ssoData = new SSODataTest();
        ssoData.setAccountSupportsBrokering(false);
        ssoData.setBrokeringApiConnectionIssues(true);
        return ssoData;
    }

    @Override
    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public void setAccountSupportsBrokering(boolean accountSupportsBrokering) {
        this.accountSupportsBrokering = accountSupportsBrokering;
    }

    @Override
    public boolean getAccountSupportsBrokering() {
        return accountSupportsBrokering;
    }

    @Override
    public void setAccountSupportsDirectLogin(boolean supportDirectLogin) {
        this.supportsDirectLogin = supportDirectLogin;
    }

    @Override
    public boolean getAccountSupportsDirectLogin() {
        return supportsDirectLogin;
    }

    @Override
    public void setUriSuffixDirectLogin(String suffix) {
        // no-op: not needed in tests
    }

    @Override
    public String getUriSuffixDirectLogin() {
        return null;
    }

    @Override
    public List<String> getCompanyDomainNames() {
        return companyDomainNames;
    }

    @Override
    public void setCompanyDomainNames(List<String> domains) {
        this.companyDomainNames = domains;
    }

    @Override
    public boolean getBrokeringApiConnectionIssues() {
        return brokeringApiIssues;
    }

    @Override
    public void setBrokeringApiConnectionIssues(boolean brokeringApiConnectionIssues) {
        this.brokeringApiIssues = brokeringApiConnectionIssues;
    }
}