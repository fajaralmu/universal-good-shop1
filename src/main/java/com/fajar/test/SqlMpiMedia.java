package com.fajar.test;

public class SqlMpiMedia {
	
	static final String sqlInsert = "INSERT INTO mpi_db.dbo.division\r\n" + 
			"(name, description, institution_id, created_date )\r\n" + 
			"VALUES('Divisi Syiar', 'desc divisi SYiar', ${instId}, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.division\r\n" + 
			"(name, description, institution_id, created_date)\r\n" + 
			"VALUES('Divisi Media', 'Desc divisi media', ${instId}, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.division\r\n" + 
			"(name, description, institution_id, created_date )\r\n" + 
			"VALUES('Divisi Humas', 'desc divisi humas', ${instId}, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.division\r\n" + 
			"(name, description, institution_id, created_date)\r\n" + 
			"VALUES('Divisi Kaderisasi', 'Desc Divisi Kaderisasi', ${instId}, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.division\r\n" + 
			"(name, description, institution_id, created_date )\r\n" + 
			"VALUES('Pengurus Harian', 'ketum, sekjen, bendum', ${instId}, NULL);\r\n" + 
			"";

	public static void main(String[] args) {
		
		for (int i = 2; i <= 9; i++) {
			String sql = sqlInsert.replace("${instId}", String.valueOf(i));
			System.out.println(sql);
		}
	}
}
