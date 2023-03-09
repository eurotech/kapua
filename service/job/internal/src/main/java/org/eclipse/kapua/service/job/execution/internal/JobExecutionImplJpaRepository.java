/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.job.execution.internal;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.KapuaUpdatableEntityJpaRepository;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.job.execution.JobExecution;
import org.eclipse.kapua.service.job.execution.JobExecutionAttributes;
import org.eclipse.kapua.service.job.execution.JobExecutionListResult;
import org.eclipse.kapua.service.job.execution.JobExecutionQuery;
import org.eclipse.kapua.service.job.execution.JobExecutionRepository;
import org.eclipse.kapua.storage.TxContext;

public class JobExecutionImplJpaRepository
        extends KapuaUpdatableEntityJpaRepository<JobExecution, JobExecutionImpl, JobExecutionListResult>
        implements JobExecutionRepository {
    public JobExecutionImplJpaRepository() {
        super(JobExecutionImpl.class, () -> new JobExecutionListResultImpl());
    }

    @Override
    public JobExecution delete(TxContext txContext, KapuaId scopeId, KapuaId jobExecutionId) throws KapuaException {
        final JobExecution toBeDeleted = this.find(txContext, scopeId, jobExecutionId);
        if (toBeDeleted == null) {
            throw new KapuaEntityNotFoundException(JobExecution.TYPE, jobExecutionId);
        }
        return this.delete(txContext, toBeDeleted);
    }

    @Override
    public long countByJobId(TxContext tx, KapuaId scopeId, KapuaId jobId) throws KapuaException {

        JobExecutionQuery jobExecutionQuery = new JobExecutionQueryImpl(scopeId);
        jobExecutionQuery.setPredicate(
                jobExecutionQuery.attributePredicate(JobExecutionAttributes.JOB_ID, jobId)
        );
        return this.count(tx, jobExecutionQuery);
    }
}