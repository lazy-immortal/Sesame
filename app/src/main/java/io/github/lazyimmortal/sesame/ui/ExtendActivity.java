package io.github.lazyimmortal.sesame.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.Uri;
import android.os.Build;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.github.lazyimmortal.sesame.hook.ApplicationHook;
import io.github.lazyimmortal.sesame.R;
import io.github.lazyimmortal.sesame.util.*;

public class ExtendActivity extends BaseActivity {

    Button btnSendRpc;
    EditText txtData;
    EditText txtMethod;
    Button view_log;
    Button btnGetTreeItems;
    Button btnGetNewTreeItems;
    private Uri uri;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_extend);
        txtData = findViewById(R.id.data);
        txtMethod = findViewById(R.id.method);
        btnSendRpc = findViewById(R.id.send_rpc);
        view_log = findViewById(R.id.view_log);
        btnGetTreeItems = findViewById(R.id.get_tree_items);
        btnGetNewTreeItems = findViewById(R.id.get_newTree_items);

        setBaseTitle("扩展功能");

        btnGetTreeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                sendItemsBroadcast("getTreeItems");
                Toast.makeText(ExtendActivity.this, "已发送查询请求，请在森林日志查看结果！", Toast.LENGTH_SHORT).show();
            }
        });

        btnGetNewTreeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                sendItemsBroadcast("getNewTreeItems");
                Toast.makeText(ExtendActivity.this, "已发送查询请求，请在森林日志查看结果！", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendRpc.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                sendRpcBroadcast();
                Toast.makeText(ExtendActivity.this, "已发送Rpc请求，请在debug日志查看结果！", Toast.LENGTH_SHORT).show();
            }
        });

        view_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                viewLog();
            }
        });
    }

    private void sendRpcBroadcast() {
        String rpc_data = txtData.getText().toString();
        String rpc_method = txtMethod.getText().toString();
        if (rpc_data.isEmpty() || rpc_method.isEmpty()) {
            return;
        }
        Intent intent = new Intent("com.eg.android.AlipayGphone.sesame.rpctest");
        intent.putExtra("method", rpc_method);
        intent.putExtra("data", rpc_data);
        intent.putExtra("type", "Rpc");
        sendBroadcast(intent);
    }

    private void viewLog() {
        String debugData = "file://" + FileUtil.getDebugLogFile().getAbsolutePath();
        Intent debugIt = new Intent(this, HtmlViewerActivity.class);
        debugIt.setData(Uri.parse(debugData));
        debugIt.putExtra("canClear", true);
        startActivity(debugIt);
    }

    private void sendItemsBroadcast(String type) {
        Intent intent = new Intent("com.eg.android.AlipayGphone.sesame.rpctest");
        intent.putExtra("method", "");
        intent.putExtra("data", "");
        intent.putExtra("type", type);
        sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.uri = getIntent().getData();
    }

}
