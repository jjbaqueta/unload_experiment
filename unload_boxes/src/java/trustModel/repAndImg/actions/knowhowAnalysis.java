package trustModel.repAndImg.actions;

import java.util.Set;
import java.util.TreeSet;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import trustModel.repAndImg.enums.Mnemonic;
import trustModel.repAndImg.model.Impression;
import trustModel.repAndImg.services.ImpressionAggregation;
import trustModel.repAndImg.services.ImpressionConverter;

/**
 * This class implements an action executed by an agent.
 * Action: evaluation about the knowhow of a given agent.
 */
public class knowhowAnalysis extends DefaultInternalAction
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Action's arguments (from args parameter):
	 * args[0]: requester's name.
	 * args[1]: provider's name.
	 * args[2]: references of the agent considering its work history (impressions).
	 */
	@Override
	public Object execute(TransitionSystem ts,	Unifier un, Term[] args) throws Exception 
	{	
		String requesterName = args[0].toString();
		String providerName = args[1].toString();
		ListTerm list = (ListTerm) args[2];
		Set<Impression> references = new TreeSet<Impression>();
		
		for(Term imp : list)
			references.add(Impression.parserBeleif((Structure) imp));
		
		Impression impTemp = (Impression) references.toArray()[0];
		Impression reference = ImpressionAggregation.run(references, 
				requesterName, providerName, 
				impTemp.getSkill(), impTemp.getCriteria());
		
		ts.getAg().delBel(Literal.parseLiteral(Mnemonic.KNOWHOW.getMnemonic() + 
				"(_," + providerName + ",_,"+ impTemp.getSkill() +",_,_)[source(_)]"));
		
		ts.getAg().addBel(ImpressionConverter.run(reference, Mnemonic.KNOWHOW));
		return true;
	}
}
