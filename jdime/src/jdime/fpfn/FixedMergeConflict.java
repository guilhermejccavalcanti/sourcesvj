package jdime.fpfn;

/**
 * JDime detects delection conflicts but does not put the markers in a few cases.
 * This class store the conflicting code for further actions.
 * @author Guilherme
 *
 */
public class FixedMergeConflict {

	public enum DelectionDirection{
		LEFT,
		RIGHT;
	}

	public String conflictingCode;
	public DelectionDirection direction;

	public FixedMergeConflict(String code, DelectionDirection direction){
		this.conflictingCode = code;
		this.direction = direction;
	}

	@Override
	public String toString() {
		if(this.direction == DelectionDirection.LEFT) {
			return "<<<<<<< MINE\n" + "=======\n" + conflictingCode + ">>>>>>> YOURS";
		} else {
			return "<<<<<<< MINE\n" + conflictingCode + "=======\n" + ">>>>>>> YOURS";
		}
	}
}

