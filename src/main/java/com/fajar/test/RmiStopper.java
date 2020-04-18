package com.fajar.test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.fajar.dto.RegistryModel;

public class RmiStopper {

	public static void main(String[] args) {
		 
//	        try {
//				Registry reg = LocateRegistry.getRegistry(12345);
//				UnicastRemoteObject.unexportObject(reg, true);
//				System.out.println("- 0000 -");
//				RegistryModel model = (RegistryModel) (reg.lookup("page_req_id"));
//				System.out.println(model.getUserToken());
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				System.out.println("RemoteException");
//				e.printStackTrace();
//			} catch (NotBoundException e) {
//				// TODO Auto-generated catch block
//				System.out.println("NOT BOUND");
//				e.printStackTrace();
//			}
	    
	}
}
