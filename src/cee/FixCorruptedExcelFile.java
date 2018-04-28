package cee;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.apache.log4j.Logger;

public class FixCorruptedExcelFile {

    public static String fixCorruptedExcelFileForHouston(String originalFilePath, String filteredFilePath) {
        BufferedReader bf;

        DataOutputStream fos;
        try {
            bf = new BufferedReader(new InputStreamReader(new FileInputStream(originalFilePath)));

            fos = new DataOutputStream(new FileOutputStream(filteredFilePath));

            String ch = null;
            StringBuilder sb = new StringBuilder();
            while ((ch = bf.readLine()) != null) {

                if (ch.contains("<TR") || ch.contains("<TD") || ch.contains("</TR")) {
                    if (ch.startsWith("</TR")) {
                        sb.append(ch).append("\n");
                    } else if (ch.startsWith("<TR>")) {
                        //sb.append(ch);
                    } else {
                        sb.append(ch).append("\t");
                    }

                }
            }

            String nohtml = sb.toString().replaceAll("\\<TD.*?>", "\"");
            nohtml = nohtml.replaceAll("\\</TD>", "\"");
            nohtml = nohtml.replaceAll("\\<TR>", "");
            nohtml = nohtml.replaceAll("\\</TR>", "");
            nohtml = nohtml.replaceAll("\"", "");
            nohtml = sb.toString().replaceAll("\\<.*?>", "");

            fos.writeBytes(nohtml);
            fos.close();
            bf.close();

            bf = new BufferedReader(new InputStreamReader(new FileInputStream(filteredFilePath)));
            String[] cells = null;
            int rowCount = 0;

            WritableWorkbook workbook = Workbook.createWorkbook(new File(originalFilePath));
            WritableSheet sheet = workbook.createSheet("Sheet1", 0);
            while ((ch = bf.readLine()) != null) {
                int cellCount = 0;
                cells = ch.split("\t");
                for (String cell : cells) {
                    Label label = new Label(cellCount, rowCount, cell);
                    sheet.addCell(label);
                    cellCount++;
                }
                rowCount++;
            }
            workbook.write();
            workbook.close();

            return "success";
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Logger.getLogger(FixCorruptedExcelFile.class).error("", e);
            return "error";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Logger.getLogger(FixCorruptedExcelFile.class).error("", e);
            return "error";
        } catch (RowsExceededException e) {
            // TODO Auto-generated catch block
            Logger.getLogger(FixCorruptedExcelFile.class).error("", e);
            return "error";
        } catch (WriteException e) {
            // TODO Auto-generated catch block
            Logger.getLogger(FixCorruptedExcelFile.class).error("", e);
            return "error";
        }

    }
}
