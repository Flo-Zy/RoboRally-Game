package SEPee.client;

import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ClientLogger {
    private static Logger logger = Logger.getLogger("MyClientLogger");

    static {
        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler(System.getProperty("user.dir") + System.getProperty("file.separator") + "ClientLogFile", true); // um log files anzufügen
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
