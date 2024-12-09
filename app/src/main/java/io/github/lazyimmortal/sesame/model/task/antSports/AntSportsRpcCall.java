package io.github.lazyimmortal.sesame.model.task.antSports;

import org.json.JSONObject;

import io.github.lazyimmortal.sesame.hook.ApplicationHook;
import io.github.lazyimmortal.sesame.util.Log;
import io.github.lazyimmortal.sesame.util.MessageUtil;

public class AntSportsRpcCall {
    private static final String chInfo = "ch_appcenter__chsub_9patch",
            timeZone = "Asia/Shanghai", version = "3.0.1.2", alipayAppVersion = "0.0.852",
            cityCode = "330100", appId = "2021002116659397";

    // 运动任务查询
    public static String queryCoinTaskPanel() {
        String args = "[{}]";
        return ApplicationHook.requestString("com.alipay.sportshealth.biz.rpc.SportsHealthCoinTaskRpc.queryCoinTaskPanel", args);
    }

    public static String signUpTask(String taskId) {
        String args = "[{\"taskId\":\"" + taskId + "\"}]";
        return ApplicationHook.requestString("com.alipay.sportshealth.biz.rpc.SportsHealthCoinTaskRpc.signUpTask", args);
    }

    public static String completeTask(String taskAction, String taskId) {
        String args = "[{\"taskAction\":\"" + taskAction + "\",\"taskId\":\"" + taskId + "\"}]";
        return ApplicationHook.requestString("com.alipay.sportshealth.biz.rpc.SportsHealthCoinTaskRpc.completeTask", args);
    }
    public static String signInCoinTask() {
        String args = "[{\"operatorType\":\"signIn\"}]";
        return ApplicationHook.requestString("com.alipay.sportshealth.biz.rpc.SportsHealthCoinTaskRpc.signInCoinTask", args);
    }

    public static String queryCoinBubbleModule() {
        String args = "[{}]";
        return ApplicationHook.requestString("com.alipay.sportshealth.biz.rpc.sportsHealthHomeRpc.queryCoinBubbleModule", args);
    }

    public static String receiveCoinAsset(String assetId, int coinAmount) {
        // "tracertPos": "首页金币收集" "任务面板"
        String args = "[{\"assetId\":\"" + assetId + "\",\"coinAmount\":" + coinAmount + "}]";
        return ApplicationHook.requestString("com.alipay.sportshealth.biz.rpc.SportsHealthCoinCenterRpc.receiveCoinAsset", args);
    }

    public static String queryDonateRecord() {
        String args = "[{\"pageIndex\":1,\"pageSize\":10}]";
        return ApplicationHook.requestString("alipay.antsports.walk.charity.queryDonateRecord", args);
    }

    public static String queryProjectList(int index) {
        String args = "[{\"index\":" + index + ",\"projectListUseVertical\":true}]";
        return ApplicationHook.requestString("alipay.antsports.walk.charity.queryProjectList", args);
    }

    public static String queryProjectDetail(String projectId) {
        String args = "[{\"projectId\": \"" + projectId + "\"}]";
        return ApplicationHook.requestString("alipay.antsports.walk.charity.queryProjectDetail", args);
    }

    public static String donate(int donateCharityCoin, String projectId) {
        String args = "[{\"donateCharityCoin\":" + donateCharityCoin + ",\"projectId\":\"" + projectId + "\"}]";
        return ApplicationHook.requestString("alipay.antsports.walk.charity.donate", args);
    }

    public static String queryWalkStep() {
        String args = "[{}]";
        return ApplicationHook.requestString("alipay.antsports.walk.user.queryWalkStep", args);
    }

    public static String walkDonateSignInfo(int count) {
        return ApplicationHook.requestString("alipay.charity.mobile.donate.walk.walkDonateSignInfo",
                "[{\"needDonateAction\":false,\"source\":\"walkDonateHome\",\"steps\":" + count
                        + ",\"timezoneId\":\""
                        + timeZone + "\"}]");
    }

