package uk.ac.bris.cs.scotlandyard.model;

import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static uk.ac.bris.cs.scotlandyard.model.Colour.*;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.*;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.Double;

public class ScotlandYardModel implements ScotlandYardGame, Consumer<Move> {
	
	private List<Boolean> rounds;
	private Graph<Integer, Transport> graph;
	private int lastMrLocation;
	private List<PlayerConfiguration> configurations;
	private HashMap<Colour, Integer> colours;
	private Colour currentPlayer;
	private int currentRound;
	private Set<Colour> winningPlayers;
	private List<ScotlandYardPlayer> players;
	private List<Colour> players_asColours;
	private MoveVisitor removeTicket;
	private Boolean mrXisCaptured;
	private final List<Spectator> spectators;
	private ScotlandYardModel view;
	
	public ScotlandYardModel(List<Boolean> rounds, Graph<Integer, Transport> graph,
			PlayerConfiguration mrX, PlayerConfiguration firstDetective,
			PlayerConfiguration... restOfTheDetectives) {
		
		configurations = new ArrayList<>();
		winningPlayers = new HashSet<>();
		spectators = new CopyOnWriteArrayList<>();
		colours = new HashMap<>();
		colours.put(Black, 0);
		colours.put(Blue, 0);
		colours.put(Red, 0);
		colours.put(Green, 0);
		colours.put(Yellow, 0);
		colours.put(White, 0);
		
		view = this;
		this.rounds = requireNonNull(rounds);
		this.currentRound = 0; //initialised with 0 at first, will be changed later.
		this.graph = requireNonNull(graph);
		configurations.add(requireNonNull(mrX));
		configurations.add(requireNonNull(firstDetective));
		for(PlayerConfiguration current : restOfTheDetectives)
			configurations.add(requireNonNull(current));
				
		ValidMapCheck();
		ValidConfigurationsCheck();	// Should it be a throw function -- sisi, tova e za men da pitam
		ValidTicketSlotsCheck();
		ValidDetectiveTicketsCheck();
		
		players = new ArrayList<>();
		for(PlayerConfiguration current : configurations)
			players.add(new ScotlandYardPlayer(current.player, current.colour, current.location, current.tickets));
		
		players_asColours = new ArrayList<>();
		for(ScotlandYardPlayer current : players)
			players_asColours.add(current.colour());

		currentPlayer = players.get(0).colour();
		currentRound = 0;
		lastMrLocation = 0;
		mrXisCaptured = false;
		
		removeTicket = new MoveVisitor(){
			public void visit(PassMove move){
				for(Spectator current : spectators)
					current.onMoveMade(view, move);
			}
			public void visit(TicketMove move){
				ScotlandYardPlayer curPlayer = getCurrentPlayerFromColour(currentPlayer);
				ScotlandYardPlayer mrX = getCurrentPlayerFromColour(Black);
				
				curPlayer.location(move.destination());
				
				curPlayer.removeTicket(move.ticket());
				
				if(currentPlayer.isDetective()){
					mrX.addTicket(move.ticket());	
					if(curPlayer.location() == mrX.location()){
						mrXisCaptured = true;
					}
					for (Spectator current : spectators){
						current.onMoveMade(view, move);
					}
				}
				if(currentPlayer.isMrX()) {
					currentRound++;
					for (Spectator current : spectators){
						current.onRoundStarted(view, currentRound);
						if (isRevealRound()) current.onMoveMade(view, move);
						else current.onMoveMade(view, new TicketMove(curPlayer.colour(), move.ticket(), lastMrLocation));
					}
				}
				
			}
			public void notifyOnRevealRound(DoubleMove move){
				ScotlandYardPlayer mrX = players.get(0);
				
				if(isRevealRound()) { //FIRST MOVE IS REVEAL ROUND.
					currentRound++;
					if (isRevealRound()){ //SECOND MOVE IS REVEAL ROUND.
						currentRound = currentRound - 2;
						for(Spectator current : spectators)
							current.onMoveMade(view, move);
					}
					else { //SECOND MOVE IS HIDDEN ROUND.
						currentRound = currentRound - 2;
						for(Spectator current : spectators){
							current.onMoveMade(view, new DoubleMove(Black, move.firstMove(), new TicketMove(Black, move.secondMove().ticket(), move.firstMove().destination())));
						}
					}
				}

				else { //FIRST MOVE IS HIDDEN ROUND.
					currentRound++;
					if(isRevealRound()){ //SECOND MOVE IS REVEAL ROUND.
						currentRound = currentRound - 2;
						for(Spectator current : spectators){
							current.onMoveMade(view, new DoubleMove(Black, new TicketMove(Black, move.firstMove().ticket(), lastMrLocation), move.secondMove()));
									
						}
					}
					else { //SECOND MOVE IS HIDDEN ROUND.
						currentRound = currentRound - 2;
						for(Spectator current : spectators){
							current.onMoveMade(view, new DoubleMove(Black, move.firstMove().ticket(), lastMrLocation, move.secondMove().ticket(), lastMrLocation));
						}
					}
				}
			}
			
			public void visit(DoubleMove move){
				ScotlandYardPlayer mrX = getCurrentPlayerFromColour(Black);
				currentRound++;
				
				notifyOnRevealRound(move);
				
				firstMoveOfDoubleMove(move);
				
				secondMoveOfDoubleMove(move);
				
				
				getCurrentPlayerFromColour(currentPlayer).location(move.finalDestination());
				mrX.removeTicket(Double);
		}
		
			private void firstMoveOfDoubleMove(DoubleMove move){
				ScotlandYardPlayer mrX = getCurrentPlayerFromColour(Black);
				
				mrX.removeTicket(move.firstMove().ticket());
				currentRound++;
				mrX.location(move.firstMove().destination());
				if (isRevealRound()) lastMrLocation = mrX.location();
				for (Spectator current : spectators){
					current.onRoundStarted(view, currentRound);
					if(isRevealRound()) current.onMoveMade(view, move.firstMove());
					else current.onMoveMade(view, new TicketMove(mrX.colour(), move.firstMove().ticket(), lastMrLocation));
				}
			}
			
			private void secondMoveOfDoubleMove(DoubleMove move){
				ScotlandYardPlayer mrX = getCurrentPlayerFromColour(Black);
				
				mrX.removeTicket(move.secondMove().ticket());
				currentRound++;
				mrX.location(move.secondMove().destination());
				if (isRevealRound()) lastMrLocation = mrX.location();
				for (Spectator current : spectators){
					current.onRoundStarted(view, currentRound);
					if(isRevealRound()) current.onMoveMade(view, move.secondMove());
					else current.onMoveMade(view, new TicketMove(mrX.colour(), move.secondMove().ticket(), lastMrLocation));
				}
			}
		};
	}

