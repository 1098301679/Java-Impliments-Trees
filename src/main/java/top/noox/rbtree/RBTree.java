package top.noox.rbtree;




/**
 * <p>创建RBTree，定义颜色，红色为true,黑色为false
 * <p>创建RBNode结点内部类
 * <p>辅助方法定义: parentOf(node)，isRed (node)，isBlack (node)，setRed(node)，setBlack(node)，inOrderPrint()
 * <p>左旋方法定义: leftRotate(node)
 * <p>右旋方法定义: rightRotate(node)
 * <p>公开插入接口方法定义: insert(K key, v value)
 * <p>内部插入接口方法定义: insert(RBNode node)
 * <p>修正插入导致红黑树失衡的方法定义: insertFIxUp(RBNode node)
 * <p>测试红黑树正确性
 * <p>
 * @param <K> key
 * @param <V> value
 */

@SuppressWarnings("unchecked")
public class RBTree<K extends Comparable<K>, V>{

    private static final  boolean RED = true;
    private static final  boolean BLACK = false;

    private RBNode root;



    /**
     * 中序遍历
     */
    public void inOrderPrint() {
        inOrderPrint(root);
    }

    public void inOrderPrint(RBNode root) {
        if (root != null) {
            inOrderPrint(root.left);
            System.out.println(root);
            inOrderPrint(root.right);
        }
    }


    /**
     * 返回当前结点的前驱结点
     * @param node  当前结点
     * @return  前驱结点
     */
    public RBNode precursor(RBNode node){

        if (node == null) return null;

        RBNode pre = null;
        //左子树不为空
        if (node.left != null) {
            pre = node.left;
            while (pre.right != null) {
                pre = pre.right;
            }
        //左子树为空
        } else {
            RBNode parent = node.parent;
            RBNode child = node;
            while (parent != null) {
                child = parent;
                parent = parent.parent;
            }
            pre = child;
        }

        return pre;
    }


    /**
     * 对外插入方法
     * @param key  key
     * @param value value
     */

    public void insert(K key, V value) {
        insert(new RBNode(key,value,RED));
    }


    private void insert(RBNode node) {

        RBNode parent = null, x = this.root;

        while (x!= null) {
            parent = x;
            int cmp = node.key.compareTo(x.key);
            if (cmp == 0) {
                x.value = node.value;
                return;
            }
            //当前结点key更大，需要到x的右子树寻找
            if (cmp > 0) {
                x = x.right;
            } else {
                x = x.left;
            }
        }

        node.parent = parent;
        if (parent != null)
            if (node.key.compareTo(parent.key) > 0)
                parent.right = node;
            else
                parent.left = node;
        else    //首次插入
            this.root = node;

        //调用修复红黑树平衡的方法
        insertFixUp(node);
    }

    /**
     * <p>插入后修复红黑树平衡的方法<p/>
     *
     * <ul>
     *     <li>情景1：红黑树为空树 => <span style="color:#4f86cd">将插入结点染为黑色
     *     <li>情景2：插入结点的key已经存在 => <span style="color:#4f86cd">来不到这个方法
     *     <li>情景3：插入结点的父结点为黑色 => <span style="color:#4f86cd">无需处理
     *     <li>情景4：插入结点的父结点为红色
     *     <ul>
     *         <li>情景4.1：叔叔结点存在，并且为红色（父叔双红） => <span style="color:#4f86cd">将爸爸叔叔染为黑色，将爷爷染为红色，并且再以爷爷结点为当前结点进行下一轮处理
     *         <li>情景4.2：叔叔结点不存在，或者为黑色，父结点为爷爷结点的左子树
     *         <ul>
     *             <li>情景4.2.1：插入结点为其父结点的左子结点（LL情况） => <span style="color:#4f86cd">将爸爸染色为黑色，将爷爷染色为红色，以爷爷结点右旋
     *             <li>情景4.2.2：插入结点为其父结点的右子结点（LR情况） => <span style="color:#4f86cd">父左旋，然后以4.2.1处理
     *         </ul>
     *         <li>情景4.3：叔叔结点不存在，或者为黑色，父结点为爷爷结点的右子树
     *         <ul>
     *             <li>情景4.3.1：插入结点为其父结点的右子结点（RR情况）=> <span style="color:#4f86cd">将爸爸染色为黑色，将爷爷染色为红色，以爷爷结点右旋
     *             <li>情景4.3.2：插入结点为其父结点的左子结点（RL情况）=> <span style="color:#4f86cd">父右旋，然后以4.3.1处理
     *         </ul>
     *     </ul>
     * </ul>
     *
     * @param node 当前新插入结点
     */


