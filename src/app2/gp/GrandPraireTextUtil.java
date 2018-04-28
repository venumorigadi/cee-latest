package app2.gp;

import app2.CountyObject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GrandPraireTextUtil {

    private String filename;
    private ServletContext context;

    public GrandPraireTextUtil(String filename, ServletContext context) {
        this.filename = filename;
        this.context = context;
        Logger.getLogger(this.getClass()).log(Priority.INFO, "HarrisTextUtil init");
    }

    public String perform() throws FileNotFoundException, IOException {
        try {
            List<GrandPraireObject> list = new ArrayList<GrandPraireObject>();
            FileInputStream fis = new FileInputStream(context.getRealPath("/runtime/upload/" + filename));
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator iterator = sheet.rowIterator();
            iterator.next();

            while (iterator.hasNext()) {
                GrandPraireObject object = new GrandPraireObject();
                XSSFRow row = (XSSFRow) iterator.next();
                Iterator ci = row.cellIterator();
                int i = 0;

                while (ci.hasNext()) {
                    XSSFCell cell = (XSSFCell) ci.next();
                    String celldata = "";
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            celldata = cell.getRichStringCellValue().getString();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                Date dcv = cell.getDateCellValue();
                                Calendar c = Calendar.getInstance();
                                c.setTime(dcv);
                                celldata = c.get(Calendar.MONTH) + "/" + c.get(Calendar.DATE) + "/" + c.get(Calendar.YEAR);
                            } else {
                                try {
                                    celldata = "" + cell.getNumericCellValue();
                                } catch (Exception e) {
                                    celldata = " ";
                                }
                            }
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:
                            celldata = "" + cell.getBooleanCellValue();
                            break;
                        default:
                            celldata = "";
                    }
                    object.setProperty(cell.getColumnIndex(), celldata);
                }
                if (object.getPiSubDivision()==null || object.getPiSubDivision().isEmpty() || object.getPiSubDivision().equalsIgnoreCase("")) {
                    object.setPiSubDivision("");
                }
                if (Double.parseDouble(object.getAiValuation()) > 30000) {
                    list.add(object);
                }

                Logger.getLogger(this.getClass()).log(Priority.INFO, "found row - " + object.getPermitNumber());
            }

            StringBuilder builder = new StringBuilder();
            builder.append(new CountyObject().getHeader());
            Iterator<GrandPraireObject> itr = list.iterator();
            while (itr.hasNext()) {
                try {
                    GrandPraireObject gpObject = itr.next();
                    CountyObject object = gpObject.toCountyObject();
                    builder.append(object.getRow());
                } catch (Exception exception) {
                    Logger.getLogger(this.getClass()).error("skipping to next row", exception);
                }
            }

            String str = builder.toString();
            FileOutputStream fos = new FileOutputStream(context.getRealPath("/runtime/csv/" + filename + ".csv"));
            fos.write(str.getBytes());
            fos.close();
            Logger.getLogger(this.getClass()).log(Priority.INFO, "file saved to - " + context.getRealPath("/runtime/csv/" + filename + ".csv"));
            return filename;
        } catch (Exception exception) {
            exception.printStackTrace(System.out);
            return null;
        }
    }
}
