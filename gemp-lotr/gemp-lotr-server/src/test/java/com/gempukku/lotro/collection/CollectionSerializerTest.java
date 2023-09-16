package com.gempukku.lotro.collection;

import com.gempukku.lotro.game.CardCollection;
import com.gempukku.lotro.game.DefaultCardCollection;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CollectionSerializerTest {
    private final CollectionSerializer _serializer = new CollectionSerializer();

    private CardCollection serializeAndDeserialize(DefaultCardCollection collection) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        _serializer.serializeCollection(collection, baos);

        final byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return _serializer.deserializeCollection(bais);
    }

    @Test
    public void testLotsOfCurrency() throws IOException {
        DefaultCardCollection collection = new DefaultCardCollection();
        collection.addCurrency(127 * 255);

        assertEquals(127 * 255, serializeAndDeserialize(collection).getCurrency());
    }

}
