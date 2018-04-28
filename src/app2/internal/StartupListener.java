package app2.internal;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class StartupListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Logger logger = null;
        String log4jFile = sce.getServletContext().getInitParameter("logger");
        DOMConfigurator.configure(sce.getServletContext().getRealPath(log4jFile));
        logger = LogManager.getLogger(StartupListener.class.getName());
        
        logger.debug("Loaded: " + log4jFile);
    }
}
