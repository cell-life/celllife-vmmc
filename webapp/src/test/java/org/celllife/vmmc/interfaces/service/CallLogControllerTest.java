package org.celllife.vmmc.interfaces.service;

import junit.framework.Assert;
import org.celllife.vmmc.test.TestConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
public class CallLogControllerTest extends TestConfiguration {

    CallLogController callLogController = new CallLogController();

    @Ignore
    @Test
    public void testParseVerboiceData() {

        Map<String, String> vars = callLogController.parseVerboiceData("campaignId=10&passwordEntered=1111&msisdn=27724194158&messageNumber=100&CallSid=322");

        Assert.assertEquals("10", vars.get("campaignId"));
        Assert.assertEquals("322", vars.get("CallSid"));

    }

    @Test
    public void testTimes() throws ParseException {

        DateFormat formatter = new SimpleDateFormat("HH:mm");

        Date date = (Date)formatter.parse("00:00");
        System.out.println(date);

        date = (Date)formatter.parse("05:00");
        System.out.println(date);

        date = (Date)formatter.parse("12:00");
        System.out.println(date);

        date = (Date)formatter.parse("14:00");
        System.out.println(date);

        date = (Date)formatter.parse("22:00");
        System.out.println(date);
    }

}
