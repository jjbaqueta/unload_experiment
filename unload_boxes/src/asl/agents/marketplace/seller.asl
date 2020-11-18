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
			!withdrawProduct(Product, Status);
			
			if(Status == done)
			{
				.nth(0, Products, Offer);
		      	+my_proposal(CNPId, Offer);	      	
				.print("I have the product ", Product, ". Sending my proposal: ", Offer);
		      	.send(Buyer, tell, proposal(CNPId, Offer));
		      	!sendMyknowHow(Product, Buyer);		
			}
			else
			{
				.print("Missing product ", Product, ". I refused the call.");
	    		.send(Buyer, tell, refuse(CNPId));
	    		.send(Buyer, tell, missing(CNPId, Product));
	    		lost(sale);
			}
	    }
	    else
	    {
	    	.print("I don't have the product ", Product, ". I refused the call.");
	    	.send(Buyer, tell, refuse(CNPId));
	    }
	    -cfp(CNPId,_)[source(Buyer)];
.

/*
 * 
 */
@s0[atomic]
+!withdrawProduct(Product, Status): getMyName(Me)
	<-	scenario_Marketplace.actions.seller.withdrawProduct(Me, Product, Status);
.

/*
 * The offer sent by seller was accepted by a buyer
 */
@s1[atomic]
+accept_proposal(CNPId)[source(Buyer)]: getMyName(Me) &	my_proposal(CNPId,_)
	<-	.print("I won CNP: ", CNPId, ". I'm going to delive the product.");
		made(sale);
		!delivery(CNPId, Buyer);
		
		// Saving data for analysis	 
		scenario_Marketplace.actions.generic.saveContent(Me, "SALE", "sale");
		
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
	:	my_proposal(CNPId, Old_offer) & getMyName(Me)
	
	<-	scenario_Marketplace.actions.seller.getNewContract(Me, offer(Old_offer, Me), New_offer);
		.print("[REPORT] old offer: ", Old_offer, ", new offer: ", New_offer);
		.send(Buyer, tell, delivered(CNPId, New_offer));
		-my_proposal(CNPId,_);
.

/*
 * The seller shows his sale report.
 */
+!showReport: getMyName(Me)
	<-	scenario_Marketplace.actions.seller.getMadeSales(Me, Made_sales);
		scenario_Marketplace.actions.seller.getLostSales(Me, Lost_sales);
		    
		.print("[FINAL REPORT] Number of calls: ", Made_sales + Lost_sales, 
		       ", Made sales: ", Made_sales, 
		       ", Lost sales: ", Lost_sales);
		
		// Saving data for analysis
		.concat("made_sales:", Made_sales, ContentM);	 
		scenario_Marketplace.actions.generic.saveContent(Me, "AGENT", ContentM);
		 
		.concat("lost_sales:", Lost_sales, ContentL);	 
		scenario_Marketplace.actions.generic.saveContent(Me, "AGENT", ContentL);
.