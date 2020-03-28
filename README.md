# 五子棋网络对战 java实现

```
	最近期末考试，压力实在是太大了。专业不对口的痛苦，别人永远体会不来。
	闲暇之余，便想着巩固一下java的基础知识，以前看别人写的游戏，心里都觉得哇 好厉害啊，我什么时候才可以写出自己的游戏，当然我最想做的不是游戏，因为我感觉我只有欣赏美的能力，却没有制造美的能力。好了，废话这么多，哎，我就是喜欢啰嗦。
```



### <font color=red>游戏消息传输构思：</font>

​	<font color=#0099ff face="楷体" bgcolor=#FF4500>首先呢，写一个程序，最重要的就是构思啦，那么首先想到的是网络实现的方式，在这里我们可以用http协议去传输我们的数据，也可以采用封装的TCP/cp的scctet去实现我们想要的功能，在网络这个世界，一切皆socket,包括你数值的http协议，底层传输数据也是socket连接传输的，学过java的都知道Tomcat这个web服务器吧，那么这就好了，Tomcat给客户端传输数据也是socket连接的，你也可以通过socket实现自己的web服务器了，好了，传输协议确定了，那么接下来就要考虑游戏构思了</font>

### <font color=red>游戏构思：</font>

​	<font color=#0099ff face="楷体" bgcolor=#FF4500>既然要写网络游戏嘛，那么肯定要有一个客户端，一个服务端对吧。这是最典型的C/S(Client/Server)，对于WEB而言的话，那么是B/S(Browser/Server).那么我们就选择一个最简单的C/S模型吧。</font>

#### <font color=#7FFFD4>1. Server实现大致思路</font>

​	<font color=#0099ff face="楷体" bgcolor=#FF4500>然后呢，服务端接受客户端的连接，然后保存每一个客户端连接的socket对象，每连接一个客户端，则开启一个线程去读当前客户端消息的线程，然后定义我们游戏的消息头，消息头很重要，能够微量防止不法连接。然后就是对消息体的一些处理逻辑了。到这里先不要考虑连接数，实际上，这么写是最笨的做法，想想，万一有一万个连接，难道就要开一万个线程去读每个socket客户端的消息吗？对于很大的连接数，这里就不要考虑了，我们只追求基本功能能够实现即可，刚开始不要考虑那么多。你只要知道这是一个最简单的游戏的制作过程</font>

<font color=#0000FF>Server  implements ：（伪代码）</font>

```java
class Server {
    static Map<Socket,String> Clients = new HashMap<>(Socket,String);
    ServerSocket serverSocket;
    main(){
        //监听本机6666端口
        serverSocket = new ServerSocket(6666)
        Client =  serverSocket.accpet();//服务端开启监听
        new HandleClient(Client).start();//启动当前客户端监听
        //连接上则添加到map列表，名字默认为空
        Clients.put(Client,"");
       
    }
}
class HandleClient extends Thread{
    Socket Client;
    BufferedReader br;//当前Socket的Reader对象
    BufferedWriter bw;//当前Socket的Wirter对象
    BufferedWriter otherbw //另一个玩家的writer对象
    HandleClient(Socket Client){
        this.Client = Client;
	}
    public void run(){
        bw = new BufferedWriter(new OutputStreamWriter（Client.getoutputStream()）;
        br= new BufferedReader(new OutputStreamReader（Client.getinputStream()）;  
                               while(true){
                                  String msg =  bf.readLine();
                                   String msgStr[] = msg.split(":");
                                   //定义消息头，消息如果满足消息头
                                   if(msgStr[0].equals("MSGTYPE")){
                                       //。。。。消息解析处理过程
  //对消息体用“#” 切割 拿到消息的Key,如果当前消息是发来自己的名字  ，
                                      // 那么 就把这个连接添加到Server
                                       if(msgStr[1].splint("#")[0].equals("MYNAME")){
                            //更新改Socket玩家的姓名
                                           Server.Clients.put(Client,msgStr[1].splint("#")[1]);   
                                           //接下来 的代码就不写了 直接看我的工程即可，服务端游戏思路大致就是这样。就是这样切割消息体来进入相应的处理
                                       }
                                   }
                               }
	}
}
```

#### <font color = #7FFF00>思考：</font>

​	<font color=#0099ff face="楷体" bgcolor=#FF4500>这里为什么HandleClient类里我还写了一个  《BufferedWriter otherbw //另一个玩家的writer对象》 这句代码，你可能有所疑惑，为什么都有了一个bw，还要一个otherbw,但是你忘了吗，我们要写的五子棋是双人对战的，这里 这个服务器的当前线程，当有人加入到房间，或者是自己创建的房间，有人加入进来，拿Server的静态对象Clients找到加入房间玩家的Socket，接着拿到输出流，不就可以直接给他发消息了吗？这里要好好想想了啊，这里是重点</font>

#### <font color=#7FFFD4>1. Client实现大致思路</font>

