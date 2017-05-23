package com.plymouth.sera.buggy;

import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;

public class Client extends AsyncTask<Void, Void, Void> {

    //Ip adress and Port of Socket host defined
    String IpAdress = "10.188.110.84"; //Pi s Ip adress
    int portAdress = 2004; //defined port for communication

    String messages;

    //constructor for client
    //Initiazling message to send
    Client (String data2send)
    {

        messages = data2send;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Socket clientSoc = null;

        try{
            //creating java socket
            clientSoc = new Socket(IpAdress, portAdress);

            //creating ouput streaming channel
            DataOutputStream dataOutputStream = new DataOutputStream(clientSoc.getOutputStream());

            //streaming data written and send
            dataOutputStream.writeBytes(messages);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (clientSoc != null) {
                try {
                    clientSoc.close();

                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result){

        super.onPostExecute(result);
    }

}
