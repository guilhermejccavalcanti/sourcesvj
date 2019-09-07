package br.ufpe.cin.mergers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.exceptions.ExceptionUtils;
import br.ufpe.cin.exceptions.SemistructuredMergeException;
import br.ufpe.cin.exceptions.StructuredMergeException;
import br.ufpe.cin.exceptions.TextualMergeException;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.parser.JParser;
import br.ufpe.cin.printers.Prettyprinter;
import cide.gparser.ParseException;
import cide.gparser.TokenMgrError;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;
import fpfn.Difference;
import fpfn.FPFNUtils;

public final class SemistructuredMerge {
	static final String MERGE_SEPARATOR = "##FSTMerge##";
	static final String SEMANTIC_MERGE_MARKER = "~~FSTMerge~~";

	public static String merge(File left, File base, File right,
			MergeContext context) throws SemistructuredMergeException,
			TextualMergeException, StructuredMergeException {
		try {
			JParser parser = new JParser();
			FSTNode leftTree = parser.parse(left);
			FSTNode baseTree = parser.parse(base);
			FSTNode rightTree = parser.parse(right);

			context.join(merge(leftTree, baseTree, rightTree));

			//ConflictsHandler.handle(context);
		} catch (ParseException | FileNotFoundException
				| UnsupportedEncodingException | TokenMgrError ex) {
			String message = ExceptionUtils.getCauseMessage(ex);
			if ((ex instanceof FileNotFoundException)) {
				message = "The merged file was deleted in one version.";
			}
			throw new SemistructuredMergeException(message, context);
		}
		return FilesManager.indentCode(Prettyprinter.print(context.superImposedTree));
	}

	private static MergeContext merge(FSTNode left, FSTNode base, FSTNode right)
			throws TextualMergeException, StructuredMergeException {
		left.index = 0;
		base.index = 1;
		right.index = 2;

		MergeContext context = new MergeContext();
		context.leftTree = left;
		context.baseTree = base;
		context.rightTree = right;

		FSTNode mergeLeftBase = superimpose(left, base, null, context, true);
		FSTNode mergeLeftBaseRight = superimpose(mergeLeftBase, right, null,
				context, false);

		removeRemainingBaseNodes(mergeLeftBaseRight, context);
		mergeMatchedContent(mergeLeftBaseRight, context);

		context.superImposedTree = mergeLeftBaseRight;

		return context;
	}

	private static FSTNode superimpose(FSTNode nodeA, FSTNode nodeB, FSTNonTerminal parent, MergeContext context, boolean isProcessingBaseTree) {
		if (nodeA.compatibleWith(nodeB)) {
			FSTNode composed = nodeA.getShallowClone();
			composed.index = nodeB.index;
			composed.setParent(parent);

			if (nodeA instanceof FSTNonTerminal && nodeB instanceof FSTNonTerminal) {
				FSTNonTerminal nonterminalA = (FSTNonTerminal) nodeA;
				FSTNonTerminal nonterminalB = (FSTNonTerminal) nodeB;
				FSTNonTerminal nonterminalComposed = (FSTNonTerminal) composed;

				/*
				 * nodes from base or right
				 */
				for (FSTNode childB : nonterminalB.getChildren()) { 	
					FSTNode childA = nonterminalA.getCompatibleChild(childB);
					if (childA == null) { 								// means that a base node was deleted by left, or that a right node was added
						FSTNode cloneB = childB.getDeepClone();
						if (childB.index == -1)
							childB.index = nodeB.index;
						cloneB.index = childB.index;

						nonterminalComposed.addChild(cloneB);			// cloneB must be removed afterwards if it is a base node

						if (isProcessingBaseTree) {
							context.deletedBaseNodes.add(cloneB); 		// base nodes deleted by left
							context.nodesDeletedByLeft.add(cloneB);
						} else {
							context.addedRightNodes.add(cloneB); 		// nodes added by right
						}
					} else {
						if (childA.index == -1)
							childA.index = nodeA.index;
						if (childB.index == -1)
							childB.index = nodeB.index;

						if(!isProcessingBaseTree && context.addedLeftNodes.contains(childA)){ //duplications
							context.addedRightNodes.add(childB); 		
						}

						nonterminalComposed.addChild(superimpose(childA, childB, nonterminalComposed, context, isProcessingBaseTree));
					}
				}

				/*
				 * nodes from left or leftBase
				 */
				List<FSTNode> nonterminalAChildren = nonterminalA.getChildren();

				for (int i = 0; i < nonterminalAChildren.size(); i++) {
					FSTNode childA = nonterminalAChildren.get(i);
					FSTNode childB = nonterminalB.getCompatibleChild(childA);

					if (childB == null) { 								// is a new node from left, or a deleted base node in right
						FSTNode cloneA = childA.getDeepClone();
						if (childA.index == -1)
							childA.index = nodeA.index;
						cloneA.index = childA.index;

						FSTNode childALeftNeighbour = getLeftNeighbourNode(nonterminalAChildren, i);
						FSTNode childARightNeighbour = getRightNeighbourNode(nonterminalAChildren, i);
						addNodeToNonTerminalNearNeighbour(cloneA, childALeftNeighbour, childARightNeighbour, nonterminalComposed);

						if (context.deletedBaseNodes.contains(childA)) { // this is only possible when processing right nodes because this is a base node not present either in left and right
							context.deletedBaseNodes.remove(childA);
							context.deletedBaseNodes.add(cloneA);
						}

						if(isProcessingBaseTree){ //node added by left in relation to base
							context.addedLeftNodes.add(cloneA);
						} else {
							if(!context.addedLeftNodes.contains(childA))
								context.nodesDeletedByRight.add(cloneA);
						}
					} else {
						if (!isProcessingBaseTree) {
							context.deletedBaseNodes.remove(childA); 	// node common to right and base but not to left
						}
					}
				}
				return nonterminalComposed;

			} else if (nodeA instanceof FSTTerminal && nodeB instanceof FSTTerminal	&& parent instanceof FSTNonTerminal) {
				FSTTerminal terminalA = (FSTTerminal) nodeA;
				FSTTerminal terminalB = (FSTTerminal) nodeB;
				FSTTerminal terminalComposed = (FSTTerminal) composed;

				if (!terminalA.getMergingMechanism().equals("Default")) {
					terminalComposed.setBody(markContributions(terminalA.getBody(), terminalB.getBody(),isProcessingBaseTree, terminalA.index, terminalB.index));
				}
				return terminalComposed;
			}
			return null;
		} else
			return null;
	}


