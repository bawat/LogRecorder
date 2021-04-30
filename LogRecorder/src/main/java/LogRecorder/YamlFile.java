package LogRecorder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class YamlFile {
	@Getter @Setter
	public String logFolder 		= "None Specified",
				  logFolderBackup 	= "None Specified";
	
	private static Yaml yaml = new Yaml(new Constructor(YamlFile.class));
	private static final String FILE_NAME = "LogRecorderConfig.yml";
	
	@SneakyThrows
	public static YamlFile load() {
		if(!new File(FILE_NAME).exists())
			new YamlFile().save();
		try(var br = new BufferedReader(new FileReader(FILE_NAME))){
    		return yaml.load(br);
		}
	}
	
	
	public void save() {
		save(this);
	}
	@SneakyThrows
	private void save(YamlFile toSave) {
		try(var br = new BufferedWriter(new FileWriter(FILE_NAME))){
			yaml.dump(toSave, br);
		}
	}
	
}
