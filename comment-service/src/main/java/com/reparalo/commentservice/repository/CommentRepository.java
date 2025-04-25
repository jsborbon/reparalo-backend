package com.reparalo.commentservice.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.reparalo.commentservice.model.Comment;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Repository
public class CommentRepository {

    private final Firestore firestore;
    private final CollectionReference commentsCollection;

    public CommentRepository(Firestore firestore) {
        this.firestore = firestore;
        this.commentsCollection = firestore.collection("comments");
    }

    public Comment save(Comment comment) throws ExecutionException, InterruptedException {
        if (comment.getId() == null) {
            comment.setId(UUID.randomUUID().toString());
            comment.setCreatedAt(LocalDateTime.now());
        }
        comment.setUpdatedAt(LocalDateTime.now());
        
        commentsCollection.document(comment.getId()).set(comment).get();
        return comment;
    }

    public Optional<Comment> findById(String id) throws ExecutionException, InterruptedException {
        var documentSnapshot = commentsCollection.document(id).get().get();
        if (documentSnapshot.exists()) {
            return Optional.of(documentSnapshot.toObject(Comment.class));
        }
        return Optional.empty();
    }

    public List<Comment> findByTargetIdAndTargetType(String targetId, String targetType) throws ExecutionException, InterruptedException {
        return commentsCollection
                .whereEqualTo("targetId", targetId)
                .whereEqualTo("targetType", targetType)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .get()
                .toObjects(Comment.class);
    }

    public List<Comment> findByUserId(String userId) throws ExecutionException, InterruptedException {
        return commentsCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .get()
                .toObjects(Comment.class);
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        commentsCollection.document(id).delete().get();
    }
}