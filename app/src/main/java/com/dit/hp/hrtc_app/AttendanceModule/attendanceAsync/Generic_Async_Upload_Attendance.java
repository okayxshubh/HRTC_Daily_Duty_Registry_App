package com.dit.hp.hrtc_app.AttendanceModule.attendanceAsync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dit.hp.hrtc_app.AttendanceModule.attendanceInterfaces.AsyncTaskListenerFile;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceModals.AadhaarDoc;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.network.HttpFileUpload;

import org.json.JSONException;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Generic_Async_Upload_Attendance extends AsyncTask<UploadObject, String, String> {



    String outputStr;
    ProgressDialog dialog;
    Context context;
    AsyncTaskListenerFile taskListener;
    TaskType taskType;
    private ProgressDialog mProgressDialog;


    public Generic_Async_Upload_Attendance(Context context, AsyncTaskListenerFile taskListener, TaskType taskType){
        this.context = context;
        this.taskListener = taskListener;
        this.taskType = taskType;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("Uploading");
        mProgressDialog.setMessage("Uploading Files and Images, Please Wait!");
        // mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(UploadObject... mediaFiles) {
        HttpFileUpload hfu = null;
        String responseMessage = null;

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();

            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            // Add surveyData as a common part
            multipartBuilder.addFormDataPart("data", mediaFiles[0].getParam());


            List<AadhaarDoc> imagePathList = mediaFiles[0].getImagePaths(); // Assuming getImagePath returns an ArrayList<String>



            if (imagePathList != null && !imagePathList.isEmpty()) {


                for(int i=0; i< imagePathList.size(); i++){
                    String firstImagePath = imagePathList.get(i).getDocPath(); // Assuming you want to use the first element

                    File file = new File(firstImagePath);
                    Log.e("File Name", file.getName());
                    Log.e("File Path", file.getPath());
                    Log.e("File Size", String.valueOf(file.length()));

                    multipartBuilder.addFormDataPart("files", file.getName().trim(),
                            RequestBody.create(MediaType.parse("application/octet-stream"), file));
                }

            } else {
                Log.e("List", "List Empty");
            }

            RequestBody body = multipartBuilder.build();
            Request request = new Request.Builder()
                    .url(mediaFiles[0].getUrl() + mediaFiles[0].getMethordName())
                    .method("POST", body)
                    .addHeader("Connection", "Keep-Alive")
                    .build();

            System.out.println("=-===-=Request=-=-=-=-" + request.toString());
            Response response = client.newCall(request).execute();

            String responsex = response.body().string();

            System.out.println("=-===-Response=-=-=-=-" + responsex);
            System.out.println("=-===-==-=-Response code-=-=-" + response.code());

            if (response.code() == 201) {
                System.out.println("=-===- Response 201 code =-=-=-=-=-=-=-" + responsex);
                return responsex;
            } else {
                System.out.println("=-===- Response non 200 code error =-=-=-=-=-=-=-" + responsex);
                return responsex;
            }

        } catch (Exception e) {
            System.out.println("Exception is" + e.getMessage());
            return e.getLocalizedMessage().toString();
        }
    }


//    @Override
//    protected void onProgressUpdate() {
//
//        super.onProgressUpdate(values);
//        Log.e("Progress",Integer.toString(values[0]));
//        mProgressDialog.setProgress(values[0]);
//
//    }

    @Override
    protected void onPostExecute(String result) {
        // super.onPostExecute(object);

        try {
            Log.e("#@# Object\n \t @#@",result);
            taskListener.onTaskCompleted(result, taskType);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mProgressDialog.dismiss();
    }


}
