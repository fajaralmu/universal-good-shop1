//package com.fajar.test;
//
//import java.rmi.NotBoundException;
//import java.rmi.Remote;
//import java.rmi.RemoteException;
//import java.rmi.registry.Registry;
//
//public class TestRMI {
//	
//	public static void main (String[] ss) {
//		 try {
//				Registry reg = java.rmi.registry.LocateRegistry.getRegistry("192.168.43.241",12345);
//				Remote r = reg.lookup("product");
//				
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (NotBoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	}
//	public static void maincc(String[] ss) {
//		 try {
//				Registry reg = java.rmi.registry.LocateRegistry.createRegistry(12345);
//				reg.rebind("product", (Remote) new RMIObj());
//				System.out.println((RMIObj)reg.lookup("product"));
//				System.out.println("OK");
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (NotBoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}  
//	}
//
//}
