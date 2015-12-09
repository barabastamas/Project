package com.engineering.software.sapi.project.Profile;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ProfileFragment extends Fragment {

    private final int RESULT_GALLERY = 1;
    private final int RESULT_CAMERA = 2;
    private Button pBtn;
    private ImageView profilePic;
    private EditText inputName, inputPhone, inputEmail;
    private TextView rating, username;
    private String _username, _name, _phone, _email, _rating;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPhone;
    private ParseFile _pic;
    private ParseUser currentUser;
    private ProgressDialog progressDialog;
    private File _dir, mCurrentPhoto;
    private Uri fileUri = null;
    private Bitmap bitmap;

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

    /**
     * Create a File for saving an image
     */
    private static Uri getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir;
        mediaStorageDir = new File(Environment.DIRECTORY_PICTURES, "MyCameraApp");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "ProjectIMG_" + timeStamp + ".png");
        return Uri.fromFile(mediaFile);
    }

    /**
     * Create a file Uri for saving an image
     */
//    private static Uri getOutputMediaFileUri() {
//        return Uri.fromFile(getOutputMediaFile());
//    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void imagePickerDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this.getContext());
        myAlertDialog.setTitle("Pictures Option");
        myAlertDialog.setMessage("Select Picture Mode");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_GALLERY);
            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFile(); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                startActivityForResult(intent, RESULT_CAMERA);
            }
        });
        myAlertDialog.show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container = (ViewGroup) inflater.inflate(R.layout.fragment_profile, null);

        profilePic = (ImageView) container.findViewById(R.id.profile_picture);
        pBtn = (Button) container.findViewById(R.id.profile_button);
        username = (TextView) container.findViewById(R.id.username);
        inputLayoutName = (TextInputLayout) container.findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) container.findViewById(R.id.input_layout_email);
        inputLayoutPhone = (TextInputLayout) container.findViewById(R.id.input_layout_phone);
        inputName = (EditText) container.findViewById(R.id.name);
        inputEmail = (EditText) container.findViewById(R.id.email);
        inputPhone = (EditText) container.findViewById(R.id.phone);
        rating = (TextView) container.findViewById(R.id.rating);
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPhone.addTextChangedListener(new MyTextWatcher(inputPhone));

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
                ParseFile object = currentUser.getParseFile("profilePic");
                object.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            profilePic.setImageBitmap(bmp);
                            profilePic.setClickable(false);
                            profilePic.setFocusable(false);
                        }
                    }
                });
            }

            username.setText(_username);
            inputName.setText(_name);
            inputPhone.setText(_phone);
            inputEmail.setText(_email);
            rating.setText(_rating);
            setter("false");


        } else {
            Toast.makeText(getContext(), "Session lost. Please log in again!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }

        pBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnText = pBtn.getText().toString();

                if (pBtn.isPressed() && btnText.equals("Edit")) {
                    setter("true");
                    pBtn.setText("Save");
                }
                if (pBtn.isPressed() && btnText.equals("Save")) {
                    editProfile();
                    currentUser.put("name", inputName.getText().toString().trim());
                    currentUser.setEmail(inputEmail.getText().toString().trim());
                    currentUser.put("phoneNumber", inputPhone.getText().toString().trim());
                    currentUser.saveInBackground();
                    setter("false");
                    pBtn.setText("Edit");
                }
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickerDialog();
            }
        });
        return container;
    }

    private void setter(String s) {
        if (s.equals("true")) {

            inputName.setFocusableInTouchMode(true);
            inputPhone.setFocusableInTouchMode(true);
            inputEmail.setFocusableInTouchMode(true);
            profilePic.setClickable(true);
            profilePic.setFocusable(true);
        } else {
            inputName.setFocusable(false);
            inputPhone.setFocusable(false);
            inputEmail.setFocusable(false);
            profilePic.setFocusable(false);
            profilePic.setClickable(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RESULT_GALLERY:
                    if (data != null && data.getData() != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        if (cursor == null || cursor.getCount() < 1) {
                            break;
                        }
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        if (columnIndex < 0) {
                            break;                       // no column index
                        }
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        // String picturePath contains the path of selected Image
                        bitmap = decodeSampledBitmapFromResource(picturePath.toString(), 124, 124);
                        // Convert it to byte
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        // Compress image to lower quality scale 1 - 100
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] image = stream.toByteArray();

                        ParseFile file = new ParseFile("prof_pic.png", image);
                        file.saveInBackground();

                        currentUser.put("profilePic", file);
                        currentUser.saveInBackground();
                        profilePic.setImageBitmap(bitmap);
                        Toast.makeText(getActivity(), "Profile picture changed.",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RESULT_CAMERA:
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), fileUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // bitmap = decodeSampledBitmapFromResource(fileUri.getPath(), 124, 124);
                    // Convert it to byte
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();

                    ParseFile file = new ParseFile("prof_pic.png", image);
                    file.saveInBackground();

                    currentUser.put("profilePic", file);
                    currentUser.saveInBackground();
                    profilePic.setImageBitmap(bitmap);
                    bitmap.recycle();
                    Toast.makeText(getActivity(), "Profile picture changed.",
                            Toast.LENGTH_SHORT).show();

                    break;
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void editProfile() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePhone()) {
            return;
        }

        Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        if (inputPhone.getText().toString().trim().isEmpty()) {
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(inputPhone);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.name:
                    validateName();
                    break;
                case R.id.email:
                    validateEmail();
                    break;
                case R.id.phone:
                    validatePhone();
                    break;
            }
        }
    }
}