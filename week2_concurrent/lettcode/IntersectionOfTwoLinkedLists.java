package org.example.week2_Threadlife.LeetCode;
import java.util.*;

/**
 * 160. 相交链表
 * 题目：找出两个链表相交的起始节点，没有相交返回 null
 */
public class IntersectionOfTwoLinkedLists {

    // 链表节点定义
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) {
            this.val = val;
            this.next = null;
        }
    }


    static class Solution {
        public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
            // TODO: 在这里写你的代码
            //首先出现两种情况 相交or不相交 如果相交那么就说明
            //建立一个hashmap 存入链表节点
            HashSet<ListNode  > map = new HashSet<>();
            //建立key

            //如果一个链表遍历完第二个链表都无论都没有相连接的节点了
            while (headA != null) {
            //第一个链表直接添加
                map.add(headA);
                headA = headA.next;
                //如果和链表中存在内存地址一样的那就说明相交
            }
            while (headB != null) {
                if (map.contains(headB)) {
                    return headB;
                }
                headB = headB.next;
            }
            return null;
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();

        // 测试用例1：相交于 8
        System.out.println("测试用例1：相交于节点 8");
        // 手动构建相交链表
        // 共用部分：8 → 4 → 5
        ListNode common = new ListNode(8);
        common.next = new ListNode(4);
        common.next.next = new ListNode(5);

        // A: 4 → 1 → 共用部分
        ListNode headA = new ListNode(4);
        headA.next = new ListNode(1);
        headA.next.next = common;

        // B: 5 → 6 → 1 → 共用部分
        ListNode headB = new ListNode(5);
        headB.next = new ListNode(6);
        headB.next.next = new ListNode(1);
        headB.next.next.next = common;

        System.out.print("链表A: ");
        printList(headA);
        System.out.print("链表B: ");
        printList(headB);
        ListNode result1 = solution.getIntersectionNode(headA, headB);
        System.out.println("相交节点值: " + (result1 != null ? result1.val : "null"));
        System.out.println("期望: 8");
        System.out.println();

        // 测试用例2：相交于 2
        System.out.println("测试用例2：相交于节点 2");
        ListNode common2 = new ListNode(2);
        common2.next = new ListNode(4);

        ListNode headA2 = new ListNode(1);
        headA2.next = new ListNode(9);
        headA2.next.next = new ListNode(1);
        headA2.next.next.next = common2;

        ListNode headB2 = new ListNode(3);
        headB2.next = common2;

        System.out.print("链表A: ");
        printList(headA2);
        System.out.print("链表B: ");
        printList(headB2);
        ListNode result2 = solution.getIntersectionNode(headA2, headB2);
        System.out.println("相交节点值: " + (result2 != null ? result2.val : "null"));
        System.out.println("期望: 2");
        System.out.println();

        // 测试用例3：不相交
        System.out.println("测试用例3：不相交");
        ListNode headA3 = new ListNode(2);
        headA3.next = new ListNode(6);
        headA3.next.next = new ListNode(4);

        ListNode headB3 = new ListNode(1);
        headB3.next = new ListNode(5);

        System.out.print("链表A: ");
        printList(headA3);
        System.out.print("链表B: ");
        printList(headB3);
        ListNode result3 = solution.getIntersectionNode(headA3, headB3);
        System.out.println("相交节点值: " + (result3 != null ? result3.val : "null"));
        System.out.println("期望: null");
        System.out.println();

        // 测试用例4：完全相同的链表（从头相交）
        System.out.println("测试用例4：完全相同的链表");
        ListNode headA4 = new ListNode(1);
        headA4.next = new ListNode(2);
        headA4.next.next = new ListNode(3);
        ListNode headB4 = headA4;  // 指向同一个链表

        System.out.print("链表A: ");
        printList(headA4);
        System.out.print("链表B: ");
        printList(headB4);
        ListNode result4 = solution.getIntersectionNode(headA4, headB4);
        System.out.println("相交节点值: " + (result4 != null ? result4.val : "null"));
        System.out.println("期望: 1");
        System.out.println();

        // 测试用例5：一个链表为空
        System.out.println("测试用例5：一个链表为空");
        ListNode result5 = solution.getIntersectionNode(null, new ListNode(1));
        System.out.println("相交节点值: " + (result5 != null ? result5.val : "null"));
        System.out.println("期望: null");
    }

    private static void printList(ListNode head) {
        ListNode current = head;
        int count = 0;
        while (current != null && count < 20) {
            System.out.print(current.val);
            if (current.next != null) System.out.print(" → ");
            current = current.next;
            count++;
        }
        System.out.println();
    }
}