/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.caterpillar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.caterpillar.camera.CameraSourcePreview;
import com.example.caterpillar.camera.GraphicOverlay;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.CameraSource.PictureCallback;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.server.interaction.SocketManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.google.android.gms.vision.face.FaceDetector.ACCURATE_MODE;
import static com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS;
import static java.lang.StrictMath.abs;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public final class InitialiseFaceActivity<string> extends AppCompatActivity {
    private static final String TAG = "FaceTracker";
    private Socket mSocket;
    private SocketManager app;


    String userID = "USER";


    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private StorageReference mStorageRef;
    private int sentImageCount=0;
    private int imageCapturedCount=0;

    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_detection);


        mStorageRef = FirebaseStorage.getInstance().getReference();

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        app = (SocketManager) getApplication();
        mSocket = app.getmSocket();
        if (mSocket.connected()) {
            Toast.makeText(this, "Connected!!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not Connected!!", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setMinFaceSize(0.2f)
                .setMode(ACCURATE_MODE)
                .setProminentFaceOnly(true)
                .setLandmarkType(ALL_LANDMARKS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();



    }



    protected static final int MEDIA_TYPE_IMAGE = 0;
    private void takePhoto() {
//        MediaActionSound sound = null;


        mCameraSource.takePicture(null, new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data) {
                File picFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (picFile == null) {
                    Log.e(TAG, "Couldn't create media file; check storage permissions?");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(picFile);
                    fos.write(data);
                    fos.close();
                    uploadImage(picFile);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "File not found: " + e.getMessage());
                    e.getStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, "I/O error writing file: " + e.getMessage());
                    e.getStackTrace();
                }

            }
        });

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
////            sound = new MediaActionSound();
////            sound.play(MediaActionSound.SHUTTER_CLICK);
//
//        }
    }

    private File getOutputMediaFile(int type) {
        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), userID);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "Failed to create storage directory.");
                return null;
            }
        }
        String timeStamp =
                new SimpleDateFormat("yyyMMdd_HHmmssSS", Locale.UK).format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            return new File(dir.getPath() + File.separator + "IMG_"
                    + timeStamp + ".jpg");
        } else {
            return null;
        }
    }

    private void sendImage(String path)
    {
        JSONObject sendData = new JSONObject();
        Log.i("file name:", path);
        try{
            sendData.put("imageData", encodeImage(path));
            sendData.put("userID", userID);
            mSocket.emit("train_image",sendData);
        }catch(Exception e){
            return;
        }
    }

    private void sendURL(String url)
    {
        try{
            mSocket.emit("train_image",url);
        }catch(Exception e){
            return;
        }
    }

    private void uploadImage(final File filename)
    {   Uri fileUri = Uri.fromFile(filename);
        final StorageReference fileRef = mStorageRef.child(userID + "/" + filename.getName());
        UploadTask uploadTask = fileRef.putFile(fileUri);
        imageCapturedCount++;
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    Log.i("URL", downloadUri.toString());
                    sendURL(downloadUri.toString());
                    filename.delete();
                    sentImageCount++;
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
    private void delFolder (File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                file.delete();
                // do something here with the file

            }

        }
        dir.delete();
    }
    private String encodeImage(String path)
    {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,80,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.NO_WRAP);
        Log.i("Image base64", encImage);
        //Base64.de
        return encImage;
    }

    private String getBase64Data(String filePath) {
        try {
            InputStream inputStream = new FileInputStream(filePath);//You can get an inputStream using any IO API
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();
            Log.i("image base64",   Base64.encodeToString(bytes, Base64.DEFAULT));
            return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    private void sendFolder (File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];

                uploadImage(file);
                file.delete();
                    // do something here with the file

            }
            try {
                mSocket.emit("train_model", "finished uploading. train model");
            } catch (Exception e){
                return;

            }
        }
        dir.delete();
//        if (dir.delete()){
//            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
//        } else{
//            Toast.makeText(this, "Not deleted", Toast.LENGTH_SHORT).show();
//        }
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
//        dispatchTakePictureIntent();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
        try {

            mSocket.emit("train_model", "finished uploading. train model");
        } catch(Exception e){

        }
        Log.i("socketio", "sent train command");
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private int midPhotoCount=0;
    private int leftPhotoCount=0;
    private int rightPhotoCount=0;
    private class GraphicFaceTracker extends Tracker<Face>{
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;
        private int frameCount=0;


        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay
         */

        @Override
        public void onNewItem(int faceId, Face item) {

            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);

            if (midPhotoCount<3 || leftPhotoCount<3 || rightPhotoCount<3){
                if (frameCount>0) {
                    if (abs(face.getEulerY())<3 && midPhotoCount<3) {
                        takePhoto();
                        midPhotoCount++;
                    }else if (face.getEulerY()>3 && leftPhotoCount<3) {
                        takePhoto();
                        leftPhotoCount++;
                    }else if (face.getEulerY()<3 && rightPhotoCount<3) {
                        takePhoto();
                        rightPhotoCount++;
                    }
                }

            } else{
//                mCameraSource.stop();
//                mCameraSource.release();
//                FaceTrackerActivity.this.onPause();
//                FaceTrackerActivity.this.onStop();
//                FaceTrackerActivity.this.onDestroy();
                while (imageCapturedCount!=sentImageCount) {
                }
                InitialiseFaceActivity.this.finish();
//                finish();
//                Intent intent2 = new Intent(FaceTrackerActivity.this, LoginActivity.class);
//                startActivity(intent2);
            }
            frameCount++;
//            private mShuttleCallback = mCameraSource.ShutterCallback;
//            mCameraSource.TakePicture(, mCameraSource.PictureCallback)
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }
}
