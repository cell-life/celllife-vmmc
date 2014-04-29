package org.celllife.vmmc.interfaces.service;

import org.celllife.ivr.application.calllog.CallLogService;
import org.celllife.ivr.domain.callog.CallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CallLogController {

    @Autowired
    CallLogService callLogService;

    @ResponseBody
    @RequestMapping(value = "/service/serverCallback", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void getContactsInCampaign(@RequestParam(value = "CallSid", required = true) Long callId, @RequestParam(value = "CallStatus", required = true) String callStatus, @RequestParam(value = "From", required = false) String msisdn) {

        CallLog callLog = callLogService.findCallLogByVerboiceId(callId);

        if ((callLog != null) && (callLog.getMsisdn().equals(msisdn))) {
            callLog.setState(callStatus);
            callLogService.saveCallLog(callLog);
        }

    }


}
