package com.example.hsinhwang.android_merchant.ManagerPanel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.CommonTask;
import com.example.hsinhwang.android_merchant.Classes.Rating;

import com.example.hsinhwang.android_merchant.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.lang.reflect.Type;
import java.util.List;

public class RatingReviewActivity extends AppCompatActivity {
    final static String TAG = "RatingReviewActivity";
    private Activity activity;
    private SwipeMenuRecyclerView rvRatings;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommonTask ratingGetAllTask, ratingDeleteTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_comment);
        activity = RatingReviewActivity.this;
        findview();
        rvRatings.setLayoutManager(new LinearLayoutManager(activity));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showAllRatings();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        showAllRatings();
    }

    private void showAllRatings() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/RatingServlet";
            List<Rating> ratings = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            ratingGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = ratingGetAllTask.execute().get();
                Type listType = new TypeToken<List<Rating>>() {
                }.getType();
                ratings = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (ratings == null || ratings.isEmpty()) {
                Common.showToast(activity, R.string.msg_NoCommentsFound);
            } else {
                rvRatings.setAdapter(new RatingReviewRecyclerViewAdapter(activity, ratings));
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }

    private void findview() {
        rvRatings = findViewById(R.id.rvRatings);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (ratingGetAllTask != null) {
            ratingGetAllTask.cancel(true);
            ratingGetAllTask = null;
        }
    }


    private class RatingReviewRecyclerViewAdapter extends RecyclerView.Adapter<RatingReviewRecyclerViewAdapter.ReviewMyViewHolder> {
        private int opened = -1;
        private LayoutInflater layoutInflater;
        private List<Rating> ratings;
        private RelativeLayout rlReview;
        private LinearLayout llReview;


        public RatingReviewRecyclerViewAdapter(Context context, List<Rating> ratings) {
            this.layoutInflater = LayoutInflater.from(context);
            this.ratings = ratings;
        }

        @Override
        public ReviewMyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            View itemView = layoutInflater.inflate(R.layout.item_view_review, parent, false);
            llReview = (LinearLayout) itemView.findViewById(R.id.llReview);
            rlReview = (RelativeLayout)itemView.findViewById(R.id.rlReview);
            return new ReviewMyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(@NonNull final ReviewMyViewHolder reviewMyViewHolder, int position) {
            final Rating rating = ratings.get(position);

            reviewMyViewHolder.tvRatingName.setText(rating.getName());
            reviewMyViewHolder.tvIdRoomReservation.setText(String.valueOf(rating.getIdRoomReservation()));
            reviewMyViewHolder.rbCardStar.setRating(rating.getRatingStar());
            reviewMyViewHolder.tvRatingOpinion.setText(rating.getOpinion());
            reviewMyViewHolder.etRatingReview.setText(rating.getReview());


            reviewMyViewHolder.ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(activity)
                            .setTitle("SS Hotel")
                            .setMessage("刪除留言")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.e(TAG, "ibDelete onClick");
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL + "/RatingServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "delete");
                                        jsonObject.addProperty("IdRoomReservation", rating.getIdRoomReservation());
                                        int count = 0;
                                        try {
                                            ratingDeleteTask = new CommonTask(url, jsonObject.toString());
                                            String result = ratingDeleteTask.execute().get();
                                            count = Integer.valueOf(result);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(activity, R.string.msg_DeleteFail);
                                        } else {
                                            ratings.remove(rating);
                                            RatingReviewActivity.RatingReviewRecyclerViewAdapter.this.notifyDataSetChanged();
                                            Common.showToast(activity, R.string.msg_DeleteSuccess);
                                        }
                                    }
                                }
                            })
                            .show();

                }



            });

            reviewMyViewHolder.btReviewOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String review = reviewMyViewHolder.etRatingReview.getText().toString();
                    int IdRoomReservation = rating.getIdRoomReservation();
                    if (Common.networkConnected(activity)) {
                        String url = Common.URL + "/RatingServlet";
                        Rating comment = new Rating(IdRoomReservation, review);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "updateReview");
                        jsonObject.addProperty("rating", new Gson().toJson(comment));
                        int count = 0;
                        String result = null;
                        try {
                            result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.valueOf(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        if (result == null){
                            Common.showToast(activity, R.string.msg_UpdateFail);
                        }else {
                            Common.showToast(activity, R.string.msg_UpdateSuccess);
                        }
                    }else{
                        Common.showToast(activity, R.string.msg_NoNetwork);
                    }
                    finish();
                }
            });

            if (position == opened) {
                llReview.setVisibility(View.VISIBLE);
            } else {
                llReview.setVisibility(View.GONE);
            }

            rlReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (opened == reviewMyViewHolder.getAdapterPosition()){
                        opened = -1;
                        notifyItemChanged(reviewMyViewHolder.getAdapterPosition());
                    }else{
                        int oldOpened = opened;
                        opened =reviewMyViewHolder.getAdapterPosition();
                        notifyItemChanged(oldOpened);
                        notifyItemChanged(opened);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return ratings.size();
        }

        class ReviewMyViewHolder extends  RecyclerView.ViewHolder {
            TextView tvRatingName, tvIdRoomReservation, tvRatingOpinion;
            EditText etRatingReview;
            RatingBar rbCardStar;
            ImageButton ibDelete;
            Button btReviewOK;
            private LinearLayout llReview;

            public ReviewMyViewHolder(View itemView) {
                super(itemView);
                tvRatingName = (TextView) itemView.findViewById(R.id.tvRatingName);
                tvIdRoomReservation = (TextView) itemView.findViewById(R.id.tvIdRoomReservation);
                tvRatingOpinion = (TextView) itemView.findViewById(R.id.tvRatingOpinion);
                etRatingReview = (EditText) itemView.findViewById(R.id.etRatingReview);
                rbCardStar = (RatingBar) itemView.findViewById(R.id.rbCardStar);
                ibDelete = (ImageButton) itemView.findViewById(R.id.ibDelete);
                btReviewOK = (Button) itemView.findViewById(R.id.btReviewOK);


            }
        }
    }
}
