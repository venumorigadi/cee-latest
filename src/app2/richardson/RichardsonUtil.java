package app2.richardson;

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

public class RichardsonUtil {

    private List<RichardsonObject> list = new ArrayList<RichardsonObject>();
    private String filename = null;
    private ServletContext context;

    public RichardsonUtil(String filename, ServletContext context) {
        this.filename = filename;
        this.context = context;
        Logger.getLogger(this.getClass()).log(Priority.INFO, "RichardsonUtil init");
    }

    private String extractText() throws IOException {
        File input = new File(context.getRealPath("/runtime/upload/" + filename));
        PDDocument pd = PDDocument.load(input);
        PDFTextStripper pts = new PDFTextStripper();
        String text = pts.getText(pd);
        pd.close();
        return text;
    }

    private void loadText(String content) {
        String[] rows = content.split("[*]{131}");
        RichardsonParser parser = new RichardsonParser("");
        for (int i = 1; i < rows.length; i++) {
            parser.setRow(rows[i]);
            RichardsonObject object = new RichardsonObject();
            object.setApplicationNumber(parser.getApplicationNumber());
            object.setType(parser.getType());
            object.setZoning(parser.getZoning());
            object.setLocation(parser.getLocation());
            object.setConstructionArea(parser.getConstructionArea());
            object.setValuation(parser.getValuation());
            object.setAltAddress(parser.getAltAddress());
            object.setSuite(parser.getSuite());
            object.setOwner(parser.getOwner());
            object.setProperty(parser.getProperty());
            object.setGeneral(parser.getGeneral());
            object.setContractorNumber(parser.getContractorNumber());
            object.setPermit(parser.getPermit());
            object.setIssuedDate(parser.getIssued());
            object.setStatus(parser.getStatus());
            object.setStructure(parser.getStructureNumber());
            object.setSeq(parser.getSeqNumber());
            object.setWorkDescription(parser.getWorkDescription());
            object.setSubContractor(parser.getSubContractor());
            object.setDescription(parser.getDescription());
            String[] props = parser.extractSubRow();
            object.setOwnerInfo(props[0]);
            object.setPropertyInfo(props[1]);
            object.setContractorInfo(props[2]);
            object.setContractor(props[3]);
            list.add(object);
        }
    }

    private String getRichardsonCSV(String csvfilename) throws FileNotFoundException, IOException {
        try {
            Iterator<RichardsonObject> i = list.iterator();
            StringBuilder sb = new StringBuilder();
            sb.append(new CountyObject().getHeader());
            while (i.hasNext()) {
                RichardsonObject rObj = i.next();
                if (rObj.isRow()) {
                    CountyObject object = rObj.toCountyObject();
                    sb.append(object.getRow());
                    Logger.getLogger(this.getClass()).log(Priority.INFO, "found row - " + object.getPermitNumber());
                }
            }

            String str = sb.toString();
            FileOutputStream fos = new FileOutputStream(context.getRealPath(csvfilename));
            fos.write(str.getBytes(Charset.forName("utf-8")));
            fos.close();
            Logger.getLogger(this.getClass()).log(Priority.INFO, "file saved to - " + context.getRealPath(csvfilename));
            return csvfilename;
        } catch (Exception exception) {
            Logger.getLogger(this.getClass()).error("", exception);
            return null;
        }
    }

    public String perform() throws IOException {
        String content = extractText();
        loadText(content);
        return getRichardsonCSV("/runtime/csv/" + filename + ".csv");
    }
}
