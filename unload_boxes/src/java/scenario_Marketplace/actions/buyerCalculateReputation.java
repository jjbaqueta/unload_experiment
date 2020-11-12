package scenario_Marketplace.actions;

import java.util.ArrayList;
import java.util.List;

import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import reputationModels.Impression;
import reputationModels.Rating;
import reputationModels.ReGret;

public class buyerCalculateReputation extends DefaultInternalAction{

	private static final long serialVersionUID = 1L;
	
	/*
	 * This method computes the reputation of a given seller from a list of impressions about him.
	 * The arguments are defined as follow:
	 * - args[0]: List of impressions, literal format: imp(Buyer,Agent,Time,[Price,Quality,Delivery])
	 * - args[1]: Return Agent's reputation, format rep(Agent,R_price,R_quality,R_delivery,L_price,L_quality,L_delivery); R: reputation, L: reliability
	 */	
	@Override
	public Object execute( TransitionSystem ts, Unifier un, Term[] args ) throws JasonException 
	{
		try
		{	
			List<Rating> ratings = new ArrayList<Rating>();
			
			// Translating impressions in Literal format to an Object list 
			ListTerm impressionTermList = (ListTerm) args[0];
			
			for(Term t : impressionTermList)
				ratings.add(Impression.parseImpression(t.toString()));
			
			if(!ratings.isEmpty())
			{
				// Getting current time.
				long currentTime = System.currentTimeMillis();
				
				// Using ReGret model.
				double[] reputations = ReGret.computeSubjectiveReputation(currentTime, ratings);
				double[] reliabilities = ReGret.computeReliability(reputations, currentTime, ratings);
				
				Impression imp = (Impression) ratings.get(0);
				
				return un.unifies(Literal.parseLiteral("rep("+ imp.getAppraised().getName()+","+currentTime+","
				+reputations[0]+","+reputations[1]+","+reputations[2]+","
				+reliabilities[0]+","+reliabilities[1]+","+reliabilities[2]+")"), args[1]);
			}
			else
				return un.unifies(Literal.parseLiteral("rep(none)"), args[1]);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			throw new JasonException("The internal action must receive four arguments!");
		}
		catch (ClassCastException e) {
			throw new JasonException("received arguments are out of format");
		}
		catch(Exception e)
		{
			throw new JasonException(e.getMessage());
		}
	}
}
