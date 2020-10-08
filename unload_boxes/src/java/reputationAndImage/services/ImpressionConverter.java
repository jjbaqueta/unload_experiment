package reputationAndImage.services;

import java.util.Map;

import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.StringTermImpl;
import jason.asSyntax.Structure;
import reputationAndImage.model.Impression;
import reputationAndImage.model.Mnemonic;

/**
 * This class converts objects into beliefs. 
 */

public class ImpressionConverter 
{
	/**
	 * This method generates impression, image or reputation beliefs.
	 * @param impression: basic object used to produce image and reputation beliefs.
	 * @param beliefType: type of the desired belief.
	 * @return a literal that represents the produced belief.
	 */
	public static Literal run(Impression impression, Mnemonic beliefType)
	{
		Structure belief;
		
		switch (beliefType)
		{
			case IMPRESSION:
				belief = new Structure(Mnemonic.IMPRESSION.getMnemonic());
				break;
				
			case IMAGE:
				belief = new Structure(Mnemonic.IMAGE.getMnemonic());
				break;
				
			case REPUTATION:
				belief = new Structure(Mnemonic.REPUTATION.getMnemonic());
				break;
				
			case KNOWHOW:
				belief = new Structure(Mnemonic.KNOWHOW.getMnemonic());
				break;
				
			default:
				throw new IllegalArgumentException("Literal name is not valid: " + beliefType);
		}
	
		belief.addTerm(new Atom(impression.getRequesterName()));
		belief.addTerm(new Atom(impression.getProviderName()));
		belief.addTerm(new NumberTermImpl(impression.getTime()));
		belief.addTerm(new StringTermImpl(impression.getSkill().name()));
		
		ListTerm criteriaNames = new ListTermImpl();
		ListTerm criteriaValues = new ListTermImpl();
		
		for(Map.Entry<String, Double> pair : impression.getValues())
		{
			criteriaNames.add(new StringTermImpl(pair.getKey()));
			criteriaValues.add(new NumberTermImpl(pair.getValue()));
		}
		
		belief.addTerm(criteriaNames);
		belief.addTerm(criteriaValues);
		
		return Literal.parseLiteral(belief.toString());
	}
}