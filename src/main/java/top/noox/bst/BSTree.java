package top.noox.bst;



public class BSTree<K extends Comparable<K>, V> {

    private BSNode<K,V> root;

    public BSNode<K,V> getRoot() {
        return root;
    }




    public void insert(K key, V value) {
        insert(new BSNode<>(key, value));
    }

    private void insert(BSNode<K,V> node) {
        if (root == null) {
            root = node;
            root.height = 1;
            return;
        }
        //x：记录要插入的位置  parent
        BSNode<K,V> x = this.root, parent = null;
        while (x != null) {
            parent = x;

            int cmp = node.key.compareTo(x.key);
            if (cmp > 0)
                x = x.right;
            else if (cmp < 0)
                x = x.left;
            else {
                x.value = node.value;
                return;
            }
        }

        node.parent = parent;
        if (node.key.compareTo(parent.key) > 0)
            parent.right = node;
        else
            parent.left = node;

        node.height = 1;

        balanceInsertion(node);
    }

    public void delete(K key) {
        BSNode<K,V> node = find(key);
        if (node != null)
            delete(node);
    }

    //能进来说明node不为null
    private void delete(BSNode<K,V> node) {

        BSNode<K,V> parent = node.parent;

        //如果是叶子结点
        if (node.left == null && node.right == null) {
            //如果是根节点,删除后直接返回，不需要做平衡性检查
            if (parent == null) {
                this.root = null;
                return;
            }
            //如果不是根节点，删除后需要做平衡性检查
            if (node == parent.left)
                parent.left = null;
            else
                parent.right = null;

            balanceDeletion(node);
            return;
        }

        //走到这说明不是叶子结点，会转化为删除叶子节点

        if (node.left == null || node.right == null) {
            //说明只有右孩子
            if (node.left == null) {
                node.key = node.right.key;
                node.value = node.right.value;
                delete(node.right);
            //说明只有所孩子
            } else {
                node.key = node.left.key;
                node.value = node.left.value;
                delete(node.left);
            }
        //说明左右孩子都有
        }

        if (node.left != null && node.right != null) {
            BSNode<K,V> precursor = precursor(node);
            node.key = precursor.key;
            node.value = precursor.value;
            delete(precursor);
        }


    }

    private void balanceInsertion(BSNode<K,V> node) {

        //新插入结点的父节点
        BSNode<K,V> parent = node.parent;
        while (parent != null) {
            parent.height = getHeight(parent);

            if (Math.abs(getHeight(parent.left) - getHeight(parent.right)) == 2) {

                if (getHeight(parent.left) - getHeight(parent.right) == 2) {
                    //LL
                    if (isParent(node, parent.left.left))
                        rightRotate(parent);
                        //LR
                    else {
                        leftRotate(parent.left);
                        rightRotate(parent);
                    }
                    fixUpHeight(parent);
                    return;
                } else if (getHeight(parent.left) - getHeight(parent.right) == -2) {
                    //RR
                    if (isParent(node, parent.right.right))
                        leftRotate(parent);
                        //RL
                    else {
                        rightRotate(parent.right);
                        leftRotate(parent);
                    }
                    fixUpHeight(parent);
                    return;
                }
            }

            parent = parent.parent;
        }
    }

    private void balanceDeletion(BSNode<K,V> node) {

        BSNode<K,V> parent = node.parent;
        while (parent != null) {
            parent.height = getHeight(parent);

            int balanceFactor = getBalanceFactor(parent);


            if (balanceFactor == -2) {
                int rFactor = getBalanceFactor(parent.right);
                if (rFactor == -1 || rFactor == 0)
                    leftRotate(parent);
                else {
                    rightRotate(parent.right);
                    leftRotate(parent);
                }
                fixUpHeight(parent);
                return;
            }
            if (balanceFactor == 2) {
                int lFactor = getBalanceFactor(parent.left);
                if (lFactor == 1 || lFactor == 0)
                    rightRotate(parent);
                else {
                    leftRotate(parent.left);
                    rightRotate(parent);
                }
                fixUpHeight(parent);
                return;
            }

            parent = parent.parent;
        }
    }

    private int getBalanceFactor(BSNode<K,V> node) {
        if (node.left == null && node.right == null)
            return 0;

        return getHeight(node.left) - getHeight(node.right);
    }

