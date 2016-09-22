package code.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Writer {

	private File file;

	public Writer(File file) {
		this.file = file;
	}

	public void write(Map<String, Set<String>> dependencies) {
		try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
			br.write("var dependencies = {links:[\n");
			for (Map.Entry<String, Set<String>> entry : dependencies.entrySet()) {
				//System.out.println(entry.getKey() + ": " + Arrays.toString(entry.getValue().toArray(new String[entry.getValue().size()])));
				for (String dep : entry.getValue()) {
					br.write("{\"source\":\"" + entry.getKey() + "\",\"dest\":\"" + dep + "\"},\n");
				}
			}
			br.write("]};");
		} catch (FileNotFoundException e) {
			System.err.println("Cannot found " + file.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Cannot write " + file.getAbsolutePath());
		}
	}

}
