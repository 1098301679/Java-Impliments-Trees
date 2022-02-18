package top.noox.bst;

import top.noox.rbtree.RBTree;

import java.util.Scanner;

public class BSTreeTest {

    public static void main(String[] args) {

        BSTree<String, Object> bsTree = new BSTree<>();

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
                    bsTree.insert(key, null);
                    bsTree.inOrderPrint();
                    TreeOperation.show(bsTree.getRoot());
                }
            } else {
                while(true) {

                    System.out.println("请输入要删除的结点:");
                    String key = sc.next();

                    if (key.equals("exit")) break;
                    bsTree.delete(key);
                    TreeOperation.show(bsTree.getRoot());
                }
            }
        }


    }
}
