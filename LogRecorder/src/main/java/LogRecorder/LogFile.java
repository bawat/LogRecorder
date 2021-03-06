package LogRecorder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.SneakyThrows;

public class LogFile {
    
    @SneakyThrows
    public static String getContents() {
        return new String(Files.readAllBytes(TodaysURI.provide()));
    }
    @SneakyThrows
    public static void append(String toAppend) {
        String timestamp = DateTimeFormatter.ofPattern("HH:mm ").format(LocalDateTime.now());
        Files.write(TodaysURI.provide(), (timestamp + toAppend).getBytes(), StandardOpenOption.APPEND);
    }
    
    public static class TodaysURI{
        private final static String logDumpFolder = "\\\\192.168.1.106\\Mark\\Wurm Employment Records\\Logs\\";
        private static String getFileName() {
            return DateTimeFormatter.ofPattern("yyyy-MM-LLL-dd").format(LocalDateTime.now()) + ".txt";
        }
        private static String getFolderDirectory() {
            return DateTimeFormatter.ofPattern("yyyy\\MM-LLL\\").format(LocalDateTime.now());
        }
        
        @SneakyThrows
        public static Path provide() {
            new File(logDumpFolder + getFolderDirectory()).mkdirs();
            File file = new File(logDumpFolder + getFolderDirectory() + getFileName());
            file.createNewFile();
            return Paths.get(file.getAbsolutePath());
        }
    }
}
