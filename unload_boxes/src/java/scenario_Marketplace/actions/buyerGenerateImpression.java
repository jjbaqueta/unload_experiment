package scenario_Marketplace.actions;

import entities.model.Offer;
import entities.model.buyers.Buyer;
import entities.model.sellers.Seller;
import entities.services.MarketFacade;
import enums.CriteriaType;
import environments.Market;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import reputationModels.Impression;

public class buyerGenerateImpression extends DefaultInternalAction{

	private static final long serialVersionUID = 1L;

	/*
	 * This method is used by a buyer to evaluate the contract conditions defined by a seller
	 * The buyer checks if contract conditions are agreement with initial conditions and after that generates an impression about the seller
	 * The informations to generate a impression are passed from the array args:
	 * - args[0]: buyer's name
	 * - args[1]: seller's name
	 * - args[2]: original offer (initial contract conditions)
	 * - args[3]: current offer (current contract conditions)
	 * - args[4]: return a impression about the seller (rating) 
	 */	
	@Override
	public Object execute( TransitionSystem ts, Unifier un, Term[] args ) throws JasonException 
	{		
		try
		{	
			// Get the index from buyer
			int index = MarketFacade.getBuyerIdFrom(args[0].toString());
			Buyer buyer = Market.buyers[index];
			
			// Get the index from seller
			index = MarketFacade.getSellerIdFrom(args[1].toString());
			Seller seller = Market.sellers[index];
			
			Offer proposal = Offer.parseProposal(args[2].toString(), seller);
			Offer contract = Offer.parseProposal(args[3].toString(), seller);
					
			return un.unifies(evaluateSeller(proposal, contract, buyer, seller).getImpressionAsLiteral(), args[4]);		
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
	
	/*
	 * This method computes a reputation value for each criterion defined on the reputation model
	 * @param proposal Initial offer defined by seller
	 * @param contract Real offer considering respective penalties (like delivery delay and quality reductions)
	 * @param buyer appraiser
	 * @param seller evaluated
	 * @return A impression from buyer with respect to seller (rating)
	 */
	private Impression evaluateSeller(Offer proposal, Offer contract, Buyer buyer, Seller seller)
	{
		double priceDiscrepancy = contract.getProduct().getPrice() / proposal.getProduct().getPrice();
		double qualityDiscrepancy = proposal.getProduct().getQuality() / contract.getProduct().getQuality();
		double deliveryDiscrepancy = (double) contract.getProduct().getDeliveryTime() / proposal.getProduct().getDeliveryTime();
		
		Impression impression = new Impression(buyer, seller, System.currentTimeMillis());
		
		impression.setRatings(CriteriaType.PRICE.getValue(), getScore(priceDiscrepancy));
		impression.setRatings(CriteriaType.QUALITY.getValue(), getScore(qualityDiscrepancy));
		impression.setRatings(CriteriaType.DELIVERY.getValue(), getScore(deliveryDiscrepancy));
		
		return impression;
	}
	
	/*
	 * This method defines the score for each evaluation criterion
	 * The minimum score is 0 and the maximum score is 10.
	 * When variation is 1, it means that the contract hasn't been change, at least for the evaluated criterion.
	 * On the other hand, if variation is larger or equals to 2, it means the evaluated criterion has its value at least doubled.
	 * @param variation Normalized value that represents the discrepancy between the proposal and the contract
	 * @return rating value with respect to an evaluation criterion
	 */
	private double getScore (double variation)
	{	
		if(variation <= 1)
			return 1;					//maximum score
		else if(variation >= 2)
			return -1;						//minimum score
		else
			return -2 * variation + 3;	//intermediate score (based on line equation)
	}
}
