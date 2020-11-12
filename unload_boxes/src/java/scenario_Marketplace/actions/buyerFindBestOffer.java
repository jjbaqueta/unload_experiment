package scenario_Marketplace.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import entities.model.Offer;
import entities.model.buyers.Buyer;
import entities.model.sellers.Seller;
import entities.services.MarketFacade;
import enums.CriteriaType;
import environments.Experiment;
import environments.Files;
import environments.Market;
import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;
import reputationModels.Image;
import reputationModels.Rating;
import reputationModels.ReGret;
import reputationModels.Reputation;
import reputationModels.Util;

/*
 * This class implements an action of an agent of type buyer
 */
public class buyerFindBestOffer extends DefaultInternalAction
{
	private static final long serialVersionUID = 1L;
	
	private final double REP_CUT_SCORE = 0.8;
	private final double IMG_CUT_SCORE = 0.6;
	
	private String buyerName;
	
	/*
	 * This method is used by buyer to decide what is the best offer among all offers sent to him 
	 * From the reputation and preferences informations the buyer is able to decide which offer accepts or rejects
	 * The informations about offers are passed from the array args:
	 * - args[0]: buyer's name
	 * - args[1]: list of offers (every offers received by buyer)
	 * - args[2]: list of reputations (it may contain the reputation of each seller that sent a proposal)
	 * - args[3]: list of images (it may contain the image of each seller)
	 * - args[4]: Return the name of seller who received the best evaluation score, considering his reputation and the buyer's preferences 
	 */
	
	@Override
	public Object execute( TransitionSystem ts, Unifier un, Term[] args ) throws JasonException 
	{
		try
		{	
			/* 
			 * ************************************
			 * Get the index from buyer : args[0]
			 * ************************************
			 */
			
			buyerName = args[0].toString();
			int index = MarketFacade.getBuyerIdFrom(args[0].toString());
			Buyer buyer = Market.buyers[index];
			
			/* 
			 * ************************************
			 * Parsing the list of OFFERS : args[1]
			 * ************************************
			 */
			
			List<Offer> offers = new ArrayList<Offer>();
			ListTerm offerTermList = (ListTerm) args[1];
			
			if(!offerTermList.isEmpty())
			{				
				for(Term t : offerTermList)
					offers.add(Offer.parseOffer(t.toString()));
			}
			else	
				return un.unifies(new Atom("none"), args[4]);
			
			/* 
			 * ************************************
			 * Parsing the list of REPUTATIONS : args[2]
			 * ************************************
			 */
			
			ListTerm repTermList = (ListTerm) args[2];
			List<Reputation> reputations = new ArrayList<Reputation>();
			
			if(!repTermList.isEmpty())
			{
				for(Term t : repTermList)
					reputations.add(Reputation.parseReputation(t.toString()));
			}
			
			/* 
			 * ************************************
			 * Parsing the list of IMAGES : args[3]
			 * ************************************
			 */	
			ListTerm imageTermList = (ListTerm) args[3];
			List<Image> images = new ArrayList<Image>();
			
			if(!imageTermList.isEmpty())
			{
				for(Term t : imageTermList)
					images.add(Image.parseImage(t.toString()));
			}

			/* 
			 * ***********************************************
			 * Computing who is the best seller and return him
			 * ***********************************************
			 */
			Seller bestSeller = computeBestOffer(offers, reputations, images, buyer.getPreferenceByPrice(), buyer.getPreferenceByQuality(), buyer.getPreferenceByDelivery()).getSeller();

			if(bestSeller == null)
				return un.unifies(new Atom("none"), args[4]);
			else
				return un.unifies(new Atom(bestSeller.getName()), args[4]);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			throw new JasonException("The internal action must receive four arguments!");
		}
		catch (ClassCastException e) {
			throw new JasonException("received arguments are out of format");
		}
		catch(Exception e)
		{
			throw new JasonException(e.getMessage());
		}
	}
	
