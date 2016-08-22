package ui.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import base.app.BaseConsts;

/*//
 * 通用 Fragment 基类
 * 
//*/
public abstract class BaseFragment extends Fragment {

	/*//
	public static interface BaseFragmentActivityInterface {
	     public abstract void setSelectedFragment(BaseFragment selectedFragment);
	}
	//*/
	
	// Activity可以很容易的得到物理返回键的监听事件，而Fragment却不能
	
	/*//
	 * 1 Fragment is added
	 *   onAttach
	 *   onCreate
	 *   onCreateView
	 *   onActivityCreated
	 *   onStart
	 *   onResume
	 * 2 Fragment is active
	 *   onPause
	 *   onStop
	 *   onDestroyView
	 *   onDetach
	 * 3 Fragment is destroyed  
	//*/
	private static final String LOGTAG = BaseFragment.class.getSimpleName();
	protected android.view.View mView = null;
	protected abstract int getLayoutId();

	@Override
	public void onAttach(android.app.Activity activity)
	{
		super.onAttach(activity);
		//BaseApp.showToast("onAttach:" + this.getClass().getSimpleName());
	}
	
	@Override
	public void onStart() {
	    super.onStart();
	    //告诉FragmentActivity，当前Fragment在栈顶
	    ((BaseFragmentActivity)getActivity()).setSelectedFragment(this);
	}
	
	@Override
	public void onDetach() {
		//BaseApp.showToast("onDetach:" + this.getClass().getSimpleName());
		super.onDetach();
	}

	@Override
	public void onPause() {
		//BaseApp.showToast("onPause:" + this.getClass().getSimpleName());
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getWindow().setFormat(PixelFormat.TRANSPARENT);
		if (null != savedInstanceState) {
			//BaseApp.showToast("onCreate instance:" + this.getClass().getSimpleName());
		} else {
			//BaseApp.showToast("onCreate state:" + this.getClass().getSimpleName());
		}
	}

	/*//
	@Override
	public void onDestroy() {
		BaseApp.showToast("onDestroy:" + this.getClass().getSimpleName());
		super.onDestroy();
	}
	//*/

	public void redirectToActivity (Class destActivityClass) {
		android.content.Intent intent = new android.content.Intent();
		intent.setClass(this.getActivity(), destActivityClass);
		this.startActivity(intent);
	}	

	public void redirectToActivity (Class destActivityClass, String param) {
		android.content.Intent intent = new android.content.Intent();
		intent.setClass(this.getActivity(), destActivityClass);
		intent.putExtra(BaseConsts.EXTRA_ACTIVITY_PARAM, param);
		this.startActivity(intent);
	}
	
	public void redirectToActivity (Class destActivityClass, String title, String param) {
		android.content.Intent intent = new android.content.Intent();
		intent.setClass(this.getActivity(), destActivityClass);
		intent.putExtra(BaseConsts.EXTRA_ACTIVITY_TITLE, title);
		intent.putExtra(BaseConsts.EXTRA_ACTIVITY_PARAM, param);
		this.startActivity(intent);
	}	

	public void redirectToActivity (Class destActivityClass, int requestCode) {
		android.content.Intent intent = new android.content.Intent();
		intent.setClass(this.getActivity(), destActivityClass);
		this.startActivityForResult(intent, requestCode);
	}	

	protected String idStr(int id) {
		return getResources().getString(id);
	}

	protected int idColor(int id) {
		return getResources().getColor(id);
	}

	protected float idDimen(int id) {
		return getResources().getDimension(id);
	}	
	
	public int getDisableViewPagerMaxY() {
		return 0;
	}
}