	private static String markContributions(String bodyA, String bodyB,
			boolean firstPass, int indexA, int indexB) {
		if (bodyA.contains("~~FSTMerge~~")) {
			return bodyA + " " + bodyB;
		}
		if (firstPass) {
			return "~~FSTMerge~~ " + bodyA + " " + "##FSTMerge##" + " " + bodyB
					+ " " + "##FSTMerge##";
		}
		if (indexA == 0) {
			return "~~FSTMerge~~ " + bodyA + " " + "##FSTMerge##" + " "
					+ "##FSTMerge##" + " " + bodyB;
		}
		return "~~FSTMerge~~ ##FSTMerge## " + bodyA + " " + "##FSTMerge##"
		+ " " + bodyB;
	}

	private static void removeRemainingBaseNodes(FSTNode mergedTree,
			MergeContext context) {
		boolean removed = false;
		if (!context.deletedBaseNodes.isEmpty()) {
			for (FSTNode loneBaseNode : context.deletedBaseNodes) {
				if (mergedTree == loneBaseNode) {
					FSTNonTerminal parent = (FSTNonTerminal) mergedTree
							.getParent();
					if (parent != null) {
						parent.removeChild(mergedTree);
						removed = true;
					}
				}
			}
			if (!removed && mergedTree instanceof FSTNonTerminal) {
				Object[] children = ((FSTNonTerminal) mergedTree).getChildren()
						.toArray();
				for (Object child : children) {
					removeRemainingBaseNodes((FSTNode) child, context);
				}
			}
		}
	}