    public static String donateWalkHome(int steps) {
        String args = "[{\"module\":\"3\",\"steps\":" + steps + ",\"timezoneId\":\"" + timeZone + "\"}]";
        return ApplicationHook.requestString("alipay.charity.mobile.donate.walk.home", args);
    }

    public static String donateExchangeRecord() {
        String args = "[{\"page\":1,\"pageSize\":10}]";
        return ApplicationHook.requestString("alipay.charity.mobile.donate.exchange.record", args);
    }

    public static String donateWalkExchange(String actId, int count, String donateToken) {
        return ApplicationHook.requestString("alipay.charity.mobile.donate.walk.exchange",
                "[{\"actId\":\"" + actId + "\",\"count\":"
                        + count + ",\"donateToken\":\"" + donateToken + "\",\"timezoneId\":\""
                        + timeZone + "\",\"ver\":0}]");
    }


    /*
     * 新版 走路线
     */

    // 查询用户
    public static String queryUser() {
        return ApplicationHook.requestString("com.alipay.sportsplay.biz.rpc.walk.queryUser", "[{}]");
    }

    // 查询主题列表
    public static String queryThemeList() {
        return ApplicationHook.requestString("com.alipay.sportsplay.biz.rpc.walk.theme.queryThemeList", "[{}]");
    }

    // 查询世界地图
    public static String queryWorldMap(String themeId) {
        String args = "[{\"themeId\":\"" + themeId + "\"}]";
        return ApplicationHook.requestString("com.alipay.sportsplay.biz.rpc.walk.queryWorldMap", args);
    }

    // 查询城市路线
    public static String queryCityPath(String cityId) {
        String args = "[{\"cityId\":\"" + cityId + "\"}]";
        return ApplicationHook.requestString("com.alipay.sportsplay.biz.rpc.walk.queryCityPath", args);
    }

    // 查询路线
    public static String queryPath(String date, String pathId) {
        String args = "[{\"date\":\"" + date + "\",\"pathId\":\"" + pathId + "\"}]";
        return ApplicationHook.requestString("com.alipay.sportsplay.biz.rpc.walk.queryPath", args);
    }

    public static String queryPathName(String pathId) {
        try {
            JSONObject jo = new JSONObject(queryPath(Log.getFormatDate(), pathId));
            if (MessageUtil.checkSuccess("queryPathName", jo)) {
                jo = jo.getJSONObject("data").getJSONObject("path");
                return jo.getString("name");
            }
        } catch (Throwable t) {
            Log.record("查询路线:[" + pathId + "]失败！");
        }
        return null;
    }

    // 加入路线
    public static String joinPath(String pathId) {
        String args = "[{\"pathId\":\"" + pathId + "\"}]";
        return ApplicationHook.requestString("com.alipay.sportsplay.biz.rpc.walk.joinPath", args);
    }

    // 行走路线
    public static String walkGo(String date, String pathId, int useStepCount) {
        String args = "[{\"date\":\"" + date + "\",\"pathId\":\"" + pathId + "\",\"useStepCount\":\"" + useStepCount + "\"}]";
        return ApplicationHook.requestString("com.alipay.sportsplay.biz.rpc.walk.go", args);
    }

    public static String queryMailList() {
        String args = "[{\"mailType\":\"SYSTEM\",\"pageSize\":50,\"userMailStartIndex\":1}]";
        return ApplicationHook.requestString("alipay.antsports.walk.mail.queryMailList", args);
    }

    // 开启宝箱
    // eventBillNo = boxNo(WalkGo)
    public static String receiveEvent(String eventBillNo) {
        return ApplicationHook.requestString("com.alipay.sportsplay.biz.rpc.walk.receiveEvent",
                "[{\"eventBillNo\":\"" + eventBillNo + "\"}]");
    }

