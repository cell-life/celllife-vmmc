package org.celllife.vmmc.interfaces.service;

import org.celllife.ivr.application.campaign.CampaignService;
import org.celllife.ivr.application.contact.ContactService;
import org.celllife.ivr.application.verboice.VerboiceApplicationService;
import org.celllife.ivr.domain.campaign.Campaign;
import org.celllife.ivr.domain.contact.Contact;
import org.celllife.ivr.domain.contact.FailedContactDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
public class CsvUploadController {

    private static Logger log = LoggerFactory.getLogger(CsvUploadController.class);

    @Value("${vmmc.validation_regex}")
    String validationRegex;

    @Autowired
    ContactService contactService;

    @Autowired
    CampaignService campaignService;

    @Autowired
    VerboiceApplicationService verboiceApplicationService;

    @ResponseBody
    @RequestMapping(value = "/service/campaign/{campaignName}/contacts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<FailedContactDto> upload(@RequestParam("file") MultipartFile uploadedFile, @PathVariable String campaignName) throws Exception {

        List<Campaign> campaigns = campaignService.findCampaignByName(campaignName);

        Campaign campaign = new Campaign();
        if (campaigns.size() > 1)   {
            throw new Exception("More than one campaign with this name exists.");
        }
        else {
            campaign = campaigns.get(0);
        }

        CsvMapReader mapReader = new CsvMapReader(new InputStreamReader(uploadedFile.getInputStream()), CsvPreference.STANDARD_PREFERENCE);

        List<FailedContactDto> failedContactDtos = new ArrayList<FailedContactDto>();

        final String[] header = {"msisdn", "password"};
        final CellProcessor[] processors = getProcessors();

        Map<String, Object> contactMap;
        List<Contact> contactList = new ArrayList<>();

        contactMap = mapReader.read(header, processors);
        if (contactMap == null) {
            throw new Exception("CSV file is blank.");
        } else if ( (!contactMap.get("msisdn").toString().equalsIgnoreCase("msisdn")) || (!contactMap.get("password").toString().equalsIgnoreCase("password"))) {
            throw new Exception("The column headers must be 'msisdn' and 'password'.");
        }

        try {
            while ((contactMap = mapReader.read(header, processors)) != null) {
                String msisdn = contactMap.get("msisdn").toString();
                String password = contactMap.get("password").toString();
                if (msisdn.matches(validationRegex)) {
                    Contact contact = new Contact(msisdn, password, campaign.getId(), 0);
                    contactList.add(contact);
                } else {
                    failedContactDtos.add(new FailedContactDto(msisdn, "Number format is invalid. The number should consist of 11 digits and be in the format 27721234567."));
                }
            }
        } catch (SuperCsvConstraintViolationException e) {
            log.warn("Number format is invalid. The number should not be blank.", e);
            failedContactDtos.add(new FailedContactDto("", "Number format is invalid. The number should not be blank."));
        }

        List<String> locallyFailedNumbers = contactService.saveContacts(contactList);
        for (String number : locallyFailedNumbers) {
            log.warn("The number " + number + " could not be added to the local database.");
            failedContactDtos.add(new FailedContactDto(number, "Possibly this number already exists in the campaign."));
        }

        mapReader.close();
        return failedContactDtos;

    }

    private static CellProcessor[] getProcessors() {

        final CellProcessor[] processors = new CellProcessor[]{
                new NotNull(),
                new NotNull()
        };

        return processors;
    }

}
