package org.celllife.vmmc.interfaces.service;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

public class TestCallLogController {

    CallLogController callLogController = new CallLogController();

    @Ignore
    @Test
    public void testParseVerboiceData() {

        Map<String, String> vars = callLogController.parseVerboiceData("campaignId=10&passwordEntered=1111&msisdn=27724194158&messageNumber=100&CallSid=322");

        Assert.assertEquals("10", vars.get("campaignId"));
        Assert.assertEquals("322", vars.get("CallSid"));

    }

}
