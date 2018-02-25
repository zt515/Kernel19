package cc.kernel19.wetalk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author kiva
 * @date 2017/4/28
 */
public class WetalkClient {
    private static class ReadThread extends Thread {
        boolean running = true;
        InputStream is;
        OnNewDataListener dataListener;

        ReadThread(InputStream is, OnNewDataListener dataListener) {
            this.is = is;
            this.dataListener = dataListener;
        }

        @Override
        public void run() {
            String data;
            while (running && (data = WetalkClient.read(is)) != null) {
                if (Constant.CMD_SERVER_CLOSED.equals(data)) {
                    break;
                }
                // is message
                if (data.startsWith("[")) {
                    data = data.substring(data.indexOf("]") + 1).trim();
                    dataListener.onNewData(data);
                }
            }
        }
    }

    private Socket socket;
    private DataOutputStream os;
    private DataInputStream is;
    private ReadThread readThread;

    private int token;

    public WetalkClient(int token) {
        this.token = token;
    }

    public boolean isConnected() {
        return socket != null;
    }

    public void connect() throws IOException {
        socket = new Socket("kernel19.cc", Constant.SERVER_PORT);
        os = new DataOutputStream(socket.getOutputStream());
    }

    public boolean start(OnNewDataListener dataListener) {
        // Uid check
        DataInputStream is = null;
        try {
            is = new DataInputStream(socket.getInputStream());
            sendText(String.format("%s %d k12fd15dp", Constant.CMD_LOGIN, token));
            String result = read(is);
            System.out.println("try-login: " + result);

            if (result == null || !Constant.CMD_SUCCESS.equals(result)) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        readThread = new ReadThread(is, dataListener);
        readThread.start();
        return true;
    }

    public void sendText(String data) {
        if (isConnected() && os != null) {
            try {
                os.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        sendText(Constant.CMD_EXIT);

        try {
            readThread.running = false;
            readThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception ignore) {
        }
        os = null;
        is = null;
        socket = null;
    }

    private static byte[] buffer = new byte[1024];
    private static String read(InputStream is) {
        try {
            int count = is.read(buffer);
            return count > 0 ? new String(buffer, 0, count) : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
