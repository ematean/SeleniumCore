package core.support.annotation.template.dataObject;

import java.io.BufferedWriter;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.JavaFileObject;

import core.support.annotation.helper.DataObjectHelper;
import core.support.annotation.helper.FileCreatorHelper;
import core.support.annotation.helper.PackageHelper;
import core.support.annotation.helper.PanelMapHelper;

public class ModuleClass {
	
	public static JavaFileObject CSV_File_Object = null;
	public static String MODULE_ROOT = "module";
	public static String DATA_ROOT = "data";

	
	  public static void writeModuleClass() throws Exception {
		  List<File> files = DataObjectHelper.getAllCsvDataFiles(); 

		  // return if no data files
		  if(files.isEmpty()) return;
		  
		 writeModuleClasses(files);
	  }
	
	/**
	 * write module class, initiating all csv data files
	 * @param files
	 * @throws Exception
	 */
	public static void writeModuleClasses(List<File> files) throws Exception {
		
		Map<String, List<File>> moduleMap = PanelMapHelper.getDataModuleMap(files);
		
		for (Entry<String, List<File>> entry : moduleMap.entrySet()) {
			writeModuleClass(entry);
		}
	}

	/**
	 * 
	 * 
		package data.webApp;
		
		public class webApp {
			
			public User user() {
				return new User();
			}
		}

	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void writeModuleClass(Entry<String, List<File>> entry) throws Exception {
		
		String module = entry.getKey();

		// create file: data.webApp.webApp.java
		String filePath = PackageHelper.DATA_PATH + "." + module + "." + module;
		JavaFileObject fileObject = FileCreatorHelper.createFileAbsolutePath(filePath);

		BufferedWriter bw = new BufferedWriter(fileObject.openWriter());

		Date currentDate = new Date();
		bw.append("/**Auto generated code,don't modify it.\n");
		bw.append("* Author             ---- > Auto Generated.\n");
		bw.append("* Date  And Time     ---- > " + currentDate.toString() + "\n");
		bw.append("*");
		bw.append("**/\n\n\n\n");
		bw.append("package " +  DATA_ROOT +"." + module + ";\n");
		bw.newLine();
		bw.newLine();
		
		bw.append("public class " + module + " {" + "\n");
		bw.newLine();
		bw.newLine();
		
//		public User user() {
//			return new User();
//		}
		for(File file : entry.getValue()) {
			String csvName =  file.getName().replaceFirst("[.][^.]+$", "");

			bw.append("public " + csvName + " " +  csvName.toLowerCase() +"() {" + "\n" );
			bw.append("    return new " + csvName + "();" + "\n");
			bw.append("}");
			bw.newLine();
			bw.newLine();
		}
		bw.newLine();
		bw.newLine();


		bw.append("}\n");

		bw.flush();
		bw.close();	
		
	}
}
