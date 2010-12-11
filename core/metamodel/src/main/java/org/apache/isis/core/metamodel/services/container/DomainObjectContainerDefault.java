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


package org.apache.isis.core.metamodel.services.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Filter;
import org.apache.isis.applib.PersistFailedException;
import org.apache.isis.applib.RepositoryException;
import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.security.RoleMemento;
import org.apache.isis.applib.security.UserMemento;
import org.apache.isis.core.commons.ensure.Assert;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.authentication.AuthenticationSession;
import org.apache.isis.core.metamodel.consent.InteractionResult;
import org.apache.isis.core.metamodel.runtimecontext.RuntimeContext;
import org.apache.isis.core.metamodel.runtimecontext.RuntimeContextAware;
import org.apache.isis.core.metamodel.services.container.query.QueryFindAllInstances;
import org.apache.isis.core.metamodel.services.container.query.QueryFindByPattern;
import org.apache.isis.core.metamodel.services.container.query.QueryFindByTitle;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.util.IsisUtils;


public class DomainObjectContainerDefault implements DomainObjectContainer, RuntimeContextAware {
	
	private RuntimeContext runtimeContext;


    ////////////////////////////////////////////////////////////////////
    // newInstance, disposeInstance
    ////////////////////////////////////////////////////////////////////

	/**
     * @see #doCreateTransientInstance(ObjectSpecification)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T newTransientInstance(final Class<T> ofClass) {
        final ObjectSpecification spec = getRuntimeContext().getSpecificationLoader().loadSpecification(ofClass);
        final ObjectAdapter adapter = doCreateTransientInstance(spec);
        return (T) adapter.getObject();
    }

    /**
     * Returns a new instance of the specified class that will have been persisted.
     */
    @Override
    public <T> T newPersistentInstance(final Class<T> ofClass) {
        T newInstance = newTransientInstance(ofClass);
        persist(newInstance);
        return newInstance;
    }

    /**
     * Returns a new instance of the specified class that has the sane persisted state as the specified object.
     */
    @Override
    public <T> T newInstance(final Class<T> ofClass, final Object object) {
        if (isPersistent(object)) {
            return newPersistentInstance(ofClass);
        } else {
            return newTransientInstance(ofClass);
        }
    }

    /**
     * Factored out as a potential hook method for subclasses.
     */
    protected ObjectAdapter doCreateTransientInstance(final ObjectSpecification spec) {
    	return getRuntimeContext().createTransientInstance(spec);
    }

    @Override
    public void remove(final Object persistentObject) {
        if (persistentObject == null) {
            throw new IllegalArgumentException("Must specify a reference for disposing an object");
        }
        final ObjectAdapter adapter = getRuntimeContext().getAdapterFor(persistentObject);
        if (!isPersistent(persistentObject)) {
            throw new RepositoryException("Object not persistent: " + adapter);
        }
        
        getRuntimeContext().remove(adapter);
    }
    
    @Override
    public void removeIfNotAlready(final Object object) {
        if (!isPersistent(object)) {
            return;
        }
        remove(object);
    }


    ////////////////////////////////////////////////////////////////////
    // resolve, objectChanged
    ////////////////////////////////////////////////////////////////////

    @Override
    public void resolve(final Object parent) {
    	runtimeContext.resolve(parent);
    }

    @Override
    public void resolve(final Object parent, final Object field) {
    	runtimeContext.resolve(parent, field);
    }

    @Override
    public void objectChanged(final Object object) {
    	runtimeContext.objectChanged(object);
    }

    ////////////////////////////////////////////////////////////////////
    // flush, commit
    ////////////////////////////////////////////////////////////////////

    @Override
    public boolean flush() {
    	return runtimeContext.flush();
    }

    @Override
    public void commit() {
    	runtimeContext.commit();
    }

    ////////////////////////////////////////////////////////////////////
    // isValid, validate
    ////////////////////////////////////////////////////////////////////


	@Override
    public boolean isValid(final Object domainObject) {
		return validate(domainObject) == null;
	}

	@Override
    public String validate(final Object domainObject) {
		final ObjectAdapter adapter = getRuntimeContext().adapterFor(domainObject);
		InteractionResult validityResult = adapter.getSpecification().isValidResult(adapter);
		return validityResult.getReason();
	}


    ////////////////////////////////////////////////////////////////////
    // persistence
    ////////////////////////////////////////////////////////////////////

    @Override
    public boolean isPersistent(final Object domainObject) {
        final ObjectAdapter adapter = getRuntimeContext().adapterFor(domainObject);
        return adapter.isPersistent();
    }

    @Override
    public void persist(final Object transientObject) {
        final ObjectAdapter adapter = getRuntimeContext().getAdapterFor(transientObject);
        if (isPersistent(transientObject)) {
            throw new PersistFailedException("Object already persistent: " + adapter);
        }
        getRuntimeContext().makePersistent(adapter);
    }
    
    @Override
    public void persistIfNotAlready(final Object object) {
        if (isPersistent(object)) {
            return;
        }
        persist(object);
    }

    ////////////////////////////////////////////////////////////////////
    // security
    ////////////////////////////////////////////////////////////////////

    @Override
    public UserMemento getUser() {
        final AuthenticationSession session = getRuntimeContext().getAuthenticationSession();

        final String name = session.getUserName();
        final List<RoleMemento> roleMementos = asRoleMementos(session.getRoles());

        final UserMemento user = new UserMemento(name, roleMementos);
        return user;
    }

