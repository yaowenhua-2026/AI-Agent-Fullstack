package org.example.week1_jvm.LeetCode;

import java.util.Arrays;
import java.util.HashMap;

public class twosum1 {
    public static void main(String[] args) {
        int[] nums={2,7,11,15};
        int target=9;

        System.out.println(Arrays.toString(twosum(nums, target)));
    }
    private static int[] twosum(int[] nums,int target){
        //创建hashmap
        HashMap<Integer,Integer> map=new HashMap<>();
        //遍历nums
        for (int i = 0; i <nums.length ; i++) {
            int need=target-nums[i];

            if (map.containsKey(need)){
                return new int[]{i,map.get(need)};
            }else {
                map.put(nums[i],i);
            }
        }
        return new int[]{};
    }
}
