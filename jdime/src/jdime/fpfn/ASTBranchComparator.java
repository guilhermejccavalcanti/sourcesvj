package jdime.fpfn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import org.extendj.ast.Block;
import org.extendj.ast.List;
import org.extendj.ast.Opt;
import org.extendj.ast.Stmt;

import de.fosd.jdime.artifact.ast.ASTNodeArtifact;
import de.fosd.jdime.config.merge.MergeContext;
import de.fosd.jdime.config.merge.Revision;
import de.fosd.jdime.matcher.matching.Matching;
import fpfn.Difference;
import fpfn.Difference.Type;
import fpfn.FPFNUtils;

/**
 * FPFN
 * Compares two given tree branches
 * @author Guilherme
 *
 */
public class ASTBranchComparator {

	public final static int LAST_COMMON_STMT_INDEX 			= 0;
	public final static int LAST_COMMON_NODE_INDEX 			= 1;
	public final static int LAST_COMMON_STMT_INDEX_LEFT	 	= 2;
	public final static int LAST_COMMON_STMT_INDEX_RIGHT 	= 3;

	public MergeContext context;

	public int countEditionsToDifferentPartsOfSameStmt(Revision baseRevision, ArrayList<ArrayList<ASTNodeArtifact>> branchesFromLeft, ArrayList<ArrayList<ASTNodeArtifact>> branchesFromRight){
		int editionsToDifferentPartsOfSameStmt = 0;
		for(ArrayList<ASTNodeArtifact> leftBranch : branchesFromLeft){
			for(ArrayList<ASTNodeArtifact> rightBranch : branchesFromRight){
				if(!isChangesInTheSameParentNode(leftBranch, rightBranch)){
					ArrayList<ASTNodeArtifact> results = findDeeperEqualNode(leftBranch, rightBranch);
					ASTNodeArtifact comonStmt = results.get(LAST_COMMON_STMT_INDEX);
					ASTNodeArtifact leftStmt  = results.get(LAST_COMMON_STMT_INDEX_LEFT);
					ASTNodeArtifact rightStmt = results.get(LAST_COMMON_STMT_INDEX_RIGHT);				
					if(isStmtValid(comonStmt)){
						Map<Revision, Matching<ASTNodeArtifact>> matches = comonStmt.getMatches();
						ASTNodeArtifact baseStmt = matches.get(baseRevision).getMatchingArtifact(comonStmt);
						if(isContentValid(leftStmt, baseStmt, rightStmt)){
							editionsToDifferentPartsOfSameStmt++;
							logFalseNegative(leftBranch,rightBranch, comonStmt,leftStmt,rightStmt);
						}
					}
				}
			}
		}
		return (editionsToDifferentPartsOfSameStmt/2);

	}

	private void logFalseNegative(ArrayList<ASTNodeArtifact> leftBranch, ArrayList<ASTNodeArtifact> rightBranch, ASTNodeArtifact common, ASTNodeArtifact left, ASTNodeArtifact right) {
		String leftBranchRepresentation  = leftBranch.toString();
		String rightBranchRepresentation = rightBranch.toString();
		String fnBranch 	= (printCommonBranch(common)).toString();
		String leftCode 	= left.prettyPrint();
		String rightCode 	= right.prettyPrint();
		String entry = (context.mergedFiles+";"+fnBranch+";"+rightBranchRepresentation+";"+leftBranchRepresentation+";"+leftCode+";"+rightCode);
		context.editedBranches.LOG_EDITIONS_TO_DIFFERENT_PARTS_OF_SAME_STMT.add(entry);
		
		/*
		 * It will be printed two times, but you have to count as only one instance
		 */
		ASTNodeArtifact methodDecl = FPFNUtils.getMethodNode(common);
		if(methodDecl != null && methodDecl.isMethod()){
			String mergedBodyContent   = FPFNUtils.getMethodBody(methodDecl);
			String signature 		   = FPFNUtils.extractSignature(mergedBodyContent);
			br.ufpe.cin.mergers.util.MergeConflict mc = new br.ufpe.cin.mergers.util.MergeConflict(leftCode,rightCode); //actually how the false negative should be conflict
			for(Difference jfstmergeDiff: context.differences){ //filling differences with jdime's info
				if(FPFNUtils.areSignatureEqual(signature, jfstmergeDiff.signature))
					jfstmergeDiff.jdimeBody = mergedBodyContent;
			}
			Difference diff = FPFNUtils.getOrCreateDifference(context.differences,signature,mc);
			diff.types.add(Type.SAME_STATEMENT);
			diff.jdimeConf = mc;
			diff.jdimeBody = mergedBodyContent;
			diff.signature = signature;
		}
	}

	private boolean isStmtValid(ASTNodeArtifact stmt) {
		return ((null != stmt) && !(stmt.getASTNode() instanceof Block));
	}

	private boolean isStmt(ASTNodeArtifact deeperEqualNode) {
		return (deeperEqualNode.getASTNode() instanceof Stmt);
	}

