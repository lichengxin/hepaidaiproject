package com.app.activity;

import java.util.ArrayList;

import ui.activity.BaseActivity;
import utils.*;


import base.app.*;


import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

/*//
 * 学习向导页 半透明的  新手向导蒙版
 * 蒙版提示页（添加新功能后的一种提示）
 * http://www.cnblogs.com/ws5861/p/3675935.html
 * 其实提示页本身就是一个布局，里面有一张或是几张图片，
 * 向用户提示在当前版本有新添加了某个功能，引导用户使用，一般只会出现一次
//*/

/*//
 * <?xml version="1.0" encoding="utf-8"?>
 3 <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
 4     android:id="@+id/parentlayout"
 5     android:layout_width="fill_parent"
 6     android:layout_height="fill_parent"
 7     android:background="@color/all_backgroud" >
 8 
 9 
10       .
11       .(这里省略无关的代码)
12       .
13 
14 <LinearLayout
15         android:id="@+id/linearLayout_mask"
16         android:layout_width="fill_parent"
17         android:layout_height="fill_parent"（注意设置全屏）
18         android:background="@drawable/share_mask_back"
19         android:gravity="top"（适当的方位也很重要）
20         android:visibility="gone" >
21 
22         <ImageView
23             android:id="@+id/imageView_mask"
24             android:layout_width="fill_parent"
25             android:layout_height="wrap_content"
26             android:background="@drawable/share_mask"(美工设计的引导提示图片)
27             android:scaleType="fitStart" >
28         </ImageView>
29     </LinearLayout>
30     
31 </RelativeLayout>
//*/

/*//
 * share_mask_back背景设置代码（半透明）：
复制代码
 1 <?xml version="1.0" encoding="utf-8"?>
 2 <shape xmlns:android="http://schemas.android.com/apk/res/android"
 3 >
 4     <gradient 
 5         android:startColor="#d2000000"
 6         android:centerColor="#d2000000"
 7         android:endColor="#d2000000" 
 8         android:shape="rectangle"
 9         android:centerX="-5"
10         android:angle="0"
11     />
12 </shape>
复制代码
//*/

/*//
//初次进入时候的蒙版背景 
 3 private LinearLayout linearLayout_mask;  
 4 // 初次进入时的蒙版图片
 5 private ImageView imageView_mask;
 6 
 7 //蒙版相关初始化
 8 linearLayout_mask = (LinearLayout)findViewById(R.id.linearLayout_mask);
 9 imageView_mask = (ImageView)findViewById(R.id.imageView_mask);
10 
11 //设置监听
12 
13 linearLayout_mask.setOnClickListener(this);
14 
15  @Override
16 
17       public void onClick(View v) {
18           // TODO Auto-generated method stub
19           switch (v.getId()) {
20           case R.id.linearLayout_mask://分享蒙版监听,截取蒙板下方的点击事件
21               break;
22              
23           case R.id.imageView_mask://分享蒙版上的按钮
24               linearLayout_mask.setVisibility(View.GONE);
25              context.getSharedPreferences("Setting", Context.MODE_PRIVATE).edit().putBoolean("read_share", true).commit();
26             break;
27             
28         default:
29              break;
30          }
31     }
32 
33   setMask();//设置蒙版，一般在oncreat()里面设置
34 
36     // 设置第一次进入时的蒙版
38     private void setMask() {
39         
40         SharedPreferences sharedPreferences = context.getSharedPreferences(
41                 "Setting", Context.MODE_PRIVATE);
42         boolean isread =  sharedPreferences.getBoolean("read_share", false);
43         if(!isread){
44             // 调整顶部背景图片的大小，适应不同分辨率的屏幕
45             DisplayMetrics dm = new DisplayMetrics();
46             getWindowManager().getDefaultDisplay().getMetrics(dm);
47             int width = dm.widthPixels;
48             int height = (int) ((float) width / 48 *31);
49             imageView_mask.setLayoutParams(new LinearLayout.LayoutParams(width, height));
50             linearLayout_mask.setVisibility(View.VISIBLE);
51         }else{
52             linearLayout_mask.setVisibility(View.GONE);
53         }
54     }
//*/
public class ActivityAppStudyGuide extends BaseActivity {

	private static String LOGTAG = ActivityAppStudyGuide.class.getSimpleName();

	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
	}

	private void initViews() {
	}
}
