package Model.Node.TreeNode;

import Model.Node.INode;

public class TypeNode implements INode {
	private String name;
	private int id;
	
	public TypeNode(String name) {
		// TODO Auto-generated constructor stub
		this.name=name;
	}
	public TypeNode(String name,int id){
		this.name=name;
		this.id=id;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
