/**
 * This agent is responsible for initialization and management of environment.
 * He creates and removes agents from the environment.
 */

/* BEHAVIOR *************/

/**
 * Create a new buyer
 * @param Name: name of buyer.
 */ 
+add_buyer(Name)
	<-	.create_agent(Name, "buyer.asl");
		.print("A new buyer was created. Name: ", Name);
.

/**
 * Create a new seller
 * @param Name: name of seller.
 */ 
+add_seller(Name)
	<-	.create_agent(Name, "seller.asl");
		.print("A new seller was created. Name: ", Name);
.

/**
 * Informs to buyers that they have to show their reports
 */ 
+show_report
	<-	.findall(Buyer, add_buyer(Buyer), Buyers);
		!reports(Buyers);
.

+!reports([Buyer|T])
	<-	.send(Buyer, achieve, showReport);
		!reports(T);
.

+!reports([]).