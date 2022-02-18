package top.noox.rbtree;

import java.util.Scanner;

public class RBTreeTest {

    public static void main(String[] args) {

        RBTree<String, Object> rbTree = new RBTree<>();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("想要新增结点请按1，想要删除结点请按任意键，推出请按exit");
            String cmd = sc.next();
            if (cmd.equals("exit")) break;
            if (cmd.equals("1")) {
                while(true) {

                    System.out.println("请输入key:");
                    String key = sc.next();

                    if (key.equals("exit")) break;
                    rbTree.insert(key, null);
                    TreeOperation.show(rbTree.getRoot());
                }
            } else {
                while(true) {

                    System.out.println("请输入要删除的结点:");
                    String key = sc.next();

                    if (key.equals("exit")) break;
                    rbTree.delete(key);
                    TreeOperation.show(rbTree.getRoot());
                }
            }
        }



    }
}
