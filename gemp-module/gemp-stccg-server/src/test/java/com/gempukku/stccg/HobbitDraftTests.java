package com.gempukku.stccg;

import com.gempukku.stccg.cards.SetDefinition;
import com.gempukku.stccg.collection.CollectionsManager;
import com.gempukku.stccg.draft.SoloDraft;
import com.gempukku.stccg.draft.SoloDraftDefinitions;
import com.gempukku.stccg.formats.FormatLibrary;
import org.junit.Test;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class HobbitDraftTests extends AbstractServerTest {
    @Test
    public void HobbitDraftTest() {
        CollectionsManager collectionsManager = new CollectionsManager(null, null, null, _cardLibrary);
        FormatLibrary formatLibrary = new FormatLibrary(_cardLibrary);

        SoloDraftDefinitions soloDraftDefinitions = new SoloDraftDefinitions(collectionsManager, _cardLibrary, formatLibrary);

        final SoloDraft hobbitDraft = soloDraftDefinitions.getSoloDraft("hobbit_draft");

        long collectionType = 1568486003481L;
        final int playerId = 1000;

        Map<String, Integer> availableCards = new TreeMap<>(
                (o1, o2) -> {
                    int set1 = Integer.parseInt(o1.substring(0, o1.indexOf('_')));
                    int set2 = Integer.parseInt(o2.substring(0, o2.indexOf('_')));
                    if (set1 != set2)
                        return set1 - set2;
                    int card1 = Integer.parseInt(o1.substring(o1.indexOf('_') + 1));
                    int card2 = Integer.parseInt(o2.substring(o2.indexOf('_') + 1));
                    return card1 - card2;
                });

        for (int i = 0; i < 10000; i++) {
            // Take an example seed
            long seed = getSeed(String.valueOf(collectionType + i));

            int stage = 0;

            while (hobbitDraft.hasNextStage(seed, stage)) {
                final Iterable<SoloDraft.DraftChoice> availableChoices = hobbitDraft.getAvailableChoices(seed, stage, null);
                for (SoloDraft.DraftChoice availableChoice : availableChoices) {
                    final String blueprintId = availableChoice.getBlueprintId();
                    availableCards.merge(blueprintId, 1,
                            Integer::sum);
                }

                stage++;
            }
        }

        for (Map.Entry<String, Integer> entry : availableCards.entrySet()) {
            final String blueprint = entry.getKey();
            String set = blueprint.substring(0, blueprint.indexOf('_'));
            final SetDefinition setDefinition = _cardLibrary.getSetDefinitions().get(set);
            final String cardRarity = setDefinition.getCardRarity(blueprint);
            System.out.println(blueprint + " (" + cardRarity + "): " + entry.getValue());
        }
    }

    private static void testRandomness2() {
        int lastValue = -1;
        long patchStart = 1813024350;
        long longestPatch = 0;
        for (long seed = 0; seed < 8813024350L; seed++) {
            final int value = new Random(seed).nextInt(8);
            if (value != lastValue) {
                long patchLength = seed - patchStart;
                if (patchLength > longestPatch) {
                    System.out.println("Longest patch: " + patchStart + "-" + (seed - 1));
                    System.out.println("Patch length: " + patchLength);
                    longestPatch = patchLength;
                }
                patchStart = seed;
                lastValue = value;
            }
        }
    }

    @Test
    public void testRandomness() {
        doRandomTest(4, false);
        System.out.println("WTF!!!!");
        System.out.println("WTF!!!!");
        System.out.println("WTF!!!!");
        doRandomTest(3, false);
        doRandomTest(4, true);
        doRandomTest(3, true);
    }

    private static void doRandomTest(int nextIntValue, boolean getFloatBeforeInt) {
        System.out.println("Get float before int: " + getFloatBeforeInt);
        System.out.println("Next int value: " + nextIntValue);
        int[] values = new int[nextIntValue];
        for (int i = 0; i < 1000; i++) {
            Random rnd = new Random(i);
            if (getFloatBeforeInt)
                rnd.nextFloat();
            values[rnd.nextInt(nextIntValue)]++;
        }
        for (int i = 0; i < values.length; i++) {
            System.out.println(i + ": " + values[i]);
        }
    }

    private static long getSeed(String collectionType) {
        return collectionType.hashCode() + 1000 * 8963L;
    }
}
