package com.projectmpt.projectmpt;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Urban on 2/1/2018.
 */

public class RecyclerAdapterTransports extends RecyclerView.Adapter<RecyclerAdapterTransports.MyHolder>{

    List<Transports> list;
    Context context;
    private static int currentPosition = 0;
    private  ClickListener clicklistener = null;

    public RecyclerAdapterTransports(List<Transports> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list, parent, false);

        View view = LayoutInflater.from(context).inflate(R.layout.card_transport,parent,false);
        MyHolder myHolder = new MyHolder(view);


        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        Transports mylist = list.get(position);
        holder.heading.setText(mylist.getHeading());
        holder.description.setText(mylist.getDescription());
        holder.distance.setText(String.format("%.1f",(mylist.getDistanceto()*0.00062137)) + " miles total");

        Long millis =  mylist.getTimeto() - System.currentTimeMillis();
        Long hours = TimeUnit.MILLISECONDS.toHours(millis);
        Long mins = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));

        holder.timeleft.setText(hours + ":" + mins);


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


    class MyHolder extends RecyclerView.ViewHolder {
        TextView heading,description,distance,timeleft;
        private ConstraintLayout main;
        //LinearLayout linearLayout;

        public MyHolder(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.heading_transport);
            description= (TextView) itemView.findViewById(R.id.description_transport);
            timeleft= (TextView) itemView.findViewById(R.id.description_timeleft);
            distance= (TextView) itemView.findViewById(R.id.distance_transport);
            main = (ConstraintLayout) itemView.findViewById(R.id.cardmaintransport);

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
