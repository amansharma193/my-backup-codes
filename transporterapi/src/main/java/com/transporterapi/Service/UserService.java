package com.transporterapi.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.transporterapi.FileUtility;
import com.transporterapi.bean.User;
import com.transporterapi.exception.ResourceNotFoundException;

@Service
public class UserService {
	public static final String COL_NAME = "Users";

	public User updateUser(User user) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		dbFirestore.collection(COL_NAME).document(user.getUserId()).set(user);
		return user;
	}
  //remove auto generated userid...
	public User createUser(User user, MultipartFile file) throws IOException {
		String imageUrl = new FileUtility().uploadFile(file);
		user.setImageUrl(imageUrl);
		Firestore dbFirestore = FirestoreClient.getFirestore();
		dbFirestore.collection(COL_NAME).document(user.getUserId()).set(user);
		return user;
	}

	public User getUserById(String id) throws InterruptedException, ExecutionException {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		User user = dbFirestore.collection(COL_NAME).document(id).get().get().toObject(User.class);
		return user;
	}

	public User deleteUserById(String id) throws ResourceNotFoundException, InterruptedException, ExecutionException {

		Firestore dbFirestore = FirestoreClient.getFirestore();
		User user = dbFirestore.collection(COL_NAME).document(id).get().get().toObject(User.class);
		if (user != null)
			dbFirestore.collection(COL_NAME).document(id).delete();
		return user;
	}

	public ArrayList<User> getAllUsers() throws InterruptedException, ExecutionException {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		ArrayList<User> al = new ArrayList<User>();
		ApiFuture<QuerySnapshot> future = dbFirestore.collection(COL_NAME).get();
		List<QueryDocumentSnapshot> documents;
		documents = future.get().getDocuments();
		for (QueryDocumentSnapshot document : documents) {
			al.add(document.toObject(User.class));
		}
		return al;
	}

	public User updateImage(MultipartFile file, String id)
			throws IOException, InterruptedException, ExecutionException {
		String imageUrl = new FileUtility().uploadFile(file);
		Firestore dbFirestore = FirestoreClient.getFirestore();
		dbFirestore.collection(COL_NAME).document(id).update("imageUrl", imageUrl);
		User user = dbFirestore.collection(COL_NAME).document(id).get().get().toObject(User.class);
		return user;
	}
}