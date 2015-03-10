package app.web.tw;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.hsqldb.Server;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/application-context.xml" })
public class BaseTest {

	private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
	
    private static final String HSQLDB_DRIVER = "org.hsqldb.jdbcDriver";
    private static final String DEFAULT_CONFIG_FILE = "hsqlserver.properties";
    private static final String URL_PROPERTY = "hsql.url";
    private static final String USER_PROPERTY = "hsql.user";
    private static final String PASSWORD_PROPERTY = "hsql.password";
    private static final String DATA_DIR_PROPERTY = "hsql.data.dir";
    private static final String DATABASE_PROPERTY = "hsql.database";
    private static Properties properties;
    
    public BaseTest() {
    	initHsql();
    }
    
    private void initHsql() {
    	properties = new Properties();
        try {
        	InputStream is =this.getClass().getResourceAsStream("/" + DEFAULT_CONFIG_FILE); 
        	properties.load(is);
            String databaseDir = properties.getProperty(DATA_DIR_PROPERTY);
            String database = properties.getProperty(DATABASE_PROPERTY);
            Server.main(new String[]{
                    "-database.0",
                    this.getClass().getResource("/") + databaseDir + "/" + database,
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
    
    public void finalize() {    	
    	hsqlDestoryed();
    }
    
    public void hsqlDestoryed() {
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

	public String toString() {
		return "BaseTest";
	}
	
}
