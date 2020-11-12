/* Rules */

update_nb_offers(CNPId)
	:- 	nb_participants(CNPId, NP) &				// Retrieves the number of participants for CNPId
		.count(proposal(CNPId,_)[source(_)], NO) &	// Counts the number of proposals received until now 
		NP = NO.									// Updates the amount participants considering only whose sent a proposal

check_impressions(Ag, Impressions)
	:-	.findall(imp(Buyer, Ag, Time, Rating), imp(Buyer, Ag, Time, Rating), Impressions).
	
//check_image(Ag, Impressions)
//	:-	.findall(image(Buyer, Ag, Time, Rating), image(Ag, _, Time, Rating), Impressions).

/* Goals */

!register.

/* Plans ***************************************************/

/*
 * The buyer is register as initiator 
 */
+!register 
	<- .df_register(initiator).

/*
 * The buyer ends his activities
 */ 
+buy(nothing).

/*
 * The buyer starts the Contract Net Protocol (CNP)
 */
+buy(Id, P_name)
	<-	.print("PURCHASE REQUEST: ", Id, ", PRODUCT: ", P_name);	// Waiting 2 seconds for participants
		.print("Waiting for participants ...");
      	.wait(2000); 
      
      	.df_search("participant", Sellers);							// Loads a list of possible participants (Sellers)
      	+nb_participants(Id, .length(Sellers));						// Retrieves the amount of participants
      	+cnp_state(Id, propose); 									// Updates the status of the CNP (protocol's state)
      	.send(Sellers, tell, cfp(Id, P_name));						// Sends a call for proposal (CFP)
      	.print("Sending the calls for proposal to: ", Sellers);
      	
      	.wait(update_nb_offers(Id), 4000, _);						// Waiting 4 seconds for proposals
      	!contract(Id, P_name).										// Entering in contract phase

/*
 * This plan is executed every time that a seller delivery a given product
 */
+delivered(CNPId, NewOffer)[source(Ag)]
	:	cnp_state(CNPId, contract)
	<-	!create_impression(CNPId, NewOffer, Ag);
		-cnp_state(CNPId,_);
		+cnp_state(CNPId, finished);
		purchase(finished);
		purchase(completed);
		!check_activities_status.

/*
 * The buyer evaluates all proposal received and chooses the best seller
 * This plan needs to be atomic to not accept proposals while contracting
 */
@cont1[atomic]
+!contract(CNPId, P_name)
 	:	cnp_state(CNPId, propose)									// Checks if the state of CNP is in propose
	<-	-cnp_state(CNPId,_);										// Update the state of CNP, from propose to contract
      	+cnp_state(CNPId, contract);
      	
      	// Retrieve all proposals sent and as well the reputation from sellers  
      	.findall(offer(Offer, Ag), proposal(CNPId, Offer)[source(Ag)], Offers);
      	.length(Offers, N_offers)
      	.print("Number of offers received: ", N_offers); 
      	
      	Offers \== [];												// CONSTRAINT: If exist at least one offer, the plan continues
      	
      	// Update the reputation from sellers 
      	!compute_reputation(Offers);
      	
      	.findall(rep(Ag, Time, Rprice, Rquality, Rdelivery, Lprice, Lquality, Ldelivery),
      			 rep(Ag, Time, Rprice, Rquality, Rdelivery, Lprice, Lquality, Ldelivery), Reputations);
      			 
      	// Retrieve images of every seller with respect a given product (P_name)
      	.findall(image(Seller, P_name, Time, Rating), image(Seller, P_name, Time, Rating), Images);
      	
      	/*
      	 * TAKE DECISION:
      	 * Find the best seller among those that send a proposal 
      	 * The buyer's preferences and the seller's reputation are considered in this decision 
      	 */  
      	.my_name(N);
      	actions.buyerFindBestOffer(N, Offers, Reputations, Images, Ag_winner);
      	
      	Ag_winner \== none;											// CONSTRAINT: If exist a winner, the plan continues
      	
      	.print("The best offer came from: ", Ag_winner);
      	.print("Notifying participants about decision ...");
		+winner(CNPId, Ag_winner);
      	!announce_result(CNPId, Offers, Ag_winner).

/*
 * Nothing to do, the current state of CNP is not 'propose'
 */
@cont2 +!contract(_,_).

/*
 * The execution of the contract (plan: @cont1) has failed
 */
