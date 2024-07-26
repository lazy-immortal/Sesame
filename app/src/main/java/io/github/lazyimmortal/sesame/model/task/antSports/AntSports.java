package io.github.lazyimmortal.sesame.model.task.antSports;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import org.json.JSONArray;
import org.json.JSONObject;
import io.github.lazyimmortal.sesame.data.ModelFields;
import io.github.lazyimmortal.sesame.data.ModelGroup;
import io.github.lazyimmortal.sesame.data.modelFieldExt.BooleanModelField;
import io.github.lazyimmortal.sesame.data.modelFieldExt.ChoiceModelField;
import io.github.lazyimmortal.sesame.data.modelFieldExt.IntegerModelField;
import io.github.lazyimmortal.sesame.data.modelFieldExt.SelectModelField;
import io.github.lazyimmortal.sesame.data.modelFieldExt.StringModelField;
import io.github.lazyimmortal.sesame.data.task.ModelTask;
import io.github.lazyimmortal.sesame.entity.AlipayUser;
import io.github.lazyimmortal.sesame.hook.ApplicationHook;
import io.github.lazyimmortal.sesame.model.base.TaskCommon;
import io.github.lazyimmortal.sesame.model.normal.base.BaseModel;
import io.github.lazyimmortal.sesame.util.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;

public class AntSports extends ModelTask {

    private static final String TAG = AntSports.class.getSimpleName();

    private int tmpStepCount = -1;
    private BooleanModelField walk;
    private ChoiceModelField walkPathTheme;
    private String walkPathThemeId;
    private BooleanModelField walkCustomPath;
    private StringModelField walkCustomPathId;
    private BooleanModelField receiveCoinAsset;
    private BooleanModelField donateCharityCoin;
    private ChoiceModelField donateCharityCoinType;
    private IntegerModelField donateCharityCoinAmount;
    private IntegerModelField minExchangeCount;
    private IntegerModelField latestExchangeTime;
    private IntegerModelField syncStepCount;
    private BooleanModelField tiyubiz;
    private BooleanModelField battleForFriends;
    private ChoiceModelField trainItemType;
    private ChoiceModelField battleForFriendType;
    private SelectModelField originBossIdList;
    private BooleanModelField sportsTasks;

    @Override
    public String getName() {
        return "运动";
    }

    @Override
    public ModelGroup getGroup() {
        return ModelGroup.SPORTS;
    }

    @Override
    public ModelFields getFields() {
        ModelFields modelFields = new ModelFields();
        modelFields.addField(walk = new BooleanModelField("walk", "行走路线 | 开启", false));
        modelFields.addField(walkPathTheme = new ChoiceModelField("walkPathTheme", "行走路线 | 主题", WalkPathTheme.DA_MEI_ZHONG_GUO, WalkPathTheme.nickNames));
        modelFields.addField(walkCustomPath = new BooleanModelField("walkCustomPath", "行走路线 | 开启自定义路线", false));
        modelFields.addField(walkCustomPathId = new StringModelField("walkCustomPathId", "行走路线 | 自定义路线代码(debug)", "p0002023122214520001"));
        modelFields.addField(sportsTasks = new BooleanModelField("sportsTasks", "开启运动任务", false));
        modelFields.addField(receiveCoinAsset = new BooleanModelField("receiveCoinAsset", "收运动币", false));
        modelFields.addField(donateCharityCoin = new BooleanModelField("donateCharityCoin", "捐运动币 | 开启", false));
        modelFields.addField(donateCharityCoinType = new ChoiceModelField("donateCharityCoinType", "捐运动币 | 方式", DonateCharityCoinType.ONE, DonateCharityCoinType.nickNames));
        modelFields.addField(donateCharityCoinAmount = new IntegerModelField("donateCharityCoinAmount", "捐运动币 | 数量(每次)", 100));
        modelFields.addField(battleForFriends = new BooleanModelField("battleForFriends", "抢好友 | 开启", false));
        modelFields.addField(trainItemType = new ChoiceModelField("trainItemType", "抢好友 | 训练项目", TrainItemType.BARBELL, TrainItemType.nickNames));
        modelFields.addField(battleForFriendType = new ChoiceModelField("battleForFriendType", "抢好友 | 动作", BattleForFriendType.ROB, BattleForFriendType.nickNames));
        modelFields.addField(originBossIdList = new SelectModelField("originBossIdList", "抢好友 | 好友列表", new LinkedHashSet<>(), AlipayUser::getList));
        modelFields.addField(tiyubiz = new BooleanModelField("tiyubiz", "文体中心", false));
        modelFields.addField(minExchangeCount = new IntegerModelField("minExchangeCount", "最小捐步步数", 0));
        modelFields.addField(latestExchangeTime = new IntegerModelField("latestExchangeTime", "最晚捐步时间(24小时制)", 22));
        modelFields.addField(syncStepCount = new IntegerModelField("syncStepCount", "自定义同步步数", 22000));
        return modelFields;
    }

