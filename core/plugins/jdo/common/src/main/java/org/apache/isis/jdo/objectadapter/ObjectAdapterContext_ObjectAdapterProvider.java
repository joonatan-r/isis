/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.jdo.objectadapter;

import org.apache.isis.metamodel.adapter.ObjectAdapter;
import org.apache.isis.metamodel.adapter.ObjectAdapterProvider;
import org.apache.isis.metamodel.adapter.oid.factory.OidFactory;
import org.apache.isis.metamodel.spec.ManagedObject;
import org.apache.isis.metamodel.spec.ObjectSpecification;
import org.apache.isis.metamodel.specloader.SpecificationLoader;
import org.apache.isis.runtime.system.context.session.RuntimeContext;
import org.apache.isis.runtime.system.persistence.PersistenceSession;

import lombok.val;

/**
 * package private mixin for ObjectAdapterContext
 * <p>
 * Responsibility: provides ObjectAdapterProvider implementation
 * </p> 
 * @since 2.0
 */
class ObjectAdapterContext_ObjectAdapterProvider implements ObjectAdapterProvider {

    private final ObjectAdapterContext objectAdapterContext;
    private final SpecificationLoader specificationLoader; 
    private final OidFactory oidFactory; 

    ObjectAdapterContext_ObjectAdapterProvider(
            ObjectAdapterContext objectAdapterContext,
            PersistenceSession persistenceSession, 
            RuntimeContext runtimeContext) {

        this.objectAdapterContext = objectAdapterContext;
        this.specificationLoader = runtimeContext.getSpecificationLoader();
        this.oidFactory = OidFactory.buildDefault();
    }

    @Override
    public ObjectAdapter adapterFor(Object pojo) {

        if(pojo == null) {
            return null;
        }

        val rootOid = oidFactory.oidFor(ManagedObject.of(specificationLoader::loadSpecification, pojo));
        val newAdapter = objectAdapterContext.getFactories().createRootAdapter(pojo, rootOid);
        return objectAdapterContext.injectServices(newAdapter);
    }

    // -- DOMAIN OBJECT CREATION SUPPORT

    @Override
    public ObjectAdapter newTransientInstance(ObjectSpecification objectSpec) {
        return objectAdapterContext.objectCreationMixin.newInstance(objectSpec);
    }


}