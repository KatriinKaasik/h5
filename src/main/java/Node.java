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

   public static Node parsePostfix (String s) {
      if (s == null)
         throw new RuntimeException ("Sisendsõne on null: " + s);
      if (s.isEmpty())
         throw new RuntimeException ("Sisendsõne on tühi: \"" + s + "\"");

      Stack<List<Node>> stack = new Stack<List<Node>>();
      List<Node> current = new ArrayList<Node>();
      List<Node> closed = null;
      int i = 0;
      while (i < s.length()) {
         char c = s.charAt (i);
         char prev = i > 0 ? s.charAt (i - 1) : '(';
         if (Character.isWhitespace (c)) {
            throw new RuntimeException ("Sõnes \"" + s + "\" on tühik kohal " + i);
         } else if (c == '(') {
            if (prev != '(' && prev != ',')
               throw new RuntimeException ("Sõnes \"" + s + "\" on '(' valel kohal " + i);
            stack.push (current);
            current = new ArrayList<Node>();
            i++;
         } else if (c == ',' || c == ')') {
            if ("(),".indexOf (prev) >= 0)
               throw new RuntimeException ("Sõnes \"" + s + "\" puudub tipu nimi kohal " + i);
            if (stack.isEmpty())
               throw new RuntimeException ("Sõnes \"" + s + "\" on liigne '" + c + "' kohal " + i);
            if (c == ')') {
               closed = current;
               current = stack.pop();
            }
            i++;
         } else {
            int start = i;
            while (i < s.length() && "(),".indexOf (s.charAt (i)) < 0
                    && !Character.isWhitespace (s.charAt (i)))
               i++;
            Node first = null;
            if (closed != null) {
               for (int k = closed.size() - 1; k >= 0; k--) {
                  closed.get (k).nextSibling = first;
                  first = closed.get (k);
               }
               closed = null;
            }
            current.add (new Node (s.substring (start, i), first, null));
         }
      }
      if (!stack.isEmpty())
         throw new RuntimeException ("Sõnes \"" + s + "\" on sulge, mis ei ole suletud");
      if ("(),".indexOf (s.charAt (s.length() - 1)) >= 0)
         throw new RuntimeException ("Sõnes \"" + s + "\" puudub juurtipu nimi");
      return current.get (0);
   }

   public String leftParentheticRepresentation() {
      StringBuilder b = new StringBuilder();
      build (b);
      return b.toString();
   }

   private void build (StringBuilder b) {
      b.append (name);
      if (firstChild != null) {
         b.append ('(');
         for (Node c = firstChild; c != null; c = c.nextSibling) {
            c.build (b);
            if (c.nextSibling != null)
               b.append (',');
         }
         b.append (')');
      }
   }

   public static void main (String[] param) {
      String[] tests = { "(B1,C)A", "A", "((C,D)B,E)A", "(((512,1)-,4)*,(-6,3)/)+" };
      for (String s : tests)
         System.out.println (s + " ==> " + parsePostfix (s).leftParentheticRepresentation());

      String[] bad = { "", "()A", "(B,C", "(B,C)", "(B C)A", "(B,,C)A", "A(B)", "(B,C)A)",
              "A,B", "((B)", "(B))A", "((B),C)", ",A", "(B,)A" };
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
