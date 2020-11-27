// Internal action code for project Trust_scenarios

package scenario_Marketplace.actions.buyer;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Buyer;
import scenario_Marketplace.enums.CriteriaType;
import scenario_Marketplace.environments.Market;
import trustModel.repAndImg.enums.Mnemonic;
import trustModel.repAndImg.model.Impression;
import trustModel.repAndImg.services.ImpressionConverter;

/**
 * This class implements an action executed by an agent.
 * Action: update the reputation, image, and knowhow believes based on the buyer's preferences.
 */
public class applyPreferences extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Action's arguments (from args parameter):
	 * args[0]: buyer name.
	 * args[1]: buyers's image about a seller.
	 * args[2]: buyers's reputation about a seller.
	 * args[3]: buyers's knowhow about a seller.
	 * args[4]: return the updated image, reputation and knowhow.
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	Buyer buyer = Market.buyers.get(args[0].toString());
    	
    	ListTerm listImg = (ListTerm) args[1];
    	ListTerm listRep = (ListTerm) args[2];
    	ListTerm listRef = (ListTerm) args[3];
    	
    	if(!listImg.isEmpty())
    	{
    		Structure img = (Structure) listImg.get(0);
    		Impression image = Impression.parserBeleif(img);
    		updateValues(buyer, image);
    		listImg = new ListTermImpl();
    		listImg.add(ImpressionConverter.run(image, Mnemonic.IMAGE));
    	}
    	
    	if(!listRep.isEmpty())
    	{
    		Structure rep = (Structure) listRep.get(0);
    		Impression reputation = Impression.parserBeleif(rep);
    		updateValues(buyer, reputation);
    		listRep = new ListTermImpl();
    		listRep.add(ImpressionConverter.run(reputation, Mnemonic.REPUTATION));
    	}
    	
    	if(!listRef.isEmpty())
    	{
    		Structure ref = (Structure) listRef.get(0);
    		Impression reference = Impression.parserBeleif(ref);
    		updateValues(buyer, reference);
    		listRef = new ListTermImpl();
    		listRef.add(ImpressionConverter.run(reference, Mnemonic.KNOWHOW));
    	}

    	Structure tuples = new Structure("tuple");
    	
    	tuples.addTerm(listImg);
    	tuples.addTerm(listRep);
    	tuples.addTerm(listRef);
    	
        return un.unifies(tuples, args[4]);
    }
    
    /**
     * Change the value of a criterion according with the buyer's preferences.
     * @param buyer: a buyer.
     * @param imp: the impression that'll be its values changed.
     */
    private void updateValues(Buyer buyer, Impression imp)
    {
    	for (String criterion : imp.getCriteria())
    	{
    		double value = imp.getValue(criterion) * buyer.getPreference(CriteriaType.valueOf(criterion));
    		imp.changeValue(criterion, value);    		
    	}
    }
}