    @Override
    public void boot(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod("com.alibaba.health.pedometer.core.datasource.PedometerAgent", classLoader,
                    "readDailyStep", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            int originStep = (Integer) param.getResult();
                            int step = tmpStepCount();
                            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 6 || originStep >= step) {
                                return;
                            }
                            param.setResult(step);
                        }
                    });
            Log.i(TAG, "hook readDailyStep successfully");
        } catch (Throwable t) {
            Log.i(TAG, "hook readDailyStep err:");
            Log.printStackTrace(TAG, t);
        }
    }

    @Override
    public Boolean check() {
        return !TaskCommon.IS_ENERGY_TIME;
    }

    @Override
    public void run() {
        try {
            if (Status.canSyncStepToday(UserIdMap.getCurrentUid()) && TimeUtil.isNowAfterOrCompareTimeStr("0600")) {
                addChildTask(new ChildModelTask("syncStep", () -> {
                    int step = tmpStepCount();
                    try {
                        ClassLoader classLoader = ApplicationHook.getClassLoader();
                        if ((Boolean) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(classLoader.loadClass("com.alibaba.health.pedometer.intergation.rpc.RpcManager"), "a"), "a", new Object[]{step, Boolean.FALSE, "system"})) {
                            Log.other("同步步数🏃🏻‍♂️[" + step + "步]");
                        } else {
                            Log.record("同步运动步数失败:" + step);
                        }
                        Status.SyncStepToday(UserIdMap.getCurrentUid());
                    } catch (Throwable t) {
                        Log.printStackTrace(TAG, t);
                    }
                }));
            }
            if (sportsTasks.getValue())
                sportsTasks();

            ClassLoader loader = ApplicationHook.getClassLoader();
            if (walk.getValue()) {
                walkPathThemeId = WalkPathTheme.walkPathThemeId[walkPathTheme.getValue()];
                walk();
            }

            if (donateCharityCoin.getValue() && Status.canDonateCharityCoin())
                queryProjectList(loader);

            if (minExchangeCount.getValue() > 0 && Status.canExchangeToday(UserIdMap.getCurrentUid()))
                queryWalkStep(loader);

            if (tiyubiz.getValue()) {
                userTaskGroupQuery("SPORTS_DAILY_SIGN_GROUP");
                userTaskGroupQuery("SPORTS_DAILY_GROUP");
                userTaskRightsReceive();
                pathFeatureQuery();
                participate();
            }

            if (battleForFriends.getValue()) {
                queryClubHomeBeforeCollect();
                queryClubHomeBeforeTrain();
                buyMember();
            }

            if (receiveCoinAsset.getValue())
                receiveCoinAsset();
        } catch (Throwable t) {
            Log.i(TAG, "start.run err:");
            Log.printStackTrace(TAG, t);
        }
    }

    public int tmpStepCount() {
        if (tmpStepCount >= 0) {
            return tmpStepCount;
        }
        tmpStepCount = syncStepCount.getValue();
        if (tmpStepCount > 0) {
            tmpStepCount = RandomUtil.nextInt(tmpStepCount, tmpStepCount + 2000);
            if (tmpStepCount > 100000) {
                tmpStepCount = 100000;
            }
        }
        return tmpStepCount;
    }

    // 运动
    private void sportsTasks() {
        try {
            sportsCheck_in();
            JSONObject jo = new JSONObject(AntSportsRpcCall.queryCoinTaskPanel());
            if (jo.optBoolean("success")) {
                JSONObject data = jo.getJSONObject("data");
                JSONArray taskList = data.getJSONArray("taskList");

                for (int i = 0; i < taskList.length(); i++) {
                    JSONObject taskDetail = taskList.getJSONObject(i);

                    String taskId = taskDetail.getString("taskId");
                    String taskName = taskDetail.getString("taskName");
                    String prizeAmount = taskDetail.getString("prizeAmount");
                    String taskStatus = taskDetail.getString("taskStatus");
                    int currentNum = taskDetail.getInt("currentNum");
                    // 要完成的次数
                    int limitConfigNum = taskDetail.getInt("limitConfigNum")-currentNum;

                    if (taskStatus.equals("HAS_RECEIVED"))
                        return;
                    for (int i1 = 0; i1 < limitConfigNum; i1++) {
                        jo = new JSONObject(AntSportsRpcCall.completeExerciseTasks(taskId));
                        if (jo.optBoolean("success")) {
                            Log.record("做任务得运动币👯[完成任务：" + taskName + "，得" + prizeAmount + "🪙]");
                            receiveCoinAsset();
                        }
                        if (limitConfigNum>1)
                            Thread.sleep(10000);
                        else
                            Thread.sleep(1000);
                    }


                }
            }
        } catch (Exception e) {
            Log.printStackTrace(e);
        }
    }
    private void sportsCheck_in() {
        try {
            JSONObject jo = new JSONObject(AntSportsRpcCall.sportsCheck_in());
            if (jo.optBoolean("success")) {
                JSONObject data = jo.getJSONObject("data");
                if(!data.getBoolean("signed")){
                    JSONObject subscribeConfig;
                    if (data.has("subscribeConfig")) {
                        subscribeConfig = data.getJSONObject("subscribeConfig");
                        Log.record("做任务得运动币👯[完成任务：签到" + subscribeConfig.getString("subscribeExpireDays")+"天，"+data.getString("toast") + "🪙]");
                    }else {
//                        Log.record("没有签到");
                    }
                }else {
                    Log.record("运动签到今日已签到");
                }
            }else {
                Log.record(jo.toString());
            }
        } catch (Exception e) {
            Log.record("sportsCheck_in err");
            Log.printStackTrace(e);
        }
    }

    private void receiveCoinAsset() {
        try {
            String s = AntSportsRpcCall.queryCoinBubbleModule();
            JSONObject jo = new JSONObject(s);
            if (jo.optBoolean("success")) {
                JSONObject data = jo.getJSONObject("data");
                if (!data.has("receiveCoinBubbleList"))
                    return;
                JSONArray ja = data.getJSONArray("receiveCoinBubbleList");
                for (int i = 0; i < ja.length(); i++) {
                    jo = ja.getJSONObject(i);
                    String assetId = jo.getString("assetId");
                    int coinAmount = jo.getInt("coinAmount");
                    jo = new JSONObject(AntSportsRpcCall.receiveCoinAsset(assetId, coinAmount));
                    if (jo.optBoolean("success")) {
                        Log.other("收集金币💰[" + coinAmount + "个]");
                    } else {
                        Log.record("首页收集金币" + " " + jo);
                    }
                }
            } else {
                Log.i(TAG, s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "receiveCoinAsset err:");
            Log.printStackTrace(TAG, t);
        }
    }

    /*
     * 新版行走路线 -- begin
     */
    private void walk() {
        try {
            JSONObject user = new JSONObject(AntSportsRpcCall.queryUser());
            if (!user.optBoolean("success")) {
                return;
            }
            String joinedPathId = user.getJSONObject("data").getString("joinedPathId");
            if (joinedPathId == null) {
                String pathId = queryJoinPath(walkPathThemeId);
                if(joinPath(pathId)) {
                    TimeUtil.sleep(1000);
                    walk();
                }
                return;
            }
            JSONObject path = queryPath(joinedPathId);
            JSONObject userPathStep = path.getJSONObject("userPathStep");
            if ("COMPLETED".equals(userPathStep.getString("pathCompleteStatus"))) {
                Log.record("行走路线🚶🏻‍♂️路线[" + userPathStep.getString("pathName") + "]已完成");
                String pathId = queryJoinPath(walkPathThemeId);
                if (joinPath(pathId)) {
                    TimeUtil.sleep(1000);
                    walk();
                }
                return;
            }
            int minGoStepCount = path.getJSONObject("path").getInt("minGoStepCount");
            int pathStepCount = path.getJSONObject("path").getInt("pathStepCount");
            int forwardStepCount = userPathStep.getInt("forwardStepCount");
            int remainStepCount = userPathStep.getInt("remainStepCount");
            int needStepCount = pathStepCount - forwardStepCount;
            if  (remainStepCount >= minGoStepCount) {
                int useStepCount = Math.min(remainStepCount, needStepCount);
                walkGo(userPathStep.getString("pathId"), useStepCount, userPathStep.getString("pathName"));
                TimeUtil.sleep(1000);
                walk();
            }
        } catch (Throwable t) {
            Log.i(TAG, "walk err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private void walkGo(String pathId, int useStepCount, String pathName) {
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            JSONObject jo = new JSONObject(AntSportsRpcCall.walkGo("202312191135", sdf.format(date), pathId, useStepCount));
            if (jo.optBoolean("success")) {
                Log.record("行走路线🚶🏻‍♂️路线[" + pathName + "]#前进了" + useStepCount + "步");
                queryPath(pathId);
            }
        } catch (Throwable t) {
            Log.i(TAG, "walkGo err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private JSONObject queryWorldMap(String themeId) {
        JSONObject theme = null;
        try {
            JSONObject jo = new JSONObject(AntSportsRpcCall.queryWorldMap(themeId));
            if (jo.optBoolean("success")) {
                theme = jo.getJSONObject("data");
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryWorldMap err:");
            Log.printStackTrace(TAG, t);
        }
        return theme;
    }

    private JSONObject queryCityPath(String cityId) {
        JSONObject city = null;
        try {
            JSONObject jo = new JSONObject(AntSportsRpcCall.queryCityPath(cityId));
            if (jo.optBoolean("success")) {
                city = jo.getJSONObject("data");
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryCityPath err:");
            Log.printStackTrace(TAG, t);
        }
        return city;
    }

    private JSONObject queryPath(String pathId) {
        JSONObject path = null;
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            JSONObject jo = new JSONObject(AntSportsRpcCall.queryPath("202312191135", sdf.format(date), pathId));
            if (jo.optBoolean("success")) {
                path = jo.getJSONObject("data");
                JSONArray ja = jo.getJSONObject("data").getJSONArray("treasureBoxList");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject treasureBox = ja.getJSONObject(i);
                    receiveEvent(treasureBox.getString("boxNo"));
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryPath err:");
            Log.printStackTrace(TAG, t);
        }
        return path;
    }

    private void receiveEvent(String eventBillNo) {
        try {
            JSONObject jo = new JSONObject(AntSportsRpcCall.receiveEvent(eventBillNo));
            if (!jo.optBoolean("success")) {
                return;
            }
            JSONArray ja = jo.getJSONObject("data").getJSONArray("rewards");
            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);
                Log.record("行走路线🎁开启宝箱[" + jo.getString("rewardName") + "]*" + jo.getInt("count"));
            }
        } catch (Throwable t) {
            Log.i(TAG, "receiveEvent err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private String queryJoinPath(String themeId) {
        if (walkCustomPath.getValue()) {
            return walkCustomPathId.getValue();
        }

        String pathId = null;
        try {
            JSONObject theme = queryWorldMap(walkPathThemeId);
            if (theme == null) {
                return pathId;
            }
            JSONArray cityList = theme.getJSONArray("cityList");
            for (int i = 0; i < cityList.length(); i++) {
                String cityId = cityList.getJSONObject(i).getString("cityId");
                JSONObject city = queryCityPath(cityId);
                if (city == null) {
                    continue;
                }
                JSONArray cityPathList = city.getJSONArray("cityPathList");
                for (int j = 0; j < cityPathList.length(); j++) {
                    JSONObject cityPath = cityPathList.getJSONObject(j);
                    pathId = cityPath.getString("pathId");
                    if (!"COMPLETED".equals(cityPath.getString("pathCompleteStatus"))) {
                        return pathId;
                    }
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryJoinPath err:");
            Log.printStackTrace(TAG, t);
        }
        return pathId;
    }

    private boolean joinPath(String pathId) {
        if (pathId == null) {
            // 龙年祈福线
            pathId = "p0002023122214520001";
        }
        try {
            JSONObject jo = new JSONObject(AntSportsRpcCall.joinPath(pathId));
            if (jo.optBoolean("success")) {
                JSONObject path = queryPath(pathId);
                Log.record("行走路线🚶🏻‍♂️路线[" + path.getJSONObject("path").getString("name") + "]已加入");
                return true;
            } else {
                Log.record("行走路线🚶🏻‍♂️路线[" + pathId + "]有误，无法加入！");
                return false;
            }
        } catch (Throwable t) {
            Log.i(TAG, "joinPath err:");
            Log.printStackTrace(TAG, t);
            return false;
        }
    }

    /*
     * 新版行走路线 -- end
     */

    private void queryProjectList(ClassLoader loader) {
        try {
            JSONObject jo = new JSONObject(AntSportsRpcCall.queryProjectList(0));
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                int charityCoinCount = jo.getInt("charityCoinCount");
                if (charityCoinCount < donateCharityCoinAmount.getValue()) {
                    return;
                }
                JSONArray ja = jo.getJSONObject("projectPage").getJSONArray("data");
                for (int i = 0; i < ja.length() && charityCoinCount >= donateCharityCoinAmount.getValue(); i++) {
                    jo = ja.getJSONObject(i).getJSONObject("basicModel");
                    if ("DONATE_COMPLETED".equals(jo.getString("footballFieldStatus"))) {
                        break;
                    }
                    donate(loader, donateCharityCoinAmount.getValue(), jo.getString("projectId"), jo.getString("title"));
                    Status.donateCharityCoin();
                    charityCoinCount -=  donateCharityCoinAmount.getValue();
                    if (donateCharityCoinType.getValue() == DonateCharityCoinType.ONE) {
                        break;
                    }
                }
            } else {
                Log.record(TAG);
                Log.i(jo.getString("resultDesc"));
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryProjectList err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private void donate(ClassLoader loader, int donateCharityCoin, String projectId, String title) {
        try {
            String s = AntSportsRpcCall.donate(donateCharityCoin, projectId);
            JSONObject jo = new JSONObject(s);
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                Log.other("捐赠活动❤️[" + title + "][" + donateCharityCoin + "运动币]");
            } else {
                Log.i(TAG, jo.getString("resultDesc"));
            }
        } catch (Throwable t) {
            Log.i(TAG, "donate err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private void queryWalkStep(ClassLoader loader) {
        try {
            String s = AntSportsRpcCall.queryWalkStep();
            JSONObject jo = new JSONObject(s);
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                jo = jo.getJSONObject("dailyStepModel");
                int produceQuantity = jo.getInt("produceQuantity");
                int hour = Integer.parseInt(Log.getFormatTime().split(":")[0]);
                if (produceQuantity >= minExchangeCount.getValue() || hour >= latestExchangeTime.getValue()) {
                    s = AntSportsRpcCall.walkDonateSignInfo(produceQuantity);
                    s = AntSportsRpcCall.donateWalkHome(produceQuantity);
                    jo = new JSONObject(s);
                    if (!jo.getBoolean("isSuccess"))
                        return;
                    JSONObject walkDonateHomeModel = jo.getJSONObject("walkDonateHomeModel");
                    JSONObject walkUserInfoModel = walkDonateHomeModel.getJSONObject("walkUserInfoModel");
                    if (!walkUserInfoModel.has("exchangeFlag")) {
                        Status.exchangeToday(UserIdMap.getCurrentUid());
                        return;
                    }

                    String donateToken = walkDonateHomeModel.getString("donateToken");
                    JSONObject walkCharityActivityModel = walkDonateHomeModel.getJSONObject("walkCharityActivityModel");
                    String activityId = walkCharityActivityModel.getString("activityId");

                    s = AntSportsRpcCall.exchange(activityId, produceQuantity, donateToken);
                    jo = new JSONObject(s);
                    if (jo.getBoolean("isSuccess")) {
                        JSONObject donateExchangeResultModel = jo.getJSONObject("donateExchangeResultModel");
                        int userCount = donateExchangeResultModel.getInt("userCount");
                        double amount = donateExchangeResultModel.getJSONObject("userAmount").getDouble("amount");
                        Log.other("捐出活动❤️[" + userCount + "步]#兑换" + amount + "元公益金");
                        Status.exchangeToday(UserIdMap.getCurrentUid());

                    } else if (s.contains("已捐步")) {
                        Status.exchangeToday(UserIdMap.getCurrentUid());
                    } else {
                        Log.i(TAG, jo.getString("resultDesc"));
                    }
                }
            } else {
                Log.i(TAG, jo.getString("resultDesc"));
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryWalkStep err:");
            Log.printStackTrace(TAG, t);
        }
    }

    /* 文体中心 */// SPORTS_DAILY_SIGN_GROUP SPORTS_DAILY_GROUP
    private void userTaskGroupQuery(String groupId) {
        try {
            String s = AntSportsRpcCall.userTaskGroupQuery(groupId);
            JSONObject jo = new JSONObject(s);
            if (jo.optBoolean("success")) {
                jo = jo.getJSONObject("group");
                JSONArray userTaskList = jo.getJSONArray("userTaskList");
                for (int i = 0; i < userTaskList.length(); i++) {
                    jo = userTaskList.getJSONObject(i);
                    if (!"TODO".equals(jo.getString("status")))
                        continue;
                    JSONObject taskInfo = jo.getJSONObject("taskInfo");
                    String bizType = taskInfo.getString("bizType");
                    String taskId = taskInfo.getString("taskId");
                    jo = new JSONObject(AntSportsRpcCall.userTaskComplete(bizType, taskId));
                    if (jo.optBoolean("success")) {
                        String taskName = taskInfo.optString("taskName", taskId);
                        Log.other("完成任务🧾[" + taskName + "]");
                    } else {
                        Log.record("文体每日任务" + " " + jo);
                    }
                }
            } else {
                Log.record("文体每日任务" + " " + s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "userTaskGroupQuery err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private void participate() {
        try {
            String s = AntSportsRpcCall.queryAccount();
            JSONObject jo = new JSONObject(s);
            if (jo.optBoolean("success")) {
                double balance = jo.getDouble("balance");
                if (balance < 100)
                    return;
                jo = new JSONObject(AntSportsRpcCall.queryRoundList());
                if (jo.optBoolean("success")) {
                    JSONArray dataList = jo.getJSONArray("dataList");
                    for (int i = 0; i < dataList.length(); i++) {
                        jo = dataList.getJSONObject(i);
                        if (!"P".equals(jo.getString("status")))
                            continue;
                        if (jo.has("userRecord"))
                            continue;
                        JSONArray instanceList = jo.getJSONArray("instanceList");
                        int pointOptions = 0;
                        String roundId = jo.getString("id");
                        String InstanceId = null;
                        String ResultId = null;
                        for (int j = instanceList.length() - 1; j >= 0; j--) {
                            jo = instanceList.getJSONObject(j);
                            if (jo.getInt("pointOptions") < pointOptions)
                                continue;
                            pointOptions = jo.getInt("pointOptions");
                            InstanceId = jo.getString("id");
                            ResultId = jo.getString("instanceResultId");
                        }
                        jo = new JSONObject(AntSportsRpcCall.participate(pointOptions, InstanceId, ResultId, roundId));
                        if (jo.optBoolean("success")) {
                            jo = jo.getJSONObject("data");
                            String roundDescription = jo.getString("roundDescription");
                            int targetStepCount = jo.getInt("targetStepCount");
                            Log.other("走路挑战🚶🏻‍♂️[" + roundDescription + "]#" + targetStepCount);
                        } else {
                            Log.record("走路挑战赛" + " " + jo);
                        }
                    }
                } else {
                    Log.record("queryRoundList" + " " + jo);
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "participate err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private void userTaskRightsReceive() {
        try {
            String s = AntSportsRpcCall.userTaskGroupQuery("SPORTS_DAILY_GROUP");
            JSONObject jo = new JSONObject(s);
            if (jo.optBoolean("success")) {
                jo = jo.getJSONObject("group");
                JSONArray userTaskList = jo.getJSONArray("userTaskList");
                for (int i = 0; i < userTaskList.length(); i++) {
                    jo = userTaskList.getJSONObject(i);
                    if (!"COMPLETED".equals(jo.getString("status")))
                        continue;
                    String userTaskId = jo.getString("userTaskId");
                    JSONObject taskInfo = jo.getJSONObject("taskInfo");
                    String taskId = taskInfo.getString("taskId");
                    jo = new JSONObject(AntSportsRpcCall.userTaskRightsReceive(taskId, userTaskId));
                    if (jo.optBoolean("success")) {
                        String taskName = taskInfo.optString("taskName", taskId);
                        JSONArray rightsRuleList = taskInfo.getJSONArray("rightsRuleList");
                        StringBuilder award = new StringBuilder();
                        for (int j = 0; j < rightsRuleList.length(); j++) {
                            jo = rightsRuleList.getJSONObject(j);
                            award.append(jo.getString("rightsName")).append("*").append(jo.getInt("baseAwardCount"));
                        }
                        Log.other("领取奖励🎖️[" + taskName + "]#" + award);
                    } else {
                        Log.record("文体中心领取奖励");
                        Log.i(jo.toString());
                    }
                }
            } else {
                Log.record("文体中心领取奖励");
                Log.i(s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "userTaskRightsReceive err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private void pathFeatureQuery() {
        try {
            String s = AntSportsRpcCall.pathFeatureQuery();
            JSONObject jo = new JSONObject(s);
            if (jo.optBoolean("success")) {
                JSONObject path = jo.getJSONObject("path");
                String pathId = path.getString("pathId");
                String title = path.getString("title");
                int minGoStepCount = path.getInt("minGoStepCount");
                if (jo.has("userPath")) {
                    JSONObject userPath = jo.getJSONObject("userPath");
                    String userPathRecordStatus = userPath.getString("userPathRecordStatus");
                    if ("COMPLETED".equals(userPathRecordStatus)) {
                        pathMapHomepage(pathId);
                        pathMapJoin(title, pathId);
                    } else if ("GOING".equals(userPathRecordStatus)) {
                        pathMapHomepage(pathId);
                        String countDate = Log.getFormatDate();
                        jo = new JSONObject(AntSportsRpcCall.stepQuery(countDate, pathId));
                        if (jo.optBoolean("success")) {
                            int canGoStepCount = jo.getInt("canGoStepCount");
                            if (canGoStepCount >= minGoStepCount) {
                                String userPathRecordId = userPath.getString("userPathRecordId");
                                tiyubizGo(countDate, title, canGoStepCount, pathId, userPathRecordId);
                            }
                        }
                    }
                } else {
                    pathMapJoin(title, pathId);
                }
            } else {
                Log.i(TAG, jo.getString("resultDesc"));
            }
        } catch (Throwable t) {
            Log.i(TAG, "pathFeatureQuery err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private void pathMapHomepage(String pathId) {
        try {
            String s = AntSportsRpcCall.pathMapHomepage(pathId);
            JSONObject jo = new JSONObject(s);
            if (jo.optBoolean("success")) {
                if (!jo.has("userPathGoRewardList"))
                    return;
                JSONArray userPathGoRewardList = jo.getJSONArray("userPathGoRewardList");
                for (int i = 0; i < userPathGoRewardList.length(); i++) {
                    jo = userPathGoRewardList.getJSONObject(i);
                    if (!"UNRECEIVED".equals(jo.getString("status")))
                        continue;
                    String userPathRewardId = jo.getString("userPathRewardId");
                    jo = new JSONObject(AntSportsRpcCall.rewardReceive(pathId, userPathRewardId));
                    if (jo.optBoolean("success")) {
                        jo = jo.getJSONObject("userPathRewardDetail");
                        JSONArray rightsRuleList = jo.getJSONArray("userPathRewardRightsList");
                        StringBuilder award = new StringBuilder();
                        for (int j = 0; j < rightsRuleList.length(); j++) {
                            jo = rightsRuleList.getJSONObject(j).getJSONObject("rightsContent");
                            award.append(jo.getString("name")).append("*").append(jo.getInt("count"));
                        }
                        Log.other("文体宝箱🎁[" + award + "]");
                    } else {
                        Log.record("文体中心开宝箱");
                        Log.i(jo.toString());
                    }
                }
            } else {
                Log.record("文体中心开宝箱");
                Log.i(s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "pathMapHomepage err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private void pathMapJoin(String title, String pathId) {
        try {
            JSONObject jo = new JSONObject(AntSportsRpcCall.pathMapJoin(pathId));
            if (jo.optBoolean("success")) {
                Log.other("加入线路🚶🏻‍♂️[" + title + "]");
                pathFeatureQuery();
            } else {
                Log.i(TAG, jo.toString());
            }
        } catch (Throwable t) {
            Log.i(TAG, "pathMapJoin err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private void tiyubizGo(String countDate, String title, int goStepCount, String pathId,
                           String userPathRecordId) {
        try {
            String s = AntSportsRpcCall.tiyubizGo(countDate, goStepCount, pathId, userPathRecordId);
            JSONObject jo = new JSONObject(s);
            if (jo.optBoolean("success")) {
                jo = jo.getJSONObject("userPath");
                Log.other("行走线路🚶🏻‍♂️[" + title + "]#前进了" + jo.getInt("userPathRecordForwardStepCount") + "步");
                pathMapHomepage(pathId);
                boolean completed = "COMPLETED".equals(jo.getString("userPathRecordStatus"));
                if (completed) {
                    Log.other("完成线路🚶🏻‍♂️[" + title + "]");
                    pathFeatureQuery();
                }
            } else {
                Log.i(TAG, s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "tiyubizGo err:");
            Log.printStackTrace(TAG, t);
        }
    }

    /* 抢好友大战 */
    private void queryClubHomeBeforeCollect() {
        try {
            JSONObject jo = new JSONObject(AntSportsRpcCall.queryClubHome());
            if (!"SUCCESS".equals(jo.getString("resultCode"))) {
                Log.i(TAG, jo.getString("resultDesc"));
                return;
            }
            JSONArray roomList = jo.optJSONArray("roomList");
            if (roomList == null) {
                return;
            }
            for (int i = 0; i < roomList.length(); i++) {
                JSONObject room = roomList.getJSONObject(i);
                collectBubble(room);
                TimeUtil.sleep(1000);
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryClubHomeBeforeCollect err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private void queryClubHomeBeforeTrain() {
        try {
            JSONObject jo = new JSONObject(AntSportsRpcCall.queryClubHome());
            if (!"SUCCESS".equals(jo.getString("resultCode"))) {
                Log.i(TAG, jo.getString("resultDesc"));
                return;
            }
            JSONArray roomList = jo.optJSONArray("roomList");
            if (roomList == null) {
                return;
            }
            for (int i = 0; i < roomList.length(); i++) {
                JSONObject room = roomList.getJSONObject(i);
                trainMember(room);
                TimeUtil.sleep(1000);
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryClubHomeBeforeTrain err:");
            Log.printStackTrace(TAG, t);
        }
    }

    // 抢好友大战-收集运动币
    private void collectBubble(JSONObject room) {
        try {
            if (room == null || !room.has("bubbleList")) {
                return;
            }
            JSONArray bubbleList = room.getJSONArray("bubbleList");
            for (int i = 0; i < bubbleList.length(); i++) {
                JSONObject bubble = bubbleList.getJSONObject(i);
                String bubbleId = bubble.getString("bubbleId");
                JSONObject jo = new JSONObject(AntSportsRpcCall.collectBubble(bubbleId));
                if (!"SUCCESS".equals(jo.getString("resultCode"))) {
                    Log.i(TAG, jo.getString("resultDesc"));
                    return;
                }
                int collectCoin = jo.getInt("collectCoin");
                Log.record("训练好友💰️[获得:" + collectCoin + "金币]");
            }
        } catch (Throwable t) {
            Log.i(TAG, "collectBubble err:");
            Log.printStackTrace(TAG, t);
        }
    }

    // 抢好友大战-训练好友
    private void trainMember(JSONObject room) {
        try {
            if (room == null || !room.has("memberList")) {
                return;
            }
            JSONArray memberList = room.getJSONArray("memberList");
            for (int i = 0; i < memberList.length(); i++) {
                JSONObject member = memberList.getJSONObject(i);
                String memberId = member.getString("memberId");
                String originBossId = member.getString("originBossId");
                JSONObject trainInfo = member.getJSONObject("trainInfo");
                if (!trainInfo.getBoolean("training")) {
                    trainMember(memberId, originBossId);
                } else {
                    autoTrainMember(memberId, originBossId, trainInfo);
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "trainMember err:");
            Log.printStackTrace(TAG, t);
        }
    }

    // 抢好友大战-获取训练项目
    private JSONObject getTrainItem() {
        JSONObject trainItem = null;
        try {
            JSONObject jo = new JSONObject(AntSportsRpcCall.queryTrainItem());
            if (!jo.optBoolean("success")) {
                return trainItem;
            }
            String selectedTrainItemType = TrainItemType.itemTypes[trainItemType.getValue()];
            JSONArray trainItemList = jo.getJSONArray("trainItemList");
            for (int i = 0; i < trainItemList.length(); i++) {
                trainItem = trainItemList.getJSONObject(i);
                String itemType = trainItem.getString("itemType");
                if (itemType.equals(selectedTrainItemType)) {
                    return  trainItem;
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "getTrainItem err:");
            Log.printStackTrace(TAG, t);
        }
        return trainItem;
    }

    // 抢好友大战-训练指定好友
    private void trainMember(String memberId, String originBossId) {
        try {
            JSONObject trainItem = getTrainItem();
            String trainItemName = trainItem.getString("name");
            String trainItemType = trainItem.getString("itemType");
            JSONObject jo = new JSONObject(AntSportsRpcCall.trainMember(trainItemType, memberId, originBossId));
            if (jo.optBoolean("success")) {
                String userName = UserIdMap.getMaskName(originBossId);
                Log.record("训练好友🥋训练[" + userName + "]" + trainItemName);
                String taskId = "TRAIN|" + originBossId;
                if (hasChildTask(taskId)) {
                    removeChildTask(taskId);
                }
                autoTrainMember(memberId, originBossId, jo.getJSONObject("trainInfo"));
            }
        } catch (Throwable t) {
            Log.i(TAG, "collectBubble err:");
            Log.printStackTrace(TAG, t);
        }
    }

    // 抢好友大战-蹲点训练
    private void autoTrainMember(String memberId, String originBossId, JSONObject trainInfo) {
        try {
            // 获取用户名称
            String userName = UserIdMap.getMaskName(originBossId);

            Long gmtEnd = trainInfo.getLong("gmtEnd");
            String taskId = "TRAIN|" + originBossId;
            if (!hasChildTask(taskId)) {
                addChildTask(new ChildModelTask(taskId, "TRAIN", () -> {
                    queryClubHomeBeforeCollect();
                    trainMember(memberId, originBossId);
                }, gmtEnd));
                Log.record("添加蹲点训练🥋[" + userName + "]在[" + TimeUtil.getCommonDate(gmtEnd) + "]执行");
            }
        } catch (Throwable t) {
            Log.i(TAG, "autoTrainMember err:");
            Log.printStackTrace(TAG, t);
        }
    }

    // 抢好友大战-抢购好友
    private void buyMember() {
        try {
            // 发送 RPC 请求获取 club home 数据
            String clubHomeResponse = AntSportsRpcCall.queryClubHome();
            TimeUtil.sleep(500);
            JSONObject clubHomeJson = new JSONObject(clubHomeResponse);
            // 判断 clubAuth 字段是否为 "ENABLE"
            if (!clubHomeJson.optString("clubAuth").equals("ENABLE")) {
                // 如果 clubAuth 不是 "ENABLE"，停止执行
                Log.record("抢好友大战🧑‍🤝‍🧑未授权开启");
                return;
            }
            // 获取 coinBalance 的值
            JSONObject assetsInfo = clubHomeJson.getJSONObject("assetsInfo");
            int coinBalance = assetsInfo.getInt("coinBalance");
            JSONArray roomList = clubHomeJson.getJSONArray("roomList");
            // 遍历 roomList
            for (int i = 0; i < roomList.length(); i++) {
                JSONObject room = roomList.getJSONObject(i);
                JSONArray memberList = room.optJSONArray("memberList");
                // 检查 memberList 是否为空
                if (memberList == null || memberList.length() == 0) {
                    // 获取 roomId 的值
                    String roomId = room.getString("roomId");
                    // 调用 queryMemberPriceRanking 方法并传递 coinBalance 的值
                    String memberPriceResult = AntSportsRpcCall.queryMemberPriceRanking(String.valueOf(coinBalance));
                    TimeUtil.sleep(500);
                    JSONObject memberPriceJson = new JSONObject(memberPriceResult);
                    // 检查是否存在 rank 字段
                    if (memberPriceJson.has("rank") && memberPriceJson.getJSONObject("rank").has("data")) {
                        JSONArray dataArray = memberPriceJson.getJSONObject("rank").getJSONArray("data");
                        // 遍历 data 数组
                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject dataObj = dataArray.getJSONObject(j);
                            String originBossId = dataObj.getString("originBossId");
                            // 检查 originBossId 是否在 originBossIdList 中
                            boolean isBattleForFriend = originBossIdList.getValue().contains(originBossId);
                            if (battleForFriendType.getValue() == BattleForFriendType.DONT_ROB) {
                                isBattleForFriend = !isBattleForFriend;
                            }
                            if (isBattleForFriend) {
                                // 在这里调用 queryClubMember 方法并传递 memberId 和 originBossId 的值
                                String clubMemberResult = AntSportsRpcCall.queryClubMember(dataObj.getString("memberId"), originBossId);
                                TimeUtil.sleep(500);
                                // 解析 queryClubMember 返回的 JSON 数据
                                JSONObject clubMemberJson = new JSONObject(clubMemberResult);
                                if (clubMemberJson.has("member")) {
                                    JSONObject memberObj = clubMemberJson.getJSONObject("member");
                                    // 获取当前成员的信息
                                    String currentBossId = memberObj.getString("currentBossId");
                                    String memberId = memberObj.getString("memberId");
                                    String priceInfo = memberObj.getString("priceInfo");
                                    // 调用 buyMember 方法
                                    String buyMemberResult = AntSportsRpcCall.buyMember(currentBossId, memberId, originBossId, priceInfo, roomId);
                                    TimeUtil.sleep(500);
                                    // 处理 buyMember 的返回结果
                                    JSONObject buyMemberResponse = new JSONObject(buyMemberResult);
                                    if ("SUCCESS".equals(buyMemberResponse.getString("resultCode"))) {
                                        String userName = UserIdMap.getMaskName(originBossId);
                                        Log.other("抢购好友🥋[成功:将 " + userName + " 抢回来]");
                                        // 执行训练好友
                                        trainMember(memberId, originBossId);
                                    } else if ("CLUB_AMOUNT_NOT_ENOUGH".equals(buyMemberResponse.getString("resultCode"))) {
                                        Log.record("[运动币不足，无法完成抢购好友！]");
                                    } else if ("CLUB_MEMBER_TRADE_PROTECT".equals(buyMemberResponse.getString("resultCode"))) {
                                        Log.record("[暂时无法抢购好友，给Ta一段独处的时间吧！]");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "buyMember err:");
            Log.printStackTrace(TAG, t);
        }
    }

    public interface WalkPathTheme {
        int DA_MEI_ZHONG_GUO = 0;
        int GONG_YI_YI_XIAO_BU = 1;
        int DENG_DING_ZHI_MA_SHAN = 2;
        int WEI_C_DA_TIAO_ZHAN = 3;
        int LONG_NIAN_QI_FU = 4;

        String[] nickNames = {"大美中国", "公益一小步", "登顶芝麻山", "维C大挑战", "龙年祈福"};
        String[] walkPathThemeId = {"M202308082226", "M202401042147", "V202405271625", "202404221422", "WF202312050200"};
    }

    public interface DonateCharityCoinType {

        int ONE = 0;
        int ALL = 1;

        String[] nickNames = {"捐赠一个项目", "捐赠所有项目"};

    }

    public interface BattleForFriendType {

        int ROB = 0;
        int DONT_ROB = 1;

        String[] nickNames = {"选中抢", "选中不抢"};

    }

    public interface TrainItemType {

        int BALLET = 0;
        int SANDBAG = 1;
        int BARBELL = 2;
        int YANGKO = 3;
        int SKATE = 4;
        int MUD = 5;

        String[] nickNames = {"跳芭蕾", "打沙包", "举杠铃", "扭秧歌", "玩滑板", "踩泥坑"};
        String[] itemTypes = {"ballet", "sandbag", "barbell", "yangko", "skate", "mud"};

    }
}