    private void insertFixUp(RBNode node) {
        //情景1：红黑树为空树
        if (node.parent == null) {
            this.root.color = BLACK;
            return;
        }

        //parent:父    gParent:爷
        RBNode parent = parentOf(node);
        RBNode gParent = parentOf(parent);

        //情景4   插入结点的父结点为红色
        if (isRed(parent)) {

            RBNode uncle = null;
            //父为爷的左孩子
            if (parent == gParent.left) {
                uncle = gParent.right;

                //叔父双红  递归处理完后可能会进下方if逻辑所以需要return
                if (uncle != null && isRed(uncle)) {
                    parent.color = BLACK;
                    uncle.color = BLACK;
                    gParent.color = RED;
                    insertFixUp(gParent);
                    return;
                }
                //叔叔结点不存在，或者为黑色
                if (uncle == null || isBlack(uncle)) {
                    //插入结点为其父结点的左子结点（LL情况)
                    if (node == parent.left) {

                        parent.color = BLACK;
                        gParent.color = RED;
                        rightRotate(gParent);

                    } else {
                        //插入结点为其父结点的右子结点（LR情况）
                        leftRotate(parent);
                        insertFixUp(parent);

                    }

                }

            //父为爷的右孩子
            } else {
                uncle = gParent.left;

                //叔父双红 递归处理完后可能会进下方if逻辑所以需要return
                if (uncle!= null && isRed(uncle)) {
                    parent.color = BLACK;
                    uncle.color = BLACK;
                    gParent.color = RED;
                    insertFixUp(gParent);
                    return;
                }

                //叔叔结点不存在，或者为黑色
                if (uncle == null || isBlack(uncle)) {
                    //插入结点为其父结点的左子结点（LL情况)
                    if (node == parent.right) {
                        parent.color = BLACK;
                        gParent.color = RED;
                        leftRotate(gParent);
                    } else {
                        //插入结点为其父结点的右子结点（LR情况）
                        rightRotate(parent);
                        insertFixUp(parent);
                    }

                }
            }
        }


    }

    public void delete(K key) {
        RBNode node = find(key);
        if (node == null) return;
        delete(node);
    }

    /**
     * <p>删除结点
     * <p>
     * <p>情景1：单个红结点 <span style="color:#4f86cd"> => 直接删除返回即可
     * <p>情景2：带有两个子树的结点 <span style="color:#4f86cd"> => 交换后递归处理（转为情景1或4）
     * <p>情景3：带有一个子树的结点（必是黑父红子）<span style="color:#4f86cd"> => 交换后删除红子返回即可
     * <p>情景4：单个黑结点 <span style="color:#4f86cd"> => 直接删除，删除后需要调整
     * <p>注：只有删除黑结点才真正需要平衡调整
     * <p>
     * @param node 要删除的结点
     */

    private void delete(RBNode node) {

        RBNode parent = parentOf(node);
        //deleteNode:被删除的结点  isLeftChild:是否为左孩子
        RBNode deleteNode = null;
        boolean isLeftChild = false;


        //单个红结点：直接清空父结点引用然后返回即可
        if (isRed(node) && node.right == null && node .left == null) {
            if (node == parent.left)
                parent.left = null;
            parent.right = null;

            return;
        }

        //带有两个子树的结点：交换后递归处理
        if (node.left != null && node.right != null) {
            RBNode precursor = precursor(node);

            node.key = precursor.key;
            node.value = precursor.value;

            delete(precursor);
            return;
        }

        //带有一个子树的结点（必是黑父红子）：交换后删除红子
        if (isRed(node.left) || isRed(node.right)) {
            RBNode sun = null;
            if (node.left != null) {
                sun = node.left;
                node.key = sun.key;
                node.value = sun.value;
                node.left = null;
            } else {
                sun = node.right;
                node.key = sun.key;
                node.value = sun.value;
                node.right = null;
            }
            return;
        }


        //单个黑结点：直接删除，删除后需要调整
        if (isBlack(node) && node.left == null && node.right == null) {
            if (parent != null) {
                if (node == parent.left)  {
                    parent.left = null;
                    isLeftChild = true;
                } else {
                    parent.right = null;
                }
                deleteNode = node;
            } else {
                this.root = null;
                return;
            }
        }

        deleteFixUp(deleteNode,isLeftChild);
    }

