package cee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

public class ProcessSugarLandData extends HttpServlet {

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


        String outputFile = createExcelSheetForHouston(fileLocation, csvFile);
        if ("error".equals(outputFile)) {
            outputFile = "Error Processing input file";
        }
        request.setAttribute("succesMessage", "File has been Processed! Please proceed to download");
        request.setAttribute("genCsvFileLoc", outputFile);
        RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }

    private static String createExcelSheetForHouston(String originalFilePath, String filteredFilePath) {
        fileLocation = originalFilePath;
        BufferedReader bf = null;
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd_HHmmss");
        String dateNow = formatter.format(currentDate.getTime()).toString() + "_SugarLand" + ".csv";
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
                e.printStackTrace();
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
            int countNoOfRecordFetched = 0;

            String ch = null;
            String[] recordWithoutHtmlTags = null;
            StringBuilder sb = new StringBuilder();
            StringBuilder sb3 = new StringBuilder();
            StringBuilder sb1 = new StringBuilder();
            while ((ch = bf.readLine()) != null) {
                sb.append(ch + "\n");
            }
            String nohtml = sb.toString().replaceAll("\\<TD.*?>", "\"");
            nohtml = nohtml.replaceAll("\\</TD>", "\"");
            nohtml = nohtml.replaceAll("\\<TR>", "");
            nohtml = nohtml.replaceAll("\\</TR>", "");
            nohtml = nohtml.replaceAll("\"", "");
            nohtml = sb.toString().replaceAll("\\<.*?>", "");

            recordWithoutHtmlTags = nohtml.split("\n");
            int count = 0;
            for (String record : recordWithoutHtmlTags) {
                if (record.startsWith("11-")) {
                    record = record.replace("CM11 COM", "");
                    sb1.append("\n" + record);
                } else {
                    record = record.replace("PERMIT TYPE", "");
                    sb1.append(record.replace("\n", " "));
                }

            }
            String removeUnwantedWords = sb1.toString().replace("CM04 COM, COMMERCIAL ADDITION", "");
            removeUnwantedWords = removeUnwantedWords.replace("CM02 COM, COMMERCIAL BUILDOUT", "");
            removeUnwantedWords = removeUnwantedWords.replace("CM11 COM, COMMERCIAL NEW -OFFICES, BANKS, ETC.", "");
            removeUnwantedWords = removeUnwantedWords.replace("CM03 COM, COMMERCIAL REMODEL", "");
            removeUnwantedWords = removeUnwantedWords.replace("(CONTINUED)", "");
            removeUnwantedWords = removeUnwantedWords.replace("----------CM03 COM, COMMERCIAL", "");
            removeUnwantedWords = removeUnwantedWords.replace("RS02 RES, SINGLE FAMILY, ADDITION", "");
            removeUnwantedWords = removeUnwantedWords.replace("RS01 RES, SINGLE", "");
            removeUnwantedWords = removeUnwantedWords.replace("FAMILY, NEW", "");
            removeUnwantedWords = removeUnwantedWords.replace("RS03 RES, SINGLE FAMILY, REMODEL", "");
            removeUnwantedWords = removeUnwantedWords.replace("RSI0 RES, STRUCTURE", "");
            removeUnwantedWords = removeUnwantedWords.replace("RS10 RES, STRUCTURE", "");
            removeUnwantedWords = removeUnwantedWords.replace("STRUCTURE -000 000 ISSUE DATE VALUATION SUBCONTRACTOR(S)  -BLDG 00 BUILDING PERMIT", "");
            removeUnwantedWords = removeUnwantedWords.replace("STRUCTURE -000 000  -BLDG 00 BUILDING PERMIT", "");
            removeUnwantedWords = removeUnwantedWords.replace("STRUCTURE -000 000 ISSUE DATE VALUATION  -BLDG 00 BUILDING PERMIT", "");
            removeUnwantedWords = removeUnwantedWords.replace("ISSUE DATE", "");
            removeUnwantedWords = removeUnwantedWords.replace("TOTAL PERMITS ISSUED", "");
            removeUnwantedWords = removeUnwantedWords.replace("TOTAL", "");
            removeUnwantedWords = removeUnwantedWords.replace("PERMITS", "");
            removeUnwantedWords = removeUnwantedWords.replace("HELD", "");
            removeUnwantedWords = removeUnwantedWords.replace("VALUATION", "");
            removeUnwantedWords = removeUnwantedWords.replace("PROPERTY OWNER", "");
            removeUnwantedWords = removeUnwantedWords.replace("SUBCONTRACTOR(S)", "");
            removeUnwantedWords = removeUnwantedWords.replace("STRUCTURE -000 000   SUBCONTRACTOR(S}  -BLDG 00 BUILDING PERMIT", "");
            removeUnwantedWords = removeUnwantedWords.replace("-----------", "");
            removeUnwantedWords = removeUnwantedWords.replace("----------", "");
            removeUnwantedWords = removeUnwantedWords.replace("STRUCTURE PERMIT -000 000 TYPE -BLDG 00 BUILDING PERMIT", "");


            int countNoOfBuildingRecord = 0;
            List<String> finalFilteredReocords = new ArrayList<String>();

            String[] lineRecords = removeUnwantedWords.split("\n");
            for (int countLine = 1; countLine < lineRecords.length - 1; countLine++) {
                //System.out.println(lineRecords[countLine]);
                Map<String, String> fileterRecordMap = new HashMap<String, String>();
                Map<String, String> data = new HashMap<String, String>();

                data.put("upload date", "");
                data.put("state", "");
                data.put("county", "");
                data.put("job address", "");
                data.put("permit description", "");
                data.put("permit type", "");
                data.put("job address", "");
                data.put("permit number", "");
                data.put("permit date", "");
                data.put("job value", "");
                data.put("owner", "");
                data.put("job square feet", "");
                data.put("job square feet", "");

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
                data.put("contractor address", "");
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


                String[] splitLines = lineRecords[countLine].split(" ");
                String address = "";
                int countDateOccurace = 1;
                if (splitLines.length >= 7) {
                    for (int i = 2; i < 7; i++) {
                        address = address + " " + splitLines[i];
                    }
                }
                fileterRecordMap.put("address", address);
                data.put("job address", address);
                for (int i = 1; i < splitLines.length; i++) {
                    //System.out.println(splitLines[i]);
                    if (splitLines[0] != "" || splitLines[0] != null) {
                        fileterRecordMap.put("permitNo", splitLines[0]);
                        data.put("permit number", splitLines[0]);
                    }

                    if (splitLines[i].contains("/")) {
                        if (countDateOccurace == 2) {
                            fileterRecordMap.put("date", splitLines[i]);
                            data.put("permit date", splitLines[i]);
                            if ((countLine + 1) < lineRecords.length && (lineRecords[countLine].startsWith(splitLines[0]) == lineRecords[countLine + 1].startsWith(splitLines[0]))) {
                                String[] splitNextLine = lineRecords[countLine + 1].split(" ");
                                for (int countNextSplits = 0; countNextSplits < splitNextLine.length; countNextSplits++) {
                                    if (splitNextLine[countNextSplits].contains("/")) {
                                        fileterRecordMap.put("date", splitNextLine[countNextSplits]);
                                        data.put("permit date", splitNextLine[countNextSplits]);
                                        if (countNextSplits + 1 < splitNextLine.length) {
                                            fileterRecordMap.put("valuation", splitNextLine[countNextSplits + 1]);
                                            data.put("job value", splitNextLine[countNextSplits + 1]);
                                        }
                                        if (countNextSplits + 2 < splitNextLine.length) {
                                            fileterRecordMap.put("company", splitNextLine[countNextSplits + 2]);
                                            data.put("owner", splitNextLine[countNextSplits + 2]);
                                        }
                                    }
                                }

                            } else if ((i + 1) <= splitLines.length) {
                                fileterRecordMap.put("valuation", splitLines[i + 1]);
                                data.put("job value", splitLines[i + 1]);
                            }
                            String company = "";

                            for (int j = (i + 2); j < splitLines.length; j++) {
                                company = company + " " + splitLines[j];
                                fileterRecordMap.put("company", company.replace(",", ""));
                                data.put("owner", company.replace(",", ""));
                            }

                        }
                        countDateOccurace++;
                    }
                    if (data.get("job address") == null) {
                        data.put("job address", "");
                    }
                    if (data.get("permit number") == null) {
                        data.put("permit number", "");
                    }
                    if (data.get("permit date") == null) {
                        data.put("permit date", "");
                    }
                    if (data.get("job value") == null) {
                        data.put("job value", "");
                    }
                    if (data.get("owner") == null) {
                        data.put("owner", "");
                    }
                    if (!data.containsKey("permit number")) {
                        Logger.getLogger(ProcessSugarLandData.class).log(Priority.INFO, "Not found PermitNubmer");
                        data.put("permit number", "");
                    }
                    if (!data.containsKey("job address")) {
                        data.put("job address", "");
                    }
                    if (!data.containsKey("permit date")) {
                        data.put("permit date", "");
                    }

                    if (!data.containsKey("job value")) {
                        data.put("job value", "");
                    }
                    if (!data.containsKey("owner")) {
                        data.put("owner", "");
                    }

                }
                excelSheetWriter.write(data, header);


            }
            excelSheetWriter.close();
            Logger.getLogger(ProcessSugarLandData.class).log(Priority.INFO, "" + count);

            bf.close();
            csvFile = "";
            Logger.getLogger(ProcessSugarLandData.class).log(Priority.INFO, "Successfully Processed the Pdf file");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Logger.getLogger(ProcessSugarLandData.class).error("", e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Logger.getLogger(ProcessSugarLandData.class).error("", e);
        }

        Logger.getLogger(ProcessSugarLandData.class).log(Priority.INFO, "\n\n==csvFile==" + csvFile);
        return filePath;
    }
}
