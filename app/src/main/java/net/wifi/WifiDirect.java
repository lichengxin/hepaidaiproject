package net.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.*;
import android.net.wifi.p2p.*;

public class WifiDirect {

	/*CHANGE_WIFI_STATE,ACCESS_WIFI_STATE 和INTERNET
	 * 创建一个广播接收器和对等网络管理器 
	 * 初始化一个 IntentFilter，并让它监听以下动作:
WIFI_P2P_STATE_CHANGED_ACTION
* 表明Wi-Fi对等网络（P2P）是否已经启用
WIFI_P2P_PEERS_CHANGED_ACTION
* 表明可用的对等点的列表发生了改变
WIFI_P2P_CONNECTION_CHANGED_ACTION
* 表示Wi-Fi对等网络的连接状态发生了改变
WIFI_P2P_THIS_DEVICE_CHANGED_ACTION*/
	
	/*
	 * initialize()  把应用程序注册到Wi-Fi框架中，它必须在调用其他Wi-Fi Direct方法之前调用。
       connect()  用指定的配置来启动设备间的对等连接。
       cancelConnect()  取消任何进行中的对等设备间连接请求。
       requestConnectInfo()  请求设备的连接信息。
       createGroup()  用当前设备作为组管理员来创建一个对等组。
       removeGroup()  删除当期对等设备组。
       requestGroupInfo()  请求对等组信息。
       discoverPeers()  启动对等点的发现。
       requestPeers() 请求当前发现的对等点的列表。
      
      WifiP2pManager.ActionListener    connect(), cancelConnect(),
             createGroup(),removeGroup(), and discoverPeers()
      WifiP2pManager.ChannelListener   initialize()
      WifiP2pManager.ConnectionInfoListener  requestConnectInfo()
      WifiP2pManager.GroupInfoListener  requestGroupInfo()
      WifiP2pManager.PeerListListener   requestPeers()
      
      WIFI_P2P_CONNECTION_CHANGED_ACTION   在设备的Wi-Fi连接状态变化时，发出这个广播。
      WIFI_P2P_PEERS_CHANGED_ACTION  在调用discoverPeers()方法时，发出这个广播
                   如果你要在应用程序中处理这个Intent，通常是希望调用requestPeers()方法来获取对等设备的更新列表。
      WIFI_P2P_STATE_CHANGED_ACTION  当启用或禁用设备上的Wi-Fi Direct时，发出这个广播。
      WIFI_P2P_THIS_DEVICE_CHANGED_ACTION   当设备的细节（如设备的名称）发生变化时，发出这个广播。
      */
	
	private static WifiDirect mInstance = null;
	public static WifiDirect getInstance() {
		if (null == mInstance) {
			mInstance = new WifiDirect(base.app.BaseApp.instance);
		}
		return mInstance;
	}

    android.net.wifi.p2p.WifiP2pManager mWifiP2pManager = null;
    android.net.wifi.p2p.WifiP2pManager.Channel mWifiP2pChannel = null;
    
	private Context mContext = null;
	private static android.content.IntentFilter mIntentFilter = null;
    public WifiDirect(Context context) {
    	mContext = context;
		mIntentFilter = new android.content.IntentFilter();
		  //表示Wi-Fi对等网络状态发生了改变  
		mIntentFilter.addAction(android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);  
	  
	    //表示可用的对等点的列表发生了改变  
		mIntentFilter.addAction(android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);  
	  
	    //表示Wi-Fi对等网络的连接状态发生了改变  
		mIntentFilter.addAction(android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);  
	  
	    //设备配置信息发生了改变  
		mIntentFilter.addAction(android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);  
    }
    
    android.os.Looper mLooper = null;
    @SuppressLint("NewApi") 
    public void initWifiDirect(android.content.Context context) {
    	mWifiP2pManager = (android.net.wifi.p2p.WifiP2pManager) context.getSystemService(android.content.Context.WIFI_P2P_SERVICE);
    	mLooper = context.getMainLooper();
    	mWifiP2pChannel = mWifiP2pManager.initialize(context, mLooper, null);  
    }
    
    /*创建一个新的 BroadcastReceiver 类，用来监听系统的Wi-Fi P2P状态的改变*/
    
    public class WiFiDirectBroadcastReceiver extends android.content.BroadcastReceiver {
    	/*
    	@Override  
        public void onResume() {  
            super.onResume();  
            receiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);  
            registerReceiver(receiver, intentFilter);  
        }   
        
        @Override  
        public void onPause() {  
            super.onPause();  
            unregisterReceiver(receiver);  
        }  
    	*/
    	
