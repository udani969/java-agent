/*
 *
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.wso2.javaagent;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import java.lang.instrument.Instrumentation;
import java.net.SocketException;
import java.net.UnknownHostException;

public class JDBCAgent {

    public static void premain(String agentArgs, Instrumentation instrumentation)
            throws DataEndpointException,
            DataEndpointConfigurationException,
            UnknownHostException,
            SocketException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException {

        final Logger logger = Logger.getLogger(JDBCAgent.class);

        logger.info("Starting Java Agent");
//        System.out.println("Starting JDBC Agent");
        logger.info("Initializing AgentPublisher");
        JDBCAgentPublisher javaAgentPublisher = new JDBCAgentPublisher();
        logger.info("Start instumenting");
        JDBCClassTransformer transformer = new JDBCClassTransformer();
        instrumentation.addTransformer(transformer);
        logger.info("Finish instrumentation");

    }

}
