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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;

import LogRecorder.LogFile.ExtendString;

@ExtensionMethod({java.util.Arrays.class, ExtendString.class})
public class LogFile {
    
	public static class ExtendString {
		public static String trimBeginning(String in) {
			Matcher matcher = Pattern.compile("^([^a-zA-Z0-9_]*).*").matcher(in);
			if(!matcher.lookingAt() || matcher.groupCount() < 1)
				return in;
			
			return in.replace(matcher.group(1), "");
		}
	}
	
	@SneakyThrows
    public static String getContents() {
        return new String(Files.readAllBytes(TodaysURI.provide()));
    }
    @SneakyThrows
    public static void append(String toAppend) {
    	toAppend = toAppend.trimBeginning();
        String timestamp = DateTimeFormatter.ofPattern("HH:mm ").format(LocalDateTime.now());
        Files.write(TodaysURI.provide(), (System.lineSeparator() + timestamp + toAppend).getBytes(), StandardOpenOption.APPEND);
        System.out.println("Added to file '" + toAppend + "'");
    }
    @SneakyThrows
    public static void overwrite(String toDump) {
        System.out.println("Dumping to file...");
        Files.write(TodaysURI.provide(), toDump.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("Dumped to file");
    }
    
    public static class TodaysURI{
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
        private static String getLogDumpFolder() {
        	return YamlFile.load().logFolder + "\\";
        }
        
        @SneakyThrows
        public static Path provide() {
            new File(getLogDumpFolder() + getTodaysFolderDirectory()).mkdirs();
            File file = new File(getLogDumpFolder() + getTodaysFolderDirectory() + getTodaysFileName());
            file.createNewFile();
            return Paths.get(file.getAbsolutePath());
        }
    }
    
    @SneakyThrows
    private static Optional<Path> maybeProvidePreviousFilePath() {
    	for(int days = 1; days < 30 ; days++) {
    		LocalDateTime previousDate = LocalDateTime.now().minus(days, ChronoUnit.DAYS);
    		File file = new File(TodaysURI.getLogDumpFolder() + TodaysURI.getFolderDirectory(previousDate) + TodaysURI.getFileName(previousDate));
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
