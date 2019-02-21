import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class App {

	public static void main(String[] args) {
		try {
			List<ReportEntity> details = readLogEntries();
			List<ReportEntity> conflicts = readConflictEntries(details);
			generateHTMLReport(conflicts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<ReportEntity> readLogEntries() {
		List<String> log_entries = readFileContent(new File("confsjfstmerge.txt"));
		List<ReportEntity> res = new ArrayList<ReportEntity>();
		ReportEntity re = new ReportEntity();
		boolean isReadingS3mOutput= false;
		boolean isReadingJDImeOutput= false;
		boolean isReadingLeftContent= false;
		boolean isReadingBaseContent= false;
		boolean isReadingRightContent= false;

		while(!log_entries.isEmpty()){
			String line = log_entries.remove(0);
			//managing markers
			if(line.contains("########################################################")){
				if(!re.files.isEmpty()){
					res.add(re);
				}
				re = new ReportEntity();
				isReadingS3mOutput	= false;
				isReadingJDImeOutput= false;
				isReadingLeftContent= false;
				isReadingBaseContent= false;
				isReadingRightContent = false;
				continue;
			} else if(line.contains("Files:")){
				re.files = line;
				re.files = (re.files).replaceAll("Files:","");
				re.files = ((re.files).replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
				continue;
			} else if(line.contains("Semistructured Merge Output:")){
				isReadingS3mOutput = true;
				continue;
			} else if(line.contains("Structured Merge Output:")){
				isReadingS3mOutput 	 = false;
				isReadingJDImeOutput = true;
				continue;
			} else if(line.contains("Left Content:")){
				isReadingJDImeOutput = false;
				isReadingLeftContent = true;
				continue;
			} else if(line.contains("Base Content:")){
				isReadingLeftContent = false;
				isReadingBaseContent = true;
				continue;
			} else if(line.contains("Right Content:")){
				isReadingBaseContent  = false;
				isReadingRightContent = true;
				continue;
			}

			if(isReadingS3mOutput){
				re.s3mOutput 	+= (line+'\n');;
			} else if(isReadingJDImeOutput){
				re.jdimeOutput 	+= (line+'\n');
			} else if(isReadingLeftContent){
				re.leftContent 	+= (line+'\n');
			} else if(isReadingBaseContent){
				re.baseContent 	+= (line+'\n');
			} else if(isReadingRightContent){
				re.rightContent	+= (line+'\n');
			}
		}
		return res;
	}

	private static List<ReportEntity> readConflictEntries(List<ReportEntity> details) throws Exception {
		List<String> log_entries = readFileContent(new File("logOTHER.txt"));

		List<ReportEntity> ress = new ArrayList<ReportEntity>();
		ReportEntity re = new ReportEntity();
		boolean isReadingFiles	= false;
		boolean isReadingType	= false;
		boolean isReadingS3MConflict		= false;
		boolean isReadingJDimeConflict		= false;
		boolean isReadingS3MDeclaration		= false;
		boolean isReadingJDimeDeclaration	= false;

		while(!log_entries.isEmpty()){
			String line = log_entries.remove(0);

			//managing markers
			if(line.contains("############################")){
				if(!re.files.isEmpty()){
					try{
						re.files = (re.files).replaceAll("----------------------------","");
						re.files = ((re.files).replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
						re.type = (re.type).replaceAll("----------------------------","").replaceAll("\\r\\n|\\r|\\n","").replaceAll("\\s+","");
						re.s3mConflict = (re.s3mConflict).replaceAll("----------------------------","");
						re.jdimeConflict = (re.jdimeConflict).replaceAll("----------------------------","");
						re.s3mDeclaration = (re.s3mDeclaration).replaceAll("----------------------------","");
						re.jdimeDeclaration = (re.jdimeDeclaration).replaceAll("----------------------------","");

						ReportEntity detail = details.stream()
								.filter(t -> t.files.contains(re.files))
								.collect(Collectors.toList()).get(0);
						re.s3mOutput = detail.s3mOutput;
						re.jdimeOutput = detail.jdimeOutput;
						re.leftContent = detail.leftContent;
						re.baseContent = detail.baseContent;
						re.rightContent = detail.rightContent;
						ress.add((ReportEntity)re.clone());
					} catch(Exception e){
					} finally {
						re.clean();
					}
				}
				isReadingFiles	= true;
				isReadingType	= false;
				isReadingS3MConflict		= false;
				isReadingJDimeConflict		= false;
				isReadingS3MDeclaration		= false;
				isReadingJDimeDeclaration	= false;
				continue;
			} else if(line.replaceAll("\\r\\n|\\r|\\n","").replaceAll("\\s+","").contains("CONCLUSAO:")){
				isReadingFiles= false;
				re.conclusao  = line;
				continue;
			} else if(line.replaceAll("\\r\\n|\\r|\\n","").replaceAll("\\s+","").equals("TYPE")){
				isReadingFiles= false;
				isReadingType = true;
				continue;
			} else if(line.replaceAll("\\r\\n|\\r|\\n","").replaceAll("\\s+","").equals("JFSTMERGE_CONF")){
				isReadingType= false;
				isReadingS3MConflict = true;
				continue;
			} else if(line.replaceAll("\\r\\n|\\r|\\n","").replaceAll("\\s+","").equals("JDIME_CONF")){
				isReadingS3MConflict = false;
				isReadingJDimeConflict = true;
				continue;
			} else if(line.replaceAll("\\r\\n|\\r|\\n","").replaceAll("\\s+","").equals("JFSTMERGE_DECL")){
				isReadingJDimeConflict = false;
				isReadingS3MDeclaration = true;
				continue;
			} else if(line.replaceAll("\\r\\n|\\r|\\n","").replaceAll("\\s+","").equals("JDIME_DECL")){
				isReadingS3MDeclaration = false;
				isReadingJDimeDeclaration = true;
				continue;
			} 


			if(isReadingFiles){
				re.files += (line+'\n');;
			} else if(isReadingType){
				re.type	+= (line+'\n');
			} else if(isReadingS3MConflict){
				re.s3mConflict 	+= (line+'\n');
			} else if(isReadingJDimeConflict){
				re.jdimeConflict += (line+'\n');
			} else if(isReadingS3MDeclaration){
				re.s3mDeclaration+= (line+'\n');
			} else if(isReadingJDimeDeclaration){
				re.jdimeDeclaration	+= (line+'\n');
			}
		}
		return ress;
	}

	private static void generateHTMLReport(List<ReportEntity> entries){

		String header = "<!DOCTYPE html>" +
				"<html lang=\"en\">" +
				"<head>" +
				"	<title>Report</title>" +
				"	<meta charset=\"UTF-8\">" +
				"	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
				"<!--===============================================================================================-->	" +
				"	<link rel=\"stylesheet\" type=\"text/css\" href=\"vendor/bootstrap/css/bootstrap.min.css\">" +
				"<!--===============================================================================================-->" +
				"	<link rel=\"stylesheet\" type=\"text/css\" href=\"fonts/font-awesome-4.7.0/css/font-awesome.min.css\">" +
				"<!--===============================================================================================-->" +
				"	<link rel=\"stylesheet\" type=\"text/css\" href=\"vendor/animate/animate.css\">" +
				"<!--===============================================================================================-->" +
				"	<link rel=\"stylesheet\" type=\"text/css\" href=\"vendor/select2/select2.min.css\">" +
				"<!--===============================================================================================-->" +
				"	<link rel=\"stylesheet\" type=\"text/css\" href=\"vendor/perfect-scrollbar/perfect-scrollbar.css\">" +
				"<!--===============================================================================================-->" +
				"	<link rel=\"stylesheet\" type=\"text/css\" href=\"css/util.css\">" +
				"	<link rel=\"stylesheet\" type=\"text/css\" href=\"css/main.css\">" +
				"<!--===============================================================================================-->" +
				"</head>" +
				"<body>" +
				"	" +
				"	<div class=\"limiter\">" +
				"		<div class=\"container-table100\">" +
				"			<div class=\"wrap-table100\">" +
				"				<div class=\"table100\">" +
				"					<table>" +
				"						<thead>" +
				"							<tr class=\"table100-head\">" +
				"								<th class=\"column1\">ID</th>" +
				"								<th class=\"column2\">Type</th>" +
				"								<th class=\"column3\">Files</th>" +
				"								<th class=\"column4\">Semistructured Conflict</th>" +
				"								<th class=\"column5\">Structured Conflict</th>" +
				"								<th class=\"column6\">Semistructured Declaration</th>" +
				"								<th class=\"column7\">Structured Declaration</th>" +
				"								<th class=\"column8\">Left Content</th>" +
				"								<th class=\"column9\">Base Content</th>" +
				"								<th class=\"column10\">Right Content</th>" +
				"								<th class=\"column11\">Semistructured Output</th>" +
				"								<th class=\"column12\">Structured Output</th>" +
				"								<th class=\"column13\">Findings</th>" +
				"							</tr>" +
				"						</thead>" +
				"						<tbody>";


		String bottom = "</tbody>" +
				"					</table>" +
				"				</div>" +
				"			</div>" +
				"		</div>" +
				"	</div>" +
				"	" +
				"<!--===============================================================================================-->	" +
				"	<script src=\"vendor/jquery/jquery-3.2.1.min.js\"></script>" +
				"<!--===============================================================================================-->" +
				"	<script src=\"vendor/bootstrap/js/popper.js\"></script>" +
				"	<script src=\"vendor/bootstrap/js/bootstrap.min.js\"></script>" +
				"<!--===============================================================================================-->" +
				"	<script src=\"vendor/select2/select2.min.js\"></script>" +
				"<!--===============================================================================================-->" +
				"	<script src=\"js/main.js\"></script>" +
				"</body>" +
				"</html>";
						

		StringBuilder body = new StringBuilder();
		for(int i = 0; i<entries.size(); i++){
			ReportEntity entry = entries.get(i);
			int id = (i+1);
			body.append("<tr>\n");
			body.append("<td class=\"column1\">" + id + "</td>\n"); 			
			body.append("<td class=\"column2\">" + entry.type + "</td>\n"); 	 
			
			String files = (new File((entry.files).split(",")[0])).getName();
			body.append("<td class=\"column3\"><a href=\""+ entry.type+"/" +id + "_files.txt"+ "\">" + files + "</a></td>\n"); 	
			writeContent("html/"+entry.type+"/" +id + "_files.txt", entry.files.replaceAll(",","\n"));

			body.append("<td class=\"column4\"><a href=\""+ entry.type+"/" +id + "_semi_conflict.java"+ "\">"+ id + "_semi_conflict</a></td>\n"); 
			writeContent("html/"+entry.type+"/" +id + "_semi_conflict.java", entry.s3mConflict);
			
			body.append("<td class=\"column5\"><a href=\""+ entry.type+"/" +id + "_stru_conflict.java"+ "\">"+ id + "_stru_conflict</a></td>\n");
			writeContent("html/"+entry.type+"/" +id + "_stru_conflict.java", entry.jdimeConflict);
			
			body.append("<td class=\"column6\"><a href=\""+ entry.type+"/" +id + "_semi_declaration.java"+ "\">"+ id + "_semi_declaration</a></td>\n"); 
			writeContent("html/"+entry.type+"/" +id + "_semi_declaration.java", entry.s3mDeclaration);
			
			body.append("<td class=\"column7\"><a href=\""+ entry.type+"/" +id + "_stru_declaration.java"+ "\">"+ id + "_stru_declaration</a></td>\n"); 
			writeContent("html/"+entry.type+"/" +id + "_stru_declaration.java", entry.jdimeDeclaration);

			body.append("<td class=\"column8\"><a href=\""+ entry.type+"/" +id + "_left.java"+ "\">" + id + "_left</a></td>\n");
			writeContent("html/"+entry.type+"/" +id + "_left.java", entry.leftContent);

			body.append("<td class=\"column9\"><a href=\""+ entry.type+"/" +id + "_base.java"+ "\">" + id + "_base</a></td>\n"); 
			writeContent("html/"+entry.type+"/" +id + "_base.java", entry.baseContent);

			body.append("<td class=\"column10\"><a href=\""+ entry.type+"/" +id + "_right.java"+ "\">"+ id + "_right</a></td>\n");
			writeContent("html/"+entry.type+"/" +id + "_right.java", entry.rightContent);

			body.append("<td class=\"column11\"><a href=\""+ entry.type+"/" +id + "_semi_output.java"+ "\">"+ id + "_semi_output</a></td>\n");
			writeContent("html/"+entry.type+"/" +id + "_semi_output.java", entry.s3mOutput);

			body.append("<td class=\"column12\"><a href=\""+ entry.type+"/" +id + "_stru_output.java"+ "\">"+ id + "_stru_output</a></td>\n"); 
			writeContent("html/"+entry.type+"/" +id + "_stru_output.java", entry.jdimeOutput);

			body.append("<td class=\"column13\">" + entry.conclusao + "</td>\n"); 	
			body.append("</tr>\n");
		}
		String report = header + body.toString() + bottom;
		writeContent("html/index.html", report);
	}

	private static List<String> readFileContent(File file){
		List<String> content = new ArrayList<>();
		try{
			BufferedReader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()), StandardCharsets.ISO_8859_1);
			content = reader.lines().collect(Collectors.toList());
		}catch(Exception e){
			e.printStackTrace();
		}
		return content;
	}

	public static boolean writeContent(String filePath, String content){
		filePath = filePath.replaceAll("\\r\\n|\\r|\\n","").replaceAll("\\s+","");
		if(content != null && !content.isEmpty()){
			try{
				File file = new File(filePath);
				if(!file.exists()){
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
				writer.write(content);
				writer.flush();	writer.close();
			} catch(NullPointerException ne){
				//empty, necessary for integration with git version control system
			} catch(Exception e){
				System.err.println(e.toString());
				return false;
			}
		}
		return true;
	}
}
