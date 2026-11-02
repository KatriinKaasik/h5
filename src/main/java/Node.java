// Lahenduse loomisel kasutasin tehisintellekti abi (Claude Sonnet 5.5).

import java.util.*;

public class Node {

   private String name;
   private Node firstChild;
   private Node nextSibling;

   Node (String n, Node d, Node r) {
      name = n;
      firstChild = d;
      nextSibling = r;
   }

   private static List<String> tokens;
   private static int pos;

   public static Node parsePostfix (String s) {
      tokens = new ArrayList<String>();
      StringTokenizer st = new StringTokenizer (s, "(),", true);
      while (st.hasMoreTokens())
         tokens.add (st.nextToken());
      pos = 0;
      Node root = parseTree();
      if (pos < tokens.size())
         throw new RuntimeException ("Liigne sümbol '" + tokens.get (pos) + "' sõnes " + s);
      return root;
   }

   private static String peek() {
      return pos < tokens.size() ? tokens.get (pos) : "";
   }

   private static Node parseTree() {
      List<Node> children = new ArrayList<Node>();
      if (peek().equals ("(")) {
         pos++;
         children.add (parseTree());
         while (peek().equals (",")) {
            pos++;
            children.add (parseTree());
         }
         if (!peek().equals (")"))
            throw new RuntimeException ("Oodati ',' või ')' kohal " + pos + " (märgend: '" + peek() + "')");
         pos++;
      }
      String name = peek();
      if (name.isEmpty() || "(),".contains (name))
         throw new RuntimeException ("Tipu nimi puudub (märgend nr " + pos + ")");
      if (name.matches (".*\\s.*"))
         throw new RuntimeException ("Tipu nimi sisaldab tühikut: '" + name + "'");
      pos++;
      Node first = null;
      for (int i = children.size() - 1; i >= 0; i--) {
         children.get (i).nextSibling = first;
         first = children.get (i);
      }
      return new Node (name, first, null);
   }

   public String leftParentheticRepresentation() {
      String result = name;
      if (firstChild != null) {
         StringJoiner j = new StringJoiner (",", "(", ")");
         for (Node c = firstChild; c != null; c = c.nextSibling)
            j.add (c.leftParentheticRepresentation());
         result += j;
      }
      return result;
   }

   public static void main (String[] param) {
      String[] tests = { "(B1,C)A", "A", "((C,D)B,E)A", "(((512,1)-,4)*,(-6,3)/)+" };
      for (String s : tests)
         System.out.println (s + " ==> " + parsePostfix (s).leftParentheticRepresentation());

      String[] bad = { "", "()A", "(B,C", "(B,C)", "(B C)A", "(B,,C)A", "A(B)", "(B,C)A)" };
      for (String s : bad) {
         try {
            parsePostfix (s);
            System.out.println ("\"" + s + "\" ==> viga puudub (ootamatu)");
         } catch (RuntimeException e) {
            System.out.println ("\"" + s + "\" ==> " + e.getMessage());
         }
      }
   }
}
