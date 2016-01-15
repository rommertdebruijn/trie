package org.rommert.trie;

import org.rommert.trie.impl.Node;

public class Indexer {

    private Node<Integer> node;

    public static void main( String[] args ) {
        Indexer indexer = new Indexer();
        indexer.indexText(indexer);
    }

    private void indexText(Indexer indexer) {
        String textInput = "" +
                "Dit is een heel lang verhaal over een vos die vrolijk over een luie hond springt. Wat de hond niet weet is " +
                "dat deze vos snel en bruin is, maar dat de hond zelf lui is. De hond trekt zich er weinig van aan zoals een " +
                "luie hond betaamd. Maar die vos voelt zich een partijtje goed joh, die komt thuis en zegt tegen zn vrouw dat hij " +
                "echt iets heel stoers heeft gedaan. De vrouw is niet bijster onder de indruk: hij is te laat voor het eten " +
                "en behalve een goed verhaal heeft hij verder verdraaid weinig meegebracht. Einde.";

        indexer.index(textInput);
        System.out.println(node.toDOTString());
    }

    public void index(String textInput) {
        String standardized = textInput.replaceAll("[^a-zA-Z ]", "").toLowerCase();
        String[] words = standardized.split(" ");
        node = new Node<>();
        int wordCounter = 0;
        for (String word : words) {
            node.insert(word, wordCounter);
            wordCounter++;
        }
    }
}