    private BSNode<K,V> find(K key) {
        if (this.root == null)
            return null;

        BSNode<K,V> x = this.root;
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if (cmp > 0)
                x = x.right;
            else if (cmp < 0)
                x = x.left;
            else
                return x;
        }
        return null;
    }

    private BSNode<K,V> precursor(BSNode<K,V> node){

        BSNode<K,V> pre = node.left;
        while (pre.right != null) {
            pre = pre.right;
        }
        return pre;
    }

    public void inOrderPrint() {
        inOrderPrint(root);
    }

    private void inOrderPrint(BSNode<K,V> root) {
        if (root != null) {
            inOrderPrint(root.left);
            System.out.println(root);
            inOrderPrint(root.right);
        }
    }

    /**
     * 修复以root为根节点的子树的高度
     * @param root GEN
     */

    public void fixUpHeight(BSNode<K,V> root) {
        if (root != null) {
            fixUpHeight(root.left);
            root.height = getHeight(root);
            fixUpHeight(root.right);
        }
    }








    private boolean isParent(BSNode<K,V> sun, BSNode<K,V> parent) {

        BSNode<K,V> p = sun;
        while (p != null) {
            if (p == parent) return true;
            p = p.parent;
        }

        return false;
    }

    private  int getHeight(BSNode<K,V> node) {

        if (node == null) return 0;
        //左右子树都为空
        if (node.left == null && node.right == null)
            return 1;
        //走到这里说明左右子树不全为空
        if (node.left == null)
            return getHeight(node.right) + 1;
        if (node.right == null)
            return getHeight(node.left) + 1;

        //左右子树都存在
        return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
    }



    /**
     * <p>左旋方法
     * <p>左旋示意图：左旋x结点
     *    p                   p
     *    |                   |
     *    x                   y
     *   / \         ---->   / \
     *  lx  y               x   ry
     *     / \             / \
     *    ly  ry          lx  ly
     *
     * <p>左旋做了几件事？
     * <p>1.建立x与ly的联系   => 将y的左子结点赋值给x的右边，并且把x设置为y的左子结点的父结点
     * <p>2.建立P与y的联系    => 将x的父结点（非空时）指向y，更新y的父结点为x的父结点
     * <p>3.建立x与y的来联系  => 将y的左子结点指向x，更新x的父结点为y
     * <p>
     * @param x : 要进行左旋的结点
     */

    private void leftRotate(BSNode<K,V> x) {
        BSNode<K,V> ly = x.right.left,
                p = x.parent,
                y = x.right;
        //1
        x.right = ly;
        if (ly != null)
            ly.parent = x;

        //2
        if (p != null) {
            if (x == p.left)
                p.left = y;
            else
                p.right = y;
        } else {
            this.root = y;
        }
        y.parent = p;

        //3
        y.left = x;
        x.parent = y;
    }





    /**
     * <p>右旋方法
     * <p>右旋示意图：右旋y结点
     *
     *       p                    p
     *       |                    |
     *       y                    x
     *      / \       ---->      / \
     *     x   ry               lx  y
     *    / \                      / \
     *   lx  ly                   ly  ry
     *
     * <p>右旋都做了几件事？
     * <p>1.建立y与ly的联系  => 将x的右子结点 赋值 给了 y 的左子结点，并且更新x的右子结点的父结点为 y
     * <p>2.建立p与x的联系   => 将y的父结点（不为空时）指向x，更新x的父结点为y的父结点
     * <p>3.建立x与y的联系   => 将x的右子结点指向y，更新y的父结点为x
     * <p>
     *  @param y : 要进行右旋的结点
     */

    private void rightRotate(BSNode<K,V> y) {
        BSNode<K,V> ly = y.left.right,
                x = y.left,
                p = y.parent;

        //1
        y.left = ly;
        if (ly != null)
            ly.parent = y;

        if (p != null) {
            if (y == p.left)
                p.left = x;
            else
                p.right = x;
        } else {
            this.root = x;
        }
        x.parent = p;

        x.right = y;
        y.parent = x;
    }


    static class BSNode<K extends Comparable<K>, V> {

        BSNode<K,V> left;

        BSNode<K,V> right;
        BSNode<K,V> parent;

        K key;
        V value;

        int height;

        @Override
        public String toString() {
            return "BSNode{" +
                    "key=" + key +
                    ", value=" + value +
                    ", height=" + height +
                    '}';
        }

        public BSNode<K,V> getLeft() {
            return left;
        }


        public BSNode<K,V> getRight() {
            return right;
        }

        public K getKey() {
            return key;
        }


        public BSNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
