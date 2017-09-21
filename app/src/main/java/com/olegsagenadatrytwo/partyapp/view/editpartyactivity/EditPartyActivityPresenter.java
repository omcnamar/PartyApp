package com.olegsagenadatrytwo.partyapp.view.editpartyactivity;

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

/**
 * Created by omcna on 9/21/2017.
 */

public class EditPartyActivityPresenter implements EditPartyActivityContract.presenter {

    public static final String TAG = "EditPartyPres";
    private FirebaseDatabase database;
    private EditPartyActivityContract.view view;
    private Context context;

    @Override
    public void attachView(EditPartyActivityContract.view view) {
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
    public void editParty(Party party, Bitmap bitmap) {

        Log.d(TAG, "editParty: " + party.getId());
        //Edit the party to the user
        final DatabaseReference profileReference = database.getReference("profiles");
        profileReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("parties").child(party.getId()).setValue(party);

        //edit the party to all parties
        DatabaseReference partyReference = database.getReference("parties");
        partyReference.child(party.getId()).setValue(party);

        //add the image of the party to the firebase
        if(bitmap != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");
            StorageReference mountainImagesRef = storageRef.child("images/" + party.getId() + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);
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


        view.partyEdited(true);
    }
}
