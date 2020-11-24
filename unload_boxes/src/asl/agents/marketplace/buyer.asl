/* MODULES *************/

{ include("src/asl/modules/basicModule.asl") }			// rules and plans for social protocols
{ include("src/asl/modules/contractingModule.asl") }	// rules and plans for contracting a service
{ include("src/asl/modules/socialModule.asl") }			// rules and plans for social evaluations

!start.

+!start: getMyName(Me)
	<-	scenario_Marketplace.actions.buyer.initialize(Me);
		!register(initiator);
.

/* BEHAVIOR  **********************/

/*
 * The buyer ends his activities
 */ 
+buy(nothing).

/*
 * This plan is executed every time that a seller delivered a product.
 * @param CNPId: id of the call
 * @param New_offer: new contract with new terms.
 */
+delivered(CNPId, New_offer)[source(Seller)]
	:	cnp_state(CNPId, contract) &
		proposal(CNPId, Old_offer) &
		task(CNPId, buy(Product)) &
		averagePrice(CNPId, AveragePrice) &
		getMyName(Me)
		
	<-	// Evaluating the seller
		-+cnp_state(CNPId, finished);
		scenario_Marketplace.actions.buyer.getRating(offer(Old_offer, Seller), New_offer, AveragePrice, rating(Price, Quality, Delivery));
		scenario_Marketplace.actions.buyer.getOtherBuyers(Me, Buyers);
		.print("[EVALUATION] {price:", Price , "; quality: ", Quality, "; delivery: ", Delivery, "}");
		!evaluateProvider(Seller, Product, ["PRICE", "QUALITY", "DELIVERY"], [Price, Quality, Delivery]);
		
		// Ending the purchase and cleaning the memory
		!spreadImage(Seller, Product, Buyers);
		purchase(finished);
		purchase(completed);
		!checkActivitiesStatus;
		!deleteAllPoposals(CNPId);
		!deleteAllRefuses(CNPId);
		-averagePrice(CNPId,_);
		-delivered(CNPId,_)[source(Seller)];
.

/*
 * The seller has the product registered in his stock, but the product it's not available right now.
 * @param CNPId: id of the call.
 * @param Product: product to be bought.
 */
+missing(CNPId, Product)[source(Seller)]
	<-	!initializeAvailability(Seller, Product);
		!requestHelp(Seller, Product);
		-missing(CNPId,_)[source(Seller)];
.

/*
 * The contract net protocol (CNP) is started
 * @param CNPId: id of the call.
 * @param Product: product to be bought.
 */
+task(CNPId, buy(Product))
	<-	.print("[NEW REQUEST]: CNPId: ", CNPId ,"; product: ", Product);
		+cnp_state(CNPId, propose);
		!call(CNPId, buy(Product), participant, Sellers);
		!bid(CNPId, Sellers);
		!contract(CNPId);
.

/**
 * Attempt of contracting a seller for a task.
 * Case, there are not any sellers available, the buyer ends the call.
 * @param CNPId: id of call.
 */
@b1[atomic]	
+!contract(CNPId)
	:	getMyName(Me) & 
		cnp_state(CNPId, propose) & 
		getReceivedOffers(CNPId, Offers) &
		task(CNPId, buy(Product))
	
	<-	if(Offers \== [])
		{
			!updateTrust(Offers);
			scenario_Marketplace.actions.buyer.evaluateOffers(Me, Offers, Winner);
			scenario_Marketplace.actions.buyer.getAveragePrice(Offers, AveragePrice);
			+averagePrice(CNPId, AveragePrice);
			
			if(Winner \== none)
			{
				.print("[PRODUCT: ", Product ,"] The best seller: ", Winner);
				-+cnp_state(CNPId, contract);
				!result(CNPId, Offers, Winner);
			}
			else
			{
				.print("[TRUSTLESS] It wasn't possible to find trustworthiness sellers, so I'm giving up the product: ", Product);
				-+cnp_state(CNPId, aborted);
				-averagePrice(CNPId,_);
				purchase(finished);
				!checkActivitiesStatus;
				!deleteAllPoposals(CNPId);
				!deleteAllRefuses(CNPId);
			}
		}
		else
		{
			.print("[NO OFFERS] It wasn't possible to find available workers, so I'm giving up the product: ", Product);
			-+cnp_state(CNPId, canceled);
			purchase(finished);
			!checkActivitiesStatus;
			!deleteAllRefuses(CNPId);
		}
.

/**
 * Update trust value for each seller that sent an offer to buyer.
 * @param Offers: list of received offers.
 */
