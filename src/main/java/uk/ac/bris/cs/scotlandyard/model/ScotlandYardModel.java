package uk.ac.bris.cs.scotlandyard.model;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static uk.ac.bris.cs.scotlandyard.model.Colour.*;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.Double;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.Secret;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;
import uk.ac.bris.cs.gamekit.graph.Graph;

// TODO implement all methods and pass all tests
public class ScotlandYardModel implements ScotlandYardGame {
	
	public PlayerConfiguration mrX, firstDetective;
	public List<Boolean> rounds;
	public Graph<Integer, Transport> graph;
	public ArrayList<PlayerConfiguration> restOfTheDetectives = new ArrayList<>();
	public HashMap<Colour, Integer> colours;

	public ScotlandYardModel(List<Boolean> rounds, Graph<Integer, Transport> graph,
			PlayerConfiguration mrX, PlayerConfiguration firstDetective,
			PlayerConfiguration... restOfTheDetectives) {

		if(requireNonNull(rounds).isEmpty()) throw new IllegalArgumentException("Oops");
		if(requireNonNull(graph).isEmpty()) throw new IllegalArgumentException("Oops");

		colours = new HashMap<>();
		colours.put(Blue, 0);
		colours.put(Red, 0);
		colours.put(Black, 0);
		colours.put(Green, 0);
		colours.put(Yellow, 0);

		this.mrX = requireNonNull(mrX);
		this.firstDetective = requireNonNull(firstDetective);
		colours.put(firstDetective.colour, colours.get(firstDetective.colour) + 1);
		for(PlayerConfiguration current : restOfTheDetectives) {

			this.restOfTheDetectives.add(requireNonNull(current));
		}

		if(!mrX.colour.isMrX()) throw new IllegalArgumentException("There is no MRX");
		if(firstDetective.colour.isMrX()) throw new IllegalArgumentException("There is more than one MRX");

		for(PlayerConfiguration current : this.restOfTheDetectives)
			colours.put(current.colour, colours.get(current.colour) + 1);
		if(colours.containsValue(2)) throw new IllegalArgumentException("Duplicato");
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
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public Collection<Spectator> getSpectators() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public List<Colour> getPlayers() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public Set<Colour> getWinningPlayers() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public int getPlayerLocation(Colour colour) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public int getPlayerTickets(Colour colour, Ticket ticket) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public boolean isGameOver() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public Colour getCurrentPlayer() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public int getCurrentRound() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public boolean isRevealRound() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public List<Boolean> getRounds() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public Graph<Integer, Transport> getGraph() {
		// TODO
		throw new RuntimeException("Implement me");
	}

}