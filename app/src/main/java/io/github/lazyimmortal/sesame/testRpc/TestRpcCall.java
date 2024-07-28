package io.github.lazyimmortal.sesame.testRpc;

import io.github.lazyimmortal.sesame.hook.ApplicationHook;

public class TestRpcCall {

        public static String queryEnvironmentCertDetailList(String alias, int pageNum, String targetUserID) {
                return ApplicationHook.requestString("alipay.antforest.forest.h5.queryEnvironmentCertDetailList",
                                "[{\"alias\":\"" + alias + "\",\"certId\":\"\",\"pageNum\":" + pageNum
                                                + ",\"shareId\":\"\",\"source\":\"chInfo_ch_appcenter__chsub_9patch\",\"targetUserID\":\""
                                                + targetUserID + "\",\"version\":\"20230701\"}]");
        }

        public static String sendTree(String certificateId, String friendUserId) {
                return ApplicationHook.requestString("alipay.antforest.forest.h5.sendTree",
                                "[{\"blessWords\":\"梭梭没有叶子，四季常青，从不掉发，祝你发量如梭。\",\"certificateId\":\"" + certificateId
                                                + "\",\"friendUserId\":\"" + friendUserId
                                                + "\",\"source\":\"chInfo_ch_appcenter__chsub_9patch\"}]");
        }
}