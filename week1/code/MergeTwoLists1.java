package org.example.week1_jvm.LeetCode;

import java.util.LinkedHashMap;

public class MergeTwoLists1 {

    // 链表节点定义（LeetCode 风格）
    public static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    // 在这里写你的答案
    public static ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        // 你的代码写在这里
        ListNode curr=list1;
        ListNode curr1=list2;
        LinkedHashMap<Integer,Integer> map =new LinkedHashMap<>();
        //添加两个元素
        int count=0;
        while (curr1!=null){

            map.put(count,curr1.val);
            curr1=curr1.next;
            count++;
        }
        while (curr!=null){
            map.put(count,curr.val);
            curr=curr.next;
            count++;
        }



            int min=101;
            int index=0;
            ListNode tail=null;
            //记录头指针
            ListNode head=null;

       while (!map.isEmpty()){
           for (Integer key : map.keySet()) {
               //单指针
               if (min>map.get(key)){
                   min=map.get(key);
                   index=key;
               }

           }
           System.out.println(min);
           //说明第一次插入
           if (tail==null){
               tail=new ListNode(min);
               head=tail;
           }else{
               tail.next=new ListNode(min);
               //切换节点
               tail=tail.next;
           }
           //重置索引
            min=101;
           map.remove(index);
       }

        return head;
    }

    // 测试代码
    public static void main(String[] args) {
        // 创建链表1：1 → 2 → 4
        ListNode list1 = new ListNode(1);
        list1.next = new ListNode(2);
        list1.next.next = new ListNode(4);

        // 创建链表2：1 → 3 → 4
        ListNode list2 = new ListNode(1);
        list2.next = new ListNode(3);
        list2.next.next = new ListNode(4);

        // 合并
        ListNode result = mergeTwoLists(list1, list2);

        // 打印结果
        ListNode cur = result;
        while (cur != null) {
            System.out.print(cur.val);
            if (cur.next != null) System.out.print(" → ");
            cur = cur.next;
        }
        // 期望输出：1 → 1 → 2 → 3 → 4 → 4
    }
}