    	private android.app.Activity mActivity = null;
    	
		@SuppressLint("NewApi") 
		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
			 String action = intent.getAction();  
		        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {  
		  
		            //确定Wi-Fi Direct模式是否已经启用，并提醒Activity。  
		            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);  
		            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {  
		            	//mActivity.setIsWifiP2pEnabled(true);  
		            } else {  
		            	//mActivity.setIsWifiP2pEnabled(false);  
		            }  
		        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {  
		            //对等点列表已经改变！我们可能需要对此做出处理。  
		            //从Wi-Fi P2P管理器中请求可用的对等点。  
		            //这是个异步的调用，  
		            //并且，调用行为是通过PeerListListener.onPeersAvailable()上的一个回调函数来通知的。  
		            if (null != mWifiP2pManager) {  
		            	mWifiP2pManager.requestPeers(mWifiP2pChannel, mPeerListListener);  
		            }  
		        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {  
		            //连接状态已经改变！我们可能需要对此做出处理。  
		        	 if (mWifiP2pManager == null) {  
		                 return;  
		             }  
		   
		             android.net.NetworkInfo networkInfo = (android.net.NetworkInfo) intent  
		                     .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);  
		   
		             if (networkInfo.isConnected()) {  
		   
		             //我们连上了其他的设备，请求连接信息，以找到群主的IP。  
		            	 //mWifiP2pManager.requestConnectionInfo(mWifiP2pChannel, connectionListener);  
		             }  
		        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {  
		            //DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()  
		        	//        .findFragmentById(R.id.frag_list);  
		        	//fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(  
		        	//        WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));  
		  
		        }  
		}
    }
    
    /*初始化对等点的搜索
         使用Wi-Fi Direct搜索附近的设备，需要调用 discoverPeers()方法。其参数如下：
      WifiP2pManager.Channel 是在你初始化对等网络的mManager时收到的。
      WifiP2pManager.ActionListener 的一个实例，实现了系统在查找成功或失败时会调用的方法
    */
    @SuppressLint("NewApi") 
    public void search() {
    	mWifiP2pManager.discoverPeers(mWifiP2pChannel, new WifiP2pManager.ActionListener() {  
    		  
            @Override  
            public void onSuccess() {  
            //查找初始化成功时的处理写在这里。  
      
            //实际上并没有发现任何服务，所以该方法可以置空。  
            //对等点搜索的代码在onReceive方法中，详见下文。  
            }  
      
            @Override  
            public void onFailure(int reasonCode) {  
            //查找初始化失败时的处理写在这里。  
            //警告用户出错了。  
            }  
        });  
    }

	private java.util.List mPeers = new java.util.ArrayList();
	private android.net.wifi.p2p.WifiP2pManager.PeerListListener mPeerListListener = null; 
    @SuppressLint("NewApi") 
    public void getP2Plist() {
    // 获取对等点列表 
        mPeerListListener = new android.net.wifi.p2p.WifiP2pManager.PeerListListener() {  
            @Override  
            public void onPeersAvailable(android.net.wifi.p2p.WifiP2pDeviceList peerList) {  
      
                //旧的不去，新的不来  
            	mPeers.clear();  
            	mPeers.addAll(peerList.getDeviceList());  
      
                //如果AdapterView可以处理该数据，则把变更通知它。比如，如果你有可用对等点的ListView，那就发起一次更新。  
                //((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();  
                if (0 == mPeers.size()) {  
                    //Log.d(WiFiDirectActivity.TAG, "No devices found");  
                    return;  
                }  
            };  
        };
    }
    
    // 连接一个对等点
    //@Override  
    WifiP2pManager.ActionListener mWifiP2pActionListener = null;
    
    @SuppressLint("NewApi") 
    public void connect() {
        //使用在网络上找到的第一个设备。  
        WifiP2pDevice wifiP2Pdevice = (WifiP2pDevice) mPeers.get(0);  
  
        WifiP2pConfig wifiP2Pconfig = new WifiP2pConfig();  
        wifiP2Pconfig.deviceAddress = wifiP2Pdevice.deviceAddress;  
        wifiP2Pconfig.wps.setup = WpsInfo.PBC;
        
        mWifiP2pActionListener = new android.net.wifi.p2p.WifiP2pManager.ActionListener() {  
            @Override  
            public void onSuccess() {  
            // WiFiDirectBroadcastReceiver将会通知我们。现在可以先忽略。  
            }  
  
            @Override  
            public void onFailure(int reason) {  
                //Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",  
            	//        Toast.LENGTH_SHORT).show();  
            }  
        };
        mWifiP2pManager.connect(mWifiP2pChannel, wifiP2Pconfig, mWifiP2pActionListener);  
    }
    
