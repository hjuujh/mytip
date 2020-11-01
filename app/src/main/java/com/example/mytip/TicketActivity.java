package com.example.mytip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.internal.$Gson$Preconditions;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketActivity extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyAcp7F8b_uKzZMB3dU3yHbrmu0CCfWGRQE";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = TicketActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;//chae - 나중에 없앨것
    private ImageView mMainImage;
    private ProgressBar bar;
    private Button upbtn;

    private static String uid, date, title, place, seating;
    private static Uri imgUri;
    private FirebaseAuth firebaseAuth;

    Bitmap bitmap;//티켓사진
    boolean next;//자르기 했는지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TicketActivity.this);
            builder
                    .setMessage(R.string.dialog_select_prompt)
                    .setPositiveButton("갤러리", (dialog, which) -> startGalleryChooser())
                    .setNegativeButton("카메라", (dialog, which) -> startCamera());
            builder.create().show();
        });

        mImageDetails = findViewById(R.id.image_details);
        mMainImage = findViewById(R.id.main_image);
        bar = findViewById(R.id.progressBar);
        Button upbtn = findViewById(R.id.upload);

//        id = getIntent().getStringExtra("id");
        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        System.out.println("#################");
        System.out.println(uid);
        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(next==false)
                    Toast.makeText(TicketActivity.this, "사진을 먼저 잘라주세요", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("title", title);
                    intent.putExtra("place", place);
                    intent.putExtra("date", date);
                    intent.putExtra("seat", seating);
                    try {
                        imgUri = getImgUri(getApplicationContext(),bitmap,Bitmap.CompressFormat.JPEG);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgUpload();
                    startActivity(intent);
                }
            }
        });

    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }else if(requestCode == 0){
            final Bundle extras = data.getExtras();

            if(extras != null)
            {
                bitmap = extras.getParcelable("data");
                mMainImage.setImageBitmap(bitmap);
            }
            // 임시 파일 삭제
            File f = new File(imgUri.getPath());
            if(f.exists())
            {
                f.delete();
            }
        }
        if(resultCode != RESULT_OK)
        {
            return;
        }//chae-임시파일 삭제 안돼
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);
                next=false;
                //callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }

    }

    public void turnClick(View view) {
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            mMainImage.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            Toast.makeText(this, "이미지를 먼저 불러와 주세요", Toast.LENGTH_SHORT).show();
        }
    }

    public void resizeClick(View view) {
        try {
            imgUri = getImgUri(getApplicationContext(),bitmap, Bitmap.CompressFormat.JPEG);
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setType("image/*");
            intent.setData(imgUri);
            //intent.setDataAndType(reuri, "image/*");

            intent.putExtra("outputX", 400);
            intent.putExtra("outputY", 240);
            intent.putExtra("aspectX", 20);
            intent.putExtra("aspectY", 12);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, 0);
            next=true;

        } catch (NullPointerException | IOException e) {
            Toast.makeText(this, "이미지를 먼저 불러와 주세요", Toast.LENGTH_SHORT).show();
        }
    }


