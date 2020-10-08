package reputationAndImage.actions;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Term;
import reputationAndImage.model.Impression;
import reputationAndImage.model.Mnemonic;
import reputationAndImage.model.Skill;
import reputationAndImage.model.TimeBB;
import reputationAndImage.services.ImpressionConverter;

/**
 * This class implements an action executed by an agent.
 * Action: adds an impression into the agent's belief base.
 */
public class addImpression extends DefaultInternalAction
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Action's arguments (from args parameter):
	 * args[0]: requester agent's name.
	 * args[1]: provider agent's name.
	 * args[2]: skill.
	 * args[3]: criteria names.
	 * args[4]: criteria values.
	 * args[5]: return the produced impression as work reference.
	 */
	@Override
	public Object execute(TransitionSystem ts,	Unifier un, Term[] args) throws Exception 
	{	
		String requesterName = args[0].toString();
		String providerName = args[1].toString();
		StringTerm skill = (StringTerm) args[2];
		ListTerm criteriaName = (ListTerm) args[3];
		ListTerm criteriaValue = (ListTerm) args[4];
		
		Impression impression = new Impression(requesterName, providerName, 
				(long) System.currentTimeMillis() - TimeBB.start, 
				Skill.valueOf(skill.getString()));
		
		for(int i = 0; i < criteriaName.size(); i++)
		{
			StringTerm criterionName = (StringTerm) criteriaName.get(i);
			NumberTerm criterionValue = (NumberTerm) criteriaValue.get(i);
			impression.insertRating(criterionName.getString(), criterionValue.solve());
		}
		
		Literal reference = ImpressionConverter.run(impression, Mnemonic.IMPRESSION);
		ts.getAg().addBel((Literal) reference.clone());
		
		return un.unifies(reference, args[5]);
	}
}