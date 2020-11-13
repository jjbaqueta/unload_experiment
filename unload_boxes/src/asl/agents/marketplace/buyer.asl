/* MODULES *************/

{ include("src/asl/modules/basicModule.asl") }		// rules and plans for social protocols
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
		proposal(CNPId, offer(Old_offer, Seller)) &
		task(CNPId, buy(Product)) &
		getMyName(Me)
		
	<-	-+cnp_state(CNPId, finished);
		.df_search(initiator, Buyers);
		scenario_Marketplace.actions.buyer.evaluation(Old_offer, New_offer, rating(Price, Quality, Time));
		scenario_Marketplace.actions.buyer.getOtherBuyers(Me, Buyers);
		!evaluateProvider(Seller, Product, ["PRICE", "QUALITY", "TIME"], [Price, Quality, Time]);			
		!spreadImage(Seller, Product, Buyers);
		purchase(finished);
		purchase(completed);
		!checkActivitiesStatus;
		-delivered(CNPId,_)[source(Seller)];
.

/*
 * The contract net protocol (CNP) is started
 * @param CNPId: id of the call.
 * @param Product: product to be bought.
 */
+task(CNPId, buy(Product))
	<-	.print("[NEW REQUEST]: CNPId: ", CNPId ,"; product: ", Product);
		+cnp_state(CNPId, propose);
		!call(CNPId, Task, participant, Sellers);
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
			
			if(Winner \== none)
			{
				.print("[PRODUCT: ", Product ,"] Best seller", Winner);
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
			!checkActivitiesStatus;
			!deleteAllRefuses(CNPId);
		}
.

/**
 * Update trust value for each seller that sent an offer to buyer.
 * @param Offers: list of received offers.
 */
+!updateTrust([offer(product(Product,_,_,_), Seller)|T]): getMyName(Me)
	<- 	?task_urgency(Task_urgency);
        ?self_confident(Confident_profile);
		!computeAvailability(Seller, Product, Availability);
		!checkTrust(Seller, Product, Availability, Task_urgency, Confident_profile);
		!updateTrust(T);
.

+!updateTrust([]).

/*
 * Check if all buyers finished their shopping, in this case, the application ends
 */
@b2[atomic]
+!checkActivitiesStatus	<- status(end).

/*
 * The buyer shows his purchases data (the final report).
 */
+!showReport
	<-	.count(cnp_state(CNPId, finished), Finished);
		.count(cnp_state(CNPId, aborted), Aborted);
		.count(cnp_state(CNPId, canceled), Canceled);
		    
		.print("[FINAL REPORT] Number of tasks: ", Finished + Canceled + Aborted, 
		       ", Finished tasks: ", Finished, 
		       ", Canceled tasks: ", Canceled,
		       ", Aborted tasks: ", Aborted);
.