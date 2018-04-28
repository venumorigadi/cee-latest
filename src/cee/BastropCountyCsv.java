package cee;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

public class BastropCountyCsv extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String TMP_DIR_PATH = "runtime/upload";
    private File tmpDir;
    private static final String DESTINATION_DIR_PATH = "runtime/csv";
    private File destinationDir;
    private static String realPath = null;
    private static String fileLocation = "";
    private static String csvFile = null;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doPost(request, response);
    }

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
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMMdd_HHmmss");
        String dateNow = formatter.format(currentDate.getTime()).toString() + "_BastropCounty" + ".csv";
        String processedFileLocation = realPath + "/" + dateNow;
        csvFile = DESTINATION_DIR_PATH + "/" + dateNow;
        String result = processBastropCountyExcelSheet(fileLocation, processedFileLocation);
        if ("success".equals(result)) {
            request.setAttribute("succesMessage", "File has been Processed! Please proceed to download");
            request.setAttribute("genCsvFileLoc", csvFile);
        } else {
            request.setAttribute("errorMessage", result);
        }

        RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);

    }

    public static String processBastropCountyExcelSheet(String fileToProcessLocation, String processedFileLocation) {
        XSSFWorkbook wb = null;
        ICsvMapWriter writer = null;
        try {
            File fileToBeProcessed = new File(processedFileLocation);
            if (!fileToBeProcessed.exists()) {
                try {
                    fileToBeProcessed.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Logger.getLogger(BastropCountyCsv.class).error("", e);
                }
            }
            writer = new CsvMapWriter(new FileWriter(fileToBeProcessed), CsvPreference.EXCEL_PREFERENCE);
        } catch (IOException e1) {
            Logger.getLogger(BastropCountyCsv.class).error("", e1);
            return e1.getMessage();
        }
        final String[] header = new String[]{"upload date", "state", "county", "permit number", "permit description", "permit type", "permit date",
            "job value", "job square feet", "job address", "job city", "job state", "job zip", "job subdivision", "job lot number", "owner",
            "owner address", "owner city", "owner state", "owner zip", "owner phone", "owner type", "owner url", "owner fax",
            "owner primary contact", "owner email", "bldcode", "units", "buildings", "ctype", "legal", "contractor", "contractor address",
            "contractor city", "contractor state", "contractor zip", "contractor phone", "contractor url", "contractor fax",
            "contractor primary contact", "contractor email", "contractor last activity", "contractor status"};
        try {
            writer.writeHeader(header);
        } catch (IOException e1) {
            Logger.getLogger(BastropCountyCsv.class).error("", e1);
            return e1.getMessage();
        }

        try {

            wb = new XSSFWorkbook(fileToProcessLocation);
        } catch (IOException e) {
            Logger.getLogger(BastropCountyCsv.class).error("", e);
            return e.getMessage();
        }
        // get first sheet
        XSSFSheet sheet = wb.getSheetAt(0);
        int sheetLastRowNumber = sheet.getLastRowNum();

        for (int j = 1; j <= sheetLastRowNumber; j++) {
            XSSFRow row = sheet.getRow(j);
            if (row != null) {
                Map<String, String> data = new HashMap<String, String>();

                data.put("upload date", "");
                data.put("state", "");
                data.put("county", "BastropCounty");
                data.put("permit number", "");
                if (row.getCell(0).toString() != null) {
                    String permitNo = row.getCell(0).toString();
                    if (permitNo.contains(".")) {
                        String[] splitPermitNo = permitNo.replace(".", "-").split("-");
                        permitNo = splitPermitNo[0];
                    }
                    data.put("permit number", permitNo);
                }
                data.put("permit description", "");
                if (row.getCell(5).toString() != null) {
                    data.put("permit description", row.getCell(5).toString());
                }
                data.put("permit type", "");
                if (row.getCell(1).toString() != null) {
                    data.put("permit type", row.getCell(1).toString());
                }
                data.put("permit date", "");
                data.put("job value", "");
                data.put("job square feet", "");
                if (row.getCell(9).toString() != null) {
                    data.put("job square feet", row.getCell(9).toString());
                }
                data.put("job address", "");
                String jobAddress = "";
                if (row.getCell(6).toString() != null) {
                    String[] strts = row.getCell(6).toString().replace(".", "-").split("-");
                    if (strts.length > 0) {
                        jobAddress = jobAddress + strts[0];
                    }
                }
                if (row.getCell(7).toString() != null) {
                    jobAddress = jobAddress + " " + row.getCell(7).toString();
                }
                data.put("job address", jobAddress);
                data.put("job city", "Austin");
                data.put("job state", "TX");
                data.put("job zip", "");
                data.put("job subdivision", "");
                if (row.getCell(8).toString() != null) {
                    data.put("job subdivision", row.getCell(8).toString());
                }
                data.put("job lot number", "");
                String ownerName = "";
                if (row.getCell(11).toString() != null) {
                    ownerName = ownerName + row.getCell(11).toString();
                }
                if (row.getCell(10).toString() != null) {
                    ownerName = ownerName + " " + row.getCell(10).toString();
                }
                data.put("owner", ownerName);
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
                if (row.getCell(12).toString() != null) {
                    data.put("contractor", row.getCell(12).toString());
                }
                data.put("contractor address", "");
                data.put("contractor city", "");
                data.put("contractor state", "");
                data.put("contractor zip", "");
                data.put("contractor phone", "");
                if (row.getCell(13).toString() != null) {
                    data.put("contractor phone", row.getCell(13).toString());
                }
                data.put("contractor url", "");
                data.put("contractor fax", "");
                data.put("contractor primary contact", "");
                data.put("contractor email", "");
                data.put("contractor last activity", "");
                data.put("contractor status", "");

                try {
                    writer.write(data, header);
                } catch (IOException e) {
                    try {
                        writer.close();
                    } catch (IOException e1) {
                        Logger.getLogger(BastropCountyCsv.class).error("", e);
                        return e1.getMessage();
                    }
                    Logger.getLogger(BastropCountyCsv.class).error("", e);
                    return e.getMessage();
                }
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            Logger.getLogger(BastropCountyCsv.class).error("", e);
            return e.getMessage();
        }
        return "success";
    }
}
