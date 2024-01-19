package SEPee.server.model;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerLogger {
    private static Logger logger = Logger.getLogger("MyServerLogger");

    static {
        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler(System.getProperty("user.dir") + System.getProperty("file.separator") + "ServerLogFile", true); // um log files anzufügen
            logger.addHandler(fileHandler);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);

            logger.info("ServerLogger initialized");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception :: ", e);
        }
    }

    public static void writeToServerLog(Object object){
        logger.info(String.valueOf(object));
    }

    public static void writeToServerLog(String message, Exception e){
        logger.log(Level.WARNING, message, e);
    }
}
