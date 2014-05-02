package org.celllife.vmmc.interfaces.service;

import org.celllife.ivr.application.calllog.CallLogService;
import org.celllife.ivr.application.utils.JsonUtils;
import org.celllife.ivr.domain.callog.CallLog;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CallLogController {

    private static Logger log = LoggerFactory.getLogger(CallLogController.class);

    @Autowired
    CallLogService callLogService;

    JsonUtils jsonUtils = new JsonUtils();

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

    @RequestMapping(value = "/verboice-service/passwordCallback", method = RequestMethod.POST)
    public void updateCallLog(@RequestBody String requestBody) {

        log.debug("Received password update from Verboice: " + requestBody);

        Map<String, String> responseVariables = new HashMap<>();
        try {
            responseVariables = jsonUtils.extractJsonVariables("{\"response\":" + requestBody + "}");
        } catch (JSONException e) {
            log.warn("Could not extract JSON variables from Verboice response " + requestBody);
        }

        String passwordEntered = responseVariables.get("passwordEntered");
        Integer verboiceProjectId = Integer.valueOf(responseVariables.get("verboiceProjectId"));
        Integer messageNumber = Integer.valueOf(responseVariables.get("messageNumber"));
        String msisdn = responseVariables.get("msisdn");

        CallLog callLog = callLogService.findCallLogByVerboiceIdAndMsisdnAndMessageNumber(verboiceProjectId, msisdn, messageNumber);

        if ((callLog != null)) {
            callLog.setPasswordEntered(passwordEntered);
            callLogService.saveCallLog(callLog);
        }

    }


}