//    private Uri getImageUri(Context context, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }

    @NonNull
    private Uri getImgUri(@NonNull final Context context, @NonNull final Bitmap bitmap,
                           @NonNull final Bitmap.CompressFormat format
    ) throws IOException
    {
        String relativeLocation = Environment.DIRECTORY_PICTURES;

        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);

        final ContentResolver resolver = context.getContentResolver();

        OutputStream stream = null;
        Uri uri = null;

        try
        {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, contentValues);

            if (uri == null)
            {
                throw new IOException("Failed to create new MediaStore record.");
            }

            stream = resolver.openOutputStream(uri);

            if (stream == null)
            {
                throw new IOException("Failed to get output stream.");
            }

            if (bitmap.compress(format, 95, stream) == false)
            {
                throw new IOException("Failed to save bitmap.");
            }

            return uri;
        }
        catch (IOException e)
        {
            if (uri != null)
            {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
    }

    public void textClick(View view) {
        try {
            callCloudVision(bitmap);
        } catch (NullPointerException e) {
            Toast.makeText(this, "이미지를 먼저 불러와 주세요", Toast.LENGTH_SHORT).show();
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature textDetection = new Feature();
                textDetection.setType("TEXT_DETECTION");
                textDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(textDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<TicketActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(TicketActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            TicketActivity activity = mActivityWeakReference.get();
            TextView jaemok = activity.findViewById(R.id.jaemok_txt);
            TextView ilsi = activity.findViewById(R.id.ilsi_txt);
            TextView jangso = activity.findViewById(R.id.jangso_txt);
            TextView seat = activity.findViewById(R.id.seat_txt);
            String message = result;
            title="";
            date="";
            place="";
            seating="";

            char[] array_word = new char[message.length()];
            for (int i = 0; i < array_word.length; i++) { //스트링을 한글자씩 끊어 배열에 저장
                array_word[i] = message.charAt(i);
            }
            int len = array_word.length;
//            String date = "";
//            String title = "";
//            String place = "";
            boolean melon = false;
            for (int i = 0; i < len; i++) {
                if (array_word[i] != '\n') {
                    title += array_word[i];
                } else {
                    Log.d("date", "제목은 " + title);
                    break;
                }
            }
            for (int i = 0; i < len; i++) {
                if (i < len - 6) {
                    if (array_word[++i] == 'm') {
                        if (array_word[++i] == 'e') {
                            if (array_word[++i] == 'l') {
                                if (array_word[++i] == 'o') {
                                    if (array_word[++i] == 'n') {
                                        melon = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            boolean ilsi2=true;
            if(!melon) {
                for (int i = 0; i < len; i++) {
                    if (array_word[i] == '일' && ilsi2) {
                        if (array_word[i + 1] == ' ')
                            i++;
                        if (array_word[++i] == '시') {
                            i = i + 2;
                            if (array_word[i] == ':')
                                i++;
                            for (int j = i; j < len; j++) {
                                if (array_word[j] != '\n') {
                                    date += array_word[j];
                                } else {
                                    Log.d("date", "일시는 " + date);
                                    ilsi2=false;
                                    break;
                                }
                            }
                            continue;
                        }
                    }
                    if (array_word[i] == '장') {
                        if (array_word[i + 1] == ' ')
                            i++;
                        if (array_word[++i] == '소') {
                            i = i + 2;
                            if (array_word[i] == ':')
                                i++;
                            for (int j = i; j < len; j++) {
                                if (array_word[j] != '\n') {
                                    place += array_word[j];
                                } else {
                                    Log.d("date", "장소는 " + place);
                                    break;
                                }
                            }
                            continue;
                        }
                    }

                }
            }
            //멜론티켓
            if(melon){
                for(int i=0;i<len;i++) {
                    if (array_word[i] == '\n'){
                        for(int j=i+1;j<len;j++){
                            if(array_word[j] != '\n') {
                                date += array_word[j];
                            }else{
                                for (int k = j+1; k < len; k++) {
                                    if(array_word[k] != '\n') {
                                        place += array_word[k];
                                    }else
                                        break;
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            jaemok.setText(title);
            ilsi.setText(date);
            jangso.setText(place);

            //좌석
            String ch="", bl="", gu="", ye="", bu="";
            seat.setText("");
            for(int i=0 ; i<len ; i++){
                if(array_word[i]=='층') {
                    for (int j = i; j > 0; j--) {
                        if (array_word[j] == ' ' || array_word[j] == '\n')
                            break;
                        ch += array_word[j];
                    }
                    seat.append((new StringBuffer(ch)).reverse().toString()+" ");
                    seating += (new StringBuffer(ch)).reverse().toString()+" ";
                    continue;
                }
                if(array_word[i]=='블'){
                    if(array_word[i+1]=='럭'){
                        for(int j=i+1 ; j>0 ; j--){
                            if (array_word[j] == ' ' || array_word[j] == '\n')
                                break;
                            bl += array_word[j];
                        }
                        seat.append((new StringBuffer(bl)).reverse().toString()+" ");
                        seating += (new StringBuffer(bl)).reverse().toString()+" ";
                        continue;
                    }
                }
                if(array_word[i]=='구'){
                    if(array_word[i+1]=='역'){
                        for(int j=i+1 ; j>0 ; j--){
                            if (array_word[j] == ' ' || array_word[j] == '\n')
                                break;
                            gu += array_word[j];
                        }
                        seat.append((new StringBuffer(gu)).reverse().toString()+" ");
                        seating += (new StringBuffer(gu)).reverse().toString()+" ";
                        continue;
                    }
                }
                if(array_word[i]=='열') {
                    for (int j = i; j > 0; j--) {
                        if (array_word[j] == ' ' || array_word[j] == '\n')
                            break;
                        ye += array_word[j];
                    }
                    seat.append((new StringBuffer(ye)).reverse().toString()+" ");
                    seating += (new StringBuffer(ye)).reverse().toString()+" ";
                    continue;
                }
                if(array_word[i]=='번') {
                    if(array_word[i+1]!=' ' && array_word[i+1]!='\n')
                        continue;
                    for (int j = i; j > 0; j--) {
                        if (array_word[j] == ' ' || array_word[j] == '\n')
                            break;
                        bu += array_word[j];
                    }
                    seat.append((new StringBuffer(bu)).reverse().toString()+" ");
                    seating += (new StringBuffer(bu)).reverse().toString()+" ";
                    continue;
                }
            }
            ProgressBar bar = activity.findViewById(R.id.progressBar);
            bar.setVisibility(View.GONE);

            Button upbtn = activity.findViewById(R.id.upload);
            upbtn.setVisibility(View.VISIBLE);

//            if (activity != null && !activity.isFinishing()) {
//                TextView imageDetail = activity.findViewById(R.id.image_details);
//                imageDetail.setText(result);
//            }

        }
    }
    private void imgUpload() {
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        String key = title+date;
        StorageReference imgRef= firebaseStorage.getReference(uid+"/performance/"+key);

        imgRef.putFile(imgUri);
        System.out.println("이미지 업로드 성공");
    }


    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);

        if (bitmap != null)
            bar.setVisibility(View.VISIBLE);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";
        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            message  = labels.get(0).getDescription();
        } else {
            message  = "nothing";
        }
        return message;
    }
}