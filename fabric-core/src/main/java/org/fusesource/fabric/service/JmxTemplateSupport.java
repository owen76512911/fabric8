/*
 * Copyright (C) 2011, FuseSource Corp.  All rights reserved.
 * http://fusesource.com
 *
 * The software in this package is published under the terms of the
 * CDDL license a copy of which has been included with this distribution
 * in the license.txt file.
 */
package org.fusesource.fabric.service;

import org.apache.karaf.admin.management.AdminServiceMBean;
import org.fusesource.fabric.api.Agent;
import org.fusesource.fabric.api.FabricException;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.ServiceStateMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMX;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Utility class which contains code related to JMX connectivity.
 *
 * @author ldywicki
 */
public abstract class JmxTemplateSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmxTemplateSupport.class);

    public interface JmxConnectorCallback<T> {

        T doWithJmxConnector(JMXConnector connector) throws Exception;

    }

    public abstract <T> T execute(JmxConnectorCallback<T> callback);


    // mBean specific callbacks

    public static ObjectName safeObjectName(String domain, String ... args) {
        if ((args.length % 2) != 0) {
             LOGGER.warn("Not all values were defined for arguments %", Arrays.toString(args));
        }
        Hashtable<String, String> table = new Hashtable<String, String>();
        for (int i = 0; i < args.length; i += 2) {
            table.put(args[i], args[i + 1]);
        }
        try {
            return new ObjectName(domain, table);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException("Object name is invalid", e);
        }
    }

    public <T> T getMBean(JMXConnector connector, Class<T> type, String domain, String ... params) {
        try {
            return JMX.newMBeanProxy(connector.getMBeanServerConnection(), safeObjectName(domain, params), type);
        } catch (IOException e) {
            throw new FabricException(e);
        }
    }

}
