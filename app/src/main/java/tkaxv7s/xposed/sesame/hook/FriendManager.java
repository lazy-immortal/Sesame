package tkaxv7s.xposed.sesame.hook;

import de.robv.android.xposed.XposedHelpers;
import org.json.JSONException;
import org.json.JSONObject;
import tkaxv7s.xposed.sesame.entity.UserEntity;
import tkaxv7s.xposed.sesame.util.FileUtil;
import tkaxv7s.xposed.sesame.util.Log;
import tkaxv7s.xposed.sesame.util.TimeUtil;
import tkaxv7s.xposed.sesame.util.UserIdMap;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class FriendManager {
    private static final String TAG = FriendManager.class.getSimpleName();

    public static void fillUser() {
        new Thread() {

            @Override
            public void run() {
                ClassLoader loader;
                try {
                    loader = ApplicationHook.getClassLoader();
                } catch (Exception e) {
                    Log.i(TAG, "Error getting classloader");
                    return;
                }
                try {
                    UserIdMap.clear();
                    String selfId = ApplicationHook.getUserId();
                    Class<?> clsUserIndependentCache = loader.loadClass("com.alipay.mobile.socialcommonsdk.bizdata.UserIndependentCache");
                    Class<?> clsAliAccountDaoOp = loader.loadClass("com.alipay.mobile.socialcommonsdk.bizdata.contact.data.AliAccountDaoOp");
                    Object aliAccountDaoOp = XposedHelpers.callStaticMethod(clsUserIndependentCache, "getCacheObj", clsAliAccountDaoOp);
                    List<?> allFriends = (List<?>) XposedHelpers.callMethod(aliAccountDaoOp, "getAllFriends", new Object[0]);
                    UserEntity selfEntity = null;
                    for (Object userObject : allFriends) {
                        try {
                            Class<?> friendClass = userObject.getClass();
                            String userId = (String) XposedHelpers.findField(friendClass, "userId").get(userObject);
                            String account = (String) XposedHelpers.findField(friendClass, "account").get(userObject);
                            String name = (String) XposedHelpers.findField(friendClass, "name").get(userObject);
                            String nickName = (String) XposedHelpers.findField(friendClass, "nickName").get(userObject);
                            String remarkName = (String) XposedHelpers.findField(friendClass, "remarkName").get(userObject);
                            boolean isSelf = Objects.equals(selfId, userId);
                            UserEntity userEntity = new UserEntity(userId, account, name, nickName, remarkName, isSelf);
                            if (isSelf) {
                                selfEntity = userEntity;
                            }
                            UserIdMap.addUser(userEntity);
                        } catch (Throwable t) {
                            Log.i(TAG, "addUserObject err:");
                            Log.printStackTrace(TAG, t);
                        }
                    }
                    UserIdMap.saveSelf(selfEntity);
                    UserIdMap.save(selfId);
                } catch (Throwable t) {
                    Log.i(TAG, "checkUnknownId.run err:");
                    Log.printStackTrace(TAG, t);
                }
            }
        }.start();
    }

    public static UserEntity addUserObject(Object userObject, Boolean isMaskAccount) {
        return null;
    }

    public static boolean needUpdateAll(long last) {
        if (last == 0L) {
            return true;
        }
        Calendar cLast = Calendar.getInstance();
        cLast.setTimeInMillis(last);
        Calendar cNow = Calendar.getInstance();
        if (cLast.get(Calendar.DAY_OF_YEAR) == cNow.get(Calendar.DAY_OF_YEAR)) {
            return false;
        }
        return cNow.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
    }

    private static JSONObject joFriendWatch;

    public static void friendWatch(String id, int collectedEnergy) {
        if (id.equals(UserIdMap.getCurrentUid())) {
            return;
        }
        try {
            if (joFriendWatch == null) {
                String strFriendWatch = FileUtil.readFromFile(FileUtil.getFriendWatchFile());
                if (!"".equals(strFriendWatch)) {
                    joFriendWatch = new JSONObject(strFriendWatch);
                } else {
                    joFriendWatch = new JSONObject();
                }
            }
            if (needUpdateAll(FileUtil.getFriendWatchFile().lastModified())) {
                friendWatchNewWeek();
            }
            friendWatchSingle(id, collectedEnergy);
        } catch (Throwable th) {
            Log.i(TAG, "friendWatch err:");
            Log.printStackTrace(TAG, th);
        }
    }

    private static void friendWatchSingle(String id, int collectedEnergy) throws JSONException {
        JSONObject joSingle = joFriendWatch.optJSONObject(id);
        if (joSingle == null) {
            joSingle = new JSONObject();
            joSingle.put("name", UserIdMap.getMaskName(id));
            joSingle.put("allGet", 0);
            joSingle.put("startTime", TimeUtil.getDateStr());
            joFriendWatch.put(id, joSingle);
        }
        joSingle.put("weekGet", joSingle.optInt("weekGet", 0) + collectedEnergy);
        FileUtil.write2File(joFriendWatch.toString(), FileUtil.getFriendWatchFile());
    }

    private static void friendWatchNewWeek() {
        JSONObject joSingle;
        try {
            String dateStr = TimeUtil.getDateStr();
            for (String id : UserIdMap.getUserIdSet()) {
                if (joFriendWatch.has(id)) {
                    joSingle = joFriendWatch.getJSONObject(id);
                } else {
                    joSingle = new JSONObject();
                }
                joSingle.put("name", UserIdMap.getMaskName(id));
                joSingle.put("allGet", joSingle.optInt("allGet", 0) + joSingle.optInt("weekGet", 0));
                joSingle.put("weekGet", 0);
                if (!joSingle.has("startTime")) {
                    joSingle.put("startTime", dateStr);
                }
                joFriendWatch.put(id, joSingle);
            }
            FileUtil.write2File(joFriendWatch.toString(), FileUtil.getFriendWatchFile());
        } catch (Throwable th) {
            Log.i(TAG, "friendWatchNewWeek err:");
            Log.printStackTrace(TAG, th);
        }
    }
}
