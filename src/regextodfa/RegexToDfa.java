package regextodfa;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author juanarellano
 */
public class RegexToDfa {

    private static Set<Integer>[] nextPos;
    private static Node root;
    private static Set<State> DStates;
    private static Set<String> input;
    private static HashMap<Integer, String> regexStates;

    public static void main(String[] args) {
        initialize();
    }

    public static void initialize() {
        Scanner in = new Scanner(System.in);
        DStates = new HashSet<>();
        input = new HashSet<String>();
        regexStates = new HashMap<Integer, String>();
        input = new HashSet<>();
        
        String regex = getRegex(in);
        
        fecthSymbols(regex);

        SyntaxTree st = new SyntaxTree(regex);
        root = st.getRoot(); //root of the syntax tree
        nextPos = st.getFollowPos(); //the followpos of the syntax tree

        /**
         * creating the DFA using the syntax tree were created upside and
         * returning the start state of the resulted DFA
         */
        State q0 = createDFA();
        DfaTraversal dfat = new DfaTraversal(q0, input);
        
        String str = getStr(in);
        boolean acc = false;
        for (char c : str.toCharArray()) {
            if (dfat.setCharacter(c)) {
                acc = dfat.traverse();
            } else {
                System.out.println("WRONG CHARACTER!");
                System.exit(0);
            }
        }
        if (acc) {
            System.out.println((char) 27 + "[32m" + "this string is acceptable by the regex!");
        } else {
            System.out.println((char) 27 + "[31m" + "this string is not acceptable by the regex!");
        }
        in.close();
    }

    private static String getRegex(Scanner in) {
        System.out.print("Ingrese una expresion regular: ");
        String regex = in.nextLine();
        regex = "(" + changeIner(changePlus(regex)) + ")";
        return regex+"#";
    }
    
    public static String changeIner(String regex){
        if(regex.contains("?")){
            while(regex.indexOf("?") != -1){
                int index = regex.indexOf("?");
                if(regex.charAt(index - 1) == ')'){
                    int start = indexOfOpen(regex, index);
                    int end = index;
                    String exp = regex.substring(start, end);
                    String ant = regex.substring(0, start);
                    String post = regex.substring(index + 1);
                    regex = ant + "(" + exp + "|&)" + post;
                }else{
                    int start = index - 1;
                    int end = index;
                    String exp = regex.substring(start, end);
                    String ant = regex.substring(0, start);
                    String post = regex.substring(index + 1);
                    regex = ant + "(" + exp + "|&)" + post;
                }
            }
            return regex;
        }else return regex;
    }
    
    public static String changePlus(String regex){
        if(regex.contains("+")){
            while(regex.indexOf("+") != -1){
                int index = regex.indexOf("+");
                if(regex.charAt(index - 1) == ')'){
                    int start = indexOfOpen(regex, index);
                    int end = index;
                    String exp = regex.substring(start, end);
                    String ant = regex.substring(0, start);
                    String post = regex.substring(index + 1);
                    regex = ant + exp + exp + "*" + post;
                }else{
                    int start = index - 1;
                    int end = index;
                    String exp = regex.substring(start, end);
                    String ant = regex.substring(0, start);
                    String post = regex.substring(index + 1);
                    regex = ant + exp + exp + "*" + post;
                }
            }
            return regex;
        }else return regex;
    }
    
    public static int indexOfOpen(String regex, int index){
        for(int i = index; i >= 0; i--){
            if(regex.charAt(i) == '('){
                return i;
            }
        }
        return -1;
    }

    private static void fecthSymbols(String regex) {
        Set<Character> operators = new HashSet<>();
        operators.add('(');
        operators.add(')');
        operators.add('*');
        operators.add('?');
        operators.add('|');
        operators.add('$');
        //operators.add('&');
        
        int id = 1;
        for (int i = 0; i < regex.length(); i++) {
            char position = regex.charAt(i);
            
            if(!operators.contains(position)) {
                input.add("" + position);
                regexStates.put(id++, "" + position);
            }
        }
    }

    private static State createDFA() {
        int id = 0;
        Set<Integer> firstpos_n0 = root.getFirstPos();

        State q0 = new State(id++);
        q0.addAllToName(firstpos_n0);
        if (q0.getName().contains(nextPos.length)) {
            q0.setAccept();
        }
        DStates.clear();
        DStates.add(q0);

        while (true) {
            boolean exit = true;
            State s = null;
            for (State state : DStates) {
                if (!state.getIsMarked()) {
                    exit = false;
                    s = state;
                }
            }
            if (exit) {
                break;
            }
            
            if (s.getIsMarked()) {
                continue;
            }
            s.setIsMarked(true); //mark the state
            Set<Integer> name = s.getName();
            for (String a : input) {
                Set<Integer> U = new HashSet<>();
                for (int p : name) {
                    if (regexStates.get(p).equals(a)) {
                        U.addAll(nextPos[p - 1]);
                    }
                }
                boolean flag = false;
                State tmp = null;
                for (State state : DStates) {
                    if (state.getName().equals(U)) {
                        tmp = state;
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    State q = new State(id++);
                    q.addAllToName(U);
                    if (U.contains(nextPos.length)) {
                        q.setAccept();
                    }
                    DStates.add(q);
                    tmp = q;
                }
                s.addMove(a, tmp);
            }
        }

        return q0;
    }

    private static String getStr(Scanner in) {
        System.out.print("Enter a string: ");
        String str;
        str = in.nextLine();
        return str;
    }

}
