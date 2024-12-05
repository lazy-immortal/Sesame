package io.github.lazyimmortal.sesame.model.extend;

public class TestRpc {
    private static final String TAG = TestRpc.class.getSimpleName();

    public static void start(String broadcastFun, String broadcastData, String testType) {
        new Thread() {
            String broadcastFun;
            String broadcastData;
            String testType;

            public Thread setData(String fun, String data, String type) {
                broadcastFun = fun;
                broadcastData = data;
                testType = type;
                return this;
            }

            @Override
            public void run() {
                ExtendHandle.handleRequest(testType, broadcastFun, broadcastData);
            }
        }.setData(broadcastFun, broadcastData, testType).start();
    }
}
