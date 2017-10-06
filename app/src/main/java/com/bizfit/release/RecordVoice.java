package com.bizfit.release;

import android.media.MediaRecorder;
import android.view.View;

import com.bizfit.release.chat.Conversation;
import com.bizfit.release.chat.Message;
import com.bizfit.release.network.FileUpload.FileUploader;

import java.io.File;
import java.io.IOException;

/**
 * Created by attey on 25/07/2017.
 */

public class RecordVoice implements View.OnClickListener{
    View view;
    boolean recording=false;
    MediaRecorder mRecorder;
    String path;
    public RecordVoice(View view)
    {
        this.view=view;
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(recording){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            recording=false;
        }else{
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            path=view.getContext().getFilesDir().getPath()+"/"+System.currentTimeMillis()+".3gp";
            mRecorder.setOutputFile(path);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (IOException e) {

            }
            mRecorder.start();
            recording=true;
        }
    }
    public void sendVoiceMail(final Conversation conversation)
    {
        Message message=new Message(conversation,conversation.getOwner(),conversation.getOther(),"voice", Message.MessageType.VOICE);

        new FileUploader() {
            @Override
            protected void onPostExecute(ResultCode result) {

            }

            @Override
            public String getFileName() {
                return null;
            }

            @Override
            public String getFileExtension() {
                return "3gp";
            }

            @Override
            public String getUploader() {
                return null;
            }

            public String getUplader() {
                return conversation.getOwner();
            }

            @Override
            public String getFileType() {
                return "voice";
            }
        }.execute(new File(path));
    }

}
