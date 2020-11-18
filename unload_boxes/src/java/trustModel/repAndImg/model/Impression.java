package trustModel.repAndImg.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jason.NoValueException;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Structure;
import trustModel.repAndImg.enums.Skill;

/**
 * This class implements an impression.
 * An impression is produced by an appraiser agent (requester).
 * The appraiser makes an evaluation about the service performed by another agent (provider).
 */
public class Impression implements Comparable<Impression> 
{
	private String requesterName;
	private String providerName;
	private Long time;
	private Skill skill;
	private Map<String, Double> ratings;
	
	public Impression(String requesterName, String providerName, long time, Skill skill) 
	{
		this.requesterName = requesterName;
		this.providerName = providerName;
		this.time = time;
		this.skill = skill;
		this.ratings = new HashMap<String, Double>();
	}
	
	/**
	 * Insert a new rating in the rating map.
	 * @param criterion: criterion's name (key).
	 * @param rating: criterion's value.
	 */
	public void insertRating(String criterionName, Double criterionValue)
	{
		this.ratings.put(criterionName, criterionValue);
	}
	
	/**
	 * Get the value of a given criterion.
	 * @param criterionName: criterion's name (key).
	 * @return the criterion's value.
	 */
	public Double getValue(String criterionName)
	{
		Double value = this.ratings.get(criterionName);
		
		if(value == null)
		{
			throw new IllegalArgumentException("The criterion cannot be found: " + criterionName);
		}
		return value;
	}
	
	/**
	 * Change the value of a rating.
	 * @param criterionName: criterion's name (key).
	 * @param rating: criterion's value.
	 */
	public void changeValue(String criterionName, Double criterionValue)
	{
		Double value = this.ratings.get(criterionName);
		
		if(value == null)
		{
			throw new IllegalArgumentException("Criterion not found. Informed criterion was not inserted in the map yet: " + criterionName);
		}
		this.ratings.put(criterionName, criterionValue);
	}
	
	/**
	 * @return the set of criteria.
	 */
	public Set<String> getCriteria()
	{
		return this.ratings.keySet();
	}
	
	/**
	 * @return a set of pairs(key: criterion's name, value: criterion's value).
	 */
	public Set<Map.Entry<String, Double>> getValues()
	{
		return this.ratings.entrySet();
	}
	
	public String getRequesterName() 
	{
		return requesterName;
	}

	public String getProviderName() 
	{
		return providerName;
	}

	public Skill getSkill() 
	{
		return skill;
	}

	public Long getTime() 
	{
		return time;
	}
	
	/**
	 * This method translates an impression (belief) into an object.
	 * An impression is obtained from the belief base of an agent.
	 * @param imp: an impression (belief), format: imp(requester,provider,time,skill,[criteria],[values])
	 * @return an impression (Object).
	 */
	public static Impression parserBeleif(Structure imp)
	{	
		Impression impression = null;
		
		try 
		{
			Atom requesterName = (Atom) imp.getTerm(0);
			Atom providerName = (Atom) imp.getTerm(1);
			NumberTerm time = (NumberTerm) imp.getTerm(2);
			Atom skill = (Atom) imp.getTerm(3);
			ListTerm keys = (ListTerm) imp.getTerm(4);
			ListTerm values = (ListTerm) imp.getTerm(5);
			
			impression = new Impression(requesterName.toString(), providerName.toString(),
					(long) time.solve(), Skill.valueOf(skill.toString()));
			
			for(int i = 0; i < keys.size(); i++)
			{
				NumberTerm value = (NumberTerm) values.get(i);
				StringTerm key = (StringTerm) keys.get(i);
				
				impression.insertRating(key.getString(), value.solve());
			}	
		} 
		catch (NoValueException e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return impression;
	}

	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("imp(").append(this.requesterName).append(",")
		.append(this.providerName).append(",")
		.append(this.skill).append(",{");
		
		for(Map.Entry<String, Double> pair : this.ratings.entrySet())
		{
			sb.append("(key:").append(pair.getKey())
			.append(",value:").append(pair.getValue())
			.append(")");
		}
		sb.append("}");
		
		return sb.toString();
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((requesterName == null) ? 0 : requesterName.hashCode());
		result = prime * result + ((providerName == null) ? 0 : providerName.hashCode());
		result = prime * result + ((skill == null) ? 0 : skill.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		Impression other = (Impression) obj;
		if (providerName == null) 
		{
			if (other.providerName != null)
				return false;
		} 
		else if (!providerName.equals(other.providerName))
			return false;
		if (requesterName == null) 
		{
			if (other.requesterName != null)
				return false;
		} 
		else if (!requesterName.equals(other.requesterName))
			return false;
		if (skill != other.skill)
			return false;
		if (time == null) 
		{
			if (other.time != null)
				return false;
		} 
		else if (!time.equals(other.time))
			return false;
		return true;
	}

	public int compareTo(Impression other) 
	{
		if(this.requesterName.equals(other.requesterName) 
				&& (this.providerName.equals(other.providerName)) 
				&& (this.skill == other.skill) 
				&& (this.time == other.time)) 
		{
			return 0;
		}
		else if (this.time > other.time)
		{
			return 1;
		}
		else return -1;
	}
}