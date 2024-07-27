package io.github.lazyimmortal.sesame.model.normal.answerAI;

import io.github.lazyimmortal.sesame.data.Model;
import io.github.lazyimmortal.sesame.data.ModelFields;
import io.github.lazyimmortal.sesame.data.ModelGroup;
import io.github.lazyimmortal.sesame.data.modelFieldExt.ChoiceModelField;
import io.github.lazyimmortal.sesame.data.modelFieldExt.StringModelField;
import io.github.lazyimmortal.sesame.data.modelFieldExt.TextModelField;
import io.github.lazyimmortal.sesame.util.Log;

import java.util.List;

public class AnswerAI extends Model {

    private static final String TAG = AnswerAI.class.getSimpleName();

    private static Boolean enable = false;

    @Override
    public String getName() {
        return "AI答题";
    }

    @Override
    public ModelGroup getGroup() {
        return ModelGroup.OTHER;
    }

    private static AnswerAIInterface answerAIInterface;

    private final ChoiceModelField aiType = new ChoiceModelField("useGeminiAI", "AI类型", AIType.TONGYI, AIType.nickNames);

    private final TextModelField.UrlTextModelField getTongyiAIToken = new TextModelField.UrlTextModelField("getTongyiAIToken", "通义千问 | 获取令牌", "https://help.aliyun.com/zh/dashscope/developer-reference/acquisition-and-configuration-of-api-key");

    private final StringModelField setTongyiAIToken = new StringModelField("setTongyiAIToken", "通义千问 | 设置令牌", "");

    private final StringModelField setGeminiAIToken = new StringModelField("useGeminiAIToken", "GeminiAI | 设置令牌", "");

    @Override
    public ModelFields getFields() {
        ModelFields modelFields = new ModelFields();
        modelFields.addField(aiType);
        modelFields.addField(getTongyiAIToken);
        modelFields.addField(setTongyiAIToken);
        modelFields.addField(setGeminiAIToken);
        return modelFields;
    }

    @Override
    public void boot(ClassLoader classLoader) {
        enable = getEnableField().getValue();
        switch (aiType.getValue()) {
            case AIType.TONGYI:
                answerAIInterface = new TongyiAI(setTongyiAIToken.getValue());
                break;
            case AIType.GEMINI:
                answerAIInterface = new GeminiAI(setGeminiAIToken.getValue());
                break;
            default:
                answerAIInterface = AnswerAIInterface.getInstance();
                break;
        }
    }

    /**
     * 获取AI回答结果
     *
     * @param text 问题内容
     * @return AI回答结果
     */
    public static String getAnswer(String text) {
        try {
            if (enable) {
                Log.record("AI🧠答题，问题：[" + text + "]");
                return answerAIInterface.getAnswerStr(text);
            } else {
                Log.record("开始答题，问题：[" + text + "]");
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return "";
    }

    /**
     * 获取答案
     *
     * @param text     问题
     * @param answerList 答案集合
     * @return 空没有获取到
     */
    public static String getAnswer(String text, List<String> answerList) {
        try {
            if (enable) {
                Log.record("AI🧠答题，题目：[" + text + "]选项：" + answerList);
                Integer answer = answerAIInterface.getAnswer(text, answerList);
                if (answer != null && answer >= 0 && answer < answerList.size()) {
                    String answerStr = answerList.get(answer);
                    Log.record("AI🧠回答：" + answerStr);
                    return answerStr;
                }
            } else {
                Log.record("普通答题，题目：[" + text + "]选项：" + answerList);
                if (!answerList.isEmpty()) {
                    String answerStr = answerList.get(0);
                    Log.record("普通回答：" + answerStr);
                    return answerStr;
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return "";
    }

    public interface AIType {

        int TONGYI = 0;
        int GEMINI = 1;

        String[] nickNames = {"通义千问", "GEMINI"};
    }

}