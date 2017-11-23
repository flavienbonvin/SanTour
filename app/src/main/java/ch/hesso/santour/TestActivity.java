package ch.hesso.santour;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import ch.hesso.santour.business.LocationManagement;
import ch.hesso.santour.business.PictureManagement;
import ch.hesso.santour.business.PermissionManagement;
import ch.hesso.santour.business.TrackingManagement;
import ch.hesso.santour.db.DBCallback;
import ch.hesso.santour.db.TrackDB;
import ch.hesso.santour.model.Track;

public class TestActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackingManagement.startTracking(TestActivity.this);
                //Intent intentGetMessage = new Intent(TestActivity.this, PictureManagement.class);
                //startActivityForResult(intentGetMessage, PictureManagement.REQUEST_IMAGE_CAPTURE);

            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManagement.stopTracking(TestActivity.this);
            }
        });


        PermissionManagement.checkMandatoryPermission(this);

        /*
        // lors du click sur le button on ouvre les documents
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                // in onCreate or any event where your want the user to
                // select a file
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });


        // on instancie le stockage firebase (les médias)
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // on instancie le stockage firebase (la DB)
        database = FirebaseDatabase.getInstance();
        // on autorise firebase a créer une copie locale des donées pour travailler sans connexion
        database.setPersistenceEnabled(true);

        // utilisation du seed
        new Seed();
    }

    /*public void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 111);
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PictureManagement.REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("imageBitmap");
            final String imageEncoded = extras.getString("imageString");
            ((ImageView) findViewById(R.id.mImageLabel)).setImageBitmap(imageBitmap);

            TrackDB.getAll(new DBCallback() {
                @Override
                public void resolve(Object o) {
                    List<Track> list = (List<Track>) o;
                    Track mod = list.get(0);
                    for (int i = 0; i < mod.pods.size(); i++) {
                        mod.pods.get(i).setPicture(imageEncoded);
                    }

                    TrackDB.update(mod);
                }
            });

        }
    }
}

    /*
    // pour le retour de la selection de fichier
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                Uri file = selectedImageUri;
                StorageReference riversRef = mStorageRef.child("images/test.jpg");

                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e("Max",exception.getMessage());
                            }
                        });
            }
        }
    }
    */
