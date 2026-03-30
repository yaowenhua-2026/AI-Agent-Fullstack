package org.example.week1_jvm.LeetCode;

public class RemoveNthFromEnd {

    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {
        // 你的代码写在这里
        if (head==null){
            return null;
        }
        ListNode dummy = new ListNode(0, head);
        //记录块指针
        ListNode fast=dummy;
        //记录满指针
        ListNode solw=dummy;
        //哑节点

        //用循环让块指针先走
        for (int i = 0; i <n ; i++) {
            fast=fast.next;
        }
        //块指针已经走完
        //然后遍历数组
        while (fast.next != null){
            fast=fast.next;
            solw=solw.next;
        }
        //遍历到了终点 那么 solw刚刚好等于n
        solw.next=solw.next.next;
        return dummy.next;
    }

    public static void main(String[] args) {
        // 创建链表：1 → 2 → 3 → 4 → 5
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);

        RemoveNthFromEnd solution = new RemoveNthFromEnd();
        ListNode result = solution.removeNthFromEnd(head, 2);

        // 打印：1 → 2 → 3 → 5
        ListNode cur = result;
        while (cur != null) {
            System.out.print(cur.val);
            if (cur.next != null) System.out.print(" → ");
            cur = cur.next;
        }
    }
}