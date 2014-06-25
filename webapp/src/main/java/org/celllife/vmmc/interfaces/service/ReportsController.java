package org.celllife.vmmc.interfaces.service;

import org.celllife.ivr.application.utils.FindMessageStatusesProcedure;
import org.celllife.pconfig.model.FileType;
import org.celllife.pconfig.model.Pconfig;
import org.celllife.reporting.service.PconfigParameterHtmlService;
import org.celllife.reporting.service.ReportService;
import org.celllife.reporting.service.impl.PconfigParameterHtmlServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Controller
public class ReportsController {

    @Autowired
    private ReportService reportService;

    @Autowired
    FindMessageStatusesProcedure findMessageStatusesProcedure;

    private PconfigParameterHtmlService pconfigParameterHtmlService = new PconfigParameterHtmlServiceImpl();

    //private static Logger log = LoggerFactory.getLogger(ReportsController.class);

    @ResponseBody
    @RequestMapping(
            value = "/service/reports",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Collection<Pconfig> getReports() {
        return reportService.getReports();
    }

    @ResponseBody
    @RequestMapping(value = "/service/getHtml", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getHtmlForReport(@RequestParam("reportId") String reportId) throws Exception {
        Pconfig pconfig;
        try {
            pconfig = reportService.getReportByName(reportId);
        } catch (Exception e) {
            return "No such Report.";
        }
        String htmlString = pconfigParameterHtmlService.createHtmlFieldsFromPconfig(pconfig, "submitButton");
        return htmlString;
    }

    @ResponseBody
    @RequestMapping(value = "/service/downloadCSVReport", method = RequestMethod.GET)
    public void downloadCSVReport(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //run the MySQL stored procedure FindMessageStatusesProcedure()
        Map<String, Object> parameters = new HashMap<String, Object>();
        Map<String, Object> results = findMessageStatusesProcedure.execute(parameters);
        List<LinkedCaseInsensitiveMap<String>> rows = (List<LinkedCaseInsensitiveMap<String>>) results.get("#result-set-1");
        Integer numberOfMessages = rows.get(0).size() - 1;

        response.setContentType("application/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"msisdnReport" + new Date() + ".csv\"");
        OutputStream responseOutputStream = response.getOutputStream();

          //write the headers
        responseOutputStream.write("msisdn".getBytes());
        responseOutputStream.write(',');
        Integer messageCount = 1;
        while (messageCount <= numberOfMessages) {
            responseOutputStream.write(("message" + messageCount).getBytes());
            responseOutputStream.write(',');
            messageCount = messageCount + 1;
        }
        responseOutputStream.write("\r\n".getBytes());

        //write the data
        for (LinkedCaseInsensitiveMap<String> row : rows) {
            responseOutputStream.write(row.get("msisdn").getBytes());
            responseOutputStream.write(',');
            messageCount = 1;
            while (messageCount <= numberOfMessages) {
                responseOutputStream.write(row.get("message" + messageCount).getBytes());
                responseOutputStream.write(',');
                messageCount = messageCount + 1;
            }
            responseOutputStream.write("\r\n".getBytes());
        }

    }

    @ResponseBody
    @RequestMapping(value = "/service/downloadReport", method = RequestMethod.GET)
    public void downloadReport(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String reportId = request.getParameter("reportId");

        if (reportId.isEmpty()) {
            throw new RuntimeException("Could not retrieve a report with an empty reportId.");
        } else {

            Pconfig pconfig = reportService.getReportByName(reportId);
            FileType fileType = FileType.PDF;
            String fileExtension = "pdf";

            switch (pconfig.getParameter("file_type").getValue().toString().toLowerCase()) {
                case "csv":
                    fileType = FileType.CSV;
                    fileExtension = "csv";
                    break;
                case "pdf":
                    fileType = FileType.PDF;
                    fileExtension = "pdf";
                    break;
                default:
                    fileType = FileType.PDF;
                    fileExtension = "pdf";
                    break;
            }

            Pconfig returnedPconfig = pconfigParameterHtmlService.createPconfigFromHtmlFormSubmission(
                    request.getParameterNames(), request.getParameterMap(), pconfig);

            String generatedReport = null;
            File reportFile = null;
            try {
                generatedReport = reportService.generateReport(returnedPconfig, fileType);
                reportFile = reportService.getGeneratedReportFile(generatedReport);
            } catch (Exception e) {
                throw new RuntimeException("Could not retrieve report with reportId '" + reportId + "'.", e);
            }
            if (reportFile == null) {
                throw new RuntimeException("Could not retrieve report with reportId '" + reportId + "'.");
            }

            response.setContentType("application/" + fileExtension);
            response.setHeader("Content-Disposition", "attachment; filename=\"report-" + generatedReport + "." + fileExtension + "\"");
            try {
                FileInputStream fileInputStream = new FileInputStream(reportFile);
                OutputStream responseOutputStream = response.getOutputStream();
                int bytes;
                while ((bytes = fileInputStream.read()) != -1) {
                    responseOutputStream.write(bytes);
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not create file for report with reportId '" + reportId + "'.", e);
            }

        }
    }

}