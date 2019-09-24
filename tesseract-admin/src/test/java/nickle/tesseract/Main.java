//package nickle.tesseract;
//
//import java.util.*;
//
///**
// * @description:
// * @author: nickle
// * @create: 2019-09-09 14:14
// **/
//public class Main {
//    public static final String LEFT_BRACKET = "[";
//    public static final String RIGHT_BRACKET = "]";
//    public static final String COMMA = ",";
//    public static final String BLANK = " ";
//
//    static class Node {
//        public int id;
//        public int pid;
//        public int data;
//        public List<Node> children;
//
//        @Override
//        public String toString() {
//            return "Node{" +
//                    "id=" + id +
//                    ", pid=" + pid +
//                    ", data=" + data +
//                    ", children=" + children +
//                    '}';
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        Scanner scanner = new Scanner(System.in);
//        String arrStr = scanner.nextLine();
//        resolveArrStr(arrStr);
//    }
//
//    public static int curIndex = 0;
//    public static LinkedHashMap<Integer, Node> nodeLinkedHashMap = new LinkedHashMap<>();
//    public static Stack<String> stack = new Stack<>();
//
//    public static void resolveArrStr(String arrStr) {
//
//        String curSymbol = null;
//        while ((curSymbol = nextSymbol(arrStr)) != null) {
//            dispatchSymbol(curSymbol);
//        }
//    }
//
//    public static boolean dispatchSymbol(String curSymbol) {
//        if (curSymbol.equals(LEFT_BRACKET)) {
//            stack.push(curSymbol);
//            continue;
//        }
//        if (curSymbol.equals(RIGHT_BRACKET)) {
//            if (stack.peek().equals(LEFT_BRACKET) && stack.size() == 1) {
//                break;
//            }
//
//            Integer data = Integer.valueOf(stack.pop());
//            Integer pid = Integer.valueOf(stack.pop());
//            Integer id = Integer.valueOf(stack.pop());
//            Node node = nodeLinkedHashMap.get(id);
//            Node parentNode = nodeLinkedHashMap.get(pid);
//            if (node == null) {
//                node = new Node();
//                node.children = new ArrayList<>();
//                nodeLinkedHashMap.put(id, node);
//            }
//            node.id = id;
//            node.pid = pid;
//            node.data = data;
//
//            if (parentNode == null) {
//                parentNode = new Node();
//                parentNode.id = pid;
//                parentNode.children = new ArrayList<>();
//                nodeLinkedHashMap.put(pid, parentNode);
//            }
//            parentNode.children.add(node);
//            System.out.println(node);
//
//            stack.pop();
//
//            continue;
//        }
//        if (!curSymbol.equals(COMMA)) {
//            stack.push(curSymbol);
//        }
//    }
//
//    public static String nextSymbol(String arrStr) {
//        while (curIndex <= arrStr.length() - 1) {
//            char c = arrStr.charAt(curIndex);
//            ++curIndex;
//            if (c != ' ') {
//                return String.valueOf(c);
//            }
//        }
//        return null;
//    }
//
//}
