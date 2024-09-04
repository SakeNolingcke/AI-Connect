package com.example.aicommunication.tool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.example.aicommunication.DialogueList;
import com.example.aicommunication.Msg;
import com.example.aicommunication.User;
import com.example.aicommunication.fragment.DialogueFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DialogueManager {
    private static final int MAX_TOKENS = 1024;
    String TGA = "DialogueManager";
    private final Context context;
    private DialogueFragment dia;
    private final String file_name;
    private AIApiConnect ai;
    private final List<String> user_id = new ArrayList<>();
    private final int[] tokens = new int[10];
    private JSONObject js = null;
    transient FileOutputStream out;
    transient FileInputStream in;
    public boolean isNewFile;
    public int position, count;

    /**
     * 管理会话的工具类的构造函数
     *
     * @param dia 当前DialogueFragment对象
     * @param ai  用于连接AI的工具类的实例
     */
    public DialogueManager(DialogueFragment dia, AIApiConnect ai) {
        this.context = dia.getContext();
        this.ai = ai;
        this.dia = dia;
            if(dia.file_name.isEmpty())
                this.file_name = "dialogues.json";
            else {
                if (dia.file_name.contains(".json"))
                    this.file_name = dia.file_name;
                else
                    this.file_name = dia.file_name + ".json";
            }
        initDialogue();
    }

    public DialogueManager(Context context, String file_name) {
        this.context = context;
        if(file_name.isEmpty())
            this.file_name = "dialogues.json";
        else {
            if (file_name.contains(".json"))
                this.file_name = file_name;
            else
                this.file_name = file_name + ".json";
        }
        initDialogue();
    }

    private void openFileStream() {
        try {
            out = context.openFileOutput(file_name, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            Log.e(TGA, "找不到该Json文件：" + file_name);
        }
    }

    private void closeFileStream() {
        try {
            out.close();
        } catch (IOException e) {
            Log.e(TGA, "关闭该Json文件失败：" + file_name);
        }
    }

    /**
     * 以该类中的JSON数据与AI连接
     */
    public void connectAi() {
        try {
            JSONObject js_obj = js.getJSONArray("dialogues").getJSONObject(position);
            ai.startAIApiConnect(js_obj);
        } catch (JSONException e) {
            Log.e(TGA, "找不到指定对话！");
        }
    }

    private void initDialogue() {
        getMsgJson();
        if (isNewFile) {
            js = new JSONObject();
            user_id.add("新建会话1");
            position = 0;
            try {
                js.put("position", position);
                JSONArray jsa_t = new JSONArray();
                for (int i = 0; i < tokens.length; i++) {
                    jsa_t.put(i, 1);
                }
                js.put("tokens", jsa_t);
                JSONArray jsA_0 = new JSONArray();
                newDialogueJsonObject(jsA_0);
                js.put("dialogues", jsA_0);
                Log.i(TGA, "Json数据写入成功：\n" + js.toString());
                rewriteJsonFile();
            } catch (JSONException e) {
                Log.e(TGA, "Json数据写入失败！");
            }
        }
        count = user_id.size();
    }

    private void newDialogueJsonObject(JSONArray jsA_0) throws JSONException {
        JSONObject jsO_0 = new JSONObject();
        jsO_0.put("user_id", user_id.get(position));
        JSONArray js_msg = new JSONArray();
        JSONObject js_msg_0 = new JSONObject();
        js_msg_0.put("role", "user");
        js_msg_0.put("content", "你好");
        js_msg.put(js_msg_0);
        jsO_0.put("messages", js_msg);
        jsO_0.put("temperature", 0.95);
        jsO_0.put("top_p", 0.8);
        jsO_0.put("penalty_score", 1);
        jsA_0.put(position, jsO_0);
    }

    /**
     * 新建一个会话
     */
    public boolean newDialogue() {
        if (user_id.size() >= tokens.length) {
            showDialogueExceeded();
            return false;
        }
        user_id.add(user_id.size(), "新建会话" + (++count));
        position = user_id.size() - 1;
        try {
            js.put("position", position);
            JSONArray jsa_dl = js.getJSONArray("dialogues");
            newDialogueJsonObject(jsa_dl);
            js.put("dialogues", jsa_dl);
            rewriteJsonFile();
        } catch (JSONException e) {
            Log.e(TGA, "新建会话失败！");
            return false;
        }
        return true;
    }

    private void getMsgJson() {
        InputStreamReader reader = null;
        BufferedReader br = null;
        try {
            in = context.openFileInput(file_name);
            reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            br = new BufferedReader(reader);
            StringBuilder result = new StringBuilder();
            String temp;
            while ((temp = br.readLine()) != null) {
                result.append(temp);
            }
            js = new JSONObject(result.toString());
            position = js.getInt("position");
            JSONArray jsa_t = js.getJSONArray("tokens");
            for (int i = 0; i < tokens.length; i++) {
                tokens[i] = jsa_t.getInt(i);
            }
            JSONArray jsa_dl = js.getJSONArray("dialogues");
            for (int i = 0; i < jsa_dl.length(); i++) {
                user_id.add(i, jsa_dl.getJSONObject(i).getString("user_id"));
            }
            if (position < 0 || position >= jsa_dl.length()) {
                position = jsa_dl.length() - 1;
                js.put("position", position);
                rewriteJsonFile();
            }
            isNewFile = position < 0 || jsa_dl.length() <= 0;
        } catch (IOException | JSONException e) {
            isNewFile = true;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                Log.e(TGA, "Json文件读取数据流关闭失败！");
            }
        }
        if (isNewFile) {
            Log.e(TGA, "Json文件读取失败！");
        } else {
            Log.i(TGA, "Json文件读取成功：\n" + js.toString());
        }
    }

    /**
     * 将Msg列表写入本管理类的Json数据中
     *
     * @param msg List<Msg>
     */
    public void putJsonMsg(List<Msg> msg) {
        openFileStream();
        try {
            JSONArray jsa_dl = js.getJSONArray("dialogues");
            JSONObject jso_dl = jsa_dl.getJSONObject(position);
            JSONArray jsa_msg = jso_dl.getJSONArray("messages");
            JSONObject jso_msg;
            for (int i = 0; i < msg.size(); i++) {
                jso_msg = new JSONObject();
                if (msg.get(i).getOwner() == Msg.OWNER_AI) {
                    jso_msg.put("role", "assistant");
                } else if (msg.get(i).getOwner() == Msg.OWNER_USER) {
                    jso_msg.put("role", "user");
                }
                jso_msg.put("content", msg.get(i).getContent());
                jsa_msg.put(i, jso_msg);
            }
            jso_dl.put("messages", jsa_msg);
            jsa_dl.put(position, jso_dl);
            js.put("dialogues", jsa_dl);
            Log.i(TGA, "Msg数据写入成功！:\n" + js.toString());
        } catch (JSONException e) {
            Log.e(TGA, "Msg数据写入失败！");
        }
        closeFileStream();
    }

    /**
     * 将该管理类中Json数据转换为Msg列表并返回
     *
     * @return List<Msg>
     */
    public List<Msg> putListMsg() {
        List<Msg> msg = new ArrayList<>();
        try {
            JSONArray jsa_dl = js.getJSONArray("dialogues");
            JSONObject jso_dl = jsa_dl.getJSONObject(position);
            JSONArray jsa_msg = jso_dl.getJSONArray("messages");
            JSONObject jso_msg;
            for (int i = 0; i < jsa_msg.length(); i++) {
                jso_msg = jsa_msg.getJSONObject(i);
                String role = jso_msg.getString("role");
                String content = jso_msg.getString("content");
                if (role.equals("user")) {
                    msg.add(new Msg(content, Msg.OWNER_USER));
                } else if (role.equals("assistant")) {
                    msg.add(new Msg(content, Msg.OWNER_AI));
                }
            }
            Log.i(TGA, "Msg数据返回成功,msg.size()=" + msg.size());
        } catch (JSONException e) {
            Log.e(TGA, "Msg数据返回失败！");
        }
        return msg;
    }

    public void rewriteJsonFile() {
        openFileStream();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            bw.write(js.toString());
            bw.flush();
            Log.i(TGA, "Json文件保存成功！");
        } catch (IOException e) {
            Log.e(TGA, "Json文件写入失败！");
        }
        closeFileStream();
    }

    public String getUser_id() {
        return user_id.get(position);
    }

    public int getTokens() {
        int sum_tokens = 0;
        for (int token : tokens) {
            if (token != 1)
                sum_tokens += token;
        }
        return sum_tokens;
    }

    public void setUser_id(String string) {
        user_id.set(position, string);
        try {
            JSONArray dia_jsa = js.getJSONArray("dialogues");
            dia_jsa.getJSONObject(position)
                    .put("user_id", user_id.get(position));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteDialogue(int del_position) {
        try {
            JSONArray dia_jsa = js.getJSONArray("dialogues");
            if (del_position >= 0 && del_position < user_id.size()) {
                dia_jsa.remove(del_position);
            } else {
                dia_jsa = new JSONArray();
            }
            js.put("dialogues", dia_jsa);
            if (position >= dia_jsa.length()) {
                js.put("position", dia_jsa.length());
            }
        } catch (JSONException e) {
            Log.e(TGA, "删除会话失败！position：" + del_position);
        }
    }

    public void setPosition(int position) {
        try {
            if (position >= 0 && position < user_id.size()) {
                this.position = position;
                js.put("position", position);
            }
        } catch (JSONException e) {
            Log.e(TGA, "position写入文件失败！");
        }
    }

    public List<DialogueList> getDialogueLists() {
        List<DialogueList> lists = new ArrayList<>();
        try {
            JSONArray tokens_jsa = js.getJSONArray("tokens");
            JSONArray dialogue_jsa = js.getJSONArray("dialogues");
            JSONObject dialogue_jso;
            for (int i = 0; i < dialogue_jsa.length(); i++) {
                dialogue_jso = dialogue_jsa.getJSONObject(i);
                String user_id = dialogue_jso.getString("user_id");
                lists.add(new DialogueList(user_id, tokens_jsa.getInt(i)));
            }
        } catch (JSONException e) {
            Log.e(TGA, "获取会话列表失败！");
        }
        return lists.isEmpty() ? null : lists;
    }

    public boolean isTokensExceeded() {
        return tokens[position] >= MAX_TOKENS;
    }

    public void setTokens(int token) {
        try {
            if (token >= 0) {
                tokens[position] = token;
                JSONArray jsa_t = js.getJSONArray("tokens");
                jsa_t.put(position, tokens[position]);
                js.put("position", position);
                rewriteJsonFile();
            }
        } catch (JSONException e) {
            Log.e(TGA, "position写入文件失败！");
        }
    }

    private void showDialogueExceeded() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("会话已经满了，要删除一些吗？");
        builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dia.openHistory();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();//这个方法可以返回一个alertDialog对象
        alertDialog.show();
    }

    public void updateUserDB(String user_id){
        if(user_id.equals("dialogues.json")){
            Log.i(TGA, "游客用户不用写入数据！");
        }else {
            DBDao dbDao = new DBDao(context,new DBHelper(context));
            List<User> users = dbDao.query();
            User user = null;
            for (int i = 0; i < users.size(); i++) {
                if(users.get(i).getUser_id().equals(user_id)){
                    user = new User(
                            users.get(i).getUser_name(),
                            users.get(i).getUser_id(),
                            users.get(i).getPassword(),
                            getTokens()
                    );
                }
            }
            if(user!=null){
                dbDao.delete(user_id);
                dbDao.insert(user);
                Log.i(TGA, "用户数据写入成功！\nuser_id:"+user_id);
            }else
                Log.i(TGA, "数据库没有找到该用户！\nuser_id:"+user_id);
        }
    }

}
