package org.rommert.trie.impl;


import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;


@SuppressWarnings("SpellCheckingInspection")
public class NodeTest {

    private Node<Integer> root;

    @Before
    public void setup() {
        root = new Node<>();
    }

    @Test
    public void testNode() {
        Node<Integer> root = new Node<>();
        root.insert("vos", 13);
        System.out.println(root.toString());
        assertEquals(1, root.getNrOfValueNodes());
    }

    @Test
    public void testMultipleWords() {
        Node<Integer> root = new Node<>();
        root.insert("vos", 13);
        root.insert("beer", 14);
        root.insert("voordeur", 21);
        root.insert("voornamelijk", 23);

        System.out.println(root.toString());
        assertEquals(4, root.getNrOfValueNodes());
    }

    @Test
    public void testMultipleOccurrencesOfSameWord() {
        root.insert("voornamelijk", 23);
        root.insert("voornamelijk", 24);
        root.insert("voornemens", 31);
        root.insert("voornamelijk", 53);
        root.insert("voornamelijk", 274);
        root.insert("voornemens", 3);

        System.out.println(root.toString());
        assertEquals(2, root.getNrOfValueNodes());
    }

    @Test
    public void testSearch() {
        root.insert("vos", 14);
        root.insert("vos", 23);
        List<Integer> expected = Arrays.asList(14, 23);
        List<Integer> actual = root.search("vos");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchInComplexTree() {
        root.insert("vos", 14);
        root.insert("voordelig", 23);
        root.insert("beer", 5);
        root.insert("beroepsmilitair", 442);
        List<Integer> expected = Arrays.asList(23);
        List<Integer> actual = root.search("voordelig");
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchNonExistingWord() {
        root.insert("vos", 14);
        List<Integer> expected = Collections.emptyList();
        List<Integer> actual = root.search("beer");
        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteWord() {
        root.insert("voornamelijk", 23);
        root.insert("vos", 7);
        root.insert("voornemens", 3);

        System.out.println(root.toString());
        assertEquals(3, root.getNrOfValueNodes());

        root.delete("voornemens");

        System.out.println(root.toString());
        assertEquals(2, root.getNrOfValueNodes());
    }

    @Test
    public void testFirstWordContainsSecondWord() {
        root.insert("luie", 14);
        root.insert("lui", 23);

        System.out.println(root.toString());
        assertEquals(2, root.getNrOfValueNodes());
    }

    @Test
    public void testSecondWordContainsFirstWord() {
        root.insert("de", 14);
        root.insert("deze", 23);

        System.out.println(root.toString());
        assertEquals(2, root.getNrOfValueNodes());
    }

    @Test
    public void testMultiBranchedTree() {
        root.insert("werk", 14);
        root.insert("werken", 5); // branch on e
        root.insert("werkelijk", 23); // branch on l
        root.insert("werkeloos", 183);

        System.out.println(root.toString());
    }

    @Test
    public void testDeleteFromRoot() {
        root.insert("weinig", 123);
        root.delete("weinig");
        assertEquals(0, root.getNrOfValueNodes());
    }

    @Test
    public void testDeleteNonExistingLeaf() {
        root.insert("weinig", 123);
        root.delete("veel");
        assertEquals(1, root.getNrOfValueNodes());
    }

    @Test
    public void testDeleteLongerWordFirst() {
        root.insert("stof", 123);
        root.insert("stoffig", 45);
        root.delete("stoffig");
        System.out.println(root.toString());
        assertEquals(1, root.getNrOfValueNodes());
    }

    @Test
    public void testDeleteShorterWordFirst() {
        root.insert("stof", 123);
        root.insert("stoffig", 45);
        root.delete("stof");
        System.out.println(root.toString());
        assertEquals(1, root.getNrOfValueNodes());
    }

    @Test
    public void testDeleteFromBranchingRoot() {
        root.insert("stof", 123);
        root.insert("stoffig", 45);
        root.insert("storing", 3);
        root.delete("stoffig");
        System.out.println(root.toString());
        assertEquals(2, root.getNrOfValueNodes());
    }

    @Test
    public void testDeleteFromChain() {
        root.insert("la", 14);
        root.insert("lak", 3); // branch on e
        root.insert("laks", 73); // branch on l
        root.insert("lakschade", 23);

        System.out.println(root.toString());

        root.delete("laks");
        System.out.println(root.toString());
    }

    @Test
    public void testRemoveWordThatLeavesASingleNodeWithMultipleChildren() {
        root.insert("verder", 123);
        root.insert("verhaal", 45);
        root.insert("verdraaid", 3);
        root.delete("verhaal");
        System.out.println(root.toString());
    }

    @Test
    public void testAddAndRemoveFullStory() {
        String story = "" +
                "Dit is een heel lang verhaal over een vos die vrolijk over een luie hond springt. Wat de hond niet weet is dat deze " +
                "dat deze vos snel en bruin is, maar dat de hond zelf lui is. De hond trekt zich er weinig van aan zoals een " +
                "luie hond betaamd. Maar die vos voelt zich een partijtje goed joh, die komt thuis en zegt tegen zn vrouw dat hij " +
                "echt iets heel stoers heeft gedaan. De vrouw is niet bijster onder de indruk: hij is te laat voor het eten " +
                "en behalve een goed verhaal heeft hij verder verdraaid weinig meegebracht. Einde.";

        String standardized = story.replaceAll("[^a-zA-Z ]", "").toLowerCase();
        String[] words = standardized.split(" ");
        int position = 0;
        Set<String> allDistinctWords = new HashSet<>();

        for (String word : words) {
            root.insert(word, position);
            allDistinctWords.add(word);
            position++;
        }

        //expect a full tree. Expect the nr of leafs to reflect the number of unique words
        System.out.println(root.toString());
        assertEquals(allDistinctWords.size(), root.getNrOfValueNodes());

        // delete all words, at random
        List<String> shuffeled = new ArrayList<>(allDistinctWords);
        Collections.shuffle(shuffeled);
        shuffeled.forEach(root::delete);

        // expect an empty root node
        System.out.println(root.toString());
        assertEquals(0, root.getNrOfValueNodes());
    }

    @Test
    public void testToDOTString() {
        String story = "" +
                "Dit is een heel lang verhaal over een vos die vrolijk over een luie hond springt. Wat de hond niet weet is dat deze " +
                "dat deze vos snel en bruin is, maar dat de hond zelf lui is. De hond trekt zich er weinig van aan zoals een " +
                "luie hond betaamd. Maar die vos voelt zich een partijtje goed joh, die komt thuis en zegt tegen zn vrouw dat hij " +
                "echt iets heel stoers heeft gedaan. De vrouw is niet bijster onder de indruk: hij is te laat voor het eten " +
                "en behalve een goed verhaal heeft hij verder verdraaid weinig meegebracht. Einde.";

        String standardized = story.replaceAll("[^a-zA-Z ]", "").toLowerCase();
        String[] words = standardized.split(" ");
        int position = 0;

        for (String word : words) {
            root.insert(word, position);
            position++;
        }

        System.out.println(root.toDOTString());
    }
}