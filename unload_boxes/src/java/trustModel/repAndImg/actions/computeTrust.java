package trustModel.repAndImg.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import trustModel.fcm.model.FmcEdge;
import trustModel.fcm.model.FmcNode;
import trustModel.fcm.service.GenerateDotFileVisitor;
import trustModel.fcm.service.PropagateInputsVisitor;
import trustModel.repAndImg.enums.Mnemonic;
import trustModel.repAndImg.model.Impression;
import trustModel.repAndImg.model.TrustTree;

/**
 * This class implements an action executed by an agent.
 * Action: calculate the trust value based on the internal factors.
 */
public class computeTrust extends DefaultInternalAction
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Action's arguments (from args parameter):
	 * args[0]: requester's name.
	 * args[1]: target's name.
	 * args[2]: target's skill.
	 * args[3]: target's image.
	 * args[4]: target's reputation.
	 * args[5]: target's references.
	 * args[6]: target's availability
	 * args[7]: values for edges
	 * args[8]: the trust believe.
	 */
	@Override
	public Object execute(TransitionSystem ts,	Unifier un, Term[] args) throws Exception 
	{	
		TrustTree trustTree = new TrustTree();
		Map<Integer, Double> inputs = new HashMap<Integer, Double>();
		
		Atom requesterName = (Atom) args[0];
		Atom providerName = (Atom) args[1];;
		Atom skill = (Atom) args[2];
		ListTerm image = (ListTerm)args[3];
		ListTerm reputation = (ListTerm)args[4];
		ListTerm reference = (ListTerm)args[5];
		NumberTerm availability = (NumberTerm) args[6];
		Structure edgeValues = (Structure) args[7];
		
		// Loading the image to the trust tree
		addInputNodes(inputs, trustTree, trustTree.getFuzzyMap().getNodesByName(Mnemonic.IMAGE.name()).get(0), 
				image.isEmpty() ? null : (Structure) image.get(0));
		
		// Loading the reputation to the trust tree
		addInputNodes(inputs, trustTree, trustTree.getFuzzyMap().getNodesByName(Mnemonic.REPUTATION.name()).get(0), 
				reputation.isEmpty() ? null : (Structure) reputation.get(0));
		
		// Loading references to the trust tree
		addInputNodes(inputs, trustTree, trustTree.getFuzzyMap().getNodesByName(Mnemonic.KNOWHOW.name()).get(0), 
				reference.isEmpty() ? null : (Structure) reference.get(0));
		
		// Loading availability value
		trustTree.getFuzzyMap().getNodesByName(Mnemonic.AVAILABILITY.getMnemonic()).get(0).setValue(availability.solve());
		
		Structure abilityEffect = (Structure) edgeValues.getTerm(0);
		NumberTerm abilityValue = (NumberTerm) abilityEffect.getTerm(0);
		
		Structure availabilityEffect = (Structure) edgeValues.getTerm(1);
		NumberTerm availabilityValue = (NumberTerm) availabilityEffect.getTerm(0);
		
		Structure knowhowEffect = (Structure) edgeValues.getTerm(2);
		NumberTerm knowhowValue = (NumberTerm) knowhowEffect.getTerm(0);
		
		Structure irEffect = (Structure) edgeValues.getTerm(3);
		NumberTerm irValue = (NumberTerm) irEffect.getTerm(0);
		
		Structure repEffect = (Structure) edgeValues.getTerm(4);
		NumberTerm repValue = (NumberTerm) repEffect.getTerm(0);
		
		Structure imgEffect = (Structure) edgeValues.getTerm(5);
		NumberTerm imgValue = (NumberTerm) imgEffect.getTerm(0);
		
		trustTree.getFuzzyMap().getEdgesByName(Mnemonic.ABILITY_EFFECT.getMnemonic()).get(0).setValue(abilityValue.solve());	
		trustTree.getFuzzyMap().getEdgesByName(Mnemonic.AVAILABILITY_EFFECT.getMnemonic()).get(0).setValue(availabilityValue.solve());		
		trustTree.getFuzzyMap().getEdgesByName(Mnemonic.KNOWHOW_EFFECT.getMnemonic()).get(0).setValue(knowhowValue.solve());
		trustTree.getFuzzyMap().getEdgesByName(Mnemonic.REASONING_EFFECT.getMnemonic()).get(0).setValue(irValue.solve());
		trustTree.getFuzzyMap().getEdgesByName(Mnemonic.REP_EFFECT.getMnemonic()).get(0).setValue(repValue.solve());
		trustTree.getFuzzyMap().getEdgesByName(Mnemonic.IMG_EFFECT.getMnemonic()).get(0).setValue(imgValue.solve());
		
		// Computing trust value
		PropagateInputsVisitor propagateVisitor = new PropagateInputsVisitor(inputs);
		GenerateDotFileVisitor dotVisitor = new GenerateDotFileVisitor(requesterName.toString() + providerName.toString());
		trustTree.getFuzzyMap().accept(propagateVisitor);
		trustTree.getFuzzyMap().accept(dotVisitor);
		
		// Creating the trust belief
		Structure belief = new Structure(Mnemonic.TRUST.getMnemonic());
		
		belief.addTerm(providerName);
		belief.addTerm(skill);
		belief.addTerm(new NumberTermImpl(trustTree.getFuzzyMap().getOutput().getValue()));
		
		return un.unifies(belief, args[8]);
	}
	
	private void addInputNodes(Map<Integer, Double> inputs, TrustTree trustTree, FmcNode tarNode, Structure belief)
	{	
		if(belief != null)
		{			
			for(Entry<String, Double> pair: Impression.parserBeleif(belief).getValues())
			{
				FmcNode srcNode = new FmcNode(trustTree.incNodeId(), pair.getKey(), pair.getValue());
				trustTree.getFuzzyMap().insertNode(srcNode);
				trustTree.getFuzzyMap().setAsInput(srcNode.getId());
				trustTree.getFuzzyMap().insertEdge(new FmcEdge(trustTree.incEdgeId(), "e", 1.0, srcNode.getId(), tarNode.getId()));
				inputs.put(srcNode.getId(), pair.getValue());
			}
		}
		else
		{
			inputs.put(tarNode.getId(), -1.0);
			trustTree.getFuzzyMap().setAsInput(tarNode.getId());
		}
	}
}