	private boolean isChangesInTheSameParentNode(ArrayList<ASTNodeArtifact> leftBranch, ArrayList<ASTNodeArtifact> rightBranch){
		if(leftBranch.size() != rightBranch.size()){
			return false;
		} else {
			for(int i = 0; i < leftBranch.size(); i++){
				ASTNodeArtifact left  = leftBranch.get(i);
				ASTNodeArtifact right = rightBranch.get(i);
				if(left.hasMatching(right)){
					//if(left.toString().equals(right.toString())){
					continue;
				} else {
					return false;
				}
			}
			return true;
		}
	}

	private ArrayList<ASTNodeArtifact> findDeeperEqualNode(ArrayList<ASTNodeArtifact> leftBranch, ArrayList<ASTNodeArtifact> rightBranch) {
		ASTNodeArtifact deeperEqualNode 	= null; 
		ASTNodeArtifact lastEqualStmt 		= null; 
		ASTNodeArtifact lastEqualStmtLeft 	= null; 
		ASTNodeArtifact lastEqualStmtRight	= null;

		java.util.List<ASTNodeArtifact> AUXlastEqualStmtLeftSubBranch 	= null; 
		java.util.List<ASTNodeArtifact> AUXlastEqualStmtRightSubBranch 	= null; 
		int limit = (leftBranch.size() < rightBranch.size()) ? leftBranch.size() : rightBranch.size();
		int index = 0;
		while(index < limit){
			ASTNodeArtifact left  = leftBranch.get(index);
			ASTNodeArtifact right = rightBranch.get(index);
			if(left.hasMatching(right)){
				if(!(left.getASTNode() instanceof Opt) && !(left.getASTNode() instanceof List)){
					deeperEqualNode = leftBranch.get(index);
					if(isStmt(deeperEqualNode)){
						lastEqualStmt 		= deeperEqualNode;
						lastEqualStmtLeft 	= leftBranch.get(index);
						lastEqualStmtRight 	= rightBranch.get(index);
						try{
							AUXlastEqualStmtLeftSubBranch  = leftBranch.subList(index+1, leftBranch.size()-1);
						} catch(IllegalArgumentException e){
							AUXlastEqualStmtLeftSubBranch = new LinkedList<ASTNodeArtifact>();
						}
						try{
							AUXlastEqualStmtRightSubBranch = rightBranch.subList(index+1, rightBranch.size()-1);
						} catch(IllegalArgumentException e){
							AUXlastEqualStmtLeftSubBranch = new LinkedList<ASTNodeArtifact>();
						}
					}
				}
				index++;
			} else {
				break;
			}
		}

		if((null != AUXlastEqualStmtLeftSubBranch) && (null != AUXlastEqualStmtRightSubBranch)){
			if(thereIsFutherBlock(AUXlastEqualStmtLeftSubBranch, AUXlastEqualStmtRightSubBranch)){
				lastEqualStmt = null;
			}
		}

		ArrayList<ASTNodeArtifact> result = new ArrayList<ASTNodeArtifact>();
		result.add(lastEqualStmt);
		result.add(deeperEqualNode);
		result.add(lastEqualStmtLeft);
		result.add(lastEqualStmtRight);
		return result;

	}

	private boolean thereIsFutherBlock(java.util.List<ASTNodeArtifact> subBranchLeft, java.util.List<ASTNodeArtifact> subBranchRight) {
		for(ASTNodeArtifact n : subBranchLeft){
			if(n.getASTNode() instanceof Block){
				return true;
			}
		}
		for(ASTNodeArtifact n : subBranchRight){
			if(n.getASTNode() instanceof Block){
				return true;
			}
		}
		return false;
	}

	private ArrayList<ASTNodeArtifact> printCommonBranch(ASTNodeArtifact deeperEqualNode){
		return rebuildAST(deeperEqualNode);

	}

	private ArrayList<ASTNodeArtifact> rebuildAST(ASTNodeArtifact node){
		ArrayList<ASTNodeArtifact> parents = new ArrayList<ASTNodeArtifact>();
		rebuild(node, parents);
		Collections.reverse(parents);
		parents.add(node);
		return parents;
	}

	private void rebuild(ASTNodeArtifact node, ArrayList<ASTNodeArtifact> parents){
		if(null == node.getParent()){
			return;
		} else {
			parents.add(node.getParent());
			rebuild(node.getParent(),parents);
		}
	}

	private  boolean isContentValid(ASTNodeArtifact left, ASTNodeArtifact base, ASTNodeArtifact right){
		String leftContent  	= ((left.prettyPrint()).replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
		String rightContent 	= ((right.prettyPrint()).replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
		if(null == base) return (!leftContent.equals(rightContent));
		else {	
			String baseContent  = ((base.prettyPrint()).replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
			if(leftContent.equals(baseContent) || rightContent.equals(baseContent)) return false;
			else if(rightContent.equals(leftContent)) return false;
			else return true;
		}
	}
}
