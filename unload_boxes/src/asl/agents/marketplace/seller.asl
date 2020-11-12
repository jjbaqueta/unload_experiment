/* Rules */

format_products_on_list(Products) 
	:-	.findall(p(Name, Price, Quality, Delivery), sell(Name, Price, Quality, Delivery), Products).

find_product_by_name(P_name, Products)
	:-	.findall(p(P_name, Price, Quality, Delivery), sell(P_name, Price, Quality, Delivery), Products) & 
		Products \== [].

//customer_loyalty_level(Buyer, Level)
//	:-	

/* Goals */

!register.

/* Plans ***************************************************/

/*
 * The seller is register as participant
 * Each seller will be associated to initiators
 */ 
+!register
	:	format_products_on_list(Products) 
	<-	.df_register("participant");
    	.df_subscribe("initiator").

/*
 * Answering a call for proposal
 */ 
+cfp(CNPId, P_name)[source(Ag)]
	:	provider(Ag, "initiator") & 
		find_product_by_name(P_name, Products)
   <-	.nth(0, Products, Offer);
   		+proposal(CNPId, P_name, Offer); 							// Remember offer
      	.send(Ag, tell, proposal(CNPId, Offer)).

/*
 * The offer from seller was accepted by a buyer (enter in delivery state)
 * This plan needs to be atomic to not happen problem of writing in file
 */
@d1[atomic]
+accept_proposal(CNPId)[source(Ag)]
	:	proposal(CNPId, P_name, Offer)
	<-	.print("I won CNP, starting the delivery process for request: ", CNPId);
		made(sale);
		!delivery(CNPId, Ag).

/*
 * The offer from seller was rejected by a buyer
 */
@d2 +reject_proposal(CNPId)[source(Ag)]
	<-	lost(sale);
		!clear_memory(CNPId).

/*
 * The seller updates the own reputation considering a new reputation received from some buyer
 */
+rep(_,T1,_,_,_,_,_,_)[source(Ag)]
	:	rep(_,T2,_,_,_,_,_,_)[source(Ag)] & T1 > T2
	<-	-rep(_,T2,_,_,_,_,_,_)[source(Ag)].

/*
 * Recompute the contract terms (it depends on seller's type)
 */
+!delivery(CNPId, Buyer)
	:	proposal(CNPId, P_name, Offer)
	<-	!update_relationship(Buyer, Loyalty_level);
		.my_name(N);
		actions.sellerDefineDeliveryConditions(N, Offer, CNPId, Loyalty_level, NewOffer);
		.send(Buyer, tell, delivered(CNPId, NewOffer));		
		!clear_memory(CNPId).

/*
 * This plan updates the relationship level between a buyer and a seller
 */
+!update_relationship(Buyer, Loyalty_level)
	:	friend(Buyer, Likes)
	<-	Count = Likes + 1;
		-friend(Buyer, Likes);
		+friend(Buyer, Count);
		Loyalty_level = Count.

/*
 * Represents the situation where the seller and buyer are not friend yet
 */
+!update_relationship(Buyer, Loyalty_level)
	:	not friend(Buyer, _)
	<-	+friend(Buyer, 1);
		Loyalty_level = 1.

/*
 * Remove all informations about a given purchase request
 */
+!clear_memory(CNPId)
	<-	-accept_proposal(CNPId)[source(_)];
		-reject_proposal(CNPId)[source(_)];
		-cfp(CNPId,_)[source(_)];
		-proposal(CNPId,_,_)[source(_)].