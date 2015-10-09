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

import com.lmax.disruptor.RingBuffer;

public class QueryEventProducer {
    private final RingBuffer<QueryEvent> ringBuffer;

    public QueryEventProducer(RingBuffer<QueryEvent> ringBuffer)
    {
        this.ringBuffer = ringBuffer;
    }

    /**
     * When provided with current query and timestamp initialize next event with current data
     *
     * @param timestamp current timestamp of the executed query
     * @param queryExecuted query executed by the server
     */
    public void onData(long timestamp, String queryExecuted)
    {
        long sequence = ringBuffer.next();
        try
        {
            QueryEvent event = ringBuffer.get(sequence);
            event.setTimestamp(timestamp);
            event.setQueryExecuted(queryExecuted);
        }
        finally
        {
            ringBuffer.publish(sequence);
        }
    }
}

//System.currentTimeMillis()