	private static void mergeMatchedContent(FSTNode node, MergeContext context)
			throws TextualMergeException, StructuredMergeException {
		if ((node instanceof FSTNonTerminal)) {
			for (FSTNode child : ((FSTNonTerminal) node).getChildren()) {
				mergeMatchedContent(child, context);
			}
		} else if ((node instanceof FSTTerminal)) {
			if (((FSTTerminal) node).getBody().contains("##FSTMerge##")) {
				String body = ((FSTTerminal) node).getBody() + " ";
				String[] splittedBodyContent = body.split("##FSTMerge##");

				String leftContent = splittedBodyContent[0].replace(
						"~~FSTMerge~~", "").trim();
				String baseContent = splittedBodyContent[1].trim();
				String rightContent = splittedBodyContent[2].trim();


				if(IS_ORDERED && (node.getType().equals("MethodDecl") 
						|| node.getType().equals("ConstructorDecl") 
						|| node.getType().equals("FieldDecl"))){//FPFN without if...else

					orderedMerge(node, context, leftContent, baseContent,rightContent);

				} else {
					String mergedBodyContent = TextualMerge.merge(leftContent, baseContent, rightContent, false); //FPFN original = true
					((FSTTerminal) node).setBody(mergedBodyContent);

					if(	node.getType().equals("MethodDecl") 
							|| node.getType().equals("ConstructorDecl") 
							|| node.getType().equals("FieldDecl")){
						if(mergedBodyContent.contains("<<<<<<")){
							context.ssmergeConfsInDecl++;
						}
					}

					/*				
					identifyNodesEditedInOnlyOneVersion(node, context, leftContent,	baseContent, rightContent);

					identifyPossibleNodesDeletionOrRenamings(node, context,	leftContent, baseContent, rightContent);

					 */

					//FPFN
					long t0 = System.nanoTime();
					if(JFSTMerge.countConsecutiveLinesConflicts){
						findConsecutiveLinesOrSpacingConflicts(node, context, leftContent, baseContent, rightContent, mergedBodyContent);
					}
					if(node.getType().equals("MethodDecl") || node.getType().equals("ConstructorDecl")){
						countMethodsEditions(leftContent, baseContent, rightContent, context);
					}
					context.semistructuredMergeTime += System.nanoTime() - t0; //for fairness should disconsider these call this

				}
			}
		} else {
			System.err
			.println("Warning: node is neither non-terminal nor terminal!");
		}
	}

	private static void countMethodsEditions(String leftContent,	String baseContent, String rightContent, MergeContext context) {
		if(!leftContent.equals(rightContent) && !leftContent.equals(baseContent) && !rightContent.equals(baseContent)){
			context.commonChangedMethods++;

		} 
		if(!leftContent.equals(baseContent) || !rightContent.equals(baseContent)){
			context.changedMethods++;
		}
	}

	/*	FPFN this is the original version 
	 * private static void findConsecutiveLinesOrSpacingConflicts(FSTNode node,
			MergeContext context, String leftContent, String baseContent,
			String rightContent, String mergedBodyContent)
					throws TextualMergeException {

		if ((node.getType().equals("MethodDecl")) || (node.getType().equals("ConstructorDecl"))) {
			if (mergedBodyContent.contains("<<<<<<")) {
				if (!FPFNUtils.isRenamingOrDeletionConflict(leftContent, baseContent, rightContent)) {
					String mergedBodyContentInclBase = new TextualMerge().mergeIncludingBase(leftContent, baseContent,	rightContent, node);
					List<MergeConflict> mergeConflictsOriginal = FilesManager.extractMergeConflicts(mergedBodyContent);
					List<MergeConflict> mergeConflictsInclBase = FilesManager.extractMergeConflicts(mergedBodyContentInclBase);
					List<MergeConflict> mergeConflicts = FilesManager.filterNonOverlapingMergeConflicts(mergeConflictsInclBase,	mergeConflictsOriginal);
					for (MergeConflict mc : mergeConflicts) {
						Difference diff = new Difference();
						if (FPFNUtils.isConsecutiveLineConflict(mc)) {
							diff.types.add(Difference.Type.CONSECUTIVE_LINES);
						} else if (FPFNUtils.isSpacingConflict(mc)) {
							diff.types.add(Difference.Type.SPACING);
						} else {
							diff.types.add(Difference.Type.OTHER);
						}
						diff.jfstmergeConf = mc;
						diff.jfstmergeBody = mergedBodyContent;
						diff.signature = FPFNUtils.extractSignature(mergedBodyContentInclBase);
						context.differences.add(diff);
					}
				}
			} else {
				Difference diff = new Difference();
				diff.jfstmergeBody = mergedBodyContent;
				diff.signature = FPFNUtils.extractSignature(mergedBodyContent);
				context.differences.add(diff);
			}
		}
	}*/

