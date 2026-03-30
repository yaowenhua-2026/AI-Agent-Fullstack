package org.example.week1_jvm.LeetCode;

public class hasCycle1 {
    static class ListNode {
        int val;
        ListNode next;  // 改成自己的 ListNode
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }
    public boolean hasCycle(ListNode head) {
        if (head==null){
            return false;
        }
        ListNode slow =head;
        ListNode fast=head;
        while (fast!=null&&fast.next!=null){
            slow=slow.next;
            fast=fast.next.next;
            if (fast==slow){
                return true;
            }

        }

        return false;
    }
}
