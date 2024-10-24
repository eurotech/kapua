/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.shiro.realm;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.handler.shiro.PasswordCredentialTypeHandler;
import org.eclipse.kapua.service.authentication.shiro.UsernamePasswordCredentialsImpl;
import org.eclipse.kapua.service.authentication.shiro.exceptions.MfaRequiredException;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * {@link UsernamePasswordCredentials} based {@link AuthenticatingRealm} implementation.
 *
 * @since 1.0.0
 */
public class UserPassAuthenticatingRealm extends KapuaAuthenticatingRealm {

    private static final Logger LOG = LoggerFactory.getLogger(UserPassAuthenticatingRealm.class);
    // Get Services
    private final UserService userService = KapuaLocator.getInstance().getService(UserService.class);

    /**
     * Realm name.
     */
    public static final String REALM_NAME = "userPassAuthenticatingRealm";

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public UserPassAuthenticatingRealm() {
        setName(REALM_NAME);

        CredentialsMatcher credentialsMather = new UserPassCredentialsMatcher();
        setCredentialsMatcher(credentialsMather);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        // Extract credentials
        UsernamePasswordCredentialsImpl token = (UsernamePasswordCredentialsImpl) authenticationToken;
        String tokenUsername = token.getUsername();
        // Get the associated user by name
        final User user;
        try {
            user = KapuaSecurityUtils.doPrivileged(() -> userService.findByName(tokenUsername));
        } catch (AuthenticationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new ShiroException("Unexpected error while looking for the user!", e);
        }
        // Check user
        checkUser(user);
        // Check account
        Account account = checkAccount(user.getScopeId());
        // Find credentials
        // FIXME: manage multiple credentials and multiple credentials type
        Credential credential;
        try {
            credential = KapuaSecurityUtils.doPrivileged(() -> {
                Credential passwordCredential = credentialService.findByUserId(user.getScopeId(), user.getId(), PasswordCredentialTypeHandler.TYPE).getFirstItem();

                return passwordCredential != null ?
                        credentialService.findWithKey(passwordCredential.getScopeId(), passwordCredential.getId()) :
                        null;
            });
        } catch (AuthenticationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new ShiroException("Unexpected error while looking for the credentials!", e);
        }
        // Check credential
        checkCredential(credential);
        // Get CredentialService config
        Map<String, Object> credentialServiceConfig = getCredentialServiceConfig(credential.getScopeId());
        // Check credential lockout
        checkCredentialLockout(credential, credentialServiceConfig);
        // BuildAuthenticationInfo
        return new LoginAuthenticationInfo(getName(),
                account,
                user,
                credential,
                credentialServiceConfig);
    }

    @Override
    protected void assertCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info)
            throws AuthenticationException {
        LoginAuthenticationInfo kapuaInfo = (LoginAuthenticationInfo) info;

        try {
            super.assertCredentialsMatch(authcToken, info);
        } catch (MfaRequiredException mfaRequiredException) {
            throw mfaRequiredException;
        } catch (AuthenticationException authenticationException) {
            // Increase count of failed attempts
            increaseLockoutPolicyCount(kapuaInfo);

            throw authenticationException;
        }
        // Reset Credential lockout policy after successful login
        resetCredentialLockout((Credential) kapuaInfo.getCredentials());
        // Populate Session with info
        populateSession(SecurityUtils.getSubject(), kapuaInfo);
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof UsernamePasswordCredentialsImpl;
    }
}
