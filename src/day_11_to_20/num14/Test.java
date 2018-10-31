package day_11_to_20.num14;

import java.util.Scanner;



public class Test {
	
	/***
	 * 基本数据类型
	 * 8个
	 * byte
	 * short
	 * int
	 * long
	 * 
	 * float
	 * double
	 * 
	 * char
	 * 
	 * boolean
	 * 
	 * 
	 */
	public static void main (String[] args){
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("你好");
		int ss = scanner.nextInt();
		
		System.out.println(ss);
		
		long startTime = System.currentTimeMillis();
		long start = 	 System.currentTimeMillis();
		for(int i =0;i<1000;i++){
			int j = 8342+3451-10000;
			System.out.println(j);
			i++;
		}
		long endTime = System.currentTimeMillis();
		long nn = endTime-startTime;
		System.out.println(nn);
	}
	
	

	
	public static void main1(String []args){
		long startTime = System.currentTimeMillis();
		for(int i =0;i<1000;i++){
			int j = 8342+3451-1000;
		}
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime-startTime);
	}
	

}
