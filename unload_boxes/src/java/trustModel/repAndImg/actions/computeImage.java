package trustModel.repAndImg.actions;

import java.util.Set;
import java.util.TreeSet;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
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
	 * args[5]: intimate level of interactions for other impressions (itm).
	 */
	@Override
	public Object execute(TransitionSystem ts,	Unifier un, Term[] args) throws Exception 
	{
		Set<Impression> impressions = new TreeSet<Impression>();
		
		for(Term imp : (ListTerm) args[0])
			impressions.add(Impression.parserBeleif((Structure) imp));
		
		Atom requesterName = (Atom) args[1];
		Atom providerName = (Atom) args[2];
		Atom skill = (Atom) args[3];
		Set<String> criteria = new TreeSet<String>();
		
		for(Term t : (ListTerm) args[4])
		{
			StringTerm criterion = (StringTerm) t;
			criteria.add(criterion.getString());
		}
		NumberTerm iTM = (NumberTerm) args[5];
		
		Impression image = ImpressionAggregation.run(impressions, requesterName.toString(), providerName.toString(), 
				Skill.valueOf(skill.toString()), criteria, (int) iTM.solve());
		
		ts.getAg().delBel(Literal.parseLiteral(Mnemonic.IMAGE.getMnemonic() + 
				"(_," + providerName + ",_,"+ skill +",_,_)[source(self)]"));
		
		ts.getAg().addBel(ImpressionConverter.run(image, Mnemonic.IMAGE));
		return true;
	}
}