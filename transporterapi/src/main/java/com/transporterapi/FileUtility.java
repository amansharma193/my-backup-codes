package com.transporterapi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class FileUtility {
	public String uploadFile(MultipartFile file) throws IOException {
		InputStream serviceAccount = this.getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
		Storage storage = StorageOptions.newBuilder().setProjectId("transporter-vendor")
				.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build().getService();
		HashMap<String, String> hm = new HashMap<>();
		hm.put("firebaseStorageDownloadTokens", "3434434343434dfdf");
		BlobId blobId = BlobId.of("transporter-vendor.appspot.com", file.getOriginalFilename());
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").setMetadata(hm).build();
		File convertedFile = new File(file.getOriginalFilename());
		FileOutputStream fos;
		fos = new FileOutputStream(convertedFile);
		fos.write(file.getBytes());
		fos.close();
		Blob blob = storage.create(blobInfo, Files.readAllBytes(convertedFile.toPath()));
		String imageUrl = "https://firebasestorage.googleapis.com/v0/b/transporter-vendor.appspot.com/o/"
				+ convertedFile + "?alt=media&token=3434434343434dfdf";
		return imageUrl;
	}
}