    /**
     * 修复删除后造成的可能的红黑树失衡
     * <p>
     * <p>情景1. 黑兄弟，右红侄（无论左侄有没有）<span style="color:#4f86cd"> => 左旋父，爷染父色，父叔黑
     * <p>情景2. 黑兄弟，左红侄（右红侄没有才生效）<span style="color:#4f86cd"> => 右旋兄，交换兄弟与其右子颜色，变为情景1
     * <p>情景3. 黑兄弟，双黑侄 <span style="color:#4f86cd">=> 兄弟红，向上找，遇根或红结点，染黑即解决，若非根父黑，以父递归处理
     * <p>情景4. 红兄弟 <span style="color:#4f86cd">=> 左旋父，父，祖换色，变成情景123
     * <p>删除黑结点时，只有情景1和情景3父结点是红色或根结点时可以解决平衡。其它情况都会向这两种情况转化
     * <p>
     * @param node  当前被删除结点
     * @param isLeftChild    被删除结点是否是左孩子
     */
    private void deleteFixUp(RBNode node, boolean isLeftChild){

        //parent一定不为null且node一定为黑
        RBNode parent = parentOf(node);
        RBNode brother,nephew = null;

        if (isLeftChild) {

            brother = parent.right;
            //黑兄弟
            if (isBlack(brother)) {

                //黑兄弟，右红侄（无论左侄有没有）
                if (brother.right != null && isRed(brother.right)) {
                    nephew = brother.right;
                    leftRotate(parent);
                    brother.color = parent.color;
                    parent.color = nephew.color = BLACK;
                    TreeOperation.show(root);
                //黑兄弟，左红侄
                } else if (brother.left != null && isRed(brother.left)) {
                    nephew = brother.left;
                    rightRotate(brother);
                    brother.color = RED;
                    nephew.color = BLACK;
                    TreeOperation.show(root);
                    deleteFixUp(node,true);
                //黑兄弟，双黑侄
                } else {
                    brother.color = RED;
                    if (parent == this.root || isRed(parent)) {
                        parent.color = BLACK;
                        TreeOperation.show(root);
                    } else
                        deleteFixUp(parent, parent.parent.left == parent);
                }
            }
            //红兄弟
            else {
                leftRotate(parent);
                parent.color = RED;
                brother.color = BLACK;
                TreeOperation.show(root);
                deleteFixUp(node,true);
            }

        } else {

            brother = parent.left;
            //黑兄弟
            if (isBlack(brother)) {
                //黑兄弟，左红侄（无论右侄有没有）
                if (brother.left != null) {
                    nephew = brother.left;
                    rightRotate(parent);
                    brother.color = parent.color;
                    parent.color = nephew.color = BLACK;
                    TreeOperation.show(root);
                    //黑兄弟，右红侄
                } else if (brother.right != null && isRed(brother.right)) {
                    nephew = brother.right;
                    leftRotate(brother);
                    brother.color = RED;
                    nephew.color = BLACK;
                    TreeOperation.show(root);
                    //黑兄弟，双黑侄
                } else {
                    brother.color = RED;
                    TreeOperation.show(root);
                    if (parent == this.root || isRed(parent)) {
                        parent.color = BLACK;
                        TreeOperation.show(root);
                    } else
                        deleteFixUp(parent, parent.parent.left == parent);
                }
            }
            //红兄弟
            else {
                rightRotate(parent);
                parent.color = RED;
                brother.color = BLACK;
                TreeOperation.show(root);
                deleteFixUp(node,false);
            }
        }
    }



    /**
     * 根据key查找红黑树中结点
     * @param key   key
     * @return  找到返回结点，否则返回null
     */
    public RBNode find(K key) {

        if (this.root == null)
            return null;

        RBNode node = this.root;

        while (node != null) {
            int cmp = key.compareTo((K) node.key);

            if (cmp == 0)
                return node;
            else if (cmp > 0)
                node = node.right;
            else
                node = node.left;
        }

        return null;
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

    private void leftRotate(RBNode x) {
        //1
        RBNode y = x.right;
        x.right = y.left;
        if (y.left != null)
            y.left.parent = x;

        //2
        if (x.parent != null) {
            y.parent = x.parent;

            if (x == x.parent.left)
                x.parent.left = y;
            else
                x.parent.right = y;
        } else {
            this.root = y;
            this.root.parent = null;
        }


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


    private void rightRotate(RBNode y) {
        RBNode x = y.left;
        y.left = x.right;
        if (x.right != null)
            x.right.parent = y;

        if (y.parent != null) {
            x.parent = y.parent;

            if (y == y.parent.left)
                y.parent.left = x;
            else
                y.parent.right = x;
        } else {
            this.root = x;
            this.root.parent = null;
        }


        x.right = y;
        y.parent = x;
    }






    static class RBNode<K extends Comparable<K>, V> {

        private RBNode parent;
        private RBNode left;
        private RBNode right;

        private boolean color;
        private K key;
        private V value;


        public RBNode(K key, V value, boolean red) {
            this.key = key;
            this.value = value;
            this.color = red;
        }

        @Override
        public String toString() {
            return "RBNode{" +
                    "color=" + color +
                    ", key=" + key +
                    ", value=" + value +
                    '}';
        }

        public RBNode getLeft() {
            return left;
        }

        public RBNode getRight() {
            return right;
        }

        public boolean isColor() {
            return color;
        }

        public K getKey() {
            return key;
        }
    }


    private RBNode parentOf(RBNode node) {
        if(node != null)
            return node.parent;
        return null;
    }

    private boolean isRed(RBNode node){
        if (node != null)
            return node.color == RED;
        return false;
    }

    //nil结点与黑结点都为false
    private boolean isBlack(RBNode node){
        if (node != null)
            return node.color == BLACK;
        return false;
    }

    private void setRed(RBNode node) {
        if (node != null)
            node.color = RED;

    }

    private void setBlack(RBNode node) {
        if (node != null)
            node.color = BLACK;
    }

    public RBNode getRoot() {
        return this.root;
    }

}
