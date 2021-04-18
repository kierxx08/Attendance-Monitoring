package com.kierasis.attendancemonitoring.teacher;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kierasis.attendancemonitoring.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class teacher_adapter_class_pending_rv extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<HashMap<String,String>> mDataset;
    private Context mcontext;
    private Activity mactivity;
    private OnItemClickListener listener;

    // for load more
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public interface OnItemClickListener {
        void onItemClick(HashMap<String, String> item);
        void onAcceptClick(HashMap<String, String> item);
        void onDeclineClick(HashMap<String, String> item);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void add(int position, HashMap<String,String> item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove( HashMap<String,String> item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public teacher_adapter_class_pending_rv(Context context, ArrayList<HashMap<String, String>> myDataset, RecyclerView recyclerView) {

        mcontext = context;
        mactivity = (Activity)context;
        mDataset = myDataset;

        // load more
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(mactivity).inflate(R.layout.teacher_list_layout_pending_student, parent, false);


            ViewHolderRow vHolder = new ViewHolderRow(view);


/*
            vHolder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String,String> map = mDataset.get(vHolder.getAdapterPosition());
                    String account_id = user_info.getString("account_id","");
                    Log.d("tag", "My account_id: "+ account_id);
                    Log.d("tag", "Map: "+ map);

                    Toast.makeText(mactivity,"Accept: Test Click "+ map.get("account_id"),Toast.LENGTH_SHORT).show();

                    decision("accepted",map.get("CScon_id"),user_info.getString("account_id",""),device_info.getString("device_id",""));

                }
            });
            vHolder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String,String> map = mDataset.get(vHolder.getAdapterPosition());
                    Log.d("tag", "Map: "+ map);
                    Toast.makeText(mactivity,"Decline: Test Click "+ map.get("account_id"),Toast.LENGTH_SHORT).show();

                }
            });

 */

            return vHolder;

        } else if (viewType == VIEW_TYPE_LOADING) {
             view = LayoutInflater.from(mactivity).inflate(R.layout.list_00_load_layout, parent, false);
            return new ViewHolderLoading(view);
        }


        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (holder instanceof ViewHolderRow) {
            HashMap<String,String> map = mDataset.get(position);

            ViewHolderRow userViewHolder = (ViewHolderRow) holder;

            userViewHolder.title.setText(map.get("fullname"));
            userViewHolder.subtitle.setText(map.get("date"));
            Picasso.get().load(map.get("img_url")).into(userViewHolder.pic);

            //userViewHolder.email.setText(contact.getPhone());

            // binding item click listner
            userViewHolder.bind(mDataset.get(position), listener);
        } else if (holder instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setOnItemListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoaded() {
        isLoading = false;
    }

    private class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.l01ll_progressbar);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolderRow extends RecyclerView.ViewHolder {
        public TextView title, subtitle;
        public ImageView pic;
        public Button accept, decline;

        public ViewHolderRow(View v) {
            super(v);
            title = (TextView)v.findViewById(R.id.tllps_title);
            subtitle = (TextView)v.findViewById(R.id.tllps_subtitle);
            pic = v.findViewById(R.id.tllps_img);
            accept = v.findViewById(R.id.tllps_accept);
            decline = v.findViewById(R.id.tllps_decline);
        }

        public void bind(final HashMap<String,String> item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAcceptClick(item);
                }
            });
            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeclineClick(item);
                }
            });
        }
    }

}