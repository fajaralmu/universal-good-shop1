package com.fajar.util;

import java.io.IOException;
import java.sql.*;
import javax.swing.JOptionPane;

//import com.mysql.jdbc.Connection;

public class Koneksi {

	private static String alamatServer, database, username, password;
	private static String port;

	public static Connection connect() throws IOException {
		/*
		 * Konfigurasi konf = new Konfigurasi();
		 * 
		 * Pengaturan p = konf.getPengaturan(); alamatServer =
		 * p.getNamaServer(); port = p.getPort().toString(); database =
		 * p.getDatabase(); username = p.getUsername(); password =
		 * p.getKatasandi();
		 */
		Connection conn = null;
		try {
			
//			 Class.forName("com.mysql.jdbc.Driver"); conn =
//			 DriverManager.getConnection
//			 ("jdbc:mysql://sql3.freemysqlhosting.net:3306/sql3281124",
//			 "sql3281124", "xPds5BbYVy");
			 
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/puskesmasv2", "fajar", "root");
			
			return conn;

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}
}
