package org.example.week2_Threadlife.LeetCode;

import java.math.BigInteger;

// 把公共类名改成通俗易懂的名字
public class AddLinkTwoNumbers {

    // ListNode 定义保持不变
    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    // 解决方案类（可以保持叫 Solution，也可以改名）
    static class Solution {
        public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
            //首先定义一个字符串接收倒序的链表
            StringBuffer sb = new StringBuffer();
            //完成链表1的取值
            while (l1 !=null ) {
                sb.append(l1.val);
                l1 = l1.next;
            }
            //将链表一点值提取出来
            String link1=sb.reverse().toString();
            //测试输出结果

            //清除容器
            sb = new StringBuffer();
            while (l2 !=null ) {
                sb.append(l2.val);
                l2 = l2.next;
            }
            String link2=sb.reverse().toString();

            // 使用 BigInteger 避免溢出
            BigInteger num1 = new BigInteger(link1);
            BigInteger num2 = new BigInteger(link2);
            BigInteger sum = num1.add(num2);

            //创建新的链表头
            ListNode head =null;

            //将sum的值提取出来分给新的链表
            for (char c:String.valueOf(sum).toCharArray()) {
                    ListNode newNode = new ListNode(c-'0');
                    newNode.next=head;
                    head=newNode;
            }
            //真正的头节点
            return head;
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();

        // 测试代码...
        System.out.println("两数相加（链表）测试");

        // 测试用例1：342 + 465 = 807
        ListNode l1 = createList(new int[]{2, 4, 3});
        ListNode l2 = createList(new int[]{5, 6, 4});
        System.out.print("342 + 465 = ");
        printList(solution.addTwoNumbers(l1, l2));
    }

    // 辅助方法
    static ListNode createList(int[] nums) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int num : nums) {
            cur.next = new ListNode(num);
            cur = cur.next;
        }
        return dummy.next;
    }

    static void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val);
            if (head.next != null) System.out.print(" -> ");
            head = head.next;
        }
        System.out.println();
    }
}