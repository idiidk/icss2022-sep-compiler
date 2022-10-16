package nl.han.ica.datastructures;

import java.util.HashMap;

public class SymbolTable<K, V> {
    private IHANLinkedList<HashMap<K, V>> scopes = new HANLinkedList<>();

    public void pushScope() {
        scopes.addFirst(new HashMap<>());
    }

    public void popScope() {
        scopes.removeFirst();
    }

    public void putVariable(K key, V value) {
        scopes.getFirst().put(key, value);
    }

    public V getVariable(K key) {
        for (int i = 0; i < scopes.getSize(); i++) {
            HashMap symbolMap = scopes.get(i);
            V result = (V) symbolMap.get(key);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public int getSize() {
        return scopes.getSize();
    }
}
