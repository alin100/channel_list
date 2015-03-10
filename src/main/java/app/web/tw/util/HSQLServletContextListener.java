package app.web.tw.util;


import org.hsqldb.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.web.tw.web.TestController;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class HSQLServletContextListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(HSQLServletContextListener.class);
	
    private static final String HSQLDB_DRIVER = "org.hsqldb.jdbcDriver";

    private static final String DEFAULT_CONFIG_FILE = "hsqlserver.properties";

    private static final String URL_PROPERTY = "hsql.url";
    private static final String USER_PROPERTY = "hsql.user";
    private static final String PASSWORD_PROPERTY = "hsql.password";
    private static final String DATA_DIR_PROPERTY = "hsql.data.dir";
    private static final String DATABASE_PROPERTY = "hsql.database";
    private static Properties properties;


    public void contextInitialized(ServletContextEvent sce) {
        properties = new Properties();
        try {
            ServletContext context = sce.getServletContext();

            String param = context.getInitParameter("HSQLDB_CONFIG");
            if (param == null || "".equals(param.trim())) {
                properties.load(this.getClass().getResourceAsStream(DEFAULT_CONFIG_FILE));
            } else {
            	File file = new File(context.getRealPath(param));
            	if (!file.exists()) {
            		logger.error("file not foune : ", file.toString());
            	}
                properties.load(new FileInputStream(file));
            }
            Class.forName(HSQLDB_DRIVER);
            String databaseDir = properties.getProperty(DATA_DIR_PROPERTY);
            String database = properties.getProperty(DATABASE_PROPERTY);
            logger.debug("databaseDir : " + context.getRealPath(databaseDir) + "/" + database);
            logger.debug("database : " + database);
            Server.main(new String[]{
                    "-database.0",
                    context.getRealPath(databaseDir + "/" + database),
                    "-dbname.0",
                    database,
                    "-no_system_exit",
                    "true"
            });
            logger.debug("HSQL DB activated.");
        } catch (RuntimeException ex) {
        	ex.printStackTrace();
            throw ex;
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        Connection connection = null;
        try {
            Class.forName(HSQLDB_DRIVER);
            connection = DriverManager.getConnection(
                    properties.getProperty(URL_PROPERTY),
                    properties.getProperty(USER_PROPERTY),
                    properties.getProperty(PASSWORD_PROPERTY));

            Statement stmt = connection.createStatement();
            stmt.executeUpdate("SHUTDOWN;");
            stmt.close();
        } catch (RuntimeException ex) {
        	ex.printStackTrace();
            throw ex;
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Cannot close database connection " + e.getMessage());
                }
            }
        }
    }
}