    // 查询路线奖励
    public static String queryPathReward(String pathId) {
        String args = "[{\"pathId\":\"" + pathId + "\"}]";
        return ApplicationHook.requestString("com.alipay.sportsplay.biz.rpc.walk.queryPathReward", args);
    }

    /* 这个好像没用 */
    public static String exchangeSuccess(String exchangeId) {
        String args1 = "[{\"exchangeId\":\"" + exchangeId
                + "\",\"timezone\":\"GMT+08:00\",\"version\":\"" + version + "\"}]";
        return ApplicationHook.requestString("alipay.charity.mobile.donate.exchange.success", args1);
    }

    /* 文体中心 */
    public static String userTaskGroupQuery(String groupId) {
        return ApplicationHook.requestString("alipay.tiyubiz.sports.userTaskGroup.query",
                "[{\"cityCode\":\"" + cityCode + "\",\"groupId\":\"" + groupId + "\"}]");
    }

    public static String userTaskComplete(String bizType, String taskId) {
        return ApplicationHook.requestString("alipay.tiyubiz.sports.userTask.complete",
                "[{\"bizType\":\"" + bizType + "\",\"cityCode\":\"" + cityCode + "\",\"completedTime\":"
                        + System.currentTimeMillis() + ",\"taskId\":\"" + taskId + "\"}]");
    }

    public static String userTaskRightsReceive(String taskId, String userTaskId) {
        return ApplicationHook.requestString("alipay.tiyubiz.sports.userTaskRights.receive",
                "[{\"taskId\":\"" + taskId + "\",\"userTaskId\":\"" + userTaskId + "\"}]");
    }

    public static String queryAccount() {
        return ApplicationHook.requestString("alipay.tiyubiz.user.asset.query.account",
                "[{\"accountType\":\"TIYU_SEED\"}]");
    }

    public static String queryRoundList() {
        return ApplicationHook.requestString("alipay.tiyubiz.wenti.walk.queryRoundList",
                "[{}]");
    }

    public static String participate(int bettingPoints, String InstanceId, String ResultId, String roundId) {
        return ApplicationHook.requestString("alipay.tiyubiz.wenti.walk.participate",
                "[{\"bettingPoints\":" + bettingPoints + ",\"guessInstanceId\":\"" + InstanceId
                        + "\",\"guessResultId\":\"" + ResultId
                        + "\",\"newParticipant\":false,\"roundId\":\"" + roundId
                        + "\",\"stepTimeZone\":\"" + timeZone + "\"}]");
    }

    public static String pathFeatureQuery() {
        return ApplicationHook.requestString("alipay.tiyubiz.path.feature.query",
                "[{\"appId\":\"" + appId
                        + "\",\"features\":[\"USER_CURRENT_PATH_SIMPLE\"],\"sceneCode\":\"wenti_shijiebei\"}]");
    }

    public static String pathMapJoin(String pathId) {
        return ApplicationHook.requestString("alipay.tiyubiz.path.map.join",
                "[{\"appId\":\"" + appId + "\",\"pathId\":\"" + pathId + "\"}]");
    }

    public static String pathMapHomepage(String pathId) {
        return ApplicationHook.requestString("alipay.tiyubiz.path.map.homepage",
                "[{\"appId\":\"" + appId + "\",\"pathId\":\"" + pathId + "\"}]");
    }

    public static String stepQuery(String countDate, String pathId) {
        return ApplicationHook.requestString("alipay.tiyubiz.path.map.step.query",
                "[{\"appId\":\"" + appId + "\",\"countDate\":\"" + countDate
                        + "\",\"pathId\":\""
                        + pathId + "\",\"timeZone\":\"" + timeZone + "\"}]");
    }

    public static String tiyubizGo(String countDate, int goStepCount, String pathId, String userPathRecordId) {
        return ApplicationHook.requestString("alipay.tiyubiz.path.map.go",
                "[{\"appId\":\"" + appId + "\",\"countDate\":\"" + countDate
                        + "\",\"goStepCount\":"
                        + goStepCount + ",\"pathId\":\"" + pathId
                        + "\",\"timeZone\":\"" + timeZone + "\",\"userPathRecordId\":\""
                        + userPathRecordId + "\"}]");
    }

