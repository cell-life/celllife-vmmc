package org.celllife.vmmc.interfaces.web

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ContactsController {

    @Value('${external.base.url}')
    def String externalBaseUrl;

    @RequestMapping("/")
    def index(Model model) {
        return getIndexPage(model);
    }

    @RequestMapping(value = "index", method = RequestMethod.GET)
    def getIndexPage(Model model) {

        def campaigns = org.celllife.vmmc.framework.restclient.RESTClient.get("${externalBaseUrl}/service/campaigns")
        model.put("campaigns", campaigns)

        def reports = org.celllife.vmmc.framework.restclient.RESTClient.get("${externalBaseUrl}/service/reports")
        model.put("reports", reports)

        return "index";

    }

}
