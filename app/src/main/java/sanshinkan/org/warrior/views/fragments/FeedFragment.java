package sanshinkan.org.warrior.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import sanshinkan.org.warrior.R;
import sanshinkan.org.warrior.SApplication;
import sanshinkan.org.warrior.adapters.FeedAdapter;
import sanshinkan.org.warrior.data.NewsFeed;
import sanshinkan.org.warrior.utils.RandomCallback;

/**
 * Created by apoorvarora on 17/02/19.
 */
public class FeedFragment extends BaseFragment implements RandomCallback {
    private LayoutInflater inflater;
    private Activity mActivity;
    private View getView;
    private boolean destroyed;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private FeedAdapter mAdapter;
    private List<NewsFeed> loadList;
    int start = 0, count = 100;

    public FeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        destroyed = false;
        getView = getView();
        mActivity = getActivity();
        inflater = LayoutInflater.from(mActivity);

        swipeRefresh = getView.findViewById(R.id.swipeRefresh);
        recyclerView = getView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadList = new ArrayList<>();
        mAdapter = new FeedAdapter(mActivity, loadList, this);
        recyclerView.setAdapter(mAdapter);

        setListeners();

        getView.findViewById(R.id.progress_view).setVisibility(View.VISIBLE);
        getView.findViewById(R.id.empty_view).setVisibility(View.GONE);
        getView.findViewById(R.id.recyclerView).setVisibility(View.GONE);
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
    public void onDestroyView() {
        destroyed = true;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }

    private void refreshView() {
        getView.findViewById(R.id.progress_view).setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
        loadList = ((SApplication)mActivity.getApplication()).getNewsFeed();;
        mAdapter = new FeedAdapter(mActivity, loadList, this);
        recyclerView.setAdapter(mAdapter);

        getView.findViewById(R.id.empty_view).setVisibility(View.GONE);
        getView.findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
    }

    @Override
    public void randomCallback(Object object) {
        if (object != null) {
            if (object instanceof NewsFeed) {
            }
        }
    }

    public void filter(String query) {
        List<NewsFeed> filtered_loadList = new ArrayList<>();

        for (NewsFeed feed: loadList) {
            for (Method m : feed.getClass().getMethods()) {
                try {
                    if (m.getParameterTypes().length == 0) {
                        final String r = String.valueOf(m.invoke(feed));
                        if (r.contains(query)) {
                            filtered_loadList.add(feed);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        loadList = filtered_loadList;
        mAdapter = new FeedAdapter(mActivity, loadList, this);
        recyclerView.setAdapter(mAdapter);
    }

}
