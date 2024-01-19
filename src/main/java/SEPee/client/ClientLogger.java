package SEPee.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ClientLogger {
    private static Logger logger = Logger.getLogger("ClientLogger");

    static {
        FileHandler fileHandler;
        try {
            // für unique logFile
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String clientLogFile = "ClientLogger_" + timestamp + ".log";

            fileHandler = new FileHandler(System.getProperty("user.dir") + System.getProperty("file.separator") + clientLogFile, true); // um log files anzufügen
            logger.addHandler(fileHandler);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);

            logger.info("ClientLogger initialized");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception :: ", e);
        }
    }

    public static void writeToClientLog(Object object) {
        logger.info(String.valueOf(object));
    }
    public static void writeToClientLog(String message, Exception e){
        logger.log(Level.WARNING, message, e);
    }
}