@cont3 -!contract(CNPId, P_name)
	<-	.print("**** WARNING **** - CNP ",CNPId," has failed! - There were not proposals for the purchase request: ", CNPId);
		-cnp_state(CNPId,_);
		+cnp_state(CNPId, finished);									// Update the state of CNP to finished
		purchase(aborted);												// Set the current purchase request as aborted
		purchase(finished);												// End the current purchase request
		!clear_memory(CNPId);											// Delete informations about the request from agent's memory
		!check_activities_status.										// Check if there are more shopping to do

/*
 * This plan compute the reputation of each agent that sent a offer
 * The computation is base on impressions that the buyer has about the seller
 * If the buyer has already a reputation stored about the seller, this reputation is updated
 * Every the a new reputation is computed, such a reputation is send to the respective seller
 */
+!compute_reputation([offer(Offer, Ag)|T])
	:	check_impressions(Ag, Impressions) & Impressions \== []
	<-	actions.buyerCalculateReputation(Impressions, Reputation_profile);
		.send(Ag, tell, Reputation_profile);
		actions.sellerSaveReputation(Reputation_profile, R);
		!update_reputation(Reputation_profile);
		!compute_reputation(T).

/*
 * There are not impressions about current seller, go to the next
 */
@cmpR2 +!compute_reputation([offer(Offer, Ag)|T])
	:	check_impressions(Ag, Impressions) & Impressions == []
	<-	!compute_reputation(T).

/*
 * Nothing to do, end of list of offers
 */
@cmpR3 +!compute_reputation([]).

/*
 * The reputation computation (plan: @cmpR1) has failed
 */
@cmpR4 -!compute_reputation([offer(Offer, Ag)|T])
	<- .print("**** ERROR **** - Inner operation has presented error to compute the reputation of the agent:", Ag).

/*
 * Update the reputation of a seller
 */
+!update_reputation(rep(Seller, Time, Rp, Rq, Rd, Lp, Lq, Ld))
	<-	-rep(Seller,_,_,_,_,_,_,_);
		+rep(Seller, Time, Rp, Rq, Rd, Lp, Lq, Ld).

/*
 * Announce the winner to sellers: notifying the winner
 */
@ann1 +!announce_result(CNPId,[offer(_, Ag)|T], Ag_winner)
	:	Ag == Ag_winner
	<-	.send(Ag, tell, accept_proposal(CNPId));
		!announce_result(CNPId, T, Ag_winner).
      
/*
 * Announce the winner to sellers: notifying the losers
 */
@ann2 +!announce_result(CNPId,[offer(_, Ag)|T], Ag_winner)
	:	Ag \== Ag_winner
	<-	.send(Ag, tell, reject_proposal(CNPId));
		!announce_result(CNPId, T, Ag_winner).

/*
 * Nothing to do, end of list of sellers
 */
@ann3 +!announce_result(_,[],_).

/*
 * Check if all buyers finish their shopping, in this case, the application ends
 */
@a3[atomic]
+!check_activities_status
	<-	check_status(end).

/*
 * In this plan the buyer evaluates the seller with respect to contract conditions (commitment with offer)
 * According to seller type, the conditions defined in an initial offer may be change. The seller may redefine the terms of contract
 * This way, the buyer produces an impression about the seller's behavior. This impression is associated to trust level from seller.
 * An impression may be spread on society besides to be stored by agent
 */
+!create_impression(CNPId, NewOffer, Seller)
	:	proposal(CNPId, Offer)[source(Seller)]
	<-	.my_name(N);
		actions.buyerGenerateImpression(N, Seller, Offer, NewOffer, Rating);
		!update_image(Seller, NewOffer, Rating);
		.df_search("initiator", Buyers);										// Find buyers of a specific group (in this case, initiators)
      	.send(Buyers, tell, Rating);											// Spread the impression in the group of buyers 
		!clear_memory(CNPId).

/*
 * This plane updates the image of a given seller
 */	
+!update_image(Seller, p(Name,_,_,_), imp(_,_, Time, Rating_list))
	<- +image(Seller, Name, Time, Rating_list).

/*
 * Remove all informations about a given purchase request
 */
+!clear_memory(CNPId)
	<-	-winner(CNPId,_);
		-nb_participants(CNPId,_);
		-cnp_state(CNPId,_);
		-delivered(CNPId,_)[source(_)];
		-proposal(CNPId,_)[source(_)].