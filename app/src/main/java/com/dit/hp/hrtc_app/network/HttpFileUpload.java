package com.dit.hp.hrtc_app.network;


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpFileUpload {
    URL connectURL;
    String responseString;

    String filename_, filepath_, jsonData_;
    String filename2_, filepath2_;
    byte[ ] dataToServer;
    FileInputStream fileInputStream, fileInputStream2 = null;
    List<FileInputStream> streams_ = new ArrayList<>();
    List<String> fileNmaes_ = new ArrayList<>();
    DataOutputStream dos = null;
    public String ResponceCode = null;
    BufferedReader reader =null;

    public HttpFileUpload(String urlString, String filename, String fileName2, String filepath, String filePath2, String jsonData){
        try{

            connectURL = new URL(urlString);
            filename_= filename;
            filename2_ = fileName2;
            filepath_ = filepath;
            filepath2_ = filePath2;
            jsonData_ = jsonData;

            fileNmaes_.add(filename_);
            fileNmaes_.add(filename2_);
            System.out.println("connected");
        }catch(Exception ex){
            Log.i("HttpFileUpload","URL Malformatted");
        }
    }

//    public String Send_Now(FileInputStream fStream, FileInputStream fStream2){
//        fileInputStream = fStream;
//        fileInputStream2 = fStream2;
//       return Sending();
//    }

    public String Send_Now(List<FileInputStream> streams) {

        streams_ = streams;
        return Sending();
    }

   private String Sending(){
        String iFileName = filename_;
        String iFileName2 = filename2_;
        Log.e("iFileName",iFileName);
        Log.e("filepath",filepath_);

       Log.e("iFileName2",iFileName2);
       Log.e("filepath2",filepath2_);

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag="fSnd";
        try
        {
            Log.e(Tag,"Starting Http File Sending to URL");

            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();
            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);


//            dos.writeBytes(twoHyphens + boundary + lineEnd);
//            //UploadFile1
//            dos.writeBytes("Content-Disposition: form-data; name=\"files\";filename=\"" + iFileName +"\"" + lineEnd);
//            dos.writeBytes(lineEnd);
            //Log.e("We are here",dos.);

        //    dos.writeBytes(twoHyphens + boundary + lineEnd);
            //UploadFile1
          //  dos.writeBytes("Content-Disposition: form-data; name=\"files\";filename=\"" + iFileName2 +"\"" + lineEnd);
           // dos.writeBytes(lineEnd);


            // create a buffer of maximum size
            //Sending File 1 working
//            int bytesAvailable = fileInputStream.available();
//            Log.e("Bytes Avaul", Integer.toString(bytesAvailable));
//            Log.e("File Path",fileInputStream.toString());
//            int maxBufferSize = 1024;
//            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            byte[ ] buffer = new byte[bufferSize];
//
//            // read file and write it into form...
//            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//            while (bytesRead > 0)
//            {
//                //  Log.e("bytesRead",Integer.toString(bytesRead));
//                dos.write(buffer, 0, bufferSize);
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable,maxBufferSize);
//                bytesRead = fileInputStream.read(buffer, 0,bufferSize);
//            }
//            dos.writeBytes(lineEnd);
//            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//            // close streams
//            fileInputStream.close();




            for(int i=0; i<streams_.size();i++){


                try{
                    dos = new DataOutputStream(conn.getOutputStream());
                }catch(Exception e){
                    System.out.println(e.getCause());
                }
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"jsondata\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(jsonData_);
                dos.writeBytes(lineEnd);

                Log.e("JsonData",jsonData_.toString());


                dos.writeBytes(twoHyphens + boundary + lineEnd);
                //UploadFile1
                dos.writeBytes("Content-Disposition: form-data; name=\"files\";filename=\"" + fileNmaes_.get(i) +"\"" + lineEnd);
                dos.writeBytes(lineEnd);

//                    dos.writeBytes(twoHyphens + boundary + lineEnd);
//              //  UploadFile1
//                  dos.writeBytes("Content-Disposition: form-data; name=\"files\";filename=\"" + iFileName2 +"\"" + lineEnd);
//                 dos.writeBytes(lineEnd);

                int bytesAvailable = streams_.get(i).available();
                Log.e("Bytes Avaul", Integer.toString(bytesAvailable));
                Log.e("File Path",streams_.get(i).toString());
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[ ] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = streams_.get(i).read(buffer, 0, bufferSize);
                while (bytesRead > 0)
                {
                    //  Log.e("bytesRead",Integer.toString(bytesRead));
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = streams_.get(i).available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = streams_.get(i).read(buffer, 0,bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                streams_.get(i).close();
                dos.flush();
            }



            // create a buffer of maximum size
            //Sending File 2
//            int bytesAvailable2 = fileInputStream2.available();
//            Log.e("Bytes Avaul", Integer.toString(bytesAvailable2));
//            Log.e("File Path",fileInputStream2.toString());
//            int maxBufferSize2 = 1024;
//            int bufferSize2 = Math.min(bytesAvailable2, maxBufferSize2);
//            byte[ ] buffer2 = new byte[bufferSize2];
//
//            // read file and write it into form...
//            int bytesRead2 = fileInputStream2.read(buffer2, 0, bufferSize2);
//            while (bytesRead2 > 0)
//            {
//                //  Log.e("bytesRead",Integer.toString(bytesRead));
//                dos.write(buffer2, 0, bufferSize2);
//                bytesAvailable2 = fileInputStream2.available();
//                bufferSize2 = Math.min(bytesAvailable2,maxBufferSize2);
//                bytesRead2 = fileInputStream2.read(buffer2, 0,bufferSize2);
//            }
//            dos.writeBytes(lineEnd);
//            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//            // close streams
//            fileInputStream2.close();



  System.out.println("@#@#@#@#@#@#@#@#@#"+conn.getResponseCode());

            if (conn.getResponseCode() != 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                conn.disconnect();
              Log.e("Error",sb.toString());
                return sb.toString()+"~~"+conn.getResponseCode();
            }else {


                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                conn.disconnect();
                Log.e("Success",sb.toString());
                return sb.toString()+"~~"+conn.getResponseCode();

            }




        }
        catch (MalformedURLException ex)
        {
            return  ex.getLocalizedMessage().toString();
        }

        catch (IOException ioe)
        {
            return  ioe.getLocalizedMessage().toString();
        }
    }



}
