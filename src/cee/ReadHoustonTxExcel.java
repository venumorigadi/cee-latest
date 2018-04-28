package cee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

public class ReadHoustonTxExcel extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String TMP_DIR_PATH = "runtime/upload";
    private File tmpDir;
    private static final String DESTINATION_DIR_PATH = "runtime/csv";
    private File destinationDir;
    private static String realPath = null;
    private static String fileLocation = "";
    private static String csvFile = null;

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
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd_HHmmss");
        String dateNow = formatter.format(currentDate.getTime()).toString() + ".csv";
        String filePath = DESTINATION_DIR_PATH + "/" + dateNow;
        if (filteredFilePath == null) {
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
                Logger.getLogger(ReadHoustonTxExcel.class).error("", e);
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
            Workbook excelBook = Workbook.getWorkbook(new File(fileLocation));
            Sheet excelSheet = excelBook.getSheet(0);

            int countNoOfRecordFetched = 0;
            for (int row = 1; row < excelSheet.getRows(); row++) {
                Cell[] cellDatas = excelSheet.getRow(row);
                Cell permitType = cellDatas[1];
                Cell VALUATATION_CURR = cellDatas[5];
                double valuation = new Double(VALUATATION_CURR.getContents());

                if (("Building Pmt".equals(permitType.getContents().trim())
                        || ("Pool".equals(permitType.getContents().trim())) && valuation > 30000.00)) {
                    Logger.getLogger(ReadHoustonTxExcel.class).log(Priority.INFO, "Fetching Record for permit number " + cellDatas[0].getContents());
                    Map<String, String> detailsOfProject = getProjectDetail(cellDatas[0].getContents());
                    Map<String, String> data = new HashMap<String, String>();

                    data.put("upload date", "");
                    data.put("state", "TX");
                    data.put("county", "Harris");
                    data.put("permit number", cellDatas[0].getContents());
                    data.put("permit description", "");
                    if (detailsOfProject.get("USE") != null) {
                        data.put("permit description", detailsOfProject.get("USE").replace(",,", ""));
                    }
                    data.put("permit type", cellDatas[1].getContents());
                    if (detailsOfProject.get("Date") != null) {
                        data.put("permit date", detailsOfProject.get("Date").replace(",,", "").replace(" 00", ""));
                    } else {
                        data.put("permit date", "");
                    }
                    data.put("job value", cellDatas[5].getContents());
                    data.put("job square feet", "");
                    data.put("job square feet", "");
                    data.put("job address", "");
                    data.put("job city", "");
                    data.put("job state", "");
                    if (detailsOfProject.get("Address") != null) {
                        String[] jobAddress = detailsOfProject.get("Job Address").replace(",,", "").split(" ");
                        String add = "";
                        for (int i = 0; i < jobAddress.length - 2; i++) {
                            add = add + " " + jobAddress[i];
                        }
                        data.put("job address", add.trim());
                        String jobZip = jobAddress[jobAddress.length - 1];
                        data.put("job zip", jobZip);
                    } else {
                        data.put("job zip", "");
                    }
                    data.put("job subdivision", "");
                    data.put("job lot number", "");
                    if (detailsOfProject.get("Owner/Occupant") != null) {
                        String rawName = detailsOfProject.get("Owner/Occupant").replace(",,", "").replace("*", "");
                        String[] fullName = rawName.split(" ");
                        String name = "";
                        if (fullName.length == 2) {
                            name = fullName[1] + " " + fullName[0];
                        } else if ((fullName.length == 4 && rawName.contains("and"))) {
                            name = fullName[1] + " " + fullName[2] + " " + fullName[3] + " " + fullName[0];
                        } else if ((fullName.length == 3 && (fullName[2].contains(".") || fullName[2].length() == 1))) {
                            name = fullName[1] + " " + fullName[2] + " " + fullName[0];
                        } else {
                            name = rawName;
                        }

                        data.put("owner", name);
                    } else {
                        data.put("owner", "");
                    }

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
                    if (detailsOfProject.get("Buyer") != null) {
                        data.put("contractor", detailsOfProject.get("Buyer").replace(",,", "").replace("*", ""));
                    }
                    data.put("contractor address", "");
                    data.put("contractor city", "");
                    data.put("contractor state", "");
                    data.put("contractor zip", "");
                    data.put("contractor phone", "");
                    if (detailsOfProject.get("Address") != null) {
                        String[] jobAddress = detailsOfProject.get("Address").replace(",,", "").split(" ");
                        String add = "";
                        for (int i = 0; i < jobAddress.length - 2; i++) {
                            add = add + " " + jobAddress[i];
                        }
                        data.put("contractor address", add.trim());
                        String jobZip = jobAddress[jobAddress.length - 1];
                        data.put("contractor zip", jobZip);
                    }
                    if (detailsOfProject.get("Phone") != null) {
                        data.put("contractor phone", detailsOfProject.get("Phone").replace(",,", ""));
                    }
                    data.put("contractor url", "");
                    data.put("contractor fax", "");
                    data.put("contractor primary contact", "");
                    data.put("contractor email", "");
                    data.put("contractor last activity", "");
                    data.put("contractor status", "");
                    countNoOfRecordFetched++;
                    //System.out.println ("Record Fetched for permit number " + cellDatas[0].getContents());
                    excelSheetWriter.write(data, header);
                }

            }
            Logger.getLogger(ReadHoustonTxExcel.class).log(Priority.INFO, "No Of Total Record Fetched : " + countNoOfRecordFetched);

            excelBook.close();
            excelSheetWriter.close();
            return csvFile;
        } catch (BiffException e) {
            //e.printStackTrace();
            try {
                excelSheetWriter.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                Logger.getLogger(ReadHoustonTxExcel.class).error("", e1);
            }
            Logger.getLogger(ReadHoustonTxExcel.class).log(Priority.INFO, "Input excel sheet is in bad format. Trying to fix!!");
            String result = FixCorruptedExcelFile.fixCorruptedExcelFileForHouston(originalFilePath, csvFile);
            if ("success".equals(result)) {
                createExcelSheetForHouston(originalFilePath, csvFile);
            }
        } catch (IOException e) {
            Logger.getLogger(ReadHoustonTxExcel.class).error("", e);
            return "error";
        }

        Logger.getLogger(ReadHoustonTxExcel.class).log(Priority.INFO, "\n\n==csvFile==" + csvFile);
        csvFile = null;
        return filePath;
    }

    public static Map<String, String> getProjectDetail(String projectNo) {
        //projectNo = "11031097";
        Map<String, String> detailsReceived = new HashMap<String, String>();
        try {
            URL oracle = new URL("http://www.cohtora.houstontx.gov/ibi_apps/WFServlet?IBIF_webapp=/ibi_apps&IBIC_server=EDASERVE&IBIWF_msgviewer=OFF&IBIAPP_app=soldpermits&IBIF_ex=sold_permit_d&CLICKED_ON=&PN=" + projectNo + "&PT=13");
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            StringBuilder sb = new StringBuilder();
            String[] noHtmlData = null;
            while ((inputLine = in.readLine()) != null) {

                //System.out.println(inputLine);
                sb.append(inputLine);
            }
            String nohtml = sb.toString().replaceAll("\\<.*?>", ",");
            noHtmlData = nohtml.split(",,,,");

            //System.out.println(nohtml);
            String[] splitDownInToMap = null;
            for (String noHtmlDat : noHtmlData) {
                // System.out.println(noHtmlDat);

                if (noHtmlDat.contains(":")) {
                    splitDownInToMap = noHtmlDat.split(":");
                    detailsReceived.put(splitDownInToMap[0].trim(), splitDownInToMap[1].trim());
                }

            }


            in.close();

        } catch (IOException e) {
            Logger.getLogger(ReadHoustonTxExcel.class).error("", e);
        }
        return detailsReceived;
    }
}
