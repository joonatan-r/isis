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
package demoapp.dom.annotDomain.DomainObject;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import lombok.RequiredArgsConstructor;
import lombok.val;

import demoapp.dom.annotDomain.DomainObject.entityChangePublishing.DomainObjectEntityChangePublishingVm;
import demoapp.dom.annotDomain.DomainObject.nature.viewmodels.jaxbrefentity.StatefulVmJaxbRefsEntity;
import demoapp.dom.annotDomain.DomainObject.nature.viewmodels.usingjaxb.StatefulVmUsingJaxb;

@DomainService(nature=NatureOfService.VIEW, objectType = "demo.DomainObjectMenu")
//@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class DomainObjectMenu {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-question-circle", describedAs = "Search object in prompt"
            , named = "Auto Complete (TODO)"
    )
    public void autoComplete(){
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-list-ul", describedAs = "Choose 'reference data' object (one of a bounded set) in prompt"
            , named = "Bounded (TODO)"
    )
    public void bounded(){
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-pencil-alt", describedAs = "Default editability of properties"
            , named = "Editing (TODO)"
    )
    public void editing() {
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-book", describedAs = "Entity changed events as XML")
    public DomainObjectEntityChangePublishingVm entityChangePublishing(){
        return new DomainObjectEntityChangePublishingVm();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-tools", describedAs = "For mixins, override the default method name"
            , named = "Mixin method (TODO)"
    )
    public void mixinMethod() {
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-gamepad", describedAs = "@DomainObject(nature=VIEW_MODEL) for a Stateful View Model")
    public StatefulVmUsingJaxb natureStateful(final String message) {
        val viewModel = new StatefulVmUsingJaxb();
        viewModel.setMessage(message);
        return viewModel;
    }
    public String default0NatureStateful() {
        return "Some initial state";
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-gamepad", describedAs = "@DomainObject(nature=VIEW_MODEL) for a Stateful View Model referencing an entity")
    public StatefulVmJaxbRefsEntity natureStatefulRefsEntity(final String message) {
        val viewModel = new StatefulVmJaxbRefsEntity();
        viewModel.setMessage(message);
        return viewModel;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-circle", describedAs = "Explicitly specify the object type alias"
            , named = "Object type (TODO)"
    )
    public void objectType() {
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-asterisk", describedAs = "Default class of the domain event emitted when interacting with the domain object's actions, properties or collections"
            , named = "??? Domain Event (TODO)"
    )
    public void xxxDomainEvent() {
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-redo", describedAs = "Class of the lifecycle event emitted when the domain entity transitions through its persistence lifecycle"
            , named = "??? Lifecycle Event (TODO)"
    )
    public void xxxLifecycleEvent() {
    }

    public String default0NatureStatefulRefsEntity() {
        return "Some initial state";
    }


}
