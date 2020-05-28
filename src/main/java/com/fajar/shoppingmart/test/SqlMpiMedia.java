package com.fajar.shoppingmart.test;

public class SqlMpiMedia {
	
	static final String sqlInsert = "INSERT INTO mpi_db.dbo.section\r\n" + 
			"(name,  division_id, created_date )\r\n" + 
			"VALUES('Divisi Syiar',   ${instId}, CURRENT_TIMESTAMP );\r\n" + 
			"INSERT INTO mpi_db.dbo.section\r\n" + 
			"(name,  division_id, created_date)\r\n" + 
			"VALUES('Divisi Media',   ${instId}, CURRENT_TIMESTAMP );\r\n" + 
			"INSERT INTO mpi_db.dbo.section\r\n" + 
			"(name,   division_id, created_date )\r\n" + 
			"VALUES('Divisi Humas',   ${instId}, CURRENT_TIMESTAMP );\r\n" + 
			"INSERT INTO mpi_db.dbo.section\r\n" + 
			"(name,   division_id, created_date)\r\n" + 
			"VALUES('Divisi Kaderisasi',  ${instId}, CURRENT_TIMESTAMP );\r\n" + 
			"INSERT INTO mpi_db.dbo.section\r\n" + 
			"(name,  division_id, created_date )\r\n" + 
			"VALUES('Pengurus Harian',   ${instId}, CURRENT_TIMESTAMP);\r\n" + 
			"";
	
	static final String sqlInsertProgram = "INSERT INTO mpi_db.dbo.program\r\n" + 
			"(name, description, sect_id, created_date )\r\n" + 
			"VALUES('Kajian', 'test', 3, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.program\r\n" + 
			"(name, description, sect_id, created_date, id)\r\n" + 
			"VALUES('Training', 'ddd', 5, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.program\r\n" + 
			"(name, description, sect_id, created_date )\r\n" + 
			"VALUES('Kajian Pekanan', 'sdsd', 13, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.program\r\n" + 
			"(name, description, sect_id, created_date )\r\n" + 
			"VALUES('Halaqah Umum', 'halaqah umum kader', 16, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.program\r\n" + 
			"(name, description, sect_id, created_date )\r\n" + 
			"VALUES('Rihlah', 'jalan jalan', 16, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.program\r\n" + 
			"(name, description, sect_id, created_date)\r\n" + 
			"VALUES('Mabit', 'malam bina iman taqwa', 16, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.program\r\n" + 
			"(name, description, sect_id, created_date )\r\n" + 
			"VALUES('Kunjungan Oranisasi', 'kunjungan ke organisasi pergerakan lain', 15, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.program\r\n" + 
			"(name, description, sect_id, created_date )\r\n" + 
			"VALUES('Tablig Akbar', '123', 13, NULL );\r\n" + 
			"INSERT INTO mpi_db.dbo.program\r\n" + 
			"(name, description, sect_id, created_date  )\r\n" + 
			"VALUES('Posting Rutin', '', 14, NULL );\r\n" + 
			"";

	public static void main(String[] args) {
		
		for (int i = 48; i <= 58; i++) {
			String sql = sqlInsert.replace("${instId}", String.valueOf(i));
			System.out.println(sql);
		}
	}
}
