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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        holder.heading.setText("Transport " + mylist.getHeading());
        holder.description.setText(mylist.getDescription());
        //holder.distance.setText(String.format("%.1f",(mylist.getDistanceto()*0.00062137)) + " miles");

        Long millis =  mylist.getTimeto() - System.currentTimeMillis();
        Long hours = TimeUnit.MILLISECONDS.toHours(millis);
        Long mins = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));

       // holder.timeleft.setText(hours + ":" + mins);

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
        private ImageButton imgMap;
        //LinearLayout linearLayout;

        public MyHolder(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.tvType);
            description= (TextView) itemView.findViewById(R.id.tvDetails);
          //  distance= (TextView) itemView.findViewById(R.id.distance);
          //  timeleft= (TextView) itemView.findViewById(R.id.timeleft);

            imgMap = (ImageButton) itemView.findViewById(R.id.imgMap);

            imgMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("urb", "Map:");
                    if(clicklistener !=null){
                        clicklistener.itemClicked(v,getAdapterPosition());
                    }
                }
            });



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


