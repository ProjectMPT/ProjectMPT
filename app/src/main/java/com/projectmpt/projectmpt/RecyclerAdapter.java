package com.projectmpt.projectmpt;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder>{

    List<Transports> list;
    Context context;
    private static int currentPosition = 0;
    private  ClickListener clicklistener = null;

    public FirebaseUser fUser;
    public TextView txtCommited;


    public RecyclerAdapter(List<Transports> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        View view = LayoutInflater.from(context).inflate(R.layout.card,parent,false);
        MyHolder myHolder = new MyHolder(view);

        txtCommited = view.findViewById(R.id.tvCommited);

        return myHolder;
    }



    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {


        Transports mylist = list.get(position);

        String strHeader = mylist.getType() + " " + mylist.getHeading();

        String strDistance = "Distance " + mylist.getDistanceto();

        holder.distance.setText(strDistance);

        if(mylist.getType().equals("Provide")) {
            holder.imgType.setImageResource(R.drawable.heart);
        }else if(mylist.getType().equals("Transport")) {

            if(mylist.getProvideowner().equals(fUser.getEmail())) {
                txtCommited.setVisibility(View.VISIBLE);
                strHeader = "Provide " + mylist.getHeading();
            }

            holder.imgType.setImageResource(R.drawable.ic_run);
        }else {
            holder.imgType.setImageResource(R.drawable.ic_delivery);

            if(mylist.getTransportowner().equals(fUser.getEmail())) {
                txtCommited.setVisibility(View.VISIBLE);
                strHeader = "Transport " + mylist.getHeading();
            }


        }


        holder.heading.setText(strHeader);
        holder.description.setText(mylist.getDescription());

        Long millis =  mylist.getTimeto() - System.currentTimeMillis();
        Long hours = TimeUnit.MILLISECONDS.toHours(millis);
        Long mins = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));

           if (mins<1){
               holder.timeleft.setText("Expired");
           }else {
               holder.timeleft.setText(hours + " hours " + mins + " minutes left");
           }

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
        private ImageView imgType;


        public MyHolder(View itemView) {
            super(itemView);
            heading =  itemView.findViewById(R.id.tvType);
            description=  itemView.findViewById(R.id.tvDetails);
            distance=  itemView.findViewById(R.id.distance);
            timeleft=  itemView.findViewById(R.id.timeleft);
            imgType =   itemView.findViewById(R.id.imgType);

            imgMap =  itemView.findViewById(R.id.imgMap);

            imgMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                                      if(clicklistener !=null){
                        clicklistener.itemClicked(v,getAdapterPosition());
                    }
                }
            });



            main =  itemView.findViewById(R.id.cardmain);

            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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


