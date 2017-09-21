package com.olegsagenadatrytwo.partyapp.view.addpartyactivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddPartyActivityPresenter implements AddPartyActivityContract.presenter {

    private static final String TAG = "AdActivityPresenter";
    private AddPartyActivityContract.view view;
    private Context context;

    private FirebaseDatabase database;

    @Override
    public void attachView(AddPartyActivityContract.view view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void addNewParty(final Party party, Bitmap bitmap) {

        //add new UUID to the party
        UUID id = UUID.randomUUID();
        String idString = id.toString();
        //add the party to the user
        final DatabaseReference profileReference = database.getReference("profiles");
        profileReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("parties").child(idString).setValue(party);

        //add the party to all parties
        DatabaseReference partyReference = database.getReference("parties");
        partyReference.child(idString).setValue(party);

        //add the image of the party to the firebase
        if(bitmap != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");
            StorageReference mountainImagesRef = storageRef.child("images/" + idString + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = mountainImagesRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "onSuccess: " + downloadUrl);
                    //sendMsg("" + downloadUrl, 2);
                    //Log.d("downloadUrl-->", "" + downloadUrl);
                }
            });
        }


        view.partySaved(true);
    }
}
