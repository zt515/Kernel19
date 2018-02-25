package cc.kernel19.wetalk;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        final WetalkClient client = new WetalkClient(1);
        try {
            client.connect();
            client.start(new OnNewDataListener() {
                @Override
                public void onNewData(String data) {
                    System.out.println(data);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                client.close();
            }
        });

        while (client.isConnected());
    }
}
