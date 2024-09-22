package com.gempukku.stccg.packs;

import com.gempukku.stccg.cards.CardBlueprintLibrary;
import com.gempukku.stccg.collection.PackBox;
import com.gempukku.stccg.collection.RarityPackBox;
import com.gempukku.stccg.common.AppConfig;
import com.gempukku.stccg.common.JSONDefs;
import com.gempukku.stccg.common.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Semaphore;


public class ProductLibrary {
    private final Map<String, PackBox> _products = new HashMap<>();
    private final CardBlueprintLibrary _cardLibrary;
    private final File _packDirectory;
    private static final Logger LOGGER = LogManager.getLogger(ProductLibrary.class);

    private final Semaphore collectionReady = new Semaphore(1);

    public ProductLibrary(CardBlueprintLibrary cardLibrary) {
        this(cardLibrary, AppConfig.getProductPath());
    }
    public ProductLibrary(CardBlueprintLibrary cardLibrary, File packDefinitionDirectory) {
        _cardLibrary = cardLibrary;
        _packDirectory = packDefinitionDirectory;

        collectionReady.acquireUninterruptibly();
        loadPacks(_packDirectory);
        collectionReady.release();
    }

    public void ReloadPacks() {
        try {
            collectionReady.acquire();
            loadPacks(_packDirectory);
            collectionReady.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadPacks(File path) {
        if (path.isFile()) {
            loadPackFromFile(path);
        }
        else if (path.isDirectory()) {
            for (File file : Objects.requireNonNull(path.listFiles())) {
                loadPacks(file);
            }
        }
    }

    private void loadPackFromFile(File file) {
        if (JsonUtils.IsInvalidHjsonFile(file))
            return;
        JSONParser parser = new JSONParser();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            var defs = JsonUtils.ConvertArray(reader, JSONDefs.Pack.class);

            if(defs == null)
            {
                var def= JsonUtils.Convert(reader, JSONDefs.Pack.class);
                if(def != null)
                {
                    defs = new ArrayList<>();
                    defs.add(def);
                }
                else {
                    System.out.println(file + " is not a PackDefinition nor an array of PackDefinitions.  Could not load from file.");
                    return;
                }
            }

            for (var def : defs) {
                LOGGER.debug("Loading pack definitions for " + def.Name);

                PackBox result = null;
                String[] rarities;
                String[] sets;
                switch (def.Type) {
                    case RANDOM -> {
                        if (def.Items == null || def.Items.isEmpty())
                            continue;
                        if (def.Items.stream().anyMatch(x -> x.contains("%"))) {
                            result = WeightedRandomPack.LoadFromArray(def.Items);
                        } else {
                            result = UnweightedRandomPack.LoadFromArray(def.Items);
                        }
                    }
                    case RANDOM_FOIL -> {
                        if (def.Data == null || !def.Data.containsKey("rarities") || !def.Data.containsKey("sets")) {
                            System.out.println(def.Name + " RANDOM_FOIL pack type must contain a definition for 'rarities' and 'sets' within data.");
                            continue;
                        }
                        rarities = def.Data.get("rarities").toUpperCase().split("\\s*,\\s*");
                        sets = def.Data.get("sets").split("\\s*,\\s*");
                        result = new RandomFoilPack(rarities, sets, _cardLibrary);
                    }
                    case BOOSTER -> {
                        if (def.Data == null || !def.Data.containsKey("set")) {
                            System.out.println(def.Name + " BOOSTER pack type must contain a definition for 'set' within data.");
                            continue;
                        }
                        if (def.Data.get("set").contains(",")) {
                            System.out.println(def.Name + " BOOSTER pack type must define exactly one set.");
                            continue;
                        }
                        String set = def.Data.get("set").trim();
                        if (set.equals("9")) {
                            result = new ReflectionsPackBox(_cardLibrary);
                        } else {
                            result = new RarityPackBox(_cardLibrary.getSetDefinitions().get(set));
                        }
                    }
                    case PACK, SELECTION -> {
                        if (def.Items == null || def.Items.isEmpty())
                            continue;
                        result = FixedPackBox.LoadFromArray(def.Items, def.Recursive);
                    }
                }
                if(result == null)
                {
                    System.out.println("Unrecognized pack type: " + def.Type);
                    continue;
                }

                if(_products.containsKey(def.Name)) {
                    System.out.println("Overwriting existing pack '" + def.Name + "'!");
                }
                _products.put(def.Name, result);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, PackBox> GetAllProducts() {
        try {
            collectionReady.acquire();
            var data = Collections.unmodifiableMap(_products);
            collectionReady.release();
            return data;
        }
        catch (InterruptedException exp) {
            throw new RuntimeException("ProductLibrary.GetAllProducts() interrupted: ", exp);
        }
    }

    public PackBox GetProduct(String name) {
        try {
            collectionReady.acquire();
            var data = _products.get(name);
            collectionReady.release();
            return data;
        }
        catch (InterruptedException exp) {
            throw new RuntimeException("ProductLibrary.GetProduct() interrupted: ", exp);
        }
    }
}

