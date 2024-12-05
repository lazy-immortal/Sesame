package io.github.lazyimmortal.sesame.model.extend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Objects;

import io.github.lazyimmortal.sesame.data.TokenConfig;
import io.github.lazyimmortal.sesame.hook.Toast;
import io.github.lazyimmortal.sesame.model.task.antDodo.AntDodo;
import io.github.lazyimmortal.sesame.model.task.antDodo.AntDodoRpcCall;
import io.github.lazyimmortal.sesame.model.task.antForest.AntForestV2;
import io.github.lazyimmortal.sesame.model.task.antSports.AntSportsRpcCall;
import io.github.lazyimmortal.sesame.model.task.protectEcology.ProtectTreeRpcCall;
import io.github.lazyimmortal.sesame.util.Log;
import io.github.lazyimmortal.sesame.util.MessageUtil;
import io.github.lazyimmortal.sesame.util.StringUtil;
import io.github.lazyimmortal.sesame.util.TimeUtil;
import io.github.lazyimmortal.sesame.util.idMap.UserIdMap;

public class ExtendHandle {
    private static final String TAG = ExtendHandle.class.getSimpleName();

    public static void handleRequest(String type, String fun, String data) {
        if (handleAlphaRequest(type, fun, data)) {
            return;
        }
        switch (type) {
            case "getTreeItems":
                getTreeItems();
                break;
            case "getNewTreeItems":
                getNewTreeItems();
                break;
            case "queryAreaTrees":
                queryAreaTrees();
                break;
            case "getUnlockTreeItems":
                getUnlockTreeItems();
                break;
            case "collectHistoryAnimal":
                collectHistoryAnimal();
                break;
            case "setCustomWalkPathId":
                setCustomWalkPathId(data);
                break;
            case "addCustomWalkPathIdQueue":
                addCustomWalkPathIdQueue(data);
                break;
            case "clearCustomWalkPathIdQueue":
                clearCustomWalkPathIdQueue();
                break;
        }
    }
    public static Boolean handleAlphaRequest(String type, String fun, String data) {
        try {
            return (Boolean) Class.forName("io.github.lazyimmortal.sesame.model.extend.ExtendHandleAlpha")
                    .getMethod("handleAlphaRequest", String.class, String.class, String.class)
                    .invoke(null, type, fun, data);
        } catch (Exception e) {
            return false;
        }
    }

