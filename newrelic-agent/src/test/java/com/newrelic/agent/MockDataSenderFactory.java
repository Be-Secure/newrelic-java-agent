/*
 *
 *  * Copyright 2020 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.newrelic.agent;

import com.newrelic.agent.config.DataSenderConfig;
import com.newrelic.agent.transport.DataSender;
import com.newrelic.agent.transport.DataSenderListener;
import com.newrelic.agent.transport.IDataSenderFactory;

public class MockDataSenderFactory implements IDataSenderFactory {

    private MockDataSender lastDataSender;

    @Override
    public DataSender create(DataSenderConfig config) {
        MockDataSender dataSender = new MockDataSender(config);
        lastDataSender = dataSender;
        return dataSender;
    }

    @Override
    public DataSender create(DataSenderConfig config, DataSenderListener dataSenderListener) {
        MockDataSender dataSender = new MockDataSender(config, dataSenderListener);
        lastDataSender = dataSender;
        return dataSender;
    }

    public MockDataSender getLastDataSender() {
        return lastDataSender;
    }

}
