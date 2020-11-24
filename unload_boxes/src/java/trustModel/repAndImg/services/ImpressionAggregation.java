package trustModel.repAndImg.services;

import java.util.Set;

import trustModel.repAndImg.enums.Skill;
import trustModel.repAndImg.model.Impression;
import trustModel.repAndImg.model.TimeBB;

public class ImpressionAggregation 
{
	/**
	 * This method aggregates a set of impressions into a single impression. 
	 * @param impressions: a set of impressions.
	 * @param requestName: the name of requester agent.
	 * @param providerName: the name of provider agent.
	 * @param skill: the skill which the evaluation refers.
	 * @param criteria: a set of evaluation criteria.
	 * @param iTM: the intimate level of interactions.
	 * @return an impression resulting of aggregation operation.
	 */
	public static Impression run(Set<Impression> impressions, String requesterName, String providerName, 
			Skill skill, Set<String> criteria, int iTM)
	{
		long aggrTime = System.currentTimeMillis() - TimeBB.start;
		
		double tj = fTj(impressions, aggrTime);
		double ti, aggrValue;
		
		// Computing reputation
		Impression resultImp = new Impression(requesterName, providerName, aggrTime, skill);
		
		for(String criterion : criteria)
		{
			aggrValue = 0;
			
			for(Impression imp : impressions)
			{
				ti = (((double) imp.getTime() / aggrTime) / tj);
				aggrValue += imp.getValue(criterion) * ti;
			}
			double value = aggrValue;
			
			resultImp.insertRating(criterion, value);			
		}
		
		// Computing reliability
		Impression reliability = computeReliability(resultImp, aggrTime, impressions, requesterName, providerName, skill, criteria, iTM);
		
		for(String criterion : criteria)
		{
			double value = resultImp.getValue(criterion);
			resultImp.changeValue(criterion, value * reliability.getValue(criterion));
		}
		return resultImp;
	}

	/*
	 * This method computes the reliability of subjective reputation 
	 * @param subjectiveRep Subjective reputation from criterion selected.
	 * @param currentTime Time instant used during the computation of subjective reputation.
	 * @param ratings List of impressions considered to compute the subjective reputation.
	 * @return how much reliable is the subjective reputation for each evaluated criterion
	 */
	private static Impression computeReliability(Impression aggrImp, long aggrTime, 
			Set<Impression> impressions, String requesterName, String providerName, 
			Skill skill, Set<String> criteria, int iTM)
	{			
		Impression resultImp = new Impression(requesterName, providerName, aggrTime, skill);
		Impression deviations = computeDeviation(aggrImp, aggrTime, impressions, requesterName, providerName, skill, criteria);
		double ni = computeNi(impressions, iTM);
		double aggrValue;
		
		for(String criterion : criteria)
		{
			aggrValue = 0;
			
			for(Impression imp : impressions)
			{
				aggrValue += imp.getValue(criterion);
			}
			double value = aggrValue / impressions.size();
			resultImp.insertRating(criterion, (1 - value) * ni + value * deviations.getValue(criterion));			
		}
		return resultImp;
	}
	
	/*
	 * This method computes the importance level of impressions list.
	 * The intimate level of interactions (ITM) is adopted in order to minimize the effects from ratings with low occurrence levels.
	 * @param impressions List of impressions considered to compute the subjective reputation.
	 * @return how much expressive is the list of impressions
	 */
	private static double computeNi(Set<Impression> impressions, int iTM)
	{	
		// Number of impressions used to calculate the reputation.
		int cardinality = impressions.size();
	
		/*
		 * Computing Ni
		 * If the rating cardinality is below the ITM, the importance of impressions is reduced (to a value less than 1).
		 * Otherwise, the importance level is defined as 1. 
		 */
		if(cardinality >=0 && cardinality <= iTM)
			return Math.sin((Math.PI * cardinality) / (2 * iTM));
//			return Math.tanh(2 * iTM * cardinality);
		else
			return 1;
	}
	
	/*
	 * This method computes the deviation of subjective reputation considering a given evaluation criterion (price, quality or delivery)
	 * A deviation value near 0 indicates a high variability, in turn, close to 1 indicates a low variability (bigger reliability).
	 * @param subjectiveRep Subjective reputation from criterion selected.
	 * @param currentTime Time instant used during the computation of subjective reputation.
	 * @param ratings List of impressions considered to compute the subjective reputation.
	 * @return Deviation of subjective reputation.
	 */
	private static Impression computeDeviation(Impression aggrImp, long aggrTime, 
			Set<Impression> impressions, String requesterName, String providerName, 
			Skill skill, Set<String> criteria)
	{		
		double tj = fTj(impressions, aggrTime);
		double ti, aggrValue;
		
		Impression deviationImp = new Impression(requesterName, providerName, aggrTime, skill);
		
		for(String criterion : criteria)
		{
			aggrValue = 0;
			
			for(Impression imp : impressions)
			{
				ti = (((double) imp.getTime() / aggrTime) / tj);
				aggrValue += Math.abs((double) imp.getValue(criterion) - aggrImp.getValue(criterion)) * ti;
			}
			double value = aggrValue;
			
			deviationImp.insertRating(criterion, 1.0 - value);			
		}
		return deviationImp;
	}
	
	/**
	 * This method applies a time adjusting based on the time function f(t, tj). 
	 * The idea is reducing the effects of older ratings on the image and reputation computing.
	 * @param impressions: a set of impressions.
	 * @param aggrTime: time when the aggregation operation was started.
	 * @return time adjustment factor. 
	 */
	private static double fTj(Set<Impression> impressions, long aggrTime)
	{
		double tj = 0;
		
		for(Impression imp : impressions)
		{
			tj += (double) imp.getTime() / aggrTime;
		}
		return tj;
	}
}