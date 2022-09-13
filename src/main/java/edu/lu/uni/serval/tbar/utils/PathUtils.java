package edu.lu.uni.serval.tbar.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
//import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.*;

public class PathUtils {

	public static ArrayList<String> getSrcPath(String bugProject) {
		ArrayList<String> path = new ArrayList<String>();
		String[] words = bugProject.split("-");
		String projectName = words[0];
		int bugId = Integer.parseInt(words[1]);
		if (projectName.equals("Math")) {
			if (bugId < 85) {
				path.add("/target/classes/");
				path.add("/target/test-classes/");
				path.add("/src/main/java/");
				path.add("/src/test/java/");
			} else {
				path.add("/target/classes/");
				path.add("/target/test-classes/");
				path.add("/src/java/");
				path.add("/src/test/");
			}
		} else if (projectName.equals("Time")) {
			if (bugId < 12) {
				path.add("/target/classes/");
				path.add("/target/test-classes/");
				path.add("/src/main/java/");
				path.add("/src/test/java/");
			} else {
				path.add("/build/classes/");
				path.add("/build/tests/");
				path.add("/src/main/java/");
				path.add("/src/test/java/");
			}
		} else if (projectName.equals("Lang")) {
			if (bugId <= 20) {
				path.add("/target/classes/");
				path.add("/target/tests/");
				path.add("/src/main/java/");
				path.add("/src/test/java/");
			} else if (bugId >= 21 && bugId <= 35) {
				path.add("/target/classes/");
				path.add("/target/test-classes/");
				path.add("/src/main/java/");
				path.add("/src/test/java/");
			} else if (bugId >= 36 && bugId <= 41) {
				path.add("/target/classes/");
				path.add("/target/test-classes/");
				path.add("/src/java/");
				path.add("/src/test/");
			} else if (bugId >= 42 && bugId <= 65) {
				path.add("/target/classes/");
				path.add("/target/tests/");
				path.add("/src/java/");
				path.add("/src/test/");
			}
		} else if (projectName.equals("Chart")) {
			path.add("/build/");
			path.add("/build-tests/");
			path.add("/source/");
			path.add("/tests/");

		} else if (projectName.equals("Closure")) {
			path.add("/build/classes/");
			path.add("/build/test/");
			path.add("/src/");
			path.add("/test/");
		} else if (projectName.equals("Mockito")) {
			if (bugId <= 11 || (bugId >= 18 && bugId <= 21)) {
				path.add("/build/classes/main/");
				path.add("/build/classes/test/");
				path.add("/src/");
				path.add("/test/");
			} else {
				path.add("/target/classes/");
				path.add("/target/test-classes/");
				path.add("/src/");
				path.add("/test/");
			}
		} else {
				JsonNode content;
				try {
					content = readJson(bugProject);
				} catch (IOException e) {
					e.printStackTrace();
					return path;
				}
				System.out.println(bugProject);
			System.out.println("YOYOY");
			System.out.println(content.get("method_line_before"));

				path.add("/" + content.get("src_classes").asText() + "/");
				path.add("/" + content.get("test_classes").asText() + "/");

				path.add(trimTrailingJava(content.get("src_path").asText()));
				path.add(trimTrailingJava(content.get("test_path").asText()));

//				path.add("/" + content.get("src_path").asText() + "/");
//				path.add("/" + content.get("test_path").asText() + "/");

//			System.out.println("/" + content.get("src_classes").asText() + "/");
//			System.out.println("/" + content.get("test_classes").asText() + "/");
//			System.out.println("/" + content.get("src_path").asText() + "/");
//			System.out.println("/" + content.get("test_path").asText() + "/");

//				path.add("/target/classes/");
//				path.add("/target/test-classes/");
//				path.add("/src/main/");
//				path.add("/src/test/");
			System.out.println("BRUH123");
		}
		
		return path;
	}

	public static String trimTrailingJava(String path) {
		String x = path;

		String[] content = x.split("/");
		String output = "";
		if (content[content.length - 1].equals("java")) {
			for (int i = 0; i < content.length - 1; i++) {
				output += content[i] + "/";
			}
			return "/" + output;
		}

		return x;

	}

	public static JsonNode readJson(String bugProject) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String[] elements = bugProject.split("-");
		String x = elements[0] + "-" +elements[1];
		JsonNode jsonNode = mapper.readTree(new File(System.getProperty("user.dir") + "/data/" + x + ".json"));
		return jsonNode;
	}

	public static String getJunitPath() {
		return System.getProperty("user.dir")+"/target/dependency/junit-4.12.jar";
	}
	
	private static String getHamcrestPath() {
		return System.getProperty("user.dir")+"/target/dependency/hamcrest-all-1.3.jar";
	}

	public static String buildCompileClassPath(List<String> additionalPath, String classPath, String testClassPath){
		String path = "\"";
		path += classPath;
		path += System.getProperty("path.separator");
		path += testClassPath;
		path += System.getProperty("path.separator");
		path += JunitRunner.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		path += System.getProperty("path.separator");
		path += StringUtils.join(additionalPath,System.getProperty("path.separator"));
		path += "\"";
		return path;
	}
	
	public static String buildTestClassPath(String classPath, String testClassPath) {
		String path = "\"";
		path += classPath;
		path += System.getProperty("path.separator");
		path += testClassPath;
		path += System.getProperty("path.separator");
		path += JunitRunner.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		path += System.getProperty("path.separator");
	    path += getJunitPath();
	    path += System.getProperty("path.separator");
	    path += getHamcrestPath();
	    path += System.getProperty("path.separator");
		path += "\"";
		return path;
    }

}
