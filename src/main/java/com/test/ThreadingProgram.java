package com.test;

public class ThreadingProgram {
	
	// Constants for beginning middle and end of ASCII uppercase alphabet
	public static final int  ASCII_BEGIN = 65;
	public static final int  ASCII_MIDDLE = 77;
	public static final int  ASCII_END = 90;
	
	public static class AtoMThread implements Runnable{

		@Override
		public void run() {
			int i = ASCII_BEGIN; // Beginning of ASCII Uppercase alphabet
			
			while(i <= ASCII_MIDDLE) {
				System.out.println(Character.toString((char)i));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
		}
		
	}
	
	public static class NtoZThread implements Runnable{

		@Override
		public void run() {
			int i = ASCII_MIDDLE + 1;
			
			while(i <= ASCII_END) {
				System.out.println(Character.toString((char)i));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
		}
	}
	
	public static void main(String args[]) {
		// Initialize the inner classes for alphabet threads
		AtoMThread AtoMthread = new AtoMThread();
		NtoZThread NtoZthread = new NtoZThread();
		
		// Lambdas for the integer threads
		Runnable oneToTwenty = () ->{
			int i = 1;
			
			while(i <= 20) {
				System.out.println(i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
		};
		
		Runnable twentyToFourty = () ->{
			int i = 20;
			
			while(i <= 40) { 
				System.out.println(i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
		};
		
		Runnable fourtyToSixty = () ->{
			int i = 40;
			
			while(i <= 60) {
				System.out.println(i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
		};
		
		//Initialize the threads
		Thread count1to20  	= new Thread(oneToTwenty);
		Thread count20to40 	= new Thread(twentyToFourty);
		Thread count40to60 	= new Thread(fourtyToSixty);
		Thread printAtoM 	= new Thread(AtoMthread);
		Thread printNtoZ	= new Thread(NtoZthread);
		
		// Start all of the threads
		count1to20.start();
		count20to40.start();
		count40to60.start();
		printAtoM.start();
		printNtoZ.start();
	}
}