	// Checks whether the graph and visible rounds are empty
	private void ValidMapCheck(){
		if(rounds.isEmpty()) throw new IllegalArgumentException("Rounds is empty");
		if(graph.isEmpty()) throw new IllegalArgumentException("Graph is empty");
	}
	
	//Checks if the players already exists and whether their position overlaps with mrX
	private void ValidConfigurationsCheck (){
		if(!configurations.get(0).colour.isMrX()) throw new IllegalArgumentException("MrX is missing");
		
		for(PlayerConfiguration current : configurations){
			Colour currentColour = current.colour;
			colours.put(currentColour, colours.get(currentColour) + 1);
			if(colours.get(currentColour) > 1) throw new IllegalArgumentException("Players duplicate");
		}
		ValidPositionsCheck();
	}
	
	private void ValidPositionsCheck(){ // is this a good idea or it's not dry enough? -- pak iskam da pitam tam
		Iterator<PlayerConfiguration> detectives = configurations.listIterator(1);
		while(detectives.hasNext()){
			PlayerConfiguration detective = detectives.next();
			if(detective.location == configurations.get(0).location) 
				throw new IllegalArgumentException("Detective has spawned with the same location as mrX");
		}
	}
	
	//Checks whether all the players have all the ticket slots
	private void ValidTicketSlotsCheck(){
		for(PlayerConfiguration current : configurations){
			Map<Ticket, Integer> tickets = current.tickets;
			if(!(tickets.containsKey(Bus)  	  	 &&
				 tickets.containsKey(Taxi) 	  	 &&
				 tickets.containsKey(Underground)&&
				 tickets.containsKey(Double)     &&
				 tickets.containsKey(Secret)))
					throw new IllegalArgumentException("One of the players doesn't have all tickets!");	
			
		}
	}
	
