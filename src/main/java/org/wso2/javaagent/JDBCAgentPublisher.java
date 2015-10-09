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

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.wso2.agent.disruptor.QueryEvent;
import org.wso2.agent.disruptor.QueryEventFactory;
import org.wso2.agent.disruptor.QueryEventHandler;
import org.wso2.agent.disruptor.QueryEventProducer;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class JDBCAgentPublisher {
    private static final String CARBON_HOME = org.wso2.carbon.utils.CarbonUtils.getCarbonHome();
    private static final String QUERY_STREAM = "org.wso2.jdbc.query.stream";
    private static final String VERSION = "1.0.0";
    private static final int defaultThriftPort = 7611;
    private static final int defaultBinaryPort = 9611;
    private static String originalQuery;
    private static String connectionURL;
    private static int variableCount;
    private static ArrayList arrayList;
    private static String streamId;
    private static DataPublisher dataPublisher;

    private static QueryEventProducer producer;
    private static RingBuffer<QueryEvent> ringBuffer;
    private static Disruptor<QueryEvent> disruptor;
    private static QueryEventFactory factory;
    private static Executor executor;
    private static boolean serverState;

    final static Logger logger = Logger.getLogger(JDBCAgentPublisher.class);


    public JDBCAgentPublisher() throws DataEndpointException, SocketException, UnknownHostException, DataEndpointConfigurationException, DataEndpointAuthenticationException, DataEndpointAgentConfigurationException, TransportException {
        serverState = false;
        // Executor that will be used to construct new threads for consumers
        //these threads wil be reused
        executor = Executors.newCachedThreadPool();

        // The factory for the event
        factory = new QueryEventFactory();

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 64;

        // Construct the Disruptor
        disruptor = new Disruptor<QueryEvent>(factory, bufferSize, executor);

        // Connect the handler, the consumer
        disruptor.handleEventsWith(new QueryEventHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        ringBuffer = disruptor.getRingBuffer();

        producer = new QueryEventProducer(ringBuffer);
        logger.info("Initialize LMAX Disruptor");

        String currentDir = System.getProperty("user.dir");
        String log4jConfPath = currentDir + "/samples/java_agent/src/main/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);

        System.setProperty("javax.net.ssl.trustStore",CARBON_HOME+"/repository/conf/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

//        initializeServerPublisher();
    }

    public static String getOriginalQuery() {
        return originalQuery;
    }

    public static int getVariableCount() {
        return variableCount;
    }

    public static String getArrayList(int index) {
        return (String) arrayList.get(index);
    }

    public static DataPublisher getDataPublisher() {
        return dataPublisher;
    }

    public static String getStreamId() {
        return streamId;
    }

    public static String getConnectionURL() {
        return connectionURL;
    }

    public static boolean isServerState() {
        return serverState;
    }

    public static QueryEventProducer getProducer() {
        return producer;
    }

    public static void initializeServerPublisher() throws SocketException,
            UnknownHostException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException, DataEndpointException,
            DataEndpointConfigurationException {

//        String log4jConfPath = "./src/main/resources/log4j.properties";
//        PropertyConfigurator.configure(log4jConfPath);

//        String currentDir = System.getProperty("user.dir");
//        System.setProperty("javax.net.ssl.trustStore",
//                currentDir + "/samples/java_agent/src/main/resources/client-truststore.jks");
//        System.setProperty("javax.net.ssl.trustStore",CARBON_HOME+"/repository/conf/client-truststore.jks");
//        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

        AgentHolder.setConfigPath(getDataAgentConfigPath());
        String host = getLocalAddress().getHostAddress();

        String type = getProperty("type", "Thrift");
        int receiverPort = defaultThriftPort;
        if (type.equals("Binary")) {
            receiverPort = defaultBinaryPort;
        }
        int securePort = receiverPort + 100;

        String url = getProperty("url", "tcp://" + host + ":" + receiverPort);
        String authURL = getProperty("authURL", "ssl://" + host + ":" + securePort);
        String username = getProperty("username", "admin");
        String password = getProperty("password", "admin");

        dataPublisher = new DataPublisher(type, url, authURL, username, password);

        streamId = DataBridgeCommonsUtils.generateStreamId(QUERY_STREAM, VERSION);
//        /samples/java_agent
    }

    /**
     * Publish the obtained queries to DAS
     *
     * @param dataPublisher DataPublisher object
     * @param streamId Id of the stream in use
     * @param sqlQuery query to be passed to the stream
     * @param connectionURL URL passed to the establish the connection
     * @throws FileNotFoundException
     * @throws SocketException
     * @throws UnknownHostException
     */
//    public static void publishEvents(DataPublisher dataPublisher, String streamId,
//                                     String connectionURL, String sqlQuery)
//            throws FileNotFoundException, SocketException, UnknownHostException {
//
//        Object[] payload = new Object[] { connectionURL, sqlQuery };
//        Event event = new Event(streamId, System.currentTimeMillis(), null, null, payload);
//        dataPublisher.publish(event);
//        logger.info("Publish query : "+ sqlQuery);
//    }

    public static void publishEvents(DataPublisher dataPublisher, String streamId, long timestamp,
                                     String connectionURL, String sqlQuery)
            throws FileNotFoundException, SocketException, UnknownHostException {
        Object[] payload = new Object[] { connectionURL, sqlQuery };
        Event event = new Event(streamId, timestamp, null, null, payload);
        logger.info("Publish query : "+ sqlQuery);
        dataPublisher.publish(event);
    }


    private static String getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0 || result.equals("")) {
            result = def;
        }
        return result;
    }

    public static InetAddress getLocalAddress() throws SocketException, UnknownHostException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            Enumeration<InetAddress> addresses = iface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                    return addr;
                }
            }
        }
        return InetAddress.getLocalHost();
    }

    public static String getDataAgentConfigPath() {
//        File filePath = new File("samples" + File.separator +"java_agent" + File.separator +"src" +
//                File.separator + "main" + File.separator + "resources");
//        File filePath = new File("src" + File.separator + "main" + File.separator + "resources");
        return CARBON_HOME + File.separator + "repository" + File.separator + "conf"
                + File.separator + "data-agent-conf.xml";
//        return filePath.getAbsolutePath() + File.separator + "data-agent-conf.xml";
    }

    public static void setConnectionURL(String connectionURL) {
        JDBCAgentPublisher.connectionURL = connectionURL;
    }

    public static void setServerState(boolean serverState) {
        JDBCAgentPublisher.serverState = serverState;
    }

    /**
     * Count the number of parameters to be filled and create a array list to store actual values
     * Pass the obtained query to originalQuery
     * @param query original query obtained
     */
    public static void setObtainedQuery(String query) {
        originalQuery = query;
        variableCount = StringUtils.countMatches(query, "?");
        if (variableCount > 0) {
            arrayList = new ArrayList();
        }
    }

    /**
     * add the modified variable value to the listArray
     * @param value value of the variable passed with set method
     * @param methodName uppercase value of the method name
     */
    @SuppressWarnings("unchecked")
    public static void fillArrayList(String value, String methodName) {
        arrayList.add(checkVariableType(methodName, value));
    }

    /**
     * Checks whether the passed value is a string or a char and
     * modify by adding quotes to notify as string in the query
     * @param methodName uppercase value of the method name
     * @param value value of the variable passed with set method
     * @return If variable is a string or a char, add quotes to the variable,
     * else return the original value
     */
    public static String checkVariableType(String methodName, String value) {
        if (methodName.contains("STRING") || methodName.contains("CHAR")) {
            return "\'" + value + "\'";
        } else {
            return value;
        }
    }

    /**
     * Modify the obtained original query by replacing '?' with respective values from the array list
     * Clear the the arrayList at end
     * @return the modified query
     */
    public static String modifyOriginalQuery() {
        String modifiedQuery = getOriginalQuery();
        for (int i = 0; i < org.wso2.javaagent.JDBCAgentPublisher.getVariableCount(); i++) {
            modifiedQuery = modifiedQuery.replaceFirst("\\?", getArrayList(i));
        }
        arrayList.clear();
        return modifiedQuery;
    }
}
