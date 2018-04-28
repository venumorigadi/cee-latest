package cee.ext;

import app2.gp.GrandPraireTextUtil;
import app2.harris.HarrisTextUtil;
import app2.richardson.RichardsonUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class ProcessCounty extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpServletRequest r = request;
            String county = "-1";
            //final String pd = getServletContext().getInitParameter("repository");
            HttpSession session = request.getSession();
            File file = null;
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> items = upload.parseRequest(request);
            String filename = (session.getId() + System.currentTimeMillis());

            for (FileItem field : items) {
                if (field.isFormField()) {
                    if (field.getFieldName().equalsIgnoreCase("uploadingContext")) {
                        county = field.getString();
                    }
                } else {
                    filename = field.getName();
                    filename = filename.substring(filename.lastIndexOf("\\") + 1, filename.length()).trim().replaceAll(" ", "-");
                    file = new File(getServletContext().getRealPath("/runtime/upload/" + filename));
                    if (file.exists() && file.isFile()) {
                        filename += "-" + file.getParentFile().listFiles().length;
                    }
                    file = new File(getServletContext().getRealPath("/runtime/upload/" + filename));
                    field.write(file);
                }
            }


            request.setAttribute("filename", filename);

            //there are better ways to get the proper county requested.
            //but this is a quick fix in lack of time.
            int options = Integer.parseInt(county.substring(county.length() - 1, county.length()));
            String savedfile = null;

            switch (options) {
                case 1:
                    savedfile = new HarrisTextUtil(filename, getServletContext()).perform();
                    break;
                case 2:
                    savedfile = new RichardsonUtil(filename, getServletContext()).perform();
                    break;
                case 3:
                    savedfile = new GrandPraireTextUtil(filename, getServletContext()).perform();
                    break;
                default:
                    Logger.getLogger(this.getClass()).log(Priority.ERROR, "no county found");
                    savedfile = null;
                    return;
            }

            if (savedfile != null) {
                request.setAttribute("succesMessage", "Done successfully.. ");
                request.setAttribute("genCsvFileLoc", getServletContext().getContextPath() + "/runtime/csv/" + filename + ".csv");
            } else {
                request.setAttribute("succesMessage", "There was some errors.");
            }
            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
            rd.forward(request, response);
            /**
            file = new File(pd + filename + csvext);
            response.setContentType("text/csv");
            response.setContentLength((int) (file.length()));
            response.setHeader("content-disposition", "attachment;filename=" + filename + csvext);
            OutputStream out = response.getOutputStream();
            InputStream is = new FileInputStream(file);
            byte[] contentBytes = new byte[1024];
            int c = 0;
            while ((c = is.read(contentBytes)) != -1) {
            out.write(contentBytes, 0, c);
            }
            out.close();
            log.removeFilename(filename);
            session.removeAttribute("filename");
             *
             */
        } catch (Exception exception) {
            Logger.getLogger(this.getClass()).error("error in request processing", exception);
        }

    }
}
