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
      if (s == null) {
         throw new RuntimeException("Tree input must not be null: " + s);
      }

      Node root = new Node(null, null, null);
      Node current = root;

      Stack<Node> parents = new Stack<>();

      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         if (Character.isWhitespace(c) || Character.isSpaceChar(c)) {
            continue;
         }

         if (c == '(') {
            if (current.name != null || current.firstChild != null) {
               throw new RuntimeException("A node may have only one child list, before its name. Input: \"" + s + "\"");
            }
            parents.push(current);
            current.firstChild = new Node(null, null, null);
            current = current.firstChild;
         } else if (c == ',') {
            if (current.name == null) {
               throw new RuntimeException("Missing node name before ','. Input: \"" + s + "\"");
            }
            if (parents.empty()) {
               throw new RuntimeException("A tree must have only one root. Unexpected ','. Input: \"" + s + "\"");
            }
            current.nextSibling = new Node(null, null, null);
            current = current.nextSibling;
         } else if (c == ')') {
            if (parents.empty()) {
               throw new RuntimeException("Closing ')' has no matching '('. Input: \"" + s + "\"");
            }
            if (current.name == null) {
               throw new RuntimeException("Missing node name before ')'. Input: \"" + s + "\"");
            }
            current = parents.pop();
         } else {
            if (current.name != null) {
               throw new RuntimeException("Whitespace is not allowed inside a node name. Input: \"" + s + "\"");
            }
            int start = i;
            while (i < s.length()) {
               char letter = s.charAt(i);
               if (letter == '(' || letter == ')' || letter == ','
                       || Character.isWhitespace(letter) || Character.isSpaceChar(letter)) {
                  break;
               }
               i++;
            }
            current.name = s.substring(start, i);
            i--;
         }
      }

      if (!parents.empty()) {
         throw new RuntimeException("Missing closing ')'. Input: \"" + s + "\"");
      }
      if (current.name == null) {
         throw new RuntimeException("Missing node name. Input: \"" + s + "\"");
      }
      return root;
   }

   public String leftParentheticRepresentation() {
      StringBuilder result = new StringBuilder();
      Stack<Node> parents = new Stack<>();
      Node current = this;

      while (true) {
         result.append(current.name);
         if (current.firstChild != null) {
            result.append('(');
            parents.push(current);
            current = current.firstChild;
         } else {
            while (current != this && current.nextSibling == null) {
               result.append(')');
               current = parents.pop();
            }
            if (current == this) {
               return result.toString();
            }
            result.append(',');
            current = current.nextSibling;
         }
      }
   }

   /**
    * Tagastab puu pseudo-XML kujul. Juure sügavus on 1.
    * @return puu märgendite ja taanetega
    */
   public String toXML() {
      StringBuilder result = new StringBuilder();
      appendXML(result, 1);
      return result.toString();
   }

   /**
    * Lisab selle tipu ja tema järglased ühisesse puhvrisse.
    * @param result tulemuse puhver
    * @param depth selle tipu sügavus
    */
   private void appendXML (StringBuilder result, int depth) {
      for (int i = 1; i < depth; i++) {
         result.append("    ");
      }
      result.append("<L").append(depth).append("> ").append(name);

      if (firstChild != null) {
         result.append('\n');
         for (Node child = firstChild; child != null; child = child.nextSibling) {
            child.appendXML(result, depth + 1);
         }
         for (int i = 1; i < depth; i++) {
            result.append("    ");
         }
      } else {
         result.append(' ');
      }

      result.append("</L").append(depth).append(">\n");
   }

   public static void main (String[] param) {
      String s = "(B1,C)A";
      Node t = Node.parsePostfix (s);
      String v = t.leftParentheticRepresentation();
      System.out.println (s + " ==> " + v); // (B1,C)A ==> A(B1,C)
      System.out.println(t.toXML());

      s = "A";
      t = Node.parsePostfix (s);
      System.out.println (s + " ==> " + t.leftParentheticRepresentation());
      System.out.println(t.toXML());

      s = "((C)B,(E,F)D,G)A";
      t = Node.parsePostfix(s);
      System.out.println(s + " ==> XML");
      System.out.println(t.toXML());
   }
}