    private List<RoleMemento> asRoleMementos(List<String> roles) {
        List<RoleMemento> mementos = new ArrayList<RoleMemento>();
        if (roles != null) {
            for(String role: roles) {
                mementos.add(new RoleMemento(role));
            }
        }
        return mementos;
    }

    ////////////////////////////////////////////////////////////////////
    // properties
    ////////////////////////////////////////////////////////////////////

    @Override
    public String getProperty(String name) {
        return runtimeContext.getProperty(name) ;
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        String value = getProperty(name);
        return value == null ? defaultValue : value;
    }
    
    @Override
    public List<String> getPropertyNames() {
        return runtimeContext.getPropertyNames() ;
    }
    
    ////////////////////////////////////////////////////////////////////
    // info, warn, error messages
    ////////////////////////////////////////////////////////////////////

    @Override
    public void informUser(final String message) {
        getRuntimeContext().informUser(message);
    }

    @Override
    public void raiseError(final String message) {
    	getRuntimeContext().raiseError(message);
    }

    @Override
    public void warnUser(final String message) {
    	getRuntimeContext().warnUser(message);
    }

    
    ////////////////////////////////////////////////////////////////////
    // allInstances, allMatches
    ////////////////////////////////////////////////////////////////////
    
	@Override
    public <T> List<T> allInstances(final Class<T> type) {
        return allMatches(new QueryFindAllInstances<T>(type));
    }

	@Override
    public <T> List<T> allMatches(final Class<T> cls, final Filter<T> filter) {
		final List<T> allInstances = allInstances(cls);
		final List<T> filtered = new ArrayList<T>();
		for (T instance: allInstances) {
			if (filter.accept(instance)) {
				filtered.add(instance);
			}
		}
		return filtered;
	}

	@Override
    public <T> List<T> allMatches(final Class<T> type, final T pattern) {
        Assert.assertTrue("pattern not compatible with type", type.isAssignableFrom(pattern.getClass()));
        return allMatches(new QueryFindByPattern<T>(type, pattern));
    }

    @Override
    public <T> List<T> allMatches(final Class<T> type, final String title) {
        return allMatches(new QueryFindByTitle<T>(type, title));
    }

    @Override
    public <T> List<T> allMatches(final Query<T> query) {
        List<ObjectAdapter> allMatching = getRuntimeContext().allMatchingQuery(query);
		return IsisUtils.unwrap(allMatching);
    }

    ////////////////////////////////////////////////////////////////////
    // firstMatch
    ////////////////////////////////////////////////////////////////////

	@Override
    public <T> T firstMatch(final Class<T> cls, final Filter<T> filter) {
		final List<T> allInstances = allInstances(cls);
		for (T instance: allInstances) {
			if (filter.accept(instance)) {
				return instance;
			}
		}
		return null;
	}
    
    @Override
    public <T> T firstMatch(final Class<T> type, final T pattern) {
        final List<T> instances = allMatches(type, pattern);
        return firstInstanceElseNull(instances);
    }

    @Override
    public <T> T firstMatch(final Class<T> type, final String title) {
        final List<T> instances = allMatches(type, title);
        return firstInstanceElseNull(instances);
    }

    @Override
    @SuppressWarnings("unchecked")
	public <T> T firstMatch(final Query<T> query) {
        ObjectAdapter firstMatching = getRuntimeContext().firstMatchingQuery(query);
        return (T) IsisUtils.unwrap(firstMatching);
    }

    ////////////////////////////////////////////////////////////////////
    // uniqueMatch
    ////////////////////////////////////////////////////////////////////

	@Override
    public <T> T uniqueMatch(final Class<T> type, final Filter<T> filter) {
		final List<T> instances = allMatches(type, filter);
		if (instances.size() > 1) {
			throw new RepositoryException(
					"Found more than one instance of " + type + " matching filter " + filter);
		}
		return firstInstanceElseNull(instances);
	}

    @Override
    public <T> T uniqueMatch(final Class<T> type, final T pattern) {
        final List<T> instances = allMatches(type, pattern);
        if (instances.size() > 1) {
            throw new RepositoryException("Found more that one instance of " + type + " matching pattern " + pattern);
        }
        return firstInstanceElseNull(instances);
    }

    @Override
    public <T> T uniqueMatch(final Class<T> type, final String title) {
        final List<T> instances = allMatches(type, title);
        if (instances.size() > 1) {
            throw new RepositoryException("Found more that one instance of " + type + " with title " + title);
        }
        return firstInstanceElseNull(instances);
    }

    @Override
    public <T> T uniqueMatch(final Query<T> query) {
        final List<T> instances = allMatches(query);
        if (instances.size() > 1) {
            throw new RepositoryException("Found more that one instance for query:" + query.getDescription());
        }
        return firstInstanceElseNull(instances);
    }

    private <T> T firstInstanceElseNull(final List<T> instances) {
        return instances.size() == 0 ? null : instances.get(0);
    }


    
    ////////////////////////////////////////////////////////////////////
    // Dependencies (due to being RuntimeContextAware)
    ////////////////////////////////////////////////////////////////////
	
	/**
	 * The {@link RuntimeContext}, as provided by the constructor.
	 * 
	 * <p>
	 * Not API.
	 */
	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

	@Override
    public void setRuntimeContext(RuntimeContext runtimeContext) {
		this.runtimeContext = runtimeContext;
		runtimeContext.setContainer(this);
	}
	



}
