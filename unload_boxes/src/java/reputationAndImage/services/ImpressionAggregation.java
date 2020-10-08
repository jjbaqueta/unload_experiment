package reputationAndImage.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import reputationAndImage.model.Impression;
import reputationAndImage.model.Skill;
import reputationAndImage.model.TimeBB;

public class ImpressionAggregation 
{
	/**
	 * This method aggregates a set of impressions into a single impression. 
	 * @param impressions: a set of impressions.
	 * @param requestName: the name of requester agent.
	 * @param providerName: the name of provider agent.
	 * @param skill: the skill which the evaluation refers.
	 * @param criteria: a set of evaluation criteria.
	 * @return an impression resulting of aggregation operation.
	 */
	public static Impression run(Set<Impression> impressions, String requesterName, String providerName, 
			Skill skill, Set<String> criteria)
	{
		long aggrTime = System.currentTimeMillis() - TimeBB.start;
		
		double tj = fTj(impressions, aggrTime);
		double ti, aggrValue;
		List<Double> data;
		
		Impression resultImp = new Impression(requesterName, providerName,
				aggrTime, skill);
		
		for(String criterion : criteria)
		{
			aggrValue = 0;
			data = new ArrayList<Double>();
			
			for(Impression imp : impressions)
			{
				ti = (((double) imp.getTime() / aggrTime) / tj);
				aggrValue += imp.getValue(criterion) * ti;
				
				data.add(imp.getValue(criterion));
			}
			resultImp.insertRating(criterion, aggrValue * computeVariation(data));
		}
		return resultImp;
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
	
	/**
	 * This method computes the Pearson correlation coefficient.
	 * In this case, a value near 1 indicates low variability (high credibility).
	 * On the other hand, a value near 0 indicates low credibility.
	 * @param values: the set of data.
	 * @return the correlation coefficient value.
	 */
	private static double computeVariation(List<Double> values)
	{
		// Computing the mean
		double x = 0, v = 0, sd;
		
		for(double value : values)
			x += value;
		
		x /= values.size();
		
		// Computing the standard deviation
		for(double value: values)
			v += Math.pow(value - x, 2);
		
		v /= values.size();
		sd = Math.sqrt(v);
		
		return 1 - (sd / x);
	} 
}