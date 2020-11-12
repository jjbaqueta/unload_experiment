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