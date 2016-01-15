package org.rommert.trie.interfaces;

import java.util.List;

/**
 * Interface with the default methods of the trie
 * @author Evert Duipmans
 *
 * @param <D> Data type that can be stored in the trie-nodes
 */
public interface Trie<D> {
    /**
     * Add a word (and dataobject) to the trie
     * @param word Word to add
     * @param data Data element that should be stored in the trie node
     */
    void insert(String word, D data);

    /**
     * Search for a word in the trie and get all the Data elements that are stored in the particular node
     * @param word Word to search
     * @return List with Data items
     */
    List<D> search(String word);

    /**
     * Delete a word (and all its data objects) from the trie
     * @param word Word to delete
     */
    void delete(String word);

    /**
     * Converts the Trie structure into a graphviz string, so that it can be visualized using Graphviz
     * See: https://en.wikipedia.org/wiki/DOT_(graph_description_language)
     * Render online:
     * - http://www.webgraphviz.com/
     * - http://sandbox.kidstrythisathome.com/erdos/
     * @return DOT string representation of the trie
     */
    String toDOTString();
}