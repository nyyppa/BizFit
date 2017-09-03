package com.bizfit.bizfitUusYritysKeskusAlpha.network.FileDownload;

import android.os.AsyncTask;
import android.os.Environment;

import com.bizfit.bizfitUusYritysKeskusAlpha.DebugPrinter;
import com.bizfit.bizfitUusYritysKeskusAlpha.MyApplication;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.MyConnection;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Atte Ylivrronen on 14.6.2017.
 */

public abstract class DownloadFile <T> extends AsyncTask<URL, Integer, DownloadFile.FileResult> {

    protected FileResult doInBackground(URL... urls) {
        URL url = urls[0];
        File file=null;

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


            MyConnection httpConn=new MyConnection(url);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("job","downloadfile");
            httpConn.setRequestProperty("fileid",getFileID());
            httpConn.setSSLSocketFactory(context.getSocketFactory());
            //httpConn.setReadTimeout(10000 /* milliseconds */);
            //httpConn.setConnectTimeout(15000 /* milliseconds */);
            //httpConn.setRequestMethod("POST");
            httpConn.setDoInput(true);
            //httpConn.setDoOutput(true);
            httpConn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {

                    return HttpsURLConnection.getDefaultHostnameVerifier().verify("bizfit server",session);

                }
            });

            int responseCode = httpConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                if(httpConn.getHeaderField("filefound")==null||httpConn.getHeaderField("filefound").equals("false"))
                {
                    throw new DownloadFileExeption("file not found");
                }
                int contentLength = httpConn.getContentLength();

                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {

                }
                System.out.println("disposition = " + disposition);
                System.out.println("Content-Type = " + contentType);
                System.out.println("Content-Disposition = " + disposition);
                System.out.println("Content-Length = " + contentLength);
                System.out.println("fileName = " + fileName);

                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();
                //TODO change to use temp files?
                file = File.createTempFile(UUID.randomUUID().toString(),null,MyApplication.getContext().getFilesDir());
                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(file);
                int i=0;
                int bytesRead = -1;
                byte[] buffer = new byte[1024];
                double total=0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    DebugPrinter.Debug("heheee");
                    publishProgress((int)(total/contentLength*100));
                    DebugPrinter.Debug("bytesRead: "+bytesRead);
                    total+=bytesRead;
                    outputStream.write(buffer, 0, bytesRead);
                    i++;
                }

                outputStream.close();
                inputStream.close();

                System.out.println("File downloaded");
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();
        } catch (IOException e) {
            return new FileResult(e);
        } catch (CertificateException e) {
            return new FileResult(e);
        } catch (NoSuchAlgorithmException e) {
            return new FileResult(e);
        } catch (NoSuchProviderException e) {
            return new FileResult(e);
        } catch (KeyStoreException e) {
            return new FileResult(e);
        } catch (KeyManagementException e) {
            return new FileResult(e);
        } catch (DownloadFileExeption downloadFileExeption) {
            return new FileResult(downloadFileExeption);
        }

        return new FileResult(file);
    }

    public abstract void onProgressUpdate(Integer... progress);
    public abstract String getFileID();

    protected abstract void onPostExecute(FileResult result);

    public abstract void doResult(T result);

    public abstract void error(Exception e);


    public class FileResult{
        private File result;
        Exception exception;
        public FileResult(File result)
        {
            super();
            this.result=result;
        }
        public FileResult(Exception exception)
        {
            super();
            this.exception=exception;
        }
        public File  getResult  () throws Exception
        {
            if(exception!=null)
            {
                throw exception;
            }
            return result;
        }
    }
}
