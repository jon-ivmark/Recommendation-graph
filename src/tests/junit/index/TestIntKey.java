package tests.junit.index;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Test;

import recng.index.IntKey;
import recng.index.Key;
import recng.index.KeyFactory;

public class TestIntKey {

    private Key<String> getKey(String id) {
        return IntKey.Factory.getInstance().parse(id);
    }

    @Test public void testGetValue() {
        String id = "123";
        Key<String> key = getKey(id);
        assertEquals(id, key.getValue());
    }

    @Test public void testEquals() {
        String id1 = "1";
        Key<String> key1 = getKey(id1);
        String id2 = "2";
        Key<String> key2 = getKey(id2);

        assertEquals(key1, getKey(id1));
        assertEquals(getKey(id1), key1);
        assertFalse(key1.equals(key2));

        Map<Key<String>, String> map = new HashMap<Key<String>, String>();
        map.put(key1, id1);
        assertEquals(map.get(key1), id1);
    }

    @Test public void testInvalidId() {
        KeyFactory<String> factory = IntKey.Factory.getInstance();
        String id = "abc2";
        assertFalse(factory.matches(id));
        boolean exception = false;
        try {
            factory.parse(id);
        } catch (Key.KeyFormatException e) {
            exception = true;
        }
        assertTrue(exception);

        id = "00123";
        assertFalse(factory.matches(id));
    }
}
