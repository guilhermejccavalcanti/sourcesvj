package br.ufpe.cin.printers;

import java.io.File;
import java.util.List;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.exceptions.PrintException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.files.FilesTuple;
import br.ufpe.cin.generated.SimplePrintVisitor;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.MergeScenario;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

/**
 * Class responsible for converting ASTs into source code and
 * responsible for operations related to printing/generating merged code.
 * @author Guilherme
 */
public final class Prettyprinter {

	/**
	 * Converts a given tree into textual source code.
	 * @param tree
	 * @return textual representation of the given tree, or empty string in case of given empty tree.
	 */
	public static String print(FSTNode tree){
		//uncomment to print the AST
		//System.out.println(tree.printFST(0));

		String printable = "";
		//de.ovgu.cide.fstgen.parsers.generated_java18_merge.SimplePrintVisitor printer = new de.ovgu.cide.fstgen.parsers.generated_java18_merge.SimplePrintVisitor();
		SimplePrintVisitor printer = new SimplePrintVisitor();
		FSTNode root = getCompilationUnit(tree);
		if(root != null){
			root.accept(printer);
			printable = printer.getResult();
		}
		//printable = printable.trim().replaceAll(" +", " "); //fix for a bug in java18_merge.SimplePrintVisitor, not working 
		return printable;
	}

	public static void printOnScreenMergedCode(MergeContext context)
	{
		System.out.println("S3M OUTPUT:");
		System.out.println(context.semistructuredOutput.isEmpty() ? "empty (deleted, inexistent or invalid merged files)\n" : context.semistructuredOutput);

		System.out.println("JDIME OUTPUT:");
		System.out.println(context.structuredOutput.isEmpty() ? "empty (deleted, inexistent or invalid merged files)\n" : context.structuredOutput);
	}

	public static void generateMergedFile(MergeContext context, String outputFilePath)
			throws PrintException
	{
		if (outputFilePath != null && context!=null)
		{
			if (outputFilePath.isEmpty()) {
				outputFilePath = context.getRight().getAbsolutePath();
			}
			String semistructuredOutputFilePath = outputFilePath;
			String semistructuredMergeOutputContent = "";
			if(JFSTMerge.isStructured) {
				semistructuredMergeOutputContent = context.structuredOutput;
			} else {
				semistructuredMergeOutputContent = context.semistructuredOutput; 
			}
			
			boolean writeSucceed = FilesManager.writeContent(semistructuredOutputFilePath, semistructuredMergeOutputContent);
			if ((writeSucceed) && (!JFSTMerge.isGit))
			{
				String unstructuredOutputFilePath = outputFilePath + ".merge";
				String unstructuredMergeOutputContent = context.unstructuredOutput;
				writeSucceed = FilesManager.writeContent(unstructuredOutputFilePath, unstructuredMergeOutputContent);
			}
			if (writeSucceed && (!JFSTMerge.isGit))
			{
				String jdimeOutputFilePath = outputFilePath + ".jdime";
				String jdimeOutputContent = context.structuredOutput;
				writeSucceed = FilesManager.writeContent(jdimeOutputFilePath, jdimeOutputContent);
			}
			if (!writeSucceed) {
				throw new PrintException("Unable to manage merged output file!");
			}
		}
	}
	
	/**
	 * Prints the merged code of the given file tuple into a given directory.
	 * @param outputDirPath
	 * @param tuple
	 * @throws PrintException in case cannot write output file.
	 */
	public static void generateMergedTuple(FilesTuple tuple) throws PrintException {
		String outputDirPath = tuple.getOutputpath();
		if(outputDirPath != null){
			String fileNameExample;
			if(tuple.getBaseFile()!=null){
				fileNameExample = tuple.getBaseFile().getName();
			} else if(tuple.getLeftFile() != null){
				fileNameExample = tuple.getLeftFile().getName();
			} else {
				fileNameExample =tuple.getRightFile().getName();
			}
			String outputFilePath = outputDirPath+File.separator+fileNameExample;
			generateMergedFile(tuple.getContext(), outputFilePath);
		}
	}

	/**
	 * Create files with the resulting merged code of the given merge scenario.
	 * @throws PrintException 
	 */
	public static void generateMergedScenario(MergeScenario scenario) throws PrintException  {
		String mergedRevisionId = "rev_"+scenario.getLeftRevisionID()+"-"+scenario.getRightRevisionID();
		List<FilesTuple> tuples = scenario.getTuples();
		for(FilesTuple mergedTuple : tuples){
			String mergedDirectory = mergedTuple.getOutputpath().replace(scenario.getRightRevisionID(), mergedRevisionId); 
			mergedTuple.setOutputpath(mergedDirectory);
			generateMergedTuple(mergedTuple);
		}
	}

	/**
	 * Returns the first printable node of a AST, namely, the compilation unit.
	 * @param tree
	 * @return node representing the compilation unit, or null in case there is no compilation unit
	 */
	private static FSTNonTerminal getCompilationUnit(FSTNode tree){
		if(null != tree && tree instanceof FSTNonTerminal){
			FSTNonTerminal node = (FSTNonTerminal)tree;
			if(node.getType().equals("CompilationUnit")){
				return node;
			} else {
				return node.getChildren().isEmpty()? null : getCompilationUnit(node.getChildren().get(1));
			}
		} else {
			return null;
		}
	}
}