	/*
	 * This method computes the best offer received by buyer
	 * The computation is done from a cost function which is adjusted considering the trade-off among the buyer's preferences
	 * @param offers List containing every offers received
	 * @param priceWeight Relevance of price factor on the best offer computation, 0.0 minimum, 1.0 maximum 
	 * @param qualityWeight Relevance of quality factor on the best offer computation, 0.0 minimum, 1.0 maximum
	 * @param timeWeight Relevance of delivery factor on the best offer computation, 0.0 minimum, 1.0 maximum
	 * @return name of the best seller, if exist the best offer, otherwise returns null
	 */
	private Offer computeBestOffer(List<Offer> offers, List<Reputation> reputations, List<Image> images, double priceWeight, double qualityWeight, double deliveryWeight)
	{	
		if(!reputations.isEmpty())
			filterByReputation(offers, reputations, priceWeight, qualityWeight, deliveryWeight);
		
		if(!images.isEmpty())
			filterByImage(offers, images, priceWeight, qualityWeight, deliveryWeight);
		
		if(Experiment.ReputationFilter && Experiment.ImageFilter)
			offers.removeIf(offer -> offer.isAcceptByReputation() == false && offer.isAcceptByImage() == false);
		
		else if(!Experiment.ReputationFilter && Experiment.ImageFilter)
			offers.removeIf(offer -> offer.isAcceptByImage() == false);
		
		else if(Experiment.ReputationFilter && !Experiment.ImageFilter)
			offers.removeIf(offer -> offer.isAcceptByReputation() == false);
		
		// If there are not offers (all seller were removed due to their low reputation)
		if(offers.isEmpty())
			return null;
		
		// Getting optimal values for each criteria
		double minPrice = Collections.min(offers, (offer1, offer2) -> offer1.getProduct().getPrice().compareTo(offer2.getProduct().getPrice())).getProduct().getPrice();
		double maxQuality = Collections.max(offers, (offer1, offer2) -> offer1.getProduct().getQuality().compareTo(offer2.getProduct().getQuality())).getProduct().getQuality();
		int minTime = Collections.min(offers, (offer1, offer2) -> offer1.getProduct().getDeliveryTime().compareTo(offer2.getProduct().getDeliveryTime())).getProduct().getDeliveryTime();
		
		// Rating array
		double[] ratings = new double[offers.size()]; 
		Arrays.fill(ratings, 0);
		
		// Normalization and computation of the ratings (based on weighted average)
		for(int i = 0; i < offers.size(); i++)
		{
			ratings[i] += (offers.get(i).getProduct().getPrice() / minPrice) * (priceWeight);
			ratings[i] -= (offers.get(i).getProduct().getQuality() / maxQuality) * (qualityWeight);
			ratings[i] += ((double) offers.get(i).getProduct().getDeliveryTime() / minTime) * (deliveryWeight);
			ratings[i] /= 3;
		}
		
		// Finding the best offer
		int minIdx = IntStream.range(0, ratings.length)
				.reduce((i, j) -> ratings[i] > ratings[j] ? j : i)
				.getAsInt();
		
		return offers.get(minIdx);
	}
	
	private void filterByReputation(List<Offer> offers, List<Reputation> reputations, double priceWeight, double qualityWeight, double deliveryWeight)
	{	
		for(Offer offer: offers)
		{
			for(Reputation rep : reputations)
			{
				if(rep.getAgent().getName().equals(offer.getSeller().getName()))
				{
					double repPrice = Util.convertToNormalizedInterval(rep.getReputationRatings().get(CriteriaType.PRICE.getValue())) * 
					 		  		  Util.convertToNormalizedInterval(rep.getReliabilityRatings().get(CriteriaType.PRICE.getValue()));
					 		  
					double repQuality = Util.convertToNormalizedInterval(rep.getReputationRatings().get(CriteriaType.QUALITY.getValue())) * 
									 	Util.convertToNormalizedInterval(rep.getReliabilityRatings().get(CriteriaType.QUALITY.getValue()));
			
					double repDelivery = Util.convertToNormalizedInterval(rep.getReputationRatings().get(CriteriaType.DELIVERY.getValue())) * 
								 	 	 Util.convertToNormalizedInterval(rep.getReliabilityRatings().get(CriteriaType.DELIVERY.getValue()));
			
					offer.setAcceptByReputation(Util.isAcceptableScore(REP_CUT_SCORE, repPrice, repQuality, repDelivery, priceWeight, qualityWeight, deliveryWeight));
				}
			}
		}
	}
	
	private void filterByImage(List<Offer> offers, List<Image> images, double priceWeight, double qualityWeight, double deliveryWeight)
	{
		for(Offer offer: offers)
		{
			List<Rating> ratings = Util.getImagesFrom(offer.getSeller(), images);
			
			if(!ratings.isEmpty())
			{
				double[] ratting = ReGret.computeSubjectiveReputation(System.currentTimeMillis(), ratings);
				
				double pImage = Util.convertToNormalizedInterval(ratting[0]);
				double qImage = Util.convertToNormalizedInterval(ratting[1]);
				double dImage = Util.convertToNormalizedInterval(ratting[2]);
				
				Files.writeImgStatus(buyerName, offer.getSeller().getName(), offer.getProduct().getName(), pImage, qImage, dImage);
				
				offer.setAcceptByImage(Util.isAcceptableScore(IMG_CUT_SCORE, pImage, qImage, dImage, priceWeight, qualityWeight, deliveryWeight));	
			}
		}
	}
}