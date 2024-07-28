package io.github.lazyimmortal.sesame.model.testRpc;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.lazyimmortal.sesame.model.task.antForest.AntForestRpcCall;
import io.github.lazyimmortal.sesame.model.task.antDodo.AntDodoRpcCall;
import io.github.lazyimmortal.sesame.model.task.reserve.ReserveRpcCall;
import io.github.lazyimmortal.sesame.model.task.antOrchard.AntOrchardRpcCall;
import io.github.lazyimmortal.sesame.hook.ApplicationHook;
import io.github.lazyimmortal.sesame.util.*;

public class TestRpc {
    private static final String TAG = "TestRpc";

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
                if ("Rpc".equals(testType)) {
                    String s = test(broadcastFun, broadcastData);
                    Log.debug("收到测试消息:\n方法:" + broadcastFun + "\n数据:" + broadcastData + "\n结果:" + s);
                }
                if ("sendAntdodoAllCard".equals(testType)) {
                    sendAntdodoCard(broadcastFun, broadcastData, true);
                }
                if ("sendAntdodoOneSetCard".equals(testType)) {
                    sendAntdodoCard(broadcastFun, broadcastData, false);
                }

                if ("sendAntdodoOneWholeSetCard".equals(testType)) {
                    sendAntdodoOneWholeSetCard(broadcastFun, broadcastData);
                }
                if ("getNewTreeItems".equals(testType)) {
                    getNewTreeItems();
                }

                if ("collectHistoryAnimal".equals(testType)) {
                    collectHistoryAnimal();
                }

