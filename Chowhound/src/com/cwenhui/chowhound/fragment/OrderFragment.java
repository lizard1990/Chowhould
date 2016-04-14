package com.cwenhui.chowhound.fragment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cwenhui.chowhound.adapter.CommonAdapter;
import com.cwenhui.chowhound.adapter.OrderFragmentAdapter;
import com.cwenhui.chowhound.bean.OrderFragmentBean;
import com.cwenhui.chowhound.config.Configs;
import com.cwenhui.chowhound.utils.HttpUtil;
import com.cwenhui.chowhound.utils.ImageFirstDisplayListener;
import com.cwenhui.chowhound.utils.ViewHolder;
import com.cwenhui.chowhound.widget.LoadingDialog;
import com.example.chowhound.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class OrderFragment extends Fragment implements OnRefreshListener2<ListView> {
	private static final int INIT = 0;
	private static final int PULL_DOWN = 1;
	private static final int PULL_UP = 2;
	private static final int PAGE_SIZE = 5;
	private int PAGE = 2; 												// 默认第2页,初始化数据时已加载了第一页的数据
	private final String TAG = "OrderFragment";
	private View mView;
	private List<OrderFragmentBean> orders;
	private PullToRefreshListView mListView;
	private OrderFragmentAdapter adapter;
	private int lastOrderId = -1;
	private int totalPrice = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater
				.inflate(R.layout.fragment_main_order, container, false);
		initData();
		initView();
		return mView;
	}

	private void initView() {
		mListView = (PullToRefreshListView) mView.findViewById(R.id.pull_refresh_list_fragment_order);
		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(this);
		adapter = new OrderFragmentAdapter(getActivity(), orders, R.layout.item_fragment_order2);
		mListView.setAdapter(adapter);
	}

	private void initData() {
		orders = new ArrayList<OrderFragmentBean>();
		getDataTask(Configs.APIOrderDetailsByUid+"uid=6&pageNo=1&pageSize=5", INIT);
	}

	private void getDataTask(String url, final int tag) {
		LoadingDialog.showDialog(getActivity());		//加载数据时显示对话框
		HttpUtil.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				OrderFragmentBean bean = null;
				try {
					JSONArray orderDetails = new JSONArray(new String(data));
					if(tag == PULL_DOWN){													// 如果是下拉刷新，则先清除搜游数据，然后重新加载
						orders.clear();
						PAGE = 2;
					}
					for (int i = 0; i < orderDetails.length(); i++) {
						JSONObject node = orderDetails.getJSONObject(i);
						JSONObject order = new JSONObject(node.getString("order"));				//获取订单信息
						JSONObject goods = new JSONObject(node.getString("goods"));				//获取订单对应的商品信息
						if(order.getInt("orderId") == lastOrderId){								//和上一个orderDetail是同一个orderId,即同一个订单
							totalPrice += node.getInt("goodsNum")*goods.getInt("goodsPrice");	//计算目前总价
							bean.setTotalPrice(totalPrice);
							bean.setGoodsName(goods.getString("goodsName")+" 等若干商品");			
						}else{																	//和上一个orderDetail不是同一个orderId,即新的订单
							totalPrice = 0;														//新的订单totalPrice置0
							totalPrice += node.getInt("goodsNum")*goods.getInt("goodsPrice");	//计算目前总价
							bean = new OrderFragmentBean(order.getInt("orderId"),order.getString("orderName"), goods.getString("goodsImgPath"), 
									goods.getString("goodsName"), order.getString("orderCreateTime"), order.getString("orderState"), totalPrice);
							orders.add(bean);													//如果是新的订单就将bean加入订单列表
						}
						lastOrderId = order.getInt("orderId");
					}
					adapter.notifyDataSetChanged();
					mListView.onRefreshComplete();
					LoadingDialog.dismissDialog();
					if(tag == PULL_UP){
						PAGE++;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable throwable) {
				Log.v(TAG, "Configs.APIOrderDetailsByUid  throwable:  "
						+ throwable.toString());
			}
		});
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		Toast.makeText(getActivity(), "Pull Down", Toast.LENGTH_SHORT).show();
		getDataTask(Configs.APIOrderDetailsByUid+"uid=6&pageNo=1&pageSize="+PAGE_SIZE, PULL_DOWN);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		Toast.makeText(getActivity(), "Pull Up-->"+PAGE, Toast.LENGTH_SHORT).show();
		getDataTask(Configs.APIOrderDetailsByUid+"uid=6&pageNo="+PAGE+"&pageSize="+PAGE_SIZE, PULL_UP);
	}

}
