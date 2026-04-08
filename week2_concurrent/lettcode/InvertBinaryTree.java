package org.example.week2_Threadlife.LeetCode;

import java.util.*;

/**
 * 226. 翻转二叉树
 * 题目：翻转一棵二叉树，交换每个节点的左右子节点
 */
public class InvertBinaryTree {

    // 二叉树节点定义
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

    // 解决方案（你来写）
    static class Solution {
        public TreeNode invertTree(TreeNode root) {
            // TODO: 在这里写你的代码
            //如果当前节点是空那么就不需要如何操作
            if (root == null)
                return null;
            //运用递归找到最里面的分支然后进行交换

                 invertTree(root.left);



                invertTree(root.right);


            //交换完后返回当前节点

            TreeNode node = root.left;
            root.left = root.right;
            root.right = node;

            return root;
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();

        // 测试用例1：示例 [4,2,7,1,3,6,9]
        System.out.println("测试用例1：");
        TreeNode root1 = buildTree(new Integer[]{4, 2, 7, 1, 3, 6, 9});
        System.out.print("原树: ");
        printTree(root1);
        TreeNode result1 = solution.invertTree(root1);
        System.out.print("翻转后: ");
        printTree(result1);
        System.out.println("期望: [4,7,2,9,6,3,1]");
        System.out.println();

        // 测试用例2：单节点 [1]
        System.out.println("测试用例2：");
        TreeNode root2 = buildTree(new Integer[]{1});
        System.out.print("原树: ");
        printTree(root2);
        TreeNode result2 = solution.invertTree(root2);
        System.out.print("翻转后: ");
        printTree(result2);
        System.out.println("期望: [1]");
        System.out.println();

        // 测试用例3：空树 []
        System.out.println("测试用例3：");
        TreeNode root3 = buildTree(new Integer[]{});
        System.out.print("原树: ");
        printTree(root3);
        TreeNode result3 = solution.invertTree(root3);
        System.out.print("翻转后: ");
        printTree(result3);
        System.out.println("期望: []");
        System.out.println();

        // 测试用例4：只有左子树 [1,2]
        System.out.println("测试用例4：");
        TreeNode root4 = buildTree(new Integer[]{1, 2});
        System.out.print("原树: ");
        printTree(root4);
        TreeNode result4 = solution.invertTree(root4);
        System.out.print("翻转后: ");
        printTree(result4);
        System.out.println("期望: [1,null,2]");
        System.out.println();

        // 测试用例5：不对称树 [1,2,3,4,5]
        System.out.println("测试用例5：");
        TreeNode root5 = buildTree(new Integer[]{1, 2, 3, 4, 5});
        System.out.print("原树: ");
        printTree(root5);
        TreeNode result5 = solution.invertTree(root5);
        System.out.print("翻转后: ");
        printTree(result5);
        System.out.println("期望: [1,3,2,5,4]");
    }

    /**
     * 根据数组构建二叉树（层序遍历方式）
     */
    private static TreeNode buildTree(Integer[] nums) {
        if (nums == null || nums.length == 0) return null;

        TreeNode root = new TreeNode(nums[0]);
        Queue<TreeNode> queue = new LinkedList<>();
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

    /**
     * 打印二叉树（层序遍历方式）
     */
    private static void printTree(TreeNode root) {
        if (root == null) {
            System.out.println("[]");
            return;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        List<String> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node != null) {
                result.add(String.valueOf(node.val));
                queue.offer(node.left);
                queue.offer(node.right);
            } else {
                result.add("null");
            }
        }

        // 去掉末尾多余的null
        for (int i = result.size() - 1; i >= 0; i--) {
            if (result.get(i).equals("null")) {
                result.remove(i);
            } else {
                break;
            }
        }

        System.out.println(result);
    }
}