	//Checks whether the detective has double/secret tickets
	private void ValidDetectiveTicketsCheck(){
		Iterator<PlayerConfiguration> detectives = configurations.listIterator(1);
		while(detectives.hasNext()){
			PlayerConfiguration detective = detectives.next();
			if(detective.tickets.get(Double) != 0) throw new IllegalArgumentException("Detective has double tickets");
			if(detective.tickets.get(Secret) != 0) throw new IllegalArgumentException("Detective has secret tickets");
		}
	}
	
	@Override
	public void registerSpectator(Spectator spectator) {
		requireNonNull(spectator);
		if(!spectators.contains(spectator)){
			this.spectators.add(spectator);
		}
		else throw new IllegalArgumentException("This spectator is already registered.");
	}

	@Override
	public void unregisterSpectator(Spectator spectator) {
		requireNonNull(spectator);
		if(!spectators.isEmpty()){
			this.spectators.remove(requireNonNull(spectator));
		}
		else throw new IllegalArgumentException("No spectators to unregister.");	
	}

	@Override
	public void startRotate() {
		currentPlayer = players.get(0).colour();
		if(isGameOver() == true) throw new IllegalStateException("Game is not over initially.");
		makeMoveCurrentPlayer();
	}
	
	public Set<Move> validMoves(ScotlandYardPlayer player){
		Set<Move> valid = new HashSet<>();
		Collection<Edge<Integer, Transport>> edgesFromLocation = graph.getEdgesFrom(graph.getNode(player.location()));

		if(player.isDetective()){
				valid.addAll(AddTicketMoves(player, edgesFromLocation));
				
				if(valid.isEmpty()) 							// produces a pass for the detective if no moves are available
					valid.add(new PassMove(player.colour()));
			}

		
		else if(player.isMrX()){
				valid.addAll(AddTicketMoves(player, edgesFromLocation));
				
				if(player.hasTickets(Secret))													
					valid.addAll(AddSecretMoves(player, edgesFromLocation));
				
				if(player.hasTickets(Double) && getCurrentRound() < getRounds().size() - 1 )	//the if also checks if there are enough rounds to play a double move
					valid.addAll(AddDoubleMoves(player, valid));	
		}
		return valid;
	}
	
	private Set<Move> AddTicketMoves(ScotlandYardPlayer player, Collection<Edge<Integer, Transport>> edgesFromLocation){
		Set<Move> ticketMoves = new HashSet<>();

		for(Edge<Integer, Transport> currentEdge : edgesFromLocation){
			int destination = currentEdge.destination().value();
			
			if(player.hasTickets(fromTransport(currentEdge.data())) && !LocationOccupiedByDetective(destination)){
				TicketMove move = new TicketMove(player.colour(), fromTransport(currentEdge.data()), destination);
				ticketMoves.add(move);
				}
		}
		return ticketMoves;
	}
	
	private Set<Move> AddSecretMoves(ScotlandYardPlayer player, Collection<Edge<Integer, Transport>> edgesFromLocation){
		Set<Move> ticketMoves = new HashSet<>();

		for(Edge<Integer, Transport> currentEdge : edgesFromLocation){
			int destination = currentEdge.destination().value();
			
			if(player.hasTickets(Secret) && !LocationOccupiedByDetective(destination)){
				TicketMove move = new TicketMove(player.colour(), Secret, destination);
				ticketMoves.add(move);
				}
		}
		return ticketMoves;
	}
	
	private Set<Move> AddDoubleMoves(ScotlandYardPlayer player, Set<Move> firstSetOfMoves){
		Set<Move> ticketMoves = new HashSet<>();
		
		for(Move move : firstSetOfMoves){
			TicketMove tMove = (TicketMove)move;
			player.removeTicket(tMove.ticket()); 	// It removes the ticket used for the first move so that we can check that we are not using the same ticket twice
			int destination = tMove.destination();
			Collection<Edge<Integer, Transport>> edgesFromDestination = graph.getEdgesFrom(graph.getNode(destination));
			
			for(Edge<Integer, Transport> currentEdge : edgesFromDestination){
				int destinationLocation = currentEdge.destination().value();
				if(!LocationOccupiedByDetective(destinationLocation)){
					if(player.hasTickets(fromTransport(currentEdge.data()))){
						DoubleMove doubleMove = new DoubleMove (player.colour(), tMove.ticket(), destination, 
																				fromTransport(currentEdge.data()), destinationLocation);
						ticketMoves.add(doubleMove);
					}
					if(player.hasTickets(Secret)  && destinationLocation != destination){
						DoubleMove doubleMove = new DoubleMove (player.colour(), tMove.ticket(), destination, 
																				Secret, destinationLocation);
						
						ticketMoves.add(doubleMove);
					}
				}
			}
			player.addTicket(tMove.ticket());	// returns the ticket that we removed so we don't change anything
		}
		
		return ticketMoves;
	}

