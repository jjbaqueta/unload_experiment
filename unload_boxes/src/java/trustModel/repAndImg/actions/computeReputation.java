package trustModel.repAndImg.actions;

import java.util.Set;
import java.util.TreeSet;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import trustModel.repAndImg.enums.Mnemonic;
import trustModel.repAndImg.enums.Skill;
import trustModel.repAndImg.model.Impression;
import trustModel.repAndImg.services.ImpressionAggregation;
import trustModel.repAndImg.services.ImpressionConverter;

/**
 * This class implements an action executed by an agent.
 * Action: images aggregation into a reputation belief.
 */

public class computeReputation extends DefaultInternalAction
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Action's arguments (from args parameter):
	 * args[0]: list of images (impressions from third party agents).
	 * args[1]: requester agent's name.
	 * args[2]: provider agent's name.
	 * args[3]: evaluated skill.
	 * args[4]: evaluation criteria.
	 */
	@Override
	public Object execute(TransitionSystem ts,	Unifier un, Term[] args) throws Exception 
	{
		Set<Impression> Images = new TreeSet<Impression>();
		
		for(Term img : (ListTerm) args[0])
			Images.add(Impression.parserBeleif((Structure) img));
		
		Atom requesterName = (Atom) args[1];
		Atom providerName = (Atom) args[2];
		Atom skill = (Atom) args[3];
		Set<String> criteria = new TreeSet<String>();
		
		for(Term t : (ListTerm) args[4])
		{
			StringTerm criterion = (StringTerm) t;
			criteria.add(criterion.getString());
		}
		
		Impression reputation = ImpressionAggregation.run(Images, requesterName.toString(), providerName.toString(), 
				Skill.valueOf(skill.toString()), criteria);
		
		ts.getAg().delBel(Literal.parseLiteral(Mnemonic.REPUTATION.getMnemonic() + 
				"(_," + providerName + ",_,"+ skill +",_,_)[source(_)]"));
		
		ts.getAg().addBel(ImpressionConverter.run(reputation, Mnemonic.REPUTATION));
		
		return true;
	}
}