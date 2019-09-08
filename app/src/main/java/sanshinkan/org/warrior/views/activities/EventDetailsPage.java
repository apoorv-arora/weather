package sanshinkan.org.warrior.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Member;
import java.util.ArrayList;

import sanshinkan.org.warrior.R;
import sanshinkan.org.warrior.adapters.FeedAdapter;
import sanshinkan.org.warrior.data.NewsFeed;
import sanshinkan.org.warrior.utils.RandomCallback;
import sanshinkan.org.warrior.utils.networking.UploadManagerCallback;

public class EventDetailsPage extends AppCompatActivity implements UploadManagerCallback, RandomCallback {

    private LayoutInflater inflater;
    private boolean destroyed;
    private Activity mActivity;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private FeedAdapter mAdapter;
    private ArrayList<NewsFeed> loadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        // bar code scanner
        // scan and fetch the code
        // code will be validated at the backend and attendance will be marked
        // provide them a link for payment updates
        mActivity = this;
        destroyed = false;
//        UploadManager.getInstance().addCallback(this
//                , UploadManager.MEMBERS_ALL);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadList = new ArrayList<>();
        mAdapter = new FeedAdapter(this, loadList, this);
        recyclerView.setAdapter(mAdapter);

        setListeners();

        findViewById(R.id.progress_view).setVisibility(View.VISIBLE);
        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        refreshView();
    }

    private void setListeners() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });
    }

    @Override
    public void onDestroy() {
        destroyed = true;
//        UploadManager.getInstance().removeCallback(this
//                , UploadManager.MEMBERS_ALL);
        super.onDestroy();
    }

    private void refreshView() {
//        UploadManager.getInstance().apiCall(new HashMap<String, String>(), UploadManager.MEMBERS_ALL, null, null);
    }

    @Override
    public void randomCallback(Object object) {
        if (object != null) {
            if (object instanceof Member) {
            }
        }
    }

    @Override
    public void uploadStarted(int requestType, Object data, Object requestData) {

    }

    @Override
    public void uploadFinished(int requestType, Object data, boolean status, String errorMessage, Object requestData) {
//        if (requestType == UploadManager.MEMBERS_ALL) {
//            if (!destroyed) {
//                findViewById(R.id.progress_view).setVisibility(View.GONE);
//                swipeRefresh.setRefreshing(false);
//                if (data != null && data instanceof Object[] && ((Object[])data)[0] instanceof ArrayList<?>) {
//                    loadList = (ArrayList<Member>) ((Object[])data)[0];
//                    mAdapter = new MembersAdapter(mActivity, loadList, this);
//                    recyclerView.setAdapter(mAdapter);
//
//                    findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
//                } else {
//                    findViewById(R.id.recyclerView).setVisibility(View.GONE);
//                }
//            }
//        }
    }
}
