package org.celllife.vmmc.interfaces.service;

import org.celllife.ivr.application.calllog.CallLogService;
import org.celllife.ivr.domain.callog.CallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${external.base.url}")
   String externalBaseUrl;

    @ResponseBody
    @RequestMapping(value = "/verboice-service/serverCallback", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateCallState(@RequestParam(value = "CallSid", required = true) Long callId, @RequestParam(value = "CallStatus", required = true) String callStatus, @RequestParam(value = "From", required = false) String msisdn) {

        CallLog callLog = callLogService.findCallLogByVerboiceId(callId);

        if ((callLog != null) && (callLog.getMsisdn().equals(msisdn))) {
            callLog.setState(callStatus);
            callLogService.saveCallLog(callLog);
        }

    }

    @ResponseBody
    @RequestMapping(value = "/verboice-service/passwordCallback.xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    public String getManifestForPasswordCallBack() {

        String manifest = "<verboice-service>\n" +
                "    <name>Cell Life</name>\n" +
                "    <global-settings>\n" +
                "        <variable name=\"service_domain\" display-name=\"Service Domain\" type=\"string\"/>\n" +
                "    </global-settings>\n" +
                "    <steps>\n" +
                "        <step name=\"password_details\" display-name=\"Send Password Details\" icon=\"dummy\" type=\"callback\" callback-url=\"" + externalBaseUrl + "/verboice-service/passwordCallback\">\n" +
                "            <settings>\n" +
                "                <variable name=\"passwordEntered\" display-name=\"Password Entered\" type=\"string\"/>\n" +
                "                <variable name=\"campaignId\" display-name=\"Campaign Id\" type=\"string\"/>\n" +
                "                <variable name=\"messageNumber\" display-name=\"Message Number\" type=\"string\"/>\n" +
                "                <variable name=\"msisdn\" display-name=\"Msisdn Number\" type=\"string\"/>\n" +
                "            </settings>\n" +
                "            <response type=\"none\"/>\n" +
                "        </step>\n" +
                "    </steps>\n" +
                "</verboice-service>";

        return manifest;

    }

    @ResponseBody
    @RequestMapping(value = "/verboice-service/passwordCallback", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateCallLog(@RequestParam(value = "passwordEntered", required = true) String passwordEntered, @RequestParam(value = "verboiceProjectId", required = true) Integer verboiceProjectId, @RequestParam(value = "messageNumber", required = true) Integer messageNumber, @RequestParam(value = "msisdn", required = true) String msisdn) {

        CallLog callLog = callLogService.findCallLogByVerboiceIdAndMsisdnAndMessageNumber(verboiceProjectId,msisdn,messageNumber);

        if ((callLog != null)) {
            callLog.setPasswordEntered(passwordEntered);
            callLogService.saveCallLog(callLog);
        }

    }


}
