package sanshinkan.org.warrior.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import sanshinkan.org.warrior.R;
import sanshinkan.org.warrior.data.NewsFeed;
import sanshinkan.org.warrior.utils.CommonLib;
import sanshinkan.org.warrior.utils.ImageLoader;

/**
 * Created by apoorvarora on 17/02/19.
 */

public class FeedItemViewHolder extends RecyclerView.ViewHolder {

    public View snippet_recommended_parent;
    ImageView event_image, country_image;
    TextView event_title, country_name, dateTv, days_to_go;

    public FeedItemViewHolder(View view) {
        super(view);
        snippet_recommended_parent = view.findViewById(R.id.snippet_recommended_parent);
        event_image = view.findViewById(R.id.event_image);
        country_image = view.findViewById(R.id.country_image);
        event_title = view.findViewById(R.id.event_title);
        country_name = view.findViewById(R.id.country_name);
        dateTv = view.findViewById(R.id.date);
        days_to_go = view.findViewById(R.id.days_to_go);
    }

    public void setValues(ImageLoader loader, Activity mActivity, NewsFeed course) {
        int width = mActivity.getWindowManager().getDefaultDisplay().getWidth();

        loader.setImageFromUrlOrDisk(course.getCoverImage(), event_image, "", width, width, false);
        event_title.setText(course.getTitle());
        country_name.setText(course.getLocationCountryReadable());
        int days = course.getDaysToStart();
        long currentDate = System.currentTimeMillis();
        Date date=new Date(currentDate);
        date.setDate(date.getDate()+days);

        if (days < 1)
            days_to_go.setText(mActivity.getResources().getString(R.string.day_to_go, days));
        else
            days_to_go.setText(mActivity.getResources().getString(R.string.days_to_go, days));

        dateTv.setText(CommonLib.getDateString(mActivity, date.getTime()));
    }
}