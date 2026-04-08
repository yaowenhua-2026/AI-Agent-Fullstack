package org.example.week2_Threadlife.LeetCode;

import java.util.*;

/**
 * 104. 二叉树的最大深度
 */
public class MaximumDepthOfBinaryTree {

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
   static int maxDepthnumbser=0;
    // 解决方案（你来写）
    static class Solution {
        public int maxDepth(TreeNode root) {
            // TODO: 在这里写你的代码
            // 如果是空节点，深度为 0
            if (root == null) {
                return 0;
            }

            // 问左边：你有多深？
            int leftDepth = maxDepth(root.left);

            // 问右边：你有多深？
            int rightDepth = maxDepth(root.right);

            // 我这一层(1) + 左右中更深的那边
            return 1 + Math.max(leftDepth, rightDepth);
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();

        // 测试用例1：root = [3,9,20,null,null,15,7]
        System.out.println("测试用例1：");
        TreeNode root1 = buildTree(new Integer[]{3, 9, 20, null, null, 15, 7});
        System.out.print("二叉树结构: ");
        printTree(root1);
        int result1 = solution.maxDepth(root1);
        System.out.println("最大深度: " + result1);
        System.out.println("期望输出: 3");
        System.out.println();

        // 测试用例2：root = [1,null,2]
        System.out.println("测试用例2：");
        TreeNode root2 = buildTree(new Integer[]{1, null, 2});
        System.out.print("二叉树结构: ");
        printTree(root2);
        int result2 = solution.maxDepth(root2);
        System.out.println("最大深度: " + result2);
        System.out.println("期望输出: 2");
        System.out.println();

        // 测试用例3：root = [] (空树)
        System.out.println("测试用例3：");
        TreeNode root3 = buildTree(new Integer[]{});
        System.out.print("二叉树结构: ");
        printTree(root3);
        int result3 = solution.maxDepth(root3);
        System.out.println("最大深度: " + result3);
        System.out.println("期望输出: 0");
        System.out.println();

        // 测试用例4：root = [1]
        System.out.println("测试用例4：");
        TreeNode root4 = buildTree(new Integer[]{1});
        System.out.print("二叉树结构: ");
        printTree(root4);
        int result4 = solution.maxDepth(root4);
        System.out.println("最大深度: " + result4);
        System.out.println("期望输出: 1");
        System.out.println();

        // 测试用例5：只有左子树 [1,2,3,4]
        System.out.println("测试用例5：");
        TreeNode root5 = buildTree(new Integer[]{1, 2, null, 3, null, 4});
        System.out.print("二叉树结构: ");
        printTree(root5);
        int result5 = solution.maxDepth(root5);
        System.out.println("最大深度: " + result5);
        System.out.println("期望输出: 4");
    }

    /**
     * 根据数组构建二叉树（层序遍历方式）
     * @param nums 层序遍历数组，null表示空节点
     */
    private static TreeNode buildTree(Integer[] nums) {
        if (nums == null || nums.length == 0) return null;

        TreeNode root = new TreeNode(nums[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        int i = 1;
        while (!queue.isEmpty() && i < nums.length) {
            TreeNode node = queue.poll();

            // 处理左子节点
            if (i < nums.length && nums[i] != null) {
                node.left = new TreeNode(nums[i]);
                queue.offer(node.left);
            }
            i++;

            // 处理右子节点
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
