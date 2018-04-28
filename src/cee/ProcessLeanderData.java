package cee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

public class ProcessLeanderData extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String TMP_DIR_PATH = "runtime/upload";
    private File tmpDir;
    private static final String DESTINATION_DIR_PATH = "runtime/csv";
    private File destinationDir;
    private static String realPath = null;
    private static String fileLocation = "";
    private static String csvFile = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        String tempDir = getServletContext().getRealPath(TMP_DIR_PATH);
        tmpDir = new File(tempDir);
        if (!tmpDir.isDirectory()) {
            throw new ServletException(TMP_DIR_PATH + " is not a directory");
        }
        realPath = getServletContext().getRealPath(DESTINATION_DIR_PATH);
        destinationDir = new File(realPath);
        if (!destinationDir.isDirectory()) {
            throw new ServletException(DESTINATION_DIR_PATH + " is not a directory");
        }

        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        /*
         * Set the size threshold, above which content will be stored on disk.
         */
        fileItemFactory.setSizeThreshold(1 * 1024 * 1024 * 1024); // 1 MB
        /*
         * Set the temporary directory to store the uploaded files of size above
         * threshold.
         */
        fileItemFactory.setRepository(tmpDir);

        ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
        try {
            /*
             * Parse the request
             */
            @SuppressWarnings("unchecked")
            List<FileItem> items = uploadHandler.parseRequest(request);
            for (FileItem item : items) {
                /*
                 * Handle Form Fields.
                 */
                if (item.isFormField()) {
                    Logger.getLogger(this.getClass()).log(Priority.INFO, "File Name = " + item.getFieldName() + ", Value = " + item.getString());
                } else {
                    // Handle Uploaded files.
                    Logger.getLogger(this.getClass()).log(Priority.INFO, "Field Name = " + item.getFieldName() + ", File Name = " + item.getName() + ", Content type = "
                            + item.getContentType() + ", File Size = " + item.getSize());
                    /*
                     * Write file to the ultimate location.
                     */
                    Logger.getLogger(this.getClass()).log(Priority.INFO, "Write file to the ultimate location.");
                    File file = new File(tmpDir, item.getName());
                    // Window
                    fileLocation = tempDir + "\\" + item.getName();
                    Logger.getLogger(this.getClass()).log(Priority.INFO, "fileLocation.." + fileLocation);
                    // linux
                    //fileLocation = tempDir + "/" + item.getName();
                    item.write(file);
                    Logger.getLogger(this.getClass()).log(Priority.INFO, "Done..");
                }
            }
        } catch (FileUploadException ex) {
            Logger.getLogger(this.getClass()).error("Error encountered while parsing the request", ex);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass()).error("Error encountered while uploading file", ex);
        }


        String outputFile = createExcelSheetForBunkerVillage(fileLocation, csvFile);
        if ("error".equals(outputFile)) {
            outputFile = "Error Processing input file";
        }
        request.setAttribute("succesMessage", "File has been Processed! Please proceed to download");
        request.setAttribute("genCsvFileLoc", outputFile);
        RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }

    private static String createExcelSheetForBunkerVillage(String originalFilePath, String filteredFilePath) {
        fileLocation = originalFilePath;
        BufferedReader bf = null;
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd_HHmmss");
        String dateNow = "_Leander_" + formatter.format(currentDate.getTime()).toString() + ".csv";
        String filePath = DESTINATION_DIR_PATH + "/" + dateNow;
        if (filteredFilePath.isEmpty()) {
            csvFile = realPath + "\\" + dateNow;
        } else {
            csvFile = filteredFilePath;
        }

        File f = new File(csvFile);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                 Logger.getLogger(ProcessLeanderData.class).error("", e);
            }
        }
        ICsvMapWriter excelSheetWriter = null;

        try {
            excelSheetWriter = new CsvMapWriter(new FileWriter(csvFile), CsvPreference.EXCEL_PREFERENCE);
            final String[] header = new String[]{"upload date", "state", "county", "permit number", "permit description", "permit type", "permit date",
                "job value", "job square feet", "job address", "job city", "job state", "job zip", "job subdivision", "job lot number", "owner",
                "owner address", "owner city", "owner state", "owner zip", "owner phone", "owner type", "owner url", "owner fax",
                "owner primary contact", "owner email", "bldcode", "units", "buildings", "ctype", "legal", "contractor", "contractor address",
                "contractor city", "contractor state", "contractor zip", "contractor phone", "contractor url", "contractor fax",
                "contractor primary contact", "contractor email", "contractor last activity", "contractor status"};
            // Write Excel sheet Header
            excelSheetWriter.writeHeader(header);

            // Read the content of ocr generated xml file
            bf = new BufferedReader(new InputStreamReader(new FileInputStream(fileLocation)));

            int count = 0;
            Map<String, String> data = new HashMap<String, String>();

            String ch = null;
            StringBuilder sb = new StringBuilder();
            boolean foundProjectNo = false;
            while ((ch = bf.readLine()) != null) {
                if (ch.startsWith("<TH>PROJECT:") || ch.startsWith("<TD>PROJECT:") || ch.startsWith("<Annot>PROJECT:") || ch.startsWith("PROJECT:") || ch.startsWith("<P>PROJECT:")) {
                    foundProjectNo = true;
                    if (count != 0) {
                        sb.append("\n");
                    }
                }
                if (foundProjectNo) {
                    sb.append(ch);
                }
                count++;
            }
            bf.close();
            String nohtml = sb.toString().replaceAll("\\<.*?>", "");
            nohtml = nohtml.replace(" - ", " ");
            String[] recordLines = nohtml.split("\n");
            String fetchedRecord = "";
            for (String recordLine : recordLines) {
                if (recordLine == null) {
                    continue;
                }
                String contractorAddress = "";
                String projectNo = StringUtils.substringBetween(recordLine, "PROJECT: ", " ");
                String permitType = StringUtils.substringBetween(recordLine, projectNo, ":");
                String issueDate = StringUtils.substringBetween(recordLine, "ISSUED DATE: ", " ");
                String contractor = StringUtils.substringBetween(recordLine, "CONTRACTOR: ", "SQUARE FEET:");
                String issuedTo = StringUtils.substringBetween(recordLine, "ISSUED TO:", "SQUARE FEET:");
                String squareFeet = StringUtils.substringBetween(recordLine, "SQUARE FEET: ", " ");
                String valuation = StringUtils.substringBetween(recordLine, "VALUATION: ", " ");
                if (issueDate != null) {
                    issueDate = issueDate.replace("CONTRACTOR:", "");
                }
                if (StringUtils.substringBetween(contractor, " ", "ISSUED TO:") != null) {
                    contractor = StringUtils.substringBetween(contractor, " ", "ISSUED TO:");
                    if (contractor != null) {
                        contractorAddress = StringUtils.substringBetween(contractor, "ISSUED TO:", " ");
                    }
                }

                fetchedRecord = fetchedRecord + "<>" + projectNo + "<>" + permitType + "<> " + issueDate
                        + "<> " + contractor + " <>" + issuedTo + " <>" + squareFeet + " -> " + valuation;
                fetchedRecord = fetchedRecord + "\n";
                if ((projectNo != null) && (valuation != null)) {
                    data.put("upload date", "");
                    data.put("state", "");
                    data.put("county", "");
                    data.put("permit number", projectNo);
                    data.put("permit description", "");
                    data.put("permit type", "");
                    if (permitType != null) {
                        data.put("permit type", permitType);
                    }
                    data.put("permit date", "");
                    if (issueDate != null) {
                        data.put("permit date", issueDate);
                    }
                    data.put("job value", "");
                    if (valuation != null) {
                        data.put("job value", valuation.replace("-", ""));
                    }
                    data.put("job square feet", "");
                    if (squareFeet != null) {
                        data.put("job square feet", squareFeet.replace("DWELLING", ""));
                    }
                    data.put("job address", "");
                    if (issuedTo != null) {
                        data.put("job address", issuedTo);
                    }
                    data.put("job city", "");
                    data.put("job state", "");
                    data.put("job zip", "");
                    data.put("job subdivision", "");
                    data.put("job lot number", "");
                    data.put("owner", "");
                    data.put("owner address", "");
                    data.put("owner city", "");
                    data.put("owner state", "");
                    data.put("owner zip", "");
                    data.put("owner phone", "");
                    data.put("owner type", "");
                    data.put("owner url", "");
                    data.put("owner fax", "");
                    data.put("owner primary contact", "");
                    data.put("owner email", "");
                    data.put("bldcode", "");
                    data.put("units", "");
                    data.put("buildings", "");
                    data.put("ctype", "");
                    data.put("legal", "");
                    data.put("contractor", "");
                    if (contractor != null) {
                        data.put("contractor", contractor);
                    }
                    data.put("contractor address", "");
                    if (contractorAddress != null) {
                        data.put("contractor address", contractorAddress);
                    }
                    data.put("contractor city", "");
                    data.put("contractor state", "");
                    data.put("contractor zip", "");
                    data.put("contractor phone", "");
                    data.put("contractor url", "");
                    data.put("contractor fax", "");
                    data.put("contractor primary contact", "");
                    data.put("contractor email", "");
                    data.put("contractor last activity", "");
                    data.put("contractor status", "");
                    excelSheetWriter.write(data, header);
                    count++;
                }

            }

            excelSheetWriter.close();
            Logger.getLogger(ProcessLeanderData.class).log(Priority.INFO, "Process Records : " + count);

            bf.close();
            Logger.getLogger(ProcessLeanderData.class).log(Priority.INFO, "Successfully Processed the Pdf file");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
                        Logger.getLogger(ProcessLeanderData.class).error("", e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
                        Logger.getLogger(ProcessLeanderData.class).error("", e);
        }

            Logger.getLogger(ProcessLeanderData.class).log(Priority.INFO, "\n\n==csvFile==" + csvFile);
        csvFile = "";
        return filePath;
    }
}
