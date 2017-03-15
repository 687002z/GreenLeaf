package Model.Tree;

public class TypeNode implements ITreeNode{
	private String name;
	
	public TypeNode(String name) {
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
}
