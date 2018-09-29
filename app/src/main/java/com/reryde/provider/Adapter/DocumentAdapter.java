package com.reryde.provider.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.reryde.provider.Activity.ShowInvoicePicture;
import com.reryde.provider.Bean.Document;
import com.reryde.provider.Helper.URLHelper;
import com.reryde.provider.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {

    private ArrayList<Document> listModels;
    private Context context;
    JSONArray jsonArraylist;
    private RadioButton lastChecked = null;
    BottomSheetBehavior behavior;
    String TAG = "ServiceListAdapter";
    private int pos;
    private ServiceClickListener serviceClickListener;
    Document serviceListModel;
    boolean[] selectedService;
    boolean select;

    public DocumentAdapter(ArrayList<Document> listModel, Context context) {
        this.listModels = listModel;
        this.context = context;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public interface ServiceClickListener {
        void onDocImgClick(Document document);
    }


    public List<Document> getServiceListModel() {
        return listModels;
    }

    public void setServiceClickListener(ServiceClickListener serviceClickListener) {
        this.serviceClickListener = serviceClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_list_item, parent, false);
        return new ViewHolder(v);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView docImg;
        TextView docName;

        ViewHolder(View itemView) {
            super(itemView);
            docImg = itemView.findViewById(R.id.doc_image);
            docName = itemView.findViewById(R.id.doc_name);

            docImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Document document = (Document) v.getTag();
            if (document.getImg() != null && !document.getImg().equalsIgnoreCase("null") && document.getImg().length() > 0) {
                showDialog(document);
            } else {
                serviceClickListener.onDocImgClick(document);
            }
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Document serviceListModel = listModels.get(position);

        String name = serviceListModel.getName();
        if (name != null && !name.equalsIgnoreCase("null") && name.length() > 0) {
            holder.docName.setText(name);
        }
        Log.e(TAG, "onBindViewHolder: " + serviceListModel.getImg());
        if (serviceListModel.getImg() != null && !serviceListModel.getImg().equalsIgnoreCase("null") && serviceListModel.getImg().length() > 0) {
            Log.e(TAG, "onBindViewHolder: " + URLHelper.base + "storage/" + serviceListModel.getImg());
            Picasso.with(context).load(URLHelper.base + "storage/" + serviceListModel.getImg()).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.doc_placeholder).error(R.drawable.doc_placeholder).into(holder.docImg);
        }

        holder.docImg.setTag(serviceListModel);
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    private void showDialog(final Document document) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.img_click_dialog, null);
        dialogBuilder.setView(dialogView);
        final TextView viewTxt = dialogView.findViewById(R.id.view_txt);
        final TextView updateTxt = dialogView.findViewById(R.id.update_txt);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        viewTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent=new Intent(context, ShowInvoicePicture.class);
                intent.putExtra("image",URLHelper.base + "storage/" + document.getImg());
                context.startActivity(intent);
            }
        });

        updateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceClickListener.onDocImgClick(document);
                alertDialog.dismiss();
            }
        });
    }
}
