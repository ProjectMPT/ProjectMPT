package com.projectmpt.projectmpt;

import android.content.Context;
import android.location.LocationListener;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by csa on 3/7/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder>{

    List<Transports> list;
    Context context;
    private static int currentPosition = 0;
    private  ClickListener clicklistener = null;
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");


    public RecyclerAdapter(List<Transports> list, Context context) {
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

   // Long timeLeft = mylist.g - System.currentTimeMillis()/1000;
  //  java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("HH:mm");

                              //          transports.setTimeleft(df.format(timeLeft));


    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        Transports mylist = list.get(position);
        holder.heading.setText(mylist.getHeading());
        holder.description.setText(mylist.getDescription());
        holder.distance.setText(String.format("%.1f",(mylist.getDistanceto()*0.00062137)) + " miles");

        Integer hours = 0;
        Integer minutes = 0;
        Long timeLeft = mylist.getTimeto() - System.currentTimeMillis();

        hours = (int)(timeLeft/60000/60);
        minutes = (int)((timeLeft/60000)-hours*60);

        holder.timeleft.setText(hours + "h " + minutes + "m left");

       // Log.d("urb", "Time: " + mylist.getTimeto() + ", " + System.currentTimeMillis()+ ", " + timeLeft);


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
        TextView heading,description,distance,timeleft;
        private ConstraintLayout main;
        //LinearLayout linearLayout;

        public MyHolder(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.heading);
            description= (TextView) itemView.findViewById(R.id.description);
            distance= (TextView) itemView.findViewById(R.id.distance);
            timeleft= (TextView) itemView.findViewById(R.id.timeleft);
            main = (ConstraintLayout) itemView.findViewById(R.id.cardmain);

            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("urb", "Click 2:");
                    if(clicklistener !=null){
                        clicklistener.itemClicked(v,getAdapterPosition());
                    }
                }
            });
        }
    }
    public void setClickListener(ClickListener clickListener){
        this.clicklistener = clickListener;
    }

}


