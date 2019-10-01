package regextodfa;

import java.util.*;

/**
 *
 * @author juanarellano
 */
class BinaryTree {

    private int leafNodeID = 0;
    private Stack<Node> stackNode = new Stack<>();
    private Stack<Character> operator = new Stack<Character>();
    private Set<Character> input = new HashSet<Character>();
    private ArrayList<Character> operators = new ArrayList<>();

    public Node generateTree(String regex) {
        operators.add('*');
        operators.add('|');
        operators.add('$');

        Character chars[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'Ñ', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z'};
        for (int i = 0; i < chars.length; i++) {
            input.add(chars[i]);
            input.add(Character.toLowerCase(chars[i]));
        }

        Character numsSyms[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                '#', '=', '_', '.', '*', '/', '+', '-', ' ', '(', ')', ' '};

        for (int i = 0; i < numsSyms.length; i++) {
            input.add(numsSyms[i]);
        }

        regex = AddConcatenationSymbol(regex);
        System.out.println(regex);

        stackNode.clear();
        operator.clear();

        for (int i = 0; i < regex.length(); i++) {
            if (isCharInput(regex.charAt(i)) || regex.charAt(i) == '&') {
                pushStack(Character.toString(regex.charAt(i)));
            } else if (operator.isEmpty()) {
                operator.push(regex.charAt(i));

            } else if (regex.charAt(i) == '(') {
                operator.push(regex.charAt(i));

            } else if (regex.charAt(i) == ')') {
                while (operator.get(operator.size() - 1) != '(') {
                    operate();
                }
                operator.pop();
            } else {
                while (!operator.isEmpty() && OrderOfPrecedence(regex.charAt(i), operator.get(operator.size() - 1))) {
                    operate();
                }
                operator.push(regex.charAt(i));
            }
        }
        while (!operator.isEmpty()) {
            operate();
        }

        Node completeTree = stackNode.pop();
        return completeTree;
    }

    private boolean OrderOfPrecedence(char first, Character second) {
        if (first == second) {
            return true;
        }
        if (first == '*') {
            return false;
        }
        if (second == '*') {
            return true;
        }
        if (first == '$') {
            return false;
        }
        if (second == '$') {
            return true;
        }
        if (first == '|') {
            return false;
        }
        return true;
    }

    private void operate() {
        if (this.operator.size() > 0) {
            char pos = operator.pop();

            switch (pos) {
                case ('$'):
                    concat();
                    break;
                case ('*'):
                    noneOrMore();
                    break;
                case ('|'):
                    or();
                    break;
                default:
                    System.out.println("Simbolo desconocido: " + pos);
                    //System.exit(1);
                    break;
            }
        }
    }

    private void noneOrMore() {
        Node node = stackNode.pop();

        Node root = new Node("*");
        root.setLeft(node);
        root.setRight(null);
        node.setParent(root);

        stackNode.push(root);
    }

    private void concat() {
        Node node2 = stackNode.pop();
        Node node1 = stackNode.pop();

        Node root = new Node("$");
        root.setLeft(node1);
        root.setRight(node2);
        node1.setParent(root);
        node2.setParent(root);

        stackNode.push(root);
    }

    private void or() {
        Node node2 = stackNode.pop();
        Node node1 = stackNode.pop();

        Node root = new Node("|");
        root.setLeft(node1);
        root.setRight(node2);
        node1.setParent(root);
        node2.setParent(root);

        stackNode.push(root);
    }

    private void pushStack(String symbol) {
        Node node = new LeafNode(symbol, ++leafNodeID);
        stackNode.push(node);
    }

    private String AddConcatenationSymbol(String regex) {
        String regEx = new String("");

        for (int i = 0; i < regex.length() - 1; i++) {
            if ((isCharInput( regex.charAt(i) ) || regex.charAt(i+1) == '&' ) && isCharInput( regex.charAt(i + 1) )) {
                regEx += regex.charAt(i) + "$";
            } else if (isCharInput(regex.charAt(i)) && regex.charAt(i + 1) == '(') {
                regEx += regex.charAt(i) + "$";
            } else if (regex.charAt(i) == ')' && isCharInput(regex.charAt(i + 1))) {
                regEx += regex.charAt(i) + "$";
            } else if (regex.charAt(i) == '*' && isCharInput(regex.charAt(i + 1))) {
                regEx += regex.charAt(i) + "$";
            } else if (regex.charAt(i) == '*' && regex.charAt(i + 1) == '(') {
                regEx += regex.charAt(i) + "$";
            } else if (regex.charAt(i) == ')' && regex.charAt(i + 1) == '(') {
                regEx += regex.charAt(i) + "$";
            } else {
                regEx += regex.charAt(i);
            }
        }
        regEx += regex.charAt(regex.length() - 1);
        return regEx;
    }

    private boolean isCharInput(char charAt) {
        if (operators.contains(charAt)) {
            return false;
        }
        for (Character c : input) {
            if ((char) c == charAt && charAt != '(' && charAt != ')') {
                return true;
            }
        }
        return false;
    }

    /* This method is here just to test buildTree() */
    public void printInorder(Node node) {
        if (node == null) {
            return;
        }

        /* first recur on left child */
        printInorder(node.getLeft());

        /* then print the data of node */
        System.out.print(node.getSymbol() + " ");

        /* now recur on right child */
        printInorder(node.getRight());
    }

    public int getNumberOfLeafs() {
        return leafNodeID;
    }

}