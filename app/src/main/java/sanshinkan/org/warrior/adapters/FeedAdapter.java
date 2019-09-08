package sanshinkan.org.warrior.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import sanshinkan.org.warrior.R;
import sanshinkan.org.warrior.SApplication;
import sanshinkan.org.warrior.data.NewsFeed;
import sanshinkan.org.warrior.holders.FeedItemViewHolder;
import sanshinkan.org.warrior.holders.GifViewHolder;
import sanshinkan.org.warrior.utils.ImageLoader;
import sanshinkan.org.warrior.utils.RandomCallback;

/**
 * Created by apoorvarora on 17/02/19.
 */

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Activity mActivity;
    private ImageLoader loader;
    private RandomCallback callback;
    private List<NewsFeed> loadList;

    private final int TYPE_LOADING = 0;
    private final int TYPE_NORMAL = 1;

    public FeedAdapter(Activity mActivity, List<NewsFeed> loadList, RandomCallback callback) {
        this.mActivity = mActivity;
        this.loadList = loadList;
        this.callback = callback;
        loader = new ImageLoader(mActivity, (SApplication) mActivity.getApplication());
    }

    @Override
    public int getItemViewType(int position){
        if(loadList.get(position) == null){
            return TYPE_LOADING;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType){
            case TYPE_NORMAL:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_feed_item, viewGroup, false);
                FeedItemViewHolder viewHolder = new FeedItemViewHolder(view);
                viewHolder.snippet_recommended_parent.setOnClickListener(this);
                return viewHolder;

            case TYPE_LOADING:
                View view_null = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gif, viewGroup, false);
                return new GifViewHolder(view_null);
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!(viewHolder instanceof FeedItemViewHolder))
            return;

        if (loadList.get(position) == null)
            return;

        FeedItemViewHolder vh = (FeedItemViewHolder) viewHolder;
        NewsFeed course = loadList.get(position);
        vh.setValues(loader, mActivity, course);
        vh.snippet_recommended_parent.setTag(position);
    }

    @Override
    public int getItemCount() {
        return loadList == null ? 0 : loadList.size();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Integer) {
            int position = (int)tag;
            NewsFeed course = loadList.get(position);
            callback.randomCallback(course);
        }
    }
}
