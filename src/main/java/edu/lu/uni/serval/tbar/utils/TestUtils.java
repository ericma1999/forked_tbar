package edu.lu.uni.serval.tbar.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.*;

public class TestUtils {


	public static int getFailTestNumInProject(String projectName, String defects4jPath, List<String> failedTests){
        String testResult = getDefects4jResult(projectName, defects4jPath, "test");
        System.out.println("this is the test result");
        System.out.println(testResult);

        Pattern pattern = Pattern.compile(".*Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+).*");


        Matcher m = pattern.matcher(testResult);
        // return 1;
        if (m.find()) {
            if (Integer.parseInt(m.group(2)) != 0) {
                return Integer.parseInt(m.group(2));
            }
        
        
            if (Integer.parseInt(m.group(3)) != 0) {
                return Integer.parseInt(m.group(3));
            }
        }

        System.out.println("WTF");

        if (testResult.equals("")){//error occurs in run
            return Integer.MAX_VALUE;
        }
        if (!testResult.contains("Failing tests:")){
            return Integer.MAX_VALUE;
        }

        return Integer.MAX_VALUE;





        // int errorNum = 0;
        // String[] lines = testResult.trim().split("\n");
        // for (String lineString: lines){
        //     if (lineString.startsWith("Failing tests:")){
        //         errorNum =  Integer.valueOf(lineString.split(":")[1].trim());
        //         if (errorNum == 0) break;
        //     } else if (lineString.startsWith("Running ")) {
        //     	break;
        //     } else {
        //     	failedTests.add(lineString.trim());
        //     }
        // }
        // return errorNum;
	}
	
//	public static int getFailTestNumInProject(String buggyProject, List<String> failedTests, String classPath,
//			String testClassPath, String[] testCasesArray){
//		StringBuilder builder = new StringBuilder();
//		for (String testCase : testCasesArray) {
//			builder.append(testCase).append(" ");
//		}
//		String testCases = builder.toString();
//		
//		String testResult = "";
//		try {
//			testResult = ShellUtils.shellRun(Arrays.asList("java -cp " + PathUtils.buildClassPath(classPath, testClassPath)
//					+ " org.junit.runner.JUnitCore " + testCases), buggyProject);
//		} catch (IOException e) {
////			e.printStackTrace();
//		}
//		
//        if (testResult.equals("")){//error occurs in run
//            return Integer.MAX_VALUE;
//        }
//        if (!testResult.contains("Failing tests:")){
//            return Integer.MAX_VALUE;
//        }
//        int errorNum = 0;
//        String[] lines = testResult.trim().split("\n");
//        for (String lineString: lines){
//            if (lineString.startsWith("Failing tests:")){
//                errorNum =  Integer.valueOf(lineString.split(":")[1].trim());
//                if (errorNum == 0) break;
//            } else if (lineString.startsWith("Running ")) {
//            	break;
//            } else {
//            	failedTests.add(lineString);
//            }
//        }
//        return errorNum;
//	}
	
	public static int compileProjectWithDefects4j(String projectName, String defects4jPath) {
		String compileResults = getDefects4jResult(projectName, defects4jPath, "compile");
		String[] lines = compileResults.split("\n");
		if (lines.length != 2) return 1;
        for (String lineString: lines){
        	if (!lineString.endsWith("OK")) return 1;
        }
		return 0;
	}

    private static JsonNode readJson(String bugProject) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String[] elements = bugProject.split("-");
        String x = elements[0] + "-" +elements[1];
        JsonNode jsonNode = mapper.readTree(new File(System.getProperty("user.dir") + "/data/" + x + ".json"));
        return jsonNode;
    }

	private static String getDefects4jResult(String projectName, String defects4jPath, String cmdType) {
		try {
			String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
			//which java\njava -version\n
            System.out.println(cmdType);
            System.out.println(projectName);
            String testCommand = "";
            String[] projectPath = projectName.split("/");
            JsonNode content = readJson(projectPath[projectPath.length - 1]);
            System.out.println("ahoy my friend");
            System.out.println(content.get("build_system").get("custom"));
            JsonNode custom = content.get("build_system").get("custom");

            if (custom.isArray()) {
                for (JsonNode config: custom) {
                    System.out.println(config);
                    if (cmdType == "test" && config.get("test") != null) {
                        System.out.println("The test command");
                        String x = config.get("test").textValue();
                        String[] potentialCommand = x.split("#");
                        String command;
                        if (potentialCommand.length != 2) {
                            command = x;
                        }else {
                            command = potentialCommand[0];
                        }
                        testCommand = command;

                    }
                    else if (cmdType == "compile" && config.get("compile") != null) {
                        System.out.println("The compile command");
                        System.out.println(config.get("compile"));
                        testCommand = config.get("compile").textValue();
                    }
                }
            }

//            if (cmdType == "compile") {
//                testCommand = "mvn -DskipTests clean install";
//            } else if (cmdType == "test") {
//                testCommand = "mvn test -Dtest=org.apache.commons.compress.archivers.zip.ZipArchiveInputStreamTest";
//            }

            String result = ShellUtils.shellRun(Arrays.asList("cd " + projectName + "\n", testCommand + "\n"), buggyProject, cmdType.equals("test") ? 2 : 1);//"defects4j " + cmdType + "\n"));//
            return result.trim();
        } catch (IOException e){
        	e.printStackTrace();
            return "";
        }
	}

	public static String recoverWithGitCmd(String projectName) {
		try {
			String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
            ShellUtils.shellRun(Arrays.asList("cd " + projectName + "\n", "git checkout -- ."), buggyProject, 1);
            return "";
        } catch (IOException e){
            return "Failed to recover.";
        }
	}

	public static String readPatch(String projectName) {
		try {
			String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
            return ShellUtils.shellRun(Arrays.asList("cd " + projectName + "\n", "git diff"), buggyProject, 1).trim();
        } catch (IOException e){
            return null;
        }
	}

	public static String checkout(String projectName) {
		try {
			String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
            return ShellUtils.shellRun(Arrays.asList("cd " + projectName + "\n", "git checkout -- ."), buggyProject, 1).trim();
        } catch (IOException e){
            return null;
        }
	}

}
