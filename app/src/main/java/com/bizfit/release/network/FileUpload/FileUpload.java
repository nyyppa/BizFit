package com.bizfit.release.network.FileUpload;

import android.os.AsyncTask;

import com.bizfit.release.DebugPrinter;
import com.bizfit.release.MyApplication;
import com.bizfit.release.network.MyConnection;
import com.bizfit.release.utils.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by attey on 05/07/2017.
 */

public abstract class FileUpload<T> extends AsyncTask<T,Void,FileUpload.ResultCode>{

    String fileID;

    @Override
    protected ResultCode doInBackground(T... params) {
        File f=convertGenerictoFile(params[0]);
        try {

            MyConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = f;
            String sourceFileUri="file";

            if (sourceFile.isFile()) {

                try {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509","BC");
                    // From https://www.washington.edu/itconnect/security/ca/load-der.crt
                    ;
                    InputStream caInput = new BufferedInputStream(MyApplication.getContext().getAssets().open("certs/server_ca_signed.crt"));
                    Certificate ca;
                    try {
                        ca = cf.generateCertificate(caInput);
                        //System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                    } finally {
                        caInput.close();
                    }
                    fileID= UUID.randomUUID().toString();
                    // Create a KeyStore containing our trusted CAs
                    String keyStoreType = KeyStore.getDefaultType();
                    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                    keyStore.load(null, null);
                    keyStore.setCertificateEntry("ca", ca);

                    // Create a TrustManager that trusts the CAs in our KeyStore
                    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                    tmf.init(keyStore);

                    // Create an SSLContext that uses our TrustManager
                    SSLContext context = SSLContext.getInstance("TLS");
                    context.init(null, tmf.getTrustManagers(), null);

                    String upLoadServerUri = Constants.file_connection_address;
                    URL url = new URL(upLoadServerUri);
                    conn=new MyConnection(url);
                    conn.setSSLSocketFactory(context.getSocketFactory());
                    //httpConn.setReadTimeout(10000 /* milliseconds */);
                    //httpConn.setConnectTimeout(15000 /* milliseconds */);
                    //httpConn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    //httpConn.setDoOutput(true);
                    conn.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {

                            return HttpsURLConnection.getDefaultHostnameVerifier().verify("bizfit server",session);

                        }
                    });

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(
                            sourceFile);


                    // Open a HTTP connection to the URL
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");

                    String filename=getFileName();
                    if(filename==null||filename.isEmpty())
                    {
                        filename=f.getName();
                        int pos = filename.lastIndexOf(".");
                        if (pos > 0) {
                            filename = filename.substring(0, pos);
                        }
                    }
                    String fileType=getFileType();
                    if(fileType==null||fileType.isEmpty())
                    {
                        fileType= getFileTypeFromUrl(f.toURL());
                        if(fileType==null||fileType.isEmpty())
                        {
                            fileType="undefined";
                        }
                        DebugPrinter.Debug("fileType: "+fileType);
                        fileType=fileType.split("/")[0];
                    }
                    String fileExtension=getFileExtension();
                    if(fileExtension==null||fileExtension.isEmpty())
                    {
                        fileExtension=getFileTypeFromUrl(f.toURL());
                        String [] strings=fileExtension.split("/");
                        fileExtension=strings[strings.length-1];
                        if(fileExtension.length()>3)
                        {
                            fileExtension=getFileTypeFromFile(f);
                        }
                        if(fileExtension.length()>3)
                        {
                            try {
                                fileExtension=f.getName().substring(f.getName().lastIndexOf(".") + 1);
                            }catch (Exception e) {
                                fileExtension="unknown";
                            }
                        }
                    }
                    fileExtension=fileExtension.toLowerCase();
                    if(fileType.equals("unknown"))
                    {
                        fileType=quessFileType(fileExtension);
                    }
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE",
                            "multipart/form-data");
                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);

                    conn.setRequestProperty("fileType", fileType);
                    conn.setRequestProperty("fileName",filename);
                    conn.setRequestProperty("fileExtension",fileExtension);
                    conn.setRequestProperty("fileID",f.getName().substring(0,f.getName().lastIndexOf(".")));
                    //conn.setRequestProperty("fileID",fileID);
                    conn.setRequestProperty("uploader", getUploader());
                    conn.setRequestProperty("job","uploadfile");
                    dos = new DataOutputStream(conn.getOutputStream());
                    //DebugPrinter.Debug("tiedosto nimi"+getFileTypeFromUrl(f.toURL()));
/*

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""
                            + sourceFileUri + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);
*/
                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math
                                .min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,
                                bufferSize);

                    }

                    // send multipart form data necesssary after file
                    // data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens
                            + lineEnd);

                    // Responses from the server (code and message)
                    int serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn
                            .getResponseMessage();
                    DebugPrinter.Debug("heihoi"+serverResponseMessage);
                    if (serverResponseCode == 200) {

                        // messageText.setText(msg);
                        //Toast.makeText(ctx, "File Upload Complete.",
                        //      Toast.LENGTH_SHORT).show();

                        // recursiveDelete(mDirectory1);

                    }

                    // close the streams //

                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                    if(serverResponseCode==200)
                    {
                        return ResultCode.success;
                    }

                } catch (Exception e) {

                    // dialog.dismiss();
                    e.printStackTrace();

                }
                // dialog.dismiss();

            } // End else block


        } catch (Exception ex) {
            // dialog.dismiss();

            ex.printStackTrace();
            return ResultCode.failure;
        }
        return ResultCode.failure;
    }

    protected abstract void onPostExecute(ResultCode result);

    public abstract File convertGenerictoFile(T param);

    public enum ResultCode{
        success,failure;
    }
    public abstract String getFileName();
    public abstract String getFileExtension();
    public abstract String getUploader();

    /**
     * Valid returns are video,profile,image,document,voice,undefined.
     * If uploaded file is part of profile(profile image, sale video, etc) return profile, otherwise return apporiate category
     * @return
     */
    public abstract String getFileType();
    public String getFileTypeFromUrl(URL url) throws IOException {
        URLConnection connection =url.openConnection();
        return connection.getContentType();
    }
    public String getFileTypeFromFile(File file) throws IOException {
        BufferedReader brTest = new BufferedReader(new FileReader(file));
        return brTest .readLine().replaceAll("[^a-zA-Z0-9 ]","");
    }
    public String getFileID()
    {
        return fileID;
    }

    public String quessFileType(String fileExtension)
    {
        switch(fileExtension.toLowerCase())
        {
            case "png":
            case "jpg":
                return "image";
            case "mp4":
                return "video";
            default:
                return "content";
        }
    }

}
