package br.ufpe.cin.mergers.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import br.ufpe.cin.files.FilesManager;
import de.ovgu.cide.fstgen.ast.FSTNode;
import fpfn.Difference;

public class MergeContext {
	File base;
	File right;
	File left;
	String outputFilePath;
	String baseContent;
	String leftContent;
	String rightContent;
	public List<FSTNode> addedLeftNodes = new ArrayList();
	public List<FSTNode> addedRightNodes = new ArrayList();
	public List<FSTNode> deletedBaseNodes = new ArrayList();
	public List<FSTNode> nodesDeletedByLeft = new ArrayList();
	public List<FSTNode> nodesDeletedByRight = new ArrayList();
	public List<Pair<String, FSTNode>> possibleRenamedLeftNodes = new ArrayList();
	public List<Pair<String, FSTNode>> possibleRenamedRightNodes = new ArrayList();
	public List<FSTNode> editedLeftNodes = new ArrayList();
	public List<FSTNode> editedRightNodes = new ArrayList();
	public FSTNode leftTree;
	public FSTNode baseTree;
	public FSTNode rightTree;
	public FSTNode superImposedTree;
	public String semistructuredOutput;
	public String unstructuredOutput;
	public String structuredOutput;
	public List<Difference> differences = new ArrayList();
	public int newElementReferencingEditedOneConflicts = 0;
	public int renamingConflicts = 0;
	public int typeAmbiguityErrorsConflicts = 0;
	public int deletionConflicts = 0;
	public int initializationBlocksConflicts = 0;
	public int acidentalConflicts = 0;
	public long semistructuredMergeTime = 0L;
	public long unstructuredMergeTime = 0L;
	public int semistructuredNumberOfConflicts = 0;
	public int unstructuredNumberOfConflicts = 0;
	public int semistructuredMergeConflictsLOC = 0;
	public int unstructuredMergeConflictsLOC = 0;
	public int orderingConflicts = 0;
	public int duplicatedDeclarationErrors = 0;
	public int equalConflicts = 0;
	public int innerDeletionConflicts = 0;
	public long structuredMergeTime = 0L;
	public boolean sucessfullMerge = false;
	public boolean isSsEqualsToUn = false;
	public boolean isStEqualsToUn = false;
	public String fullQualifiedMergedClass = "";
	public int changedMethods = 0;
	public int commonChangedMethods = 0;
	public int ssmergeConfsInDecl = 0;
	public int jdimeConfsInDecl = 0;

	public MergeContext() {
	}

	public MergeContext(File left, File base, File right, String outputFilePath) {
		this.left = left;
		this.base = base;
		this.right = right;
		this.outputFilePath = outputFilePath;

		this.leftContent = FilesManager.readFileContent(this.left);
		this.baseContent = FilesManager.readFileContent(this.base);
		this.rightContent = FilesManager.readFileContent(this.right);

		this.setFullQualifiedMergedClassName();
	}

	public MergeContext join(MergeContext otherContext) {
		this.addedLeftNodes.addAll(otherContext.addedLeftNodes);
		this.addedRightNodes.addAll(otherContext.addedRightNodes);

		this.editedLeftNodes.addAll(otherContext.editedLeftNodes);
		this.editedRightNodes.addAll(otherContext.editedRightNodes);

		this.deletedBaseNodes.addAll(otherContext.deletedBaseNodes);
		this.nodesDeletedByLeft.addAll(otherContext.nodesDeletedByLeft);
		this.nodesDeletedByRight.addAll(otherContext.nodesDeletedByRight);

		this.possibleRenamedLeftNodes
				.addAll(otherContext.possibleRenamedLeftNodes);
		this.possibleRenamedRightNodes
				.addAll(otherContext.possibleRenamedRightNodes);

		this.leftTree = otherContext.leftTree;
		this.baseTree = otherContext.baseTree;
		this.rightTree = otherContext.rightTree;
		this.superImposedTree = otherContext.superImposedTree;

		this.differences.addAll(otherContext.differences);

		this.commonChangedMethods += otherContext.commonChangedMethods;
		this.changedMethods += otherContext.changedMethods;
		
		this.ssmergeConfsInDecl += otherContext.ssmergeConfsInDecl;
		this.jdimeConfsInDecl += otherContext.jdimeConfsInDecl;

		return this;
	}

	public File getBase() {
		return this.base;
	}

	public void setBase(File base) {
		this.base = base;
	}

	public File getRight() {
		return this.right;
	}

	public void setRight(File right) {
		this.right = right;
	}

	public File getLeft() {
		return this.left;
	}

	public void setLeft(File left) {
		this.left = left;
	}

	public String getBaseContent() {
		return this.baseContent;
	}

	public void setBaseContent(String baseContent) {
		this.baseContent = baseContent;
	}

	public String getLeftContent() {
		return this.leftContent;
	}

	public void setLeftContent(String leftContent) {
		this.leftContent = leftContent;
	}

	public String getRightContent() {
		return this.rightContent;
	}

	public void setRightContent(String rightContent) {
		this.rightContent = rightContent;
	}

	private void setFullQualifiedMergedClassName() {
		String name = "";
		if (this.left != null && this.left.length() != 0) {
			name = FilesManager.getFullQualifiedName(this.left);
		} else if (this.right != null && this.right.length() != 0) {
			name = FilesManager.getFullQualifiedName(this.right);
		} else if (this.base != null && this.base.length() != 0) {
			name = FilesManager.getFullQualifiedName(this.base);
		}
		if (name.isEmpty()) {
			name = (this.left != null ? this.left.getAbsolutePath()
					: "<empty left>")
					+ "#"
					+ (this.base != null ? this.base.getAbsolutePath()
							: "<empty base>")
					+ "#"
					+ (this.right != null ? this.right.getAbsolutePath()
							: "<empty right>");
		}
		String projectAndCommit = FilesManager.lastLine(System
				.getProperty("user.home")
				+ File.separator
				+ ".jfstmerge"
				+ File.separator + "execution.log");
		this.fullQualifiedMergedClass = projectAndCommit + "," + name;
	}
}
