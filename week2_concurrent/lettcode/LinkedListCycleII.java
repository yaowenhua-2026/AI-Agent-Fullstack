package org.example.week2_Threadlife.LeetCode;

import java.util.*;

/**
 * 142. 环形链表 II
 * 题目：给定一个链表，返回链表开始入环的第一个节点。如果无环，返回 null。
 */
public class LinkedListCycleII {

    // 链表节点定义
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) {
            this.val = val;
            this.next = null;
        }
    }

    // 解决方案（你需要实现的方法）
    static class Solution {
        public ListNode detectCycle(ListNode head) {
            // TODO: 在这里写你的代码
            if (head == null || head.next == null) return null;

            // 步骤1：判断是否有环，找到相遇点
            ListNode slow = head;
            ListNode fast = head;
            boolean hasCycle = false;

            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
                if (slow == fast) {
                    hasCycle = true;
                    break;
                }
            }

            // 无环，直接返回 null
            if (!hasCycle) return null;

            // 步骤2：找到环入口
            ListNode ptr = head;  // 从头开始
            while (ptr != slow) {  // 同时移动，相遇点就是入口
                ptr = ptr.next;
                slow = slow.next;
            }

            return ptr;
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();

        // 测试用例1：有环，环入口在节点2
        System.out.println("测试用例1：有环链表，入口在节点2");
        ListNode head1 = createLinkedListWithCycle(new int[]{3, 2, 0, -4}, 1);
        System.out.print("链表: ");
        printList(head1, 10);
        ListNode result1 = solution.detectCycle(head1);
        System.out.println("环入口节点值: " + (result1 != null ? result1.val : "null"));
        System.out.println("期望结果: 2");
        System.out.println();

        // 测试用例2：有环，环入口在节点0
        System.out.println("测试用例2：有环链表，入口在节点0");
        ListNode head2 = createLinkedListWithCycle(new int[]{1, 2}, 0);
        System.out.print("链表: ");
        printList(head2, 10);
        ListNode result2 = solution.detectCycle(head2);
        System.out.println("环入口节点值: " + (result2 != null ? result2.val : "null"));
        System.out.println("期望结果: 1");
        System.out.println();

        // 测试用例3：无环
        System.out.println("测试用例3：无环链表");
        ListNode head3 = createLinkedListWithCycle(new int[]{1}, -1);
        System.out.print("链表: ");
        printList(head3, 10);
        ListNode result3 = solution.detectCycle(head3);
        System.out.println("环入口节点值: " + (result3 != null ? result3.val : "null"));
        System.out.println("期望结果: null");
        System.out.println();

        // 测试用例4：单个节点无环
        System.out.println("测试用例4：单个节点，无环");
        ListNode head4 = new ListNode(1);
        System.out.print("链表: ");
        printList(head4, 10);
        ListNode result4 = solution.detectCycle(head4);
        System.out.println("环入口节点值: " + (result4 != null ? result4.val : "null"));
        System.out.println("期望结果: null");
        System.out.println();

        // 测试用例5：两个节点有环
        System.out.println("测试用例5：两个节点，环入口在节点0");
        ListNode head5 = createLinkedListWithCycle(new int[]{1, 2}, 0);
        System.out.print("链表: ");
        printList(head5, 10);
        ListNode result5 = solution.detectCycle(head5);
        System.out.println("环入口节点值: " + (result5 != null ? result5.val : "null"));
        System.out.println("期望结果: 1");
    }

    /**
     * 创建链表（可选环）
     * @param values 节点值数组
     * @param pos 环入口索引（-1表示无环，0表示第一个节点）
     */
    private static ListNode createLinkedListWithCycle(int[] values, int pos) {
        if (values.length == 0) return null;

        ListNode head = new ListNode(values[0]);
        ListNode current = head;
        ListNode cycleNode = null;

        if (pos == 0) cycleNode = head;

        for (int i = 1; i < values.length; i++) {
            current.next = new ListNode(values[i]);
            current = current.next;
            if (i == pos) cycleNode = current;
        }

        if (pos != -1 && cycleNode != null) {
            current.next = cycleNode;
        }

        return head;
    }

    /**
     * 打印链表（限制打印次数避免死循环）
     */
    private static void printList(ListNode head, int maxCount) {
        ListNode current = head;
        int count = 0;
        Set<ListNode> visited = new HashSet<>();

        while (current != null && count < maxCount && !visited.contains(current)) {
            visited.add(current);
            System.out.print(current.val);
            if (current.next != null && !visited.contains(current.next)) {
                System.out.print(" → ");
            }
            current = current.next;
            count++;
        }

        if (current != null && count < maxCount) {
            System.out.print(" → ... (继续循环)");
        }
        System.out.println();
    }
}