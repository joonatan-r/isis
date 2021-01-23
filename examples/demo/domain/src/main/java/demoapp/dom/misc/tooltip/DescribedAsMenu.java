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
package demoapp.dom.misc.tooltip;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.factory.FactoryService;

import lombok.RequiredArgsConstructor;
import lombok.val;

import demoapp.dom.progmodel.actions.assoc.DemoItem;

@DomainService(nature=NatureOfService.VIEW, objectType = "demo.DescribedAsMenu")
@RequiredArgsConstructor(onConstructor_ = { @Inject })
public class DescribedAsMenu {

    final FactoryService factoryService;

    @Action
    @ActionLayout(
            cssClassFa="fa-bolt",
            describedAs="Opens the Tooltip-Demo page.")
    public TooltipDemo tooltipDemo(){
        val demo = factoryService.viewModel(new TooltipDemo());

        demo.getCollection().add(DemoItem.of("first"));
        demo.getCollection().add(DemoItem.of("second"));
        demo.getCollection().add(DemoItem.of("third"));

        return demo;
    }

}
