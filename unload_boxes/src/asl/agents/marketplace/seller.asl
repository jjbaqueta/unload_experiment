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
		      	!sendMyknowHow(Product, Buyer);		
		      	.send(Buyer, tell, proposal(CNPId, Offer));
			}
			else
			{
	    		.send(Buyer, tell, missing(CNPId, Product));
	    		.wait(50);
	    		.send(Buyer, tell, refuse(CNPId));
	    		!countLostSale;
			}
	    }
	    else
	    {
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
+accept_proposal(CNPId, Requisition)[source(Buyer)]
	: 	getMyName(Me) & 
		my_proposal(CNPId, product(Product,_,_,_))
	<-	!countMadeSale;
		!delivery(CNPId, Buyer);
		
		// Saving data for analysis	
		.concat(Buyer,"-", Temp);
		.concat(Temp, Product, Content)	  
		scenario_Marketplace.actions.generic.saveContent(Me, "SALE", Content, Requisition);
		
		-accept_proposal(CNPId)[source(Buyer)];
.

/*
 * The offer sent by seller was rejected by a buyer.
 */
@s2[atomic]
+reject_proposal(CNPId)[source(Buyer)]
	<-	!countLostSale;
		-my_proposal(CNPId,_);
		-reject_proposal(CNPId)[source(Buyer)];
.

/*
 * Recalculate the contract terms (this process depends on seller's type)
 * @param CNPId: id of the call.
 * @param Buyer: buyer that bought a product.
 */
 @s3[atomic]
+!delivery(CNPId, Buyer)
	:	my_proposal(CNPId, Old_offer) & getMyName(Me)
	
	<-	scenario_Marketplace.actions.seller.getNewContract(Me, offer(Old_offer, Me), New_offer);
		.print("[REPORT] old offer: ", offer(Old_offer, Me), ", new offer: ", New_offer);
		.send(Buyer, tell, delivered(CNPId, New_offer, offer(Old_offer, Me)));
		-my_proposal(CNPId,_);
.

@s4[atomic]
+!countMadeSale
	<-	made(sale);
.

@s5[atomic]
+!countLostSale
	<-	lost(sale);
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
		scenario_Marketplace.actions.generic.saveContent(Me, "AGENT", ContentM, -1);
		 
		.concat("lost_sales:", Lost_sales, ContentL);	 
		scenario_Marketplace.actions.generic.saveContent(Me, "AGENT", ContentL, -1);
.