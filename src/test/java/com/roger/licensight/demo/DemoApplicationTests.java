package com.roger.licensight.demo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;

import java.io.File;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	private static final long MAX_FILE_SIZE = 1 * 1024 * 1024; // 1MB
	private static final String LOG_DIR = "log";
	private static final String BASE_LOG_NAME = LOG_DIR + File.separator + "log.txt";

	@Test
	void ReadNexusMavenRepositoryIndex() {
		String indexPath = "C:\\Users\\ENGUYEHW0\\Downloads\\maven\\lucene-index";

		try (FSDirectory dir = FSDirectory.open(Paths.get(indexPath));
				IndexReader reader = DirectoryReader.open(dir)) {

			IndexSearcher searcher = new IndexSearcher(reader);

				for (int i = 0; i < reader.maxDoc(); i++) {
				String u = "";
				try {
					Document doc = reader.document(i);

					u = doc.get("u");

					if (u != null) {
						String[] parts = u.split(":");
						try {
							if (parts.length >= 2) {
								System.out.println(parts[0] + ":" + parts[1]);
								logToFile(parts[0] + ":" + parts[1]);
							}
						}catch (Exception e) {
							System.out.println("Lỗi case này: " + parts[0] + ":" + parts[1]);
						}

					}

				} catch (Exception e) {
					System.out.println("Lỗi doc.get(\"u\") " + e);
				}
				}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static void logToFile(String content) {
		if (content == null || content.trim().isEmpty()) return;

		try {
			// Tạo thư mục log nếu chưa tồn tại
			Files.createDirectories(Paths.get(LOG_DIR));

			File logFile = new File(BASE_LOG_NAME);

			if (logFile.exists() && logFile.length() >= MAX_FILE_SIZE) {
				int index = 1;
				File rotatedFile;
				do {
					rotatedFile = new File(LOG_DIR + File.separator + "log" + index + ".txt");
					index++;
				} while (rotatedFile.exists());

				if (!logFile.renameTo(rotatedFile)) {
					System.err.println("Không thể đổi tên " + BASE_LOG_NAME + " thành " + rotatedFile.getName());
					return;
				}
			}

			// Ghi nội dung log vào file log.txt
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
				writer.write(content);
				writer.newLine();
			}

		} catch (IOException e) {
			System.err.println("Lỗi khi ghi log: " + e.getMessage());
		}
	}


}

//java -Xmx4G -jar indexer-cli-6.2.2.jar --unpack nexus-maven-repository-index.gz --destination lucene-index --type full
/*
* https://maven.apache.org/repository/central-index.html
*
* ===Maven index===
https://github.com/aria2/aria2/releases
C:\Users\ENGUYEHW0\Downloads\maven\aria2-1.37.0-win-64bit-build1>
.\aria2c.exe https://repo1.maven.org/maven2/.index/nexus-maven-repository-index.gz

https://maven.apache.org/repository/central-index.html

PS C:\Users\ENGUYEHW0\Downloads\maven>
* java -Xmx4G -jar indexer-cli-6.2.2.jar --unpack nexus-maven-repository-index.gz --destination lucene-index --type full

luke-with-deps.tar
/mnt/c/Users/ENGUYEHW0/Downloads/maven$ ./luke.sh -index ./central-lucene-index

===Maven index===
* */