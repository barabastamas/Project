package com.engineering.software.sapi.project.Profile;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.engineering.software.sapi.project.LoginRegister.LoginActivity;
import com.engineering.software.sapi.project.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;


public class ProfileFragment extends Fragment {

    private final int RESULT_LOAD_IMAGE = 11;
    private Button pBtn;
    private ImageView profilePic;
    private EditText name, phone, email;
    private TextView rating, username;
    private String _username, _name, _phone, _email, _rating;
    private ParseFile _pic;
    private ParseUser currentUser;
    private ProgressDialog progressDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String strPath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strPath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(strPath, options);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_profile, null);

        profilePic = (ImageView) v.findViewById(R.id.profile_picture);
        pBtn = (Button) v.findViewById(R.id.profile_button);
        username = (TextView) v.findViewById(R.id.username);
        name = (EditText) v.findViewById(R.id.name);
        phone = (EditText) v.findViewById(R.id.phone);
        email = (EditText) v.findViewById(R.id.email);
        rating = (TextView) v.findViewById(R.id.rating);

        name.setEnabled(false);
        phone.setEnabled(false);
        email.setEnabled(false);
        profilePic.setClickable(false);
        profilePic.setFocusable(false);

        currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.getUsername() == null) {
                _username = "";
            } else {
                _username = currentUser.getUsername();
            }
            if (currentUser.get("name") == null) {
                _name = "";
            } else {
                _name = currentUser.get("name").toString();
            }
            if (currentUser.get("phoneNumber") == null) {
                _phone = "";
            } else {
                _phone = currentUser.get("phoneNumber").toString();
            }
            if (currentUser.getEmail() == null) {
                _email = "";
            } else {
                _email = currentUser.getEmail();
            }
            if (currentUser.get("rating") == null) {
                _rating = "";
            } else {
                _rating = currentUser.get("rating").toString();
            }
            if (currentUser.getParseFile("profilePic") == null) {
                profilePic.setImageResource(R.drawable.default_profile_pic);
            } else {
                _pic = currentUser.getParseFile("profilePic");
                progressDialog = ProgressDialog.show(getActivity(), "", "Loading profile picture...", true);
                _pic.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
                                    data.length);
                            if (bmp != null) {
                                profilePic.setImageBitmap(bmp);
                                progressDialog.dismiss();
                            }
                        } else {
                            Log.e("Image download error!", " null");
                        }
                    }
                });
            }
            username.setText(_username);
            name.setText(_name);
            phone.setText(_phone);
            email.setText(_email);
            rating.setText(_rating);


        } else {
            Toast.makeText(getContext(), "Session lost. Please log in again!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }

        pBtn.setOnClickListener(new EditProfile(pBtn, name, phone, email, profilePic, currentUser));
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if (cursor == null) throw new AssertionError();
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            // String picturePath contains the path of selected Image
            Bitmap bitmap = decodeSampledBitmapFromResource(picturePath, 124, 124);
            //bitmap = BitmapFactory.decodeFile(picturePath);
            // Convert it to byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
            byte[] image = stream.toByteArray();

            ParseFile file = new ParseFile("prof_pic.png", image);
            file.saveInBackground();

            currentUser.put("profilePic", file);
            profilePic.setImageBitmap(bitmap);
            bitmap.recycle();
            Toast.makeText(getActivity(), "Profile picture changed.",
                    Toast.LENGTH_SHORT).show();

        } else if (requestCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
        }

    }
}