                if ("getWateringLeftTimes".equals(testType)) {
                    getWateringLeftTimes();
                }
                if ("getTreeItems".equals(testType)) {
                    getTreeItems();
                }
                if ("batchHireAnimalRecommend".equals(testType)) {
                    batchHireAnimalRecommend();
                }
            }
        }.setData(broadcastFun, broadcastData, testType).start();
    }

    private static String test(String fun, String data) {
        return ApplicationHook.requestString(fun, data);
    }

    private static void sendAntdodoCard(String bookIdInfo, String targetUser, boolean sendAll) {
        try {
            JSONObject jo = new JSONObject(bookIdInfo);
            JSONArray bookIdList = jo.getJSONArray("bookIdList");
            for (int i = 0; i < bookIdList.length(); i++) {
                JSONObject bookInfo = bookIdList.getJSONObject(i);
                if (sendAll) {
                    sendAntdodoAllCard(bookInfo.getString("bookId"), targetUser);
                } else {
                    sendAntdodoOneSetCard(bookInfo.getString("bookId"), targetUser);
                }
            }
        } catch (Throwable th) {
            Log.i(TAG, "sendAntdodoCard err:");
            Log.printStackTrace(TAG, th);
        }
    }

    private static void sendAntdodoAllCard(String bookId, String targetUser) {
        try {
            JSONObject jo = new JSONObject(AntDodoRpcCall.queryBookInfo(bookId));
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                JSONArray animalForUserList = jo.getJSONObject("data").optJSONArray("animalForUserList");
                for (int i = 0; i < animalForUserList.length(); i++) {
                    JSONObject animalForUser = animalForUserList.getJSONObject(i);
                    int count = animalForUser.getJSONObject("collectDetail").optInt("count");
                    if (count <= 0)
                        continue;
                    JSONObject animal = animalForUser.getJSONObject("animal");
                    String animalId = animal.getString("animalId");
                    String ecosystem = animal.getString("ecosystem");
                    String name = animal.getString("name");
                    for (int j = 0; j < count; j++) {
                        jo = new JSONObject(AntDodoRpcCall.social(animalId, targetUser));
                        if ("SUCCESS".equals(jo.getString("resultCode"))) {
                            Log.forest("赠送卡片🦕[" + UserIdMap.getMaskName(targetUser) + "]#" + ecosystem + "-" + name);
                        } else {
                            Log.i(TAG, jo.getString("resultDesc"));
                        }
                        Thread.sleep(500L);
                    }
                }
            }
        } catch (Throwable th) {
            Log.i(TAG, "sendAntdodoAllCard err:");
            Log.printStackTrace(TAG, th);
        }
    }

    private static void sendAntdodoOneSetCard(String bookId, String targetUser) {
        try {
            JSONObject jo = new JSONObject(AntDodoRpcCall.queryBookInfo(bookId));
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                JSONArray animalForUserList = jo.getJSONObject("data").optJSONArray("animalForUserList");
                for (int i = 0; i < animalForUserList.length(); i++) {
                    JSONObject animalForUser = animalForUserList.getJSONObject(i);
                    int count = animalForUser.getJSONObject("collectDetail").optInt("count");
                    if (count <= 0)
                        continue;
                    JSONObject animal = animalForUser.getJSONObject("animal");
                    String animalId = animal.getString("animalId");
                    String ecosystem = animal.getString("ecosystem");
                    String name = animal.getString("name");
                    jo = new JSONObject(AntDodoRpcCall.social(animalId, targetUser));
                    if ("SUCCESS".equals(jo.getString("resultCode"))) {
                        Log.forest("赠送卡片🦕[" + UserIdMap.getMaskName(targetUser) + "]#" + ecosystem + "-" + name);
                    } else {
                        Log.i(TAG, jo.getString("resultDesc"));
                    }
                    Thread.sleep(500L);
                }
            }
        } catch (Throwable th) {
            Log.i(TAG, "sendAntdodoOneSetCard err:");
            Log.printStackTrace(TAG, th);
        }
    }

    private static void sendAntdodoOneWholeSetCard(String bookId, String targetUser) {
        try {
            JSONObject jo = new JSONObject(AntDodoRpcCall.queryBookInfo(bookId));
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                JSONArray animalForUserList = jo.getJSONObject("data").optJSONArray("animalForUserList");
                for (int i = 0; i < animalForUserList.length(); i++) {
                    JSONObject animalForUser = animalForUserList.getJSONObject(i);
                    int count = animalForUser.getJSONObject("collectDetail").optInt("count");
                    if (count <= 0)
                        return;
                }
                for (int j = 0; j < animalForUserList.length(); j++) {
                    JSONObject animalForUser = animalForUserList.getJSONObject(j);
                    JSONObject animal = animalForUser.getJSONObject("animal");
                    String animalId = animal.getString("animalId");
                    String ecosystem = animal.getString("ecosystem");
                    String name = animal.getString("name");
                    jo = new JSONObject(AntDodoRpcCall.social(animalId, targetUser));
                    if ("SUCCESS".equals(jo.getString("resultCode"))) {
                        Log.forest("赠送卡片🦕[" + UserIdMap.getMaskName(targetUser) + "]#" + ecosystem + "-" + name);
                    } else {
                        Log.i(TAG, jo.getString("resultDesc"));
                    }
                    Thread.sleep(500L);

                }
            }
        } catch (Throwable th) {
            Log.i(TAG, "sendAntdodoOneWholeSetCard err:");
            Log.printStackTrace(TAG, th);
        }
    }

    public static String queryEnvironmentCertDetailList(String alias, int pageNum, String targetUserID) {
        return TestRpcCall.queryEnvironmentCertDetailList(alias, pageNum, targetUserID);
    }

    public static String sendTree(String certificateId, String friendUserId) {
        return TestRpcCall.sendTree(certificateId, friendUserId);
    }

    private static void getNewTreeItems() {
        try {
            String s = ReserveRpcCall.queryTreeItemsForExchange();
            JSONObject jo = new JSONObject(s);
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                JSONArray ja = jo.getJSONArray("treeItems");
                for (int i = 0; i < ja.length(); i++) {
                    jo = ja.getJSONObject(i);
                    if (!jo.has("projectType"))
                        continue;
                    if (!"TREE".equals(jo.getString("projectType")))
                        continue;
                    if (!"COMING".equals(jo.getString("applyAction")))
                        continue;
                    String projectId = jo.getString("itemId");
                    queryTreeForExchange(projectId);
                }
            } else {
                Log.i(TAG, jo.getString("resultDesc"));
            }
        } catch (Throwable t) {
            Log.i(TAG, "getTreeItems err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void queryTreeForExchange(String projectId) {
        try {
            String s = ReserveRpcCall.queryTreeForExchange(projectId);
            JSONObject jo = new JSONObject(s);
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                JSONObject exchangeableTree = jo.getJSONObject("exchangeableTree");
                int currentBudget = exchangeableTree.getInt("currentBudget");
                String region = exchangeableTree.getString("region");
                String treeName = exchangeableTree.getString("treeName");
                String tips = "不可合种";
                if (exchangeableTree.optBoolean("canCoexchange", false)) {
                    tips = "可以合种-合种类型："
                            + exchangeableTree.getJSONObject("extendInfo").getString("cooperate_template_id_list");
                }
                Log.forest("新树上苗🌱[" + region + "-" + treeName + "]#" + currentBudget + "株-" + tips);
            } else {
                Log.record(jo.getString("resultDesc") + s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryTreeForExchange err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void collectHistoryAnimal() {
        try {
            String s = AntForestRpcCall.exchangeBenefit("SP20230518000022", "SK20230518000062");
            JSONObject jo = new JSONObject(s);
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                antdodoPropList();
            } else {
                Log.record(jo.getString("resultDesc") + s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "collectHistoryAnimal err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void antdodoPropList() {
        try {
            JSONObject jo = new JSONObject(AntDodoRpcCall.propList());
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                JSONArray propList = jo.getJSONObject("data").optJSONArray("propList");
                for (int i = 0; i < propList.length(); i++) {
                    JSONObject prop = propList.getJSONObject(i);
                    String propType = prop.getString("propType");
                    if ("COLLECT_HISTORY_ANIMAL_7_DAYS".equals(propType)) {
                        JSONArray propIdList = prop.getJSONArray("propIdList");
                        String propId = propIdList.getString(0);
                        String propName = prop.getJSONObject("propConfig").getString("propName");
                        int holdsNum = prop.optInt("holdsNum", 0);
                        jo = new JSONObject(AntDodoRpcCall.consumeProp(propId, propType));
                        if ("SUCCESS".equals(jo.getString("resultCode"))) {
                            JSONObject useResult = jo.getJSONObject("data").getJSONObject("useResult");
                            JSONObject animal = useResult.getJSONObject("animal");
                            String ecosystem = animal.getString("ecosystem");
                            String name = animal.getString("name");
                            Log.forest("使用道具🎭[" + propName + "]#" + ecosystem + "-" + name);
                            if (holdsNum > 1) {
                                Thread.sleep(1000L);
                                antdodoPropList();
                                return;
                            }
                        } else {
                            Log.record(jo.getString("resultDesc") + jo.toString());
                        }
                    }
                }
            }
        } catch (Throwable th) {
            Log.i(TAG, "antdodoPropList err:");
            Log.printStackTrace(TAG, th);
        }
    }

    private static void getWateringLeftTimes() {
        try {
            JSONObject jo = new JSONObject(AntOrchardRpcCall.orchardIndex());
            if ("100".equals(jo.getString("resultCode"))) {
                String taobaoData = jo.getString("taobaoData");
                jo = new JSONObject(taobaoData);
                JSONObject plantInfo = jo.getJSONObject("gameInfo").getJSONObject("plantInfo");
                /*
                 * boolean canExchange = plantInfo.getBoolean("canExchange");
                 * if (canExchange) {
                 * Log.farm("农场果树似乎可以兑换了！");
                 * return;
                 * }
                 */
                JSONObject accountInfo = jo.getJSONObject("gameInfo").getJSONObject("accountInfo");
                int wateringLeftTimes = accountInfo.getInt("wateringLeftTimes");
                Log.farm("今日剩余施肥次数[" + wateringLeftTimes + "]");
            }
        } catch (Throwable t) {
            Log.i(TAG, "getWateringLeftTimes err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void getTreeItems() {
        try {
            String s = ReserveRpcCall.queryTreeItemsForExchange();
            JSONObject jo = new JSONObject(s);
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                JSONArray ja = jo.getJSONArray("treeItems");
                for (int i = 0; i < ja.length(); i++) {
                    jo = ja.getJSONObject(i);
                    if (!jo.has("projectType"))
                        continue;
                    if (!"AVAILABLE".equals(jo.getString("applyAction")))
                        continue;
                    String projectId = jo.getString("itemId");
                    String itemName = jo.getString("itemName");
                    getTreeCurrentBudget(projectId, itemName);
                    Thread.sleep(100);
                }
            } else {
                Log.i(TAG, jo.getString("resultDesc"));
            }
        } catch (Throwable t) {
            Log.i(TAG, "getTreeItems err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void getTreeCurrentBudget(String projectId, String treeName) {
        try {
            String s = ReserveRpcCall.queryTreeForExchange(projectId);
            JSONObject jo = new JSONObject(s);
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                JSONObject exchangeableTree = jo.getJSONObject("exchangeableTree");
                int currentBudget = exchangeableTree.getInt("currentBudget");
                String region = exchangeableTree.getString("region");
                Log.forest("树苗查询🌱[" + region + "-" + treeName + "]#剩余:" + currentBudget);
            } else {
                Log.record(jo.getString("resultDesc") + s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryTreeForExchange err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void batchHireAnimalRecommend() {
        try {
            JSONObject jo = new JSONObject(AntOrchardRpcCall.batchHireAnimalRecommend(UserIdMap.getCurrentUid()));
            if ("100".equals(jo.getString("resultCode"))) {
                JSONArray recommendGroupList = jo.optJSONArray("recommendGroupList");
                if (recommendGroupList != null && recommendGroupList.length() > 0) {
                    List<String> GroupList = new ArrayList<>();
                    for (int i = 0; i < recommendGroupList.length(); i++) {
                        jo = recommendGroupList.getJSONObject(i);
                        String animalUserId = jo.getString("animalUserId");
                        int earnManureCount = jo.getInt("earnManureCount");
                        String groupId = jo.getString("groupId");
                        String orchardUserId = jo.getString("orchardUserId");
                        GroupList.add("{\"animalUserId\":\"" + animalUserId + "\",\"earnManureCount\":"
                                + earnManureCount + ",\"groupId\":\"" + groupId + "\",\"orchardUserId\":\""
                                + orchardUserId + "\"}");
                    }
                    if (!GroupList.isEmpty()) {
                        jo = new JSONObject(AntOrchardRpcCall.batchHireAnimal(GroupList));
                        if ("100".equals(jo.getString("resultCode"))) {
                            Log.farm("一键捉鸡🐣[除草]");
                        }
                    }
                }
            } else {
                Log.record(jo.getString("resultDesc") + jo.toString());
            }
        } catch (Throwable t) {
            Log.i(TAG, "batchHireAnimalRecommend err:");
            Log.printStackTrace(TAG, t);
        }
    }
}
