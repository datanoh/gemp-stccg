package com.gempukku.stccg.tournament;

import com.gempukku.stccg.AbstractServerTest;
import com.gempukku.stccg.competitive.BestOfOneStandingsProducer;
import com.gempukku.stccg.competitive.PlayerStanding;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

public class SwissPairingMechanismTest extends AbstractServerTest {

    @Test
    public void testPairingSmallTournament() {
        int repeatCount = 10;
        int playerCount = 12;

        for (int repeat = 0; repeat < repeatCount; repeat++) {
            testSwissPairingForPlayerCount(playerCount);
        }
    }

    @Test
    public void testPairingVerySmallTournament() {
        int repeatCount = 10;
        int playerCount = 8;

        for (int repeat = 0; repeat < repeatCount; repeat++) {
            testSwissPairingForPlayerCount(playerCount);
        }
    }

    @Test
    public void testPairingSmallTournamentWithOddNumberOfPlayers() {
        int repeatCount = 10;
        int playerCount = 9;

        for (int repeat = 0; repeat < repeatCount; repeat++) {
            testSwissPairingForPlayerCount(playerCount);
        }
    }

    private void testSwissPairingForPlayerCount(int playerCount) {
        Set<String> players = new HashSet<>();
        for (int i = 0; i < playerCount; i++)
            players.add("p" + i);

        Set<String> droppedPlayers = new HashSet<>();
        Map<String, Integer> byes = new HashMap<>();

        Set<TournamentMatch> matches = new HashSet<>();
        Map<String, Set<String>> previouslyPaired = new HashMap<>();
        for (String player : players)
            previouslyPaired.put(player, new HashSet<>());

        SwissPairingMechanism pairing = new SwissPairingMechanism("swiss");
        for (int i = 1; i < 20; i++) {
            if (!pairing.isFinished(i - 1, players, droppedPlayers)) {
                System.out.println("Pairing round " + i);
                List<PlayerStanding> standings = BestOfOneStandingsProducer.produceStandings(players, matches, 1, 0, byes);
                for (PlayerStanding standing : standings) {
                    String player = standing.getPlayerName();
                    log(player + " points - " + standing.getPoints() + " played against: " + StringUtils.join(previouslyPaired.get(player), ","));
                }

                Map<String, String> newPairings = new LinkedHashMap<>();
                Set<String> newByes = new HashSet<>();

                assertFalse("Unable to pair for round " + i, pairing.pairPlayers(i, players, droppedPlayers, byes, standings, previouslyPaired, newPairings, newByes));
                assertEquals("Invalid number of pairings", playerCount / 2, newPairings.size());
                if (playerCount % 2 == 0)
                    assertEquals("Invalid number of byes", 0, newByes.size());
                else {
                    assertEquals("Invalid number of byes", 1, newByes.size());
                    String newBye = newByes.iterator().next();
                    log("Bye - " + newBye);
                    assertNull("Player already received bye", byes.get(newBye));
                    byes.put(newBye, 1);
                }

                for (Map.Entry<String, String> newPairing : newPairings.entrySet()) {
                    String playerOne = newPairing.getKey();
                    String playerTwo = newPairing.getValue();

                    assertFalse(previouslyPaired.get(playerOne).contains(playerTwo));
                    assertFalse(previouslyPaired.get(playerTwo).contains(playerOne));

                    System.out.println("Paired " + playerOne + " against " + playerTwo + " points - " + getPlayerPoints(standings, playerOne) + " vs " + getPlayerPoints(standings, playerTwo));
                    String winner = ThreadLocalRandom.current().nextBoolean() ? playerOne : playerTwo;
                    log("Winner - " + winner);

                    previouslyPaired.get(playerOne).add(playerTwo);
                    previouslyPaired.get(playerTwo).add(playerOne);

                    matches.add(new TournamentMatch(playerOne, playerTwo, winner, i));
                }
            }
        }
        System.out.println("Final standings:");
        List<PlayerStanding> standings = BestOfOneStandingsProducer.produceStandings(players, matches, 1, 0, byes);
        for (PlayerStanding standing : standings) {
            String player = standing.getPlayerName();
            System.out.println(standing.getStanding() + ". " + player + " points - " + standing.getPoints() + " played against: " + StringUtils.join(previouslyPaired.get(player), ","));
        }
    }

    private void log(String s) {
        // System.out.println(s);
    }

    private int getPlayerPoints(List<PlayerStanding> standings, String player) {
        for (PlayerStanding standing : standings) {
            if (standing.getPlayerName().equals(player))
                return standing.getPoints();
        }
        return -1;
    }
}
