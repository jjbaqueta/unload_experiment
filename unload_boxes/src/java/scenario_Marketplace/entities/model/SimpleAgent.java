package scenario_Marketplace.entities.model;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class implements a simple agent.
 * It is inherited by Buyer and Seller classes.
 * @see{Buyer and Seller}
 */

public abstract class SimpleAgent implements Comparable<SimpleAgent>
{
	private static AtomicInteger seqId = new AtomicInteger();
	
	protected Integer id;
	protected String name;

	public SimpleAgent(String name) 
	{
		this.id = seqId.getAndIncrement() + 1;
		this.name = name + "_" + id;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public int getId() 
	{
		return this.id;
	}
	
	public int compareTo(SimpleAgent agent) 
	{ 
		return name.compareTo(agent.getName());
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
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
		SimpleAgent other = (SimpleAgent) obj;
		if (name == null) 
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() 
	{
		return "id=" + id + ", name=" + name + "";
	}
}
