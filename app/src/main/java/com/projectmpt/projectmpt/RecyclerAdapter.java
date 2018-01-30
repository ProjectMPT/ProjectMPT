package com.projectmpt.projectmpt;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by csa on 3/7/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder>{

    List<Needs> list;
    Context context;
    private static int currentPosition = 0;

    public RecyclerAdapter(List<Needs> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list, parent, false);

        View view = LayoutInflater.from(context).inflate(R.layout.card,parent,false);
        MyHolder myHolder = new MyHolder(view);


        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        Needs mylist = list.get(position);
        holder.heading.setText(mylist.getHeading());
        holder.description.setText(mylist.getDescription());


//    if (currentPosition == position) {
//        //creating an animation
//        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
//
//        //toggling visibility
//        holder.linearLayout.setVisibility(View.VISIBLE);
//
//        //adding sliding effect
//        holder.linearLayout.startAnimation(slideDown);
//    }
//
//        holder.heading.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//            //getting the position of the item to expand it
//            currentPosition = position;
//
//            //reloding the list
//            notifyDataSetChanged();
//        }
//    });
}

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;

            }
            else{

                arr=list.size();
            }



        }catch (Exception e){



        }

        return arr;

    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView heading,description;
        //LinearLayout linearLayout;

        public MyHolder(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.heading);
            description= (TextView) itemView.findViewById(R.id.description);

          //  linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }
    }

}