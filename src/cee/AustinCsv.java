package cee;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * Servlet implementation class AustinCsv
 */
public class AustinCsv extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String TMP_DIR_PATH = "runtime/upload";
    private File tmpDir;
    private static final String DESTINATION_DIR_PATH = "runtime/csv";
    private File destinationDir;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AustinCsv() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        String tempDir = getServletContext().getRealPath(TMP_DIR_PATH);
        tmpDir = new File(tempDir);
        if (!tmpDir.isDirectory()) {
            throw new ServletException(TMP_DIR_PATH + " is not a directory");
        }
        String realPath = getServletContext().getRealPath(DESTINATION_DIR_PATH);
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
        String fileLocation = "";
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
                    //fileLocation = tempDir + "\\" + item.getName();

                    // linux
                    fileLocation = tempDir + "/" + item.getName();
                    item.write(file);
                    Logger.getLogger(this.getClass()).log(Priority.INFO, "Done..");
                }
            }
        } catch (FileUploadException ex) {
            Logger.getLogger(this.getClass()).error("Error encountered while parsing the request", ex);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass()).error("Error encountered while parsing the request", ex);
        }

        List<Map<String, String>> appNumbers = new ArrayList<Map<String, String>>();
        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(fileLocation);
            // get first sheet
            XSSFSheet sheet = wb.getSheetAt(0);
            int sheetLastRowNumber = sheet.getLastRowNum();
            for (int j = 1; j <= sheetLastRowNumber; j++) {
                XSSFRow row = sheet.getRow(j);
                if (row != null) {
                    // read application# from first column
                    // "sheet column index starts from 0"
                    XSSFCell cell2 = row.getCell((int) 0);
                    cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                    String application = cell2.getRichStringCellValue().toString();
                    if (application != null && !"".equals(application) && application.contains("BP")) {
                        Map<String, String> applicationMap = new HashMap<String, String>();
                        cell2 = row.getCell((int) 29);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String total_valuation_remodel = cell2.getRichStringCellValue().toString().trim();
                        cell2 = row.getCell((int) 30);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String total_job_valuation = cell2.getRichStringCellValue().toString().trim();
                        BigDecimal jobValue = BigDecimal.ZERO;
                        if (!"".equals(total_valuation_remodel)) {
                            jobValue = jobValue.add(new BigDecimal(total_valuation_remodel));
                        }
                        if (!"".equals(total_job_valuation)) {
                            jobValue = jobValue.add(new BigDecimal(total_job_valuation));
                        }
                        cell2 = row.getCell((int) 1);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String permitType = cell2.getRichStringCellValue().toString().trim();
                        applicationMap.put("permit type", permitType.toString());

                        cell2 = row.getCell((int) 5);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String permitDescription = cell2.getRichStringCellValue().toString();

                        if (jobValue.compareTo(new BigDecimal(30000)) >= 0 || ("R- 329 Res Structures Other Than Bldg".equals(permitType) && permitDescription.contains("pool"))) {
                            applicationMap.put("job value", jobValue.toString());
                        } else {
                            if (!"R- 329 Res Structures Other Than Bldg".equals(permitType)) {
                                continue;
                            }
                        }
                        applicationMap.put("permit number", application);

                        // total_new_add_footage + number_of_units
                        cell2 = row.getCell((int) 27);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String total_new_add_footage = cell2.getRichStringCellValue().toString().trim();
                        cell2 = row.getCell((int) 31);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String number_of_units = cell2.getRichStringCellValue().toString().trim();
                        BigDecimal jobSquareFeet = BigDecimal.ZERO;
                        if (!"".equals(total_new_add_footage)) {
                            jobSquareFeet = jobSquareFeet.add(new BigDecimal(total_new_add_footage));
                        }
                        if (!"".equals(number_of_units)) {
                            jobSquareFeet = jobSquareFeet.add(new BigDecimal(number_of_units));
                        }
                        if (jobSquareFeet.compareTo(BigDecimal.ZERO) > 0) {
                            applicationMap.put("job square feet", jobSquareFeet.toString());
                        }

                        cell2 = row.getCell((int) 4);
                        // cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        @SuppressWarnings("deprecation")
                        Date date = new Date(cell2.getStringCellValue());
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                        String dateIssued = formatter.format(date.getTime()).toString();
                        applicationMap.put("permit date", dateIssued.toString());

                        cell2 = row.getCell((int) 5);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        applicationMap.put("permit description", cell2.getRichStringCellValue().toString().trim());

                        cell2 = row.getCell((int) 3);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String jobAddress = cell2.getRichStringCellValue().toString().trim();
                        applicationMap.put("job address", jobAddress.trim());

                        cell2 = row.getCell((int) 7);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String folderOwner = cell2.getRichStringCellValue().toString().trim();
                        // folder_owner
                        applicationMap.put("contractor", folderOwner.toString());

                        cell2 = row.getCell((int) 8);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String folder_owner_addrhouse = cell2.getRichStringCellValue().toString().trim();
                        String contractorAddress = "";
                        if (!"".equals(folder_owner_addrhouse)) {
                            contractorAddress = folder_owner_addrhouse;
                        }
                        cell2 = row.getCell((int) 9);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String folder_owner_addrstreet = cell2.getRichStringCellValue().toString().trim();
                        if (!"".equals(folder_owner_addrstreet)) {
                            contractorAddress = contractorAddress + " " + folder_owner_addrstreet;
                        }
                        cell2 = row.getCell((int) 10);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String folder_owner_addrstreettype = cell2.getRichStringCellValue().toString().trim();
                        if (!"".equals(folder_owner_addrstreettype)) {
                            contractorAddress = contractorAddress + " " + folder_owner_addrstreettype;
                        }
                        cell2 = row.getCell((int) 11);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String folder_owner_addrunittype = cell2.getRichStringCellValue().toString().trim();
                        if (!"".equals(folder_owner_addrunittype)) {
                            contractorAddress = contractorAddress + " " + folder_owner_addrunittype;
                        }

                        // Rename 'folder_owner_addrcity' to contractor
                        // city(column AB).

                        cell2 = row.getCell((int) 13);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String contractorCity = cell2.getRichStringCellValue().toString().trim();
                        applicationMap.put("contractor city", contractorCity.toString());

                        // Rename 'folder_owner_addrprovince' to contractor
                        // state(column AC).
                        cell2 = row.getCell((int) 14);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String contractorState = cell2.getRichStringCellValue().toString().trim();
                        applicationMap.put("contractor state", contractorState.toString());

                        // Rename 'folder_owner_addrpostal' to contractor
                        // zip(column AD).
                        cell2 = row.getCell((int) 15);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String contractorZip = cell2.getRichStringCellValue().toString().trim();
                        applicationMap.put("contractor zip", contractorZip.toString());

                        // Rename 'folder_owner_phone' to contractor
                        // phone(column AE).
                        cell2 = row.getCell((int) 16);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String contractorPhone = cell2.getRichStringCellValue().toString().trim();
                        applicationMap.put("contractor phone", contractorPhone.toString());

                        /*String contractorAddress = "";
                        cell2 = row.getCell((int) 17);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        contractorAddress = contractorAddress + " " + cell2.getRichStringCellValue().toString().trim();

                        cell2 = row.getCell((int) 18);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        contractorAddress = contractorAddress + " " + cell2.getRichStringCellValue().toString().trim();

                        cell2 = row.getCell((int) 19);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        contractorAddress = contractorAddress + " " + cell2.getRichStringCellValue().toString().trim();

                        cell2 = row.getCell((int) 20);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        contractorAddress = contractorAddress + " " + cell2.getRichStringCellValue().toString().trim();

                        cell2 = row.getCell((int) 21);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        contractorAddress = contractorAddress + " " + cell2.getRichStringCellValue().toString().trim();
                         */
                        cell2 = row.getCell((int) 34);
                        cell2.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String jobSubdivision = cell2.getRichStringCellValue().toString().trim();
                        if (jobSubdivision.indexOf("Subdivision:") != -1) {
                            jobSubdivision = jobSubdivision.substring(jobSubdivision.indexOf("Subdivision:") + 12);
                            if (jobSubdivision.indexOf(",") != -1) {
                                jobSubdivision = jobSubdivision.substring(jobSubdivision.indexOf(",") + 1);
                            }
                            if (jobSubdivision.indexOf(";") != -1) {
                                jobSubdivision = jobSubdivision.substring(jobSubdivision.indexOf(";") + 1);
                            }
                        }

                        applicationMap.put("job subdivision", jobSubdivision);

                        applicationMap.put("contractor address", contractorAddress);

                        appNumbers.add(applicationMap);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            Logger.getLogger(this.getClass()).log(Priority.ERROR, "\n\n\n===========");
            Logger.getLogger(this.getClass()).error("", e);
            Logger.getLogger(this.getClass()).log(Priority.ERROR, "\n\n\n===========");
            request.setAttribute("errorMessage", e.getMessage());
        }

        Logger.getLogger(this.getClass()).log(Priority.INFO, "Fetching Application records for following numbers : " + appNumbers.size());
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd_HHmmss");
        String dateNow = formatter.format(currentDate.getTime()).toString() + ".csv";
        String csvFile = realPath + "/" + dateNow;
        String csvFileRelataviPath = DESTINATION_DIR_PATH + "/" + dateNow;
        File f = new File(csvFile);
        if (!f.exists()) {
            f.createNewFile();
        }

        ICsvMapWriter writer = new CsvMapWriter(new FileWriter(csvFile), CsvPreference.EXCEL_PREFERENCE);
        final String[] header = new String[]{"upload date", "state", "county", "permit number", "permit description", "permit type", "permit date",
            "job value", "job square feet", "job address", "job city", "job state", "job zip", "job subdivision", "job lot number", "owner",
            "owner address", "owner city", "owner state", "owner zip", "owner phone", "owner type", "owner url", "owner fax",
            "owner primary contact", "owner email", "bldcode", "units", "buildings", "ctype", "legal", "contractor", "contractor address",
            "contractor city", "contractor state", "contractor zip", "contractor phone", "contractor url", "contractor fax",
            "contractor primary contact", "contractor email", "contractor last activity", "contractor status"};
        writer.writeHeader(header);

        for (Map<String, String> appNumber : appNumbers) {
            Map<String, String> data = new HashMap<String, String>();
            formatter = new SimpleDateFormat("MM/dd/yy");
            data.put("upload date", formatter.format(currentDate.getTime()).toString());
            data.put("state", "TX");
            data.put("county", "Travis");
            data.put("permit number", appNumber.get("permit number"));
            data.put("permit description", appNumber.get("permit description"));
            data.put("permit type", appNumber.get("permit type"));
            data.put("permit date", appNumber.get("permit date"));
            if (appNumber.get("job value") == null) {
                data.put("job value", "");
            } else {
                data.put("job value", appNumber.get("job value"));
            }

            if (appNumber.get("job square feet") == null) {
                data.put("job square feet", "");
            } else {
                data.put("job square feet", appNumber.get("job square feet"));
            }
            if (appNumber.get("job address") == null) {
                data.put("job address", "");
            } else {
                data.put("job address", appNumber.get("job address"));
            }

            data.put("job city", "Austin");
            data.put("job state", "TX");
            data.put("job zip", "");
            data.put("job subdivision", appNumber.get("job subdivision"));
            data.put("job lot number", "");
            data.put("owner", "Contractor");
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

            if (appNumber.get("contractor") == null) {
                data.put("contractor", "");
            } else {
                data.put("contractor", appNumber.get("contractor"));
            }

            data.put("contractor address", appNumber.get("contractor address"));
            if (appNumber.get("contractor city") == null) {
                data.put("contractor city", "");
            } else {
                data.put("contractor city", appNumber.get("contractor city"));
            }

            if (appNumber.get("contractor state") == null) {
                data.put("contractor state", "");
            } else {
                data.put("contractor state", appNumber.get("contractor state"));
            }
            if (appNumber.get("contractor zip") == null) {
                data.put("contractor zip", "");
            } else {
                data.put("contractor zip", appNumber.get("contractor zip"));
            }

            if (appNumber.get("contractor phone") == null) {
                data.put("contractor phone", "");
            } else {
                data.put("contractor phone", appNumber.get("contractor phone"));
            }

            data.put("contractor url", "");
            data.put("contractor fax", "");
            data.put("contractor primary contact", "");
            data.put("contractor email", "");
            data.put("contractor last activity", "");
            data.put("contractor status", "");
            writer.write(data, header);
        }
        writer.close();

        request.setAttribute("succesMessage", "Done successfully.. ");
        request.setAttribute("genCsvFileLoc", csvFileRelataviPath);
        RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }
}
