package com.example.gooddaughter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private String host = "tcp://120.77.92.95:1883";
    private String userName = "admin";
    private String passWord = "public";
    private String mqtt_id = "1363035157_APP";
    //private String mqtt_id = "马婷_APP";
    //private String mqtt_sub_topic = "1363035157";
    private String mqtt_sub_topic = "mt";
    private String mqtt_pub_topic = "1363035157_ESP";
    private String T_val;
    private int led_flag = 1;
    private Button bt_1; //定义开关变量
    private ImageView iv_1,iv_3,iv_4,iv_5,iv_6;
    private ImageView iv_2;
    private TextView tv_7;
    private MqttClient client;
    private MqttConnectOptions options;
    private Handler handler;
    private ScheduledExecutorService scheduler;

    @SuppressLint("HandlerLeak")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_1 = findViewById(R.id.bt_1);//将开关变量与实际控件操作捆绑
        bt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Hello World!");//调试输出HELLO
                Toast.makeText(MainActivity.this,"Hello World!",Toast.LENGTH_SHORT).show();//在当前Activity,显示弹窗Hello World!  Toast弹窗
            }
        });

        iv_1 = findViewById(R.id.iv_1);
        iv_2 = findViewById(R.id.iv_2);
        iv_3 = findViewById(R.id.iv_3);
        iv_4 = findViewById(R.id.iv_4);
        iv_5 = findViewById(R.id.iv_5);
        iv_6 = findViewById(R.id.iv_6);
        iv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("马婷（小考拉）是赵展文（大考拉）的好老婆！！！");
                Toast.makeText(MainActivity.this,"马婷（小考拉）是赵展文（大考拉）的好老婆！！！",Toast.LENGTH_SHORT).show();
//                publishmessageplus(mqtt_pub_topic,"Open Led");
            }
        });
        iv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("马婷（小考拉）是赵展文（大考拉）这辈子唯一认定的人！！！");
                Toast.makeText(MainActivity.this,"马婷（小考拉）是赵展文（大考拉）这辈子唯一认定的人！！！（唯一）",Toast.LENGTH_SHORT).show();
            }
        });
        iv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("马婷（小考拉）是赵展文（大考拉）的”炮弹娃闺女！！！“");
                Toast.makeText(MainActivity.this,"马婷（小考拉）是赵展文的”炮弹娃闺女！！！“！",Toast.LENGTH_SHORT).show();
            }
        });
        iv_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("马婷（小考拉）是赵展文（大考拉）的”蠢蛋闺女！！！“");
                Toast.makeText(MainActivity.this,"马婷（小考拉）是赵展文（大考拉）的”蠢蛋闺女！！！“",Toast.LENGTH_SHORT).show();
            }
        });
        iv_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("马婷（小考拉）是赵展文（大考拉）的”尕欢蛋闺女！！！“");
                Toast.makeText(MainActivity.this,"马婷（小考拉）是赵展文（大考拉）的”尕欢蛋闺女！！！“",Toast.LENGTH_SHORT).show();
            }
        });
        iv_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("赵展文（大考拉）是马婷（小考拉）的终生好老公、好爸爸、好男友！！！");
                Toast.makeText(MainActivity.this,"赵展文（大考拉）是马婷（小考拉）的终生好老公、好爸爸、好男友！！！",Toast.LENGTH_SHORT).show();
            }
        });
        /*iv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            /*public void onClick(View view) {
                if(led_flag == 0){
                    System.out.println("\"set_led\":1");
                    Toast.makeText(MainActivity.this,"\"set_led\":1",Toast.LENGTH_SHORT).show();
                    publishmessageplus(mqtt_pub_topic,"{\"set_led\":1}");
                    led_flag = 1;
                }
                else {
                    System.out.println("\"set_led\":0");
                    Toast.makeText(MainActivity.this,"\"set_led\":0",Toast.LENGTH_SHORT).show();
                    publishmessageplus(mqtt_pub_topic,"{\"set_led\":0}");
                    led_flag = 0;
                }
            }
        });*/
        tv_7 = findViewById(R.id.tv_7);
//*************************Mqtt***************************************//
        Mqtt_init();
        startReconnect();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1 ://开机校验回传
                        break;
                    case 2://反馈回传
                        break;
                    case 3://MQTT收到消息回传
                        tv_7.setText(msg.obj.toString());
                        System.out.println(msg.obj.toString());
                        //T_val = msg.toString().substring(msg.obj.toString().indexOf("Temperature\":")+13,msg.obj.toString().indexOf("}"));
                        //String text_val = "温度：" + T_val + "℃";
                        //tv_7.setText(text_val);
                        //Toast.makeText(MainActivity.this,T_val, Toast.LENGTH_SHORT).show();
                        break;
                    case 30://连接失败
                        System.out.println("连接失败");
                        Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_LONG).show();
                        break;
                    case 31://连接成功
                        System.out.println("连接成功");
                        Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_LONG).show();
                        try {
                            client.subscribe(mqtt_sub_topic,1);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
//                if (msg.what == 1) {
//                    Toast.makeText(MainActivity.this, (String) msg.obj,
//                            Toast.LENGTH_SHORT).show();
//                } else if (msg.what == 2) {
//                    System.out.println("连接成功");
//                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
//                    try {
////                        client.subscribe(myTopic, 1);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else if (msg.what == 3) {
//                    Toast.makeText(MainActivity.this, "连接失败，系统正在重连", Toast.LENGTH_SHORT).show();
//                    System.out.println("连接失败，系统正在重连");
//                }
            }


        };

    }
    
    private void Mqtt_init() {
        try {
            //host为主机名，mqtt_id为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, mqtt_id,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }
                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    String str = new String(message.getPayload(),"GB2312");
                    msg.what = 3;
                    //msg.obj = topicName + "---" + message.toString();
                    msg.obj = str;
                    handler.sendMessage(msg);
                }
                /*@Override
                //订阅多个主题时的操作
                public void messageArrived(String topic, MqttMessage message)throws Exception {
                    if (topic.equals(topic1) {
                        temp.setText(new String(message.getPayload()));
                    }
                    else
                    if(topic.equals(topic2) {
                        foo.setText(new String(message.getPayload()));
                    }
                }*/
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Mqtt_connect() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    client.connect(options);
                    Message msg = new Message();
                    msg.what = 31;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 30;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    
    private void startReconnect(){
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable(){
            @Override
            public void run(){
                if (!client.isConnected()){
                    Mqtt_connect();    
                }
            }
        }, 0*1000,10*1000,TimeUnit.MILLISECONDS);
    }

    private void publishmessageplus(String topic,String message2){
        if(client == null || !client.isConnected())
        {
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(message2.getBytes());
        try{
            client.publish(topic,message);
        } catch (MqttException e){
            e.printStackTrace();
        }
    }
    
}