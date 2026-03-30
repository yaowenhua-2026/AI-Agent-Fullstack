package org.example.week1_jvm.LeetCode;

import java.util.*;

/**
 * 链表反转 - 完整测试代码
 */
public class ReverseLinkedList {

    // 链表节点定义
    static class ListNode {
        int val;
        ListNode next;

        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    // 反转链表方法（核心）
    public static ListNode reverseList(ListNode head) {
     ListNode curr=head;
     ListNode preve=null;
     //遍历链表
     while (curr!=null){
         //记录下一个指针 免得反转后链表断裂
         ListNode next=curr.next;
         //指针反转
         //让当前指针指向反转
         curr.next=preve;
         preve=curr;
         curr=next;

     }
        return preve;
    }

    // 辅助方法：打印链表
    public static void printList(ListNode head) {
        ListNode curr = head;
        while (curr != null) {
            System.out.print(curr.val);
            if (curr.next != null) {
                System.out.print(" → ");
            }
            curr = curr.next;
        }
        System.out.println();
    }

    // 辅助方法：创建链表（从数组）
    public static ListNode createList(int[] arr) {
        if (arr.length == 0) return null;
        ListNode head = new ListNode(arr[0]);
        ListNode curr = head;
        for (int i = 1; i < arr.length; i++) {
            curr.next = new ListNode(arr[i]);
            curr = curr.next;
        }
        return head;
    }

    // 测试
    public static void main(String[] args) {
        // 测试数据1：1→2→3→4→5
        System.out.println("===== 测试1 =====");
        int[] arr1 = {1, 2, 3, 4, 5};
        ListNode list1 = createList(arr1);
        System.out.print("原链表: ");
        printList(list1);

        ListNode reversed1 = reverseList(list1);
        System.out.print("反转后: ");
        printList(reversed1);
        System.out.println();

        // 测试数据2：1→2
        System.out.println("===== 测试2 =====");
        int[] arr2 = {1, 2};
        ListNode list2 = createList(arr2);
        System.out.print("原链表: ");
        printList(list2);

        ListNode reversed2 = reverseList(list2);
        System.out.print("反转后: ");
        printList(reversed2);
        System.out.println();

        // 测试数据3：只有一个节点
        System.out.println("===== 测试3 =====");
        int[] arr3 = {1};
        ListNode list3 = createList(arr3);
        System.out.print("原链表: ");
        printList(list3);

        ListNode reversed3 = reverseList(list3);
        System.out.print("反转后: ");
        printList(reversed3);
        System.out.println();

        // 测试数据4：空链表
        System.out.println("===== 测试4 =====");
        ListNode list4 = null;
        System.out.print("原链表: ");
        printList(list4);

        ListNode reversed4 = reverseList(list4);
        System.out.print("反转后: ");
        printList(reversed4);
    }
}