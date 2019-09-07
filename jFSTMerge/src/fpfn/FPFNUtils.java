package fpfn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import jdime.fpfn.FixedMergeConflict;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.files.FilesTuple;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.MergeScenario;
import de.fosd.jdime.artifact.ast.ASTNodeArtifact;
import fpfn.Difference.Type;

public final class FPFNUtils {

	/*
	public static boolean isConsecutiveLineConflict(MergeConflict mergeConflict) {
		boolean falsePositive = false;
		if ((mergeConflict.left!=null && mergeConflict.base!=null && mergeConflict.right!=null) &&
				(!mergeConflict.left.equals("")&& !mergeConflict.base.equals("") && !mergeConflict.right.equals(""))) {
			String[] leftLines = mergeConflict.left.split("\n");
			String[] baseLines = mergeConflict.base.split("\n");
			String[] rightLines = mergeConflict.right.split("\n");
			if (baseLines.length!=0 && !baseLines[0].equals("")) {
				String fixedElement = baseLines[0];
				boolean foundOnLeft = searchFixedElement(fixedElement,
						leftLines);
				if (foundOnLeft) {
					falsePositive = true;
				} else {
					boolean foundOnRight = searchFixedElement(fixedElement,
							rightLines);
					if (foundOnRight) {
						falsePositive = true;
					}
				}
			}

		}
		return falsePositive;
	}
	 */

	public static boolean isConsecutiveLineConflict(MergeConflict mergeConflict) {
		boolean result = false;
		/*		if (	 (mergeConflict.left!=null && mergeConflict.base!=null && mergeConflict.right!=null) &&
				(!mergeConflict.left.isEmpty() && !mergeConflict.base.isEmpty() && !mergeConflict.right.isEmpty())) {
			String[] leftLines = mergeConflict.left.split("\n");
			String[] baseLines = mergeConflict.base.split("\n");
			String[] rightLines = mergeConflict.right.split("\n");
			if (baseLines.length!=0 && !baseLines[0].equals("")) {
				String fixedElement = baseLines[0];
				boolean foundOnLeft = searchFixedElement(fixedElement,leftLines);
				if (foundOnLeft) {
					result = true;
				} else {
					boolean foundOnRight = searchFixedElement(fixedElement,rightLines);
					if (foundOnRight) {
						result = true;
					}
				}
			}

		}*/
		//if(result){ //potential consecutive lines conflict
		List<Integer> lb = findLinesContributions(mergeConflict.left, mergeConflict.base); //diff2 left to base
		List<Integer> rb = findLinesContributions(mergeConflict.right, mergeConflict.base);//diff2 right to base
		if(!lb.isEmpty() && !rb.isEmpty()){
			int maxLineLeft  = lb.get(lb.size()-1);
			int minLineLeft  = lb.get(0);

			int maxLineRight = rb.get(rb.size()-1);
			int minLineRight  =rb.get(0);

			result = (maxLineLeft < minLineRight) || (maxLineRight < minLineLeft);
		}
		//}
		return result;
	}

	public static boolean isSpacingConflict(MergeConflict mc) {
		if(mc.base != null && mc.right != null){
			if( FilesManager.getStringContentIntoSingleLineNoSpacing(mc.right).equals(FilesManager.getStringContentIntoSingleLineNoSpacing(mc.base))) 		
				return true;
		} 
		if(mc.base != null && mc.left != null){
			if(FilesManager.getStringContentIntoSingleLineNoSpacing(mc.left).equals(FilesManager.getStringContentIntoSingleLineNoSpacing(mc.base))) 		
				return true;
		} 
		if(mc.right != null & mc.left != null){
			if(FilesManager.getStringContentIntoSingleLineNoSpacing(mc.right).equals(FilesManager.getStringContentIntoSingleLineNoSpacing(mc.left))) 
				return true;
		}
		return false;
	}

	public static boolean isRenamingOrDeletionConflict(String leftContent,	String baseContent, String rightContent) {
		return !baseContent.isEmpty() && (leftContent.isEmpty() || rightContent.isEmpty());
	}

