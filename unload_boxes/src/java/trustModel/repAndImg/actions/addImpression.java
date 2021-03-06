package trustModel.repAndImg.actions;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Term;
import trustModel.repAndImg.enums.Mnemonic;
import trustModel.repAndImg.enums.Skill;
import trustModel.repAndImg.model.Impression;
import trustModel.repAndImg.model.TimeBB;
import trustModel.repAndImg.services.ImpressionConverter;

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
		Atom requesterName = (Atom) args[0];
		Atom providerName = (Atom) args[1];
		Atom skill = (Atom) args[2];
		ListTerm criteriaName = (ListTerm) args[3];
		ListTerm criteriaValue = (ListTerm) args[4];
		
		Impression impression = new Impression(requesterName.toString(), providerName.toString(), 
				(long) System.currentTimeMillis() - TimeBB.start, 
				Skill.valueOf(skill.toString()));
		
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