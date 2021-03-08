package LogRecorder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import lombok.SneakyThrows;

public class LogFile {
    
	@SneakyThrows
    public static String getContents() {
        return new String(Files.readAllBytes(TodaysURI.provide()));
    }
    @SneakyThrows
    public static void append(String toAppend) {
        String timestamp = DateTimeFormatter.ofPattern("HH:mm ").format(LocalDateTime.now());
        Files.write(TodaysURI.provide(), (timestamp + toAppend + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        System.out.println("Added to file '" + toAppend + "'");
    }
    @SneakyThrows
    public static void overwrite(String toDump) {
        System.out.println("Dumpeding to file...");
    	if(!toDump.endsWith(System.lineSeparator())) toDump += System.lineSeparator();
        Files.write(TodaysURI.provide(), toDump.getBytes(), StandardOpenOption.WRITE);
        System.out.println("Dumped to file");
    }
    
    public static class TodaysURI{
        private final static String logDumpFolder = "\\\\192.168.1.106\\Mark\\Wurm Employment Records\\Logs\\";
        private static String getTodaysFileName() {
        	return getFileName(LocalDateTime.now());
        }
        private static String getFileName(LocalDateTime time) {
            return DateTimeFormatter.ofPattern("yyyy-MM-LLL-dd").format(time) + ".txt";
        }
        private static String getTodaysFolderDirectory() {
        	return getFolderDirectory(LocalDateTime.now());
        }
        private static String getFolderDirectory(LocalDateTime time) {
            return DateTimeFormatter.ofPattern("yyyy\\MM-LLL\\").format(time);
        }
        
        @SneakyThrows
        public static Path provide() {
            new File(logDumpFolder + getTodaysFolderDirectory()).mkdirs();
            File file = new File(logDumpFolder + getTodaysFolderDirectory() + getTodaysFileName());
            file.createNewFile();
            return Paths.get(file.getAbsolutePath());
        }
    }
    
    @SneakyThrows
    private static Optional<Path> maybeProvidePreviousFilePath() {
    	for(int days = 1; days < 30 ; days++) {
    		LocalDateTime previousDate = LocalDateTime.now().minus(days, ChronoUnit.DAYS);
    		File file = new File(TodaysURI.logDumpFolder + TodaysURI.getFolderDirectory(previousDate) + TodaysURI.getFileName(previousDate));
    		if(file.exists()) return Optional.of(Paths.get(file.getAbsolutePath()));
    	}
        
        return Optional.empty();
    }
    @SneakyThrows
    public static Optional<String> maybeProvidePreviousFile() {
    	Optional<Path> path = maybeProvidePreviousFilePath();
    	if(path.isPresent()) return Optional.of(new String(Files.readAllBytes(path.get())));
        
        return Optional.empty();
    }
}