​	<font color=#0099ff face="楷体" bgcolor=#FF4500>然后，客户端实现过程当眼也很简单了啊，直接建立一个Socket对象去连接服务端，然后发送相应的消息体，服务端响应后，返回给客户端对应的消息。这里我给大家举一个完整的消息例子。 </font>

<font color=#0099ff face="楷体" bgcolor=#FF4500>Client--------------->Server （连接成功）</font>

<font color=#0099ff face="楷体" bgcolor=#FF4500>BEGIN:</font>

<font color=#0099ff face="楷体" bgcolor=#FF4500>发送MyName--------->Server （保存到Server的Clients对象里）</font>

<font color=#0099ff face="楷体" bgcolor=#FF4500>发送创建房间---------->Server  (向服务器发送一条创建房间的消息，服务器记录当前玩家创建了房间，然后调用bw.write("MSGTYPE:CreateRoomSuccess\r\n"))通知当前玩家建立房间成功</font>

<font color=#0099ff face="楷体" bgcolor=#FF4500>Gameing:</font>

<font color=#0099ff face="楷体" bgcolor=#FF4500>发送落子坐标---------->Server(接受到当前玩家的落子信息，转发给另一个 玩家，怎么转发呢？前面不是直接拿到了otherbw,那么这时候直接调用加入玩家的bw即可向对方发消息。)</font>

<font color=#0000FF>Client implements ：（伪代码）</font>

```java
class Client{
  
    main(){
        
        new Room();
    }
}
class Room extends Jframe{
    static String MyName; //自己的名字
    static Socket socket;
    BufferedReader br;//当前Socket的Reader对象
    BufferedWriter bw;//当前Socket的Wirter对象
    BufferedWriter otherbw //另一个玩家的writer对象
    Jframe(){
       
        //MouseLister 实现监听鼠标点击事件
        addMouseListener(new MouseListener{
                // 如果点击到某个坐标是开始游戏
                if(x>=xxx&&x<=xxx&&y>=xxx&&y<=xxx){
                	String name = JOptionPane.showInternalInputDialog(null,"请给你取一个个性的名字把！")
                        if(socket==null){
                            //这里的localhost默认是本机的地址 
                            // 如果你的Hosts文件没有定义localhost，那么改成							127.0.0.1
                            socket =  new Socket("localhost",6666);
							 bw = new BufferedWriter(new OutputStreamWriter（Client.getoutputStream()）;
        br= new BufferedReader(new OutputStreamReader（Client.getinputStream()）;  
                        }
                    		//进入游戏窗口
                           new PlayGmae();
                            //开启线程读取消息 ，为什么要开启线程读取消息？因为
                            //readLine（）方法是阻塞的，要是不开线程，那么主线									程就会卡了。
                            new ReadMsg().start();
                       
                }
        })
    }
}
     //读取消息
class ReadMsg extends Thread(){
    public void run(){
        while(true){
           String msg =  br.readLine();
            String msgStr = msg.splint(":");
            if(msgStr[0].equals("MSGTYPE")){
                ....//相关处理过程
        }
    }
}
     // 实现相关界面
    class PlayGame extends Jframe{
        PlayGame(){
           //实现相关界面 
             addMouseListener(
                 new MouseListener{
                     //增加点击 区域 消息监听 比如不同区域 有不同的功能啊
                     // 开始游戏 棋盘 认输 悔棋等等 都有自己的坐标信息啊。点击执行相关的功能
                     
                     // eg ：当前区域 ：认输
                     if(x>=xxx&&x<=xxx&&y>=xxx&&y<=xxx){
                       int staus=   JOptionPane.showInternalConfirmDialog(this, 
"确认要人数吗", ）;
                                                                          if(staus==0){
                                                                              bw.write("MSGTYPE:GAMEING#renshu")；
                                                                          }
                 }
             )；
        }
    }
```





### <font color=red>游戏截图：</font>

<font color=#7FFFD4>1. Server</font>

<font color=#0099ff face="楷体" bgcolor=#FF4500>Server我没有写界面直接启动就好了，不过要想观察后台信息，在控制台输入 "java -jar Server.jar"即可</font>

<font color=#7FFFD4>2.Client</font>

### <font color=red>总结：</font>

<font color=#0099ff face="楷体" bgcolor=#FF4500>好了，这就是游戏实现的大致思路，感觉就是这么一步一步来的，这个游戏断断续续，都怪考试太多了，哎，我写了两周才完成了所有功能...，一边复习，一边考试。其实大多时间都再调试BUG，要么是点击出问题，要么是创建房间加入出问题...各种问题，当然，要想实现自己想要的游戏，努力一下还是挺好的。到现在为止，游戏可以创建多个房间，如果游戏厅房间太多则多页显示，可以自由加入任何创建的房间</font>

<font color=#7FFFD4>关于待补充的：</font>

  <font color=#0099ff face="楷体" bgcolor=#FF4500>游戏还有 很多要完善的地方，比如 房间观战，送♥动画特效，人机对战，哎，有时间我再完善，没时间，这个小项目就到这里，新的2019，要继续努力，送给每一个看到此篇文章的人，愿你们都能开心过好每一天。</font>