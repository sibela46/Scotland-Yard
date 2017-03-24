package uk.ac.bris.cs.scotlandyard.model;

import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;

import java.util.*;
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
	
	public ScotlandYardModel(List<Boolean> rounds, Graph<Integer, Transport> graph,
			PlayerConfiguration mrX, PlayerConfiguration firstDetective,
			PlayerConfiguration... restOfTheDetectives) {
		
		configurations = new ArrayList<>();
		winningPlayers = new HashSet<>();
		colours = new HashMap<>();
		colours.put(Black, 0);
		colours.put(Blue, 0);
		colours.put(Red, 0);
		colours.put(Green, 0);
		colours.put(Yellow, 0);
		colours.put(White, 0);
		
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
		currentRound = NOT_STARTED;
		lastMrLocation = 0;
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
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public void unregisterSpectator(Spectator spectator) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public void startRotate() {
		ScotlandYardPlayer player = players.get(players_asColours.indexOf(currentPlayer));
		Set<Move> Moves = new HashSet<>();
				
		player.player().makeMove(this, player.location(), validMoves(player), this);
	}
	
	public Set<Move> validMoves(ScotlandYardPlayer player){
		Set<Move> valid = new HashSet<>();

		Collection<Edge<Integer, Transport>> edgesFromLocation = graph.getEdgesFrom(graph.getNode(player.location()));

		for(Edge<Integer, Transport> currentEdge : edgesFromLocation){
			if(player.hasTickets(fromTransport(currentEdge.data()))){
				TicketMove move = new TicketMove(player.colour(), fromTransport(currentEdge.data()), (int) currentEdge.destination().value());
				valid.add(move);
			}
		}
		return valid;
	}
	
	@Override
	public Collection<Spectator> getSpectators() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public List<Colour> getPlayers() {
		return Collections.unmodifiableList(players_asColours);
	}

	@Override
	public Set<Colour> getWinningPlayers() {
		return Collections.unmodifiableSet(this.winningPlayers);
	}

	@Override
	public int getPlayerLocation(Colour colour) {
		if(colour.isMrX()){
			if(rounds.get(currentRound))
				return getCurrentPlayerFromColour(colour).location();
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
		return false; //will have to change this later
	}

	@Override
	public Colour getCurrentPlayer() {
		return this.currentPlayer;
	}

	@Override
	public int getCurrentRound() {
		return this.currentRound;
	}

	@Override
	public boolean isRevealRound() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public List<Boolean> getRounds() {
		return Collections.unmodifiableList(rounds);
	}

	@Override
	public Graph<Integer, Transport> getGraph() {
		return new ImmutableGraph<>(graph);
	}

	@Override
	public void accept(Move move) {
		if(!validMoves(players.get(players_asColours.indexOf(currentPlayer))).contains(requireNonNull(move))) throw new IllegalArgumentException("Invalid move");
        switchPlayer(getCurrentPlayerFromColour(currentPlayer));
	}
	
	private ScotlandYardPlayer switchPlayer(ScotlandYardPlayer player){
		for (int i=0; i<players.size(); i++){
			player = players.get(i);
		}
		return player;
	}
	
	private ScotlandYardPlayer getCurrentPlayerFromColour(Colour colour){
		return players.get(players_asColours.indexOf(colour));
	}
}