	public static String extractSignature(String mergedBodyContentInclBase) {
		return mergedBodyContentInclBase.split("\\{")[0];
	}

	public static ASTNodeArtifact getMethodNode(ASTNodeArtifact conf) {
		ASTNodeArtifact parent = conf.getParent();
		if (parent == null || parent.isMethod()) {
			return parent;
		} else {
			return getMethodNode(parent);
		}
	}

	public static String getMethodBody(ASTNodeArtifact methodDecl) {
		return methodDecl.prettyPrint();
	}

	public static Difference getOrCreateDifference(
			List<Difference> differences, String signature,
			MergeConflict jdimeMergeConflict) {
		for (Difference diff : differences) {
			if (areSignatureEqual(signature, diff.signature)) {
				if (areSimilarConflicts(diff.jfstmergeConf, jdimeMergeConflict)) {
					return diff;
				}
			}
		}
		Difference diff = new Difference();
		differences.add(diff);
		return diff;
	}

	public static boolean areSignatureEqual(String signature,
			String othersignature) {
		return FilesManager.getStringContentIntoSingleLineNoSpacing(
				othersignature)
				.equals(FilesManager
						.getStringContentIntoSingleLineNoSpacing(signature));
	}

	public static void countAndPrintStatsAndLogOfDifferences(MergeContext context) {
		try {
			if (null != context.semistructuredOutput
					&& null != context.structuredOutput) {
				//FPFN uncomment log(context);
				count(context);

				//printing merged files for further analyzes
				try{
					String path = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator + "mergedFiles" + File.separator;
					new File(path).mkdirs(); //ensuring it exists
					//				FilesManager.writeContent(path + (context.fullQualifiedMergedClass).split(",")[2] + ".unstructured", context.unstructuredOutput); only filename
					//				FilesManager.writeContent(path + (context.fullQualifiedMergedClass).split(",")[2] + ".semistructured", context.semistructuredOutput);
					//				FilesManager.writeContent(path + (context.fullQualifiedMergedClass).split(",")[2] + ".structured", context.structuredOutput);
					FilesManager.writeContent(path + (context.fullQualifiedMergedClass).split("/")[1] + ".unstructured", context.unstructuredOutput); //project,merge commit, filename
					FilesManager.writeContent(path + (context.fullQualifiedMergedClass).split("/")[1] + ".semistructured", context.semistructuredOutput);
					FilesManager.writeContent(path + (context.fullQualifiedMergedClass).split("/")[1] + ".structured", context.structuredOutput);
				} catch(Exception e) {
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void countAndPrintDifferencesNumbersByScenario(MergeScenario scenario) throws FileNotFoundException {
		String scenarioId 					= scenario.getRevisionsFilePath();
		int consecutiveLinesonly 			= 0;
		int spacingonly 					= 0;
		int samePositiononly 				= 0;
		int sameStmtonly 					= 0;
		int otheronly 						= 0;
		int consecutiveLinesAndSamePosition = 0;
		int consecutiveLinesAndsameStmt 	= 0;
		int otherAndsamePosition 			= 0;
		int otherAndsameStmt 				= 0;
		int spacingAndSamePosition 			= 0;
		int spacingAndSameStmt 				= 0;

		int ssmergeConf = 0;
		int jdimeConf   = 0;
		int textualConf = 0;

		//counting
		for(FilesTuple tp : scenario.getTuples()){
			MergeContext ctx = tp.getContext();
			if(ctx != null){
				for (Difference diff : ctx.differences) {
					if (diff.types.contains(Type.CONSECUTIVE_LINES)
							&& diff.types.contains(Type.SAME_POSITION)) {
						consecutiveLinesAndSamePosition++;
					} else if (diff.types.contains(Type.CONSECUTIVE_LINES)
							&& diff.types.contains(Type.SAME_STATEMENT)) {
						consecutiveLinesAndsameStmt++;
					} else if (diff.types.contains(Type.OTHER)
							&& diff.types.contains(Type.SAME_POSITION)) {
						otherAndsamePosition++;
					} else if (diff.types.contains(Type.OTHER)
							&& diff.types.contains(Type.SAME_STATEMENT)) {
						otherAndsameStmt++;
					} else if (diff.types.contains(Type.SPACING)
							&& diff.types.contains(Type.SAME_POSITION)) {
						spacingAndSamePosition++;
					} else if (diff.types.contains(Type.SPACING)
							&& diff.types.contains(Type.SAME_STATEMENT)) {
						spacingAndSameStmt++;
					} else if (diff.types.contains(Type.CONSECUTIVE_LINES)) {
						consecutiveLinesonly++;
					} else if (diff.types.contains(Type.SPACING)) {
						spacingonly++;
					} else if (diff.types.contains(Type.SAME_POSITION)) {
						samePositiononly++;
					} else if (diff.types.contains(Type.SAME_STATEMENT)) {
						sameStmtonly++;
					} else if (diff.types.contains(Type.OTHER)) {
						otheronly++;
					}
				}

				ssmergeConf += FilesManager.extractMergeConflicts(ctx.semistructuredOutput).size();
				jdimeConf 	+= FilesManager.extractMergeConflicts(ctx.structuredOutput).size();
				textualConf += FilesManager.extractMergeConflicts(ctx.unstructuredOutput).size();
			}
		}


		//printing
		String header = "revision;consecutiveLinesonly;spacingonly;samePositiononly;sameStmtonly;otheronly;consecutiveLinesAndSamePosition;consecutiveLinesAndsameStmt;otherAndsamePosition;otherAndsameStmt;spacingAndSamePosition;spacingAndSameStmt;ssmergeConf;textualConf;jdimeConf";
		String logPath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator + "numbersDifferencesScenarios.csv";
		String logEntry = scenarioId + ";" + consecutiveLinesonly + ";" 
				+ spacingonly + ";" + samePositiononly + ";" + sameStmtonly
				+ ";" + otheronly + ";" + consecutiveLinesAndSamePosition + ";"
				+ consecutiveLinesAndsameStmt + ";" + otherAndsamePosition
				+ ";" + otherAndsameStmt + ";" + spacingAndSamePosition + ";"
				+ spacingAndSameStmt + ";" + ssmergeConf + ";"
				+ textualConf + ";" + jdimeConf;

		File out = new File(logPath);
		PrintWriter pw; 
		if (!out.exists()) {
			pw = new PrintWriter(new FileOutputStream(out, true), true);
			pw.append(header + "\n");
			pw.append(logEntry + "\n");
		} else {
			pw = new PrintWriter(new FileOutputStream(out, true), true);
			pw.append(logEntry + "\n");
		}
		pw.close();


	}

	public static String fixJdimeMergeConflicts(String src, List<FixedMergeConflict> mergeConflictsToFix){
		for(FixedMergeConflict fmc : mergeConflictsToFix){
			String[] outputLines = src.split("(\r\n|\r|\n)", -1);
			String[] fmcLines = fmc.conflictingCode.split("(\r\n|\r|\n)", -1);
			int matchCandidateStartingIdx = -1;
			int matchCandidateEndIdx = -1;
			String f = FilesManager.getStringContentIntoSingleLineNoSpacing(fmcLines[0]);
			for(int j = 0; j < outputLines.length; j++){
				String o = FilesManager.getStringContentIntoSingleLineNoSpacing(outputLines[j]);
				if(f.equals(o)){
					matchCandidateStartingIdx = j;
					matchCandidateEndIdx = j+fmcLines.length;
					try{
						String[] suboutput = Arrays.copyOfRange(outputLines, matchCandidateStartingIdx, matchCandidateEndIdx);
						for(int h = 0; h<suboutput.length; h++){
							String l = FilesManager.getStringContentIntoSingleLineNoSpacing(suboutput[h]);
							String r = FilesManager.getStringContentIntoSingleLineNoSpacing(fmcLines[h]);
							if(!l.equals(r)){
								matchCandidateStartingIdx = -1;
								matchCandidateEndIdx= - 1;
								break;
							}
						}
					} catch(Exception e){
						matchCandidateStartingIdx = -1;
						matchCandidateEndIdx= - 1;
					}
					if(matchCandidateStartingIdx > 0 && matchCandidateEndIdx > 0){break; /*found match declaration*/}
				}
			}
			if(matchCandidateStartingIdx > 0 && matchCandidateEndIdx > 0){
				String[] begin = Arrays.copyOfRange(outputLines,0,matchCandidateStartingIdx);
				String[] end = Arrays.copyOfRange(outputLines,matchCandidateEndIdx+1,outputLines.length);
				src = String.join("\n", begin) + "\n" + fmc.toString() + "\n" + String.join("\n", end);
			}
		}
		return src;
	}

	public static List<Integer> findLinesContributions(String parentContent, String baseContent){
		List<Integer> allContribs = new ArrayList<>(); 

		try{
			//writing temp files for diffing
			long time = System.currentTimeMillis();
			File tmpDir = new File(System.getProperty("user.dir")+ File.separator + "fstmerge_tmp" + time);
			tmpDir.mkdir();
			File left = File.createTempFile("fstmerge_var1_", ".java",tmpDir);
			File base = File.createTempFile("fstmerge_base_", ".java",tmpDir);

			FilesManager.writeContent(left, parentContent);
			FilesManager.writeContent(base, baseContent);

			//diff
			String diffcmd = "C:/KDiff3/bin/diff.exe " + "\"" + left.getPath() + "\"" + " " + "\"" + base.getPath() + "\"";
			if (!System.getProperty("os.name").contains("Windows")) {
				diffcmd = "diff " + "\"" + left.getPath() + "\"" + " " + "\"" + base.getPath() + "\"";
			}
			Runtime runTime = Runtime.getRuntime();
			Process process = runTime.exec(diffcmd);

			String changePattern		= "\\d+(,)?\\d*c\\d+(,)?\\d*";
			Set<String> changedLines 	= new TreeSet<>();

			BufferedReader buffer 	= new BufferedReader(new InputStreamReader(process.getInputStream()));
			String currentLine 		= "";
			while ((currentLine=buffer.readLine())!=null) {
				//System.out.println(currentLine);
				if(currentLine.matches(changePattern)){
					String[] contributions = currentLine.split("c");
					for(String changedLine : contributions){
						if(changedLine.contains(",")){
							changedLines.addAll(Arrays.asList(changedLine.split(",")));
						} else {
							changedLines.add(changedLine);
						}
					}
				}
			}

			allContribs = changedLines.stream().map(t -> Integer.parseInt(t)).collect(Collectors.toList());
			Collections.sort(allContribs);

			left.delete();
			base.delete();
			tmpDir.delete();
			buffer.close();

		} catch(Exception e){
			e.printStackTrace();
		}
		return allContribs;
	}

	private static boolean areSimilarConflicts(MergeConflict jfstmergeConf,	MergeConflict jdimeMergeConflict) {
		if (null == jfstmergeConf || null == jdimeMergeConflict)
			return false;
		else {
			String ljfst = FilesManager.getStringContentIntoSingleLineNoSpacing(jfstmergeConf.left);
			String rjfst = FilesManager.getStringContentIntoSingleLineNoSpacing(jfstmergeConf.right);

			String ljdm = FilesManager.getStringContentIntoSingleLineNoSpacing(jdimeMergeConflict.left);
			String rjdm = FilesManager.getStringContentIntoSingleLineNoSpacing(jdimeMergeConflict.right);

			return (	
					ljfst.contains(ljdm) || 
					ljdm.contains(ljfst) || 
					rjfst.contains(rjdm) || 
					rjdm.contains(rjfst)
					);
		}
	}

	private static void count(MergeContext context)
			throws FileNotFoundException {
		int consecutiveLinesonly = 0;
		int spacingonly = 0;
		int samePositiononly = 0;
		int sameStmtonly = 0;
		int otheronly = 0;
		int consecutiveLinesAndSamePosition = 0;
		int consecutiveLinesAndsameStmt = 0;
		int otherAndsamePosition = 0;
		int otherAndsameStmt = 0;
		int spacingAndSamePosition = 0;
		int spacingAndSameStmt = 0;

		int ssmergeConf = 0;
		int jdimeConf   = 0;
		int textualConf = 0;

		int ssmergeConfsInDecl = 0;
		int jdimeConfsInDecl = 0;

		for (Difference diff : context.differences) {
			if (diff.types.contains(Type.CONSECUTIVE_LINES)
					&& diff.types.contains(Type.SAME_POSITION)) {
				consecutiveLinesAndSamePosition++;
			} else if (diff.types.contains(Type.CONSECUTIVE_LINES)
					&& diff.types.contains(Type.SAME_STATEMENT)) {
				consecutiveLinesAndsameStmt++;
			} else if (diff.types.contains(Type.OTHER)
					&& diff.types.contains(Type.SAME_POSITION)) {
				otherAndsamePosition++;
			} else if (diff.types.contains(Type.OTHER)
					&& diff.types.contains(Type.SAME_STATEMENT)) {
				otherAndsameStmt++;
			} else if (diff.types.contains(Type.SPACING)
					&& diff.types.contains(Type.SAME_POSITION)) {
				spacingAndSamePosition++;
			} else if (diff.types.contains(Type.SPACING)
					&& diff.types.contains(Type.SAME_STATEMENT)) {
				spacingAndSameStmt++;
			} else if (diff.types.contains(Type.CONSECUTIVE_LINES)) {
				consecutiveLinesonly++;
			} else if (diff.types.contains(Type.SPACING)) {
				spacingonly++;
			} else if (diff.types.contains(Type.SAME_POSITION)) {
				samePositiononly++;
			} else if (diff.types.contains(Type.SAME_STATEMENT)) {
				sameStmtonly++;
			} else if (diff.types.contains(Type.OTHER)) {
				otheronly++;
			}
		}

		ssmergeConf = FilesManager.extractMergeConflicts(context.semistructuredOutput).size();
		jdimeConf 	= FilesManager.extractMergeConflicts(context.structuredOutput).size();
		textualConf = FilesManager.extractMergeConflicts(context.unstructuredOutput).size();

		context.isSsEqualsToUn = FilesManager.getStringContentIntoSingleLineNoSpacing(FilesManager.stripComments(context.semistructuredOutput)).equals(FilesManager.getStringContentIntoSingleLineNoSpacing(FilesManager.stripComments(context.unstructuredOutput)));
		context.isStEqualsToUn = FilesManager.getStringContentIntoSingleLineNoSpacing(FilesManager.stripComments(context.structuredOutput)).equals(FilesManager.getStringContentIntoSingleLineNoSpacing(FilesManager.stripComments(context.unstructuredOutput)));

		ssmergeConfsInDecl = context.ssmergeConfsInDecl;
		jdimeConfsInDecl = context.jdimeConfsInDecl;

		String mergedFiles = context.fullQualifiedMergedClass;
		String logPath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator + "numbers-current-file.csv";
		String header = "files;"
				+ "consecutiveLinesonly;"
				+ "spacingonly;"
				+ "samePositiononly;"
				+ "sameStmtonly;"
				+ "otheronly;"
				+ "consecutiveLinesAndSamePosition;"
				+ "consecutiveLinesAndsameStmt;"
				+ "otherAndsamePosition;"
				+ "otherAndsameStmt;"
				+ "spacingAndSamePosition;"
				+ "spacingAndSameStmt;"
				+ "ssmergeConf;"
				+ "textualConf;"
				+ "jdimeConfs;"
				+ "smergeTime;"
				+ "textualTime;"
				+ "jdimeTime;"
				+ "sucessfullMerge;"
				+ "isSsEqualsToUn;"
				+ "isStEqualsToUn;"
				+ "changedMethods;" 
				+ "commonChangedMethods;"
				+ "ssmergeConfsInDecl;" 
				+ "jdimeConfsInDecl" 
				;

		String logEntry = mergedFiles + ";" 
				+ consecutiveLinesonly + ";" 
				+ spacingonly + ";" 
				+ samePositiononly + ";" 
				+ sameStmtonly + ";" 
				+ otheronly + ";" 
				+ consecutiveLinesAndSamePosition + ";"
				+ consecutiveLinesAndsameStmt + ";" 
				+ otherAndsamePosition + ";" 
				+ otherAndsameStmt + ";" 
				+ spacingAndSamePosition + ";"
				+ spacingAndSameStmt + ";" 
				+ ssmergeConf + ";"
				+ textualConf + ";" 
				+ jdimeConf + ";" 
				+ context.semistructuredMergeTime + ";" 
				+ context.unstructuredMergeTime + ";" 
				+ context.structuredMergeTime + ";" 
				+ context.sucessfullMerge+ ";" 
				+ context.isSsEqualsToUn + ";" 
				+ context.isStEqualsToUn + ";"
				+ context.changedMethods + ";" 
				+ context.commonChangedMethods + ";" 
				+ ssmergeConfsInDecl + ";"
				+ jdimeConfsInDecl 
				;

		File out = new File(logPath);
		PrintWriter pw; 
		if (!out.exists()) {
			pw = new PrintWriter(new FileOutputStream(out, true), true);
			pw.append(header 	+ "\n");
			pw.append(logEntry 	+ "\n");
		} else {
			pw = new PrintWriter(new FileOutputStream(out, true), true);
			pw.append(logEntry 	+ "\n");
		}
		pw.close();
	}

	private static  void addNumberOfLineContribution(List<String> contribLinesFromLeft, List<String> contribLinesFromRight, String fileIndicator, String line) {
		if(	fileIndicator.equals("1")){
			contribLinesFromLeft.add(line);
		}  else if (fileIndicator.equals("3")) {
			contribLinesFromRight.add(line);
		}
	}

	private static boolean searchFixedElement(String fixedElement,
			String[] variant) {
		boolean foundFixedElement = false;
		int i = 0;
		while (!foundFixedElement && i < variant.length) {
			if (variant[i].equals(fixedElement)) {
				foundFixedElement = true;
			}
			i++;
		}
		return foundFixedElement;
	}

	private static void log(MergeContext context) throws FileNotFoundException {
		String mergedFiles = 
				((context.getLeft()!=null)? context.getLeft() .getPath() : "empty") + ","
						+ ((context.getBase()!=null)? context.getBase() .getPath() : "empty") + ","
						+ ((context.getRight()!=null)?context.getRight().getPath() : "empty") + "\n";
		for (Difference diff : context.differences) {
			if(!diff.types.isEmpty()){
				String logPath = System.getProperty("user.home") + File.separator
						+ ".jfstmerge" + File.separator + "log"
						+ diff.getTypeIntoString() + ".txt";
				String logEntry = mergedFiles + diff.toString();

				PrintWriter pw = new PrintWriter(new FileOutputStream(new File(
						logPath), true), true);
				pw.append(logEntry + "\n");
				pw.close();
			}
		}
	}

	private static void addMultipleNumberOfLinesContributions(List<String> contribLinesFromLeft,	List<String> contribLinesFromRight, String fileIndicator, String lineIndicator) {
		String[] lines = lineIndicator.split(",");
		int lowline = Integer.valueOf(lines[0]);
		int highline = Integer.valueOf(lines[1]);
		while(lowline <= highline){
			addNumberOfLineContribution(contribLinesFromLeft,contribLinesFromRight, fileIndicator,String.valueOf(lowline));	
			lowline++;
		}
	}

	public static void main(String[] args) {
		String s = FilesManager.readFileContent(new File("C:\\GGTS\\workspaces\\workspace_rscjd5\\jdime\\toy\\left.java"));
		s = FilesManager.stripComments(s);
		s = FilesManager.getStringContentIntoSingleLineNoSpacing(s);
		System.out.println(s);
	}
}
