package com.example.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.schedule.NewFriendsActivity.AddFriendsRequestAT;
import com.example.schedule.NewFriendsActivity.GroupItemAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NewFriendsToGroupActivity extends Activity {
	private List<Map<String, Object>> mData;
	private ListView lv_new_friends_to_group;
    private GroupItemAdapter mAdapter;
    private ArrayList<String> list;
    private ArrayList<UserInfo> friends = new ArrayList();
    private ArrayList<String> members = new ArrayList();
    private static String url = Global.BASICURL+"MemberAdd";
    private String userId;
    private String groupId;
    private String httpRespond;
    public static Map<Integer, Boolean> isSelected;  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friends_to_group);
		
		lv_new_friends_to_group = (ListView) findViewById(R.id.lv_new_friends_to_group);
	    mAdapter = new GroupItemAdapter(this, lv_new_friends_to_group);
	    //Get userId and groupId,for use of sending string
	    userId = getIntent().getStringExtra("userIdToAddFriendsToGroup");
	    groupId = getIntent().getStringExtra("groupIdToAddFriendsToGroup");
	    /*
	     * Parse JSON data to fill ListView
	     */
	    if(!getIntent().getStringExtra("paraToNewFriendsToGroupActivity").isEmpty()){
		        try {
						JSONObject resultJSON = new JSONObject(getIntent().getStringExtra("paraToNewFriendsToGroupActivity"));
						JSONObject retrieveArray = new JSONObject();
						JSONArray resultJSONArray = new JSONArray();
						resultJSONArray = resultJSON.getJSONArray("memberExcludeArray");
						for(int i=0 ; i< resultJSONArray.length();i++){
							UserInfo user = new UserInfo();
							retrieveArray = (JSONObject) resultJSONArray.get(i);
							user.setUsername(retrieveArray.getString("memberName"));												
					        user.setImage(retrieveArray.getString("memberImage"));
					        user.setUserId(retrieveArray.getString("memberId"));
					        friends.add(user); 
						}
						
					} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        isSelected = new HashMap<Integer, Boolean>();  
		        for(int i=0 ; i < friends.size();i++){
		        	mAdapter.addUser(friends.get(i));
		        }
		        lv_new_friends_to_group.setAdapter(mAdapter);
		        lv_new_friends_to_group.setOnItemClickListener(new OnItemClickListener() {
		
		            @Override
		            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		                    long arg3) {
		            	//Find touched CheckBox 
		            	CheckBox cb = (CheckBox)arg1.findViewById(R.id.cb_new_friends_to_group_is_add);
		            	cb.toggle();
		            	isSelected.put(arg2, cb.isChecked());
		            	if(cb.isChecked()){
		            		members.add(friends.get(arg2).getUserId());
		            	}else{
		            		
		            		for(int i = 0; i<members.size(); i++){
		            			if(members.get(i).contentEquals(friends.get(arg2).getUserId())){
		            				members.remove(i);
		            			}
		            		}
		            	}
		                
		            }
		        });
	       }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_friends_to_group, menu);
		MenuItem miAddFriendsToGroupOk = (MenuItem)menu.findItem(R.id.btn_new_friends_to_group_ok);
		MenuItem miAddFriendsToGroupCancel = (MenuItem)menu.findItem(R.id.btn_new_friends_to_group_cancel);
		miAddFriendsToGroupOk.setOnMenuItemClickListener(new OnMenuItemClickListener(){
				public boolean onMenuItemClick(MenuItem item) {
					// TODO Auto-generated method stub
					if(members.size() != 0){
						//Retrieve member as a string
						JSONObject sendObject = new JSONObject();
						try {
							
							JSONArray friends = new JSONArray();
							for(int i=0 ; i < members.size() ; i++){
								friends.put(members.get(i));
								sendObject.put("friends", friends);
							}
							sendObject.put("userId", userId);
							sendObject.put("groupId", groupId);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						new AddFriendsToGroupRequestAT().execute(sendObject.toString());
					}else{
						Toast inputInvalid = Toast.makeText(NewFriendsToGroupActivity.this,
							     "Please select at least one", Toast.LENGTH_LONG);
						inputInvalid.setGravity(Gravity.CENTER, 0, 0);
						inputInvalid.show();
					}
					return true;
				}
		    	
		    });
		    
		miAddFriendsToGroupCancel.setOnMenuItemClickListener(new OnMenuItemClickListener(){

				public boolean onMenuItemClick(MenuItem item) {
					// TODO Auto-generated method stub 
		            finish(); 
					return true;
				}
		    	
		    });
		    return true;
	}
	class AddFriendsToGroupRequestAT extends AsyncTask<String,Integer,Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			if(!params[0].isEmpty()){
				try {
					/*
					 * construct a http post request
					 */
					HttpPost httpPost = new HttpPost(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpEntity hEntity;
					
					hEntity = new StringEntity(params[0],"utf-8");
					System.out.println("Test sending string(in new friends to group async task):"+params[0]);
					httpPost.setEntity(hEntity);
					HttpResponse httpResponse = httpClient.execute(httpPost);
					int result;
					if(httpResponse.getStatusLine().getStatusCode() == 200){
						httpRespond = new String(EntityUtils.toByteArray(httpResponse.getEntity()),"UTF-8"); 
						JSONObject resultJSON = new JSONObject(httpRespond);
						result = resultJSON.getInt("result");
					}else{
						return Primitive.CONNECTIONREFUSED;
					}
					if (httpClient != null) {
						httpClient.getConnectionManager().shutdown();
					}
					return result;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return -1;
				}
			}else{
				return -1;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			switch(result){
			case Primitive.CONNECTIONREFUSED:
				Toast connectError = Toast.makeText(NewFriendsToGroupActivity.this,
					     "Cannot connect to the server", Toast.LENGTH_LONG);
				connectError.setGravity(Gravity.CENTER, 0, 0);
				connectError.show();
				break;
			case Primitive.ACCEPT:
	            finish(); 
				break;
			default:
				break;
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		}
	}
    public class GroupItemAdapter extends BaseAdapter {  
        private LayoutInflater mInflater;  
        private Vector<UserInfo> mUsers = new Vector<UserInfo>();  
        private ListView mListView;  
        SyncImageLoader syncImageLoader;  
  
        public GroupItemAdapter(Context context, ListView listView) {  
            mInflater = LayoutInflater.from(context);  
            syncImageLoader = new SyncImageLoader();  
            mListView = listView;  
        }  
  
        public void addGroup(String userName, String image) {  
            UserInfo user = new UserInfo();  
            user.setUsername(userName);
            user.setImage(image);
            mUsers.add(user);  
        }  
  
        public void addUser(UserInfo user){
        	mUsers.add(user);
        }
        
        public void addUsers(ArrayList<UserInfo> users){
        	mUsers.clear();
        	for(int i = 0; i < users.size(); i++)
        		mUsers.add(users.get(i));
        }
        public void clean() {  
        	mUsers.clear();  
        }  
  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return mUsers.size();  
        }  
  
        public Object getItem(int position) {  
            if (position >= getCount()) {  
                return null;  
            }  
            return mUsers.get(position);  
        }  
  
        public long getItemId(int position) {  
            // TODO Auto-generated method stub  
            return position;  
        }  
  
        public View getView(int position, View convertView, ViewGroup parent) {  
            if (convertView == null) {  
                convertView = mInflater.inflate(R.layout.new_friends_to_group_listview_item,  
                        null);  
            }  
            UserInfo user = mUsers.get(position);  
            convertView.setTag(position);  
            ImageView ivUserProfile = (ImageView) convertView.findViewById(
            		R.id.iv_new_friends_to_group_user_profile);
            TextView tvUserName = (TextView)convertView.findViewById(
            		R.id.tv_new_friends_to_group_username);
            CheckBox cbIsChecked = (CheckBox)convertView.findViewById(
            		R.id.cb_new_friends_to_group_is_add);
            cbIsChecked.setChecked(isSelected.get(position));
            tvUserName.setText(user.getUsername());  
            if(!user.getImage().contentEquals("null")){
            	ivUserProfile.setBackgroundResource(R.drawable.no_photo_small);
            	syncImageLoader.loadImage(position,
            			Global.USERIMGURL+user.getImage(),  
                        imageLoadListener, user.getImage()); 
            	} else{
            		ivUserProfile.setBackgroundResource(R.drawable.no_photo_small);
            	}
             
            return convertView;  
        }  
  
        SyncImageLoader.OnImageLoadListener imageLoadListener = 
        		new SyncImageLoader.OnImageLoadListener() {  
  
			public void onImageLoad(Integer t, Drawable drawable) {  
                // UserInfo model = (UserInfo) getItem(t);  
                View view = mListView.findViewWithTag(t);  
                if (view != null) {  
                    ImageView iv = (ImageView) view  
                            .findViewById(R.id.iv_new_friends_to_group_user_profile);  
                    iv.setBackgroundDrawable(drawable);  
                }  
            }  
  
            public void onError(Integer t) {  
                UserInfo model = (UserInfo) getItem(t);  
                View view = mListView.findViewWithTag(model);  
                if (view != null) {  
                    ImageView iv = (ImageView) view  
                            .findViewById(R.id.iv_new_friends_to_group_user_profile);  
                    iv.setBackgroundResource(R.drawable.no_photo_small);  
                }  
            }  
  
        };  
  
        public void loadImage() {  
            int start = mListView.getFirstVisiblePosition();  
            int end = mListView.getLastVisiblePosition();  
            if (end >= getCount()) {  
                end = getCount() - 1;  
            }  
            syncImageLoader.setLoadLimit(start, end);  
            syncImageLoader.unlock();  
        }  
  
        AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {  
  
            public void onScrollStateChanged(AbsListView view, int scrollState) {  
                switch (scrollState) {  
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:  
                    syncImageLoader.lock();  
                    break;  
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:  
                    loadImage();  
                    break;  
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:  
                    syncImageLoader.lock();  
                    break;  
  
                default:  
                    break;  
                }  
  
            }  
  
            public void onScroll(AbsListView view, int firstVisibleItem,  
                    int visibleItemCount, int totalItemCount) {  
                // TODO Auto-generated method stub  
  
            }  
        };  
    }

}
