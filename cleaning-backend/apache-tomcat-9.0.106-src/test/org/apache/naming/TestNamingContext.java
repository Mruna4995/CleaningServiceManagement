/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.naming;

import javax.naming.Context;
import javax.naming.NamingException;

import org.junit.Assert;
import org.junit.Test;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.NamingContextListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.TomcatBaseTest;
import org.apache.naming.factory.ResourceLinkFactory;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;

public class TestNamingContext extends TomcatBaseTest {

    private static final String COMP_ENV = "comp/env";
    private static final String MODULE_ENV = "module/env";
    private static final String GLOBAL_NAME = "global";
    private static final String LOCAL_NAME = "local";
    private static final String DATA = "Cabbage";


    @Test
    public void testGlobalNaming() throws Exception {
        Tomcat tomcat = getTomcatInstance();
        tomcat.enableNaming();

        org.apache.catalina.Context ctx = getProgrammaticRootContext();

        tomcat.start();

        Context webappInitial = ContextBindings.getContext(ctx);

        // Nothing added at the moment so should be null
        Object obj = doLookup(webappInitial, COMP_ENV + "/" + LOCAL_NAME);
        Assert.assertNull(obj);

        ContextEnvironment ce = new ContextEnvironment();
        ce.setName(GLOBAL_NAME);
        ce.setValue(DATA);
        ce.setType(DATA.getClass().getName());

        tomcat.getServer().getGlobalNamingResources().addEnvironment(ce);

        // No link so still should be null
        obj = doLookup(webappInitial, COMP_ENV + "/" + LOCAL_NAME);
        Assert.assertNull(obj);

        // Now add a resource link to the context
        ContextResourceLink crl = new ContextResourceLink();
        crl.setGlobal(GLOBAL_NAME);
        crl.setName(LOCAL_NAME);
        crl.setType(DATA.getClass().getName());
        ctx.getNamingResources().addResourceLink(crl);

        // Link exists so should be OK now
        obj = doLookup(webappInitial, COMP_ENV + "/" + LOCAL_NAME);
        Assert.assertEquals(DATA, obj);

        // Try shortcut
        ResourceLinkFactory factory = new ResourceLinkFactory();
        ResourceLinkRef rlr = new ResourceLinkRef(DATA.getClass().getName(), GLOBAL_NAME, null, null);
        obj = factory.getObjectInstance(rlr, null, null, null);
        Assert.assertEquals(DATA, obj);

        // Remove the link
        ctx.getNamingResources().removeResourceLink(LOCAL_NAME);

        // No link so should be null
        obj = doLookup(webappInitial, COMP_ENV + "/" + LOCAL_NAME);
        Assert.assertNull(obj);

        // Shortcut should fail too
        obj = factory.getObjectInstance(rlr, null, null, null);
        Assert.assertNull(obj);
    }


    @Test
    public void testModuleEquivalentToComp() throws Exception {
        Tomcat tomcat = getTomcatInstance();
        tomcat.enableNaming();

        org.apache.catalina.Context ctx = getProgrammaticRootContext();

        tomcat.start();

        // Equivalent to: Context initContext = new InitialContext();
        Context webappInitial = ContextBindings.getContext(ctx);

        // Make it writable (it is normally read-only)
        String namingContextName = null;
        LifecycleListener[] listeners = ctx.findLifecycleListeners();
        for (LifecycleListener listener : listeners) {
            if (listener instanceof NamingContextListener) {
                NamingContextListener namingListener = (NamingContextListener) listener;
                namingContextName = namingListener.getName();
                break;
            }
        }
        ContextAccessController.setWritable(namingContextName, ctx.getNamingToken());

        // Nothing created so should be null
        Object obj = doLookup(webappInitial, COMP_ENV + "/" + LOCAL_NAME);
        Assert.assertNull(obj);
        obj = doLookup(webappInitial, MODULE_ENV + "/" + LOCAL_NAME);
        Assert.assertNull(obj);

        // Create in java:comp/env
        webappInitial.bind(COMP_ENV + "/" + LOCAL_NAME, DATA);

        // Check it was created in java:comp/env and java:module/env
        obj = doLookup(webappInitial, COMP_ENV + "/" + LOCAL_NAME);
        Assert.assertEquals(DATA,  obj);
        obj = doLookup(webappInitial, MODULE_ENV + "/" + LOCAL_NAME);
        Assert.assertEquals(DATA,  obj);

        // Remove it
        webappInitial.unbind(COMP_ENV + "/" + LOCAL_NAME);

        // Create in java:module/env
        webappInitial.bind(MODULE_ENV + "/" + LOCAL_NAME, DATA);

        // Check it was created in java:comp/env and java:module/env
        obj = doLookup(webappInitial, COMP_ENV + "/" + LOCAL_NAME);
        Assert.assertEquals(DATA,  obj);
        obj = doLookup(webappInitial, MODULE_ENV + "/" + LOCAL_NAME);
        Assert.assertEquals(DATA,  obj);
    }


    private Object doLookup(Context context, String name) {
        Object result = null;
        try {
            result = context.lookup(name);
        } catch (NamingException nnfe) {
            // Ignore
        }
        return result;
    }
}
