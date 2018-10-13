package com.tcn.tcnbay.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tcn.tcnbay.R;
import com.tcn.tcnbay.interfaces.OnListFragmentInteractionListener;
import com.tcn.tcnbay.model.Video;

import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private List<Video> mVideos;
    private final OnListFragmentInteractionListener mListener;
    private final Activity context;
    private String host;
    private int port;

    public VideoListAdapter(Activity context, List<Video> items, OnListFragmentInteractionListener listener, String host, int port) {
        mVideos = items;
        mListener = listener;
        this.context = context;
        this.host = host;
        this.port = port;
    }

    public void updateList(List<Video> list) {
        this.mVideos.clear();
        this.mVideos.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mVideos.get(position);
        holder.mTitleView.setText(mVideos.get(position).title);
        holder.mNumberViewsView.setText(context.getResources().getQuantityString(R.plurals.video_views, mVideos.get(position).views, mVideos.get(position).views));
        Picasso.get().load("http://" + host + ":" + port + "/files/thumb/" + mVideos.get(position).thumbnail).placeholder(R.drawable.placeholder).fit().centerCrop().into(holder.mVideoThumbnailView);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mNumberViewsView;
        public final ImageView mVideoThumbnailView;
        public Video mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.txtVideoName);
            mNumberViewsView = view.findViewById(R.id.txtVideoViews);
            mVideoThumbnailView = view.findViewById(R.id.videoImage);
        }
    }
}
