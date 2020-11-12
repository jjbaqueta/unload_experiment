package scenario_Marketplace.actions;

import java.util.List;

import entities.services.MarketFacade;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Term;

public class managerInitializeSellers extends DefaultInternalAction
{
	private static final long serialVersionUID = 1L;
	
	/*
	 * This method creates the sellers within the environment 
	 * The informations used in the creation process are passed from the array args:
	 * - args[0]: Return a list which contains the sellers names  
	 */
	
	@Override
	public Object execute( TransitionSystem ts, Unifier un, Term[] args ) throws JasonException 
	{
		try
		{	
			String str = "[";
			List<String> names = MarketFacade.getSellersNamesList(); 
			
			for(int i = 0; i < names.size() - 1; i++)
				str += "seller("+ names.get(i) +"),";
			
			str += "seller("+ names.get(names.size() - 1) +")]";
			
			return un.unifies(ListTermImpl.parseList(str), args[0]);
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
