package scenario_Marketplace.actions;

import entities.model.Offer;
import entities.model.sellers.Seller;
import entities.services.MarketFacade;
import environments.Market;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;

/*
 * This class implements an action of an agent of type seller
 */
public class sellerDefineDeliveryConditions extends DefaultInternalAction
{
	private static final long serialVersionUID = 1L;

	/*
	 * This method may change the initial conditions of a contract defined between a seller and a buyer according to seller's type
	 * The contract informations are passed from the array args:
	 * - args[0]: seller's name
	 * - args[1]: proposal used to define an agreement between the seller and buyer (initial contract)
	 * - args[2]: task's CNPId
	 * - args[3]: loyalty level from customer
	 * - args[4]: new contract which may be adjusted according seller's type (return)
	 */	
	@Override
	public Object execute( TransitionSystem ts, Unifier un, Term[] args ) throws JasonException 
	{	
		try
		{	
			// Get the index from seller
			int index = MarketFacade.getSellerIdFrom(args[0].toString());
			Seller seller = Market.sellers[index];
			
			// Parsing the offer received
			Offer offer = Offer.parseProposal(args[1].toString(), seller);
			
			// Get the cnpid
			offer.setCnpid(Integer.parseInt(args[2].toString()));
			
			// Computing new contract conditions
			Offer newContract = seller.recalculateContractConditions(offer.getProduct().getName(),
																	 Market.numberBuyingRequest - 1,
																	 offer.getCnpid());
			
			if(newContract == null)
			{
				System.out.println("PRODUCT DOESN'T HAVE SALES BEHAVIOR : PRODUCT - " + offer.getProduct());
				return un.unifies(Literal.parseLiteral("none"), args[4]);	
			}
			
			// Computing discounts
			NumberTerm loyalty = (NumberTerm) args[3];
			double discount = computeDiscountFrom((int) loyalty.solve());
			
			// Applying discounts
			newContract.getProduct().setPrice(newContract.getProduct().getPrice() - newContract.getProduct().getPrice() * discount);
			newContract.getProduct().setQuality(newContract.getProduct().getQuality() + newContract.getProduct().getQuality() * discount);
			newContract.getProduct().setDeliveryTime((int) (newContract.getProduct().getDeliveryTime() - newContract.getProduct().getDeliveryTime() * discount));
			
			//Returns the result as Term
			return un.unifies(newContract.getProduct().getProductAsLiteral(), args[4]);		
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
	 * This methods computes the discount offered to customer according to his loyalty level
	 * @param loyaltyLevel A integer value that represents the loyalty level from costumer
	 * @return value that is going to discounted from product properties (like price, quality or/and delivery)
	 */
	private double computeDiscountFrom(int loyaltyLevel)
	{
		if (loyaltyLevel <= 0)
			return 0.0;
		
		else if(loyaltyLevel > 100)
			return 0.2;
		
		else
			return (loyaltyLevel * 0.2)/100;
					
	}
}