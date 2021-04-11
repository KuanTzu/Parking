package com.example.parking;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private String[] btn;
    private String csvName;
    private Application application;

    public MyAdapter(String[] btn, Application application,String csvName){
        this.btn=btn;
        this.application = application;
        this.csvName = csvName;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.modrls,parent,false);
        myVH my_VH = new myVH(v);
        return my_VH;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((myVH)holder).bt.setText(btn[position]);
        ((myVH) holder).bt.setOnClickListener(new DynamicOnClickListener(holder, position));
    }

    @Override
    public int getItemCount() {
        return btn.length;
    }

    private float[] getCsvList(String csvName){
        CSVFile csv = new CSVFile(application);
        float[] csvList = csv.getInput(csvName);
        return csvList;
    }

    public class myVH extends RecyclerView.ViewHolder{
        TextView ans,weight,time;
        Button bt;
        public myVH(@NonNull View itemView) {
            super(itemView);
            ans = itemView.findViewById(R.id.ans);
            weight = itemView.findViewById(R.id.weight);
            time = itemView.findViewById(R.id.time);
            bt = itemView.findViewById(R.id.button2);
        }
    }

    //自訂BUTTON OnClickListener
    class  DynamicOnClickListener implements View.OnClickListener {
        Model model;
        RecyclerView.ViewHolder holder;
        int position;
        public DynamicOnClickListener(RecyclerView.ViewHolder holder,int position) {
            this.holder = holder;
            this.position = position;
        }
        public void onClick(View v) {
            switch (position){
                case 0:
                    model = new RunDlc(application, 0);
                    break;
                case 1:
                    model = new RunDlc(application, 1);
                    break;
                case 2:
                    model = new TFLite(application);
                    break;
                case 3:
                    model = new TFLiteGPU(application);
                    break;
                default:
                    model = new RunDlc(application, 0);
                    break;
            }

            float[] csvList = getCsvList(csvName);
            model.runModel(csvList);
            ((myVH)holder).ans.setText(model.getAns()+"");
            ((myVH)holder).weight.setText(model.getW()+"");
            ((myVH)holder).time.setText(model.getTime()+" s");
            //
            //toWeb = new ToWeb(runDlc.getAns());
            //toWeb.run();
        }
    }
}
