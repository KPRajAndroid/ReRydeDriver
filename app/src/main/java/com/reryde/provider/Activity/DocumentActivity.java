package com.reryde.provider.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.reryde.provider.Adapter.DocumentAdapter;
import com.reryde.provider.Bean.Document;
import com.reryde.provider.Helper.AppHelper;
import com.reryde.provider.Helper.ConnectionHelper;
import com.reryde.provider.Helper.CustomDialog;
import com.reryde.provider.Helper.SharedHelper;
import com.reryde.provider.Helper.URLHelper;
import com.reryde.provider.Helper.VolleyMultipartRequest;
import com.reryde.provider.R;
import com.reryde.provider.RerydeApplication;
import com.reryde.provider.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.reryde.provider.RerydeApplication.trimMessage;

public class DocumentActivity extends AppCompatActivity implements DocumentAdapter.ServiceClickListener {

    public static final String TAG = "DocumentActivity";

    Activity activity;
    Context context;
    Utilities utils = new Utilities();
    ConnectionHelper helper;

    ImageView backArrow;
    RecyclerView recyclerView;

    ArrayList<Document> documentArrayList;
    DocumentAdapter documentAdapter;
    Boolean isPermissionGivenAlready = false;
    private static final int SELECT_PHOTO = 100;

    ImageView uploadImg;

    public static int deviceHeight;
    public static int deviceWidth;

    Document updatedDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        activity = this;
        context = this;
        helper = new ConnectionHelper(context);
        findViewsById();
        setupRecyclerView();
        getDocList();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
    }

    private void findViewsById() {
        backArrow = (ImageView) findViewById(R.id.backArrow);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        uploadImg = (ImageView) findViewById(R.id.upload_img);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getDocList() {
        if (helper.isConnectingToInternet()) {
            final CustomDialog customDialog = new CustomDialog(activity);
            customDialog.setCancelable(false);
            customDialog.show();

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.GET_DOC, new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject result) {
                    customDialog.dismiss();
                    JSONArray response = result.optJSONArray("document");
                    Log.e(TAG, "onResponse: " + response.toString());
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject doc = response.optJSONObject(i);
                            Document document = new Document();
                            document.setId(doc.optString("id"));
                            document.setName(doc.optString("name"));
                            document.setType(doc.optString("type"));
                            JSONObject docObj = doc.optJSONObject("document");
                            try {
                                if (docObj != null) {
                                    document.setImg(docObj.optString("url"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            documentArrayList.add(document);
                        }
                        if (documentArrayList.size() > 0) {
                            documentAdapter=new DocumentAdapter(documentArrayList,context);
                            documentAdapter.setServiceClickListener(DocumentActivity.this);
                            recyclerView.setAdapter(documentAdapter);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);

                    if (response != null && response.data != null) {
                        utils.print("MyTestError1", "" + response.statusCode);
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                displayMessage(getString(R.string.something_went_wrong));
                            } else if (response.statusCode == 401) {
                                displayMessage(getString(R.string.invalid_credentials));
                            } else if (response.statusCode == 422) {
                                json = RerydeApplication.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }

                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            getDocList();
                        }
                    }

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };
            RerydeApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }

    private void setupRecyclerView() {
        documentArrayList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(documentArrayList, context);
        documentAdapter.setServiceClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen._5sdp);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(documentAdapter);
    }

    @SuppressLint("NewApi")
    @Override
    public void onDocImgClick(Document document) {
        updatedDocument=document;
        if (checkStoragePermission())
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        else
            goToImageIntent();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if (!isPermissionGivenAlready) {
                        goToImageIntent();
                    }
                }
            }
        }
    }

    public void goToImageIntent() {
        isPermissionGivenAlready = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    private class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                //bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                Bitmap resizeImg = getBitmapFromUri(this, uri);
                if (resizeImg != null) {
                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(this, uri));
                    uploadImg.setImageBitmap(reRotateImg);
                    updateDocumentImage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        Log.e(TAG, "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight);
        Log.e(TAG, "getBitmapFromUri: width" + deviceWidth);
        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
            Log.e(TAG, "getBitmapFromUri: Width" + image.getWidth());
            Log.e(TAG, "getBitmapFromUri: Height" + image.getHeight());
            int inWidth = image.getWidth();
            int inHeight = image.getHeight();
            int outWidth;
            int outHeight;
            if (inWidth > inHeight) {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }
            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        } else {
            Toast.makeText(context, context.getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void updateDocumentImage() {
        if (helper.isConnectingToInternet()) {

            final CustomDialog customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            customDialog.show();
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.UPDATE_DOC, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    customDialog.dismiss();
                    String res = new String(response.data);
                    utils.print("ProfileUpdateRes", "" + res);
                    Log.e(TAG, "onResponse: "+res );
                    documentArrayList.clear();
                    documentArrayList=new ArrayList<>();
                    getDocList();
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.getClass().equals(TimeoutError.class)) {
                                updateDocumentImage();
                                return;
                            }
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                GoToBeginActivity();
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else if (response.statusCode == 503) {
                                displayMessage(getString(R.string.server_down));
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }

                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            updateDocumentImage();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    return new HashMap<>();
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }

                @Override
                protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                    Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                    String photo = "photos[" + updatedDocument.getId() + "]";
                    params.put(photo, new VolleyMultipartRequest.DataPart("doc.jpg", AppHelper.getFileDataFromDrawable(uploadImg.getDrawable()), "image/jpeg"));
                    return params;
                }
            };
            RerydeApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

}
