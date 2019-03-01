/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class newLogging {
    /*
     • ALL 	All levels including custom levels.
     • DEBUG 	Designates fine-grained informational events that are most useful to debug an application.
     • ERROR 	Designates error events that might still allow the application to continue running.
     • FATAL 	Designates very severe error events that will presumably lead the application to abort.
     • INFO 	Designates informational messages that highlight the progress of the application at coarse-grained level.
     • OFF 	The highest possible rank and is intended to turn off logging.
     • TRACE 	Designates finer-grained informational events than the DEBUG.
     • WARN 	Designates potentially harmful situations.
     There are seven logging levels provided by the Level class.
     • SEVERE (highest level)
     • WARNING
     • INFO
     • CONFIG
     • FINE
     • FINER
     • FINEST (lowest level)    
     */

    public String strRoot = System.getProperty("user.dir");

    public newLogging(String strLevel, String strClassName, String strMethodName, Exception strMessgae) {
        try {
            StringBuilder log = new StringBuilder();
            log.append(new Date());
            log.append(" ");
            log.append(strClassName);
            log.append(" ");
            log.append(strMethodName);
            log.append(" ");
            log.append(strLevel);
            log.append(" ");
            log.append(strMessgae);
            log.append(System.getProperty("line.separator"));
            PrintWriter writer = new PrintWriter(new FileOutputStream("logging.log", true));
            writer.println(log.toString());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
