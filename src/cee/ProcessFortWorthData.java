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

public class ProcessFortWorthData extends HttpServlet {

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
            Logger.getLogger(this.getClass()).error("Error encountered while parsing the request", ex);
        }


        String outputFile = createExcelSheetForFortWorth(fileLocation, csvFile);
        if ("error".equals(outputFile)) {
            outputFile = "Error Processing input file";
        }
        request.setAttribute("succesMessage", "File has been Processed! Please proceed to download");
        request.setAttribute("genCsvFileLoc", outputFile);
        RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }

    private static String createExcelSheetForFortWorth(String originalFilePath, String filteredFilePath) {
        fileLocation = originalFilePath;
        BufferedReader bf = null;
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd_HHmmss");
        String dateNow = formatter.format(currentDate.getTime()).toString() + "_FortWorth" + ".csv";
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
                Logger.getLogger(ProcessFortWorthData.class).error("", e);
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

            // Read the content of downloaded Excel Sheet
            bf = new BufferedReader(new InputStreamReader(new FileInputStream(fileLocation)));

            String ch = null;
            String[] recordWithoutHtmlTags = null;
            StringBuilder sb = new StringBuilder();

            boolean trStarted = false;
            int i = 0;
            while ((ch = bf.readLine()) != null) {
                if (ch.contains("Permit:")) {
                    i = 1;
                    sb.append("\n[").append(i).append("]").append(ch);
                    //	System.out.print("\n["+i+"]" + ch);
                } else {
                    i++;
                    sb.append("[").append(i).append("]").append(ch);
                    //	System.out.print("["+i+"]"+ch);
                }
            }
            recordWithoutHtmlTags = sb.toString().split("\n");

            for (String split : recordWithoutHtmlTags) {
                split = split.replace("N = New, A = Addition, R = Remodel Res = Y, Comm = N", "");
                String permitNo = "";
                String jobValue = "";
                String permitDate = "";
                String jobAddress = "";
                String permitType = "";
                String contractorNameAndAddress = "";
                String contractorName = "";
                String contractorAddress = "";
                String contractorStreet = "";
                String zip = "";
                String phone = "";
                String permitDescription = "";
                String jobSqFt = "";
                String workDescription = "";
                String legalDescription = "";
                if (split.startsWith("[1]Permit:")) {
                    if (split.contains("[4]Work Description")) {
                        System.out.println(split);

                        permitNo = StringUtils.substringBetween(split, "]PB", " Value").replaceAll("\\[.*?]", "");
                        jobValue = StringUtils.substringBetween(split, "Value: ", "Issued: ").replaceAll("\\[.*?]", "");

                        permitDate = StringUtils.substringBetween(split, "Issued: ", " [").replaceAll("\\[.*?]", "");

                        String issuedDate = "Issued: " + permitDate;
                        jobAddress = StringUtils.substringBetween(split, issuedDate, " Type:");
                        if (jobAddress != null) {
                            jobAddress = jobAddress.replaceAll("\\[.*?]", "");
                        }
                        permitType = StringUtils.substringBetween(split, " Type:", "Res?:").replaceAll("\\[.*?]", "");

                        if (!split.contains("[9]Res?") && !split.contains("[8]Res?:")) {
                            contractorNameAndAddress = StringUtils.substringBetween(split, "[8]", "[9]").replaceAll("\\[.*?]", "");

                        }
                        if (split.contains("[8]Res?:") && (contractorNameAndAddress != "" || contractorNameAndAddress != null)) {
                            contractorNameAndAddress = StringUtils.substringBetween(split, "[9]", "[10]").replaceAll("\\[.*?]", "");
                            ;
                        }
                        if (split.contains("[9]Res?") && (contractorNameAndAddress != "" || contractorNameAndAddress != null)) {
                            contractorNameAndAddress = StringUtils.substringBetween(split, "[10]", "[11]").replaceAll("\\[.*?]", "");
                            ;

                        }
                        String[] address = contractorNameAndAddress.split("\\d+");
                        if (address.length > 0) {
                            contractorName = address[0];
                            contractorAddress = StringUtils.substringBetween(split, address[0], "TX");
                        }
                        String[] addressZip = contractorNameAndAddress.split("\\D+");
                        if (addressZip.length > 2 && addressZip[2] != null) {
                            zip = addressZip[2];
                        }
                        String[] jobSqFts = split.split(" ");
                        jobSqFt = jobSqFts[jobSqFts.length - 2].replaceAll("\\[.*?]", "");
                        if (!isNumeric(jobSqFt)) {
                            jobSqFt = "0";
                        }
                        if (split.contains("[11]N,A,R:")) {
                            workDescription = StringUtils.substringBetween(split, "[9]", "[10]");
                            legalDescription = StringUtils.substringBetween(split, "[10]", "[11]");
                        }
                        if (split.contains("[12]N,A,R:")) {
                            workDescription = StringUtils.substringBetween(split, "[9]", "[11]");
                            legalDescription = StringUtils.substringBetween(split, "[11]", "[12]");
                        }
                        if (split.contains("[13]N,A,R:")) {
                            workDescription = StringUtils.substringBetween(split, "[11]", "[12]");
                            legalDescription = StringUtils.substringBetween(split, "[12]", "[13]");
                        }
                        if (issuedDate.contains("N,A,R:")) {
                            workDescription = StringUtils.substringBetween(split, "[11]", "[12]");
                            legalDescription = StringUtils.substringBetween(split, "[12]", "[13]");
                        }
                        if ("R ".equals(workDescription) || "N ".equals(workDescription) || workDescription == "") {
                            workDescription = StringUtils.substringBetween(split, "[9]", "[10]");
                            legalDescription = StringUtils.substringBetween(split, "[10]", "[11]");
                        }
                        //System.out.println(permitNo + " | " +permitType +" | "+ jobValue + " | "+permitDate + " | " +jobAddress + " | " + contractorName + " | "+ contractorAddress +" | " + zip + " | " + jobSqFt );
                    }
                }

                //System.out.println(lineRecords[countLine]);
                Map<String, String> data = new HashMap<String, String>();

                data.put("upload date", "");
                data.put("state", "");
                data.put("county", "");
                data.put("job address", "");
                data.put("permit description", "");
                if (workDescription != null) {
                    data.put("permit description", workDescription);
                }
                data.put("permit type", "");
                if (permitType != null) {
                    data.put("permit type", permitType);
                }

                data.put("permit number", "");
                if (permitNo != null) {
                    data.put("permit number", permitNo);
                }
                data.put("permit date", "");
                if (permitDate != null) {
                    data.put("permit date", permitDate.replace(" N,A,R:", ""));
                }
                data.put("job value", "");
                data.put("owner", "");
                data.put("job square feet", "");
                if (jobSqFt != null) {
                    data.put("job square feet", jobSqFt);
                }
                data.put("job city", "");
                data.put("job state", "");
                data.put("job zip", "");
                legalDescription = legalDescription != null ? legalDescription : "";
                data.put("job subdivision", legalDescription);
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
                data.put("contractor", contractorName);
                if (contractorName != null) {
                    data.put("contractor", contractorName);
                }
                data.put("contractor address", "");
                if (contractorAddress != null) {
                    data.put("contractor address", contractorAddress);
                }
                data.put("contractor city", "");
                data.put("contractor state", "TX");
                data.put("contractor zip", "");
                if (zip != null) {
                    data.put("contractor zip", zip);
                }
                data.put("contractor phone", "");
                //if (contractorPhone != null) data.put("contractor phone", contractorPhone);
                data.put("contractor url", "");
                data.put("contractor fax", "");
                data.put("contractor primary contact", "");
                data.put("contractor email", "");
                data.put("contractor last activity", "");
                data.put("contractor status", "");

                data.put("job value", "");
                data.put("owner", "");



                data.put("job address", jobAddress);

                //System.out.println(jobAddress);
                //
                //System.out.println("=="+split.trim());
                double jobValueD = 0d;

                jobValue = jobValue.replace("$", "").replace(",", "");
                //System.out.println(isNumeric(jobValue));
                if (isNumeric(jobValue)) {
                    String jobV = jobValue;
                    jobValueD = new Double(jobV);
                }
                data.put("job value", Double.toString(jobValueD));
                if (permitType != null) {
                    permitType = permitType.replace("\\s+", "");
                }
                //System.out.println(permitNo+" | "+jobValueD+" | " +permitType +" | "+"POOL ".equalsIgnoreCase(permitType));
                //System.out.println(data.get("job value"));
                if ((jobValueD > 30000 && !permitType.contains("Roof")) || ("POOL ".equalsIgnoreCase(permitType))) {
                    if (!data.get("permit number").equals("")) {
                        excelSheetWriter.write(data, header);
                    }
                }

            }
            excelSheetWriter.close();

            bf.close();
            csvFile = "";
            Logger.getLogger(ProcessFortWorthData.class).log(Priority.INFO, "Successfully Processed the Pdf file");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Logger.getLogger(ProcessFortWorthData.class).error("", e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Logger.getLogger(ProcessFortWorthData.class).error("", e);
        }

        //System.out.println("\n\n==csvFile==" + csvFile);
        return filePath;
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