    public static String rewardReceive(String pathId, String userPathRewardId) {
        return ApplicationHook.requestString("alipay.tiyubiz.path.map.reward.receive",
                "[{\"appId\":\"" + appId + "\",\"pathId\":\"" + pathId + "\",\"userPathRewardId\":\""
                        + userPathRewardId + "\"}]");
    }

    /* 抢好友大战 */
    public static String queryClubHome() {
        return ApplicationHook.requestString("alipay.antsports.club.home.queryClubHome",
                "[{\"chInfo\":\"healthstep\",\"timeZone\":\"" + timeZone + "\"}]");
    }

    public static String queryClubRoom(String roomId) {
        String args = "[{\"chInfo\":\"healthstep\",\"roomId\":\"" + roomId + "\"}]";
        return ApplicationHook.requestString("alipay.antsports.club.trade.queryClubRoom", args);
    }

    public static String collectBubble(String bubbleId) {
        return ApplicationHook.requestString("alipay.antsports.club.home.collectBubble",
                "[{\"bubbleId\":\"" + bubbleId + "\",\"chInfo\":\"healthstep\"}]");
    }

    public static String queryTrainItem() {
        return ApplicationHook.requestString("alipay.antsports.club.train.queryTrainItem",
                "[{\"chInfo\":\"healthstep\"}]");
    }

    public static String trainMember(String itemType, String memberId, String originBossId) {
        return ApplicationHook.requestString("alipay.antsports.club.train.trainMember",
                "[{\"chInfo\":\"healthstep\",\"itemType\":\"" + itemType + "\",\"memberId\":\"" + memberId + "\",\"originBossId\":\"" + originBossId + "\"}]");
    }

    public static String queryMemberPriceRanking() {
        String args = "[{\"buyMember\":true,\"chInfo\":\"healthstep\"}]";
        return ApplicationHook.requestString("alipay.antsports.club.ranking.queryMemberPriceRanking", args);
    }

    public static String queryClubMember(String memberId, String originBossId) {
        return ApplicationHook.requestString("alipay.antsports.club.trade.queryClubMember",
                "[{\"chInfo\":\"healthstep\",\"memberId\":\"" + memberId + "\",\"originBossId\":\"" + originBossId + "\"}]");
    }

    public static String buyMember(String currentBossId, String memberId, String originBossId, JSONObject priceInfo, String roomId) {
        String requestData = "[{\"chInfo\":\"healthstep\",\"currentBossId\":\"" + currentBossId + "\",\"memberId\":\"" + memberId + "\",\"originBossId\":\"" + originBossId + "\",\"priceInfo\":" + priceInfo + ",\"roomId\":\"" + roomId + "\"}]";
        return ApplicationHook.requestString("alipay.antsports.club.trade.buyMember", requestData);
    }

    // 运动币兑好礼
    public static String queryItemDetail(String itemId) {
        String arg = "[{\"itemId\":\"" + itemId + "\"}]";
        return ApplicationHook.requestString("com.alipay.sportshealth.biz.rpc.SportsHealthItemCenterRpc.queryItemDetail", arg);
    }

    public static String exchangeItem(String itemId, int coinAmount) {
        String arg = "[{\"coinAmount\":" + coinAmount + ",\"itemId\":\"" + itemId + "\"}]";
        return ApplicationHook.requestString("com.alipay.sportshealth.biz.rpc.SportsHealthItemCenterRpc.exchangeItem", arg);
    }

    public static String queryExchangeRecordPage(String exchangeRecordId) {
        String arg = "[{\"exchangeRecordId\":\"" + exchangeRecordId + "\"}]";
        return ApplicationHook.requestString("com.alipay.sportshealth.biz.rpc.SportsHealthItemCenterRpc.queryExchangeRecordPage", arg);
    }
}