    private static void getNewTreeItems() {
        try {
            JSONObject jo = new JSONObject(ProtectTreeRpcCall.queryTreeItemsForExchange("COMING", "project"));
            if (!MessageUtil.checkResultCode(TAG, jo)) {
                return;
            }
            JSONArray ja = jo.getJSONArray("treeItems");
            if (ja.length() == 0) {
                Log.forest("新树上苗🌱[当前没有新树上苗信息!]");
                return;
            }
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
        } catch (Throwable t) {
            Log.i(TAG, "getTreeItems err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void queryTreeForExchange(String projectId) {
        try {
            JSONObject jo = new JSONObject(ProtectTreeRpcCall.queryTreeForExchange(projectId));
            if (!MessageUtil.checkResultCode(TAG, jo)) {
                return;
            }
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
        } catch (Throwable t) {
            Log.i(TAG, "queryTreeForExchange err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void getTreeItems() {
        try {
            JSONObject jo = new JSONObject(ProtectTreeRpcCall.queryTreeItemsForExchange("AVAILABLE,ENERGY_LACK", "project"));
            if (!MessageUtil.checkResultCode(TAG, jo)) {
                return;
            }
            JSONArray ja = jo.getJSONArray("treeItems");
            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);
                if (!jo.has("projectType"))
                    continue;
                String projectId = jo.getString("itemId");
                String itemName = jo.getString("itemName");
                getTreeCurrentBudget(projectId, itemName);
                TimeUtil.sleep(100);
            }
        } catch (Throwable t) {
            Log.i(TAG, "getTreeItems err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void getTreeCurrentBudget(String projectId, String treeName) {
        try {
            JSONObject jo = new JSONObject(ProtectTreeRpcCall.queryTreeForExchange(projectId));
            if (MessageUtil.checkResultCode(TAG, jo)) {
                JSONObject exchangeableTree = jo.getJSONObject("exchangeableTree");
                int currentBudget = exchangeableTree.getInt("currentBudget");
                String region = exchangeableTree.getString("region");
                Log.forest("树苗查询🌱[" + region + "-" + treeName + "]#剩余:" + currentBudget);
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryTreeForExchange err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void queryAreaTrees() {
        try {
            JSONObject jo = new JSONObject(ProtectTreeRpcCall.queryAreaTrees());
            if (!MessageUtil.checkResultCode(TAG, jo)) {
                return;
            }
            JSONObject areaTrees = jo.getJSONObject("areaTrees");
            JSONObject regionConfig = jo.getJSONObject("regionConfig");
            Iterator<String> regionKeys = regionConfig.keys();
            while (regionKeys.hasNext()) {
                String regionKey = regionKeys.next();
                if (!areaTrees.has(regionKey)) {
                    JSONObject region = regionConfig.getJSONObject(regionKey);
                    String regionName = region.optString("regionName");
                    Log.forest("未解锁地区🗺️[" + regionName + "]");
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryAreaTrees err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void getUnlockTreeItems() {
        try {
            JSONObject jo = new JSONObject(ProtectTreeRpcCall.queryTreeItemsForExchange("", "project"));
            if (!MessageUtil.checkResultCode(TAG, jo)) {
                return;
            }
            JSONArray ja = jo.getJSONArray("treeItems");
            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);
                if (!jo.has("projectType"))
                    continue;
                int certCountForAlias = jo.optInt("certCountForAlias", -1);
                if (certCountForAlias == 0) {
                    String itemName = jo.optString("itemName");
                    String region = jo.optString("region");
                    String organization = jo.optString("organization");
                    Log.forest("未解锁项目🐘[" + region + "-" + itemName + "]#" + organization);
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "getUnlockTreeItems err:");
            Log.printStackTrace(TAG, t);
        }
    }

    // 检查是否需要收集历史物种
    private static Boolean canCollectHistoryAnimal() {
        // 图鉴合成状态 合成 可以合成 不能合成
        // medalGenerationStatus: GENERATED CAN_GENERATE CAN_NOT_GENERATE
        try {
            boolean hasMore;
            int pageStart = 0;
            do {
                JSONObject jo = new JSONObject(AntDodoRpcCall.queryBookList(9, pageStart));
                if (!MessageUtil.checkResultCode(TAG, jo)) {
                    break;
                }
                jo = jo.getJSONObject("data");
                hasMore = jo.getBoolean("hasMore");
                pageStart += 9;
                JSONArray bookForUserList = jo.getJSONArray("bookForUserList");
                for (int i = 0; i < bookForUserList.length(); i++) {
                    if (i == 0 && pageStart == 9) {
                        // 忽略当前专辑
                        continue;
                    }
                    jo = bookForUserList.getJSONObject(i);
                    if (!AntDodo.MedalGenerationStatus.CAN_NOT_GENERATE.name().equals(
                            jo.optString("medalGenerationStatus"))) {
                        continue;
                    }
                    return true;
                }
            } while (hasMore);}
        catch (Throwable t) {
            Log.i(TAG, "collectHistoryAnimal err:");
            Log.printStackTrace(TAG, t);
        }
        return false;
    }

    private static void usePropCollectHistoryAnimal() {
        try {
            JSONObject jo = new JSONObject(AntDodoRpcCall.propList());
            if (!MessageUtil.checkResultCode(TAG, jo)) {
                return;
            }
            JSONArray propList = jo.getJSONObject("data").optJSONArray("propList");
            if (propList == null) {
                return;
            }
            for (int i = 0; i < propList.length(); i++) {
                JSONObject prop = propList.getJSONObject(i);
                String propType = prop.getString("propType");
                if (!Objects.equals("COLLECT_HISTORY_ANIMAL_7_DAYS", propType)) {
                    continue;
                }
                JSONArray propIdList = prop.getJSONArray("propIdList");
                String propId = propIdList.getString(0);
                String propName = prop.getJSONObject("propConfig").getString("propName");
                jo = new JSONObject(AntDodoRpcCall.consumeProp(propId, propType));
                if (!MessageUtil.checkResultCode(TAG, jo)) {
                    return;
                }
                JSONObject useResult = jo.getJSONObject("data").getJSONObject("useResult");
                JSONObject animal = useResult.getJSONObject("animal");
                String animalInfo = AntDodo.getAnimalInfo(animal);
                Log.forest("使用道具🎭[" + propName + "]" + animalInfo);
                Toast.show("已收集历史物种，请在森林日志查看结果！");
                if (prop.optInt("holdsNum", 1) > 1) {
                    TimeUtil.sleep(1000L);
                    usePropCollectHistoryAnimal();
                    return;
                }
            }
        } catch (Throwable th) {
            Log.i(TAG, "usePropCollectHistoryAnimal err:");
            Log.printStackTrace(TAG, th);
        }
    }

    private static void collectHistoryAnimal() {
        if (!canCollectHistoryAnimal()) {
            Toast.show("没有需要收集的历史物种！");
            return;
        }
        if (AntForestV2.exchangeBenefit("SP20230518000022", "SK20230518000062", "神奇物种抽历史卡机会")) {
            usePropCollectHistoryAnimal();
        }
    }

    private static void setCustomWalkPathId(String pathId) {
        String userId = UserIdMap.getCurrentUid();
        if (StringUtil.isEmpty(userId)) {
            Toast.show("设置自定义路线失败:找不到用户信息");
            return;
        }
        String pathName = "自定义路线关闭";
        if (!StringUtil.isEmpty(pathId)) {
            pathName = AntSportsRpcCall.queryPathName(pathId);
            if (pathName == null) {
                Toast.show("设置自定义路线失败:找不到路线信息");
                return;
            }
        }
        String userMaskName = UserIdMap.getCurrentMaskName();
        if (TokenConfig.setCustomWalkPathId(userId, pathId)) {
            Toast.show("设置自定义路线成功:" + pathName + "-->" + userMaskName);
        }
    }

    private static void addCustomWalkPathIdQueue(String pathId) {
        if (!StringUtil.isEmpty(pathId)) {
            String pathName = AntSportsRpcCall.queryPathName(pathId);
            if (pathName == null) {
                Toast.show("添加待行走路线失败:找不到路线信息");
                return;
            }
            if (TokenConfig.addCustomWalkPathIdQueue(pathId)) {
                Toast.show("添加待行走路线成功:" + pathName);
            }
        }
    }

    private static void clearCustomWalkPathIdQueue() {
        if (TokenConfig.clearCustomWalkPathIdQueue()) {
            Toast.show("清除待行走路线成功");
        }
    }
}
