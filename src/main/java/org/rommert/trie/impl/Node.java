package org.rommert.trie.impl;

import org.rommert.trie.interfaces.Trie;

import java.util.*;
import java.util.stream.Collectors;

public class Node<T> implements Trie<T> {

    private static final String DOT_ROOT__NODE_NAME = "_";

    private String name;
    private List<T> values = new ArrayList<>();
    private Node<T> parent;
    private Map<String, Node<T>> children = new HashMap<>();

    // create a root node with no name and null parent
    public Node() {
    }

    // for internal use only
    private Node(String name) {
        this.name = name;
    }

    // for internal use only
    private Node(String name, List<T> values) {
        this.name = name;
        this.values.addAll(values);
    }

    public void insert(String word, T value) {
       insert(word, Collections.singletonList(value));
    }

    private void insert(String word, List<T> newValues) {
        Optional<Node<T>> childMatchingWordOpt = getChildMatchingString(word);
        // exact match with existing leaf node: add new values to list with existing values
        if (childMatchingWordOpt.isPresent()) {
            childMatchingWordOpt.get().getValues().addAll(newValues);
            return;
        }

        // an intermediate node exists that's equal to the first letter of w (if this node was a leaf it would match the first check)
        String firstLetter = head(word);
        Optional<Node<T>> childMatchingFirstLetter = getChildMatchingString(firstLetter);
        if (childMatchingFirstLetter.isPresent()) {
            Node<T> childMatchingLetter = childMatchingFirstLetter.get();
            if (childMatchingLetter.isLeaf()) {
                addNode(word, newValues);
            } else {
                childMatchingLetter.insert(tail(word), newValues);
            }
            return;
        }

        // a leaf node exists that starts with the first letter of w but is not equal to w (if this node was equal to w it would match the first check)
        Optional<Node<T>> leafOpt = getLeafStartingWithLetter(firstLetter);
        if (leafOpt.isPresent()) {
            if (word.length() == 1) {
                addNode(word, newValues);
            } else {
                Node<T> leaf = leafOpt.get();
                Node<T> intermediate = addNode(firstLetter);
                intermediate.insert(tail(leaf.getName()), leaf.getValues());
                intermediate.insert(tail(word), newValues);
                drop(leaf);
            }
            return;
        }

        // final option since there is no match: add new name as leaf
        addNode(word, newValues);
    }


    public List<T> search(String word) {
        Optional<Node<T>> childMatchingWord = getChildMatchingString(word);
        if (childMatchingWord.isPresent()) {
            return childMatchingWord.get().getValues();
        }

        String firstLetter = head(word);
        Optional<Node<T>> childMatchingFirstLetter = getChildMatchingString(firstLetter);
        if (childMatchingFirstLetter.isPresent()) {
            return childMatchingFirstLetter.get().search(tail(word));
        }
        return new ArrayList<>();
    }

    public void delete(String word) {
        // when a node is found that matches the given word, remove that node. Then, tidy up the tree
        Optional<Node<T>> childMatchingWord = getChildMatchingString(word);
        if (childMatchingWord.isPresent()) {
            drop(childMatchingWord.get());
            collapse();
            return;
        }

        // when a node is found that starts with the first letter of w, delete the tail of w from that node
        String firstLetter = head(word);
        Optional<Node<T>> childMatchingFirstLetter = getChildMatchingString(firstLetter);
        if (childMatchingFirstLetter.isPresent()) {
            childMatchingFirstLetter.get().delete(tail(word));
        }
    }

    @Override
    public String toDOTString() {
        return "digraph G\n{\n" + "_[label=\"\"]\n" + toDOTString(DOT_ROOT__NODE_NAME) + "}";
    }

    /**
     * Recursive toDOTString
     * @param nodeName is used to give each child node a unique name
     * @return the string representation
     */
    private String toDOTString(String nodeName) {
        StringBuilder dot = new StringBuilder();

        getSortedChildren().forEach(child -> {
            String childNodeName = nodeName + child.getName();
            dot.append(nodeName).append(" -> ").append(childNodeName).append("\n");
            // add label to node
            dot.append(childNodeName).append("[label=\"").append(child.getName());
            if (child.isLeaf()) {
                dot.append(child.getValues());
            }
            dot.append("\"]\n");
            dot.append(child.toDOTString(childNodeName));
        });
        return dot.toString();
    }

    private List<Node<T>> getSortedChildren() {
        return children.values().stream()
                .sorted((Node<T> c1, Node<T> c2) -> c1.getName().compareTo(c2.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Collapses combines of single nodes into a single node. So [root]-[a]-[b]-[c]-[d] and [root]-[a]-[bcd]
     * will both collapse into [root]-[abcd]. Collapsing stops at root, or when a node with multiple children
     * is encountered
     */
    private void collapse() {
        if (parent == null || getNrOfLeafs() > 1) { //cant collapse top of tree, or tree that has multiple leafs
            return;
        }

        Node<T> onlyChild = new ArrayList<>(children.values()).get(0); // last child
        String newName = name + onlyChild.getName();
        parent.addNode(newName, onlyChild.getValues());
        parent.drop(this);
        parent.collapse();
    }

    private void drop(Node<T> leaf) {
        children.remove(leaf.getName());
    }

    private Optional<Node<T>> getChildMatchingString(String word) {
        return Optional.ofNullable(children.get(word));
    }

    private Optional<Node<T>> getLeafStartingWithLetter(String firstLetter) {
        List<Node<T>> sortedChildren = getSortedChildren();
        return sortedChildren.stream()
                .filter(child -> child.getName().startsWith(firstLetter) && child.isLeaf())
                .findFirst();
    }

    private String tail(String word) {
        return word.substring(1);
    }

    private String head(String name) {
        return name.substring(0, 1);
    }

    private Node<T> addNode(String name) {
        Node<T> node = new Node<>(name);
        node.setParent(this);
        children.put(node.getName(), node);
        return node;
    }

    private Node<T> addNode(String name, List<T> values) {
        Node<T> node = new Node<>(name, values);
        node.parent = this;
        children.put(node.getName(), node);
        return node;
    }

    private boolean isLeaf() {
        return !values.isEmpty() && children.size() == 0;
    }

    protected int getNrOfLeafs() {
        return children.values().stream()
                .mapToInt(child -> child.isLeaf() ? 1 : child.getNrOfLeafs())
                .sum();
    }

    protected String getName() {
        return name;
    }

    private List<T> getValues() {
        return values;
    }

    private void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public String toString() {
        return toString(0);
    }

    /**
     * Recursive toString
     * @param level is used for indenting
     * @return the string representation
     */
    public String toString(int level) {
        StringBuilder output = new StringBuilder();
        for (int i=0;i<level;i++) {
            output.append("    ");
        }
        if (children.isEmpty()) {
            output.append(name)
                    .append(":")
                    .append(values);
        } else {
            output.append(name).append(":\n");
            List<Node<T>> sortedChildren = getSortedChildren();
            for (Node<T> node : sortedChildren) {
                output.append(node.toString(level + 1));
                output.append("\n");
            }
        }
        return output.toString();
    }
}
