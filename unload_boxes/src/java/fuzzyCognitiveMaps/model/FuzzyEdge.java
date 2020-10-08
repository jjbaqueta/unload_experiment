package fuzzyCognitiveMaps.model;

/**
 * This class implements a simple edge of a Cognitive Fuzzy Map
 */
public class FuzzyEdge implements Comparable<FuzzyEdge> 
{
	private Integer id;
	private String name;
	private Double value;
	private Integer source;
	private Integer target;
	
	public FuzzyEdge(Integer id, String name, Double value, Integer source, Integer target) 
	{
		this.id = id;
		this.name = name;
		this.value = value;
		this.source = source;
		this.target = target;
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

	public Integer getSource() 
	{
		return source;
	}

	public Integer getTarget() 
	{
		return target;
	}

	@Override
	public String toString() 
	{
		return "FuzzyEdge [id=" + id 
				+ ", name=" + name 
				+ ", value=" + value 
				+ ", source=" + source 
				+ ", target=" + target + "]";
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
		FuzzyEdge other = (FuzzyEdge) obj;
		if (id == null) 
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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

	public int compareTo(FuzzyEdge other) 
	{
		if (this.id == other.id && this.name == other.name)
		{
			return 0;
		}
		return 1;
	}
}