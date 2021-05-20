package com.kierasis.attendancemonitoring.student;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kierasis.attendancemonitoring.R;

import java.util.ArrayList;
import java.util.HashMap;

public class student_adapter_class_attendance_rv extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

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
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    public interface OnItemClickListener {
        void onItemClick(HashMap<String, String> item);
        void onCheckClick(HashMap<String, String> item);
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
    public student_adapter_class_attendance_rv(Context context, ArrayList<HashMap<String, String>> myDataset, RecyclerView recyclerView) {

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
            view = LayoutInflater.from(mactivity).inflate(R.layout.list_01_layout, parent, false);

            ViewHolderRow vHolder = new ViewHolderRow(view);



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

            userViewHolder.box.setBackgroundColor(Color.parseColor(map.get("KEY_COLOR")));
            userViewHolder.month.setText(map.get("KEY_MONTH"));
            userViewHolder.day.setText(map.get("KEY_DAY"));
            userViewHolder.title.setText(map.get("KEY_TITLE"));
            userViewHolder.subtitle.setText("Start: " + map.get("KEY_START"));
            userViewHolder.subtitle2.setText("End: " + map.get("KEY_END"));

            if(map.get("KEY_STATUS").equals("1")){
                userViewHolder.btn_check.setVisibility(View.VISIBLE);
                userViewHolder.btn_warning.setVisibility(View.GONE);
                userViewHolder.btn_ok.setVisibility(View.GONE);
            }else if(map.get("KEY_STATUS").equals("2")){
                userViewHolder.btn_check.setVisibility(View.GONE);
                userViewHolder.btn_warning.setVisibility(View.VISIBLE);
                userViewHolder.btn_ok.setVisibility(View.GONE);
            }else if (map.get("KEY_STATUS").equals("3")){
                userViewHolder.btn_check.setVisibility(View.GONE);
                userViewHolder.btn_warning.setVisibility(View.GONE);
                userViewHolder.btn_ok.setVisibility(View.VISIBLE);
            }

            //Log.d("tag",  map.get("KEY_ATTENDANCE_ID")+": "+ map.get("KEY_STATUS"));

            //userViewHolder.email.setText(contact.getPhone());

            // binding item click listener
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
        public TextView title, subtitle, subtitle2, month, day;
        LinearLayout box;
        public MaterialButton btn_more, btn_check;
        public ImageView btn_warning, btn_ok;

        public ViewHolderRow(View v) {
            super(v);
            box = (LinearLayout)v.findViewById(R.id.list_01_img);
            month = (TextView)v.findViewById(R.id.list_01_month);
            day = (TextView)v.findViewById(R.id.list_01_day);
            title = (TextView)v.findViewById(R.id.list_01_title);
            subtitle = (TextView)v.findViewById(R.id.list_01_subtitle);
            subtitle2 = (TextView)v.findViewById(R.id.list_01_subtitle2);
            btn_check = v.findViewById(R.id.list_01_check);
            btn_ok = v.findViewById(R.id.list_01_ok);
            btn_warning = v.findViewById(R.id.list_01_warning);
        }

        public void bind(final HashMap<String,String> item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
            btn_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCheckClick(item);
                }
            });
        }
    }

}