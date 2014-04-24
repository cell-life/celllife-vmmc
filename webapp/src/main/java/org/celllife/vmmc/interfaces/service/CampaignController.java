package org.celllife.vmmc.interfaces.service;

import org.celllife.ivr.application.campaign.CampaignService;
import org.celllife.ivr.application.message.CampaignMessageService;
import org.celllife.ivr.domain.campaign.Campaign;
import org.celllife.ivr.domain.campaign.CampaignDto;
import org.celllife.ivr.domain.campaign.CampaignStatus;
import org.celllife.ivr.domain.campaign.CampaignType;
import org.celllife.ivr.domain.exception.IvrException;
import org.celllife.ivr.domain.message.CampaignMessage;
import org.celllife.ivr.domain.message.CampaignMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Controller
public class CampaignController {

    private static Logger log = LoggerFactory.getLogger(CampaignController.class);

    @Autowired
    CampaignService campaignService;

    @Autowired
    CampaignMessageService campaignMessageService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value= "/service/campaigns", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<CampaignDto> createCampaign(@RequestBody List<CampaignDto> campaignDtos, HttpServletResponse response) throws Exception{

        CampaignDto campaignDto = campaignDtos.get(0);

        if (campaignDtos.size() > 1) {
            throw new Exception("You can only add one campaign at a time!");
        }

        Campaign campaign = new Campaign(campaignDto.getName(), campaignDto.getDescription(), campaignDto.getDuration(), campaignDto.getCallFlowName(), campaignDto.getChannelName(), campaignDto.getScheduleName(), campaignDto.getVerboiceProjectId());
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setStartDate(new Date());
        campaign = campaignService.saveCampaign(campaign);

        List<CampaignDto> campaignDtoList = new ArrayList<>();
        campaignDtoList.add(campaign.getCampaignDto());

        response.setStatus(HttpServletResponse.SC_CREATED);
        return campaignDtoList;

    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value= "/service/campaigns/{campaignId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CampaignDto updateCampaign(@RequestBody Collection<CampaignDto> campaignDtos, @PathVariable Long campaignId) throws Exception{

        CampaignDto campaignDto = campaignDtos.iterator().next();

        Campaign campaign = campaignService.getCampaign(campaignId);

        if (campaign == null) {
            throw new Exception("A campaign with this ID doesn't exist!");
        }

        if (campaignDto.getScheduleName() != null)
            campaign.setScheduleName(campaignDto.getScheduleName());
        if (campaignDto.getChannelName() != null)
            campaign.setChannelName(campaignDto.getChannelName());
        if (campaignDto.getCallFlowName() != null)
            campaign.setCallFlowName(campaignDto.getCallFlowName());
        if (campaignDto.getDescription() != null)
            campaign.setDescription(campaignDto.getDescription());
        if (campaignDto.getDuration() != null)
            campaign.setDuration(campaignDto.getDuration());
        if (campaignDto.getName() != null)
            campaign.setName(campaignDto.getName());

        campaign = campaignService.saveCampaign(campaign);

        return campaign.getCampaignDto();

    }

    @ResponseBody
    @RequestMapping(value = "/service/campaigns", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<CampaignDto> getCampaigns() {

        List<Campaign> campaigns = campaignService.getAllCampaigns();

        Collection<CampaignDto> campaignDtos = new ArrayList<>();

        for (Campaign campaign : campaigns) {
            campaignDtos.add(campaign.getCampaignDto());
        }

        return campaignDtos;

    }

    @ResponseBody
    @RequestMapping(value = "/service/campaigns/{campaignId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CampaignDto getCampaign(@PathVariable Long campaignId) throws Exception {

        Campaign campaign = campaignService.getCampaign(campaignId);

        if (campaign == null) {
            throw new Exception("A campaign with this ID doesn't exist!");
        }

        return campaign.getCampaignDto();

    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/service/campaigns/{campaignId}/campaignMessages")
    public Collection<CampaignMessageDto> setMessagesForCampaign(@RequestBody List<CampaignMessageDto> campaignMessages, @PathVariable Long campaignId, HttpServletResponse response) throws Exception {

        for(CampaignMessageDto campaignMessage : campaignMessages){
            DateFormat formatter = new SimpleDateFormat("hh:mm");
            try {
                Date date = (Date)formatter.parse(campaignMessage.getMessageTimeOfDay());
            } catch (ParseException e) {
                throw new Exception("An error occurred. Message times must be in the format hh:mm.");
            }
        }

        List<CampaignMessage> campaignMessagesReturned = campaignService.setMessagesForCampaign(campaignId,campaignMessages);
        Collection<CampaignMessageDto> campaignMessageDtos = new ArrayList<>();

        for (CampaignMessage campaignMessage : campaignMessagesReturned) {
            campaignMessageDtos.add(campaignMessage.getCampaignMessageDto());
        }

        response.setStatus(HttpServletResponse.SC_CREATED);

        return campaignMessageDtos;

    }

    @ResponseBody
    @RequestMapping(value = "/service/campaigns/{campaignId}/campaignMessages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<CampaignMessageDto> getCampaignMessages(@PathVariable Long campaignId) {

        List<CampaignMessage> campaignMessages = campaignMessageService.findMessagesInCampaign(campaignId);

        Collection<CampaignMessageDto> campaignMessageDtos = new ArrayList<>();

        for (CampaignMessage campaignMessage : campaignMessages) {
            campaignMessageDtos.add(campaignMessage.getCampaignMessageDto());
        }

        return campaignMessageDtos;

    }

}