+!updateTrust([offer(product(Product,_,_,_), Seller)|T]): getMyName(Me)
	<- 	?self_confident(Confident_profile);
		?urgency(Urgency);
        !initializeAvailability(Seller, Product);
        !requestHelp(Seller, Product);
        !helping(Seller, Product);
		!computeAvailability(Seller, Product, Availability);
		!checkTrust(Seller, Product, Availability, Confident_profile, Urgency);
		!saveData(Seller, Product);
		!updateTrust(T);
.

+!updateTrust([]).

/** 
 * Check if there is a trust belief for a seller.
 * If there is no a trust belief a new trust belief is created with value 0.5 
 */
+!checkTrust(Seller, Skill, Availability, Self_confident, Urgency)
	:	trust(Seller, Skill,_) &
		getMyImpressionsAbout(Impressions, Seller, Skill) &
		getThirdPartImages(Images, Seller, Skill) &
		getReferencesOf(Reference, Provider, Skill)
		
	<-	.length(Impressions, Own_imps);
		.length(Images, Other_imps);
		.length(Reference, References);
        scenario_Marketplace.actions.buyer.getFuzzyVariables(Urgency, Own_imps, Other_imps, Self_confident, References, EdgesValues);
      
		!computeTrust(Seller, Skill, Availability, EdgesValues, trust(_,_, Value));
		
		.print("[TRUST] Own_imps: ", Own_imps,": Other_imps, ", Other_imps, 
			", Self_confident: ", Self_confident, ", Urgency: ", Urgency, ", Availability: ", Availability, 
			", Edges: ", EdgesValues, ", trust: ", Value);
		
		-trust(Seller, Skill,_);
		+trust(Seller, Skill, Value);
.

+!checkTrust(Seller, Skill,_,_,_): not trust(Seller,Skill,_)
	<-	+trust(Seller, Skill, 0.5);
.

/*
 * Saving data for analysis
 */
+!saveData(Seller, Product)
	:	getMyName(Me) &
		getTrustOf(Trust, Seller, Product) &
		getReputationOf(Reputation, Seller, Product) &
		getMyImageAbout(Image, Seller, Product) &
		getReferencesOf(Knowhow, Seller, Product) &
		getAvailabilityOf(Availability, Seller, Product)
	
	<-	// Saving trust in file
		if(Trust \== [])
		{
			.nth(0, Trust, Vt);
			scenario_Marketplace.actions.generic.saveContent(Me, "TRUST", Vt);
		}
		
		// Saving reputation in file
		if(Reputation \== [])
		{
			.nth(0, Reputation, Vrep);
			scenario_Marketplace.actions.generic.saveContent(Me, "REPUTATION", Vrep);
		}
		
		// Saving image in file
		if(Image \== [])
		{
			.nth(0, Image, Vimg);
			scenario_Marketplace.actions.generic.saveContent(Me, "IMAGE", Vimg);
		}
		
		// Saving knowhow in file
		if(Knowhow \== [])
		{
			.nth(0, Knowhow, Vkw);
			scenario_Marketplace.actions.generic.saveContent(Me, "KNOWHOW", Vkw);
		}
		
		// Saving availability in file
		if(Availability \== [])
		{
			.nth(0, Availability, Vab);
			scenario_Marketplace.actions.generic.saveContent(Me, "AVAILABILITY", Vab);
		}
.

/*
 * Check if all buyers finished their shopping, in this case, the application ends
 */
@b2[atomic]
+!checkActivitiesStatus	<- status(end).

/*
 * The buyer shows his purchases data (the final report).
 */
+!showReport: getMyName(Me)
	<-	.count(cnp_state(CNPId, finished), Finished);
		.count(cnp_state(CNPId, aborted), Aborted);
		.count(cnp_state(CNPId, canceled), Canceled);
		    
		.print("[FINAL REPORT] Number of tasks: ", Finished + Canceled + Aborted, 
		       ", Finished tasks: ", Finished, 
		       ", Canceled tasks: ", Canceled,
		       ", Aborted tasks: ", Aborted);
		       
		// Saving data for analysis
		.concat("finished_sales:", Finished, ContentF);	 
		scenario_Marketplace.actions.generic.saveContent(Me, "AGENT", ContentF);
		 
		.concat("canceled_sales:", Canceled, ContentC);	 
		scenario_Marketplace.actions.generic.saveContent(Me, "AGENT", ContentC);
		
		.concat("aborted_sales:", Aborted, ContentA);	 
		scenario_Marketplace.actions.generic.saveContent(Me, "AGENT", ContentA);
//		!saveData(Seller, Product);
.