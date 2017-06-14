package Model.Tree;

public class TopicNode implements INode {
	private String name;
	private String xsdFileName;
	
		public TopicNode(String name) {
		// TODO Auto-generated constructor stub
		this.name=name;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name=name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String getXsdFileName() {
		return this.xsdFileName;
	}
	public void setXsdFileName(String xsdFileName) {
		this.xsdFileName = xsdFileName;
	}

}
