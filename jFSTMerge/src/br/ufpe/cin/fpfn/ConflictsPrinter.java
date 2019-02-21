package br.ufpe.cin.fpfn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;

/**
 * Printer of different merge tools conflicts.
 * @author Guilherme
 */
//FPFN
final public class ConflictsPrinter {
	public static void print(MergeContext context){
		if(context.sucessfullMerge){
			if(null!=context.semistructuredOutput && null!=context.structuredOutput){
				//printing ssmerge confs
				List<MergeConflict> ssmergeconfs = FilesManager.extractMergeConflicts(context.semistructuredOutput);
				String logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator +"confsjfstmerge.txt";
				printConflicts(context, ssmergeconfs, logpath);

				//printing jdime confs
				List<MergeConflict> jdimeconfs   = FilesManager.extractMergeConflicts(context.structuredOutput);
				logpath = System.getProperty("user.home")+ File.separator + ".jfstmerge" + File.separator +"confsjdime.txt";
				printConflicts(context, jdimeconfs,logpath);
			}
		}
	}

	private static void printConflicts(MergeContext context, List<MergeConflict> confs, String logpath) {
		if(!confs.isEmpty()){
			try{
				StringBuilder builder = new StringBuilder();
				builder.append("----------------------------\n");
				for(MergeConflict mc : confs){
					builder.append(mc.body);
					builder.append("\n----------------------------\n");
				}
				String conflicts = builder.toString();


				String files = context.fullQualifiedMergedClass;
				String leftContent = FilesManager.readFileContent(context.getLeft());
				String baseContent = FilesManager.readFileContent(context.getBase());
				String rightContent= FilesManager.readFileContent(context.getRight());
				String ssmeContent = context.semistructuredOutput;
				String struContent = context.structuredOutput;

				builder = new StringBuilder();
				builder.append("########################################################\n");
				builder.append("Files: " + files + "\n");
				builder.append("Conflicts:\n" + conflicts + "\n");
				builder.append("Semistructured Merge Output:\n" + ssmeContent + "\n");
				builder.append("Structured Merge Output:\n" + struContent   + "\n");
				builder.append("Left Content:\n" + ((leftContent.isEmpty()) ?"<empty>":leftContent) + "\n");
				builder.append("Base Content:\n" + ((baseContent.isEmpty()) ?"<empty>":baseContent) + "\n");
				builder.append("Right Content:\n"+ ((rightContent.isEmpty())?"<empty>":rightContent)+ "\n");


				//System.out.println(builder.toString());
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File(logpath), true), true); 
				pw.append(builder.toString()+"\n");
				pw.close();
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
}