	private Boolean LocationOccupiedByDetective(int location){
		Boolean occupied = false;
		
			for (ScotlandYardPlayer detective : players){
				if (detective.isDetective()){
					if(detective.location() == location)
						occupied = true;
				}
			}
		
		return occupied;
	}
	
	@Override
	public void accept(Move move) {
		if(!validMoves(getCurrentPlayerFromColour(currentPlayer)).contains(requireNonNull(move))) throw new IllegalArgumentException("Invalid move, move not contained in validMoves");		
		move.visit(removeTicket);
		
		if(currentPlayer == players_asColours.get(players_asColours.size() - 1)){	// makes sure to stop switching players after the rotation is done
			for(Spectator current : spectators){
				current.onRotationComplete(this);
			}
		}
		else {
			nextPlayer();
			makeMoveCurrentPlayer();
		}
	}
	
	private void nextPlayer(){
			currentPlayer = players_asColours.get(players_asColours.indexOf(currentPlayer) + 1);
	}
	
	private void makeMoveCurrentPlayer(){
    	ScotlandYardPlayer player = getCurrentPlayerFromColour(currentPlayer);
    	player.player().makeMove(this, player.location(), validMoves(player), this);
    	
    	if(isGameOver() == true){
    		for(Spectator current : spectators)
				current.onGameOver(this, winningPlayers);
    	}
	}
	
	@Override
	public Collection<Spectator> getSpectators() {
		return Collections.unmodifiableList(spectators);
	}

	@Override
	public List<Colour> getPlayers() {
		return Collections.unmodifiableList(players_asColours);
	}

	@Override
	public Set<Colour> getWinningPlayers() {
		return Collections.unmodifiableSet(winningPlayers);
	}

	@Override
	public int getPlayerLocation(Colour colour) {
		if(colour.isMrX()){
			if(isRevealRound()){
				lastMrLocation = getCurrentPlayerFromColour(colour).location();
				return getCurrentPlayerFromColour(colour).location();
			}
			else
				return lastMrLocation;
		}
		
		return getCurrentPlayerFromColour(colour).location();
	}

	@Override
	public int getPlayerTickets(Colour colour, Ticket ticket) {
		return getCurrentPlayerFromColour(colour).tickets().get(ticket);
	}

	@Override
	public boolean isGameOver() {
		boolean detectivesAreStuck = true;
		Set<Colour> detectives = new HashSet<>();
		
		for(int i=1; i<players.size(); i++){ // iterating through the detectives only
			if(!validMoves(players.get(i)).contains(new PassMove(players.get(i).colour()))){
				detectivesAreStuck = false;
			}
			detectives.add(players.get(i).colour());	// just getting the detectives if they win
		}
		
		if(detectivesAreStuck || currentRound > rounds.size() - 1){
			winningPlayers.add(Black);
			return true;
		}
		else if(mrXisCaptured || validMoves(players.get(0)).isEmpty()){
			winningPlayers = detectives;
			return true;
		}
		else
			return false;
	}
	
	@Override
	public Colour getCurrentPlayer() {
		return this.currentPlayer;
	}

	@Override
	public int getCurrentRound() {
		return currentRound;
	}

	@Override
	public boolean isRevealRound() {
		if(currentRound == 0) return false;
		else return rounds.get(currentRound - 1);
	}

	@Override
	public List<Boolean> getRounds() {
		return Collections.unmodifiableList(rounds);
	}

	@Override
	public Graph<Integer, Transport> getGraph() {
		return new ImmutableGraph<>(graph);
	}

	private ScotlandYardPlayer getCurrentPlayerFromColour(Colour colour){
		return players.get(players_asColours.indexOf(colour));
	}
}
