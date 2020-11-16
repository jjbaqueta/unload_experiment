/* MODULES *************/

{ include("src/asl/modules/basicModule.asl") }		// rules and plans for social protocols
{ include("src/asl/modules/providingModule.asl") }	// rules and plans for providing a service
{ include("src/asl/modules/socialModule.asl") }		// rules and plans for social evaluations

/* RULES *************/ 

/**
 * This rule check if the seller sell a certain product.
 * @param Product: the product to be found. 
 * @return Products: list of found products.
 */
find_product(Product, Products)
	:-	.findall(product(Product, Price, Quality, Delivery), 
				 product(Product, Price, Quality, Delivery), 
				 Products)
.

!start.

+!start: getMyName(Me)
	<-	scenario_Marketplace.actions.seller.initialize(Me); 
		!register(participant, initiator)
.

/* BEHAVIOR  **********************/

/**
 * Answering the call for proposal.
 * @param CNPId: id of the call.
 * @param Product: product requested by the buyer.
 */ 
+cfp(CNPId, buy(Product))[source(Buyer)]
	:	provider(Buyer, "initiator") & 
		find_product(Product, Products) &
		getMyName(Me)
			
	<-	if(Products \== [])
		{
			.nth(0, Products, Offer);
	      	+my_proposal(CNPId, offer(Offer, Me));	      	
			.print("I have the product ", Product, ". Sending my proposal: ", Offer);
	      	.send(Buyer, tell, proposal(CNPId, offer(Offer, Me)));
	    }
	    else
	    {
	    	.print("I don't have the product ", Product, ". I refused the call.");
	    	.send(Buyer, tell, refuse(CNPId));
	    }
	    -cfp(CNPId,_)[source(Buyer)];
.

/*
 * The offer sent by seller was accepted by a buyer
 */
@s1[atomic]
+accept_proposal(CNPId)[source(Buyer)]:	my_proposal(CNPId,_)
	<-	.print("I won CNP, starting the delivery process: ", CNPId);
		made(sale);
		!delivery(CNPId, Buyer);
		-accept_proposal(CNPId)[source(Buyer)];
.

/*
 * The offer sent by seller was rejected by a buyer.
 */
@s2[atomic]
+reject_proposal(CNPId)[source(Buyer)]
	<-	lost(sale);
		-my_proposal(CNPId,_);
		-reject_proposal(CNPId)[source(Buyer)];
.

/*
 * Recalculate the contract terms (this process depends on seller's type)
 * @param CNPId: id of the call.
 * @param Buyer: buyer that bought a product.
 */
+!delivery(CNPId, Buyer)
	:	my_proposal(CNPId, offer(Offer,_)) & getMyName(Me)
	
	<-	scenario_Marketplace.actions.seller.getNewContract(Me, Old_offer, New_offer);
		.send(Buyer, tell, delivered(CNPId, New_offer));
		.print("[REPORT]: old offer: ", Offer, 
			", new offer: ", New_offer);
.