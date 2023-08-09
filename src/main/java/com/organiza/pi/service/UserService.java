package com.organiza.pi.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.organiza.pi.model.User;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    Firestore dbFirestore = FirestoreClient.getFirestore();

    public CompletableFuture<List<User>> getAll() {
        ApiFuture<QuerySnapshot> future = dbFirestore.collection("user").get();

        return CompletableFuture.supplyAsync(() -> {
            try {
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                List<User> users = new ArrayList<>();

                for (QueryDocumentSnapshot document : documents) {
                    User user = document.toObject(User.class);
                    users.add(user);
                }
                return users;
            } catch (Exception e) {
                throw new RuntimeException("Erro ao buscar os usuários", e);
            }
        });
    }

    public Optional<User> getById(String id) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("user").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            User user = document.toObject(User.class);
            return Optional.of(user);
        }

        return Optional.empty();


    }


    public User create(User user) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("user").add(user).get();
        user.setId(documentReference.getId());
        documentReference.set(user);
        return user;
    }
}
