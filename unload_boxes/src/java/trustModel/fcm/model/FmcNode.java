package trustModel.fcm.model;

/**
 * This class implements a simple node of a Cognitive Fuzzy Map
 */
public class FmcNode implements Comparable<FmcNode> 
{
	private final Integer id;
	private final String name;
	private Double value;
	
	public FmcNode(Integer id, String name, Double value) 
	{
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public Integer getId() 
	{
		return id;
	}
	
	public String getName() 
	{
		return name;
	}

	public Double getValue() 
	{
		return value;
	}
	
	public void setValue(Double value)
	{
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "FuzzyNode [id=" + id 
				+ ", name=" + name 
				+ ", value=" + value + "]";
	}
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FmcNode other = (FmcNode) obj;
		if (id == null) 
		{
			if (other.id != null)
				return false;
		} 
		else if (!id.equals(other.id))
			return false;
		if (name == null) 
		{
			if (other.name != null)
				return false;
		} 
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	public int compareTo(FmcNode other) 
	{
		if (this.id == other.id && this.name == other.name)
		{
			return 0;
		}
		return 1;
	}
}
