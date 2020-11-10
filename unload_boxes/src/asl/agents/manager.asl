/**
 * This agent is responsible for initialization and management of environment.
 * He creates and removes agents from the environment.
 */

/* BEHAVIOR *************/
!start.

+!start
	<- 	actions.generic.getTime(StartTime);
		+startTime(StartTime);
.

/**
 * Create a new worker
 * @param Name: name of worker.
 */ 
+add_worker(Name)
	<-	.create_agent(Name, "worker.asl");
		.print("A new worker was created. Name: ", Name);
.

/**
 * Create a new worker
 * @param Name: name of trucker.
 */ 
+add_trucker(Name)
	<-	.create_agent(Name, "truck.asl");
		.print("A new truck was created. Name: ", Name);
		.send(Name, achieve, unload);
.
		
/**
 * Create a new helper
 * @param Name: name of helper.
 */ 
+add_helper(Name)
	<-	.create_agent(Name, "helper.asl");
		.print("A new helper was created. Name: ", Name);
.

/**
 * A trucker informs to manager that finished his current execution
 * @param Truck: a trucker.
 */ 
+stop(Truck) 
	<-	actions.generic.resetProperties(Truck);
		actions.trucker.getNext(Next_trucker);
		!check_end(Next_trucker);
.

/**
 * Check if all truckers finished their executions.
 * @param Next_trucker: the next trucker to be added to the system.
 */ 
@m_1[atomic]
+!check_end(Next_trucker)
	<- 	?startTime(StartTime);
	
		if(Next_trucker \== none)
		{
			.send(Next_trucker, achieve, start);
			.send(Next_trucker, achieve, unload);
		}
		
		.count(add_trucker(_), Added);
		.count(stop(_), Finished);
		
		if(Added == Finished)
		{
			actions.generic.getTime(EndTime);
			.print("[END OF EXECUTION] - Time (miliseconds): ", EndTime - StartTime);		
		}
		update_screen;
.

/**
 * The trucker informs to manager that he will go out from the system.
 */ 
@m_2[atomic]
+!quit[source(Truck)]
	<-	actions.generic.resetProperties(Truck);
		actions.trucker.goToWaitingQueue(Truck);
		actions.trucker.getNext(Next_trucker);
		.send(Next_trucker, achieve, start);
		.send(Next_trucker, achieve, unload);
		update_screen;
.