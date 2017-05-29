package com.example.aleksandrasalak.aplikacjamobilna.Portfel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aleksandrasalak.aplikacjamobilna.R;

import java.util.List;

public class PortfelAdapter extends RecyclerView.Adapter<PortfelAdapter.ViewHolder> {


    private List<WpisPortfela> listaWpisowPortfela;
    private Context mContext;


    public PortfelAdapter(Context context, List<WpisPortfela> wpisyPortfela) {
        listaWpisowPortfela = wpisyPortfela;
        mContext = context;
    }

    private Context getContext() {return mContext;}

    public PortfelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View viewWpisu = inflater.inflate(R.layout.wpis_portfela, parent, false);
        ViewHolder viewHolder = new ViewHolder(viewWpisu);

        return viewHolder;
    }

    public void onBindViewHolder(PortfelAdapter.ViewHolder viewHolder, int position) {
        WpisPortfela wpisPortfela = listaWpisowPortfela.get(position);

        viewHolder.opisWpisuTextView.setText(wpisPortfela.pobierzOpis());
        viewHolder.wartoscWpisuTextView.setText(wpisPortfela.pobierztWartosc());
        viewHolder.dataWpisuTextView.setText(wpisPortfela.pobierztDate());
    }


    public int getItemCount() {
        return listaWpisowPortfela.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView opisWpisuTextView;
        public TextView wartoscWpisuTextView;
        public TextView dataWpisuTextView;

        public View kontener1;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            opisWpisuTextView = (TextView) itemView.findViewById(R.id.opis2);
            wartoscWpisuTextView = (TextView) itemView.findViewById(R.id.wartosc2);
            dataWpisuTextView = (TextView) itemView.findViewById(R.id.data2);

            kontener1 = itemView.findViewById(R.id.kontener1);
            kontener1.setOnClickListener(this);

        }
        public void onClick(View view){
            if(view.getId()==R.id.kontener1){

            }
        }
    }

}
