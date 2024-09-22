package com.gempukku.stccg.draft.builder;

import com.gempukku.stccg.cards.GenericCardItem;
import com.gempukku.stccg.collection.CollectionsManager;
import com.gempukku.stccg.draft.DraftChoiceDefinition;
import com.gempukku.stccg.draft.SoloDraft;
import com.gempukku.stccg.collection.CardCollection;
import com.gempukku.stccg.collection.DefaultCardCollection;
import com.gempukku.stccg.cards.CardBlueprintLibrary;
import com.gempukku.stccg.game.SortAndFilterCards;
import com.gempukku.stccg.formats.FormatLibrary;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class DraftChoiceBuilder {
    public static final int HIGH_ENOUGH_PRIME_NUMBER = 9497;
    private final CollectionsManager _collectionsManager;
    private final CardBlueprintLibrary _cardLibrary;
    private final FormatLibrary _formatLibrary;
    private final SortAndFilterCards _sortAndFilterCards;

    public DraftChoiceBuilder(CollectionsManager collectionsManager, CardBlueprintLibrary cardLibrary,
                              FormatLibrary formatLibrary) {
        _collectionsManager = collectionsManager;
        _cardLibrary = cardLibrary;
        _formatLibrary = formatLibrary;
        _sortAndFilterCards = new SortAndFilterCards();
    }

    public DraftChoiceDefinition buildDraftChoiceDefinition(JSONObject choiceDefinition) {
        return constructDraftChoiceDefinition(choiceDefinition);
    }

    private DraftChoiceDefinition constructDraftChoiceDefinition(JSONObject choiceDefinition) {
        String choiceDefinitionType = (String) choiceDefinition.get("type");
        JSONObject data = (JSONObject) choiceDefinition.get("data");
        return switch (choiceDefinitionType) {
            case "singleCollectionPick" -> buildSingleCollectionPickDraftChoiceDefinition(data);
            case "weightedSwitch" -> buildWeightedSwitchDraftChoiceDefinition(data);
            case "multipleCardPick" -> buildMultipleCardPickDraftChoiceDefinition(data);
            case "randomSwitch" -> buildRandomSwitchDraftChoiceDefinition(data);
            case "filterPick" -> buildFilterPickDraftChoiceDefinition(data);
            case "draftPoolFilterPick" -> buildDraftPoolFilterPickDraftChoiceDefinition(data);
            case "draftPoolFilterPluck" -> buildDraftPoolFilterPluckDraftChoiceDefinition(data);
            default -> throw new RuntimeException("Unknown choiceDefinitionType: " + choiceDefinitionType);
        };
    }

    private DraftChoiceDefinition buildFilterPickDraftChoiceDefinition(JSONObject data) {
        final int optionCount = ((Number) data.get("optionCount")).intValue();
        String filter = ((String) data.get("filter")).replace(" ","|");

        Iterable<GenericCardItem> items = _collectionsManager.getCompleteCardCollection().getAll();

        final List<GenericCardItem> possibleCards = _sortAndFilterCards.process(filter, items, _cardLibrary, _formatLibrary);

        return new DraftChoiceDefinition() {
            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, DefaultCardCollection draftPool) {
                final List<GenericCardItem> cards = getCards(seed, stage);

                List<SoloDraft.DraftChoice> draftChoices = new ArrayList<>(optionCount);
                for (int i = 0; i < Math.min(optionCount, possibleCards.size()); i++) {
                    final int finalI = i;
                    draftChoices.add(
                            new SoloDraft.DraftChoice() {
                                @Override
                                public String getChoiceId() {
                                    return cards.get(finalI).getBlueprintId();
                                }

                                @Override
                                public String getBlueprintId() {
                                    return cards.get(finalI).getBlueprintId();
                                }

                                @Override
                                public String getChoiceUrl() {
                                    return null;
                                }
                            });
                }
                return draftChoices;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage) {
                DefaultCardCollection cardCollection = new DefaultCardCollection();
                cardCollection.addItem(choiceId, 1);
                return cardCollection;
            }

            private List<GenericCardItem> getCards(long seed, int stage) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                float thisFixesRandomnessForSomeReason = rnd.nextInt();
                final List<GenericCardItem> cards = new ArrayList<>(possibleCards);
                Collections.shuffle(cards, rnd);
                return cards;
            }
        };
    }

    private DraftChoiceDefinition buildSingleCollectionPickDraftChoiceDefinition(JSONObject data) {
        JSONArray switchResult = (JSONArray) data.get("possiblePicks");

        final Map<String, List<String>> cardsMap = new HashMap<>();
        final List<SoloDraft.DraftChoice> draftChoices = new ArrayList<>();

        for (JSONObject pickDefinition : (Iterable<JSONObject>) switchResult) {
            final String choiceId = (String) pickDefinition.get("choiceId");
            final String url = (String) pickDefinition.get("url");
            JSONArray cards = (JSONArray) pickDefinition.get("cards");

            List<String> cardIds = new ArrayList<>();
            for (String card : (Iterable<String>) cards)
                cardIds.add(card);

            draftChoices.add(
                    new SoloDraft.DraftChoice() {
                        @Override
                        public String getChoiceId() {
                            return choiceId;
                        }

                        @Override
                        public String getBlueprintId() {
                            return null;
                        }

                        @Override
                        public String getChoiceUrl() {
                            return url;
                        }
                    });
            cardsMap.put(choiceId, cardIds);
        }

        return new DraftChoiceDefinition() {
            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, DefaultCardCollection draftPool) {
                return draftChoices;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage) {
                List<String> cardIds = cardsMap.get(choiceId);
                DefaultCardCollection cardCollection = new DefaultCardCollection();
                if (cardIds != null)
                    for (String cardId : cardIds)
                        cardCollection.addItem(cardId, 1);

                return cardCollection;
            }
        };
    }

    private DraftChoiceDefinition buildMultipleCardPickDraftChoiceDefinition(JSONObject data) {
        final int count = ((Number) data.get("count")).intValue();
        JSONArray availableCards = (JSONArray) data.get("availableCards");

        final List<String> cards = new ArrayList<>();
        for (String availableCard : (Iterable<String>) availableCards)
            cards.add(availableCard);

        return new DraftChoiceDefinition() {
            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, DefaultCardCollection draftPool) {
                final List<String> shuffledCards = getShuffledCards(seed, stage);

                List<SoloDraft.DraftChoice> draftableCards = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    final int finalI = i;
                    draftableCards.add(
                            new SoloDraft.DraftChoice() {
                                @Override
                                public String getChoiceId() {
                                    return shuffledCards.get(finalI);
                                }

                                @Override
                                public String getBlueprintId() {
                                    return shuffledCards.get(finalI);
                                }

                                @Override
                                public String getChoiceUrl() {
                                    return null;
                                }
                            });
                }
                return draftableCards;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage) {
                List<String> shuffledCards = getShuffledCards(seed, stage);

                for (int i = 0; i < count; i++) {
                    if (shuffledCards.get(i).equals(choiceId)) {
                        DefaultCardCollection result = new DefaultCardCollection();
                        result.addItem(choiceId, 1);
                        return result;
                    }
                }

                return new DefaultCardCollection();
            }

            private List<String> getShuffledCards(long seed, int stage) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                float thisFixesRandomnessForSomeReason = rnd.nextFloat();
                final List<String> shuffledCards = new ArrayList<>(cards);
                Collections.shuffle(shuffledCards, rnd);
                return shuffledCards;
            }
        };
    }
    
    private DraftChoiceDefinition buildDraftPoolFilterPickDraftChoiceDefinition(JSONObject data) {
        final int optionCount = ((Number) data.get("optionCount")).intValue();
        String filter = (String) data.get("filter");

        return new DraftChoiceDefinition() {
            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, DefaultCardCollection draftPool) {
                
                List<GenericCardItem> possibleCards = _sortAndFilterCards.process(filter, draftPool.getAll(), _cardLibrary, _formatLibrary);

                final List<GenericCardItem> cards = getCards(seed, stage, possibleCards);

                List<SoloDraft.DraftChoice> draftChoices = new ArrayList<>(optionCount);
                for (int i = 0; i < Math.min(optionCount, possibleCards.size()); i++) {
                    final int finalI = i;
                    draftChoices.add(
                            new SoloDraft.DraftChoice() {
                                @Override
                                public String getChoiceId() {
                                    return cards.get(finalI).getBlueprintId();
                                }

                                @Override
                                public String getBlueprintId() {
                                    return cards.get(finalI).getBlueprintId();
                                }

                                @Override
                                public String getChoiceUrl() {
                                    return null;
                                }
                            });
                }
                return draftChoices;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage) {
                DefaultCardCollection cardCollection = new DefaultCardCollection();
                cardCollection.addItem(choiceId, 1);
                return cardCollection;
            }

            private List<GenericCardItem> getCards(long seed, int stage, List<GenericCardItem> possibleCards) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                rnd.nextInt();
                Collections.shuffle(possibleCards, rnd);
                return possibleCards;
            }
        };
    }
    
    private DraftChoiceDefinition buildDraftPoolFilterPluckDraftChoiceDefinition(JSONObject data) {
        final int optionCount = ((Number) data.get("optionCount")).intValue();
        String filter = (String) data.get("filter");

        return new DraftChoiceDefinition() {
            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, DefaultCardCollection draftPool) {
                List<GenericCardItem> fullDraftPool = new ArrayList<>();
                for (GenericCardItem item : draftPool.getAll())
                    for (int i = 0; i < draftPool.getItemCount(item.getBlueprintId()); i++)
                        fullDraftPool.add(item);

                List<GenericCardItem> possibleCards = _sortAndFilterCards.process(filter, fullDraftPool, _cardLibrary, _formatLibrary);

                final List<GenericCardItem> cards = getCards(seed, stage, possibleCards);

                List<SoloDraft.DraftChoice> draftChoices = new ArrayList<>(optionCount);
                for (int i = 0; i < Math.min(optionCount, possibleCards.size()); i++) {
                    draftPool.removeItem(cards.get(i).getBlueprintId(),1);
                    final int finalI = i;
                    draftChoices.add(
                            new SoloDraft.DraftChoice() {
                                @Override
                                public String getChoiceId() {
                                    return cards.get(finalI).getBlueprintId();
                                }

                                @Override
                                public String getBlueprintId() {
                                    return cards.get(finalI).getBlueprintId();
                                }

                                @Override
                                public String getChoiceUrl() {
                                    return null;
                                }
                            });
                }
                return draftChoices;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage) {
                DefaultCardCollection cardCollection = new DefaultCardCollection();
                cardCollection.addItem(choiceId, 1);
                return cardCollection;
            }

            private List<GenericCardItem> getCards(long seed, int stage, List<GenericCardItem> possibleCards) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                rnd.nextInt();
                Collections.shuffle(possibleCards, rnd);
                return possibleCards;
            }
        };
    }

    private DraftChoiceDefinition buildRandomSwitchDraftChoiceDefinition(JSONObject data) {
        JSONArray switchResult = (JSONArray) data.get("switchResult");

        final List<DraftChoiceDefinition> draftChoiceDefinitionList = new ArrayList<>();
        for (JSONObject switchResultObject : (Iterable<JSONObject>) switchResult)
            draftChoiceDefinitionList.add(constructDraftChoiceDefinition(switchResultObject));

        return new DraftChoiceDefinition() {
            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, DefaultCardCollection draftPool) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                float thisFixesRandomnessForSomeReason = rnd.nextFloat();
                return draftChoiceDefinitionList.get(rnd.nextInt(draftChoiceDefinitionList.size())).getDraftChoice(seed, stage, draftPool);
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                float thisFixesRandomnessForSomeReason = rnd.nextFloat();
                return draftChoiceDefinitionList.get(rnd.nextInt(draftChoiceDefinitionList.size())).getCardsForChoiceId(choiceId, seed, stage);
            }
        };
    }

    private DraftChoiceDefinition buildWeightedSwitchDraftChoiceDefinition(JSONObject data) {
        JSONArray switchResult = (JSONArray) data.get("switchResult");

        final Map<Float, DraftChoiceDefinition> draftChoiceDefinitionMap = new LinkedHashMap<>();
        float weightTotal = 0;
        for (JSONObject switchResultObject : (Iterable<JSONObject>) switchResult) {
            float weight = ((Number) switchResultObject.get("weight")).floatValue();
            weightTotal += weight;
            draftChoiceDefinitionMap.put(weightTotal, constructDraftChoiceDefinition(switchResultObject));
        }

        return new DraftChoiceDefinition() {
            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, DefaultCardCollection draftPool) {
                Random rnd = getRandom(seed, stage);
                float result = rnd.nextFloat();
                for (Map.Entry<Float, DraftChoiceDefinition> weightEntry : draftChoiceDefinitionMap.entrySet()) {
                    if (result < weightEntry.getKey())
                        return weightEntry.getValue().getDraftChoice(seed, stage, draftPool);
                }

                return null;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage) {
                Random rnd = getRandom(seed, stage);
                float result = rnd.nextFloat();
                for (Map.Entry<Float, DraftChoiceDefinition> weightEntry : draftChoiceDefinitionMap.entrySet()) {
                    if (result < weightEntry.getKey())
                        return weightEntry.getValue().getCardsForChoiceId(choiceId, seed, stage);
                }

                return null;
            }
        };
    }

    private Random getRandom(long seed, int stage) {
        return new Random(seed + (long) stage * HIGH_ENOUGH_PRIME_NUMBER);
    }
}
