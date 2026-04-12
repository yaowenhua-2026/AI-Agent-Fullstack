package org.example.week3_threadpool.Leetcode;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BinaryTreeNewTest {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    // 1. 对称二叉树
    public boolean isSymmetric(TreeNode root) {
        // TODO
        if (root == null) return true;
        return isSymmetric2(root.left,root.right);
    }
    public  boolean isSymmetric2(TreeNode left,TreeNode right) {
        //判断字节点的所有可能
        //第一种可能 成功可能
        if (left == null && right == null) return true;
        //第二种可能 容易一边为空
        if (left == null || right == null) return false;
        return  left.val==right.val &&
                isSymmetric2(left.left,right.right)&&
                isSymmetric2(left.right,right.left);
    }

    // 2. 二叉树的中序遍历
    public List<Integer> inorderTraversal(TreeNode root) {
        // TODO
        ArrayList<Integer> result = new ArrayList<>();
        sonNode(root,result);
        return result;
    }
    public  void  sonNode(TreeNode root,ArrayList<Integer> result){
            if (root == null) return ;
            //判断左子节点
            sonNode(root.left,result);
            //添加节点
            result.add(root.val);
            //判断右子节点
            sonNode(root.right,result);
    }

    // 3. 相同的树
    public boolean isSameTree(TreeNode p, TreeNode q) {
        // TODO
        if (p == null && q == null) return true;
        //处理一边为null的情况
        if (p == null || q == null) return false;
        //处理值不对等的情况
        if (p.val != q.val) return false;
        //传递子节点继续左右继续比较
        isSameTree(p.left,q.left);
        isSameTree(p.right,q.right);
        // 左右子树都相同，整棵树才相同
        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);

    }

    // 4. 将有序数组转换为二叉搜索树
    public TreeNode sortedArrayToBST(int[] nums) {
        // TODO
            if (nums == null || nums.length == 0) return null;
            //获取中间节点

            //返回二叉数头
            return build(nums, 0, nums.length - 1);
    }

    public TreeNode build(int[] nums,int left,int right) {
            if(left >right)return null;
            int mid=left+(right-left)/2;
            TreeNode root=new TreeNode(nums[mid]);
            root.left=build(nums,left,mid-1);
            root.right=build(nums,mid+1,right);


            return root;
        }


    // 5. 平衡二叉树
    public boolean isBalanced(TreeNode root ) {

        // TODO
        return height(root) != -1;

    }
    //判断高度
    public int height(TreeNode root ) {
        if (root == null) return 0;
        int left=height(root.left);
        int right=height(root.right);
        // 3. 不平衡条件
        if (left == -1 || right == -1 || Math.abs(left - right) > 1) {
            return -1;  // 标记不平衡
        }
      return Math.max(left,right)+1;

    }

    // ========== 辅助方法 ==========
    public static TreeNode buildTree(Integer[] nums) {
        if (nums == null || nums.length == 0) return null;
        TreeNode root = new TreeNode(nums[0]);
        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.offer(root);
        int i = 1;
        while (!queue.isEmpty() && i < nums.length) {
            TreeNode node = queue.poll();
            if (i < nums.length && nums[i] != null) {
                node.left = new TreeNode(nums[i]);
                queue.offer(node.left);
            }
            i++;
            if (i < nums.length && nums[i] != null) {
                node.right = new TreeNode(nums[i]);
                queue.offer(node.right);
            }
            i++;
        }
        return root;
    }

    public static void main(String[] args) {
        BinaryTreeNewTest test = new BinaryTreeNewTest();

        // 测试1：对称二叉树 [1,2,2,3,4,4,3]
        TreeNode root1 = buildTree(new Integer[]{1, 2, 2, 3, 4, 4, 3});
        System.out.println("对称二叉树: " + test.isSymmetric(root1));  // true

        // 测试2：中序遍历 [1,null,2,3]
        TreeNode root2 = buildTree(new Integer[]{1, null, 2, 3});
        System.out.println("中序遍历: " + test.inorderTraversal(root2));  // [1,3,2]

        // 测试3：相同的树
        TreeNode root3a = buildTree(new Integer[]{1, 2, 3});
        TreeNode root3b = buildTree(new Integer[]{1, 2, 3});
        System.out.println("相同的树: " + test.isSameTree(root3a, root3b));  // true

        // 测试4：有序数组转 BST
        int[] nums = {-10, -3, 0, 5, 9};
        TreeNode root4 = test.sortedArrayToBST(nums);
        System.out.println("BST 根节点: " + root4.val);  // 0 或 其他平衡值

        // 测试5：平衡二叉树
        TreeNode root5 = buildTree(new Integer[]{3, 9, 20, null, null, 15, 7});
        System.out.println("平衡二叉树: " + test.isBalanced(root5));  // true
    }
}