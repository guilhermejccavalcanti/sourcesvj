public class ReportEntity {
	public String files = "";
	public String s3mOutput = "";
	public String jdimeOutput = "";
	public String leftContent = "";
	public String baseContent = "";
	public String rightContent = "";

	public String conclusao = "";
	public String type = "";
	public String s3mConflict = "";
	public String jdimeConflict = "";
	public String s3mDeclaration = "";
	public String jdimeDeclaration = "";

	public ReportEntity() {
	}

	public ReportEntity(String files, String s3mOutput, String jdimeOutput,
			String leftContent, String baseContent, String rightContent,
			String conclusao, String type, String s3mConflict,
			String jdimeConflict, String s3mDeclaration, String jdimeDeclaration) {
		this.files = files;
		this.s3mOutput = s3mOutput;
		this.jdimeOutput = jdimeOutput;
		this.leftContent = leftContent;
		this.baseContent = baseContent;
		this.rightContent = rightContent;
		this.conclusao = conclusao;
		this.type = type;
		this.s3mConflict = s3mConflict;
		this.jdimeConflict = jdimeConflict;
		this.s3mDeclaration = s3mDeclaration;
		this.jdimeDeclaration = jdimeDeclaration;
	}

	public void clean() {
		files = "";
		s3mOutput = "";
		jdimeOutput = "";
		leftContent = "";
		baseContent = "";
		rightContent = "";

		conclusao = "";
		type = "";
		s3mConflict = "";
		jdimeConflict = "";
		s3mDeclaration = "";
		jdimeDeclaration = "";
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new ReportEntity(this.files, this.s3mOutput, this.jdimeOutput,
				this.leftContent, this.baseContent, this.rightContent,
				this.conclusao, this.type, this.s3mConflict,
				this.jdimeConflict, this.s3mDeclaration, this.jdimeDeclaration);
	}
}
