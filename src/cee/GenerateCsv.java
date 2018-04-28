package cee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.LoggerProvider;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTagType;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
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
 * Servlet implementation class GenerateCsv
 */
public class GenerateCsv extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String TMP_DIR_PATH = "runtime/upload";
    private File tmpDir;
    private static final String DESTINATION_DIR_PATH = "runtime/csv";
    private File destinationDir;

    /**
     * Default constructor.
     */
    public GenerateCsv() {
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
                    // fileLocation = tempDir + "\\" + item.getName();
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
        List<String> appNumbers = new ArrayList<String>();
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
                    if (application != null && !"".equals(application)) {
                        appNumbers.add(application);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            request.setAttribute("errorMessage", e.getMessage());
        }

        Config.LoggerProvider = LoggerProvider.DISABLED;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        Logger.getLogger(this.getClass()).log(Priority.INFO, "Signing In....");
        HttpPost httppost1 = new HttpPost("https://permits.sanantonio.gov/DP1/Metroplex/SanAntonio/login/WIZ_LOGIN.asp");
        List<BasicNameValuePair> nvps = null;
        InputStream is = null;
        nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("MAIN_LOGON_USRNAME", "gakhalsa"));
        nvps.add(new BasicNameValuePair("MAIN_LOGON_PWD", "a4vpuorx"));
        nvps.add(new BasicNameValuePair("WIZ_ACTION", "ACTION_NEXT"));
        nvps.add(new BasicNameValuePair("WIZ_CURWIZPG", "WP_LOGIN"));
        nvps.add(new BasicNameValuePair("WIZ_WIZFILENAME", "1312830034731WIZ_LOGIN"));
        nvps.add(new BasicNameValuePair("WIZ_ASPFILENAME", "%2FDP1%2FMetroplex%2FSanAntonio%2Flogin%2FWIZ_LOGIN.asp"));
        nvps.add(new BasicNameValuePair("WIZ_BTNNAME", "BTN_LOGIN"));
        nvps.add(new BasicNameValuePair("MAIN_LOGON_PWD_ENCRYPT", "Y"));
        nvps.add(new BasicNameValuePair("SERVERIP", "permits.sanantonio.gov"));
        nvps.add(new BasicNameValuePair("WANTTOPOSTTO", "%2FDP1%2FMetroplex%2FSanAntonio%2Flogin%2FWIZ_LOGIN.asp"));
        nvps.add(new BasicNameValuePair("ACTUALLYPOSTTO",
                "https%3A%2F%2Fpermits.sanantonio.gov%2FDP1%2FMetroplex%2FSanAntonio%2Flogin%2FWIZ_LOGIN.asp"));

        httppost1.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response1 = httpclient.execute(httppost1);
        HttpEntity entity1 = response1.getEntity();
        EntityUtils.consume(entity1);

        Logger.getLogger(this.getClass()).log(Priority.INFO, "Fetching Application records for following numbers : ");
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd_HHmmss");
        String dateNow = formatter.format(currentDate.getTime()).toString() + ".csv";
        // Window
        // String csvFile = realPath + "\\" + dateNow;

        // linux
        String csvFile = realPath + "/" + dateNow;
        String csvFileRelativePath = DESTINATION_DIR_PATH + "/" + dateNow;
        File f = new File(csvFile);
        if (!f.exists()) {
            f.createNewFile();
        }
        ICsvMapWriter writer = new CsvMapWriter(new FileWriter(csvFile), CsvPreference.EXCEL_PREFERENCE);
        final String[] header = new String[]{"upload date", "state", "county", "permit number", "application description", "permit description",
            "permit type", "permit date", "job value", "job square feet", "job address", "job city", "job state", "job zip", "job subdivision",
            "job lot number", "owner", "owner address", "owner city", "owner state", "owner zip", "owner phone", "owner type", "owner url",
            "owner fax", "owner primary contact", "owner email", "bldcode", "units", "buildings", "ctype", "legal", "contractor",
            "contractor address", "contractor city", "contractor state", "contractor zip", "contractor phone", "contractor url",
            "contractor fax", "contractor primary contact", "contractor email", "contractor last activity", "contractor status"};
        writer.writeHeader(header);

        List<Map<String, String>> dataErrorList = new ArrayList<Map<String, String>>();
        for (String appNumber : appNumbers) {
            try {
                HttpPost httppost2 = new HttpPost("https://permits.sanantonio.gov/DP1/Metroplex/SanAntonio/permit/WIZ_SELECT.asp");
                nvps = new ArrayList<BasicNameValuePair>();
                nvps.add(new BasicNameValuePair("SELECT_APBLDG_APNO", appNumber));
                nvps.add(new BasicNameValuePair("SELECT_MISC_QUERYSTR", "%2520AND%2520APBLDG.APNO%253D%2527%2520%2520" + appNumber + "%2527"));
                nvps.add(new BasicNameValuePair("WIZ_ACTION", "ACTION_NEXT"));
                nvps.add(new BasicNameValuePair("WIZ_CURWIZPG", "WP_APNOINPUT"));
                httppost2.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

                HttpResponse response3 = httpclient.execute(httppost2);
                HttpEntity entity3 = response3.getEntity();
                is = entity3.getContent();

                String sourceCode = convertStreamToString(is);
                sourceCode = sourceCode.substring(sourceCode.lastIndexOf("<!-- Start Table Format -->"),
                        sourceCode.indexOf("<!-- End Table Format -->"));
                Source source = new Source(sourceCode);

                Logger.getLogger(this.getClass()).log(Priority.INFO, "Writting data for application #" + appNumber);
                Map<String, String> searchedRecord = displaySegmentsUpdate(source.getFirstElement(HTMLElementName.TABLE));

                HttpPost httppost3 = new HttpPost("https://webapps1.sanantonio.gov/bipi2/PermitDetail.aspx?PermitNo=" + appNumber + "&ptype=1004");

                HttpResponse response4 = httpclient.execute(httppost3);
                HttpEntity entity4 = response4.getEntity();
                is = entity4.getContent();

                String sourceCodeOtherSite = convertStreamToString(is);
                source = new Source(sourceCodeOtherSite);

                searchedRecord = displaySegments1(source.getElementById("Table1"), searchedRecord);

                formatter = new SimpleDateFormat("MM/dd/yyyy");
                searchedRecord.put("upload date", formatter.format(currentDate.getTime()).toString());
                searchedRecord.put("state", "TX");
                searchedRecord.put("county", "Bexar");
                writer.write(searchedRecord, header);
                EntityUtils.consume(entity3);
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).error("Error on application #" + appNumber, e);
                Map<String, String> dataError = new HashMap<String, String>();
                dataError.put("permit number", appNumber);
                dataError.put("error message", e.getMessage());
                dataErrorList.add(dataError);
            }
        }
        writer.close();

        if (!dataErrorList.isEmpty()) {
            String csvErrorFile = realPath + "/Error_" + dateNow;
            String csvFileRelativePathError = DESTINATION_DIR_PATH + "/Error_" + dateNow;
            File fError = new File(csvErrorFile);
            if (!fError.exists()) {
                fError.createNewFile();
            }
            ICsvMapWriter writerError = new CsvMapWriter(new FileWriter(csvErrorFile), CsvPreference.EXCEL_PREFERENCE);
            final String[] headerError = new String[]{"permit number", "error message"};
            writerError.writeHeader(headerError);
            for (Map<String, String> dataError : dataErrorList) {
                writerError.write(dataError, headerError);
            }
            writerError.close();
            request.setAttribute("genCsvErrorFileLoc", csvFileRelativePathError);
        }

        request.setAttribute("succesMessage", "Done successfully.. ");
        request.setAttribute("genCsvFileLoc", csvFileRelativePath);
        RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }

    private static Map<String, String> displaySegmentsUpdate(Element element) {
        List<String> headers = new ArrayList<String>();
        headers.add("Application #");
        headers.add("Site Location");
        headers.add("Declared Valuation");
        headers.add("Company/Contractor");
        headers.add("Total Area (sq ft)");
        headers.add("Additional Contacts");
        headers.add("Applicant/Contact");
        headers.add("Application Description");

        Map<String, String> data = new HashMap<String, String>();
        data.put("upload date", "");
        data.put("state", "");
        data.put("county", "");
        data.put("permit number", "");
        data.put("application description", "");
        data.put("permit description", "");
        data.put("permit type", "");
        data.put("permit date", "");
        data.put("job value", "");
        data.put("job square feet", "");
        data.put("job address", "");
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

        Map<String, String> replaceDataValue = new HashMap<String, String>();
        replaceDataValue.put("Application #", "permit number");
        replaceDataValue.put("Company/Contractor", "contractor");
        replaceDataValue.put("Declared Valuation", "job value");
        replaceDataValue.put("Site Location", "job address");
        replaceDataValue.put("Total Area (sq ft)", "job square feet");
        replaceDataValue.put("Additional Contacts", "owner");
        replaceDataValue.put("Application Description", "application description");
        String applicantContact = "";

        List<Element> trElements = element.getChildElements();
        for (Element trElement : trElements) {
            if (HTMLElementName.TR.equals(trElement.getName())) {
                List<Element> tdElements = trElement.getChildElements();
                int countTd = 0;
                String headerTitle = "";
                for (Element tdElement : tdElements) {
                    if (HTMLElementName.TD.equals(tdElement.getName())) {
                        countTd++;
                        // parsing column name
                        if (countTd == 1) {
                            headerTitle = tdElement.getRenderer().toString().replace(":", "").trim();
                            if (!headers.contains(headerTitle)) {
                                countTd++;
                                continue;
                            }
                            if (replaceDataValue.containsKey(headerTitle)) {
                                headerTitle = (String) replaceDataValue.get(headerTitle);
                            }
                        }

                        // parsing column value
                        if (countTd == 2 && (data.containsKey(headerTitle) || "Applicant/Contact".equals(headerTitle))) {

                            String record = tdElement.getRenderer().toString().trim();
                            if ("job address".equals(headerTitle)) {
                                StringTokenizer st = new StringTokenizer(record, System.getProperty("line.separator"));
                                String jobAddress = st.nextToken();

                                if (st.hasMoreTokens()) {
                                    String siteCity = st.nextToken();
                                    if (siteCity.indexOf(",") == -1) {
                                        if (st.hasMoreTokens()) {
                                            siteCity = st.nextToken();
                                        }
                                    }
                                    if (siteCity.indexOf(",") == -1) {
                                        if (st.hasMoreTokens()) {
                                            siteCity = st.nextToken();
                                        }
                                    }
                                    if (siteCity.indexOf(",") != -1) {
                                        data.put("job city", siteCity.substring(0, siteCity.indexOf(",")).trim());
                                        String siteState = siteCity.substring(siteCity.indexOf(", ") + 2, siteCity.indexOf(", ") + 4).trim();
                                        data.put("job state", siteState);
                                        String siteZip = siteCity.substring(siteCity.indexOf(", ") + 5, siteCity.length()).trim();
                                        if (siteZip.length() > 5) {
                                            siteZip = siteZip.substring(0, 5);
                                        }
                                        data.put("job zip", siteZip);
                                    }

                                }
                            } else if ("contractor".equals(headerTitle) && !"".equals(record.trim())) {
                                // <C><CAPKEY>1752383</CAPKEY><CONTID>016049</CONTID><CONTNAME>NRP
                                // GROUP</CONTNAME><CONTTYPE></CONTTYPE>
                                // <ADDBY>AC14700</ADDBY><ADDDTTM>08/04/2004
                                // 15:37:17</ADDDTTM><ADDR>111 SOLEDAD STE.
                                // 1220</ADDR><ADDR2></ADDR2>
                                // <CITY>SAN
                                // ANTONIO</CITY><DBANAME></DBANAME><EFFDATE>07/09/2008
                                // 00:00:00</EFFDATE><ESCROWNO>D0216</ESCROWNO>
                                // <EXPDATE></EXPDATE><FAX>(210)487-7880</FAX><FEDTAXID></FEDTAXID><LSTAPTRNDT>09/28/2011
                                // 08:21:00</LSTAPTRNDT><LSTESCBLDT></LSTESCBLDT>
                                // /<MAXBAL>100000000.000000</MAXBAL><MINBAL>0.000000</MINBAL><MODBY>EP14139</MODBY><MODDTTM>09/28/2011
                                // 08:21:49</MODDTTM>
                                // <PHONE>(210)487-7878
                                // x</PHONE><RATE>0.000000</RATE><STATE>TX</STATE><STTAXID></STTAXID><SYSFLAG>0</SYSFLAG>
                                // /<ZIP>78205-</ZIP><PRIMARY>Y</PRIMARY><CAPACITY></CAPACITY><CAPOTHER
                                // selected="Y"></CAPOTHER><CAPOTHERDESC
                                // selected="Y"></CAPOTHERDESC>
                                // <APLKEY>2352703</APLKEY><INDEX>1</INDEX></C>

                                for (Element ele : tdElement.getAllElements(StartTagType.COMMENT)) {
                                    String oTableNode = ele.toString().substring(ele.toString().indexOf("=") + 1, ele.toString().indexOf("-->")).trim();
                                    Source oTableNodeSource = new Source(oTableNode);
                                    if (oTableNodeSource.getFirstElement("contname") != null) {
                                        data.put("contractor",
                                                oTableNodeSource.getFirstElement("contname").getContent().toString().replace("&amp;", "&").trim());
                                    }
                                    String contractorAddress = "";
                                    if (oTableNodeSource.getFirstElement("addr") != null) {
                                        contractorAddress = oTableNodeSource.getFirstElement("addr").getContent().toString().trim();
                                    }
                                    if (oTableNodeSource.getFirstElement("addr2") != null) {
                                        contractorAddress = contractorAddress + " "
                                                + oTableNodeSource.getFirstElement("addr2").getContent().toString().trim();
                                    }
                                    data.put("contractor address", contractorAddress);
                                    if (oTableNodeSource.getFirstElement("city") != null) {
                                        data.put("contractor city", oTableNodeSource.getFirstElement("city").getContent().toString().trim());
                                    }
                                    if (oTableNodeSource.getFirstElement("state") != null) {
                                        data.put("contractor state", oTableNodeSource.getFirstElement("state").getContent().toString().trim());
                                    }
                                    if (oTableNodeSource.getFirstElement("zip") != null) {
                                        String contractorZip = oTableNodeSource.getFirstElement("zip").getContent().toString().trim();
                                        if (contractorZip.length() > 5) {
                                            contractorZip = contractorZip.substring(0, 5);
                                        }
                                        data.put("contractor zip", contractorZip);
                                    }
                                    if (oTableNodeSource.getFirstElement("phone") != null) {
                                        data.put("contractor phone",
                                                oTableNodeSource.getFirstElement("phone").getContent().toString().replace(")", ") ").replace("x", "").replace("X", "").trim());
                                    }
                                    if (oTableNodeSource.getFirstElement("fax") != null) {
                                        data.put("contractor fax", oTableNodeSource.getFirstElement("fax").getContent().toString().replace(")", ") ").replace("x", "").replace("X", "").trim());
                                    }
                                    break;
                                }
                            } else if ("owner".equals(headerTitle) && !"".equals(record.trim())) {
                                // <!-- oTableNode.xml =
                                // <CONTACT2><CONTACTAPKEY>1752383</CONTACTAPKEY><CNTCTKEY>144752</CNTCTKEY>
                                // /<CNTCTID>AC144415</CNTCTID><CNTCTLAST>CONSTRUCTION</CNTCTLAST><CNTCTTYPE>C</CNTCTTYPE><ADDBY>WEB</ADDBY>
                                // <ADDDTTM>10/04/2007
                                // 14:38:48</ADDDTTM><ADDR1>111 SOLEDAD, SUITE
                                // 1220</ADDR1><ADDR2></ADDR2><CITY>SAN
                                // ANTONIO</CITY>
                                // <CNTCTFIRST>NRP (UMBRELLA
                                // ACCOUNT)</CNTCTFIRST><CNTCTFOR>N</CNTCTFOR><CNTCTIDNO></CNTCTIDNO><CNTCTIDTY></CNTCTIDTY>
                                // <CNTCTMI></CNTCTMI><CNTCTTITLE></CNTCTTITLE><CONAME></CONAME><COUNTRY></COUNTRY><DAYPHN>(210)487-7878
                                // x</DAYPHN>
                                // <EMAIL>NRPContractors@nrpgroup.com</EMAIL><EVEPHN></EVEPHN><FAX></FAX><MOBILE></MOBILE><MODDTTM>08/08/2011
                                // 14:26:27</MODDTTM>
                                // <POSITION></POSITION><PGR></PGR><PGRPIN></PGRPIN><STATE>TX</STATE><ZIP>78205</ZIP><PRIMARY>N</PRIMARY>
                                // <CAPACITY>APPL</CAPACITY><CAPOTHER
                                // selected="Y"></CAPOTHER><APLKEY>2593987</APLKEY></CONTACT2>
                                if (tdElement.getAllElements(StartTagType.COMMENT) != null) {
                                    Element ele = tdElement.getAllElements(StartTagType.COMMENT).get(
                                            tdElement.getAllElements(StartTagType.COMMENT).size() - 1);
                                    String oTableNode = ele.toString().substring(ele.toString().indexOf("=") + 1, ele.toString().indexOf("-->")).trim();
                                    Source oTableNodeSource = new Source(oTableNode);
                                    String owner = "";
                                    if (oTableNodeSource.getFirstElement("CNTCTFIRST".toLowerCase()) != null) {
                                        owner = oTableNodeSource.getFirstElement("CNTCTFIRST".toLowerCase()).getContent().toString().trim();
                                    }
                                    if (oTableNodeSource.getFirstElement("CNTCTLAST".toLowerCase()) != null) {
                                        owner = owner + " "
                                                + oTableNodeSource.getFirstElement("CNTCTLAST".toLowerCase()).getContent().toString().trim();
                                    }
                                    if ("".equals(owner)) {
                                        owner = owner + oTableNodeSource.getFirstElement("CONAME".toLowerCase()).getContent().toString().trim();
                                    }
                                    owner = owner.replace("(UMBRELLA ACCOUT)", "").replace("(UMBRELLA ACCT)", "").trim();

                                    data.put("owner", owner);
                                    String ownerAddress = "";
                                    if (oTableNodeSource.getFirstElement("addr1") != null) {
                                        ownerAddress = oTableNodeSource.getFirstElement("addr1").getContent().toString().trim();
                                    }
                                    if (oTableNodeSource.getFirstElement("addr2") != null) {
                                        ownerAddress = ownerAddress + " " + oTableNodeSource.getFirstElement("addr2").getContent().toString().trim();
                                    }
                                    data.put("owner address", ownerAddress);
                                    if (oTableNodeSource.getFirstElement("city") != null) {
                                        data.put("owner city", oTableNodeSource.getFirstElement("city").getContent().toString().trim());
                                    }
                                    if (oTableNodeSource.getFirstElement("state") != null) {
                                        data.put("owner state", oTableNodeSource.getFirstElement("state").getContent().toString().trim());
                                    }
                                    if (oTableNodeSource.getFirstElement("zip") != null) {
                                        String ownerZip = oTableNodeSource.getFirstElement("zip").getContent().toString().trim();
                                        if (ownerZip.length() > 5) {
                                            ownerZip = ownerZip.substring(0, 5);
                                        }
                                        data.put("owner zip", ownerZip);
                                    }
                                    if (oTableNodeSource.getFirstElement("dayphn") != null) {
                                        data.put("owner phone", oTableNodeSource.getFirstElement("dayphn").getContent().toString().replace(")", ") ").replace("x", "").replace("X", "").trim());
                                    }
                                    if (oTableNodeSource.getFirstElement("fax") != null) {
                                        data.put("owner fax", oTableNodeSource.getFirstElement("fax").getContent().toString().replace(")", ") ").replace("x", "").replace("X", "").trim());
                                    }
                                    break;
                                }
                            } else if ("Applicant/Contact".equals(headerTitle)) {
                                applicantContact = tdElement.getContent().toString();
                            } else if ("application description".equals(headerTitle) && !"".equals(record.trim())) {
                                String applicationDescription = record;
                                if (record.contains("**")) {
                                    applicationDescription = applicationDescription.replace("**", "");
                                }
                                data.put(headerTitle, applicationDescription.trim());
                            } else {
                                data.put(headerTitle, record);
                            }

                        }
                    }
                }
            }
        }
        if ("".equals(data.get("contractor"))) {
            Logger.getLogger(GenerateCsv.class).log(Priority.INFO, "Contractor is empty, now looking record from Applicant/contact field.");
            Source source = new Source(applicantContact);
            // <CONTACT><CONTACTAPKEY>1849798</CONTACTAPKEY><CNTCTKEY>215820</CNTCTKEY><CNTCTID>AC215432</CNTCTID>
            // <CNTCTLAST>HOLMES</CNTCTLAST><CNTCTTYPE>C</CNTCTTYPE><ADDBY>AB04128</ADDBY><ADDDTTM>11/15/2010
            // 11:40:18</ADDDTTM>
            // <ADDR1>1430 W PEACHTREE STREET NW</ADDR1><ADDR2>SUITE
            // 200</ADDR2><CITY>ATLANTA</CITY><CNTCTFIRST>JULIE</CNTCTFIRST>
            // <CNTCTFOR>N</CNTCTFOR><CNTCTIDNO></CNTCTIDNO><CNTCTIDTY></CNTCTIDTY><CNTCTMI></CNTCTMI><CNTCTTITLE></CNTCTTITLE>
            // <CONAME>GREENBERG
            // FARROW</CONAME><COUNTRY></COUNTRY><DAYPHN>(404)601-4274
            // x</DAYPHN><EMAIL>JHOLMES@GREENBERGFARROW.COM</EMAIL><EVEPHN></EVEPHN><FAX>(404)601-3990</FAX><MOBILE></MOBILE><MODDTTM>04/19/2011
            // 11:11:30</MODDTTM><POSITION></POSITION><PGR></PGR><PGRPIN></PGRPIN><STATE>GA</STATE><ZIP>30309</ZIP><PRIMARY>Y</PRIMARY><CAPACITY>APPL</CAPACITY><CAPOTHER></CAPOTHER><APLKEY>2533422</APLKEY><INDEX>1</INDEX></CONTACT>
            for (Element ele : source.getAllElements(StartTagType.COMMENT)) {
                String oTableNode = ele.toString().substring(ele.toString().indexOf("=") + 1, ele.toString().indexOf("-->")).trim();
                Source oTableNodeSource = new Source(oTableNode);
                String contractor = "";
                if (oTableNodeSource.getFirstElement("coname") != null) {
                    contractor = oTableNodeSource.getFirstElement("coname").getContent().toString().trim();
                }
                if ("".equals(contractor)) {
                    if (oTableNodeSource.getFirstElement("CNTCTFIRST".toLowerCase()) != null) {
                        contractor = oTableNodeSource.getFirstElement("CNTCTFIRST".toLowerCase()).getContent().toString().trim();
                    }
                    if (oTableNodeSource.getFirstElement("CNTCTLAST".toLowerCase()) != null) {
                        contractor = contractor + " " + oTableNodeSource.getFirstElement("CNTCTLAST".toLowerCase()).getContent().toString().trim();
                    }
                }
                contractor = contractor.replace("&amp;", "&");
                data.put("contractor", contractor);
                String contractorAddress = "";
                if (oTableNodeSource.getFirstElement("addr1") != null) {
                    contractorAddress = oTableNodeSource.getFirstElement("addr1").getContent().toString().trim();
                }
                if (oTableNodeSource.getFirstElement("addr2") != null) {
                    contractorAddress = contractorAddress + " " + oTableNodeSource.getFirstElement("addr2").getContent().toString().trim();
                }
                data.put("contractor address", contractorAddress);
                if (oTableNodeSource.getFirstElement("city") != null) {
                    data.put("contractor city", oTableNodeSource.getFirstElement("city").getContent().toString().trim());
                }
                if (oTableNodeSource.getFirstElement("state") != null) {
                    data.put("contractor state", oTableNodeSource.getFirstElement("state").getContent().toString().trim());
                }
                if (oTableNodeSource.getFirstElement("zip") != null) {
                    String contractorZip = oTableNodeSource.getFirstElement("zip").getContent().toString().trim();
                    if (contractorZip.length() > 5) {
                        contractorZip = contractorZip.substring(0, 5);
                    }
                    data.put("contractor zip", contractorZip);
                }
                if (oTableNodeSource.getFirstElement("dayphn") != null) {
                    data.put("contractor phone",
                            oTableNodeSource.getFirstElement("dayphn").getContent().toString().replace(")", ") ").replace("x", "").replace("X", "").trim());
                }
                if (oTableNodeSource.getFirstElement("fax") != null) {
                    data.put("contractor fax", oTableNodeSource.getFirstElement("fax").getContent().toString().replace(")", ") ").replace("x", "").replace("X", "").trim());
                }

                // <CONTACT><CONTACTAPKEY>1849798</CONTACTAPKEY><CNTCTKEY>215820</CNTCTKEY><CNTCTID>AC215432</CNTCTID>
                // <CNTCTLAST>HOLMES</CNTCTLAST><CNTCTTYPE>C</CNTCTTYPE><ADDBY>AB04128</ADDBY><ADDDTTM>11/15/2010
                // 11:40:18</ADDDTTM>
                // <ADDR1>1430 W PEACHTREE STREET NW</ADDR1><ADDR2>SUITE
                // 200</ADDR2><CITY>ATLANTA</CITY><CNTCTFIRST>JULIE</CNTCTFIRST>
                // <CNTCTFOR>N</CNTCTFOR><CNTCTIDNO></CNTCTIDNO><CNTCTIDTY></CNTCTIDTY><CNTCTMI></CNTCTMI><CNTCTTITLE></CNTCTTITLE>
                // <CONAME>GREENBERG
                // FARROW</CONAME><COUNTRY></COUNTRY><DAYPHN>(404)601-4274
                // x</DAYPHN><EMAIL>JHOLMES@GREENBERGFARROW.COM</EMAIL><EVEPHN></EVEPHN><FAX>(404)601-3990</FAX><MOBILE></MOBILE><MODDTTM>04/19/2011
                // 11:11:30</MODDTTM><POSITION></POSITION><PGR></PGR><PGRPIN></PGRPIN><STATE>GA</STATE><ZIP>30309</ZIP><PRIMARY>Y</PRIMARY><CAPACITY>APPL</CAPACITY><CAPOTHER></CAPOTHER><APLKEY>2533422</APLKEY><INDEX>1</INDEX></CONTACT>
                break;
            }
        }
        return data;
    }

    private static Map<String, String> displaySegments1(Element element, Map<String, String> searchedData) {
        List<String> headers = new ArrayList<String>();
        headers.add("Date Issued");
        headers.add("Structure Sq. Ft");
        headers.add("Estimated Cost Value");
        headers.add("Address");

        Map<String, String> replaceDataValue = new HashMap<String, String>();
        replaceDataValue.put("Estimated Cost Value", "job value");
        replaceDataValue.put("Address", "job address");
        replaceDataValue.put("Date Issued", "permit date");
        replaceDataValue.put("Site Location", "job address");
        replaceDataValue.put("Structure Sq. Ft", "job square feet");

        List<Element> trElements = element.getChildElements();
        for (Element trElement : trElements) {
            if (HTMLElementName.TR.equals(trElement.getName())) {
                List<Element> tdElements = trElement.getChildElements();
                int countTd = 0;
                String headerTitle = "";
                for (Element tdElement : tdElements) {
                    if (HTMLElementName.TD.equals(tdElement.getName())) {
                        countTd++;
                        // parsing column name
                        if (countTd == 1) {
                            headerTitle = tdElement.getRenderer().toString().replace(":", "").trim();
                            if (!headers.contains(headerTitle)) {
                                countTd++;
                                continue;
                            }
                            if (replaceDataValue.containsKey(headerTitle)) {
                                headerTitle = (String) replaceDataValue.get(headerTitle);
                            }
                        }
                        // parsing column value
                        if (countTd == 2 && searchedData.containsKey(headerTitle)) {
                            if ("job value".equals(headerTitle)) {
                                Pattern p = Pattern.compile("(\\d+)");
                                Matcher jobValueNumber = p.matcher(tdElement.getRenderer().toString().replace(",", "").replace("$", "").trim());
                                if (jobValueNumber.find()) {
                                    BigDecimal jobValue = new BigDecimal(jobValueNumber.group(1));
                                    Matcher jobOldValueNumber = p.matcher(searchedData.get("job value").toString());
                                    if (jobOldValueNumber.find()) {
                                        BigDecimal oldJobValue = new BigDecimal(jobOldValueNumber.group(1));

                                        if (jobValue.compareTo(oldJobValue) > 0) {
                                            searchedData.put("job value", jobValue.toString());
                                        } else {
                                            searchedData.put("job value", oldJobValue.toString());
                                        }
                                    } else {
                                        searchedData.put("job value", jobValue.toString());
                                    }
                                }

                            } else if ("job square feet".equals(headerTitle)) {
                                Pattern p = Pattern.compile("(\\d+)");
                                Matcher jobSquareFeetNumber = p.matcher(tdElement.getRenderer().toString().replace("$", "").replace(",", "").trim());
                                if (jobSquareFeetNumber.find()) {
                                    BigDecimal jobSquareFeet = new BigDecimal(jobSquareFeetNumber.group(1));

                                    Matcher oldJobSquareFeetNumber = p.matcher(searchedData.get("job square feet").toString());
                                    if (oldJobSquareFeetNumber.find()) {
                                        BigDecimal oldJobSquareFeet = new BigDecimal(oldJobSquareFeetNumber.group(1));
                                        if (jobSquareFeet.compareTo(oldJobSquareFeet) > 0) {
                                            searchedData.put("job square feet", jobSquareFeet.toString());
                                        } else {
                                            searchedData.put("job square feet", oldJobSquareFeet.toString());
                                        }
                                    } else {
                                        searchedData.put("job square feet", jobSquareFeet.toString());
                                    }
                                }
                            } else if ("permit date".equals(headerTitle)) {
                                String permitDate = tdElement.getRenderer().toString().trim();
                                if (!"".equals(permitDate)) {
                                    permitDate = permitDate.substring(0, permitDate.indexOf(" "));
                                    searchedData.put(headerTitle, permitDate);
                                }
                            } else if ("job address".equals(headerTitle)) {
                                String jobAddress = tdElement.getRenderer().toString().trim();
                                if (jobAddress.indexOf("**") != -1) {
                                    jobAddress = jobAddress.substring(0, jobAddress.indexOf("**")).trim();
                                }
                                searchedData.put(headerTitle, jobAddress);
                            } else {
                                searchedData.put(headerTitle, tdElement.getRenderer().toString().trim());
                            }
                        }
                    }
                }
            }
        }
        return searchedData;
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
