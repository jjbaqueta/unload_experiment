/* MODULES *************/

{ include("src/asl/modules/basicModule.asl") }			// rules and plans for social protocols
{ include("src/asl/modules/contractingModule.asl") }	// rules and plans for contracting a service
{ include("src/asl/modules/socialModule.asl") }			// rules and plans for social evaluations

request_counter(0).
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
 * @param New_offer: the new contract with new terms.
 * @param Old_offer: the old contract.
 */
+delivered(CNPId, New_offer, Old_offer)[source(Seller)]
	:	cnp_state(CNPId, contract) &
		task(CNPId, buy(Product)) &
		getMyName(Me)
		
	<-	// Evaluating the seller
		-+cnp_state(CNPId, finished);
		scenario_Marketplace.actions.buyer.getRating(Old_offer, New_offer, rating(Price, Quality, Delivery));
		scenario_Marketplace.actions.buyer.getOtherBuyers(Me, Buyers);
		.print("[EVALUATION] {price:", Price , "; quality: ", Quality, "; delivery: ", Delivery, "}");
		!evaluateProvider(Seller, Product, ["PRICE", "QUALITY", "DELIVERY"], [Price, Quality, Delivery]);
		
		// Ending the purchase and cleaning the memory
		!spreadImage(Seller, Product, Buyers);
		purchase(finished);
		!checkActivitiesStatus;
		!deleteAllPoposals(CNPId);
		!deleteAllRefuses(CNPId);
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
	<-	+cnp_state(CNPId, propose);
		?request_counter(Requisitions)
		Req = Requisitions + 1;
		-+request_counter(Req);
		!call(CNPId, buy(Product), participant, Sellers);
		!bid(CNPId, Sellers);
		!contract(CNPId);
.

/*
 * Based on the offers sent, it generates a list of the trust degree of each provider.
 * @param Skill: role played by the agents
 * @param Offers: list of offers
 * @return List: Old trust value from the agents.
 */
+!getTrustList(Skill, [offer(_, Agent)|T], List): getTrustOf(Trust, Agent, Skill)
	<-	
		!getTrustList(Skill, T, List1);
		.concat(Trust, List1, List);
.

+!getTrustList(_, [], List)
	<-	List = [].

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
			!getTrustList(Product, Offers, Trust_list);
			scenario_Marketplace.actions.buyer.evaluateOffers(Me, Offers, Trust_list, Winner);
			
			.print("-------------------- [WINNER]: ", Winner);
			
			if(Winner \== none)
			{
				-+cnp_state(CNPId, contract);
				!result(CNPId, Offers, Winner);
			}
			else
			{
				.print("[TRUSTLESS] It wasn't possible to find trustworthiness sellers, so I'm giving up the product: ", Product);
				-+cnp_state(CNPId, aborted);
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
			!keepState(CNPId, Product)
			!checkActivitiesStatus;
			!deleteAllRefuses(CNPId);
		}
.

/**
 * Save the current state of a seller when no one offer was done.
 * @CNPId: id of the call
 * @Product: product requested for the buyer.
 */
+!keepState(CNPId, Product)
	<-	.findall(Seller, refuse(CNPId)[source(Seller)], Sellers);
		!saveState(Sellers, Product)		
.

+!saveState([Seller|T], Product)
	<-	!saveData(Seller, Product);
		!saveState(T, Product);
.

+!saveState([],_).

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
	:	trust(Seller,Skill,_) &
		getMyImpressionsAbout(My_impressions, Seller, Skill) &
		getThirdPartImages(Other_images, Seller, Skill) &
		getMyImageAbout(My_image, Seller, Skill) &
		getReputationOf(Seller_reputation, Seller, Skill) &
		getReferencesOf(Seller_reference, Seller, Skill) &
		getMyName(Me)
		
	<-	.length(My_impressions, Own_imps);
		.length(Other_images, Other_imps);
		.length(Seller_reference, Num_references);
		
        scenario_Marketplace.actions.buyer.getFuzzyVariables(Me, Urgency, Own_imps, Other_imps, Self_confident, Num_references, EdgesValues);
		scenario_Marketplace.actions.buyer.applyPreferences(Me, My_image, Seller_reputation, Seller_reference, tuple(Image, Reputation, Knowhow));
      
		!computeTrust(Seller, Skill, Image, Reputation, Knowhow, Availability, EdgesValues, trust(_,_, Value));
				
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
	
	<-	?request_counter(Req)
	
		// Saving trust in file
		if(Trust \== [])
		{
			.nth(0, Trust, Vt);
			scenario_Marketplace.actions.generic.saveContent(Me, "TRUST", Vt, Req);
		}
		
		// Saving reputation in file
		if(Reputation \== [])
		{
			.nth(0, Reputation, Vrep);
			scenario_Marketplace.actions.generic.saveContent(Me, "REPUTATION", Vrep, Req);
		}
		
		// Saving image in file
		if(Image \== [])
		{
			.nth(0, Image, Vimg);
			scenario_Marketplace.actions.generic.saveContent(Me, "IMAGE", Vimg, Req);
		}
		
		// Saving knowhow in file
		if(Knowhow \== [])
		{
			.nth(0, Knowhow, Vkw);
			scenario_Marketplace.actions.generic.saveContent(Me, "KNOWHOW", Vkw, Req);
		}
		
		// Saving availability in file
		if(Availability \== [])
		{
			.nth(0, Availability, Vab);
			scenario_Marketplace.actions.generic.saveContent(Me, "AVAILABILITY", Vab, Req);
		}
.

/*
 * Check if all buyers finished their shopping, in this case, the application ends
 */
@b3[atomic]
+!checkActivitiesStatus	
	<- status(end)
.

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
		scenario_Marketplace.actions.generic.saveContent(Me, "AGENT", ContentF, -1);
		 
		.concat("canceled_sales:", Canceled, ContentC);	 
		scenario_Marketplace.actions.generic.saveContent(Me, "AGENT", ContentC, -1);
		
		.concat("aborted_sales:", Aborted, ContentA);	 
		scenario_Marketplace.actions.generic.saveContent(Me, "AGENT", ContentA, -1);
.

/**
 * Announce who won the calling for all participants
 * @param CNPId: id of required service
 * @param Loffers: list of received offers
 * @param Wagent: the winner agent 
 */	
// Announce result to the winner
+!result(CNPId, [offer(_, Agent)|T], Wagent): Agent == Wagent & request_counter(Requisition)
	<-	.send(Agent, tell, accept_proposal(CNPId, Requisition));
      	!result(CNPId, T, Wagent).

// Announce to others      
+!result(CNPId,[offer(_, Agent)|T], Wagent): Agent \== Wagent
	<-	.send(Agent, tell, reject_proposal(CNPId));
      	!result(CNPId, T, Wagent).

// Case the list of offers is empty      
+!result(_,[],_).