	/*	private static void findConsecutiveLinesOrSpacingConflicts(FSTNode node,
			MergeContext context, String leftContent, String baseContent,
			String rightContent, String mergedBodyContent)
					throws TextualMergeException {

		if ((node.getType().equals("MethodDecl")) || (node.getType().equals("ConstructorDecl"))) {
			if (mergedBodyContent.contains("<<<<<<")) {
				if (!FPFNUtils.isRenamingOrDeletionConflict(leftContent, baseContent, rightContent)) {
					String mergedBodyContentInclBase = new TextualMerge().mergeIncludingBase(leftContent, baseContent,	rightContent, node);
					List<MergeConflict> mergeConflictsOriginal = FilesManager.extractMergeConflicts(mergedBodyContent);
					List<MergeConflict> mergeConflictsInclBase = FilesManager.extractMergeConflicts(mergedBodyContentInclBase);
					List<MergeConflict> mergeConflicts = FilesManager.filterNonOverlapingMergeConflicts(mergeConflictsInclBase,	mergeConflictsOriginal);
					for (MergeConflict mc : mergeConflicts) {
						Difference diff = new Difference();
						if (FPFNUtils.isConsecutiveLineConflict(mc)) {
							diff.types.add(Difference.Type.CONSECUTIVE_LINES);
							diff.jfstmergeConf = mc;
							diff.jfstmergeBody = mergedBodyContent;
							diff.signature = FPFNUtils.extractSignature(mergedBodyContentInclBase);
							context.differences.add(diff);
						} 
					}
				}
			} 
		}
	}*/

	private static void findConsecutiveLinesOrSpacingConflicts(FSTNode node,
			MergeContext context, String leftContent, String baseContent,
			String rightContent, String mergedBodyContent)
					throws TextualMergeException {

		if ((node.getType().equals("MethodDecl")) || (node.getType().equals("ConstructorDecl"))) {
			if (mergedBodyContent.contains("<<<<<<")) {
				if (!FPFNUtils.isRenamingOrDeletionConflict(leftContent, baseContent, rightContent)) {
					String mergedBodyContentInclBase = new TextualMerge().mergeIncludingBase(leftContent, baseContent,	rightContent, node);
					List<MergeConflict> mergeConflictsInclBase = FilesManager.extractMergeConflicts(mergedBodyContentInclBase);
					List<MergeConflict> mergeConflictsOriginal = FilesManager.extractMergeConflicts(mergedBodyContent);
					List<MergeConflict> mergeConflicts = FilesManager.filterNonOverlapingMergeConflicts(mergeConflictsInclBase,	mergeConflictsOriginal);
					for (MergeConflict mc : mergeConflicts) {
						//for (MergeConflict mc : mergeConflictsInclBase) {
						Difference diff = new Difference();
						if (FPFNUtils.isConsecutiveLineConflict(mc)) {
							diff.types.add(Difference.Type.CONSECUTIVE_LINES);
						} else if (FPFNUtils.isSpacingConflict(mc)) {
							diff.types.add(Difference.Type.SPACING);
						} 
						diff.jfstmergeConf = mc;
						diff.jfstmergeBody = mergedBodyContent;
						diff.signature = FPFNUtils.extractSignature(mergedBodyContentInclBase);
						context.differences.add(diff);
					}
				}
			} 
		}
	}

	private static void identifyPossibleNodesDeletionOrRenamings(FSTNode node,MergeContext context, String leftContent, String baseContent,	String rightContent) {
		String leftContenttrim = FilesManager.getStringContentIntoSingleLineNoSpacing(leftContent);
		String baseContenttrim = FilesManager.getStringContentIntoSingleLineNoSpacing(baseContent);
		String rightContenttrim = FilesManager.getStringContentIntoSingleLineNoSpacing(rightContent);
		if (!baseContenttrim.isEmpty()) {
			if ((!baseContenttrim.equals(leftContenttrim))
					&& (rightContenttrim.isEmpty())) {
				Pair<String, FSTNode> tuple = Pair.of(baseContent, node);
				context.possibleRenamedRightNodes.add(tuple);
			} else if ((!baseContenttrim.equals(rightContenttrim))
					&& (leftContenttrim.isEmpty())) {
				Pair<String, FSTNode> tuple = Pair.of(baseContent, node);
				context.possibleRenamedLeftNodes.add(tuple);
			}
		}
	}

	private static void identifyNodesEditedInOnlyOneVersion(FSTNode node,MergeContext context, String leftContent, String baseContent,String rightContent) {
		String leftContenttrim = FilesManager.getStringContentIntoSingleLineNoSpacing(leftContent);
		String baseContenttrim = FilesManager.getStringContentIntoSingleLineNoSpacing(baseContent);
		String rightContenttrim = FilesManager.getStringContentIntoSingleLineNoSpacing(rightContent);
		if (!baseContenttrim.isEmpty()) {
			if ((baseContenttrim.equals(leftContenttrim))
					&& (!rightContenttrim.equals(leftContenttrim))) {
				context.editedRightNodes.add(node);
			} else if ((baseContenttrim.equals(rightContenttrim))
					&& (!leftContenttrim.equals(rightContenttrim))) {
				context.editedLeftNodes.add(node);
			}
		}
	}

