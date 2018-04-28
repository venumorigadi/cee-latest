package app2.harris;

import app2.CountyObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class HarrisTextUtil {

    private List<HarrisObject> list;
    private int rows;
    private String filename;
    private ServletContext context;

    public HarrisTextUtil(String filename, ServletContext context) {
        this.filename = filename;
        list = new ArrayList<HarrisObject>();
        rows = 0;
        this.context = context;
        Logger.getLogger(this.getClass()).log(Priority.INFO, "HarrisTextUtil init");
    }

    public String perform() throws IOException {
        String text = extractText("/runtime/upload/" + filename);
        loadText(text);
        return getHarrisCSV("/runtime/csv/" + filename + ".csv");
    }

    private String extractText(String filename) throws IOException {
        File input = new File(context.getRealPath(filename));
        PDDocument pd = PDDocument.load(input);
        PDFTextStripper pts = new PDFTextStripper();
        pts.setWordSeparator("||");
        String text = pts.getText(pd);
        pd.close();
        return text;
    }

    private void loadText(String content) {
        String lines[] = content.split("\n");
        for (int i = 1; i < lines.length; i++) {
            addToList(lines[i]);
        }
    }

    private void addToList(String row) {
        String columns[] = row.split("\\|\\|");
        HarrisObject object = new HarrisObject();
        for (int i = 1; i < columns.length; i++) {
            object.setObject(i, columns[i]);
        }
        list.add(object);
    }

    private String getHarrisCSV(String csvfilename) throws FileNotFoundException, IOException {
        try {
            Iterator<HarrisObject> i = list.iterator();
            StringBuilder sb = new StringBuilder();
            sb.append(new CountyObject().getHeader());
            while (i.hasNext()) {
                HarrisObject hObj = i.next();
                if (hObj.isRow()) {
                    CountyObject object = hObj.toCountyObject();
                    sb.append(object.getRow());
                    ++rows;
                    Logger.getLogger(this.getClass()).log(Priority.INFO, "found row - " + object.getPermitNumber());
                }
            }
            File optfile = new File(context.getRealPath(csvfilename));
            String str = sb.toString();
            FileOutputStream fos = new FileOutputStream(optfile);
            fos.write(str.getBytes(Charset.forName("iso-8859-1")));
            fos.close();
            Logger.getLogger(this.getClass()).log(Priority.INFO, "file saved to - " + context.getRealPath(csvfilename));
            return optfile.getAbsolutePath();
        } catch (Exception exception) {
            Logger.getLogger(this.getClass()).error("", exception);
            return null;
        }
    }
}
