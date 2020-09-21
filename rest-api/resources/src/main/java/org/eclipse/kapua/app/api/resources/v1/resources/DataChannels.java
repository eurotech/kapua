/*******************************************************************************
 * Copyright (c) 2016, 2020 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.app.api.resources.v1.resources;

import com.google.common.base.Strings;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.resources.v1.resources.model.CountResult;
import org.eclipse.kapua.app.api.resources.v1.resources.model.ScopeId;
import org.eclipse.kapua.app.api.resources.v1.resources.model.StorableEntityId;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.datastore.ChannelInfoRegistryService;
import org.eclipse.kapua.service.datastore.DatastoreObjectFactory;
import org.eclipse.kapua.service.datastore.internal.mediator.ChannelInfoField;
import org.eclipse.kapua.service.datastore.internal.model.query.ChannelMatchPredicateImpl;
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ChannelInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.ChannelInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.ChannelMatchPredicate;
import org.eclipse.kapua.service.datastore.model.query.DatastorePredicateFactory;
import org.eclipse.kapua.service.storable.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.TermPredicate;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("{scopeId}/data/channels")
public class DataChannels extends AbstractKapuaResource {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final ChannelInfoRegistryService CHANNEL_INFO_REGISTRY_SERVICE = LOCATOR.getService(ChannelInfoRegistryService.class);
    private static final DatastoreObjectFactory DATASTORE_OBJECT_FACTORY = LOCATOR.getFactory(DatastoreObjectFactory.class);
    private static final DatastorePredicateFactory DATASTORE_PREDICATE_FACTORY = LOCATOR.getFactory(DatastorePredicateFactory.class);

    /**
     * Gets the {@link ChannelInfo} list in the scope.
     *
     * @param scopeId  The {@link ScopeId} in which to search results.
     * @param clientId The client id to filter results.
     * @param name     The channel name to filter results. It allows '#' wildcard in last channel level
     * @param offset   The result set offset.
     * @param limit    The result set limit.
     * @return The {@link ChannelInfoListResult} of all the channelInfos associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ChannelInfoListResult simpleQuery( //
                                              @PathParam("scopeId") ScopeId scopeId,//
                                              @QueryParam("clientId") String clientId, //
                                              @QueryParam("name") String name, //
                                              @QueryParam("offset") @DefaultValue("0") int offset,//
                                              @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        AndPredicate andPredicate = DATASTORE_PREDICATE_FACTORY.newAndPredicate();
        if (!Strings.isNullOrEmpty(clientId)) {
            TermPredicate clientIdPredicate = DATASTORE_PREDICATE_FACTORY.newTermPredicate(ChannelInfoField.CLIENT_ID, clientId);
            andPredicate.getPredicates().add(clientIdPredicate);
        }

        if (!Strings.isNullOrEmpty(name)) {
            ChannelMatchPredicate channelPredicate = new ChannelMatchPredicateImpl(name);
            andPredicate.getPredicates().add(channelPredicate);
        }

        ChannelInfoQuery query = DATASTORE_OBJECT_FACTORY.newChannelInfoQuery(scopeId);
        query.setPredicate(andPredicate);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link ChannelInfoQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link ChannelInfoQuery} to used to filter results.
     * @return The {@link ChannelInfoListResult} of all the result matching the given {@link ChannelInfoQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    //
    public ChannelInfoListResult query( //
                                        @PathParam("scopeId") ScopeId scopeId, //
                                        ChannelInfoQuery query) throws KapuaException {
        query.setScopeId(scopeId);
        query.addFetchAttributes(ChannelInfoField.TIMESTAMP.field());
        return CHANNEL_INFO_REGISTRY_SERVICE.query(query);
    }

    /**
     * Counts the results with the given {@link ChannelInfoQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link ChannelInfoQuery} to used to filter results.
     * @return The count of all the result matching the given {@link ChannelInfoQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})

    public CountResult count( //
                              @PathParam("scopeId") ScopeId scopeId, //
                              ChannelInfoQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(CHANNEL_INFO_REGISTRY_SERVICE.count(query));
    }

    /**
     * Returns the ChannelInfo specified by the "channelInfoId" path parameter.
     *
     * @param channelInfoId The id of the requested ChannelInfo.
     * @return The requested ChannelInfo object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{channelInfoId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})

    public ChannelInfo find( //
                             @PathParam("scopeId") ScopeId scopeId,
                             @PathParam("channelInfoId") StorableEntityId channelInfoId) throws KapuaException {
        ChannelInfo channelInfo = CHANNEL_INFO_REGISTRY_SERVICE.find(scopeId, channelInfoId);

        return returnNotNullEntity(channelInfo);
    }
}
