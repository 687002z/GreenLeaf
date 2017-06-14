package Model.Tree;

public class EventNode implements INode {
	
	private String name;
	private String topic;
	
	private String conditions;
	
	public EventNode(String name) {
		// TODO Auto-generated constructor stub
		this.name=name;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
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
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

}
