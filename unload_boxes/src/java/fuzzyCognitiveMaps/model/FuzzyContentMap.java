package fuzzyCognitiveMaps.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to centralize the values of the nodes and edges.
 * In this way, the value of any node or edge can be changed instantaneously.
 */
public class FuzzyContentMap <C>
{
	private final Map<Integer, C> contentMap;
	
	public FuzzyContentMap() 
	{
		this.contentMap = new HashMap<Integer, C>();
	}

	/**
	 * Insert a new element in the map
	 * @param id: element's id
	 * @param content: element's content
	 */
	public void insertElement(Integer id, C content)
	{
		this.contentMap.put(id, content);
	}
	
	/**
	 * @return the element's content as result of a searching for the element's name
	 */
	public C getValue(Integer elementId)
	{
		C value = null;
		
		for (Integer id : this.contentMap.keySet())
		{
			if (id == elementId)
			{
				value = this.contentMap.get(elementId);
				break;
			}
		}
		
		if (value != null)
		{
			return value;
		}
		else
		{
			throw new IllegalArgumentException("The element cannot be found: " + elementId);
		}
	}
	
	/**
	 * Set a new value for an element
	 */
	public void setValue(Integer elementId, C newValue)
	{
		C oldValue = this.contentMap.get(elementId);
		
		if (oldValue == null)
		{
			throw new IllegalArgumentException("The element cannot be found: " + elementId);
		}
		else
		{
			this.contentMap.put(elementId, newValue);
		}
	}
	
	/**
	 * @return a Collection with the value of each element in the map
	 */
	public Collection<C> getValues()
	{
		return this.contentMap.values();
	}
	
	/**
	 * @return a Set with the id of each element in the map
	 */
	public Set<Integer> getKeys()
	{
		return this.contentMap.keySet();
	}
	
	@Override
	public String toString() 
	{
		StringBuilder strBuffer = new StringBuilder();		
		
		for (Map.Entry<Integer, C> entry : this.contentMap.entrySet())
		{
			strBuffer.append("key: " + entry.getKey() 
			+ ", value: " + entry.getValue() + "\n");
		}
		
		return strBuffer.toString();
	}
}
