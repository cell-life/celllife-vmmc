package org.celllife.vmmc.interfaces.service;

import org.celllife.ivr.application.contact.ContactService;
import org.celllife.ivr.domain.contact.Contact;
import org.celllife.ivr.domain.contact.ContactDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class ContactController {

    private static Logger log = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    ContactService contactService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/service/campaigns/{campaignId}/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ContactDto createContact(HttpServletResponse response, @RequestBody Collection<ContactDto> contactDtos, @PathVariable Long campaignId) throws Exception {

            ContactDto contactDto = contactDtos.iterator().next();

            Contact contact = new Contact(contactDto.getMsisdn(), contactDto.getPassword(), campaignId, 0);
            contact = contactService.saveContact(contact);
            response.setStatus(HttpServletResponse.SC_CREATED);
            return contact.getContactDto();

    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/service/campaigns/{campaignId}/contacts/{contactId}")
    public ContactDto updateContact(HttpServletResponse response, @RequestBody Collection<ContactDto> contactDtos, @PathVariable Long campaignId, @PathVariable Long contactId) throws Exception {

        ContactDto contactDto = contactDtos.iterator().next();
        Contact contact = contactService.getContactById(contactId);

        if (contactDto.getMsisdn() != null)
            throw new Exception("You cannot change the MSISDN of a contact. Rather add a new contact.");
        if (contactDto.getPassword() != null)
            contact.setPassword(contactDto.getPassword());
        if (contactDto.getProgress() != null)
            contact.setProgress(contactDto.getProgress());
        if (contactDto.getCampaignId() != null)
            throw new Exception("You cannot change the campaign ID of a contact. Rather add a new contact.");

        contact = contactService.saveContact(contact);

        return contact.getContactDto();

    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/service/campaigns/{campaignId}/contacts/{contactId}")
    public ContactDto deleteContact(HttpServletResponse response, @PathVariable Long campaignId, @PathVariable Long contactId) throws Exception {

        Contact contact = contactService.getContactById(contactId);
        contact.setVoided(true);
        contact = contactService.saveContact(contact);

        return contact.getContactDto();

    }

    @ResponseBody
    @RequestMapping(value = "/service/campaigns/{campaignId}/contacts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<ContactDto> getContactsInCampaign(@PathVariable Long campaignId) {

        List<Contact> contacts = contactService.findContactsInCampaign(campaignId);

        Collection<ContactDto> contactDtos = new ArrayList<>();

        for (Contact contact : contacts) {
            contactDtos.add(contact.getContactDto());
        }

        return contactDtos;

    }

}
