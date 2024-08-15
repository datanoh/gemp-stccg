package com.gempukku.lotro.competitive;

import com.gempukku.util.DescComparator;
import com.gempukku.util.MultipleComparator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ModifiedMedianStandingsProducer {

    private static final FaceOffComparator FACE_OFF_COMPARATOR = new FaceOffComparator();
    private static final Comparator<PlayerStanding> MEDIAN_STANDING_COMPARATOR =
            new MultipleComparator<>(
                    new DescComparator<>(new PointsComparator()),
                    FACE_OFF_COMPARATOR,
                    new DescComparator<>(new OpponentsWinComparator()));


    public static List<PlayerStanding> produceStandings(Collection<String> participants, Collection<? extends CompetitiveMatchResult> matches,
                                                        int pointsForWin, int pointsForLoss, Map<String, Integer> playersWithByes) {
        Map<String, List<String>> playerOpponents = new HashMap<>();
        Map<String, AtomicInteger> playerWinCounts = new HashMap<>();
        Map<String, AtomicInteger> playerLossCounts = new HashMap<>();

        FACE_OFF_COMPARATOR.matches = matches;

        // Initialize the list
        for (String playerName : participants) {
            playerOpponents.put(playerName, new ArrayList<>());
            playerWinCounts.put(playerName, new AtomicInteger(0));
            playerLossCounts.put(playerName, new AtomicInteger(0));
        }

        for (CompetitiveMatchResult leagueMatch : matches) {
            playerOpponents.get(leagueMatch.getWinner()).add(leagueMatch.getLoser());
            playerOpponents.get(leagueMatch.getLoser()).add(leagueMatch.getWinner());
            playerWinCounts.get(leagueMatch.getWinner()).incrementAndGet();
            playerLossCounts.get(leagueMatch.getLoser()).incrementAndGet();
        }

        var standings = new HashMap<String, PlayerStanding>();
        for (String playerName : participants) {
            int playerWins = playerWinCounts.get(playerName).intValue();
            int playerLosses = playerLossCounts.get(playerName).intValue();
            int points = playerWins * pointsForWin + playerLosses * pointsForLoss;
            int gamesPlayed = playerWins + playerLosses;
            var byes = new ArrayList<Integer>();

            int byeRound = 0;
            if (playersWithByes.containsKey(playerName)) {
                byeRound = playersWithByes.get(playerName);
                points += pointsForWin;
                gamesPlayed += 1;
            }

            /*

            The Modified Median system calculates tiebreakers as follows:

                Players with the same score who faced each other defer to the winner.
                Players with more than 50% score have only their lowest-scoring opponent's score discarded;
                Players with less than 50% score have only their highest-scoring opponent's score discarded.
                Players with exactly 50% score discard both their lowest and highest opponent score;

             */

            List<String> opponents = playerOpponents.get(playerName);
            List<Integer> oppScores = new ArrayList<>();

            for (String opponent : opponents) {
                int wins = playerWinCounts.get(opponent).intValue();
                oppScores.add(wins);
            }

            Collections.sort(oppScores);
            float playerscore = 0f;
            if(gamesPlayed > 0) {
                playerscore = (float) playerWins / gamesPlayed;

                if(playerscore == 50.0f) {
                    oppScores.removeFirst();
                    oppScores.removeLast();
                }
                else if(playerscore > 50.0f) {
                    oppScores.removeFirst();
                }
                else { //playerscore < 50.0f
                    oppScores.removeLast();
                }
            }

            float score = oppScores.stream().mapToInt(Integer::intValue).sum();

            var standing = new PlayerStanding(playerName, points, gamesPlayed, playerWins, playerLosses,
                    byeRound, score, 0);
            standings.put(playerName, standing);
        }

        var tempStandings = new ArrayList<>(standings.values());
        tempStandings.sort(MEDIAN_STANDING_COMPARATOR);

        int standing = 0;
        int position = 1;
        PlayerStanding lastStanding = null;
        for (var eventStanding : tempStandings) {
            if (lastStanding == null || MEDIAN_STANDING_COMPARATOR.compare(eventStanding, lastStanding) != 0) {
                standing = position;
            }
            var newStanding = eventStanding.WithStanding(standing);
            standings.put(newStanding.playerName(), newStanding);
            position++;
            lastStanding = eventStanding;
        }
        return new ArrayList<>(standings.values());

    }

    private static class PointsComparator implements Comparator<PlayerStanding> {
        @Override
        public int compare(PlayerStanding o1, PlayerStanding o2) {
            return o1.points() - o2.points();
        }
    }

    private static class GamesPlayedComparator implements Comparator<PlayerStanding> {
        @Override
        public int compare(PlayerStanding o1, PlayerStanding o2) {
            return o1.gamesPlayed() - o2.gamesPlayed();
        }
    }

    private static class OpponentsWinComparator implements Comparator<PlayerStanding> {
        @Override
        public int compare(PlayerStanding o1, PlayerStanding o2) {
            final float diff = o1.opponentScore() - o2.opponentScore();
            if (diff < 0) {
                return -1;
            }
            if (diff > 0) {
                return 1;
            }
            return 0;
        }
    }

    private static class FaceOffComparator implements Comparator<PlayerStanding> {

        public Collection<? extends CompetitiveMatchResult> matches;


        @Override
        public int compare(PlayerStanding o1, PlayerStanding o2) {
            var result = matches.stream().filter(x ->
                    (x.getLoser().equals(o1.playerName()) && x.getWinner().equals(o2.playerName())) ||
                    (x.getLoser().equals(o2.playerName()) && x.getWinner().equals(o1.playerName())) ).findFirst();


            if(result.isEmpty())
                return 0;

            if(result.get().getWinner().equals(o1.playerName()))
                return 1;

            return -1;
        }
    }
}
