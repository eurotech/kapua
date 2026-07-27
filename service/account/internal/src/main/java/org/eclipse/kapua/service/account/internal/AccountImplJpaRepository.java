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
package org.eclipse.kapua.service.account.internal;

import org.eclipse.kapua.commons.jpa.JpaAwareTxContext;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaNamedEntityJpaRepository;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountRepository;
import org.eclipse.kapua.storage.TxContext;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AccountImplJpaRepository
        extends KapuaNamedEntityJpaRepository<Account, AccountImpl, AccountListResult>
        implements AccountRepository {

    public AccountImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(AccountImpl.class, Account.TYPE, () -> new AccountListResultImpl(), jpaRepoConfig);
    }

    @Override
    public AccountListResult findChildAccountsRecursive(TxContext tx, String parentAccountPath) {
        final EntityManager em = JpaAwareTxContext.extractEntityManager(tx);
        TypedQuery<Account> q = em.createNamedQuery("Account.findChildAccountsRecursive", Account.class);
        q.setParameter("parentAccountPath", "\\" + parentAccountPath + "/%");
        final AccountListResult result = listSupplier.get();
        result.addItems(q.getResultList());
        return result;
    }

    //fetch parent accounts (the chain of accounts) from the given account path, excluding the account itself.
    //The ordering of the returned list is from the direct parent to the root account.
    @Override
    public AccountListResult findParentAccounts(TxContext tx, String parentAccountPath) {
        String[] pathParts = parentAccountPath.split("/");

        List<String> parentIds = Arrays.stream(pathParts)
                .filter(part -> !part.trim().isEmpty())
                .map(part -> part.replace("\\", ""))
                .collect(Collectors.toList());

        if (!parentIds.isEmpty()) {
            parentIds.remove(parentIds.size() - 1); // remove self
        }

        final AccountListResult result = listSupplier.get();

        // Iterate from last to first to get direct father first
        for (int i = parentIds.size() - 1; i >= 0; i--) {
            KapuaId parentId = new KapuaEid(new BigInteger(parentIds.get(i)));
            find(tx, KapuaId.ANY, parentId).ifPresent(result::addItem);
        }

        return result;
    }

}