    //@Override  
    @SuppressLint("NewApi") 
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {  
  
        // InetAddress在WifiP2pInfo结构体中。  
        //java.net.InetAddress groupOwnerAddress = info.groupOwnerAddress.getHostAddress();  
  
        //组群协商后，就可以确定群主。  
        if (info.groupFormed && info.isGroupOwner) {  
        //针对群主做某些任务。  
        //一种常用的做法是，创建一个服务器线程并接收连接请求。  
        } else if (info.groupFormed) {  
        //其他设备都作为客户端。在这种情况下，你会希望创建一个客户端线程来连接群主。  
        }  
    }  
    
    /* http://developer.android.com/tools/samples/index.html
     * 一旦建立了连接，就能够使用套接字在设备之间传输数据。基本的步骤如下：
     * http://blog.csdn.net/think_soft/article/details/8220736
     1. 创建一个ServerSocket对象。这个套接字会在指定的端口上等待来自客户端的连接
                    并且要一直阻塞到连接发生，因此要在后台线程中做这件事。
     2. 创建一个客户端的Socket对象。该客户端要使用服务套接字的IP地址和端口来连接服务端设备。
     3. 把数据从客户端发送给服务端。当客户端套接字跟服务端套接字成功的建立了连接
                   你就能够以字节流的形式，把数据从客户端发送给服务端了。
     4. 服务套接字等待客户端的连接（用accept()方法）。这个调用会一直阻塞到客户端的连接发生
                    因此这个调用要放到另外一个线程中。当连接发生时，服务端能够接收来自客户端的数据
                    并对这个数据执行一些操作，如保存到文件或展现给用户
     */
    
    public static class FileServerAsyncTask extends android.os.AsyncTask<String, Integer, String>{

        private Context context;
        public FileServerAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                java.net.ServerSocket serverSocket = new java.net.ServerSocket(8888);
                java.net.Socket client = serverSocket.accept();
                /**
                 * If this code is reached, a client has connected and transferred data
                 * Save the input stream from the client as a JPEG file
                 */

                final java.io.File f = new java.io.File(
                		android.os.Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");

                java.io.File dirs = new java.io.File(f.getParent());

                if (!dirs.exists())
                    dirs.mkdirs();

                f.createNewFile();
                java.io.InputStream inputstream = client.getInputStream();
                //copyFile(inputstream, new java.io.FileOutputStream(f));
                serverSocket.close();
                return f.getAbsolutePath();

            } catch (java.io.IOException e) {
                //Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }

        /**
         * Start activity that can handle the JPEG image
         */

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                //statusText.setText("File copied - " + result);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(android.net.Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
            }
        }
    }
    
    public void clientDemo() {
    	Context context = null; //this.getApplicationContext();
    	String host;
    	int port;
    	int len;
    	java.net.Socket socket = new java.net.Socket();
    	byte buf[] = new byte[1024];
    	try{
    	    /**
    	     * Create a client socket with the host,
    	     * port, and timeout information.
    	     */
    	    socket.bind(null);
    	    socket.connect((new java.net.InetSocketAddress("", 8888)), 500);
    	 
    	    /**
    	     * Create a byte stream from a JPEG file and pipe it to the output stream
    	     * of the socket. This data will be retrieved by the server device.
    	     */
    	    java.io.OutputStream outputStream = socket.getOutputStream();
    	    android.content.ContentResolver cr = context.getContentResolver();
    	    java.io.InputStream inputStream =null;
    	    inputStream = cr.openInputStream(Uri.parse("path/to/picture.jpg"));
    	    while((len = inputStream.read(buf))!=-1){
    	        outputStream.write(buf,0, len);
    	    }
    	    outputStream.close();
    	    inputStream.close();
    	}catch(java.io.FileNotFoundException e){
    	    //catch logic
    	}catch(java.io.IOException e){
    	    //catch logic
    	}
    	 
    	/**
    	 * Clean up any open sockets when done
    	 * transferring or if an exception occurred.
    	 */
    	finally{
    	    if(socket !=null){
    	        if(socket.isConnected()){
    	            try{
    	                socket.close();
    	            }catch(java.io.IOException e){
    	                //catch logic
    	            }
    	        }
    	    }
    	}
    }
}
