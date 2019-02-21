package br.ufpe.cin.mergers;

import java.io.File;

import org.apache.commons.io.IOUtils;

import br.ufpe.cin.exceptions.ExceptionUtils;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import de.ovgu.cide.fstgen.ast.FSTNode;

public final class TextualMerge {
	public static String merge(File left, File base, File right,
			boolean ignoreWhiteSpaces) throws TextualMergeException {
		String textualMergeResult = null;

		String leftContent = (left == null) || (!left.exists()) ? ""
				: FilesManager.readFileContent(left);
		String baseContent = (base == null) || (!base.exists()) ? ""
				: FilesManager.readFileContent(base);
		String rightContent = (right == null) || (!right.exists()) ? ""
				: FilesManager.readFileContent(right);
		textualMergeResult = merge(leftContent, baseContent, rightContent,
				ignoreWhiteSpaces);
		return textualMergeResult;
	}

	/*	public static String merge(String leftContent, String baseContent, 	String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException {
		String textualMergeResult = null;
		try {
			if(ignoreWhiteSpaces){
				String leftTrim = FilesManager.getStringContentIntoSingleLineNoSpacing(leftContent);
				String baseTrim = FilesManager.getStringContentIntoSingleLineNoSpacing(baseContent);
				String rightTrim= FilesManager.getStringContentIntoSingleLineNoSpacing(rightContent);
				if(leftTrim.equals(baseTrim)) return rightContent;
				else if(rightTrim.equals(baseTrim)) return leftContent;
				else return TextualMerge.merge(leftContent,	baseContent, rightContent, false);
			} else {
				RawTextComparator textComparator = RawTextComparator.DEFAULT;
				MergeResult mergeCommand = new MergeAlgorithm().merge(textComparator, 
						new RawText(Constants.encode(baseContent)),
						new RawText(Constants.encode(leftContent)), 
						new RawText(Constants.encode(rightContent)));

				ByteArrayOutputStream output = new ByteArrayOutputStream();
				new MergeFormatter().formatMerge(output, mergeCommand, "BASE", "MINE", "YOURS", "UTF-8");
				textualMergeResult = new String(output.toByteArray(), "UTF-8");
			}
		} catch (Exception e) {
			throw new TextualMergeException(ExceptionUtils.getCauseMessage(e),
					leftContent, baseContent, rightContent);
		}
		return textualMergeResult;
	}*/

	public static String merge(String leftContent, String baseContent, 	String rightContent, boolean ignoreWhiteSpaces) throws TextualMergeException {
		try {
			if(ignoreWhiteSpaces){
				String leftTrim = FilesManager.getStringContentIntoSingleLineNoSpacing(leftContent);
				String baseTrim = FilesManager.getStringContentIntoSingleLineNoSpacing(baseContent);
				String rightTrim= FilesManager.getStringContentIntoSingleLineNoSpacing(rightContent);
				if(leftTrim.equals(baseTrim)) return rightContent;
				else if(rightTrim.equals(baseTrim)) return leftContent;
				else return TextualMerge.merge(leftContent,	baseContent, rightContent, false);
			} else { 
				long time = System.currentTimeMillis();
				File tmpDir = null;
				//File tmpDir = new File(System.getProperty("user.dir")+ File.separator + "fstmerge_tmp" + time);
				//tmpDir.mkdir();

				File fileVar1 = File.createTempFile("fstmerge_var1_", ".java",tmpDir);
				File fileBase = File.createTempFile("fstmerge_base_", ".java",tmpDir);
				File fileVar2 = File.createTempFile("fstmerge_var2_", ".java",tmpDir);

				FilesManager.writeContent(fileVar1, leftContent);
				FilesManager.writeContent(fileBase, baseContent);
				FilesManager.writeContent(fileVar2, rightContent);

				String mergeCmd = "";
				if (System.getProperty("os.name").contains("Windows")) {
					mergeCmd = "C:/KDiff3/bin/diff3.exe -m -E \""
							+ fileVar1.getPath() + "\"" + " " + "\""
							+ fileBase.getPath() + "\"" + " " + "\""
							+ fileVar2.getPath() + "\"";
				} else {
					mergeCmd = "diff3 -m -E " + fileVar1.getPath() + " "+ fileBase.getPath() + " " + fileVar2.getPath();
				}
				Runtime run = Runtime.getRuntime();
				Process pr = run.exec(mergeCmd);
				String result = IOUtils.toString(pr.getInputStream());

				/*				BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line = "";
				String result = "";
				while ((line = buf.readLine()) != null) {
					result = result + line + "\n";
				}
				pr.getInputStream().close();*/


				/*				buf = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
				while ((line = buf.readLine()) != null) {
					System.err.println(line);
				}*/

				pr.getErrorStream().close();
				pr.getOutputStream().close();


				fileVar1.deleteOnExit();
				fileBase.deleteOnExit();
				fileVar2.deleteOnExit();
				//tmpDir.delete();
				//FileUtils.deleteDirectory(tmpDir);
				
				return result;
			}
		} catch (Exception e) {
			throw new TextualMergeException(ExceptionUtils.getCauseMessage(e), leftContent, baseContent, rightContent);
		}
	}

	public String mergeIncludingBase(String leftContent, String baseContent, String rightContent, FSTNode node) throws TextualMergeException {
		try {
			long time = System.currentTimeMillis();
			File tmpDir = null;
			//File tmpDir = new File(System.getProperty("user.dir")+ File.separator + "fstmerge_tmp" + time);
			//tmpDir.mkdir();

			File fileVar1 = File.createTempFile("fstmerge_var1_", ".java",tmpDir);
			File fileBase = File.createTempFile("fstmerge_base_", ".java",tmpDir);
			File fileVar2 = File.createTempFile("fstmerge_var2_", ".java",tmpDir);

			FilesManager.writeContent(fileVar1, leftContent);
			FilesManager.writeContent(fileBase, baseContent);
			FilesManager.writeContent(fileVar2, rightContent);

			String mergeCmdInclBase = "";
			if (System.getProperty("os.name").contains("Windows")) {
				mergeCmdInclBase = "C:/KDiff3/bin/diff3.exe -m \""
						+ fileVar1.getPath() + "\"" + " " + "\""
						+ fileBase.getPath() + "\"" + " " + "\""
						+ fileVar2.getPath() + "\"";
			} else {
				mergeCmdInclBase = "diff3 -m " + fileVar1.getPath() + " "+ fileBase.getPath() + " " + fileVar2.getPath();
			}
			Runtime run = Runtime.getRuntime();
			Process pr = run.exec(mergeCmdInclBase);
			String resultInclBase = IOUtils.toString(pr.getInputStream());

			/*			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			String resultInclBase = "";
			while ((line = buf.readLine()) != null) {
				resultInclBase = resultInclBase + line + "\n";
			}
			pr.getInputStream().close();

			buf = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
			while ((line = buf.readLine()) != null) {
				System.err.println(line);
			}
			 */			

			pr.getErrorStream().close();
			pr.getOutputStream().close();

			fileVar1.deleteOnExit();
			fileBase.deleteOnExit();
			fileVar2.deleteOnExit();
			//tmpDir.delete();
			//FileUtils.deleteDirectory(tmpDir);

			return resultInclBase;
		} catch (Exception e) {
			throw new TextualMergeException(ExceptionUtils.getCauseMessage(e), leftContent, baseContent, rightContent);
		}
	}
}
