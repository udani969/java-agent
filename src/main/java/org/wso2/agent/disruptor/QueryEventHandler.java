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

package org.wso2.agent.disruptor;

import com.lmax.disruptor.EventHandler;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import java.io.IOException;

public class QueryEventHandler implements EventHandler<QueryEvent>{
    final static Logger logger = Logger.getLogger(QueryEventHandler.class);

    public void onEvent(QueryEvent event, long sequence, boolean endOfBatch)
            throws IOException, InterruptedException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            DataEndpointException, DataEndpointConfigurationException,
            TransportException {

//        if(!org.wso2.javaagent.JDBCAgentPublisher.isServerState()){
//            String host = "localhost";
//            int port = 7611;
//
//            InetAddress address = InetAddress.getByName(host);
//            Socket socket = new Socket(address, port);
//
//            while (!socket.isConnected()){
////                System.out.println("Trying to connect to server....");
//                logger.info("Trying to connect to server...");
//                Thread.sleep(500);
//            }
            logger.info("Connected to server and initialize publisher");
            org.wso2.javaagent.JDBCAgentPublisher.initializeServerPublisher();
//            org.wso2.javaagent.JDBCAgentPublisher.setServerState(true);
//            System.out.println("Connected to server");
//        }

        org.wso2.javaagent.JDBCAgentPublisher.publishEvents(
                org.wso2.javaagent.JDBCAgentPublisher.getDataPublisher(),
                org.wso2.javaagent.JDBCAgentPublisher.getStreamId(),
                event.getTimestamp(),
                "",
                event.getQueryExecuted());
    }
}
