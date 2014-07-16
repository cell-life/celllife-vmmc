package org.celllife.vmmc.interfaces.service;

import org.celllife.ivr.application.calllog.CallLogService;
import org.celllife.ivr.domain.calllog.CallLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class CallLogController {

    private static Logger log = LoggerFactory.getLogger(CallLogController.class);

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
                "        <step name=\"password_details\" display-name=\"Send Password Details\" icon=\"flag\" type=\"callback\" callback-url=\"" + externalBaseUrl + "/verboice-service/passwordCallback\">\n" +
                "            <settings>\n" +
                "                <variable name=\"passwordEntered\" display-name=\"Password Entered\" type=\"string\"/>\n" +
                "            </settings>\n" +
                "            <response type=\"none\"/>\n" +
                "        </step>\n" +
                "    </steps>\n" +
                "</verboice-service>";

        return manifest;

    }

    @ResponseBody
    @RequestMapping(value = "/verboice-service/passwordCallback", method = RequestMethod.POST)
    public void updateCallLog(@RequestBody String requestBody) {

        log.debug("Received password update from Verboice: " + requestBody);

        Map<String, String> passwordVariables = parseVerboiceData(requestBody);

        Long verboiceCallId = 0L;

        try {
            verboiceCallId = Long.valueOf(passwordVariables.get("CallSid"));
        } catch (NumberFormatException e) {
            log.warn("Could not not detect 'CallSid' variable in verboice server response " + requestBody + ".");
            return;
        }

        CallLog callLog = callLogService.findCallLogByVerboiceId(verboiceCallId);

        if ((callLog != null)) {
            callLog.setPasswordEntered(passwordVariables.get("passwordEntered"));
            callLogService.saveCallLog(callLog);
        } else {
            log.warn("Could not update call log because the log id " + verboiceCallId + " could not be found. ");
        }

    }

    /**
     * Source: http://stackoverflow.com/questions/13592236/parse-the-uri-string-into-name-value-collection-in-java
     */
    Map<String, String> parseVerboiceData(String request) {

            Map<String, String> query_pairs = new LinkedHashMap<String, String>();
            String[] pairs = request.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(pair.substring(0, idx), pair.substring(idx + 1));
            }
            return query_pairs;
    }


}
