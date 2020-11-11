package trustModel.repAndImg.actions;

import java.util.Set;
import java.util.TreeSet;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
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
 * Action: impressions aggregation into a image belief.
 */

public class computeImage extends DefaultInternalAction
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Action's arguments (from args parameter):
	 * args[0]: list of impressions (only direct impressions).
	 * args[1]: requester agent's name.
	 * args[2]: provider agent's name.
	 * args[3]: evaluated skill.
	 * args[4]: evaluation criteria.
	 */
	@Override
	public Object execute(TransitionSystem ts,	Unifier un, Term[] args) throws Exception 
	{
		Set<Impression> impressions = new TreeSet<Impression>();
		
		for(Term imp : (ListTerm) args[0])
			impressions.add(Impression.parserBeleif((Structure) imp));
		
		String requesterName = args[1].toString();
		String providerName = args[2].toString();
		StringTerm skill = (StringTerm) args[3];
		Set<String> criteria = new TreeSet<String>();
		
		for(Term t : (ListTerm) args[4])
		{
			StringTerm criterion = (StringTerm) t;
			criteria.add(criterion.getString());
		}
		
		Impression image = ImpressionAggregation.run(impressions, requesterName, providerName, 
				Skill.valueOf(skill.getString()), criteria);
		
		ts.getAg().delBel(Literal.parseLiteral(Mnemonic.IMAGE.getMnemonic() + 
				"(_," + providerName + ",_,"+ skill +",_,_)[source(self)]"));
		
		ts.getAg().addBel(ImpressionConverter.run(image, Mnemonic.IMAGE));
		return true;
	}
}