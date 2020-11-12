package scenario_Marketplace.actions;

import entities.services.MarketFacade;
import environments.Market;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import reputationModels.Reputation;

public class sellerSaveReputation extends DefaultInternalAction
{
	private static final long serialVersionUID = 1L;

	/*
	 * This method may change the initial conditions of a contract defined between a seller and a buyer according to seller's type
	 * The contract informations are passed from the array args:
	 * - args[0]: reputation profile
	 * - args[1]: R
	 */	
	@Override
	public Object execute( TransitionSystem ts, Unifier un, Term[] args ) throws JasonException 
	{	
		try
		{	
			// Parsing the received reputation
			Reputation reputation = Reputation.parseReputation(args[0].toString());
			
			// Updating reputation of the agent in model
			int sellerId = MarketFacade.getSellerIdFrom(reputation.getAgent().getName());
			Market.sellers[sellerId].addReputationInLog(reputation);
			
			//Returns the result as Term
			return un.unifies(Literal.parseLiteral("none"), args[1]);		
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			throw new JasonException("The internal action must receive one argument!");
		}
		catch(Exception e)
		{
			throw new JasonException(e.getMessage());
		}
	}
}