	private static FSTNode getLeftNeighbourNode(List<FSTNode> nodes,
			int nodeIndex) {
		boolean nodeHasLeftNeighbour = nodeIndex > 0;
		FSTNode leftNeighbour = null;
		if (nodeHasLeftNeighbour) {
			leftNeighbour = (FSTNode) nodes.get(nodeIndex - 1);
		}
		return leftNeighbour;
	}

	private static FSTNode getRightNeighbourNode(List<FSTNode> nodes,
			int nodeIndex) {
		boolean nodeHasRightNeighbour = nodeIndex < nodes.size() - 1;
		FSTNode rightNeighbour = null;
		if (nodeHasRightNeighbour) {
			rightNeighbour = (FSTNode) nodes.get(nodeIndex + 1);
		}
		return rightNeighbour;
	}

	private static void addNodeToNonTerminalNearNeighbour(FSTNode node,
			FSTNode leftNeighbour, FSTNode rightNeighbour,
			FSTNonTerminal nonTerminal) {
		boolean hasFoundNeighbour = false;
		if (leftNeighbour != null) {
			int leftNeighbourIndex = findChildNodeIndex(nonTerminal,
					leftNeighbour);
			if (leftNeighbourIndex != -1) {
				nonTerminal.addChild(node, leftNeighbourIndex + 1);
				hasFoundNeighbour = true;
			}
		}
		if ((!hasFoundNeighbour) && (rightNeighbour != null)) {
			int rightSiblingIndex = findChildNodeIndex(nonTerminal,
					rightNeighbour);
			if (rightSiblingIndex != -1) {
				nonTerminal.addChild(node, rightSiblingIndex);
				hasFoundNeighbour = true;
			}
		}
		if (!hasFoundNeighbour) {
			nonTerminal.addChild(node);
		}
	}

	private static int findChildNodeIndex(FSTNonTerminal parentNode,
			FSTNode node) {
		return parentNode.getChildren().indexOf(node);
	}

	//FPFN
	static boolean IS_ORDERED = false;

	//FPFN
	public static String merge(File left, File base, File right, MergeContext context, boolean isOrdered) throws SemistructuredMergeException,TextualMergeException, StructuredMergeException {
		IS_ORDERED = isOrdered;
		String merged = merge(left,base,right,context);
		IS_ORDERED = false;
		return merged;
	}

	//FPFN
	private static void orderedMerge(FSTNode node, MergeContext context,String leftContent, String baseContent, String rightContent)
			throws StructuredMergeException {
		try{
			long time = System.currentTimeMillis();
			File tmpDir = null;
			//File tmpDir = new File(System.getProperty("user.dir")+ File.separator + "fstmerge_tmp" + time);
			//tmpDir.mkdir();
			File fileVar1 = File.createTempFile("fstmerge_var1_", ".java",tmpDir);
			File fileBase = File.createTempFile("fstmerge_base_", ".java",tmpDir);
			File fileVar2 = File.createTempFile("fstmerge_var2_", ".java",tmpDir);
			FilesManager.writeContent(fileVar1, "public class PlaceHolderFake {\n" + leftContent + "\n}");
			FilesManager.writeContent(fileBase, "public class PlaceHolderFake {\n" + baseContent + "\n}");
			FilesManager.writeContent(fileVar2, "public class PlaceHolderFake {\n" + rightContent+ "\n}");

			String mergedBodyContent = StructuredMerge.merge(fileVar1, fileBase, fileVar2, context);

			mergedBodyContent = mergedBodyContent.replace("public class PlaceHolderFake {","");
			mergedBodyContent = mergedBodyContent.substring(0, mergedBodyContent.lastIndexOf("}"));
			((FSTTerminal) node).setBody(mergedBodyContent);
			
			if(mergedBodyContent.contains("<<<<<<")){
				context.jdimeConfsInDecl++;
			}

			fileVar1.deleteOnExit();fileBase.deleteOnExit();fileVar2.deleteOnExit();
			//fileVar1.delete();fileBase.delete();fileVar2.delete();
			//tmpDir.delete();
			//FileUtils.deleteDirectory(tmpDir);
		}catch(Exception e){
			throw new StructuredMergeException(ExceptionUtils.getCauseMessage(e), context);
		}
	}
}
